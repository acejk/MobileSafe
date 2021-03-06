package com.oscar.mobilesafe.utils;

import android.content.Context;
import android.content.SharedPreferences;


/**
 * Created by Administrator on 2016/8/31 0031.
 */
public class SpUtil {
    private static SharedPreferences mSp;
    private static SharedPreferences.Editor mEditor;

    private static final String FILE_NAME = "config";

    /**
     * 写boolean标识到文件中
     * @param context 上下文环境
     * @param key 存储节点名称
     * @param value 存储节点值
     */
    public static void putBoolean(Context context, String key, boolean value) {
        if(mSp == null) {
            mSp = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        }
        mEditor = mSp.edit();
        mEditor.putBoolean(key, value);
        mEditor.commit();
    }

    /**
     * 从文件中读取boolean标识
     * @param context 上下文环境
     * @param key 存储节点的名称
     * @param defValue 没有此节点的默认值
     * @return 默认值或者此节点读取到的结果
     */
    public static Boolean getBoolean(Context context, String key, boolean defValue) {
        if(mSp == null) {
            mSp = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        }
        return mSp.getBoolean(key, defValue);
    }

    /**
     * 写密码到文件中
     * @param context 上下文环境
     * @param key 存储节点名称
     * @param value 存储节点值
     */
    public static void putString(Context context, String key, String value) {
        if(mSp == null) {
            mSp = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        }
        mEditor = mSp.edit();
        mEditor.putString(key, value);
        mEditor.commit();
    }

    /**
     * 从文件中读取密码
     * @param context 上下文环境
     * @param key 存储节点的名称
     * @param defValue 没有此节点的默认值
     * @return 默认值或者此节点读取到的结果
     */
    public static String getString(Context context, String key, String defValue) {
        if(mSp == null) {
            mSp = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        }
        return mSp.getString(key, defValue);
    }


    public static void remove(Context context, String key) {
        if(mSp == null) {
            mSp = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        }
        mEditor = mSp.edit();
        mEditor.remove(key);
        mEditor.commit();
    }

    /**
     * 来电归属地吐司样式
     * @param context
     * @param key
     * @param value
     */
    public static void putInt(Context context, String key, int value) {
        if(mSp == null) {
            mSp = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        }
        mEditor = mSp.edit();
        mEditor.putInt(key, value);
        mEditor.commit();
    }

    /**
     * 来电归属地吐司样式
     * @param context
     * @param key
     * @param defValue
     */
    public static int getInt(Context context, String key, int defValue) {
        if(mSp == null) {
            mSp = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        }
        return mSp.getInt(key, defValue);
    }
}
