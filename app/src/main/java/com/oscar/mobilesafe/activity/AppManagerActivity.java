package com.oscar.mobilesafe.activity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.StatFs;
import android.support.v7.app.AppCompatActivity;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.oscar.mobilesafe.R;
import com.oscar.mobilesafe.engine.AppInfoProvider;
import com.oscar.mobilesafe.model.AppInfo;
import com.oscar.mobilesafe.utils.ToastUtil;

import java.util.ArrayList;
import java.util.List;

public class AppManagerActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView mTvMemory, mTvSdMemory;
    private ListView mLvApp;
    private TextView mTvDes;

    private List<AppInfo> mAppInfos;
    private List<AppInfo> mCustomAppInfos;//用户应用
    private List<AppInfo> mSystemAppInfos;//系统应用

    private AppManagerAdapter mAdapter;

    private PopupWindow mPopupWindow;

    private AppInfo mAppInfo;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            mAdapter = new AppManagerAdapter();
            mLvApp.setAdapter(mAdapter);

            if (mTvDes != null && mCustomAppInfos != null) {
                mTvDes.setText("用户应用(" + mCustomAppInfos.size() + ")");
            }

        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_manager);

        initViews();

        initTitles();

        initDatas();

    }

    private void initDatas() {


        mLvApp.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (mCustomAppInfos != null && mSystemAppInfos != null) {
                    if (firstVisibleItem >= mCustomAppInfos.size() + 1) {
                        mTvDes.setText("系统应用(" + mSystemAppInfos.size() + ")");
                    } else {
                        mTvDes.setText("用户应用(" + mCustomAppInfos.size() + ")");
                    }
                }
            }
        });

        mLvApp.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0 || position == mCustomAppInfos.size() + 1) {
                    return;
                } else {
                    if (position < mCustomAppInfos.size() + 1) {
                        mAppInfo = mCustomAppInfos.get(position - 1);
                    } else {
                        mAppInfo = mSystemAppInfos.get(position - mCustomAppInfos.size() - 2);
                    }

                    showPopupWindow(view);
                }
            }
        });

    }

    //弹出窗体
    private void showPopupWindow(View view) {
        View popupView = View.inflate(this, R.layout.popupwindow, null);

        TextView tvUninstall = (TextView) popupView.findViewById(R.id.tv_uninstall);
        TextView tvStart = (TextView) popupView.findViewById(R.id.tv_start);
        TextView tvShare = (TextView) popupView.findViewById(R.id.tv_share);

        tvUninstall.setOnClickListener(this);
        tvStart.setOnClickListener(this);
        tvShare.setOnClickListener(this);

        AlphaAnimation alphaAnimation = new AlphaAnimation(0, 1);
        alphaAnimation.setDuration(1000);
        alphaAnimation.setFillAfter(true);

        ScaleAnimation scaleAnimation = new ScaleAnimation(0, 1, 0, 1, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        scaleAnimation.setDuration(1000);
        scaleAnimation.setFillAfter(true);

        AnimationSet animationSet = new AnimationSet(true);
        animationSet.addAnimation(alphaAnimation);
        animationSet.addAnimation(scaleAnimation);



        mPopupWindow = new PopupWindow(popupView, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);
        //弹出框背景设置为透明
        mPopupWindow.setBackgroundDrawable(new ColorDrawable());
        //弹出框位置
        mPopupWindow.showAsDropDown(view, 100, -view.getHeight());
        popupView.startAnimation(animationSet);

    }

    private void initTitles() {
        //磁盘路径
        String path = Environment.getDataDirectory().getAbsolutePath();
        //sd卡路径
        String sdPath = Environment.getExternalStorageDirectory().getAbsolutePath();
        //可用磁盘大小
        String memoryAvailSpace = Formatter.formatFileSize(this, getAvailSpace(path));
        //可用sd卡大小
        String sdMemoryAvailSpace = Formatter.formatFileSize(this, getAvailSpace(sdPath));

        mTvMemory.setText("可用磁盘大小：" + memoryAvailSpace);
        mTvSdMemory.setText("可用sd卡大小：" + sdMemoryAvailSpace);
    }

    private long getAvailSpace(String path) {
        //获取可用磁盘大小的类
        StatFs statFs = new StatFs(path);
        //获取可用区块的个数
        long count = statFs.getAvailableBlocks();
        //获取区块大小
        long size = statFs.getBlockSize();
        return count * size;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case  R.id.tv_uninstall:
                if(mAppInfo.isSystem) {
                    ToastUtil.show(getApplicationContext(), "系统应用不能卸载");
                } else {
                    Intent intent = new Intent("android.intent.action.DELETE");
                    intent.addCategory("android.intent.category.DEFAULT");
                    intent.setData(Uri.parse("package:" + mAppInfo.getPackageName()));
                    startActivity(intent);
                }
                break;
            case R.id.tv_start:
                PackageManager pm = getPackageManager();
                Intent launchIntentForPackage = pm.getLaunchIntentForPackage(mAppInfo.getPackageName());
                if(launchIntentForPackage != null) {
                    startActivity(launchIntentForPackage);
                } else {
                    ToastUtil.show(getApplicationContext(), "此应用不能被启动");
                }

                break;
            case R.id.tv_share:
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.putExtra(Intent.EXTRA_TEXT, "分享一个应用，应用名称为：" + mAppInfo.getName());
                intent.setType("text/plain");
                startActivity(intent);
                break;
        }

        if(mPopupWindow != null) {
            mPopupWindow.dismiss();
        }
    }


    class AppManagerAdapter extends BaseAdapter {

        @Override
        public int getViewTypeCount() {
            return super.getViewTypeCount() + 1;
        }

        @Override
        public int getItemViewType(int position) {
            if (position == 0 || position == mCustomAppInfos.size() + 1) {
                //文本
                return 0;
            } else {
                //图片+文本
                return 1;
            }
        }

        @Override
        public int getCount() {
            return mCustomAppInfos.size() + mSystemAppInfos.size() + 2;
        }

        @Override
        public AppInfo getItem(int position) {
            if (position == 0 || position == mCustomAppInfos.size() + 1) {
                return null;
            } else {
                if (position < mCustomAppInfos.size() + 1) {
                    return mCustomAppInfos.get(position - 1);
                } else {
                    return mSystemAppInfos.get(position - mCustomAppInfos.size() - 2);
                }
            }
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            int type = getItemViewType(position);
            //文本
            if (type == 0) {
                ViewTitleHolder viewHolder = null;
                if (convertView == null) {
                    convertView = View.inflate(getApplicationContext(), R.layout.listview_app_title_item, null);
                    viewHolder = new ViewTitleHolder();
                    viewHolder.tvTitle = (TextView) convertView.findViewById(R.id.tv_title);
                    convertView.setTag(viewHolder);
                } else {
                    viewHolder = (ViewTitleHolder) convertView.getTag();
                }
                if (position == 0) {
                    viewHolder.tvTitle.setText("用户应用(" + mCustomAppInfos.size() + ")");
                } else {
                    viewHolder.tvTitle.setText("系统应用(" + mSystemAppInfos.size() + ")");
                }
                return convertView;
            } else {
                //图片+文本
                ViewHolder viewHolder = null;
                if (convertView == null) {
                    convertView = View.inflate(getApplicationContext(), R.layout.listview_appmanager_item, null);
                    viewHolder = new ViewHolder();
                    viewHolder.ivIcon = (ImageView) convertView.findViewById(R.id.iv_icon);
                    viewHolder.tvName = (TextView) convertView.findViewById(R.id.tv_name);
                    viewHolder.tvPath = (TextView) convertView.findViewById(R.id.tv_path);
                    convertView.setTag(viewHolder);
                } else {
                    viewHolder = (ViewHolder) convertView.getTag();
                }
                viewHolder.ivIcon.setBackgroundDrawable(getItem(position).icon);
                viewHolder.tvName.setText(getItem(position).name);
                if (getItem(position).isSdCard) {
                    viewHolder.tvPath.setText("SD卡应用");
                } else {
                    viewHolder.tvPath.setText("手机应用");
                }
            }
            return convertView;
        }
    }

    static class ViewHolder {
        public ImageView ivIcon;
        public TextView tvName;
        public TextView tvPath;
    }

    static class ViewTitleHolder {
        public TextView tvTitle;
    }

    private void initViews() {
        mTvMemory = (TextView) findViewById(R.id.tv_memory);
        mTvSdMemory = (TextView) findViewById(R.id.tv_sd_memory);
        mLvApp = (ListView) findViewById(R.id.lv_app_list);
        mTvDes = (TextView) findViewById(R.id.tv_des);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getData();
    }

    /**
     * 获取数据
     */
    private void getData() {
        new Thread() {
            @Override
            public void run() {
                mAppInfos = AppInfoProvider.getAppInfoList(getApplicationContext());
                mCustomAppInfos = new ArrayList<AppInfo>();
                mSystemAppInfos = new ArrayList<AppInfo>();
                for (AppInfo appInfo : mAppInfos) {
                    if (appInfo.isSystem) {
                        //系统应用
                        mSystemAppInfos.add(appInfo);
                    } else {
                        //用户应用
                        mCustomAppInfos.add(appInfo);
                    }
                }
                mHandler.sendEmptyMessage(0);
            }
        }.start();
    }
}
