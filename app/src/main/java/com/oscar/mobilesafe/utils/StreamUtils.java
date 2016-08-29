package com.oscar.mobilesafe.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Administrator on 2016/8/27 0027.
 */
public class StreamUtils {
    public static String streamToString(InputStream is) {
        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int temp = -1;
        try {
            while((temp = is.read(buffer)) != -1) {
                bao.write(buffer, 0, temp);
            }
            return bao.toString();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
                bao.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
