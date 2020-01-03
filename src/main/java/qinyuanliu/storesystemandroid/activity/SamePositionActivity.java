package qinyuanliu.storesystemandroid.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

import qinyuanliu.storesystemandroid.R;
import qinyuanliu.storesystemandroid.adapter.FastmodeProductListAdapter;
import qinyuanliu.storesystemandroid.common.Session;
import qinyuanliu.storesystemandroid.http.Codes;
import qinyuanliu.storesystemandroid.http.SCExceptionCodeList;
import qinyuanliu.storesystemandroid.http.SCResponseListener;
import qinyuanliu.storesystemandroid.http.SCResult;
import qinyuanliu.storesystemandroid.http.SCSDK;
import qinyuanliu.storesystemandroid.listener.ClickProductListener;
import qinyuanliu.storesystemandroid.listener.RefreshTokenListener;
import qinyuanliu.storesystemandroid.model.ProductModel;

/**
 * Created by qinyuanliu on 2019/4/30.
 */

public class SamePositionActivity extends BaseActivity {
    private ListView listview_product;
    private Button btn_back;
String modelcode ;
    private ArrayList<ProductModel> datalist = new ArrayList<>();
    private FastmodeProductListAdapter listAdapter_fastmode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sameposition);

       modelcode = getIntent().getStringExtra("modelcode");

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
        listview_product = (ListView) findViewById(R.id.listview_product);
    }

    private void initData(){
        Session.CheckRefreshToken(new RefreshTokenListener() {
            @Override
            public void RefreshTokenResult(final int resultcode) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (resultcode == Codes.Code_Success) {
                            SCSDK.getInstance().GetModelByPosition(modelcode,Session.getInstance().getShopCode(), Session.getInstance().getToken(), new SCResponseListener() {
                                @Override
                                public void onResult(Object result) {
                                    final SCResult.ModelResult res = (SCResult.ModelResult) result;
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            if (res.models != null) {
                                                datalist.clear();
                                                for (int i = 0; i < res.models.size(); i++) {
                                                    SCResult.Model model = res.models.get(i);
                                                    ProductModel product = new ProductModel(model.modelcode, model.modelname, model.postion, model.spec, model.store, model.typename, "");
                                                    datalist.add(product);
                                                }

                                                RefreshListview();
                                            }
                                        }
                                    });
                                }

                                @Override
                                public void onError(int code, final String errormsg) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(SamePositionActivity.this, errormsg, Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            });
                        } else {
                            Toast.makeText(SamePositionActivity.this, SCExceptionCodeList.getExceptionMsg(resultcode), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }
    private void RefreshListview() {
        if (listAdapter_fastmode == null) {
            listAdapter_fastmode = new FastmodeProductListAdapter(SamePositionActivity.this, datalist, new ClickProductListener() {
                @Override
                public void onClickProduct(String productid,String modelname, String sn) {
                    Intent productdetail = new Intent(SamePositionActivity.this, ModelDetailActivity.class);
                    productdetail.putExtra("productid", productid);
                    startActivity(productdetail);
                }
            });
            listview_product.setAdapter(listAdapter_fastmode);
        } else {
            listview_product.setAdapter(listAdapter_fastmode);
            listAdapter_fastmode.setDatalist(datalist);
            listAdapter_fastmode.notifyDataSetChanged();
        }
    }
}
