package qinyuanliu.storesystemandroid.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import qinyuanliu.storesystemandroid.R;
import qinyuanliu.storesystemandroid.adapter.ModelDetailApater;
import qinyuanliu.storesystemandroid.common.Session;
import qinyuanliu.storesystemandroid.http.Codes;
import qinyuanliu.storesystemandroid.http.SCExceptionCodeList;
import qinyuanliu.storesystemandroid.http.SCResponseListener;
import qinyuanliu.storesystemandroid.http.SCResult;
import qinyuanliu.storesystemandroid.http.SCSDK;
import qinyuanliu.storesystemandroid.listener.RefreshTokenListener;

/**
 * Created by qinyuanliu on 2019/9/9.
 */

public class ShowPurDetailActivity extends BaseActivity {
    private Button btn_back;
private TextView tv_ordercode;
    private TextView tv_orderdate;
    private ListView listview_model;

    private String ordercode;
private ArrayList<SCResult.PurchaseModel> datalist = new ArrayList<>();
private ModelDetailApater adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ordercode = getIntent().getStringExtra("ordercode");
        setContentView(R.layout.activity_showpurdetail);

        initView();
        initData();
    }

    private void initView() {
        btn_back = (Button) findViewById(R.id.btn_back);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        tv_ordercode = (TextView)findViewById(R.id.tv_ordercode);
        tv_ordercode.setText(ordercode);
        tv_orderdate = (TextView)findViewById(R.id.tv_orderdate);
        listview_model = (ListView)findViewById(R.id.listview_model);
    }

    private void initData(){
        Session.CheckRefreshToken(new RefreshTokenListener() {
            @Override
            public void RefreshTokenResult(final int resultcode) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (resultcode == Codes.Code_Success) {
                            SCSDK.getInstance().GetPurchaseModel(Session.getInstance().getShopCode(), Session.getInstance().getToken(),ordercode, new SCResponseListener() {
                                @Override
                                public void onResult(Object result) {
                                    final SCResult.PurchaseModelResult res = (SCResult.PurchaseModelResult) result;
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            tv_orderdate.setText(res.orderdate);
                                            if(res.models != null && res.models.size()>0){
                                                for (SCResult.PurchaseModel model: res.models) {
                                                     datalist.add(model);
                                                }
                                            }
                                            refreshListview();
                                        }
                                    });
                                }

                                @Override
                                public void onError(int code, final String errormsg) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            refreshListview();
                                            Toast.makeText(ShowPurDetailActivity.this, errormsg, Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            });
                        } else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    refreshListview();
                                    Toast.makeText(ShowPurDetailActivity.this, SCExceptionCodeList.getExceptionMsg(resultcode), Toast.LENGTH_SHORT).show();

                                }
                            });
                        }
                    }
                });
            }
        });
    }

    private void refreshListview() {
        if (adapter == null) {
            adapter = new ModelDetailApater(ShowPurDetailActivity.this, datalist);
            listview_model.setAdapter(adapter);
        } else {
            adapter.setModellist(datalist);
            adapter.notifyDataSetChanged();
        }
    }
}
