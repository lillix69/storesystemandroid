package qinyuanliu.storesystemandroid.activity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import qinyuanliu.storesystemandroid.R;
import qinyuanliu.storesystemandroid.common.Session;
import qinyuanliu.storesystemandroid.http.Codes;
import qinyuanliu.storesystemandroid.http.SCExceptionCodeList;
import qinyuanliu.storesystemandroid.http.SCResponseListener;
import qinyuanliu.storesystemandroid.http.SCSDK;
import qinyuanliu.storesystemandroid.listener.RefreshTokenListener;

/**
 * Created by qinyuanliu on 2019/8/4.
 */

public class EditCustomerActivity extends BaseActivity {
    private Button btn_back;
    private TextView tv_ordercode;
    private TextView tv_confirm;
    private EditText edt_name;
    private EditText edt_tel;
    private EditText edt_remark;
    private EditText edt_receivertel;
    private EditText edt_receivername;
    private EditText edt_receiveradd;

    private String saleordercode;
    private String name;
    private String tel;
    private String remark;
    private String receivername;
    private String receivertel;
    private String receiveraddress;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        saleordercode = getIntent().getStringExtra("saleordercode");
        name = getIntent().getStringExtra("salecustomername");
        tel = getIntent().getStringExtra("salecustomertel");
        remark = getIntent().getStringExtra("saleremark");
        receivername = getIntent().getStringExtra("receivername");
        receivertel = getIntent().getStringExtra("receivertel");
        receiveraddress = getIntent().getStringExtra("receiveraddress");

        setContentView(R.layout.activity_editcustomer);
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
        tv_confirm = (TextView) findViewById(R.id.tv_confirm);
        tv_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClickEdit();
            }
        });
        edt_name = (EditText) findViewById(R.id.edt_name);
        edt_name.setText(name);
        edt_name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                name = s.toString();
            }
        });
        edt_tel = (EditText) findViewById(R.id.edt_tel);
        edt_tel.setText(tel);
        edt_tel.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                tel = s.toString();
            }
        });
        edt_remark = (EditText) findViewById(R.id.edt_remark);
        edt_remark.setText(remark);
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
        edt_receivertel = (EditText) findViewById(R.id.edt_receivertel);
        edt_receivertel.setText(receivertel);
        edt_receivertel.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                receivertel = s.toString();
            }
        });
        edt_receivername = (EditText) findViewById(R.id.edt_receivername);
        edt_receivername.setText(receivername);
        edt_receivername.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                receivername = s.toString();
            }
        });
        edt_receiveradd = (EditText) findViewById(R.id.edt_receiveradd);
        edt_receiveradd.setText(receiveraddress);
        edt_receiveradd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                receiveraddress = s.toString();
            }
        });
    }

    private void ClickEdit() {
        if (name.equals("")) {
            Toast.makeText(EditCustomerActivity.this, "客户姓名不能为空！", Toast.LENGTH_SHORT).show();
            return;
        }
        if (tel.equals("")) {
            Toast.makeText(EditCustomerActivity.this, "客户手机不能为空！", Toast.LENGTH_SHORT).show();
            return;
        }
        Session.CheckRefreshToken(new RefreshTokenListener() {
            @Override
            public void RefreshTokenResult(final int resultcode) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (resultcode == Codes.Code_Success) {
                            SCSDK.getInstance().ModeifySaleorderCustomer(saleordercode, name, tel, receivername, receivertel, remark,receiveraddress, Session.getInstance().getShopCode(), Session.getInstance().getToken(), new SCResponseListener() {
                                @Override
                                public void onResult(Object result) {

                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(EditCustomerActivity.this, "修改成功！", Toast.LENGTH_SHORT).show();
                                            finish();
                                        }
                                    });
                                }

                                @Override
                                public void onError(int code, final String errormsg) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(EditCustomerActivity.this, errormsg, Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            });
                        } else {
                            Toast.makeText(EditCustomerActivity.this, SCExceptionCodeList.getExceptionMsg(resultcode), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }

}
