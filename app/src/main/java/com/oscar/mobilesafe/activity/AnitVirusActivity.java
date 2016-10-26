package com.oscar.mobilesafe.activity;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.oscar.mobilesafe.R;
import com.oscar.mobilesafe.engine.AnitVirusDao;
import com.oscar.mobilesafe.utils.Md5Util;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class AnitVirusActivity extends AppCompatActivity {
    private static final int SCANNING = 100;
    private static final int SCAN_FINISH = 101;
    private ImageView mIvScanner;
    private TextView mTvName;
    private ProgressBar mPb;
    private LinearLayout mLLAddText;

    private List<ScanInfo> mVirusScanInfos;

    private int index = 0;

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SCANNING:
                    ScanInfo scanInfo = (ScanInfo) msg.obj;
                    mTvName.setText(scanInfo.name);
                    TextView tv = new TextView(getApplicationContext());
                    if(scanInfo.isVirus) {
                        tv.setTextColor(Color.RED);
                        tv.setText("发现病毒：" + scanInfo.name);
                    } else {
                        tv.setTextColor(Color.BLACK);
                        tv.setText("扫描安全：" + scanInfo.name);
                    }
                    mLLAddText.addView(tv, 0);
                    break;
                case SCAN_FINISH:
                    mTvName.setText("扫描完成");
                    mIvScanner.clearAnimation();
                    unInstallVirus();

                    break;
            }
        }
    };

    /**
     * 卸载病毒应用
     */
    private void unInstallVirus() {
        for(ScanInfo scanInfo : mVirusScanInfos) {
            String packagename = scanInfo.packagename;
            Intent intent = new Intent("android.intent.action.DELETE");
            intent.addCategory("android.intent.category.DEFAULT");
            intent.setData(Uri.parse("package:" + packagename));
            startActivity(intent);
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anit_virus);

        initViews();

        initAnimation();
        //检索病毒
        checkVirus();
    }

    private void checkVirus() {
        new Thread(){
            @Override
            public void run() {
                PackageManager pm = getPackageManager();
                List<String> anitVirus = AnitVirusDao.getAnitVirus();
                List<PackageInfo> packageInfos = null;
                    packageInfos = pm.getInstalledPackages(PackageManager.GET_SIGNATURES);
                mVirusScanInfos = new ArrayList<>();
                List<ScanInfo> scanInfos = new ArrayList<>();
                mPb.setMax(packageInfos.size());
                for(PackageInfo packageInfo : packageInfos) {
                    ScanInfo scanInfo = new ScanInfo();
                    Signature[] signatures = packageInfo.signatures;
                    Signature signature = signatures[0];
                    String str = signature.toCharsString();
                    String encoder = Md5Util.encoder(str);
                    if(anitVirus.contains(encoder)) {
                        //记录病毒
                        scanInfo.isVirus = true;
                        mVirusScanInfos.add(scanInfo);
                    } else {
                        scanInfo.isVirus = false;
                    }
                    scanInfo.packagename = packageInfo.packageName;
                    scanInfo.name = packageInfo.applicationInfo.loadLabel(pm).toString();
                    scanInfos.add(scanInfo);

                    index++;
                    mPb.setProgress(index);
                    try {
                        Thread.sleep(50 + new Random().nextInt(100));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    Message msg = Message.obtain();
                    msg.what = SCANNING;
                    msg.obj = scanInfo;
                    mHandler.sendMessage(msg);
                }
                Message msg = Message.obtain();
                msg.what = SCAN_FINISH;
                mHandler.sendMessage(msg);
            }
        }.start();

    }

    class ScanInfo {
        public boolean isVirus;
        public String packagename;
        public String name;
    }

    private void initAnimation() {
        RotateAnimation rotateAnimation = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotateAnimation.setDuration(500);
        //无限旋转
        rotateAnimation.setRepeatCount(RotateAnimation.INFINITE);
        //保持动画结束后状态
        rotateAnimation.setFillAfter(true);
        //开启旋转动画
        mIvScanner.startAnimation(rotateAnimation);
    }

    private void initViews() {
        mTvName = (TextView) findViewById(R.id.tv_name);
        mIvScanner = (ImageView) findViewById(R.id.iv_scanner);
        mPb = (ProgressBar) findViewById(R.id.pb);
        mLLAddText = (LinearLayout) findViewById(R.id.ll_add_text);
    }
}
