package com.oscar.mobilesafe.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.oscar.mobilesafe.R;

/**
 * Created by Administrator on 2016/8/30 0030.
 */
public class SettingClickView extends RelativeLayout {
    private TextView mTvTitle;//标题
    private TextView mTvDes;//描述

    public SettingClickView(Context context) {
        this(context, null);
    }

    public SettingClickView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SettingClickView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        View.inflate(context, R.layout.setting_click_view, this);

        mTvTitle = (TextView) findViewById(R.id.tv_title);
        mTvDes = (TextView) findViewById(R.id.tv_des);
    }

    /**
     * 设置标题
     * @param title
     */
    public void setTitle(String title) {
        mTvTitle.setText(title);
    }

    /**
     * 设置描述
     * @param des
     */
    public void setDes(String des) {
        mTvDes.setText(des);
    }





}
