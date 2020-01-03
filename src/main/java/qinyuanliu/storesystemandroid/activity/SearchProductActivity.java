package qinyuanliu.storesystemandroid.activity;

import android.app.Activity;
import android.app.Dialog;
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
import qinyuanliu.storesystemandroid.adapter.ProductListAdapter;
import qinyuanliu.storesystemandroid.common.Session;
import qinyuanliu.storesystemandroid.http.Codes;
import qinyuanliu.storesystemandroid.http.SCExceptionCodeList;
import qinyuanliu.storesystemandroid.http.SCResponseListener;
import qinyuanliu.storesystemandroid.http.SCResult;
import qinyuanliu.storesystemandroid.http.SCSDK;
import qinyuanliu.storesystemandroid.listener.ClickInStoreListener;
import qinyuanliu.storesystemandroid.listener.ClickLookStoreListener;
import qinyuanliu.storesystemandroid.listener.ClickOutStoreListener;
import qinyuanliu.storesystemandroid.listener.ClickPositionListener;
import qinyuanliu.storesystemandroid.listener.ClickProductListener;
import qinyuanliu.storesystemandroid.listener.RefreshTokenListener;
import qinyuanliu.storesystemandroid.model.ProductModel;
import qinyuanliu.storesystemandroid.util.DialogThridUtils;

/**
 * Created by lillix on 5/28/18.
 */
public class SearchProductActivity extends BaseActivity{
    private ListView listview_product;
    private EditText edt_keyword;
private TextView tv_search;
    private Button btn_back;

    private ArrayList<ProductModel> datalist = new ArrayList<>();
    private ProductListAdapter listAdapter;
    private String keyword="";
    private Dialog mDialog;

@Override
protected void onResume(){
    super.onResume();
    if(!keyword.equals("")){
        RefreshData();
    }
}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        initView();

    }
    private void initView() {
        btn_back = (Button) findViewById(R.id.btn_back);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm =  (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                if(imm != null) {
                    imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
                }
                finish();
            }
        });
        listview_product = (ListView) findViewById(R.id.listview_product);
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


        tv_search = (TextView)findViewById(R.id.tv_search);
        tv_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!keyword.equals("")){
                    RefreshData();
                }
            }
        });
    }

    private void RefreshData() {
        Session.CheckRefreshToken(new RefreshTokenListener() {
            @Override
            public void RefreshTokenResult(final int resultcode) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (resultcode == Codes.Code_Success) {
                            mDialog = DialogThridUtils.showWaitDialog(SearchProductActivity.this, "搜索中...", true);
                            SCSDK.getInstance().SearchModel(Session.getInstance().getShopCode(),keyword, Session.getInstance().getToken(), new SCResponseListener() {
                                @Override
                                public void onResult(Object result) {
                                    final SCResult.ModelResult res = (SCResult.ModelResult) result;
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            DialogThridUtils.closeDialog(mDialog);
                                            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
                                            if (res.models != null && res.models.size()>0) {
                                                datalist.clear();
                                                if(res.models.get(0).sn != null && !res.models.get(0).sn.equals("")){
                                                    keyword = "";
                                                    edt_keyword.setText("");
                                                    RefreshListview();
                                                    Intent productdetail = new Intent(SearchProductActivity.this, ProductDetailActivity.class);
                                                    productdetail.putExtra("productid",res.models.get(0).sn);
                                                    startActivity(productdetail);
                                                    return;
                                                }

                                                for (int i = 0; i < res.models.size(); i++) {
                                                        SCResult.Model searchresult = res.models.get(i);
                                                        ProductModel product = new ProductModel(searchresult.modelcode, searchresult.modelname, searchresult.postion,  searchresult.spec,  searchresult.store,searchresult.typename,searchresult.sn);
                                                        datalist.add(product);

                                                }

                                                RefreshListview();
                                            }
                                            else{
                                                datalist.clear();
                                                RefreshListview();
                                            }
                                        }
                                    });
                                }

                                @Override
                                public void onError(int code,final String errormsg) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(SearchProductActivity.this, errormsg, Toast.LENGTH_SHORT).show();
                                            DialogThridUtils.closeDialog(mDialog);
                                        }
                                    });
                                }
                            });
                        } else {
                            Toast.makeText(SearchProductActivity.this, SCExceptionCodeList.getExceptionMsg(resultcode), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }

    private void RefreshListview() {
        if (listAdapter == null) {
            listAdapter = new ProductListAdapter(SearchProductActivity.this, datalist, new ClickProductListener() {
                @Override
                public void onClickProduct(String productid,String modelname,String sn) {
                    Intent productdetail = new Intent(SearchProductActivity.this, ModelDetailActivity.class);
                    productdetail.putExtra("productid", productid);
                    startActivity(productdetail);
                }
            }, new ClickInStoreListener() {
                @Override
                public void onClickInStore(String productid) {
                    Intent inintent = new Intent(SearchProductActivity.this, InStoreActivity.class);
                    inintent.putExtra("productid",productid);
                    startActivity(inintent);
                }
            }, new ClickOutStoreListener() {
                @Override
                public void onClickOutStore(String productid) {
                    Intent outintent = new Intent(SearchProductActivity.this, OutStoreActivity.class);
                    outintent.putExtra("productid",productid);
                    startActivity(outintent);
                }
            }, new ClickLookStoreListener() {
                @Override
                public void onClickLookStore(String productid) {
                    Intent inintent = new Intent(SearchProductActivity.this, LookStoreActivity.class);
                    inintent.putExtra("productid",productid);
                    startActivity(inintent);
                }
            },new ClickPositionListener() {
                @Override
                public void ClickPosition(String productid) {
                    Intent inintent = new Intent(SearchProductActivity.this, SamePositionActivity.class);
                    inintent.putExtra("modelcode", productid);
                    startActivity(inintent);
                }
            });
            listview_product.setAdapter(listAdapter);
        } else {
            listAdapter.setDatalist(datalist);
            listAdapter.notifyDataSetChanged();
        }
    }
}
