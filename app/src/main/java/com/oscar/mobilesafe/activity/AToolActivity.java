package com.oscar.mobilesafe.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.oscar.mobilesafe.R;
import com.oscar.mobilesafe.engine.SmsBackUp;

import java.io.File;

public class AToolActivity extends AppCompatActivity {
    private TextView mTvQueryAddress, mTvSmsBackUp, mTvCommonQuery, mTvAppLock;
    private ProgressBar mPb;

    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_atool);

        mContext = this;

        initViews();

        initEvents();
    }

    private void initEvents() {
        mTvQueryAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, QueryAddressActivity.class);
                startActivity(intent);
            }
        });

        mTvSmsBackUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //短信备份进度
                showProgressDialog();
            }
        });

        mTvCommonQuery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, CommonQueryActivity.class);
                startActivity(intent);
            }
        });

        mTvAppLock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, AppLockActivity.class);
                startActivity(intent);
            }
        });
    }

    private void showProgressDialog() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setIcon(R.mipmap.ic_launcher);
        progressDialog.setTitle("短信备份");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.show();

        new Thread(){
            @Override
            public void run() {
                if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                    String path = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "sms.xml";
                    SmsBackUp.backup(getApplicationContext(), path, new SmsBackUp.CallBack() {
                        @Override
                        public void setMax(int max) {
                            mPb.setMax(max);
                            progressDialog.setMax(max);
                        }

                        @Override
                        public void setProgress(int index) {
                            mPb.setProgress(index);
                            progressDialog.setProgress(index);
                        }
                    });

                    progressDialog.dismiss();
                }
            }
        }.start();
    }

    private void initViews() {
        mTvQueryAddress = (TextView) findViewById(R.id.tv_query_address);
        mTvSmsBackUp = (TextView) findViewById(R.id.tv_backup_sms);
        mTvCommonQuery = (TextView) findViewById(R.id.tv_common_query);
        mTvAppLock = (TextView) findViewById(R.id.tv_app_lock);
        mPb = (ProgressBar) findViewById(R.id.pb);
    }
}
