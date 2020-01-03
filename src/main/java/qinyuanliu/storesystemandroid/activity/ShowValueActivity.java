package qinyuanliu.storesystemandroid.activity;

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
import qinyuanliu.storesystemandroid.adapter.ValueAdapter;
import qinyuanliu.storesystemandroid.common.Session;
import qinyuanliu.storesystemandroid.http.Codes;
import qinyuanliu.storesystemandroid.http.SCExceptionCodeList;
import qinyuanliu.storesystemandroid.http.SCResponseListener;
import qinyuanliu.storesystemandroid.http.SCResult;
import qinyuanliu.storesystemandroid.http.SCSDK;
import qinyuanliu.storesystemandroid.listener.RefreshTokenListener;
import qinyuanliu.storesystemandroid.model.ProductModel;
import qinyuanliu.storesystemandroid.util.DialogThridUtils;

/**
 * Created by qinyuanliu on 2019/5/25.
 */

public class ShowValueActivity extends BaseActivity {
    private Button btn_back;
private EditText edt_keyword;
    private TextView tv_search;
    private TextView tv_summery;

    private ListView listview_value;
    private String keyword;
private ValueAdapter valueAdapter;
    private ArrayList<SCResult.ModelValue> datalist = new ArrayList<>();
    private Dialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_showvalue);
        initView();
        refreshData();
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
        listview_value = (ListView)findViewById(R.id.listview_value);
        edt_keyword = (EditText)findViewById(R.id.edt_keyword);
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
        tv_search = (TextView)findViewById(R.id.tv_search);
        tv_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refreshData();
            }
        });
        tv_summery = (TextView)findViewById(R.id.tv_summery);

    }

    private void refreshData(){
        datalist.clear();
        Session.CheckRefreshToken(new RefreshTokenListener() {
            @Override
            public void RefreshTokenResult(final int resultcode) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (resultcode == Codes.Code_Success) {
                            mDialog = DialogThridUtils.showWaitDialog(ShowValueActivity.this, "搜索中...", true);
                            SCSDK.getInstance().SearchModelValue(Session.getInstance().getShopCode(),Session.getInstance().getToken(),keyword,  new SCResponseListener() {
                                @Override
                                public void onResult(Object result) {
                                    final SCResult.ModelValueResult res = (SCResult.ModelValueResult) result;
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            DialogThridUtils.closeDialog(mDialog);
                                            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

                                           tv_summery.setText(res.summary);
                                            if (res.models != null && res.models.size()>0) {
                                               datalist = res.models;
                                            }

                                            refreshListview();
                                        }
                                    });
                                }

                                @Override
                                public void onError(int code,final String errormsg) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(ShowValueActivity.this, errormsg, Toast.LENGTH_SHORT).show();
                                            DialogThridUtils.closeDialog(mDialog);
                                            refreshListview();
                                        }
                                    });
                                }
                            });
                        } else {
                            Toast.makeText(ShowValueActivity.this, SCExceptionCodeList.getExceptionMsg(resultcode), Toast.LENGTH_SHORT).show();
                            refreshListview();
                        }
                    }
                });
            }
        });
    }

    private void refreshListview(){
if(valueAdapter == null){
    valueAdapter = new ValueAdapter(ShowValueActivity.this, datalist);
    listview_value.setAdapter(valueAdapter);
}
else{
   valueAdapter.setModellist(datalist);
    valueAdapter.notifyDataSetChanged();
}
    }
}
