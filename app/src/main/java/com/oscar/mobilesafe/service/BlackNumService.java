package com.oscar.mobilesafe.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.telephony.SmsMessage;

import com.oscar.mobilesafe.dao.BlackNumDao;

/**
 * Created by Administrator on 2016/10/15 0015.
 */
public class BlackNumService extends Service {
    private BlackNumDao mDao;

    private InnerSmsReceiver mInnerSmsReceiver;

    @Override
    public void onCreate() {
        super.onCreate();

        mDao = BlackNumDao.getInstance(getApplicationContext());

        IntentFilter filter = new IntentFilter();
        filter.addAction("android.provider.Telephony.SMS_RECEIVED");
        filter.setPriority(1000);

        mInnerSmsReceiver = new InnerSmsReceiver();
        registerReceiver(mInnerSmsReceiver, filter);
    }

    class InnerSmsReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            //1,获取短信内容
            Object[] objects = (Object[]) intent.getExtras().get("pdus");
            //2,循环遍历短信过程
            for(Object object : objects) {
                //3,获取短信对象
                SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) object);
                //4,获取短信对象的基本信息
                String originatingAddress = smsMessage.getOriginatingAddress();
                String messageBody = smsMessage.getMessageBody();

                int mode = mDao.getMode(originatingAddress);

                if(mode == 1 || mode == 3) {
                    //阻断广播
                    abortBroadcast();
                }

            }
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if(mInnerSmsReceiver != null) {
            unregisterReceiver(mInnerSmsReceiver);
        }
    }


}
