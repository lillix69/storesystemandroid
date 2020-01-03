package qinyuanliu.storesystemandroid.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
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
 * Created by qinyuanliu on 2019/4/21.
 */

public class PurchaseSearchActivity extends BaseActivity {
    private Button btn_back;
    private ListView listview_search;
    private EditText edt_keyword;
    private TextView tv_search;
    private String keyword = "";
    private ArrayList<ProductModel> datalist = new ArrayList<>();
    private FastmodeProductListAdapter listAdapter_fastmode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_purchasesearch);

        initView();
        RefreshData();
    }

    private void initView() {
        btn_back = (Button) findViewById(R.id.btn_back);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
                }
                finish();
            }
        });
        edt_keyword = (EditText) findViewById(R.id.edt_keyword);
        edt_keyword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                keyword = s.toString();
            }
        });
        edt_keyword.requestFocus();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        tv_search = (TextView) findViewById(R.id.tv_search);
        tv_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!keyword.equals("")) {
                    RefreshData();
                }
            }
        });
        listview_search = (ListView) findViewById(R.id.listview_search);
    }

    private void RefreshData() {
        Session.CheckRefreshToken(new RefreshTokenListener() {
            @Override
            public void RefreshTokenResult(final int resultcode) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (resultcode == Codes.Code_Success) {
                            SCSDK.getInstance().SearchModel(Session.getInstance().getShopCode(), keyword, Session.getInstance().getToken(), new SCResponseListener() {
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
                                                    ProductModel product = new ProductModel(model.modelcode, model.modelname, model.postion, model.spec, model.store, model.typename, model.sn);

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
                                            Toast.makeText(PurchaseSearchActivity.this, errormsg, Toast.LENGTH_SHORT).show();
                                        }
                                    });

                                }
                            });
                        } else {
                            Toast.makeText(PurchaseSearchActivity.this, SCExceptionCodeList.getExceptionMsg(resultcode), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }

    private void RefreshListview() {
        if (listAdapter_fastmode == null) {
            listAdapter_fastmode = new FastmodeProductListAdapter(PurchaseSearchActivity.this, datalist, new ClickProductListener() {
                @Override
                public void onClickProduct(String productid ,String modelname,String sn) {

                    Intent data = new Intent();
                    data.putExtra("modelcode", productid);
                    data.putExtra("modelname",modelname);
                    setResult(RESULT_OK, data);
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm != null) {
                        imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
                    }
                    finish();
                }
            });
            listview_search.setAdapter(listAdapter_fastmode);
        } else {

            listAdapter_fastmode.setDatalist(datalist);
            listAdapter_fastmode.notifyDataSetChanged();
        }
    }

}
