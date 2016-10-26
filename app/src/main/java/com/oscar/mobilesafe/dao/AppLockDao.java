package com.oscar.mobilesafe.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.oscar.mobilesafe.db.AppLockOpenHelper;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Administrator on 2016/10/14 0014.
 */
public class AppLockDao {
    private AppLockOpenHelper mAppLockOpenHelper;

    private static AppLockDao mAppLockDao;
    //私有构造方法
    private AppLockDao(Context context) {
        mAppLockOpenHelper = new AppLockOpenHelper(context);
    }
    //提供一个静态方法,如果当前类的对象为空,创建一个新的
    public static AppLockDao getInstance(Context context) {
        if(mAppLockDao == null) {
            mAppLockDao = new AppLockDao(context);
        }
        return mAppLockDao;
    }

    /**
     * 增加
     */

    public void insert(String packagename) {
        SQLiteDatabase db = mAppLockOpenHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("packagename", packagename);
        db.insert("applock", null, values);
        db.close();
    }

    /**
     * 删除
     */
    public void delete(String packagename) {
        SQLiteDatabase db = mAppLockOpenHelper.getWritableDatabase();
        db.delete("applock", "packagename = ?", new String[]{packagename});
        db.close();
    }

    /**
     * 查询所有
     */
    public List<String> findAll() {
        SQLiteDatabase db = mAppLockOpenHelper.getWritableDatabase();
        Cursor cursor = db.query("applock", new String[]{"packagename"}, null, null, null, null, null);
        List<String> packagenames = new ArrayList<String>();
        while(cursor.moveToNext()) {
            String packagename = cursor.getString(cursor.getColumnIndex("packagename"));
            packagenames.add(packagename);
        }
        cursor.close();
        db.close();
        return packagenames;
    }



}

