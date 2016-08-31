package com.oscar.mobilesafe.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.oscar.mobilesafe.R;
import com.oscar.mobilesafe.view.SettingItemView;

public class SettingActivity extends AppCompatActivity {
    private SettingItemView mSetting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        initViews();

        initEvents();
    }

    private void initEvents() {
        mSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isCheck = mSetting.isCheck();
                mSetting.setCheck(!isCheck);
            }
        });
    }

    private void initViews() {
        mSetting = (SettingItemView) findViewById(R.id.setting);
    }
}
