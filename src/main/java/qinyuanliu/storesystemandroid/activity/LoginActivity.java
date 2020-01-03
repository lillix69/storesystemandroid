package qinyuanliu.storesystemandroid.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;

import qinyuanliu.storesystemandroid.R;
import qinyuanliu.storesystemandroid.common.Session;
import qinyuanliu.storesystemandroid.db.SyncDataHelper;
import qinyuanliu.storesystemandroid.http.Constants;
import qinyuanliu.storesystemandroid.http.SCResponseListener;
import qinyuanliu.storesystemandroid.http.SCResult;
import qinyuanliu.storesystemandroid.http.SCSDK;
import qinyuanliu.storesystemandroid.model.AccountModel;
import qinyuanliu.storesystemandroid.util.UpdateManager;

public class LoginActivity extends AppCompatActivity {
private Button btn_login;
    private EditText edt_username;
    private EditText edt_pwd;
    private TextView tv_shopname;
private ImageView img_chooseshop;
    private String username = "";
    private String pwd = "";
    private String shopname = "";
    private String shopcode="";

private ImageView img_setting;
//    //退出时的时间
//    private long mExitTime = 0;

    private SyncDataHelper syncdataHelper=null;
    private AlertDialog shopAlert;
    private String[] shopData;
    private HashMap<String, SCResult.CheckShop> shopnamemap = new HashMap<>();

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK
                && event.getAction() == KeyEvent.ACTION_DOWN) {
//            exit();
            return true;
        }
        return super.dispatchKeyEvent(event);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initView();
        initData();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(!Session.getInstance().getShopCode().equals("")) {
            Session.getInstance().setContext(LoginActivity.this);
           new Thread(new UpdateManager()).start();
       }
//       if(!Session.getInstance().getCurrentUsername().equals("") && !Session.getInstance().getCurrentPwd().equals("") ){
           edt_pwd.setText(Session.getInstance().getCurrentPwd());
           edt_username.setText(Session.getInstance().getCurrentUsername());
     //  }
    }

private void initData(){

    shopData = new String[Session.getInstance().getShops().size()];
    for (int i = 0; i < Session.getInstance().getShops().size(); i++) {
        SCResult.CheckShop temp = Session.getInstance().getShops().get(i);
        shopData[i] = temp.shopname;
        shopnamemap.put(temp.shopname, temp);
    }
}
    private void initView() {
        img_setting = (ImageView)findViewById(R.id.img_setting);
        img_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, ServerConfigActivity.class));
            }
        });
        img_chooseshop = (ImageView)findViewById(R.id.img_chooseshop);
        img_chooseshop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClickShop();
            }
        });
        tv_shopname = (TextView)findViewById(R.id.tv_shopname);
        tv_shopname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              ClickShop();
            }
        });
        btn_login = (Button) findViewById(R.id.btn_login);
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClickLogin();
            }
        });
        edt_pwd = (EditText) findViewById(R.id.edt_accountpwd);
        edt_pwd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                pwd = s.toString();
            }
        });
        edt_username = (EditText) findViewById(R.id.edt_accountname);
        edt_username.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                username = s.toString();
            }
        });
    }

    private void ClickShop(){
        if(shopAlert == null){
            shopAlert = new AlertDialog.Builder(LoginActivity.this)
                    .setTitle("选择门店")
                    .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            tv_shopname.setText(shopname);
                            for (SCResult.CheckShop tempshop:Session.getInstance().getShops()) {
                                if(tempshop.shopname.equals(shopname)){
                                    Session.getInstance().setShopCode(shopcode);
                                    SharedPreferences sp = getSharedPreferences("storesystemdemo", MODE_PRIVATE);
                                    SharedPreferences.Editor ed = sp.edit();
                                    ed.putString("shopcode",shopcode);
                                    ed.putString("serverip", tempshop.server);
                                    ed.putInt("serverport",Integer.valueOf(tempshop.port));
                                    ed.commit();
                                    SCSDK.getInstance().ServerConfig(tempshop.server,Integer.valueOf(tempshop.port));
                                    break;
                                }
                            }

                            shopAlert.dismiss();
                        }
                    })
                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            shopAlert.dismiss();
                        }
                    })
                    .setSingleChoiceItems(shopData, -1, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            shopcode = shopnamemap.get(shopData[which]).shopcode;
                            shopname = shopData[which];
                        }
                    }).create();
        }
        shopAlert.show();
    }



    private void ClickLogin(){
        if (username.equals("")) {
            Toast.makeText(LoginActivity.this, "请输入账户名", Toast.LENGTH_SHORT).show();
            return;
        }
        if (pwd.equals("")) {
            Toast.makeText(LoginActivity.this, "请输入密码", Toast.LENGTH_SHORT).show();
            return;
        }
        if(shopname.equals("")){
            Toast.makeText(LoginActivity.this, "请选择门店号", Toast.LENGTH_SHORT).show();
           // startActivity(new Intent(LoginActivity.this, ServerConfigActivity.class));
            return;
        }
        btn_login.setSelected(false);
        VerifiyLogin();
    }

    private void VerifiyLogin() {
        SCSDK.getInstance().Login(shopcode,username, pwd, Session.getInstance().getDeviceid(), new SCResponseListener() {
            @Override
            public void onResult(Object result) {
                if (result != null) {
                    final SCResult.LoginAccountResult res = (SCResult.LoginAccountResult) result;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //数据库update
                            syncdataHelper = new SyncDataHelper(LoginActivity.this);
                            AccountModel accountModel = new AccountModel(username,pwd,res.nickname,Session.getInstance().getShopCode(), res.shopname, Constants.SERVER_IP, Constants.API_SERVER_PORT, true);
                            syncdataHelper.InsertOrUpdataAccount(accountModel);
                            //同步session
                            SharedPreferences sp = getSharedPreferences("storesystemdemo", MODE_PRIVATE);
                            boolean lockflag = sp.getBoolean("lockFlag", true);
                            Session.getInstance().setLockFlag(lockflag);
                            Session.getInstance().setToken(res.token);
                            Session.getInstance().setCurrentUsername(username);
                            Session.getInstance().setCurrentPwd(pwd);
                            Session.getInstance().setNickname(res.nickname);
                            Session.getInstance().setShopname(res.shopname);
                            Session.getInstance().setShopCode(shopcode);

                            Session.getInstance().setIsLogin(true);
                            Session.getInstance().setLastTokenTimestamp(System.currentTimeMillis());
                            SharedPreferences.Editor ed = sp.edit();
                            ed.putString("loginusername", username);
                            ed.putString("loginpwd", pwd);
                            ed.commit();
                            btn_login.setSelected(true);

                            SCSDK.getInstance().BindPushAccount(Session.getInstance().getShopCode(), Session.getInstance().getToken(), Session.getInstance().getRegID(), new SCResponseListener() {
                                @Override
                                public void onResult(Object result) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(LoginActivity.this, "绑定推送账户成功", Toast.LENGTH_SHORT).show();
                                            startActivity(new Intent(LoginActivity.this, TabbarActivity.class));
                                            finish();
                                        }
                                    });
                                }

                                @Override
                                public void onError(int code, String errormsg) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(LoginActivity.this, "绑定推送账户失败", Toast.LENGTH_SHORT).show();
                                            startActivity(new Intent(LoginActivity.this, TabbarActivity.class));
                                            finish();
                                        }
                                    });
                                }
                            });
                        }
                    });
                }
            }

            @Override
            public void onError(int code, final String errormsg) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        btn_login.setSelected(true);
                        Toast.makeText(LoginActivity.this, errormsg, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}
