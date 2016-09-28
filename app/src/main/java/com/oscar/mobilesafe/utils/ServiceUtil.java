package com.oscar.mobilesafe.utils;

import android.app.ActivityManager;
import android.content.Context;

import java.util.List;

/**
 * Created by Administrator on 2016/9/23 0023.
 */
public class ServiceUtil {
    public static boolean isRunning(Context context, String serviceName) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> runningServices = activityManager.getRunningServices(1000);
        for(ActivityManager.RunningServiceInfo runningService : runningServices) {
            if(serviceName.equals(runningService.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}
