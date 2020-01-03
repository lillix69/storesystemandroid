package qinyuanliu.storesystemandroid.model;

/**
 * Created by qinyuanliu on 2019/2/17.
 */

public class AccountModel {
    public AccountModel(String account, String pwd, String nickname, String scode, String sname, String ip, int port, boolean isSelected){
        loginaccount = account;
        loginpwd = pwd;
        nick = nickname;
        storecode = scode;
        storename = sname;
        this.ip = ip;
        this.port = port;
        this.isSelected = isSelected;

    }
    public String loginaccount = "";
    public String loginpwd = "";
    public String nick = "";
    public String storecode = "";
    public String storename = "";
    public String ip = "";
    public int port = 0;
    public boolean isSelected = false;
}
