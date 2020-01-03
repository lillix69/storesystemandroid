package qinyuanliu.storesystemandroid.activity;

import android.Manifest;
import android.app.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;

import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import qinyuanliu.storesystemandroid.R;
import qinyuanliu.storesystemandroid.common.Session;
import qinyuanliu.storesystemandroid.http.Constants;
import qinyuanliu.storesystemandroid.http.SCResponseListener;
import qinyuanliu.storesystemandroid.http.SCResult;
import qinyuanliu.storesystemandroid.http.SCSDK;
import qinyuanliu.storesystemandroid.http.util.Json;
import qinyuanliu.storesystemandroid.http.util.StringUtils;

/**
 * Created by lillix on 5/28/18.
 */
public class LaunchActivity extends Activity {
    private Context mcontext;
    SharedPreferences sp;
    SharedPreferences.Editor ed;
//    // Notification manager to displaying arrived push notifications
//    private NotificationManager mNotifMan;
//    private static final int NOTIF_CONNECTED = 100;
//    private Handler handler = new Handler() {
//        //当有消息发送出来的时候就执行Handler的这个方法来处理消息分发
//        @Override
//        public void handleMessage(Message msg) {
//            super.handleMessage(msg);
//
//
//        }
//    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);

        mcontext = this;
        sp = getSharedPreferences("storesystemdemo", MODE_PRIVATE);
        ed = sp.edit();
       // mNotifMan = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        requestCamera();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1) {
            for (int i = 0; i < permissions.length; i++) {
                if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "权限申请失败,请在手机设置中开启", Toast.LENGTH_SHORT).show();
                }
            }
            AutoLogin();
        }
    }

    private void AutoLogin() {
        sp = getSharedPreferences("storesystemdemo", MODE_PRIVATE);
        final SharedPreferences.Editor ed = sp.edit();
        //device id====
        String deviceid = sp.getString("deviceid", "");
        if (deviceid.equals("")) {
            deviceid = StringUtils.GetDeviceIDForAndroidphone();
            ed.putString("deviceid", deviceid);
            ed.commit();
        }
        //server，port
        if(sp.getString("serverip","").equals("")){
            ed.putString("serverip", Constants.SERVER_IP);
            ed.putInt("serverport",Constants.API_SERVER_PORT);
            ed.commit();
        }
        else{
            SCSDK.getInstance().ServerConfig(sp.getString("serverip","www.fssocks.com"),sp.getInt("serverport",5436));
        }
        Session.getInstance().setDeviceid(deviceid);
        final String tempshopcode = sp.getString("shopcode", "");
       // Session.getInstance().setShopCode(tempshopcode);
        //fastmode
        boolean flag = sp.getBoolean("fastmodeFlag", false);
        Session.getInstance().setFastmodeFlag(flag);


        //验证sn
        final String tempsn = sp.getString("loginsn","");
        if(tempsn.equals("")){
            Session.getInstance().setLoginSN("");
            startActivity(new Intent(LaunchActivity.this, CheckSnActivity.class));
            finish();
        }
        else{
            SCSDK.getInstance().CheckSN(tempsn, new SCResponseListener() {
                @Override
                public void onResult(Object result) {
                    final SCResult.CheckSNResult res = (SCResult.CheckSNResult)result;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(res.shops.size()>0){
                                //记录当前sn返回的ArrayList<CheckShop>
                               String shopmapStr = Json.encode(res);
                                ed.putString("loginshopmap", shopmapStr);
                                ed.putString("loginsn",tempsn);
                                ed.commit();
                                Session.getInstance().setShops(res.shops);
                                Session.getInstance().setLoginSN(tempsn);
                                //验证loginaccount
                                LoginAfterChecksn(tempshopcode);
                            }
                           else{
                                Session.getInstance().setLoginSN("");
                                Toast.makeText(LaunchActivity.this, "当前序列号没有可选择的门店！请重新输入", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(LaunchActivity.this, CheckSnActivity.class));
                                finish();
                            }
                        }
                    });
                }

                @Override
                public void onError(int code, final String errormsg) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(LaunchActivity.this, errormsg, Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(LaunchActivity.this, CheckSnActivity.class));
                            finish();
                        }
                    });
                }
            });
        }
    }


    private void LoginAfterChecksn(final String tempshopcode){
        //自动登录login
        final String tempusername = sp.getString("loginusername", "");
        final String temppwd = sp.getString("loginpwd", "");

        if (!tempusername.equals("") && !temppwd.equals("") && !tempshopcode.equals("")) {
            SCSDK.getInstance().Login(tempshopcode, tempusername, temppwd, Session.getInstance().getDeviceid(), new SCResponseListener() {
                @Override
                public void onResult(Object result) {
                    if (result != null) {
                        final SCResult.LoginAccountResult res = (SCResult.LoginAccountResult) result;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Session.getInstance().setToken(res.token);
                                Session.getInstance().setCurrentUsername(tempusername);
                                Session.getInstance().setCurrentPwd(temppwd);
                                Session.getInstance().setNickname(res.nickname);
                                Session.getInstance().setShopname(res.shopname);
                                Session.getInstance().setShopCode(tempshopcode);
                                Session.getInstance().setIsLogin(true);
                                Session.getInstance().setLastTokenTimestamp(System.currentTimeMillis());

//                                sp = getSharedPreferences("storesystemdemo", MODE_PRIVATE);
//                                SharedPreferences.Editor ed = sp.edit();
                                ed.putString("loginusername", tempusername);
                                ed.putString("loginpwd", temppwd);
                                ed.commit();


                                SCSDK.getInstance().BindPushAccount(Session.getInstance().getShopCode(), Session.getInstance().getToken(), Session.getInstance().getRegID(), new SCResponseListener() {
                                    @Override
                                    public void onResult(Object result) {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                Toast.makeText(LaunchActivity.this, "绑定推送账户成功", Toast.LENGTH_SHORT).show();
                                                startActivity(new Intent(LaunchActivity.this, TabbarActivity.class));
                                                finish();
                                            }
                                        });
                                    }

                                    @Override
                                    public void onError(int code, String errormsg) {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                Toast.makeText(LaunchActivity.this, "绑定推送账户失败", Toast.LENGTH_SHORT).show();
                                                startActivity(new Intent(LaunchActivity.this, TabbarActivity.class));
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
                            startActivity(new Intent(LaunchActivity.this, LoginActivity.class));
                            finish();
                        }
                    });
                }
            });
        } else {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }
    }
    public void requestCamera() {
        if (Build.VERSION.SDK_INT >= 23) {
            //判断是否已经赋予权限
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
//                //如果应用之前请求过此权限但用户拒绝了请求，此方法将返回 true。
//                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
//                        Manifest.permission.CAMERA)) {
//                } else {
                //申请权限，字符串数组内是一个或多个要申请的权限，1是申请权限结果的返回参数，在onRequestPermissionsResult可以得知申请结果
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_PHONE_STATE}, 1);
                //   }
            } else {
                AutoLogin();
            }
        } else {
            AutoLogin();
        }
    }

//    public void stopNotification() {
//
//        mNotifMan.cancelAll();
//    }
//
//    public void showNotification(String desp) {
//        //在执行了点击通知之后要跳转到指定的Activity的时候，可以设置以下方法来相应点击事件
//        PendingIntent pi;
//        if (Session.getInstance().getIsLogin()) {
//            Intent clickIntent = new Intent(mcontext, NoticeActivity.class);
////                Bundle bundle = new Bundle();
////                bundle.putString("currentusercode", "");
////                clickalarmIntent.putExtras(bundle);
//            pi = PendingIntent.getActivity(mcontext, 0,
//                    clickIntent, PendingIntent.FLAG_UPDATE_CURRENT);
//        } else {
//            pi = PendingIntent.getActivity(mcontext, 0,
//                    new Intent(mcontext, LaunchActivity.class), 0);
//        }
//
//        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(mcontext);
//        mBuilder.setContentTitle("物料管理消息提醒")//设置通知栏标题
//                .setContentText(desp)
//                .setContentIntent(pi) //设置通知栏点击意图
//                // .setNumber(10) //设置通知集合的数量
//                .setTicker("物料管理消息提醒") //通知首次出现在通知栏，带上升动画效果的
//                .setWhen(System.currentTimeMillis())//通知产生的时间，会在通知信息里显示，一般是系统获取到的时间
//                .setPriority(Notification.PRIORITY_DEFAULT) //设置该通知优先级
////  .setAutoCancel(true)//设置这个标志当用户单击面板就可以让通知将自动取消
//                .setOngoing(false)//ture，设置他为一个正在进行的通知。他们通常是用来表示一个后台任务,用户积极参与(如播放音乐)或以某种方式正在等待,因此占用设备(如一个文件下载,同步操作,主动网络连接)
//                .setDefaults(Notification.DEFAULT_VIBRATE)//向通知添加声音、闪灯和振动效果的最简单、最一致的方式是使用当前的用户默认设置，使用defaults属性，可以组合
//                //Notification.DEFAULT_ALL  Notification.DEFAULT_SOUND 添加声音 // requires VIBRATE permission
//                .setSmallIcon(R.mipmap.ic_launcher)//设置通知小ICON
//                .setAutoCancel(true);
//
//        mNotifMan.notify(NOTIF_CONNECTED, mBuilder.build());
//    }


}
