package com.oscar.mobilesafe.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.oscar.mobilesafe.R;

/**
 * Created by Administrator on 2016/8/30 0030.
 */
public class SettingItemView extends RelativeLayout {
    private TextView mTvTitle;
    private TextView mTvDes;
    private CheckBox mCb;

    public SettingItemView(Context context) {
        this(context, null);
    }

    public SettingItemView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SettingItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        View.inflate(context, R.layout.setting_item_view, this);

        mTvTitle = (TextView) findViewById(R.id.tv_title);
        mTvDes = (TextView) findViewById(R.id.tv_des);
        mCb = (CheckBox) findViewById(R.id.cb);
    }

    /**
     * 是否选中复选框
     * @return
     */
    public boolean isCheck() {
        return mCb.isChecked();
    }


    public void setCheck(boolean isCheck) {
        mCb.setChecked(isCheck);
        if(isCheck) {
            mTvDes.setText("自动更新已开启");
        } else {
            mTvDes.setText("自动更新已关闭");
        }
    }

}
