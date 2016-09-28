package com.oscar.mobilesafe.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.oscar.mobilesafe.R;
import com.oscar.mobilesafe.utils.ConstentValue;
import com.oscar.mobilesafe.utils.SpUtil;

public class ToastLocationActivity extends Activity {
    private ImageView mIvDrag;
    private Button mBtnTop;
    private Button mBtnBottom;

    private int mStartX;//起始x坐标
    private int mStartY;//起始y坐标

    private WindowManager mWindowManager;//窗口管理者

    private int mScreenWidth;//屏幕宽
    private int mScreenHeight;//屏幕高

    private Context mContext;

    private long[] mHits = new long[2];//双击


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_toast_location);

        mContext = this;

        mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        mScreenWidth =  mWindowManager.getDefaultDisplay().getWidth();
        mScreenHeight = mWindowManager.getDefaultDisplay().getHeight();

        initViews();

        initEvents();

        //显示初始位置
        showInitLocaiton();

    }

    private void showInitLocaiton() {
        int locaiotnX = SpUtil.getInt(mContext, ConstentValue.LOCATION_X, 0);
        int locationY = SpUtil.getInt(mContext, ConstentValue.LOCATION_Y, 0);

        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.leftMargin = locaiotnX;
        layoutParams.topMargin = locationY;

        mIvDrag.setLayoutParams(layoutParams);

        if(locationY > mScreenHeight / 2) {
            mBtnBottom.setVisibility(View.INVISIBLE);
            mBtnTop.setVisibility(View.VISIBLE);
        } else {
            mBtnBottom.setVisibility(View.VISIBLE);
            mBtnTop.setVisibility(View.INVISIBLE);
        }
    }

    private void initEvents() {
        mIvDrag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.arraycopy(mHits, 1, mHits, 0, mHits.length-1);
                mHits[mHits.length-1] = SystemClock.uptimeMillis();
                if(mHits[mHits.length-1]-mHits[0]<500){
                    int left = mScreenWidth / 2 - mIvDrag.getWidth() / 2;
                    int top = mScreenHeight / 2 - mIvDrag.getHeight() / 2;
                    int right = mScreenWidth / 2 + mIvDrag.getWidth() / 2;
                    int bottom = mScreenHeight / 2 + mIvDrag.getHeight() / 2;

                    mIvDrag.layout(left, top, right, bottom);

                    SpUtil.putInt(mContext, ConstentValue.LOCATION_X, mIvDrag.getLeft());
                    SpUtil.putInt(mContext, ConstentValue.LOCATION_Y, mIvDrag.getTop());

                }
            }
        });


        mIvDrag.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    //手指按下
                    case MotionEvent.ACTION_DOWN:
                        mStartX = (int) event.getRawX();
                        mStartY = (int) event.getRawY();
                        break;
                    //移动
                    case MotionEvent.ACTION_MOVE:
                        int moveX = (int) event.getRawX();
                        int moveY = (int) event.getRawY();

                        int offsetX = moveX - mStartX;
                        int offsetY = moveY - mStartY;

                        int left = mIvDrag.getLeft() + offsetX;
                        int top = mIvDrag.getTop() + offsetY;
                        int right = mIvDrag.getRight() + offsetX;
                        int bottom = mIvDrag.getBottom() + offsetY;
                        //容错处理
                        if(left < 0 || right > mScreenWidth || top < 0 || bottom > mScreenHeight - 30) {
                            return true;
                        }

                        if(top > mScreenHeight / 2) {
                            mBtnBottom.setVisibility(View.INVISIBLE);
                            mBtnTop.setVisibility(View.VISIBLE);
                        } else {
                            mBtnBottom.setVisibility(View.VISIBLE);
                            mBtnTop.setVisibility(View.INVISIBLE);
                        }


                        mIvDrag.layout(left, top, right, bottom);
                        //重置起始坐标
                        mStartX = (int) event.getRawX();
                        mStartY = (int) event.getRawY();

                        break;
                    //手指抬起
                    case MotionEvent.ACTION_UP:
                        SpUtil.putInt(mContext, ConstentValue.LOCATION_X, mIvDrag.getLeft());
                        SpUtil.putInt(mContext, ConstentValue.LOCATION_Y, mIvDrag.getTop());

                        break;
                }
                return false;
            }
        });
    }

    private void initViews() {
        mIvDrag = (ImageView) findViewById(R.id.iv_drag);
        mBtnBottom = (Button) findViewById(R.id.btn_bottom);
        mBtnTop = (Button) findViewById(R.id.btn_top);
    }
}
