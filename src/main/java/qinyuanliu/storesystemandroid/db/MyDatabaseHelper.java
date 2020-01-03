package qinyuanliu.storesystemandroid.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by qinyuanliu on 2019/2/17.
 */

public class MyDatabaseHelper extends SQLiteOpenHelper {

    private static final String CREATE_TABLE = "create table IF NOT EXISTS localaccountdata (loginaccount text,storecode text, loginpwd text,nickname text,  storename text, ip text, port integer, PRIMARY KEY (loginaccount, storecode, port));";
    private Context mContext;

    public MyDatabaseHelper(Context context, String name,
                            SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // db.execSQL("drop table if exists equipmentsyncdata");
        onCreate(db);
    }

}
