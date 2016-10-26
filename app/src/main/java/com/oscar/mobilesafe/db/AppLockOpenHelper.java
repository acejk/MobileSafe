package com.oscar.mobilesafe.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Administrator on 2016/10/11 0011.
 */
public class AppLockOpenHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "appLock.db";

    private static final int VERSION = 1;

    public AppLockOpenHelper(Context context) {
        super(context, DB_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "create table applock (_id integer primary key autoincrement, packagename varchar(20))";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
