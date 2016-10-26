package com.oscar.mobilesafe.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.oscar.mobilesafe.R;
import com.oscar.mobilesafe.dao.AppLockDao;
import com.oscar.mobilesafe.engine.AppInfoProvider;
import com.oscar.mobilesafe.model.AppInfo;

import java.util.ArrayList;
import java.util.List;

public class AppLockActivity extends AppCompatActivity {
    private Button mBtnLock, mBtnUnLock;
    private LinearLayout mLLLock, mLLUnLock;
    private ListView mLvLock, mLvUnLock;
    private TextView mTvLock, mTvUnLock;

    private List<AppInfo> mAppInfos;
    private List<AppInfo> mAppLockInfos;
    private List<AppInfo> mAppUnLockInfos;

    private AppLockDao mDao;

    private List<String> mPackagenames;

    private AppLockAdapter mLockAdapter;//有锁适配器
    private AppLockAdapter mUnLockAdapter;//无锁适配器

    private TranslateAnimation mTranslateAnimation;

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            mLockAdapter = new AppLockAdapter(true);
            mLvLock.setAdapter(mLockAdapter);

            mUnLockAdapter = new AppLockAdapter(false);
            mLvUnLock.setAdapter(mUnLockAdapter);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_lock);

        initViews();

        initDatas();

        initEvents();
        //动画效果
        initAnimation();
    }

    private void initAnimation() {
        mTranslateAnimation = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0,
                Animation.RELATIVE_TO_SELF, 1,
                Animation.RELATIVE_TO_SELF, 0,
                Animation.RELATIVE_TO_SELF, 0
        );
        mTranslateAnimation.setDuration(500);


    }

    private void initEvents() {
        mBtnLock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLLLock.setVisibility(View.VISIBLE);
                mLLUnLock.setVisibility(View.GONE);
                mBtnLock.setBackgroundResource(R.drawable.tab_right_pressed);
                mBtnUnLock.setBackgroundResource(R.drawable.tab_left_default);

            }
        });

        mBtnUnLock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLLLock.setVisibility(View.GONE);
                mLLUnLock.setVisibility(View.VISIBLE);
                mBtnLock.setBackgroundResource(R.drawable.tab_right_default);
                mBtnUnLock.setBackgroundResource(R.drawable.tab_left_pressed);

            }
        });
    }

    private void initDatas() {
        new Thread(){
            @Override
            public void run() {
                mAppInfos = AppInfoProvider.getAppInfoList(getApplicationContext());
                mAppLockInfos = new ArrayList<AppInfo>();
                mAppUnLockInfos = new ArrayList<AppInfo>();

                mDao = AppLockDao.getInstance(getApplicationContext());
                mPackagenames = mDao.findAll();
                for(AppInfo appInfo : mAppInfos) {
                    if(mPackagenames.contains(appInfo.packageName)) {
                        mAppLockInfos.add(appInfo);
                    } else {
                        mAppUnLockInfos.add(appInfo);
                    }
                }
                //告知主线程更新UI
                mHandler.sendEmptyMessage(0);

            }
        }.start();
    }

    class AppLockAdapter extends BaseAdapter {
        private boolean isLock;

        public AppLockAdapter(boolean isLock) {
            this.isLock = isLock;
        }

        @Override
        public int getCount() {
            if (isLock) {
                mTvLock.setText("已加锁应用：" + mAppLockInfos.size() + "");
                return mAppLockInfos.size();
            } else {
                mTvUnLock.setText("未加锁应用：" + mAppUnLockInfos.size() + "");
                return mAppUnLockInfos.size();
            }
        }


        @Override
        public AppInfo getItem(int position) {
            if(isLock) {
                return mAppLockInfos.get(position);
            } else {
                return mAppUnLockInfos.get(position);
            }
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder = null;
            if(convertView == null) {
                convertView = View.inflate(getApplicationContext(), R.layout.listview_app_lock_item, null);
                viewHolder = new ViewHolder();
                viewHolder.ivIcon = (ImageView) convertView.findViewById(R.id.iv_icon);
                viewHolder.tvName = (TextView) convertView.findViewById(R.id.tv_name);
                viewHolder.ivLock = (ImageView) convertView.findViewById(R.id.iv_lock);

                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            final AppInfo appInfo = getItem(position);
            final View animationView = convertView;
            viewHolder.ivIcon.setBackgroundDrawable(appInfo.icon);
            viewHolder.tvName.setText(appInfo.name);
            if(isLock) {
                viewHolder.ivLock.setBackgroundResource(R.drawable.lock);
            } else {
                viewHolder.ivLock.setBackgroundResource(R.drawable.unlock);
            }
            viewHolder.ivLock.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    animationView.startAnimation(mTranslateAnimation);
                    mTranslateAnimation.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            if(isLock) {
                                mAppLockInfos.remove(appInfo);
                                mAppUnLockInfos.add(appInfo);
                                mDao.delete(appInfo.packageName);
                                mLockAdapter.notifyDataSetChanged();
                            } else {
                                mAppUnLockInfos.remove(appInfo);
                                mAppLockInfos.add(appInfo);
                                mDao.insert(appInfo.packageName);
                                mUnLockAdapter.notifyDataSetChanged();
                            }
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });

                }
            });
            return convertView;
        }
    }

    static class ViewHolder {
        public ImageView ivIcon;
        public TextView tvName;
        public ImageView ivLock;
    }

    private void initViews() {
        mBtnLock = (Button) findViewById(R.id.btn_lock);
        mBtnUnLock = (Button) findViewById(R.id.btn_unlock);
        mLLLock = (LinearLayout) findViewById(R.id.ll_lock);
        mLLUnLock = (LinearLayout) findViewById(R.id.ll_unlock);
        mTvLock = (TextView) findViewById(R.id.tv_lock);
        mTvUnLock = (TextView) findViewById(R.id.tv_unlock);
        mLvLock = (ListView) findViewById(R.id.lv_lock);
        mLvUnLock = (ListView) findViewById(R.id.lv_unlock);
    }
}
