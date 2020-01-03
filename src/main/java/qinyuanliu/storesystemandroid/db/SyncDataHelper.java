package qinyuanliu.storesystemandroid.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Base64;

import java.util.ArrayList;

import qinyuanliu.storesystemandroid.model.AccountModel;

/**
 * Created by qinyuanliu on 2019/2/17.
 */

public class SyncDataHelper {
    private MyDatabaseHelper dbHelper;
    private SQLiteDatabase db;

    public SyncDataHelper(Context context) {
        dbHelper = new MyDatabaseHelper(context, "accountdata.db", null, 1);

    }

    //accountsetting页面用
public ArrayList<AccountModel> queryAccountlist(String currentaccount, String currentstorecode, int portnum){
    ArrayList<AccountModel> queryarray = new ArrayList<>();
    String sql = "select * from localaccountdata";
    db = dbHelper.getWritableDatabase();
    Cursor cursor = db.rawQuery(sql,null);
    if (cursor.moveToFirst())// 判断Cursor中是否有数据
    {
        do {
            String accountdata = cursor.getString(cursor.getColumnIndex("loginaccount"));
            String pwddata = cursor.getString(cursor.getColumnIndex("loginpwd"));
            String storecodedata = cursor.getString(cursor.getColumnIndex("storecode"));
            String storenamedata = cursor.getString(cursor.getColumnIndex("storename"));
            String nickdata = cursor.getString(cursor.getColumnIndex("nickname"));
            String ipdata = cursor.getString(cursor.getColumnIndex("ip"));
            int portdata = cursor.getInt(cursor.getColumnIndex("port"));
            boolean isselect = false;
            if(accountdata.equals(currentaccount) && storecodedata.equals(currentstorecode) && (portdata == portnum)){
                isselect = true;
            }
            if(!accountdata.equals("") && !pwddata.equals("")) {
                AccountModel tempaccount = new AccountModel(accountdata, pwddata, nickdata, storecodedata, storenamedata, ipdata, portdata, isselect);
                queryarray.add(tempaccount);
            }

        }while (cursor.moveToNext());
    }

    cursor.close();
    db.close();
    return queryarray;
}

//login成功后调用
public void InsertOrUpdataAccount(AccountModel updateaccount){
    db = dbHelper.getWritableDatabase();
    String querysql = "select * from localaccountdata where loginaccount=? and storecode=? and port=?";
    String[] selectionArgs = new String[]{updateaccount.loginaccount, updateaccount.storecode, Integer.toString(updateaccount.port)};
    Cursor cursor = db.rawQuery(querysql,selectionArgs);

    if (cursor != null && cursor.getCount() > 0) {
        //更新操作
        String updatesql = "update localaccountdata set  loginpwd=? , nickname=? , storename=? , ip=? , port=? where loginaccount=? and storecode=? and port=?";
        db.execSQL(updatesql, new Object[]{updateaccount.loginpwd, updateaccount.nick, updateaccount.storename, updateaccount.ip, updateaccount.port, updateaccount.loginaccount, updateaccount.storecode, updateaccount.port});
        cursor.close();
    }else {
        //插入操作
        String insertsql = "insert into localaccountdata(loginaccount,storecode, loginpwd, nickname, storename, ip, port) values (?,?,?,?,?,?,?)";
        db.execSQL(insertsql, new Object[]{updateaccount.loginaccount, updateaccount.storecode, updateaccount.loginpwd, updateaccount.nick, updateaccount.storename, updateaccount.ip, updateaccount.port});

    }
    db.close();
}

//打开checksn页面调用，清空之前序列号下的账户信息
    public void DeleteAccount(){
        db = dbHelper.getWritableDatabase();
        db.execSQL("delete from localaccountdata");
        db.close();
    }

public AccountModel GetAccount(String account, String storecode, int portnum){
    AccountModel queryaccount = new AccountModel("","","","","","",0,false);
    String sql = "select * from localaccountdata where loginaccount=? and storecode=? and port=?";
    String[] selectionArgs = new String[]{account, storecode, Integer.toString(portnum)};

    db = dbHelper.getWritableDatabase();
    Cursor cursor = db.rawQuery(sql,selectionArgs);
    if (cursor.moveToFirst())// 判断Cursor中是否有数据
    {
        do {
            queryaccount.loginaccount = cursor.getString(cursor.getColumnIndex("loginaccount"));
            queryaccount.loginpwd = cursor.getString(cursor.getColumnIndex("loginpwd"));
            queryaccount.storecode = cursor.getString(cursor.getColumnIndex("storecode"));
            queryaccount.storename = cursor.getString(cursor.getColumnIndex("storename"));
            queryaccount.nick = cursor.getString(cursor.getColumnIndex("nickname"));
            queryaccount.ip = cursor.getString(cursor.getColumnIndex("ip"));
            queryaccount.port = cursor.getInt(cursor.getColumnIndex("port"));

        }while (cursor.moveToNext());
    }

    cursor.close();
    db.close();
    return queryaccount;
}

}
