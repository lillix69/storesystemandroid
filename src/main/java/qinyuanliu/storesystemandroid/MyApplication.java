package qinyuanliu.storesystemandroid;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.os.Process;
import android.os.StrictMode;
import android.util.Log;

import com.xiaomi.channel.commonutils.logger.LoggerInterface;
import com.xiaomi.mipush.sdk.MiPushClient;

import java.util.List;

import qinyuanliu.storesystemandroid.activity.FragmentMain;
import qinyuanliu.storesystemandroid.activity.LaunchActivity;
import qinyuanliu.storesystemandroid.activity.MainActivity;
import qinyuanliu.storesystemandroid.activity.NoticeActivity;
import qinyuanliu.storesystemandroid.activity.TabbarActivity;
import qinyuanliu.storesystemandroid.common.Session;
import qinyuanliu.storesystemandroid.util.screenlock.HomeWatcher;

/**
 * Created by lillix on 4/18/18.
 */
public class MyApplication extends Application {

    // 使用自己APP的ID（官网注册的）
    private static final String APP_ID = "2882303761517894139";
    // 使用自己APP的KEY（官网注册的）
    private static final String APP_KEY = "5401789443139";
    public static final String TAG = "com.xiaomi.mipushdemo";

    private static DemoHandler sHandler = null;
    private static FragmentMain sMainActivity = null;

    //监听器
    private HomeWatcher homeWatcher;

    @Override
    public void onCreate() {
        super.onCreate();

        //注册按键广播监听
        initBroadcard();


        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        if (Build.VERSION.SDK_INT >= 18) {

            builder.detectFileUriExposure();
        }
        StrictMode.setVmPolicy(builder.build());


        //判断用户是否已经打开App，详细见下面方法定义
        if (shouldInit()) {
            //注册推送服务
            //注册成功后会向DemoMessageReceiver发送广播
            // 可以从DemoMessageReceiver的onCommandResult方法中MiPushCommandMessage对象参数中获取注册信息
            MiPushClient.registerPush(this, APP_ID, APP_KEY);
            //参数说明
            //context：Android平台上app的上下文，建议传入当前app的application context
            //appID：在开发者网站上注册时生成的，MiPush推送服务颁发给app的唯一认证标识
            //appKey:在开发者网站上注册时生成的，与appID相对应，用于验证appID是否合法
        }


        //下面是与测试相关的日志设置
        LoggerInterface newLogger = new LoggerInterface() {

            @Override
            public void setTag(String tag) {
                // ignore
            }

            @Override
            public void log(String content, Throwable t) {
                Log.d(TAG, content, t);
            }

            @Override
            public void log(String content) {
                Log.d(TAG, content);
            }
        };
        com.xiaomi.mipush.sdk.Logger.setLogger(this, newLogger);
        if (sHandler == null) {
            sHandler = new DemoHandler(getApplicationContext());
        }
    }


    //通过判断手机里的所有进程是否有这个App的进程
//从而判断该App是否有打开
    private boolean shouldInit() {
//通过ActivityManager我们可以获得系统里正在运行的activities
//包括进程(Process)等、应用程序/包、服务(Service)、任务(Task)信息。
        ActivityManager am = ((ActivityManager) getSystemService(Context.ACTIVITY_SERVICE));
        List<ActivityManager.RunningAppProcessInfo> processInfos = am.getRunningAppProcesses();
        String mainProcessName = getPackageName();

        //获取本App的唯一标识
        int myPid = Process.myPid();
        //利用一个增强for循环取出手机里的所有进程
        for (ActivityManager.RunningAppProcessInfo info : processInfos) {
            //通过比较进程的唯一标识和包名判断进程里是否存在该App
            if (info.pid == myPid && mainProcessName.equals(info.processName)) {
                return true;
            }
        }
        return false;
    }

    public static DemoHandler getHandler() {
        return sHandler;
    }

    public static void setMainActivity(FragmentMain activity) {
        sMainActivity = activity;
    }


    //通过设置Handler来设置提示文案
    public static class DemoHandler extends Handler {

        private Context context;

        public DemoHandler(Context context) {
            this.context = context;
        }

        @Override
        public void handleMessage(Message msg) {
            String s = (String) msg.obj;
            if (sMainActivity != null) {
                sMainActivity.showdot();
            }

        }
    }

    /**
     * 初始化广播
     */
    private void initBroadcard(){
        homeWatcher = new HomeWatcher( this);
        homeWatcher.startWatch();
    }
    /**
     * BaseActivity的onResume里面设置监听
     * @param activity 当前所在的activity
     */
    public void setListener(Activity activity){
        homeWatcher.setOnHomePressedListener((HomeWatcher.OnHomePressedListener) activity);
    }

}