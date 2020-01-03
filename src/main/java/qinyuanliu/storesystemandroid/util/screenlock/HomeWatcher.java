package qinyuanliu.storesystemandroid.util.screenlock;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import qinyuanliu.storesystemandroid.common.Session;

/**
 * Created by qinyuanliu on 2019/3/4.
 */

public class HomeWatcher {
    static final String TAG = HomeWatcher.class.getClass().getSimpleName();
    private Context mContext;
    private IntentFilter mFilter;
    private OnHomePressedListener mListener;
    /**
     * 广播接收者
     */
    private InnerRecevier mRecevier;

    // 回调接口
    public interface OnHomePressedListener {

        /**
         * 短按Home按键
         */
        void onHomeClick();

        /**
         * 长按Home 或 切换程序
         */
        void onHomeLongClick();

        /**
         * 锁屏
         */
        void onLockScreen();

    }

    public HomeWatcher(Context context) {
        mContext = context;
        mFilter = new IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
        mFilter.addAction("android.intent.action.SCREEN_OFF");
        mFilter.addAction("android.intent.action.MAIN");
        mRecevier = new InnerRecevier();
    }
    /**
     * 设置监听
     *
     * @param listener
     */
    public void setOnHomePressedListener(OnHomePressedListener listener) {
        mListener = listener;

    }

    /**
     * 开始监听，注册广播
     */
    public void startWatch() {
        if (mRecevier != null) {
            mContext.registerReceiver(mRecevier, mFilter);
        }
    }

    /**
     * 停止监听，注销广播
     */
    public void stopWatch() {
        if (mRecevier != null) {
            mContext.unregisterReceiver(mRecevier);
        }
    }

    /**
     * 广播接收者
     */
    class InnerRecevier extends BroadcastReceiver {
        final String SYSTEM_DIALOG_REASON_KEY = "reason";
        final String SYSTEM_DIALOG_REASON_RECENT_APPS = "recentapps";
        final String SYSTEM_DIALOG_REASON_HOME_KEY = "homekey";
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            switch (action){
                case Intent.ACTION_MAIN:
                    if(mListener != null && context != null) {
                        mListener.onLockScreen();
                        System.out.println("========main锁屏");
                    }
                    break;
                case Intent.ACTION_SCREEN_OFF:// 锁屏
                    if(mListener != null && context != null) {
                        mListener.onLockScreen();
                        System.out.println("========screenoff锁屏");
                    }
                    break;

                case Intent.ACTION_CLOSE_SYSTEM_DIALOGS://home键监测
                    /*
                    * 这里监听了手机系统按下home键的那一刻事件，
                    * 如果想再处理再次回到app应用的事物，请参考home键及应用重新启动的过程生命周期，根据实际需求进行相关的操作的。
                    */
                    if (action.equals(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)) {   //窗口关闭广播
                        String reason = intent.getStringExtra(SYSTEM_DIALOG_REASON_KEY);
                        if (reason != null) {
                            //LogUtils.e("action:" + action + ",reason:" + reason);
                            if (mListener != null && context != null) {
                                if (SYSTEM_DIALOG_REASON_HOME_KEY.equals(reason)) {
                                    // 短按home键
                                    if(Session.getInstance().getIsLogin()) {
                                        mListener.onHomeClick();
                                        System.out.println("========短按Home键 ");
                                    }
                                }
//                                else if (SYSTEM_DIALOG_REASON_RECENT_APPS.equals(reason)) {
//                                    // 长按Home键 或者 activity切换键
//                                    mListener.onHomeLongClick();
//                                    System.out.println("======长按Home键 或者 activity切换键");
//                                }
                                    // else if (SYSTEM_DIALOG_REASON_LOCK.equals(reason)) {
//                                    // 锁屏
//                                    mListener.onLockScreen();
                          //      }
                               }
                           }
                        }
                    break;
                default:
                    break;
            }


        }
    }
}

