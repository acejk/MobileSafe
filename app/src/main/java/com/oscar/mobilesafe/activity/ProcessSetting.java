package com.oscar.mobilesafe.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.oscar.mobilesafe.R;
import com.oscar.mobilesafe.utils.ConstentValue;
import com.oscar.mobilesafe.utils.SpUtil;

public class ProcessSetting extends AppCompatActivity {
    private CheckBox mCbIsShowSystem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_process_setting);

        initViews();

        initEvents();
    }

    private void initEvents() {
        Boolean isShowSystem = SpUtil.getBoolean(this, ConstentValue.SHOW_SYSTEM_PROCESS, false);
        //回显复选框状态
        mCbIsShowSystem.setChecked(isShowSystem);
        if(isShowSystem) {
            mCbIsShowSystem.setText("显示系统进程");
        } else {
            mCbIsShowSystem.setText("隐藏系统进程");
        }
        mCbIsShowSystem.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    mCbIsShowSystem.setText("显示系统进程");
                } else {
                    mCbIsShowSystem.setText("隐藏系统进程");
                }
                SpUtil.putBoolean(getApplicationContext(), ConstentValue.SHOW_SYSTEM_PROCESS, isChecked);
            }
        });
    }

    private void initViews() {
        mCbIsShowSystem = (CheckBox) findViewById(R.id.cb_isshow_system);
    }
}
