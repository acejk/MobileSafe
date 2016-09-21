package com.oscar.mobilesafe.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.oscar.mobilesafe.R;

public class AToolActivity extends AppCompatActivity {
    private TextView mTvQueryAddress;

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
    }

    private void initViews() {
        mTvQueryAddress = (TextView) findViewById(R.id.tv_query_address);
    }
}
