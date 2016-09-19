package com.oscar.mobilesafe.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

public abstract class BaseSetUpActivity extends AppCompatActivity {
    private GestureDetector mGestureDetector;

    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mContext = this;

        getGesture();
    }

    private void getGesture() {
        mGestureDetector = new GestureDetector(mContext, new GestureDetector.SimpleOnGestureListener(){
            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                if(e1.getRawX() - e2.getRawX() > 100) {
                    showNextPage();
                } else if(e2.getRawX() - e1.getRawX() > 100) {
                    showPrePage();
                }
                return super.onFling(e1, e2, velocityX, velocityY);
            }
        });
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mGestureDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }
    //下一页
    public abstract void showNextPage();
    //上一页
    public abstract void showPrePage();

    public void nextPage(View view) {
        showNextPage();
    }

    public void prevPage(View view) {
        showPrePage();
    }
}
