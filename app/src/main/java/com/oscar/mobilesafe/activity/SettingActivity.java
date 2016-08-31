package com.oscar.mobilesafe.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.oscar.mobilesafe.R;
import com.oscar.mobilesafe.utils.ConstentValue;
import com.oscar.mobilesafe.utils.SpUtil;
import com.oscar.mobilesafe.view.SettingItemView;

public class SettingActivity extends AppCompatActivity {
    private SettingItemView mSetting;

    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        mContext = this;

        initUpdate();

        initEvents();
    }

    /**
     * 点击更新条目
     */
    private void initEvents() {
        boolean isOpenUpdate = SpUtil.getBoolean(mContext, ConstentValue.OPEN_UPDATE, false);
        mSetting.setCheck(isOpenUpdate);

        mSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isCheck = mSetting.isCheck();
                mSetting.setCheck(!isCheck);
                SpUtil.putBoolean(mContext, ConstentValue.OPEN_UPDATE, !isCheck);
            }
        });
    }

    private void initUpdate() {
        mSetting = (SettingItemView) findViewById(R.id.setting);
    }
}
