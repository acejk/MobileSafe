package com.oscar.mobilesafe.engine;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Xml;

import org.xmlpull.v1.XmlSerializer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Administrator on 2016/10/16 0016.
 */
public class SmsBackUp {
    private static int index = 0;
    public static void backup(Context context, String path, CallBack callBack) {
        FileOutputStream fos = null;
        Cursor cursor = null;
        //获取备份写入的文件
        File file = new File(path);
        cursor = context.getContentResolver().query(Uri.parse("content://sms/"), new String[]{"address", "date", "type", "body"}, null, null, null);
        try {
            //文件相应的输出流
            fos = new FileOutputStream(file);
            //序列化数据库中读取的数据,放置到xml中
            XmlSerializer newSerializer = Xml.newSerializer();
            //给xml做相应设置
            newSerializer.setOutput(fos, "utf-8");
            newSerializer.startDocument("utf-8", true);

            newSerializer.startTag(null, "smss");
            //备份短信总数
            //pd.setMax(cursor.getCount());

            if(callBack != null) {
                callBack.setMax(cursor.getCount());
            }

            while(cursor.moveToNext()) {
                newSerializer.startTag(null, "sms");

                newSerializer.startTag(null, "address");
                newSerializer.text(cursor.getString(cursor.getColumnIndex("address")));
                newSerializer.endTag(null, "address");

                newSerializer.startTag(null, "date");
                newSerializer.text(cursor.getString(cursor.getColumnIndex("date")));
                newSerializer.endTag(null, "date");

                newSerializer.startTag(null, "type");
                newSerializer.text(cursor.getString(cursor.getColumnIndex("type")));
                newSerializer.endTag(null, "type");

                newSerializer.startTag(null, "body");
                newSerializer.text(cursor.getString(cursor.getColumnIndex("body")));
                newSerializer.endTag(null, "body");

                newSerializer.endTag(null, "sms");

                index++;
                Thread.sleep(500);
                //pd.setProgress(index);

                if(callBack != null) {
                    callBack.setProgress(index);
                }
            }

            newSerializer.endTag(null, "smss");

            newSerializer.endDocument();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(cursor != null && fos != null) {
                try {
                    cursor.close();
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }



    }

    public interface CallBack {
        public void setMax(int max);
        public void setProgress(int index);
    }
}
