package qinyuanliu.storesystemandroid.activity;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import qinyuanliu.storesystemandroid.R;

import qinyuanliu.storesystemandroid.adapter.AddSaleModelAdapter;
import qinyuanliu.storesystemandroid.common.Session;
import qinyuanliu.storesystemandroid.http.Codes;
import qinyuanliu.storesystemandroid.http.SCExceptionCodeList;
import qinyuanliu.storesystemandroid.http.SCResponseListener;
import qinyuanliu.storesystemandroid.http.SCResult;
import qinyuanliu.storesystemandroid.http.SCSDK;
import qinyuanliu.storesystemandroid.listener.EditCostListener;
import qinyuanliu.storesystemandroid.listener.EditCountListener;
import qinyuanliu.storesystemandroid.listener.RefreshTokenListener;
import qinyuanliu.storesystemandroid.listener.RemoveAddSaleListener;
import qinyuanliu.storesystemandroid.model.AddSaleModel;

import qinyuanliu.storesystemandroid.util.TextUtil;

/**
 * Created by qinyuanliu on 2019/4/21.
 */

public class AddSaleActivity extends BaseActivity {
    private Button btn_back;
    private TextView tv_add;
    private ListView listview_models;
    private TextView tv_confirm;
private EditText tv_clientname;
    private EditText tv_clienttel;
    private EditText tv_address;
    private EditText edt_remark;

    final int modelrequest = 1001;

    private AddSaleModelAdapter addSaleModelAdapter;
    private ArrayList<AddSaleModel> addmodelList = new ArrayList<>();
    String currenteditcode = "";
    private String tel = "";
    private String address = "";
    private String client = "";
    private String remark = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addsale);

        initView();
        RefreshListview();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) { //resultCode为回传的标记，我在B中回传的是RESULT_OK
            case modelrequest:
                if (resultCode == RESULT_OK) {
                    Bundle b = data.getExtras();
                    final String name = b.getString("modelname");
                    final String code = b.getString("modelcode");
                    boolean isadd = false;
                    for (AddSaleModel oldmodel : addmodelList) {
                        if (oldmodel.getModelCode().equals(code)) {
                            isadd = true;
                            Toast.makeText(AddSaleActivity.this, "已添加过此型号！", Toast.LENGTH_SHORT).show();
                            break;
                        }
                    }
                    if (!isadd) {
                        Session.CheckRefreshToken(new RefreshTokenListener() {
                            @Override
                            public void RefreshTokenResult(final int resultcode) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (resultcode == Codes.Code_Success) {
                                            SCSDK.getInstance().GetProductDetail(Session.getInstance().getShopCode(), code, Session.getInstance().getToken(), new SCResponseListener() {
                                                @Override
                                                public void onResult(Object result) {
                                                    final SCResult.ModelDetailResult res = (SCResult.ModelDetailResult) result;
                                                    runOnUiThread(new Runnable() {
                                                        @Override
                                                        public void run() {

                                                            AddSaleModel newmodel = new AddSaleModel(res.modelcode, res.modelname, res.spec);
                                                            addmodelList.add(newmodel);
                                                            RefreshListview();
                                                        }
                                                    });
                                                }

                                                @Override
                                                public void onError(int code, final String errormsg) {
                                                    runOnUiThread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            Toast.makeText(AddSaleActivity.this, errormsg, Toast.LENGTH_SHORT).show();
                                                        }
                                                    });
                                                }
                                            });
                                        } else {
                                            Toast.makeText(AddSaleActivity.this, SCExceptionCodeList.getExceptionMsg(resultcode), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }
                        });
                    }
                }
                break;

            default:
                break;
        }
    }
    boolean isverify = true;
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
        edt_remark =  (EditText) findViewById(R.id.tv_remark);
        edt_remark.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                remark = s.toString();
            }
        });
        tv_clientname = (EditText) findViewById(R.id.tv_clientname);
        tv_clientname.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                client = s.toString();
            }
        });
        tv_clientname.requestFocus();
        tv_clientname.postDelayed(new Runnable() {
            @Override
            public void run() {
                InputMethodManager imm= (InputMethodManager)AddSaleActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(tv_clientname, 0);
            }
        }, 500);

       // getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        tv_clienttel = (EditText) findViewById(R.id.tv_clienttel);
        tv_clienttel.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                tel= s.toString();
            }
        });
        tv_address = (EditText) findViewById(R.id.tv_address);
        tv_address.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                address = s.toString();
            }
        });
        tv_confirm = (TextView) findViewById(R.id.tv_confirm);
        tv_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm =  (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                if(imm != null) {
                    imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
                }
                //入库数量>0, 供应商不为空,cost>0
                isverify = true;
                if(TextUtil.isEmpty(client)){
                    isverify = false;
                    Toast.makeText(AddSaleActivity.this, "客户姓名不能为空！", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(TextUtil.isEmpty(tel)){
                    isverify = false;
                    Toast.makeText(AddSaleActivity.this, "客户手机不能为空！", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(addmodelList.size()==0){
                    isverify = false;
                    Toast.makeText(AddSaleActivity.this, "请添加型号！", Toast.LENGTH_SHORT).show();
                    return;
                }

                for (AddSaleModel model:addmodelList) {
                    if(TextUtil.isEmpty(model.getSaleCount())){
                        isverify = false;
                        Toast.makeText(AddSaleActivity.this, model.getModelName()+"销售数量不能为空！", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if(TextUtil.isEmpty(model.getCost())){
                        isverify = false;
                        Toast.makeText(AddSaleActivity.this, model.getModelName()+"销售单价不能为空！", Toast.LENGTH_SHORT).show();
                        return;
                    }

                }
                //提交服务器AddSaleOrder
                if(isverify){
                    //型号编码-销售数量-销售单价，型号编码-销售数量-销售单价
                    Session.CheckRefreshToken(new RefreshTokenListener() {
                        @Override
                        public void RefreshTokenResult(final int resultcode) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (resultcode == Codes.Code_Success) {
                                        //1st
                                        String models = addmodelList.get(0).getModelCode() + "-" + addmodelList.get(0).getSaleCount() + "-" + addmodelList.get(0).getCost();

                                        //2....
                                        for(int i=1; i<addmodelList.size(); i++){
                                            models = models + "," + addmodelList.get(i).getModelCode() + "-" + addmodelList.get(i).getSaleCount() + "-" + addmodelList.get(i).getCost();
                                        }
                                        System.out.println(models);

                                        SCSDK.getInstance().AddSaleOrder(client,models,Session.getInstance().getShopCode(),tel,remark, address, Session.getInstance().getToken(), new SCResponseListener() {
                                            @Override
                                            public void onResult(Object result) {
                                               final SCResult.AddSaleOrderResult res = (SCResult.AddSaleOrderResult)result;
                                                runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        Toast.makeText(AddSaleActivity.this, "新增销售出库成功！", Toast.LENGTH_SHORT).show();
                                                        //跳转到saleorderdetail页面
                                                        Intent detail = new Intent(AddSaleActivity.this, SaleDetailActivity.class);
                                                        detail.putExtra("salecode", res.saleordercode);
                                                        startActivity(detail);
                                                        finish();
                                                    }
                                                });
                                            }

                                            @Override
                                            public void onError(int code, final String errormsg) {
                                                runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        Toast.makeText(AddSaleActivity.this, errormsg, Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                            }
                                        });
                                    } else {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                Toast.makeText(AddSaleActivity.this, SCExceptionCodeList.getExceptionMsg(resultcode), Toast.LENGTH_SHORT).show();

                                            }
                                        });
                                    }
                                }
                            });
                        }
                    });
                }

            }
        });
        tv_add = (TextView) findViewById(R.id.tv_add);
        tv_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddSaleActivity.this, PurchaseSearchActivity.class);
                startActivityForResult(intent, modelrequest);
            }
        });
        listview_models = (ListView) findViewById(R.id.listview_models);

    }

    private void RefreshListview() {
        if (addSaleModelAdapter == null) {
            addSaleModelAdapter = new AddSaleModelAdapter(AddSaleActivity.this, addmodelList, new RemoveAddSaleListener() {
                @Override
                public void RemoveAddSale(String modelcode) {
                    for (int i=0; i< addmodelList.size(); i++) {
                        if (addmodelList.get(i).getModelCode().equals(modelcode)) {
                            addmodelList.remove(i);
                            break;
                        }
                    }
                    RefreshListview();
                }
            },
                    new EditCountListener() {
                        @Override
                        public void EditCount(int index, String modelcode, String count) {
                            for (int i=0; i< addmodelList.size(); i++) {
                                if (addmodelList.get(i).getModelCode().equals(modelcode)) {
                                    addmodelList.get(i).setSaleCount(count);
                                    break;
                                }
                            }
                        }
                    }, new EditCostListener() {
                @Override
                public void EditCost(int index, String modelcode, String cost) {
                    for (int i=0; i< addmodelList.size(); i++) {
                        if (addmodelList.get(i).getModelCode().equals(modelcode)) {
                            addmodelList.get(i).setCost(cost);
                            break;
                        }
                    }
                }
            });
            listview_models.setAdapter(addSaleModelAdapter);
        } else {
            addSaleModelAdapter.setModellist(addmodelList);
            addSaleModelAdapter.notifyDataSetChanged();
        }
    }
}
