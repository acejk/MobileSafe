package com.oscar.mobilesafe.utils;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by Administrator on 2016/8/29 0029.
 */
public class ToastUtil {
    public static void show(Context context, String content) {
        Toast.makeText(context, content, Toast.LENGTH_SHORT).show();
    }
}
