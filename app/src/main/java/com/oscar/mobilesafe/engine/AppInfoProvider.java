package com.oscar.mobilesafe.engine;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.oscar.mobilesafe.model.AppInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/10/18 0018.
 */
public class AppInfoProvider {
    public static List<AppInfo> getAppInfoList(Context context) {
        PackageManager packageManager = context.getPackageManager();
        List<PackageInfo> packageInfos = packageManager.getInstalledPackages(0);
        List<AppInfo> appInfos = new ArrayList<AppInfo>();
        for (PackageInfo packageInfo : packageInfos) {
            AppInfo appInfo = new AppInfo();
            appInfo.packageName = packageInfo.packageName;
            ApplicationInfo applicationInfo = packageInfo.applicationInfo;
            appInfo.name = applicationInfo.loadLabel(packageManager).toString();
            appInfo.icon = applicationInfo.loadIcon(packageManager);

            if((applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == ApplicationInfo.FLAG_SYSTEM) {
                //系统应用
                appInfo.isSystem = true;

            } else {
                //非系统应用
                appInfo.isSystem = false;
            }

            if((applicationInfo.flags & ApplicationInfo.FLAG_EXTERNAL_STORAGE) == ApplicationInfo.FLAG_EXTERNAL_STORAGE) {
                //sd卡应用
                appInfo.isSdCard = true;
            } else {
                //手机应用
                appInfo.isSdCard = false;
            }
            appInfos.add(appInfo);
        }
        return appInfos;
    }
}
