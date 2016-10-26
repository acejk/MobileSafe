package com.oscar.mobilesafe.service;

import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.text.format.Formatter;
import android.widget.RemoteViews;

import com.oscar.mobilesafe.R;
import com.oscar.mobilesafe.engine.ProcessInfoProvider;
import com.oscar.mobilesafe.receiver.MyAppWidgetProvider;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Administrator on 2016/10/23 0023.
 */
public class UpdateWidgetService extends Service {
    private Timer mTimer;

    private ScreenReceiver mScreenReceiver;
    private String tag = "UpdateWidgetService";

    @Override
    public void onCreate() {
        super.onCreate();
        startTimer();
        //注册开锁，解锁广播
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_SCREEN_OFF);
        intentFilter.addAction(Intent.ACTION_SCREEN_ON);

        mScreenReceiver = new ScreenReceiver();
        registerReceiver(mScreenReceiver, intentFilter);

    }

    class ScreenReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
                //开启定时服务
                startTimer();
            } else {
                //关闭定时服务
                cancelTimer();
            }
        }
    }

    /**
     * 关闭定时服务
     */
    private void cancelTimer() {
        if(mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
    }

    /**
     * 定时器
     */
    private void startTimer() {
        mTimer = new Timer();
        mTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                //更新AppWidget
                updateAppWidget();
            }
        }, 0, 5000);
    }

    private void updateAppWidget() {
        AppWidgetManager aWM = AppWidgetManager.getInstance(this);
        RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.process_widget);
        //进程总数
        remoteViews.setTextViewText(R.id.tv_process_count, "进程总数：" + ProcessInfoProvider.getProcessCount(this));
        //可用内存
        String strAvailSpace = Formatter.formatFileSize(this, ProcessInfoProvider.getAvailSpace(this));
        remoteViews.setTextViewText(R.id.tv_process_memory, "可用内存：" + strAvailSpace);

        //点击除了一键清理按钮以外的地方，跳转到应用主界面
        Intent intent = new Intent("android.intent.action.Home");
        intent.addCategory("android.intent.category.DEFAULT");
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.ll_root, pendingIntent);

        //发送广播，接收到广播杀死进程
        Intent broadcastIntent = new Intent("com.oscar.action.KillProcess");
        PendingIntent broadcastPendingIntent = PendingIntent.getBroadcast(this, 0, broadcastIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.btn_clear, broadcastPendingIntent);


        ComponentName componentName = new ComponentName(getApplicationContext(), MyAppWidgetProvider.class);
        aWM.updateAppWidget(componentName, remoteViews);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mScreenReceiver != null) {
            unregisterReceiver(mScreenReceiver);
        }
        cancelTimer();
    }
}
