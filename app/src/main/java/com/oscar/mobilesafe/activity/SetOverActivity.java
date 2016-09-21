package com.oscar.mobilesafe.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.oscar.mobilesafe.R;
import com.oscar.mobilesafe.utils.ConstentValue;
import com.oscar.mobilesafe.utils.SpUtil;

public class SetOverActivity extends AppCompatActivity {
    private Context mContext;

    private TextView mPhoneNumber;
    private TextView mTvResetUp;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mContext = this;

        Boolean isSetOver = SpUtil.getBoolean(mContext, ConstentValue.SET_OVER, false);
        if(isSetOver) {
            //设置完成
            setContentView(R.layout.activity_set_over);


            initViews();

            initDatas();

            initEvents();
        } else {
            Intent intent = new Intent(mContext, SetUp1Activity.class);
            startActivity(intent);

            finish();
        }
    }
    //重新进入设置界面
    private void initEvents() {
        mTvResetUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, SetUp1Activity.class);
                startActivity(intent);

                finish();
            }
        });
    }

    private void initViews() {
        mPhoneNumber = (TextView) findViewById(R.id.tv_number);
        mTvResetUp = (TextView) findViewById(R.id.tv_reset_setup);
    }

    private void initDatas() {
        //回显手机号
        String number = SpUtil.getString(mContext, ConstentValue.CONTACT_PHONE, "");
        mPhoneNumber.setText(number);
    }
}
