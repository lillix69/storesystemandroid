package qinyuanliu.storesystemandroid.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.util.HashMap;

import qinyuanliu.storesystemandroid.R;
import qinyuanliu.storesystemandroid.common.Session;
import qinyuanliu.storesystemandroid.http.Codes;
import qinyuanliu.storesystemandroid.http.SCExceptionCodeList;
import qinyuanliu.storesystemandroid.http.SCResponseListener;
import qinyuanliu.storesystemandroid.http.SCResult;
import qinyuanliu.storesystemandroid.http.SCSDK;
import qinyuanliu.storesystemandroid.listener.RefreshTokenListener;

/**
 * Created by qinyuanliu on 2019/8/3.
 */

public class CloseOrderActivity extends BaseActivity {
    private Button btn_back;
    private TextView tv_ordercode;
    private TextView tv_name;
    private TextView tv_tel;
    private TextView tv_remark;
    private TextView tv_allcost;
    private TextView tv_pay;
    private EditText edt_paycost;
    private TextView tv_discount;
    private TextView tv_confirm;
    private TextView tv_discounttitle;

    private String saleordercode;
    private String name;
    private String tel;
    private String remark;
    private String allcost;
    private String payname;
    private int paymode = -1;
    private String paycost;
    private String discount;
    private float allcostD;

    private String[] payData;
    private HashMap<String, Integer> paynamecode = new HashMap<>();
    private int pay = -1;
    private AlertDialog payAlert;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        saleordercode = getIntent().getStringExtra("saleordercode");
        name = getIntent().getStringExtra("salecustomername");
        tel = getIntent().getStringExtra("salecustomertel");
        remark = getIntent().getStringExtra("saleremark");
        allcost = getIntent().getStringExtra("saleallcost");
        allcostD = Float.valueOf(allcost);

        setContentView(R.layout.activity_closeorder);
        initView();

    }

    private void initView() {
        btn_back = (Button) findViewById(R.id.btn_back);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        tv_ordercode = (TextView) findViewById(R.id.tv_ordercode);
        tv_ordercode.setText(saleordercode);
        tv_name = (TextView) findViewById(R.id.tv_name);
        tv_name.setText(name);
        tv_tel = (TextView) findViewById(R.id.tv_tel);
        tv_tel.setText(tel);
        tv_remark = (TextView) findViewById(R.id.tv_remark);
        tv_remark.setText(remark);
        tv_allcost = (TextView) findViewById(R.id.tv_allcost);
        tv_allcost.setText(allcost);
        tv_pay = (TextView) findViewById(R.id.tv_pay);
        tv_pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClickPay();
            }
        });
        tv_discount = (TextView) findViewById(R.id.tv_discount);
        tv_confirm = (TextView) findViewById(R.id.tv_confirm);
        tv_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClickCloseOrder();
            }
        });
        tv_discounttitle = (TextView)findViewById(R.id.tv_discounttitle);

        edt_paycost = (EditText) findViewById(R.id.edt_paycost);
        edt_paycost.addTextChangedListener(new TextWatcher() {
            boolean deleteLastChar = false;// 是否需要删除末尾

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().contains(".")) {
                    // 如果点后面有超过3位数值,则删掉最后一位
                    int length = s.length() - s.toString().lastIndexOf(".");
                    // 说明后面有4位数值
                    deleteLastChar = length >= 4;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s == null || s.toString().equals("")) {
                    paycost = "0";
                    tv_discount.setText("");
                    return;
                }
                String newcoststr = s.toString();
                if (deleteLastChar) {

                    // 设置新的截取的字符串
                    newcoststr = s.toString().substring(0, s.toString().length() - 1);
                    edt_paycost.setText(newcoststr);
                    // 光标强制到末尾
                    edt_paycost.setSelection(edt_paycost.getText().length());
                }
                paycost = newcoststr;
                float discountD = allcostD - Float.valueOf(paycost);
                //只取小数点后2位
                discount = new DecimalFormat("0.00").format(discountD);
                tv_discount.setText(discount);
            }
        });
    }

    private void ClickPay() {
        if (payData == null) {
            Session.CheckRefreshToken(new RefreshTokenListener() {
                @Override
                public void RefreshTokenResult(final int resultcode) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (resultcode == Codes.Code_Success) {
                                SCSDK.getInstance().GetPayModes(Session.getInstance().getShopCode(), Session.getInstance().getToken(), new SCResponseListener() {
                                    @Override
                                    public void onResult(Object result) {
                                        final SCResult.PayModeResult res = (SCResult.PayModeResult) result;
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                if (res.paymodes != null && res.paymodes.size() > 0) {
                                                    payData = new String[res.paymodes.size()];
                                                    for (int i = 0; i < res.paymodes.size(); i++) {
                                                        SCResult.PayMode temp = res.paymodes.get(i);
                                                        payData[i] = temp.modename;
                                                        paynamecode.put(temp.modename, temp.modecode);
                                                    }
                                                    ShowPayAlert();
                                                }
                                            }
                                        });
                                    }

                                    @Override
                                    public void onError(int code, final String errormsg) {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                Toast.makeText(CloseOrderActivity.this, errormsg, Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }
                                });
                            } else {
                                Toast.makeText(CloseOrderActivity.this, SCExceptionCodeList.getExceptionMsg(resultcode), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            });

        } else {
            ShowPayAlert();
        }
    }

    private String temppayname;
    private int temppaymode;

    private void ShowPayAlert() {
        if (payAlert == null) {
            payAlert = new AlertDialog.Builder(CloseOrderActivity.this)
                    .setTitle("选择付款方式")
                    .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            paymode = temppaymode;
                            payname = temppayname;
                            tv_pay.setText(payname);
                            if(paymode == 1){
                                //【modecode】是1时 实付金额默认值 为0，优惠金额 文字变为 记账金额 （记账金额=总金额-实付金额）
                                paycost = "0";
                                edt_paycost.setText("0");
                                tv_discounttitle.setText("记账金额");
                                discount = allcost;
                                tv_discount.setText(discount);
                            }
                            else{
                                //非赊欠时 记账金额 文字变为 优惠金额
                                tv_discounttitle.setText("优惠金额");
                               edt_paycost.setText("");
                                paycost = "0";
                                tv_discount.setText("");
                            }
                            payAlert.dismiss();
                        }
                    })
                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            payAlert.dismiss();
                        }
                    })
                    .setSingleChoiceItems(payData, -1, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            temppayname = payData[which];
                            temppaymode = paynamecode.get(temppayname);


                        }
                    }).create();
        }
        payAlert.show();
    }

    private void ClickCloseOrder() {
        if (paymode < 0) {
            Toast.makeText(CloseOrderActivity.this, "付款方式不能为空！", Toast.LENGTH_SHORT).show();
            return;
        }
        if (paycost == null || paycost.equals("")) {
            Toast.makeText(CloseOrderActivity.this, "实付金额不能为空！", Toast.LENGTH_SHORT).show();
            return;
        }
        Session.CheckRefreshToken(new RefreshTokenListener() {
            @Override
            public void RefreshTokenResult(final int resultcode) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (resultcode == Codes.Code_Success) {
                            SCSDK.getInstance().SettleSaleOrder(saleordercode, paymode, Double.valueOf(paycost), remark, Session.getInstance().getShopCode(), Session.getInstance().getToken(), new SCResponseListener() {
                                @Override
                                public void onResult(Object result) {

                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(CloseOrderActivity.this, "结算成功！", Toast.LENGTH_SHORT).show();
                                            finish();
                                        }
                                    });
                                }

                                @Override
                                public void onError(int code, final String errormsg) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(CloseOrderActivity.this, errormsg, Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            });
                        } else {
                            Toast.makeText(CloseOrderActivity.this, SCExceptionCodeList.getExceptionMsg(resultcode), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }
}
