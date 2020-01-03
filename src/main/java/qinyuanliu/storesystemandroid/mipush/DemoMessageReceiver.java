package qinyuanliu.storesystemandroid.mipush;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.xiaomi.mipush.sdk.ErrorCode;
import com.xiaomi.mipush.sdk.MiPushClient;
import com.xiaomi.mipush.sdk.MiPushCommandMessage;
import com.xiaomi.mipush.sdk.MiPushMessage;
import com.xiaomi.mipush.sdk.PushMessageReceiver;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import qinyuanliu.storesystemandroid.MyApplication;
import qinyuanliu.storesystemandroid.R;
import qinyuanliu.storesystemandroid.activity.LaunchActivity;
import qinyuanliu.storesystemandroid.activity.NoticeActivity;
import qinyuanliu.storesystemandroid.common.Session;

/**
 * 1、PushMessageReceiver 是个抽象类，该类继承了 BroadcastReceiver。<br/>
 * 2、需要将自定义的 DemoMessageReceiver 注册在 AndroidManifest.xml 文件中：
 * <pre>
 * {@code
 *  <receiver
 *      android:name="com.xiaomi.mipushdemo.DemoMessageReceiver"
 *      android:exported="true">
 *      <intent-filter>
 *          <action android:name="com.xiaomi.mipush.RECEIVE_MESSAGE" />
 *      </intent-filter>
 *      <intent-filter>
 *          <action android:name="com.xiaomi.mipush.MESSAGE_ARRIVED" />
 *      </intent-filter>
 *      <intent-filter>
 *          <action android:name="com.xiaomi.mipush.ERROR" />
 *      </intent-filter>
 *  </receiver>
 *  }</pre>
 * 3、DemoMessageReceiver 的 onReceivePassThroughMessage 方法用来接收服务器向客户端发送的透传消息。<br/>
 * 4、DemoMessageReceiver 的 onNotificationMessageClicked 方法用来接收服务器向客户端发送的通知消息，
 * 这个回调方法会在用户手动点击通知后触发。<br/>
 * 5、DemoMessageReceiver 的 onNotificationMessageArrived 方法用来接收服务器向客户端发送的通知消息，
 * 这个回调方法是在通知消息到达客户端时触发。另外应用在前台时不弹出通知的通知消息到达客户端也会触发这个回调函数。<br/>
 * 6、DemoMessageReceiver 的 onCommandResult 方法用来接收客户端向服务器发送命令后的响应结果。<br/>
 * 7、DemoMessageReceiver 的 onReceiveRegisterResult 方法用来接收客户端向服务器发送注册命令后的响应结果。<br/>
 * 8、以上这些方法运行在非 UI 线程中。
 *
 * @author mayixiang
 */
public class DemoMessageReceiver extends PushMessageReceiver {
    //应用在获取到RegID后，需要将RegID上报到应用的服务器，此时应用服务端就可以利用RegID向客户端发送消息
    private String mRegId;

    //可以在不同设备上设置同一个userAccount(可设置为app的登录账号)
    private String mAccount;
    //一台设备可以订阅多个不同的标签，并且一个标签可以对应多台设备
    private String mTopic;

    //regId的别名,针对单一设备推送消息,不需要应用服务器保存客户端上传的RegID
    //一台设备可以设置多个不同的别名，而一个别名只能对应某一台设备。如果多台设备设置同一个别名，那么只有最后设置的一台设备生效
    private String mAlias;
    private String mStartTime;
    private String mEndTime;



    @Override
    public void onReceivePassThroughMessage(Context context, MiPushMessage message) {
        Log.v("mipush_onPassThrough",
                "onReceivePassThroughMessage is called. " + message.toString());


        if (!TextUtils.isEmpty(message.getTopic())) {
            mTopic = message.getTopic();
        } else if (!TextUtils.isEmpty(message.getAlias())) {
            mAlias = message.getAlias();
        }

    }

    @Override
    public void onNotificationMessageClicked(Context context, MiPushMessage message) {
        Log.v("mipush_onMessageClicked",
                "onNotificationMessageClicked is called. " + message.toString());

        if (!TextUtils.isEmpty(message.getTopic())) {
            mTopic = message.getTopic();
        } else if (!TextUtils.isEmpty(message.getAlias())) {
            mAlias = message.getAlias();
        }

        if(Session.getInstance().getIsLogin()) {
            Intent intent = new Intent(context.getApplicationContext(), NoticeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.getApplicationContext().startActivity(intent);
        }else
            {
                Intent intent = new Intent(context.getApplicationContext(), LaunchActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.getApplicationContext().startActivity(intent);

            }

    }

    @Override
    public void onNotificationMessageArrived(Context context, MiPushMessage message) {
        Log.v("mipush_onMessageArrived",
                "onNotificationMessageArrived is called. " + message.toString());

        if (!TextUtils.isEmpty(message.getTopic())) {
            mTopic = message.getTopic();
        }
        else if (!TextUtils.isEmpty(message.getAlias())) {
            mAlias = message.getAlias();
        }

        if(Session.getInstance().getIsLogin()){
            //通过listener到mainactivity上刷新dot
            Message msg = Message.obtain();
            msg.obj = message.getDescription();
            MyApplication.getHandler().sendMessage(msg);
        }

//        //获取消息内容,由app触发产生标题栏通知.点击后进入app
//        if(!TextUtils.isEmpty(message.getDescription())){
//            String desp = message.getDescription();
//            Message msg = Message.obtain();
//            msg.obj = desp;
//            MyApplication.getHandler().sendMessage(msg);
//        }

    }

    @Override
    public void onCommandResult(Context context, MiPushCommandMessage message) {
        Log.v("mipush",
                "onCommandResult is called. " + message.toString());

        String command = message.getCommand();
        List<String> arguments = message.getCommandArguments();
        String cmdArg1 = ((arguments != null && arguments.size() > 0) ? arguments.get(0) : null);
        String cmdArg2 = ((arguments != null && arguments.size() > 1) ? arguments.get(1) : null);
        String log;
        if (MiPushClient.COMMAND_REGISTER.equals(command)) {
            if (message.getResultCode() == ErrorCode.SUCCESS) {
                mRegId = cmdArg1;
                log = context.getString(R.string.register_success) + mRegId ;
                Session.getInstance().setRegID(mRegId);
            } else {
                log = context.getString(R.string.register_fail);
            }
        } else if (MiPushClient.COMMAND_SET_ALIAS.equals(command)) {
            if (message.getResultCode() == ErrorCode.SUCCESS) {
                mAlias = cmdArg1;
                log = context.getString(R.string.set_alias_success, mAlias);
            } else {
                log = context.getString(R.string.set_alias_fail, message.getReason());
            }
        } else if (MiPushClient.COMMAND_UNSET_ALIAS.equals(command)) {
            if (message.getResultCode() == ErrorCode.SUCCESS) {
                mAlias = cmdArg1;
                log = context.getString(R.string.unset_alias_success, mAlias);
            } else {
                log = context.getString(R.string.unset_alias_fail, message.getReason());
            }
        } else if (MiPushClient.COMMAND_SET_ACCOUNT.equals(command)) {
            if (message.getResultCode() == ErrorCode.SUCCESS) {
                mAccount = cmdArg1;
                log = context.getString(R.string.set_account_success, mAccount);
            } else {
                log = context.getString(R.string.set_account_fail, message.getReason());
            }
        } else if (MiPushClient.COMMAND_UNSET_ACCOUNT.equals(command)) {
            if (message.getResultCode() == ErrorCode.SUCCESS) {
                mAccount = cmdArg1;
                log = context.getString(R.string.unset_account_success, mAccount);
            } else {
                log = context.getString(R.string.unset_account_fail, message.getReason());
            }
        } else if (MiPushClient.COMMAND_SUBSCRIBE_TOPIC.equals(command)) {
            if (message.getResultCode() == ErrorCode.SUCCESS) {
                mTopic = cmdArg1;
                log = context.getString(R.string.subscribe_topic_success, mTopic);
            } else {
                log = context.getString(R.string.subscribe_topic_fail, message.getReason());
            }
        } else if (MiPushClient.COMMAND_UNSUBSCRIBE_TOPIC.equals(command)) {
            if (message.getResultCode() == ErrorCode.SUCCESS) {
                mTopic = cmdArg1;
                log = context.getString(R.string.unsubscribe_topic_success, mTopic);
            } else {
                log = context.getString(R.string.unsubscribe_topic_fail, message.getReason());
            }
        } else if (MiPushClient.COMMAND_SET_ACCEPT_TIME.equals(command)) {
            if (message.getResultCode() == ErrorCode.SUCCESS) {
                mStartTime = cmdArg1;
                mEndTime = cmdArg2;
                log = context.getString(R.string.set_accept_time_success, mStartTime, mEndTime);
            } else {
                log = context.getString(R.string.set_accept_time_fail, message.getReason());
            }
        } else {
            log = message.getReason();
        }
        Log.v("mipush_onCommand",log);
    }

    @Override
    public void onReceiveRegisterResult(Context context, MiPushCommandMessage message) {
        Log.v("mipush",
                "onReceiveRegisterResult is called. " + message.toString());

        String command = message.getCommand();
        List<String> arguments = message.getCommandArguments();
        String cmdArg1 = ((arguments != null && arguments.size() > 0) ? arguments.get(0) : null);
        String log;
        if (MiPushClient.COMMAND_REGISTER.equals(command)) {
            if (message.getResultCode() == ErrorCode.SUCCESS) {
                mRegId = cmdArg1;
                log = context.getString(R.string.register_success)  +  mRegId;
                Session.getInstance().setRegID(mRegId);
            } else {
                log = context.getString(R.string.register_fail);
            }
        } else {
            log = message.getReason();
        }
        Log.v("mipush_onRegister",log);


    }

    @SuppressLint("SimpleDateFormat")
    private static String getSimpleDate() {
        return new SimpleDateFormat("MM-dd hh:mm:ss").format(new Date());
    }


}
