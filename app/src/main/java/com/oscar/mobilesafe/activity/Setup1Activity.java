package com.oscar.mobilesafe.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import com.oscar.mobilesafe.R;


public class SetUp1Activity extends BaseSetUpActivity {
    private Button mBtnNext;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup1);

        mContext = this;

        initViews();
    }

    @Override
    public void showNextPage() {
        Intent intent = new Intent(mContext, SetUp2Activity.class);
        startActivity(intent);

        finish();
        //平移动画
        overridePendingTransition(R.anim.next_in_anim, R.anim.next_out_anim);
    }

    @Override
    public void showPrePage() {

    }

    private void initViews() {
        mBtnNext = (Button) findViewById(R.id.btn_next);
    }
}
