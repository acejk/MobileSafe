package com.oscar.mobilesafe.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.oscar.mobilesafe.R;
import com.oscar.mobilesafe.engine.AddressDao;

public class QueryAddressActivity extends AppCompatActivity {
    private EditText mEtInputPhone;
    private Button mBtnQuery;
    private TextView mTvResult;

    private String mAddress;

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            //显示查询结果
            mTvResult.setText(mAddress);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_query_address);
        //初始化控件
        initViews();
        //事件监听
        initEvents();
    }

    private void initEvents() {
        mBtnQuery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //得到输入的电话号码
                String phone = mEtInputPhone.getText().toString();
                if(!TextUtils.isEmpty(phone)) {
                    //查询电话号码
                    query(phone);
                } else {
                    //输入框抖动
                    Animation shake = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.shake);
                    mEtInputPhone.startAnimation(shake);
                    //手机震动
                    Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
                    //震动时长
                    vibrator.vibrate(2000);
                    //规律震动
                    vibrator.vibrate(new long[]{2000, 5000, 2000, 5000}, -1);
                }
            }
        });
        //坚挺输入框文本改变
        mEtInputPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String phone = mEtInputPhone.getText().toString();
                query(phone);
            }
        });
    }

    private void query(final String phone) {
        new Thread(){
            @Override
            public void run() {
                //获得归属地
                mAddress = AddressDao.getAddress(phone);
                //消息机制，更新UI
                mHandler.sendEmptyMessage(0);
            }
        }.start();


    }

    private void initViews() {
        mEtInputPhone = (EditText) findViewById(R.id.et_query_address);
        mBtnQuery = (Button) findViewById(R.id.btn_query);
        mTvResult = (TextView) findViewById(R.id.tv_query_result);
    }
}
