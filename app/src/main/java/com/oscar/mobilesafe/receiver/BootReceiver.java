package com.oscar.mobilesafe.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;

import com.oscar.mobilesafe.utils.ConstentValue;
import com.oscar.mobilesafe.utils.SpUtil;

/**
 * Created by Administrator on 2016/9/19 0019.
 */
public class BootReceiver extends BroadcastReceiver {
    /**
     * 开机重启如果发现sim卡变更，发送短信
     * @param context
     * @param intent
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        String simNumber = SpUtil.getString(context, ConstentValue.SIM_NUMBER, "");
        TelephonyManager manager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String simSerialNumber = manager.getSimSerialNumber();
        if(!simNumber.equals(simSerialNumber)) {
            SmsManager smsManager = SmsManager.getDefault();
            String phone = SpUtil.getString(context, ConstentValue.CONTACT_PHONE, "");
            smsManager.sendTextMessage(phone, null, "sim changed!!", null, null);
        }

    }
}
