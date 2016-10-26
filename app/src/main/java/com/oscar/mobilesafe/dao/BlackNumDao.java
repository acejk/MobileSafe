package com.oscar.mobilesafe.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.oscar.mobilesafe.db.BlackNumOpenHelper;
import com.oscar.mobilesafe.model.BlackNumInfo;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Administrator on 2016/10/14 0014.
 */
public class BlackNumDao {
    private BlackNumOpenHelper mBlackNumOpenHelper;

    private static BlackNumDao mBlackNumDao;
    //私有构造方法
    private BlackNumDao(Context context) {
        mBlackNumOpenHelper = new BlackNumOpenHelper(context);
    }
    //提供一个静态方法,如果当前类的对象为空,创建一个新的
    public static BlackNumDao getInstance(Context context) {
        if(mBlackNumDao == null) {
            mBlackNumDao = new BlackNumDao(context);
        }
        return mBlackNumDao;
    }

    /**
     * 增加
     * @param phone
     * @param mode
     */
    public void insert(String phone, String mode) {
        SQLiteDatabase db = mBlackNumOpenHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("phone", phone);
        values.put("mode", mode);
        db.insert("blacknum", null, values);

        db.close();
    }

    /**
     * 删除
     * @param phone
     */
    public void delete(String phone) {
        SQLiteDatabase db = mBlackNumOpenHelper.getWritableDatabase();
        db.delete("blacknum", "phone = ?", new String[]{phone});

        db.close();
    }

    /**
     * 修改
     * @param phone
     * @param mode
     */
    public void update(String phone, String mode) {
        SQLiteDatabase db = mBlackNumOpenHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("mode", mode);
        db.update("blacknum", values, "phone = ?", new String[]{phone});

        db.close();
    }

    /**
     * 查询所有
     * @return
     */
    public List<BlackNumInfo> findAll() {
        SQLiteDatabase db = mBlackNumOpenHelper.getWritableDatabase();
        Cursor cursor = db.query("blacknum", new String[]{"phone", "mode"}, null, null, null, null, "_id desc");
        List<BlackNumInfo> blackNumInfos = new ArrayList<>();
        while(cursor.moveToNext()) {
            BlackNumInfo blackNumInfo = new BlackNumInfo();
            blackNumInfo.setPhone(cursor.getString(cursor.getColumnIndex("phone")));
            blackNumInfo.setMode(cursor.getString(cursor.getColumnIndex("mode")));
            blackNumInfos.add(blackNumInfo);
        }
        cursor.close();
        db.close();

        return blackNumInfos;
    }

    /**
     * 分页查询
     * @return
     */
    public List<BlackNumInfo> findByIndex(int index) {
        SQLiteDatabase db = mBlackNumOpenHelper.getWritableDatabase();
        Cursor cursor = db.rawQuery("select phone, mode from blacknum order by _id desc limit ?, 20", new String[]{index + ""});
        List<BlackNumInfo> blackNumInfos = new ArrayList<>();
        while(cursor.moveToNext()) {
            BlackNumInfo blackNumInfo = new BlackNumInfo();
            blackNumInfo.setPhone(cursor.getString(cursor.getColumnIndex("phone")));
            blackNumInfo.setMode(cursor.getString(cursor.getColumnIndex("mode")));
            blackNumInfos.add(blackNumInfo);
        }
        cursor.close();
        db.close();

        return blackNumInfos;
    }

    /**
     * 数据库中总记录数
     * @return
     */
    public int getCount() {
        SQLiteDatabase db = mBlackNumOpenHelper.getWritableDatabase();
        int count = 0;
        Cursor cursor = db.rawQuery("select count(*) from blacknum", null);
        if(cursor.moveToNext()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        db.close();

        return count;
    }

    /**
     * 获取拦截类型
     * @param phone
     * @return 拦截类型
     */
    public int getMode(String phone) {
        SQLiteDatabase db = mBlackNumOpenHelper.getWritableDatabase();
        int mode = 0;
        Cursor cursor = db.query("blacknum", new String[]{"mode"}, "phone = ?", new String[]{phone}, null, null, null);
        if(cursor.moveToNext()) {
            mode = cursor.getInt(cursor.getColumnIndex("mode"));
        }
        cursor.close();
        db.close();

        return mode;
    }

}

