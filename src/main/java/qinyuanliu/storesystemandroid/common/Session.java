package qinyuanliu.storesystemandroid.common;

import android.content.Context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import qinyuanliu.storesystemandroid.http.SCResponseListener;
import qinyuanliu.storesystemandroid.http.SCResult;
import qinyuanliu.storesystemandroid.http.SCSDK;
import qinyuanliu.storesystemandroid.listener.RefreshTokenListener;

/**
 * Created by lillix on 5/28/18.
 */
public class Session {
    private static volatile Session instance = null;

    private Session() {

    }

    public static Session getInstance() {
        if (instance == null) {
            synchronized (Session.class) {
                if (instance == null) {
                    instance = new Session();
                }
            }
        }
        return instance;
    }

    public static void clearSession() {
        if (instance != null) {
            instance.isLogin = false;
            instance.CurrentUsername = "";
            instance.CurrentPwd = "";
            instance.nickname = "";
            instance.shopName = "";
            instance.shopCode = "";
            instance.token = "";
            instance.lockFlag = false;
        }
    }
    public static void CheckRefreshToken(final RefreshTokenListener listener) {
        if (instance.getIsLogin()) {
            //1200s refresh token
            if (System.currentTimeMillis() - instance.getLastTokenTimestamp() > 1200000) {
                SCSDK.getInstance().Login(Session.getInstance().getShopCode(),instance.getCurrentUsername(), instance.getCurrentPwd(), instance.getDeviceid(), new SCResponseListener() {
                    @Override
                    public void onResult(Object result) {
                        SCResult.LoginAccountResult res = (SCResult.LoginAccountResult)result;
                        instance.setLastTokenTimestamp(System.currentTimeMillis());
                        instance.setToken(res.token);
                        listener.RefreshTokenResult(200);
                    }

                    @Override
                    public void onError(int code, String errormsg) {
                        listener.RefreshTokenResult(code);
                    }
                });
            }
            else{
                listener.RefreshTokenResult(200);
            }
        }
        else{
            listener.RefreshTokenResult(200);
        }
    }

    private ArrayList<SCResult.CheckShop> shops = new ArrayList<>();
    public ArrayList<SCResult.CheckShop> getShops(){return shops;}
    public void setShops(ArrayList<SCResult.CheckShop> tempshops){shops = tempshops;}

    private boolean isbackrun = false;
    public void setIsbackrun(boolean run){isbackrun = run;}
    public boolean getIsbacrun(){return isbackrun;}

    private Context context;
    public void setContext(Context c){context = c;}
public Context getContext(){return context;}

    private boolean isLogin = false;
    public boolean getIsLogin() {
        return isLogin;
    }
    public void setIsLogin(boolean flag) {
        isLogin = flag;
    }

    private boolean fastmodeFlag = false;
    public boolean getFastmodeFlag(){return fastmodeFlag;}
    public void setFastmodeFlag(boolean flag){fastmodeFlag = flag;}

    //是否开启了锁屏功能
    private boolean lockFlag = true;
    public boolean getLockFlag(){return lockFlag;}
    public void setLockFlag(boolean flag){lockFlag = flag;}

    //是否已经显示了锁屏
    private boolean isLocked = false;
    public boolean getIsLocked(){return isLocked;}
    public void setIsLocked(boolean lock){isLocked = lock;}

    private String token = "";
    public String getToken(){
        return token;}
    public void setToken(String t){token = t;}

private String loginSN = "";
    public String getLoginSN(){return loginSN;}
    public void setLoginSN(String sn){loginSN = sn;}

    private String CurrentUsername = "";
    public String getCurrentUsername() {
        return CurrentUsername;
    }
    public void setCurrentUsername(String currentusername) {
        CurrentUsername = currentusername;
    }

    private String CurrentPwd = "";
    public String getCurrentPwd() {
        return CurrentPwd;
    }
    public void setCurrentPwd(String currentpwd) {
        CurrentPwd = currentpwd;
    }

    private String shopName = "";
    public String getShopname(){return shopName;}
    public void setShopname(String shopname){shopName = shopname;}

    private String nickname = "";
    public String getNickname() {
        return nickname;
    }
    public void setNickname(String name) {
        nickname = name;
    }

    private String deviceid = "";
    public String getDeviceid(){
        return deviceid;}
    public void setDeviceid(String id){deviceid = id;}

    private String shopCode= "";
    public String getShopCode(){
        return shopCode;}
    public void setShopCode(String code){shopCode = code;}

    private  long LastTokenTimestamp = 0;
    public long getLastTokenTimestamp(){return LastTokenTimestamp;}
    public void setLastTokenTimestamp(long timestamp){LastTokenTimestamp = timestamp;}

    private String regID = "";
    public String getRegID(){return regID;}
    public void setRegID(String id){regID = id;}

    private SCResult.SubmitPrepareResult submitprepare;
    public void setSubmitprepare(SCResult.SubmitPrepareResult submit){submitprepare = submit;}
    public SCResult.SubmitPrepareResult getSubmitprepare(){return submitprepare;}

    private HashMap<String,HashSet<String>> scansnsMap = new HashMap<>();
    public void setScansnsMap(HashMap<String,HashSet<String>> map){scansnsMap = map;}
    public HashMap<String,HashSet<String>> getScansnsMap(){return scansnsMap;}

    private int menupower = -1;//0全部权限  1只有首页权限  2只有首页和采购入库  3 只有首页和销售出库
    public void setMenupower(int p){menupower = p;}
    public int getMenupower(){return menupower;}
}
