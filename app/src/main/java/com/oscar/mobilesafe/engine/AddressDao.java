package com.oscar.mobilesafe.engine;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * Created by Administrator on 2016/9/22 0022.
 */
public class AddressDao {
    private static final String DB_FILE_PATH = "data/data/com.oscar.mobilesafe/files/address.db";

    private static final String TAG = "AddressDao";

    private static String mAddress = "未知号码";


    public static String getAddress(String phone) {
        SQLiteDatabase db = null;
        mAddress = "未知号码";
        String regularExpression = "^1[3-8]\\d{9}";
        if (phone.matches(regularExpression)) {
            phone = phone.substring(0, 7);
            db = SQLiteDatabase.openDatabase(DB_FILE_PATH, null, SQLiteDatabase.OPEN_READONLY);
            Cursor cursor = db.query("data1", new String[]{"outkey"}, "id = ?", new String[]{phone}, null, null, null);
            if (cursor.moveToNext()) {
                String outkey = cursor.getString(cursor.getColumnIndex("outkey"));
                Log.i(TAG, "outkey = " + outkey);
                Cursor cursorIndex = db.query("data2", new String[]{"location"}, "id = ?", new String[]{outkey}, null, null, null);
                if (cursorIndex.moveToNext()) {
                    mAddress = cursorIndex.getString(cursorIndex.getColumnIndex("location"));
                    Log.i(TAG, "location = " + mAddress);
                }
            } else {
                mAddress = "未知号码";
            }
        } else {
            int length = phone.length();
            switch (length) {
                case 3:
                    mAddress = "报警电话";
                    break;
                case 4:
                    mAddress = "模拟器";
                    break;
                case 5:
                    mAddress = "服务电话";
                    break;
                case 7:
                    mAddress = "本地电话";
                    break;
                case 8:
                    mAddress = "本地电话";
                    break;
                case 11:
                    String area = phone.substring(1, 3);
                    Cursor cursor = db.query("data2", new String[]{"location"}, "area = ?", new String[]{area}, null, null, null);
                    if(cursor.moveToNext()) {
                        mAddress = cursor.getString(cursor.getColumnIndex("location"));
                    } else {
                        mAddress = "未知号码";
                    }
                    break;
                case 12:
                    String area1 = phone.substring(1, 4);
                    Cursor cursor1 = db.query("data2", new String[]{"location"}, "area = ?", new String[]{area1}, null, null, null);
                    if(cursor1.moveToNext()) {
                        mAddress = cursor1.getString(cursor1.getColumnIndex("location"));
                    } else {
                        mAddress = "未知号码";
                    }
                    break;
            }
        }
        return mAddress;
    }
}
