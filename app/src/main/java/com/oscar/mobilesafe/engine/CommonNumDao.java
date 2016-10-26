package com.oscar.mobilesafe.engine;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/9/22 0022.
 */
public class CommonNumDao {
    private static final String DB_FILE_PATH = "data/data/com.oscar.mobilesafe/files/commonnum.db";

    public List<Group> getGroup() {
        SQLiteDatabase db = SQLiteDatabase.openDatabase(DB_FILE_PATH, null, SQLiteDatabase.OPEN_READONLY);
        Cursor cursor = db.query("classlist", new String[]{"name", "idx"}, null, null, null, null, null);
        List<Group> groups = new ArrayList<>();
        while(cursor.moveToNext()) {
            Group group = new Group();
            group.name = cursor.getString(cursor.getColumnIndex("name"));
            group.idx = cursor.getString(cursor.getColumnIndex("idx"));
            group.childs = getChild(group.idx);
            groups.add(group);
        }
        cursor.close();
        db.close();
        return groups;

    }

    public List<Child> getChild(String idx) {
        SQLiteDatabase db = SQLiteDatabase.openDatabase(DB_FILE_PATH, null, SQLiteDatabase.OPEN_READONLY);
        Cursor cursor = db.rawQuery("select * from table" + idx, null);
        List<Child> childs = new ArrayList<>();
        while(cursor.moveToNext()) {
            Child child = new Child();
            child._id = cursor.getString(cursor.getColumnIndex("_id"));
            child.number = cursor.getString(cursor.getColumnIndex("number"));
            child.name = cursor.getString(cursor.getColumnIndex("name"));
            childs.add(child);
        }
        cursor.close();
        db.close();
        return childs;
    }

    public class Group {
        public String name;
        public String idx;
        public List<Child> childs;
    }

    public class Child {
        public String _id;
        public String number;
        public String name;
    }


}
