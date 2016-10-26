package com.oscar.mobilesafe.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.oscar.mobilesafe.engine.ProcessInfoProvider;

/**
 * Created by Administrator on 2016/10/23 0023.
 */
public class KillProcessReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        //杀死进程
        ProcessInfoProvider.killAll(context);
    }
}
