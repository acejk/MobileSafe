package com.oscar.mobilesafe.engine;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/9/22 0022.
 */
public class AnitVirusDao {
    private static final String DB_FILE_PATH = "data/data/com.oscar.mobilesafe/files/antivirus.db";

    public static List<String> getAnitVirus() {
        SQLiteDatabase db = SQLiteDatabase.openDatabase(DB_FILE_PATH, null, SQLiteDatabase.OPEN_READONLY);
        Cursor cursor = db.query("datable", new String[]{"md5"}, null, null, null, null, null);
        List<String> anitVirusList = new ArrayList<String>();
        while(cursor.moveToNext()) {
            String md5 = cursor.getString(cursor.getColumnIndex("md5"));
            anitVirusList.add(md5);
        }

        db.close();
        return anitVirusList;
    }
}
