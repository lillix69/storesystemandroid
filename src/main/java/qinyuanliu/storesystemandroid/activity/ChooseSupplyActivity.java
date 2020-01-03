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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.ArrayList;

import qinyuanliu.storesystemandroid.R;
import qinyuanliu.storesystemandroid.adapter.SupplyAdapter;
import qinyuanliu.storesystemandroid.common.Session;
import qinyuanliu.storesystemandroid.http.Codes;
import qinyuanliu.storesystemandroid.http.SCExceptionCodeList;
import qinyuanliu.storesystemandroid.http.SCResponseListener;
import qinyuanliu.storesystemandroid.http.SCResult;
import qinyuanliu.storesystemandroid.http.SCSDK;
import qinyuanliu.storesystemandroid.listener.RefreshTokenListener;
import qinyuanliu.storesystemandroid.model.NoticeModel;
import qinyuanliu.storesystemandroid.model.SupplyModel;

/**
 * Created by qinyuanliu on 2019/4/21.
 */

public class ChooseSupplyActivity extends BaseActivity {
    private Button btn_back;
   // private TextView tv_turn;
    private TextView tv_confirm;
   // private TextView tv_confirmedit;
    private EditText edt_supply;
    private TextView tv_search;
   // private RelativeLayout inputview;
    private ListView listview_supply;
    private SupplyAdapter supplyAdapter;
    private ArrayList<SupplyModel> supplylist = new ArrayList<>();

   // private int mode = 0;//0选择模式， 1输入模式
   // private String inputsupplyname = "";
    private String keyword="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choosesupply);

        initView();
        initListdata();
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
//        inputview = (RelativeLayout)findViewById(R.id.inputview);
//        tv_turn = (TextView) findViewById(R.id.tv_turn);
//        tv_turn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (mode == 0) {
//                    mode = 1;
//                    inputview.setVisibility(View.VISIBLE);
//                    listview_supply.setVisibility(View.INVISIBLE);
//
//                    showKeyboard();
//                } else {
//                    mode = 0;
//                    InputMethodManager imm =  (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
//                    if(imm != null) {
//                        imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
//                    }
//                    inputview.setVisibility(View.INVISIBLE);
//                    listview_supply.setVisibility(View.VISIBLE);
//                }
//            }
//        });
//        tv_confirmedit = (TextView)findViewById(R.id.tv_confirmedit);
//        tv_confirmedit.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                    if (inputsupplyname == null || inputsupplyname.equals("")) {
//                        Toast.makeText(ChooseSupplyActivity.this, "请输入供应商名称！", Toast.LENGTH_SHORT).show();
//                    } else {
//                        InputMethodManager imm =  (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
//                        if(imm != null) {
//                            imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
//                        }
//                        //返回前一页
//                        Intent data = new Intent();
//                        data.putExtra("supplyname", inputsupplyname);
//                        setResult(RESULT_OK, data);
//                        finish();
//                    }
//
//            }
//        });

        tv_search = (TextView) findViewById(R.id.tv_search);
        tv_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm =  (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                        if(imm != null) {
                            imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
                        }
                        ClickSearch();
            }
        });
        tv_confirm = (TextView) findViewById(R.id.tv_confirm);
        tv_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    boolean ischoose = false;
                    for (SupplyModel supply : supplylist) {
                        if (supply.isSelected) {
                            //返回前一页
                            ischoose = true;
                            Intent data = new Intent();
                            data.putExtra("supplyname", supply.supplyName);
                            data.putExtra("supplycode", supply.supplyCode);
                            setResult(RESULT_OK, data);
                            finish();
                            break;
                        }
                    }
                    if(!ischoose) {
                        Toast.makeText(ChooseSupplyActivity.this, "请选择一个供应商！", Toast.LENGTH_SHORT).show();
                    }

            }
        });
        edt_supply = (EditText) findViewById(R.id.edt_supply);
        edt_supply.addTextChangedListener(new TextWatcher() {
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
        listview_supply = (ListView) findViewById(R.id.chooseview);

    }

    private void showKeyboard(){
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.showSoftInput(edt_supply, 0);
    }

    private void refreshListview() {
        if (supplyAdapter == null) {
            supplyAdapter = new SupplyAdapter(ChooseSupplyActivity.this, supplylist);
            listview_supply.setAdapter(supplyAdapter);
        } else {
            supplyAdapter.setModellist(supplylist);
            supplyAdapter.notifyDataSetChanged();
        }
    }

    private void initListdata() {
supplylist = new ArrayList<>();
        Session.CheckRefreshToken(new RefreshTokenListener() {
            @Override
            public void RefreshTokenResult(final int resultcode) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (resultcode == Codes.Code_Success) {
                            SCSDK.getInstance().GetSuppliers(Session.getInstance().getShopCode(), Session.getInstance().getToken(),keyword, new SCResponseListener() {
                                @Override
                                public void onResult(Object result) {
                                    final SCResult.SupplierResult res = (SCResult.SupplierResult) result;
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            if (res.suppliers != null && res.suppliers.size() > 0) {
                                                for (SCResult.SupplierInfo supply :
                                                        res.suppliers) {
                                                    SupplyModel s = new SupplyModel(supply.suppliername, supply.supplierCode);
                                                    supplylist.add(s);
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
                                            Toast.makeText(ChooseSupplyActivity.this, errormsg, Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            });
                        } else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    refreshListview();
                                    Toast.makeText(ChooseSupplyActivity.this, SCExceptionCodeList.getExceptionMsg(resultcode), Toast.LENGTH_SHORT).show();

                                }
                            });
                        }
                    }
                });
            }
        });
    }

    private void ClickSearch(){
        initListdata();
    }
}
