package com.oscar.mobilesafe.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.oscar.mobilesafe.R;
import com.oscar.mobilesafe.engine.AddressDao;
import com.oscar.mobilesafe.utils.ConstentValue;
import com.oscar.mobilesafe.utils.SpUtil;

/**
 * Created by Administrator on 2016/9/23 0023.
 */
public class AddressService extends Service {

    private TelephonyManager mTelephonyManager;//电话管理者

    private MyPhoneStateListenter mPhoneStateListener;//电话状态监听器

    private final String tag = "AddressService";

    private View mViewToast;//自定义吐司
    private TextView mTvToast;//吐司显示的文本

    private final WindowManager.LayoutParams mParams = new WindowManager.LayoutParams();//窗体布局

    private WindowManager mWindowManager;//窗体管理者

    private int mScreenWidth;//屏幕宽
    private int mScreenHeight;//屏幕高

    private String mAddress;//电话号码归属地

    private int[] mToastStyles;//吐司样式

    /**
     * 消息机制，通知吐司更新归属地信息
     */
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            mTvToast.setText(mAddress);
        }
    };

    //创建服务的时候调用
    @Override
    public void onCreate() {
        super.onCreate();
        mTelephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        mWindowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        //获取屏幕宽，高
        mScreenWidth = mWindowManager.getDefaultDisplay().getWidth();
        mScreenHeight = mWindowManager.getDefaultDisplay().getHeight();

        mPhoneStateListener = new MyPhoneStateListenter();
        mTelephonyManager.listen(mPhoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);



    }

    class MyPhoneStateListenter extends PhoneStateListener {
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            switch (state) {
                case TelephonyManager.CALL_STATE_IDLE:
                    //空闲状态,没有任何活动(移除吐司)
                    Log.i(tag, "挂断电话,空闲了.......................");
                    if(mTelephonyManager != null && mViewToast != null) {
                        mWindowManager.removeView(mViewToast);
                    }
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    //摘机状态，至少有个电话活动。该活动或是拨打（dialing）或是通话
                    break;
                case TelephonyManager.CALL_STATE_RINGING:
                    //响铃
                    Log.i(tag, "响铃了........");
                    showToast(incomingNumber);
                    break;
            }
        }
    }



    /**
     * 显示来电吐司
     * @param incomingNumber
     */
    private void showToast(String incomingNumber) {
        final WindowManager.LayoutParams params = mParams;
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
        params.format = PixelFormat.TRANSLUCENT;
        //在响铃的时候吐司，和电话类型一致
        params.type = WindowManager.LayoutParams.TYPE_PHONE;
        params.setTitle("Toast");
        //指定吐司的显示位置
        params.gravity = Gravity.LEFT | Gravity.TOP;

        mViewToast = View.inflate(this, R.layout.view_toast, null);
        mTvToast = (TextView) mViewToast.findViewById(R.id.tv_toast);
        //拖拽吐司
        mViewToast.setOnTouchListener(new View.OnTouchListener() {
            private int startX;
            private int startY;
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    //手指按下
                    case MotionEvent.ACTION_DOWN:
                        startX = (int) event.getRawX();
                        startY = (int) event.getRawY();
                        break;
                    //移动
                    case MotionEvent.ACTION_MOVE:
                        int moveX = (int) event.getRawX();
                        int moveY = (int) event.getRawY();

                        int offsetX = moveX - startX;
                        int offsetY = moveY - startY;

                        params.x = params.x + offsetX;
                        params.y = params.y + offsetY;



                        //容错处理
                        if(params.x < 0) {
                            params.x = 0;
                        }

                        if(params.y < 0) {
                            params.y = 0;
                        }

                        if(params.x > mScreenWidth - mViewToast.getWidth()) {
                            params.x = mScreenWidth - mViewToast.getWidth();
                        }

                        if(params.y > mScreenHeight - mViewToast.getHeight() - 30) {
                            params.y = mScreenHeight - mViewToast.getHeight() - 30;
                        }

                        mWindowManager.updateViewLayout(mViewToast, params);

                        //重置起始坐标
                        startX = (int) event.getRawX();
                        startY = (int) event.getRawY();

                        break;
                    //手指抬起
                    case MotionEvent.ACTION_UP:
                        SpUtil.putInt(getApplicationContext(), ConstentValue.LOCATION_X, params.x);
                        SpUtil.putInt(getApplicationContext(), ConstentValue.LOCATION_Y, params.y);

                        break;
                }
                return true;
            }
        });



        params.x = SpUtil.getInt(getApplicationContext(), ConstentValue.LOCATION_X, 0);
        params.y = SpUtil.getInt(getApplicationContext(), ConstentValue.LOCATION_Y, 0);

        mToastStyles = new int[]{
                R.drawable.address_toast_transparent_bg,
                R.drawable.address_toast_orange_bg,
                R.drawable.address_toast_blue_bg,
                R.drawable.address_toast_grey_bg,
                R.drawable.address_toast_green_bg
        };
        //存储吐司样式
        int toastStyleIndex = SpUtil.getInt(getApplicationContext(), ConstentValue.Toast_Style, 0);
        mTvToast.setBackgroundResource(mToastStyles[toastStyleIndex]);


        mWindowManager.addView(mViewToast, params);

        queryAddress(incomingNumber);

    }

    /**
     * 查询归属地
     * @param incomingNumber 所要查询的电话号码
     */
    private  void queryAddress(final String incomingNumber) {
        new Thread(){
            @Override
            public void run() {
                mAddress = AddressDao.getAddress(incomingNumber);
                mHandler.sendEmptyMessage(0);
            }
        }.start();
    }

    //服务停止时候调用
    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mTelephonyManager != null && mPhoneStateListener != null) {
            mTelephonyManager.listen(mPhoneStateListener, PhoneStateListener.LISTEN_NONE);
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
