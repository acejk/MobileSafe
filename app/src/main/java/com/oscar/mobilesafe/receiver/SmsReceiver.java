package com.oscar.mobilesafe.receiver;

import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.telephony.SmsMessage;
import android.widget.Toast;

import com.oscar.mobilesafe.R;
import com.oscar.mobilesafe.service.LocationService;
import com.oscar.mobilesafe.utils.ConstentValue;
import com.oscar.mobilesafe.utils.SpUtil;

/**
 * Created by Administrator on 2016/9/20 0020.
 */
public class SmsReceiver extends BroadcastReceiver {
    private ComponentName mDeviceAdminSample;

    private DevicePolicyManager mDpm;

    @Override
    public void onReceive(Context context, Intent intent) {
        mDeviceAdminSample = new ComponentName(context, DeviceAdmin.class);
        mDpm = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);

        Boolean isOpenSecrity = SpUtil.getBoolean(context, ConstentValue.OPEN_SECRITY, false);
        if(isOpenSecrity) {
            Object[] smss = (Object[]) intent.getExtras().get("pdus");
            for(Object sms : smss) {
                SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) sms);
                String originatingAddress = smsMessage.getOriginatingAddress();
                String messageBody = smsMessage.getMessageBody();
                //报警短信
                if(messageBody.contains("#*alarm*#")) {
                    MediaPlayer mediaPlayer = MediaPlayer.create(context, R.raw.baojing);
                    mediaPlayer.setLooping(true);
                    mediaPlayer.start();
                }
                //发送位置信息
                if(messageBody.contains("#*location*#")) {
                    context.startService(new Intent(context, LocationService.class));
                }

                //一键锁屏
                if(messageBody.contains("#*lockscreen*#")) {
                    if(mDpm.isAdminActive(mDeviceAdminSample)) {
                        mDpm.lockNow();
                    } else {
                        Toast.makeText(context, "请先激活", Toast.LENGTH_SHORT).show();
                    }
                }

                //一键清除数据
                if(messageBody.contains("#*wipeData*#")) {
                    if(mDpm.isAdminActive(mDeviceAdminSample)) {
                        mDpm.wipeData(0);//手机数据
                    } else {
                        Toast.makeText(context, "请先激活", Toast.LENGTH_SHORT).show();
                    }
                }

                //一键卸载
                if(messageBody.contains("#*uninstall*#")) {
                    Intent intent1 = new Intent("android.intent.action.DELETE");
                    intent1.addCategory("android.intent.category.DEFAULT");
                    intent1.setData(Uri.parse("package:" + context.getPackageName()));
                    context.startActivity(intent);
                }
            }
        }
    }


}
