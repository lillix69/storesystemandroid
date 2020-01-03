package qinyuanliu.storesystemandroid.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import qinyuanliu.storesystemandroid.R;
import qinyuanliu.storesystemandroid.adapter.PurchaseDetailAdapter;
import qinyuanliu.storesystemandroid.common.Session;
import qinyuanliu.storesystemandroid.http.Codes;
import qinyuanliu.storesystemandroid.http.SCExceptionCodeList;
import qinyuanliu.storesystemandroid.http.SCResponseListener;
import qinyuanliu.storesystemandroid.http.SCResult;
import qinyuanliu.storesystemandroid.http.SCSDK;
import qinyuanliu.storesystemandroid.listener.RefreshTokenListener;

/**
 * Created by qinyuanliu on 2019/4/14.
 */

public class PurchaseDetailActivity extends BaseActivity {
    private Button btn_back;
    private ListView listview_models;
    private TextView tv_username;
    private TextView tv_date;
    private TextView tv_purchasecode;

    private String purchasecode;
    private ArrayList<SCResult.PurchaseStoreDetailInfo> datalist;
    private PurchaseDetailAdapter purchaseDetailAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        purchasecode = getIntent().getStringExtra("purchasecode");

        setContentView(R.layout.activity_purchasedetail);
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
        tv_purchasecode = (TextView) findViewById(R.id.tv_purchasecode);
        tv_date = (TextView) findViewById(R.id.tv_date);
        tv_username = (TextView) findViewById(R.id.tv_username);
        listview_models = (ListView) findViewById(R.id.listview_models);

    }

    private void initData() {
        datalist = new ArrayList<>();
        Session.CheckRefreshToken(new RefreshTokenListener() {
            @Override
            public void RefreshTokenResult(final int resultcode) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (resultcode == Codes.Code_Success) {
                            SCSDK.getInstance().GetPurchaseStoreDetail(purchasecode, Session.getInstance().getShopCode(), Session.getInstance().getToken(), new SCResponseListener() {
                                @Override
                                public void onResult(Object result) {
                                    final SCResult.PurchaseStoreDetailResult res = (SCResult.PurchaseStoreDetailResult) result;
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            tv_purchasecode.setText(res.instorecode);
                                            tv_username.setText(res.username);
                                            tv_date.setText(res.instoredate);
                                            if (res.purchasemodels != null && res.purchasemodels.size() > 0) {
                                                datalist = res.purchasemodels;
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
                                            Toast.makeText(PurchaseDetailActivity.this, errormsg, Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            });
                        } else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(PurchaseDetailActivity.this, SCExceptionCodeList.getExceptionMsg(resultcode), Toast.LENGTH_SHORT).show();

                                }
                            });
                        }
                    }
                });
            }
        });
    }

    private void refreshListview() {
        if (purchaseDetailAdapter == null) {
            purchaseDetailAdapter = new PurchaseDetailAdapter(PurchaseDetailActivity.this, datalist);
            listview_models.setAdapter(purchaseDetailAdapter);
        } else {
            purchaseDetailAdapter.setModellist(datalist);
            purchaseDetailAdapter.notifyDataSetChanged();
        }
    }
}
