package qinyuanliu.storesystemandroid.activity;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import qinyuanliu.storesystemandroid.MyApplication;
import qinyuanliu.storesystemandroid.common.Session;
import qinyuanliu.storesystemandroid.util.screenlock.HomeWatcher;
import qinyuanliu.storesystemandroid.util.screenlock.PassDialog;

/**
 * Created by qinyuanliu on 2019/3/4.
 */

public abstract class BaseActivity extends FragmentActivity implements HomeWatcher.OnHomePressedListener  {
    public MyApplication mApplication;

    private PassDialog passDialog;


//    private enum Step{
//        HIDE,SHOW
//    }
//
//    Step step = Step.HIDE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

       // setContentView(setView());

        mApplication = getBaseApplication();

        initPassDialog();

       // initListener();


    }
   // protected abstract void initListener();

   // public abstract int setView();

    /**
     * 获取Application
     * @return
     */
    public MyApplication getBaseApplication() {
        if (null == mApplication) {
            mApplication = (MyApplication) getApplication();
        }
        return mApplication;
    }

    /**
     * 初始化密码框
     */
    protected void initPassDialog(){
        passDialog = new PassDialog(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        getBaseApplication().setListener(this);
        Session.getInstance().setIsbackrun(false);

    }


    @Override
    protected void onStop() {
        super.onStop();
        if(Session.getInstance().getIsbacrun()) {
            if (Session.getInstance().getLockFlag() && !Session.getInstance().getIsLocked()) {
                    passDialog.show();
                Session.getInstance().setIsLocked(true);
                }
            }
    }

    @Override
    protected void onRestart() {
        super.onRestart();

    }
    @Override
    public void onHomeClick() {
        if( Session.getInstance().getLockFlag() && !Session.getInstance().getIsLocked()) {
            passDialog.show();
            Session.getInstance().setIsLocked(true);
        }

    }

    @Override
    public void onHomeLongClick() {
        if(Session.getInstance().getLockFlag() && !Session.getInstance().getIsLocked()) {
            passDialog.show();
            Session.getInstance().setIsLocked(true);
        }
    }

    @Override
    public void onLockScreen() {
        if(Session.getInstance().getLockFlag() && !Session.getInstance().getIsLocked()) {
            passDialog.show();
            Session.getInstance().setIsLocked(true);
        }
    }


}
