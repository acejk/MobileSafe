package com.oscar.mobilesafe.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.oscar.mobilesafe.R;
import com.oscar.mobilesafe.utils.ConstentValue;
import com.oscar.mobilesafe.utils.SpUtil;
import com.oscar.mobilesafe.utils.ToastUtil;

public class SetUp4Activity extends BaseSetUpActivity {
    private Context mContext;
    private CheckBox mCbSecrity;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_up4);

        mContext = this;

        initViews();

        initDatas();
    }

    @Override
    public void showNextPage() {
        Boolean isOpenSecrity = SpUtil.getBoolean(mContext, ConstentValue.OPEN_SECRITY, false);
        if(isOpenSecrity) {
            Intent intent = new Intent(mContext, SetOverActivity.class);
            startActivity(intent);

            finish();
            //平移动画
            overridePendingTransition(R.anim.next_in_anim, R.anim.next_out_anim);
        } else {
            ToastUtil.show(mContext, "请开启安全设置");
        }

        SpUtil.putBoolean(mContext, ConstentValue.SET_OVER, true);
    }

    @Override
    public void showPrePage() {
        Intent intent = new Intent(mContext, SetUp3Activity.class);
        startActivity(intent);

        finish();
        //平移动画
        overridePendingTransition(R.anim.pre_in_anim, R.anim.pre_out_anim);
    }

    private void initDatas() {
        Boolean isOpenSecrity = SpUtil.getBoolean(mContext, ConstentValue.OPEN_SECRITY, false);
        mCbSecrity.setChecked(isOpenSecrity);
        if(isOpenSecrity) {
            mCbSecrity.setText("安全设置已开启");
        } else {
            mCbSecrity.setText("安全设置已关闭");
        }

        mCbSecrity.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SpUtil.putBoolean(mContext, ConstentValue.OPEN_SECRITY, isChecked);
                if(isChecked) {
                    mCbSecrity.setText("安全设置已开启");
                } else {
                    mCbSecrity.setText("安全设置已关闭");
                }
            }
        });

    }

    private void initViews() {
        mCbSecrity = (CheckBox) findViewById(R.id.cb_secrity);
    }

}
