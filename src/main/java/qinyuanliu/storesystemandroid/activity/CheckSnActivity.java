package qinyuanliu.storesystemandroid.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
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
 * Created by qinyuanliu on 2019/6/10.
 */

public class CheckSnActivity extends Activity {
    private Button btn_check;
    private EditText edt_sn;
    private String checksn;
    //退出时的时间
    private long mExitTime = 0;
    SharedPreferences sp;
    SharedPreferences.Editor ed;
    private SyncDataHelper syncdataHelper=null;

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK
                && event.getAction() == KeyEvent.ACTION_DOWN) {
            exit();
            return true;
        }
        return super.dispatchKeyEvent(event);
    }
    private void exit() {
        if ((System.currentTimeMillis() - mExitTime) > 2000) {
            Toast.makeText(CheckSnActivity.this, "再按一次应用将进入后台", Toast.LENGTH_SHORT).show();
            mExitTime = System.currentTimeMillis();
        } else {
            moveTaskToBack(true);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checksn);

        sp = getSharedPreferences("storesystemdemo", MODE_PRIVATE);
        ed = sp.edit();
        checksn = Session.getInstance().getLoginSN();
        initView();
        initData();
    }

    private void initView() {
        btn_check = (Button) findViewById(R.id.btn_check);
        btn_check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClickCheck();
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
                checksn = s.toString();
            }
        });
        edt_sn.setText(checksn);
    }

    private void initData(){

        //每次打开这个页面，清空数据库账号信息（如果有已保存的账号信息）
        syncdataHelper = new SyncDataHelper(CheckSnActivity.this);
        syncdataHelper.DeleteAccount();
    }

    private void ClickCheck() {
        if (checksn != null && !checksn.equals("")) {
//验证sn，成功后 1sn记入本地sp中  2记录ArrayList<CheckShop> shops，数组转json字符串
            SCSDK.getInstance().CheckSN(checksn, new SCResponseListener() {
                @Override
                public void onResult(Object result) {
                    final SCResult.CheckSNResult res = (SCResult.CheckSNResult) result;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (res.shops.size() > 0) {
                                //记录当前sn返回的ArrayList<CheckShop>
                                String shopmapStr = Json.encode(res);
                                ed.putString("loginshopmap", shopmapStr);
                                ed.putString("loginsn",checksn);
                                ed.commit();
                                Session.getInstance().setShops(res.shops);
                                Session.getInstance().setLoginSN(checksn);
                                Toast.makeText(CheckSnActivity.this, "验证成功！请继续登录", Toast.LENGTH_SHORT).show();
                                //验证loginaccount
                                startActivity(new Intent(CheckSnActivity.this, LoginActivity.class));
                                finish();
                            } else {
                                Toast.makeText(CheckSnActivity.this, "当前序列号没有可选择的门店！请重新输入", Toast.LENGTH_SHORT).show();

                            }
                        }
                    });
                }

                @Override
                public void onError(int code, final String errormsg) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(CheckSnActivity.this, errormsg, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
        }
        else{
            Toast.makeText(CheckSnActivity.this, "序列号不能为空！", Toast.LENGTH_SHORT).show();
        }
    }
}
