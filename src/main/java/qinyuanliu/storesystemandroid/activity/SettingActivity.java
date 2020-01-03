package qinyuanliu.storesystemandroid.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import qinyuanliu.storesystemandroid.Manifest;
import qinyuanliu.storesystemandroid.R;
import qinyuanliu.storesystemandroid.common.Session;
import qinyuanliu.storesystemandroid.db.SyncDataHelper;
import qinyuanliu.storesystemandroid.http.Codes;
import qinyuanliu.storesystemandroid.http.Constants;
import qinyuanliu.storesystemandroid.http.SCExceptionCodeList;
import qinyuanliu.storesystemandroid.http.SCResponseListener;
import qinyuanliu.storesystemandroid.http.SCResult;
import qinyuanliu.storesystemandroid.http.SCSDK;
import qinyuanliu.storesystemandroid.listener.RefreshTokenListener;
import qinyuanliu.storesystemandroid.model.AccountModel;

/**
 * Created by lillix on 5/28/18.
 */
public class SettingActivity extends BaseActivity {
    private Button btn_back;
    private TextView tv_logout;
    private TextView versionview;
    private RelativeLayout qrcodeview;
    private RelativeLayout offduty;
    private RelativeLayout accountsetting;
    private RelativeLayout modelvalue;
    private RelativeLayout question;
    private RelativeLayout changesn;
    private RelativeLayout batchview;

    private Context mcontext = SettingActivity.this;
    private Switch sw;
    private boolean flag;
    private Switch sw_lock;
    private boolean lockflag;
    private RelativeLayout locksetting;
    SharedPreferences sp;
    SharedPreferences.Editor ed;
    private String versioninfo = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        sp = getSharedPreferences("storesystemdemo", MODE_PRIVATE);
        ed = sp.edit();

        btn_back = (Button) findViewById(R.id.btn_back);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        tv_logout = (TextView) findViewById(R.id.tv_logout);
        tv_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClickLogout();
            }
        });

        versionview = (TextView) findViewById(R.id.versionview);
        versionview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClickVersion();
            }
        });

        sw = (Switch) findViewById(R.id.sw_fastmode);
        flag = Session.getInstance().getFastmodeFlag();
        sw.setChecked(flag);
        sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                flag = isChecked;
                Session.getInstance().setFastmodeFlag(flag);
                ed.putBoolean("fastmodeFlag", flag);
                ed.apply();
            }
        });
        changesn = (RelativeLayout)findViewById(R.id.changesn);
        changesn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Session.getInstance().setShopCode("");
//                Session.getInstance().setCurrentPwd("");
//                Session.getInstance().setCurrentUsername("");
                startActivity(new Intent(mcontext, ChangeSnActivity.class));
               // finish();
            }
        });
        question = (RelativeLayout)findViewById(R.id.question);
        question.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SettingActivity.this, ShowQuestionActivity.class));
            }
        });
        modelvalue = (RelativeLayout)findViewById(R.id.modelvalue);
        modelvalue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SettingActivity.this, ShowValueActivity.class));
            }
        });

        qrcodeview = (RelativeLayout) findViewById(R.id.qrcodeview);
        qrcodeview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SettingActivity.this, ShowQRcodeActivity.class));
            }
        });
        offduty = (RelativeLayout)findViewById(R.id.offduty);
        offduty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SettingActivity.this, OffDutyActivity.class));
            }
        });

        accountsetting = (RelativeLayout)findViewById(R.id.accountsetting);
        accountsetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SettingActivity.this,AccountSettingActivity.class));
            }
        });

        locksetting = (RelativeLayout)findViewById(R.id.locksetting);
        locksetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SettingActivity.this,LockSettingActivity.class));
            }
        });

        sw_lock = (Switch) findViewById(R.id.sw_lock);
        lockflag = Session.getInstance().getLockFlag();
        sw_lock.setChecked(lockflag);
        sw_lock.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                lockflag = isChecked;
                Session.getInstance().setLockFlag(lockflag);
                ed.putBoolean("lockFlag",lockflag);
                ed.apply();
            }
        });

        batchview = (RelativeLayout)findViewById(R.id.batchview);
        batchview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SettingActivity.this,BatchActivity.class));
            }
        });
    }
private void ClickVersion(){
if(versioninfo.equals("")){
    Session.CheckRefreshToken(new RefreshTokenListener() {
        @Override
        public void RefreshTokenResult(final int resultcode) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (resultcode == Codes.Code_Success) {
                        try{
                        SCSDK.getInstance().GetVersionRemark(Integer.toString(getLocalVersionCode()),Session.getInstance().getShopCode(), new SCResponseListener() {
                            @Override
                            public void onResult(Object result) {
                                final SCResult.VersionRemarkResult versionInfo = (SCResult.VersionRemarkResult) result;
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                          versioninfo = versionInfo.remark;
                                        ShowVersionAlert();
                                    }
                                });
                            }

                            @Override
                            public void onError(int code, final String errormsg) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(mcontext, errormsg, Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        });
                        } catch (PackageManager.NameNotFoundException e) {
                            e.printStackTrace();
                        }
                    } else {
                        Toast.makeText(SettingActivity.this, SCExceptionCodeList.getExceptionMsg(resultcode), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    });
}
else{
    ShowVersionAlert();
}
}

private void ShowVersionAlert(){
     AlertDialog.Builder builder = new AlertDialog.Builder(mcontext);
    builder.setTitle("版本说明");
    builder.setMessage(versioninfo);
    builder.setPositiveButton("检查更新", new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
           CheckVersion();
            dialog.dismiss();
        }
    });
    builder.setNegativeButton("取消", null);
    builder.create().show();
}
    private void CheckVersion() {
        Session.CheckRefreshToken(new RefreshTokenListener() {
            @Override
            public void RefreshTokenResult(final int resultcode) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (resultcode == Codes.Code_Success) {
                            SCSDK.getInstance().GetVersion(Session.getInstance().getShopCode(), new SCResponseListener() {
                                @Override
                                public void onResult(Object result) {
                                    final SCResult.VersionResult updateInfo = (SCResult.VersionResult) result;
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            try {
                                                //后台版本
                                                int serverVersion = updateInfo.version;
                                                //本地版本获取
                                                int localVersionCode = getLocalVersionCode();
                                                //后台版本新！弹框提醒用户有新版本，让用户操作dialog更新
                                                if (serverVersion > localVersionCode) {
                                                    downloadurl = updateInfo.downloadurl;
                                                    openUpdateDialog();
                                                } else {
                                                    Toast.makeText(mcontext, "已经是最新版本", Toast.LENGTH_SHORT).show();
                                                }
                                            } catch (PackageManager.NameNotFoundException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    });
                                }

                                @Override
                                public void onError(int code, final String errormsg) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(mcontext, errormsg, Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            });
                        } else {
                            Toast.makeText(SettingActivity.this, SCExceptionCodeList.getExceptionMsg(resultcode), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_UNKNOWN_APP) {

            downloadNewVersion(downloadurl);
        }
    }

    private String downloadurl;
    private static final int REQUEST_CODE_UNKNOWN_APP = 2001;
    private void openUpdateDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(mcontext);
        builder.setTitle("版本有更新");
        builder.setMessage("是否立刻更新您的应用？");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (Build.VERSION.SDK_INT >= 26) {
                    boolean b = getPackageManager().canRequestPackageInstalls();
                    if (b) {
                        downloadNewVersion(downloadurl);//安装应用的逻辑(写自己的就可以)
                    } else {
                        Intent intent = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES);
                        startActivityForResult(intent, REQUEST_CODE_UNKNOWN_APP);
                    }
                } else {
                    downloadNewVersion(downloadurl);
                }

                dialog.dismiss();
            }
        });
        builder.setNegativeButton("取消", null);
        builder.create().show();
    }

    private void downloadNewVersion(final String downloadurl) {
        final ProgressDialog pd = new ProgressDialog(mcontext);
        pd.setTitle("更新进度");
        pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        pd.show();

        new Thread(new Runnable() {
            InputStream is;
            BufferedInputStream bis;
            FileOutputStream fos;

            @Override
            public void run() {
                //这里完成下载
                try {
                    URL downloadUrl = new URL(downloadurl);
                    HttpURLConnection connection = (HttpURLConnection) downloadUrl.openConnection();
                    connection.setConnectTimeout(8000);
                    connection.setReadTimeout(8000);

                    int maxlength = connection.getContentLength();
                    pd.setMax(maxlength);

                    is = connection.getInputStream();
                    bis = new BufferedInputStream(is);

                    File file = new File(Environment.getExternalStorageDirectory(), "storesystem-release.apk");
                    fos = new FileOutputStream(file);
                    byte[] buffer = new byte[1024];
                    int len;
                    int loaded = 0;

                    while ((len = bis.read(buffer)) != -1) {
                        fos.write(buffer, 0, len);
                        loaded += len;
                        pd.setProgress(loaded);
                    }

                    installApk(file);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        is.close();
                        bis.close();
                        if (fos != null) {
                            fos.close();
                        }
                        pd.dismiss();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            }
        }).start();

    }

    private void installApk(File file) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
        mcontext.startActivity(intent);
    }

    private int getLocalVersionCode() throws PackageManager.NameNotFoundException {
        PackageManager pm = mcontext.getPackageManager();
        PackageInfo info = pm.getPackageInfo(mcontext.getPackageName(), 0);
        int versionCode = info.versionCode;
        return versionCode;
    }

    private void ClickLogout() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mcontext);
        builder.setTitle("提示");
        builder.setMessage("确认要退出登录吗?");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                LogoutAPI();
            }
        });
        builder.setNegativeButton("取消", null);

        AlertDialog alert = builder.create();
        alert.show();

    }

    private void LogoutAPI() {
        Session.CheckRefreshToken(new RefreshTokenListener() {
            @Override
            public void RefreshTokenResult(final int resultcode) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (resultcode == Codes.Code_Success) {
                            SCSDK.getInstance().Logout(Session.getInstance().getShopCode(), Session.getInstance().getCurrentUsername(), Session.getInstance().getToken(), Session.getInstance().getDeviceid(),Session.getInstance().getRegID(), new SCResponseListener() {
                                @Override
                                public void onResult(Object result) {

                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                SyncDataHelper syncdataHelper = new SyncDataHelper(SettingActivity.this);
                                                AccountModel accountModel = new AccountModel( Session.getInstance().getCurrentUsername(), Session.getInstance().getCurrentPwd(),Session.getInstance().getNickname(), Session.getInstance().getShopCode(), Session.getInstance().getShopname(), Constants.SERVER_IP, Constants.API_SERVER_PORT, false);
                                                syncdataHelper.InsertOrUpdataAccount(accountModel);
                                                Toast.makeText(mcontext, "已退出登录", Toast.LENGTH_SHORT).show();

                                                Session.clearSession();
                                                SharedPreferences sp = getSharedPreferences("storesystemdemo", MODE_PRIVATE);
                                                SharedPreferences.Editor ed = sp.edit();
                                                ed.putString("loginusername", "");
                                                ed.putString("loginpwd", "");
                                                ed.commit();

                                                startActivity(new Intent(mcontext, LoginActivity.class));
                                                finish();
                                            }
                                        });

                                }

                                @Override
                                public void onError(int code, final String errormsg) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                          //  Toast.makeText(mcontext, errormsg, Toast.LENGTH_SHORT).show();
                                            SyncDataHelper syncdataHelper = new SyncDataHelper(SettingActivity.this);
                                            AccountModel accountModel = new AccountModel( Session.getInstance().getCurrentUsername(), Session.getInstance().getCurrentPwd(),Session.getInstance().getNickname(), Session.getInstance().getShopCode(), Session.getInstance().getShopname(), Constants.SERVER_IP, Constants.API_SERVER_PORT, false);
                                            syncdataHelper.InsertOrUpdataAccount(accountModel);
                                            Toast.makeText(mcontext, "已退出登录", Toast.LENGTH_SHORT).show();

                                            Session.clearSession();
                                            SharedPreferences sp = getSharedPreferences("storesystemdemo", MODE_PRIVATE);
                                            SharedPreferences.Editor ed = sp.edit();
                                            ed.putString("loginusername", "");
                                            ed.putString("loginpwd", "");
                                            ed.commit();

                                            startActivity(new Intent(mcontext, LoginActivity.class));
                                            finish();
                                        }
                                    });
                                }
                            });

                        } else {
                           // Toast.makeText(mcontext, SCExceptionCodeList.getExceptionMsg(resultcode), Toast.LENGTH_SHORT).show();
                            SyncDataHelper syncdataHelper = new SyncDataHelper(SettingActivity.this);
                            AccountModel accountModel = new AccountModel( Session.getInstance().getCurrentUsername(), Session.getInstance().getCurrentPwd(),Session.getInstance().getNickname(), Session.getInstance().getShopCode(), Session.getInstance().getShopname(), Constants.SERVER_IP, Constants.API_SERVER_PORT, false);
                            syncdataHelper.InsertOrUpdataAccount(accountModel);
                            Toast.makeText(mcontext, "已退出登录", Toast.LENGTH_SHORT).show();

                            Session.clearSession();
                            SharedPreferences sp = getSharedPreferences("storesystemdemo", MODE_PRIVATE);
                            SharedPreferences.Editor ed = sp.edit();
                            ed.putString("loginusername", "");
                            ed.putString("loginpwd", "");
                            ed.commit();

                            startActivity(new Intent(mcontext, LoginActivity.class));
                            finish();

                        }
                    }
                });
            }
        });
    }


}
