package com.oscar.mobilesafe.engine;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Debug;

import com.oscar.mobilesafe.R;
import com.oscar.mobilesafe.model.ProcessInfo;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/10/20 0020.
 */
public class ProcessInfoProvider {
    /**
     * 获取进程总数
     * @param context 上下文环境
     * @return 进程总数
     */
    public static int getProcessCount(Context context) {
        //Activity管理者
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        //进程总数
        List<ActivityManager.RunningAppProcessInfo> runningAppProcesses = am.getRunningAppProcesses();
        return runningAppProcesses.size();

    }

    /**
     * 获取可用内存大小
     * @param context
     * @return
     */
    public static long getAvailSpace(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        am.getMemoryInfo(memoryInfo);
        return memoryInfo.availMem;
    }

    public static long getTotalSpace() {
        FileReader fileReader = null;
        BufferedReader br = null;
        try {
            fileReader = new FileReader("proc/meminfo");
            br = new BufferedReader(fileReader);
            String lineOne = br.readLine();
            char[] charArray = lineOne.toCharArray();
            StringBuffer sb = new StringBuffer();
            for (char c : charArray) {
                if(c >= '0' && c <= '9') {
                    sb.append(c);
                }
            }
            return Long.parseLong(sb.toString()) * 1024;

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(fileReader != null && br != null) {
                try {
                    fileReader.close();
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return 0;
    }

    /**
     * 获取进程相关信息
     * @param context
     * @return
     */
    public static List<ProcessInfo> getProcessInfo(Context context) {
        List<ProcessInfo> processInfos = new ArrayList<>();
        //Activity管理者
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        //包管理者
        PackageManager pm = context.getPackageManager();
        List<ActivityManager.RunningAppProcessInfo> runningAppProcesses = am.getRunningAppProcesses();
        for(ActivityManager.RunningAppProcessInfo info : runningAppProcesses) {
            ProcessInfo processInfo = new ProcessInfo();
            //获取进程的名称 == 应用的包名
            processInfo.packageName = info.processName;
            //获取进程占用的内存大小(传递一个进程对应的pid数组)
            Debug.MemoryInfo[] processMemoryInfo = am.getProcessMemoryInfo(new int[]{info.pid});
            //返回数组中索引位置为0的对象,为当前进程的内存信息的对象
            Debug.MemoryInfo memoryInfo = processMemoryInfo[0];
            //获取已使用的大小
            processInfo.memSize = memoryInfo.getTotalPrivateDirty() * 1024;

            try {
                ApplicationInfo applicationInfo = pm.getApplicationInfo(processInfo.packageName, 0);
                //获取应用名称
                processInfo.name = applicationInfo.loadLabel(pm).toString();
                //获取应用图标
                processInfo.icon = applicationInfo.loadIcon(pm);
                //判断是否为系统进程
                if((applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == ApplicationInfo.FLAG_SYSTEM) {
                    processInfo.isSystem = true;
                } else {
                    processInfo.isSystem = false;
                }
            } catch (PackageManager.NameNotFoundException e) {
                processInfo.name = info.processName;
                processInfo.icon = context.getResources().getDrawable(R.mipmap.ic_launcher);
                processInfo.isSystem = true;
                e.printStackTrace();
            }
            processInfos.add(processInfo);
        }
        return processInfos;
    }

    /**
     * 杀死进程
     * @param context
     * @param processInfo
     */
    public static void killProcess(Context context, ProcessInfo processInfo) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        am.killBackgroundProcesses(processInfo.packageName);
    }

    /**
     * 杀死所有进程
     * @param ctx	上下文环境
     */
    public static void killAll(Context ctx) {
        //1,获取activityManager
        ActivityManager am = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
        //2,获取正在运行进程的集合
        List<ActivityManager.RunningAppProcessInfo> runningAppProcesses = am.getRunningAppProcesses();
        //3,循环遍历所有的进程,并且杀死
        for (ActivityManager.RunningAppProcessInfo info : runningAppProcesses) {
            //4,除了手机卫士以外,其他的进程都需要去杀死
            if(info.processName.equals(ctx.getPackageName())){
                //如果匹配上了手机卫士,则需要跳出本次循环,进行下一次寻,继续杀死进程
                continue;
            }
            am.killBackgroundProcesses(info.processName);
        }
    }
}
