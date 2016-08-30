package com.oscar.mobilesafe.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.GridView;

import com.oscar.mobilesafe.adapter.GridViewAdapter;
import com.oscar.mobilesafe.R;

public class HomeActivity extends AppCompatActivity {
    private GridView mGv;
    private GridViewAdapter mAdapter;

    private Context mContext;

    private String[] mTitles = {"手机防盗","通信卫士","软件管理","进程管理","流量统计","手机杀毒","缓存清理","高级工具","设置中心"};
    private int[] mIcons = {R.drawable.home_safe,R.drawable.home_callmsgsafe,
            R.drawable.home_apps,R.drawable.home_taskmanager,
            R.drawable.home_netmanager,R.drawable.home_trojan,
            R.drawable.home_sysoptimize,R.drawable.home_tools,R.drawable.home_settings};



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        initViews();

        initDatas();
    }

    private void initDatas() {
        mContext = this;

        mAdapter = new GridViewAdapter(mContext, mTitles, mIcons);
        mGv.setAdapter(mAdapter);
    }

    private void initViews() {
        mGv = (GridView) findViewById(R.id.gv);
    }
}
