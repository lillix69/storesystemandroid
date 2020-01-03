package qinyuanliu.storesystemandroid.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import qinyuanliu.storesystemandroid.R;
import qinyuanliu.storesystemandroid.common.Session;
import qinyuanliu.storesystemandroid.db.SyncDataHelper;
import qinyuanliu.storesystemandroid.http.SCResponseListener;
import qinyuanliu.storesystemandroid.http.SCResult;
import qinyuanliu.storesystemandroid.http.SCSDK;
import qinyuanliu.storesystemandroid.http.util.Json;

/**
 * Created by qinyuanliu on 2019/10/28.
 */

public class ChangeSnActivity extends BaseActivity {
    private Button btn_back;
    private EditText edt_sn;
    private Button btn_check;

    private String sn = "";
    SharedPreferences sp;
    SharedPreferences.Editor ed;
    private SyncDataHelper syncdataHelper = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_changesn);

        sp = getSharedPreferences("storesystemdemo", MODE_PRIVATE);
        ed = sp.edit();
        syncdataHelper = new SyncDataHelper(ChangeSnActivity.this);
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
        edt_sn = (EditText) findViewById(R.id.edt_sn);
        edt_sn.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                sn = s.toString();
            }
        });
        edt_sn.setText(Session.getInstance().getLoginSN());
        edt_sn.requestFocus();
        btn_check = (Button) findViewById(R.id.btn_check);
        btn_check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckSn();
            }
        });
    }

    private void CheckSn() {
        if (sn.equals("")) {
            Toast.makeText(ChangeSnActivity.this, "请输入要切换的序列号！", Toast.LENGTH_SHORT).show();
            return;
        }

        SCSDK.getInstance().CheckSN(sn, new SCResponseListener() {
            @Override
            public void onResult(Object result) {
                final SCResult.CheckSNResult res = (SCResult.CheckSNResult) result;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //验证sn，成功后 1删除原来登录账户session，清除数据库sn信息
                        Session.getInstance().setShopCode("");
                        Session.getInstance().setCurrentPwd("");
                        Session.getInstance().setCurrentUsername("");
                        Session.getInstance().setIsLogin(false);
                        syncdataHelper.DeleteAccount();

                        // 2sn记入本地sp中 3记录ArrayList<CheckShop> shops，数组转json字符串
                        if (res.shops.size() > 0) {
                            //记录当前sn返回的ArrayList<CheckShop>
                            String shopmapStr = Json.encode(res);
                            ed.putString("loginshopmap", shopmapStr);
                            ed.putString("loginsn", sn);
                            ed.commit();
                            Session.getInstance().setShops(res.shops);
                            Session.getInstance().setLoginSN(sn);
                            //4跳转登录页面,验证loginaccount
                            Toast.makeText(ChangeSnActivity.this, "验证成功！请继续登录", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(ChangeSnActivity.this, LoginActivity.class));
                            finish();
                        } else {
                            Toast.makeText(ChangeSnActivity.this, "当前序列号没有可选择的门店！请重新输入", Toast.LENGTH_SHORT).show();

                        }
                    }
                });
            }

            @Override
            public void onError(int code, final String errormsg) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(ChangeSnActivity.this, errormsg, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });


    }
}
