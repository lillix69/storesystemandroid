package qinyuanliu.storesystemandroid.util;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import qinyuanliu.storesystemandroid.common.Session;
import qinyuanliu.storesystemandroid.http.SCResponseListener;
import qinyuanliu.storesystemandroid.http.SCResult;
import qinyuanliu.storesystemandroid.http.SCSDK;

/**
 * Created by qinyuanliu on 2018/7/24.
 */

public class UpdateManager implements Runnable{
    private static final int HAVE_NEW_VERSION = 0;
 //   private static final int ALREADY_NEW_VERSION = 1;

    private SCResult.VersionResult updateInfo = null;

    //获取到主线程的looper,对UI操作
    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case HAVE_NEW_VERSION:
                    openUpdateDialog();
                    break;
//                case ALREADY_NEW_VERSION:
//                    Toast.makeText(context, "已经是最新版本", Toast.LENGTH_LONG).show();
//                    break;
            }
        }
    };

    private void openUpdateDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(Session.getInstance().getContext());
        builder.setTitle("版本有更新");
        builder.setMessage("是否立刻更新您的应用？");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                downloadNewVersion();
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("取消", null);
        builder.create().show();
    }

    private void downloadNewVersion() {
        final ProgressDialog pd = new ProgressDialog(Session.getInstance().getContext());
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
                    URL downloadUrl = new URL(updateInfo.downloadurl);
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
                }

                finally {
                    try {
                            is.close();
                            bis.close();
                        if(fos != null) {
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
        Session.getInstance().getContext().startActivity(intent);
    }

    @Override
    public void run() {
       final SharedPreferences sp = Session.getInstance().getContext().getSharedPreferences("storesystemdemo", Context.MODE_PRIVATE);
        String timestamp = sp.getString("LastCheckUpdateTime", "");
       final String currenttime = getCurrentDate();
        if (currenttime.compareTo(timestamp)>0) {
            SCSDK.getInstance().GetVersion(Session.getInstance().getShopCode(), new SCResponseListener() {
                @Override
                public void onResult(Object result) {
                    try {
                        //每天只检查一次,记录date
                        SharedPreferences.Editor ed = sp.edit();
                        ed.putString("LastCheckUpdateTime", currenttime);
                        ed.commit();
                        updateInfo = (SCResult.VersionResult) result;
                        //后台版本
                        int serverVersion = updateInfo.version;
                        //本地版本获取
                        int localVersionCode = getLocalVersionCode();
                        //后台版本新！弹框提醒用户有新版本，让用户操作dialog更新
                        if (serverVersion > localVersionCode) {
                            Message msg = new Message();
                            msg.what = HAVE_NEW_VERSION;
                            mHandler.sendMessage(msg);

                        }
//                    else {
//                        //后台没有新版本，所以在界面反馈用户不用更新
//                        Message msg = new Message();
//                        msg.what = ALREADY_NEW_VERSION;
//                        mHandler.sendMessage(msg);
//                    }
                    } catch (PackageManager.NameNotFoundException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onError(int code, String errormsg) {
System.out.print(code);
                }
            });
        }
    }

    private int getLocalVersionCode() throws PackageManager.NameNotFoundException {
        PackageManager pm = Session.getInstance().getContext().getPackageManager();
        PackageInfo info = pm.getPackageInfo(Session.getInstance().getContext().getPackageName(), 0);
        int versionCode = info.versionCode;
        return versionCode;
    }

    private String getCurrentDate(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String s = sdf.format(new Date());
        return s;
    }
}