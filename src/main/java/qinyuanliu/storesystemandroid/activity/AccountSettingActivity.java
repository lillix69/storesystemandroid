package qinyuanliu.storesystemandroid.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import qinyuanliu.storesystemandroid.R;
import qinyuanliu.storesystemandroid.adapter.AccountAdapter;
import qinyuanliu.storesystemandroid.common.Session;
import qinyuanliu.storesystemandroid.db.SyncDataHelper;
import qinyuanliu.storesystemandroid.http.Codes;
import qinyuanliu.storesystemandroid.http.Constants;
import qinyuanliu.storesystemandroid.http.SCExceptionCodeList;
import qinyuanliu.storesystemandroid.http.SCResponseListener;
import qinyuanliu.storesystemandroid.http.SCResult;
import qinyuanliu.storesystemandroid.http.SCSDK;
import qinyuanliu.storesystemandroid.listener.ClickChangeAccountListener;
import qinyuanliu.storesystemandroid.listener.RefreshTokenListener;
import qinyuanliu.storesystemandroid.model.AccountModel;

/**
 * Created by qinyuanliu on 2019/2/16.
 */

public class AccountSettingActivity extends BaseActivity {
    private Button btn_back;
    private ListView listview_account;
    private TextView tv_changetologin;

    private SyncDataHelper syncdataHelper = null;
    private AccountAdapter accountAdapter;
    private ArrayList<AccountModel> queryarray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accountsetting);

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
        tv_changetologin = (TextView) findViewById(R.id.tv_changetologin);
        tv_changetologin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LogoutAPI();
            }
        });
        listview_account = (ListView) findViewById(R.id.listview_account);
        initData();
        RefreshListview();
    }

    private void initData() {
        syncdataHelper = new SyncDataHelper(this);
        //account+storecode+port 为主键key，value为json字符串：服务器IP 端口 门店编号 门店名称 登录账户名 昵称 密码
        queryarray = syncdataHelper.queryAccountlist(Session.getInstance().getCurrentUsername(), Session.getInstance().getShopCode(),Constants.API_SERVER_PORT);
    }

    private void LogoutAPI() {
        Session.CheckRefreshToken(new RefreshTokenListener() {
            @Override
            public void RefreshTokenResult(final int resultcode) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (resultcode == Codes.Code_Success) {
                            SCSDK.getInstance().Logout(Session.getInstance().getShopCode(), Session.getInstance().getCurrentUsername(), Session.getInstance().getToken(), Session.getInstance().getDeviceid(), Session.getInstance().getRegID(), new SCResponseListener() {
                                @Override
                                public void onResult(Object result) {
                                    if (result != null) {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                Toast.makeText(AccountSettingActivity.this, "请重新登录要切换的账户", Toast.LENGTH_SHORT).show();
                                                Session.clearSession();
                                                SharedPreferences sp = getSharedPreferences("storesystemdemo", MODE_PRIVATE);
                                                SharedPreferences.Editor ed = sp.edit();
                                                ed.putString("loginusername", "");
                                                ed.putString("loginpwd", "");
                                                ed.commit();

                                                startActivity(new Intent(AccountSettingActivity.this, LoginActivity.class));
                                                finish();
                                            }
                                        });
                                    }
                                }

                                @Override
                                public void onError(int code, final String errormsg) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(AccountSettingActivity.this, errormsg, Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            });

                        } else {
                            Toast.makeText(AccountSettingActivity.this, SCExceptionCodeList.getExceptionMsg(resultcode), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }

    private void RefreshListview() {
        if (accountAdapter == null) {
            accountAdapter = new AccountAdapter(AccountSettingActivity.this, queryarray, new ClickChangeAccountListener() {
                @Override
                public void ClickChange(final AccountModel changeaccount) {
                    final String oldaccount = Session.getInstance().getCurrentUsername();
                    final String oldstore = Session.getInstance().getShopCode();
                    final String oldpwd = Session.getInstance().getCurrentPwd();
                    final String oldip = Constants.SERVER_IP;
                    final int oldport = Constants.API_SERVER_PORT;
                    Session.CheckRefreshToken(new RefreshTokenListener() {
                        @Override
                        public void RefreshTokenResult(final int resultcode) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (resultcode == Codes.Code_Success) {
                                        //尝试用changeaccount登录
                                        Constants.SERVER_IP = changeaccount.ip;
                                        Constants.API_SERVER_PORT = changeaccount.port;
                                        SCSDK.getInstance().Login(changeaccount.storecode, changeaccount.loginaccount, changeaccount.loginpwd, Session.getInstance().getDeviceid(), new SCResponseListener() {
                                            @Override
                                            public void onResult(Object result) {
                                                final SCResult.LoginAccountResult res = (SCResult.LoginAccountResult) result;
                                                runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        //成功后 1同步session/sp/constant中的currentaccount信息
                                                        Session.getInstance().setToken(res.token);
                                                        Session.getInstance().setCurrentUsername(changeaccount.loginaccount);
                                                        Session.getInstance().setCurrentPwd(changeaccount.loginpwd);
                                                        Session.getInstance().setNickname(changeaccount.nick);
                                                        Session.getInstance().setShopname(changeaccount.storename);
                                                        Session.getInstance().setShopCode(changeaccount.storecode);
                                                        Session.getInstance().setIsLogin(true);
                                                        Session.getInstance().setLastTokenTimestamp(System.currentTimeMillis());
                                                        SharedPreferences sp = getSharedPreferences("storesystemdemo", MODE_PRIVATE);
                                                        SharedPreferences.Editor ed = sp.edit();
                                                        ed.putString("loginusername", changeaccount.loginaccount);
                                                        ed.putString("loginpwd", changeaccount.loginpwd);
                                                        ed.putString("shopcode", changeaccount.storecode);
                                                        ed.putString("serverip", Constants.SERVER_IP);
                                                        ed.putInt("serverport", Constants.API_SERVER_PORT);
                                                        ed.commit();
                                                        //2改select标志位并刷新页面，同步数据库
                                                        for (AccountModel account : queryarray) {
                                                            if (account.loginaccount.equals(oldaccount) && account.storecode.equals(oldstore) && account.port==oldport) {
                                                                account.isSelected = false;
                                                                AccountModel accountModel = new AccountModel(account.loginaccount, account.loginpwd, account.nick, account.storecode, account.storename, account.ip, account.port, false);
                                                                syncdataHelper.InsertOrUpdataAccount(accountModel);
                                                            }
                                                            if (account.loginaccount.equals(changeaccount.loginaccount) && account.storecode.equals(changeaccount.storecode) && account.port == changeaccount.port) {
                                                                AccountModel accountModel = new AccountModel(account.loginaccount, account.loginpwd, account.nick, account.storecode, account.storename, account.ip, account.port, true);
                                                                syncdataHelper.InsertOrUpdataAccount(accountModel);
                                                                account.isSelected = true;
                                                            }
                                                        }
                                                        accountAdapter.setAccountlist(queryarray);
                                                        accountAdapter.notifyDataSetChanged();
                                                        Toast.makeText(AccountSettingActivity.this, "切换账号成功", Toast.LENGTH_SHORT).show();
//
                                                    }
                                                });
                                            }

                                            @Override
                                            public void onError(int code, final String errormsg) {
                                                runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        //不成功则提示出错
                                                        Constants.SERVER_IP = oldip;
                                                        Constants.API_SERVER_PORT = oldport;
                                                        Toast.makeText(AccountSettingActivity.this, errormsg, Toast.LENGTH_SHORT).show();
//                                                        //用切换前的账户重新登录
//                                                        SCSDK.getInstance().Login(oldstore, oldaccount, oldpwd, Session.getInstance().getDeviceid(), new SCResponseListener() {
//                                                            @Override
//                                                            public void onResult(Object result) {
//                                                                final SCResult.LoginAccountResult res = (SCResult.LoginAccountResult) result;
//                                                                runOnUiThread(new Runnable() {
//                                                                    @Override
//                                                                    public void run() {
//                                                                        //成功后 1同步session/sp/constant中的currentaccount信息
//                                                                        Session.getInstance().setToken(res.token);
//                                                                        Session.getInstance().setCurrentUsername(changeaccount.loginaccount);
//                                                                        Session.getInstance().setCurrentPwd(changeaccount.loginpwd);
//                                                                        Session.getInstance().setNickname(changeaccount.nick);
//                                                                        Session.getInstance().setShopname(changeaccount.storename);
//                                                                        Session.getInstance().setShopCode(changeaccount.storecode);
//                                                                        Session.getInstance().setIsLogin(true);
//                                                                        Session.getInstance().setLastTokenTimestamp(System.currentTimeMillis());
//                                                                        SharedPreferences sp = getSharedPreferences("storesystemdemo", MODE_PRIVATE);
//                                                                        SharedPreferences.Editor ed = sp.edit();
//                                                                        ed.putString("loginusername", changeaccount.loginaccount);
//                                                                        ed.putString("loginpwd", changeaccount.loginpwd);
//                                                                        ed.putString("shopcode", changeaccount.storecode);
//                                                                        ed.putString("serverip", Constants.SERVER_IP);
//                                                                        ed.putInt("serverport", Constants.API_SERVER_PORT);
//                                                                        ed.commit();
//
//                                                                    }
//                                                                });
//                                                            }
//
//                                                            @Override
//                                                            public void onError(int code, final String errormsg) {
//                                                                runOnUiThread(new Runnable() {
//                                                                    @Override
//                                                                    public void run() {
//                                                                        //不成功则提示出错
//                                                                        Toast.makeText(AccountSettingActivity.this, errormsg, Toast.LENGTH_SHORT).show();
//                                                                    }
//                                                                });
//                                                            }
//                                                        });
                                                    }
                                                });
                                            }
                                        });
                                    } else {
                                        Toast.makeText(AccountSettingActivity.this, SCExceptionCodeList.getExceptionMsg(resultcode), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    });
                }
            });
            listview_account.setAdapter(accountAdapter);
        } else {
            accountAdapter.setAccountlist(queryarray);
            accountAdapter.notifyDataSetChanged();
        }
    }
}



