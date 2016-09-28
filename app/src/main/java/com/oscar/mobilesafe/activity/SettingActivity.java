package com.oscar.mobilesafe.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.oscar.mobilesafe.R;
import com.oscar.mobilesafe.service.AddressService;
import com.oscar.mobilesafe.utils.ConstentValue;
import com.oscar.mobilesafe.utils.ServiceUtil;
import com.oscar.mobilesafe.utils.SpUtil;
import com.oscar.mobilesafe.view.SettingClickView;
import com.oscar.mobilesafe.view.SettingItemView;

public class SettingActivity extends AppCompatActivity {
    private SettingItemView mSivUpdate;
    private SettingItemView mSivAddress;

    private SettingClickView mScvToastStyle;
    private SettingClickView mScvToastLocation;

    private String[] mToastDesStyles = new String[]{"透明", "橙色", "蓝色", "灰色", "绿色"};

    private int mToastStyle;

    private Context mContext;


    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        mContext = this;

        initViews();
        //是否开启更新
        initUpdate();
        //是否开启电话归属地
        initAddress();
        if (! Settings.canDrawOverlays(mContext)) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + getPackageName()));
            startActivityForResult(intent,10);
        }
        //来电吐司样式
        initToastStyle();
        //吐司的位置
        initToastLocation();
    }

    /**
     * 吐司的位置
     */
    private void initToastLocation() {
        mScvToastLocation.setTitle("归属地提示框的位置");
        mScvToastLocation.setDes("设置归属地提示框的位置");
        mScvToastLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, ToastLocationActivity.class);
                startActivity(intent);
            }
        });
    }


    private void initToastStyle() {
        mScvToastStyle.setTitle("设置归属地显示风格");
        mToastStyle = SpUtil.getInt(mContext, ConstentValue.Toast_Style, 0);
        mScvToastStyle.setDes(mToastDesStyles[mToastStyle]);
        mScvToastStyle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showToastDialog();
            }
        });
    }

    /**
     * 来电归属样式对话框
     */
    private void showToastDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("请选择归属地的样式");
        builder.setIcon(R.drawable.home_safe);
        builder.setSingleChoiceItems(mToastDesStyles, mToastStyle, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SpUtil.putInt(mContext, ConstentValue.Toast_Style, which);
                dialog.dismiss();
                mScvToastStyle.setDes(mToastDesStyles[which]);
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    /**
     * 是否开启电话归属地
     */
    private void initAddress() {
        boolean isRunning = ServiceUtil.isRunning(mContext, "com.oscar.mobilesafe.service.AddressService");
        mSivAddress.setCheck(isRunning);

        mSivAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //返回点击前的选中状态
                boolean isCheck = mSivAddress.isCheck();
                mSivAddress.setCheck(!isCheck);
                if(!isCheck) {
                    startService(new Intent(mContext, AddressService.class));
                } else {
                    stopService(new Intent(mContext, AddressService.class));
                }

            }
        });
    }

    /**
     * 是否开启更新
     */
    private void initUpdate() {
        boolean isOpenUpdate = SpUtil.getBoolean(mContext, ConstentValue.OPEN_UPDATE, false);
        mSivUpdate.setCheck(isOpenUpdate);

        mSivUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isCheck = mSivUpdate.isCheck();
                mSivUpdate.setCheck(!isCheck);
                SpUtil.putBoolean(mContext, ConstentValue.OPEN_UPDATE, !isCheck);
            }
        });
    }

    private void initViews() {
        mSivUpdate = (SettingItemView) findViewById(R.id.siv_update);
        mSivAddress = (SettingItemView) findViewById(R.id.siv_address);
        mScvToastStyle = (SettingClickView) findViewById(R.id.scv_toast_style);
        mScvToastLocation = (SettingClickView) findViewById(R.id.scv_toast_location);
    }

    @SuppressLint("NewApi")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 10) {
            if (!Settings.canDrawOverlays(this)) {
                // SYSTEM_ALERT_WINDOW permission not granted...
                Toast.makeText(mContext,"not granted",Toast.LENGTH_SHORT);
            }
        }
    }
}
