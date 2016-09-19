package com.oscar.mobilesafe.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;

import com.oscar.mobilesafe.R;
import com.oscar.mobilesafe.utils.ConstentValue;
import com.oscar.mobilesafe.utils.SpUtil;
import com.oscar.mobilesafe.utils.ToastUtil;
import com.oscar.mobilesafe.view.SettingItemView;

public class SetUp2Activity extends BaseSetUpActivity {
    private Context mContext;
    private SettingItemView mSivSimBind;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup2);

        mContext = this;

        initViews();

        initDatas();
    }

    @Override
    public void showNextPage() {
        String simNumber = SpUtil.getString(mContext, ConstentValue.SIM_NUMBER, "");
        if(!TextUtils.isEmpty(simNumber)) {
            Intent intent = new Intent(mContext, SetUp3Activity.class);
            startActivity(intent);

            finish();
            //平移动画
            overridePendingTransition(R.anim.next_in_anim, R.anim.next_out_anim);
        } else {
            ToastUtil.show(mContext, "请绑定sim卡");
        }
    }

    @Override
    public void showPrePage() {
        Intent intent = new Intent(mContext, SetUp1Activity.class);
        startActivity(intent);

        finish();
        //平移动画
        overridePendingTransition(R.anim.pre_in_anim, R.anim.pre_out_anim);
    }

    private void initDatas() {
        final String simNumber = SpUtil.getString(mContext, ConstentValue.SIM_NUMBER, "");
        if(TextUtils.isEmpty(simNumber)) {
            mSivSimBind.setCheck(false);
        } else {
            mSivSimBind.setCheck(true);
        }

        mSivSimBind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //获取原有的状态
                boolean isCheck = mSivSimBind.isCheck();
                //将原有状态取反
                //状态设置给当前条目
                mSivSimBind.setCheck(!isCheck);
                if(!isCheck) {
                    TelephonyManager manager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                    String simSerialNumber = manager.getSimSerialNumber();
                    SpUtil.putString(mContext, ConstentValue.SIM_NUMBER, simSerialNumber);
                } else {
                    SpUtil.remove(mContext, ConstentValue.SIM_NUMBER);
                }
            }
        });
    }

    private void initViews() {
        mSivSimBind = (SettingItemView) findViewById(R.id.setting);
    }
}
