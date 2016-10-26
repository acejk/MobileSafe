package com.oscar.mobilesafe.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.oscar.mobilesafe.R;
import com.oscar.mobilesafe.engine.ProcessInfoProvider;
import com.oscar.mobilesafe.model.ProcessInfo;
import com.oscar.mobilesafe.utils.ConstentValue;
import com.oscar.mobilesafe.utils.SpUtil;
import com.oscar.mobilesafe.utils.ToastUtil;

import java.util.ArrayList;
import java.util.List;

public class ProcessManagerActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView mTvProcessAll;
    private TextView mTvAvailSpaceAndAllSpace;
    private ListView mLvProcess;
    private Button mBtnAll, mBtnReverse, mBtnClear, mBtnSetting;
    private TextView mTvDes;

    private List<ProcessInfo> mProcessInfos;
    private List<ProcessInfo> mCustomProcessInfos;
    private List<ProcessInfo> mSystemProcessInfos;

    private ProcessInfo mProcessInfo;

    private ProcessManagerAdapter mAdapter;

    private int mProcessCount;//进程总数
    private long mAvailSpace;//可用空间
    private String mStrTotalSpace;//总空间

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            mAdapter = new ProcessManagerAdapter();
            mLvProcess.setAdapter(mAdapter);

            if (mTvDes != null && mCustomProcessInfos != null) {
                mTvDes.setText("用户进程(" + mCustomProcessInfos.size() + ")");
            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_process_manager);

        initViews();

        initDatas();

        initEvents();
    }

    private void initDatas() {
        int processCount = ProcessInfoProvider.getProcessCount(this);
        mTvProcessAll.setText("进程总数：" + processCount);

        long availSpace = ProcessInfoProvider.getAvailSpace(this);
        String strAvailSpace = Formatter.formatFileSize(this, availSpace);

        long totalSpace = ProcessInfoProvider.getTotalSpace();
        mStrTotalSpace = Formatter.formatFileSize(this, totalSpace);
        mTvAvailSpaceAndAllSpace.setText("剩余/总共：" + strAvailSpace + "/" + mStrTotalSpace);

        getData();

        mLvProcess.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0 || position == mCustomProcessInfos.size() + 1) {
                    return;
                } else {
                    if (position < mCustomProcessInfos.size() + 1) {
                        mProcessInfo = mCustomProcessInfos.get(position - 1);
                    } else {
                        mProcessInfo = mSystemProcessInfos.get(position - mCustomProcessInfos.size() - 2);
                    }

                    if(mProcessInfo != null) {
                        if(!mProcessInfo.packageName.equals(getPackageName())) {
                            mProcessInfo.isCheck = !mProcessInfo.isCheck;
                            CheckBox cb = (CheckBox) view.findViewById(R.id.cb);
                            cb.setChecked(mProcessInfo.isCheck);
                        }
                    }

                }
            }
        });

        mLvProcess.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (mCustomProcessInfos != null && mSystemProcessInfos != null) {
                    if (firstVisibleItem >= mCustomProcessInfos.size() + 1) {
                        mTvDes.setText("系统进程(" + mSystemProcessInfos.size() + ")");
                    } else {
                        mTvDes.setText("用户进程(" + mCustomProcessInfos.size() + ")");
                    }
                }
            }
        });
    }

    /**
     * 获取数据
     */
    private void getData() {
        new Thread() {
            @Override
            public void run() {
                mProcessInfos = ProcessInfoProvider.getProcessInfo(getApplicationContext());
                mCustomProcessInfos = new ArrayList<ProcessInfo>();
                mSystemProcessInfos = new ArrayList<ProcessInfo>();
                for (ProcessInfo processInfo : mProcessInfos) {
                    if (processInfo.isSystem) {
                        //系统应用
                        mSystemProcessInfos.add(processInfo);
                    } else {
                        //用户应用
                        mCustomProcessInfos.add(processInfo);
                    }
                }
                mHandler.sendEmptyMessage(0);
            }
        }.start();
    }

    class ProcessManagerAdapter extends BaseAdapter {

        @Override
        public int getViewTypeCount() {
            return super.getViewTypeCount() + 1;
        }

        @Override
        public int getItemViewType(int position) {
            if (position == 0 || position == mCustomProcessInfos.size() + 1) {
                //文本
                return 0;
            } else {
                //图片+文本
                return 1;
            }
        }

        @Override
        public int getCount() {
            Boolean isShowSystemProcess = SpUtil.getBoolean(getApplicationContext(), ConstentValue.SHOW_SYSTEM_PROCESS, false);
            if(isShowSystemProcess) {
                return mCustomProcessInfos.size() + mSystemProcessInfos.size() + 2;
            } else {
                return mCustomProcessInfos.size() + 1;
            }

        }

        @Override
        public ProcessInfo getItem(int position) {
            if (position == 0 || position == mCustomProcessInfos.size() + 1) {
                return null;
            } else {
                if (position < mCustomProcessInfos.size() + 1) {
                    return mCustomProcessInfos.get(position - 1);
                } else {
                    return mSystemProcessInfos.get(position - mCustomProcessInfos.size() - 2);
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
                    convertView = View.inflate(getApplicationContext(), R.layout.listview_process_title_item, null);
                    viewHolder = new ViewTitleHolder();
                    viewHolder.tvTitle = (TextView) convertView.findViewById(R.id.tv_title);
                    convertView.setTag(viewHolder);
                } else {
                    viewHolder = (ViewTitleHolder) convertView.getTag();
                }
                if (position == 0) {
                    viewHolder.tvTitle.setText("用户进程(" + mCustomProcessInfos.size() + ")");
                } else {
                    viewHolder.tvTitle.setText("系统进程(" + mSystemProcessInfos.size() + ")");
                }
                return convertView;
            } else {
                //图片+文本
                ViewHolder viewHolder = null;
                if (convertView == null) {
                    convertView = View.inflate(getApplicationContext(), R.layout.listview_processmanager_item, null);
                    viewHolder = new ViewHolder();
                    viewHolder.ivIcon = (ImageView) convertView.findViewById(R.id.iv_icon);
                    viewHolder.tvName = (TextView) convertView.findViewById(R.id.tv_name);
                    viewHolder.tvMemSize = (TextView) convertView.findViewById(R.id.tv_memsize);
                    viewHolder.cb = (CheckBox) convertView.findViewById(R.id.cb);
                    convertView.setTag(viewHolder);
                } else {
                    viewHolder = (ViewHolder) convertView.getTag();
                }
                viewHolder.ivIcon.setBackgroundDrawable(getItem(position).icon);
                viewHolder.tvName.setText(getItem(position).name);
                String strMemSize = Formatter.formatFileSize(getApplicationContext(), getItem(position).getMemSize());
                viewHolder.tvMemSize.setText("占用内存 " + strMemSize);

                if(getItem(position).packageName.equals(getPackageName())) {
                    viewHolder.cb.setVisibility(View.GONE);
                } else {
                    viewHolder.cb.setVisibility(View.VISIBLE);
                }

                viewHolder.cb.setChecked(getItem(position).isCheck);
            }
            return convertView;
        }
    }

    static class ViewHolder {
        public ImageView ivIcon;
        public TextView tvName;
        public TextView tvMemSize;
        public CheckBox cb;
    }

    static class ViewTitleHolder {
        public TextView tvTitle;
    }

    private void initEvents() {
        mBtnAll.setOnClickListener(this);
        mBtnReverse.setOnClickListener(this);
        mBtnClear.setOnClickListener(this);
        mBtnSetting.setOnClickListener(this);
    }

    private void initViews() {
        mTvProcessAll = (TextView) findViewById(R.id.tv_process_all);
        mTvAvailSpaceAndAllSpace = (TextView) findViewById(R.id.tv_availspace_allspace);
        mTvDes = (TextView) findViewById(R.id.tv_des);
        mLvProcess = (ListView) findViewById(R.id.lv_process);
        mBtnAll = (Button) findViewById(R.id.btn_choose_all);
        mBtnReverse = (Button) findViewById(R.id.btn_choose_reverse);
        mBtnClear = (Button) findViewById(R.id.btn_clear);
        mBtnSetting = (Button) findViewById(R.id.btn_setting);
    }

    /**
     * 全选，反选，一键清理，设置
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_choose_all:
                selectAll();
                break;
            case R.id.btn_choose_reverse:
                selectReverse();
                break;
            case R.id.btn_clear:
                clear();
                break;
            case R.id.btn_setting:
                setting();
                break;
        }
    }

    /**
     * 设置
     */
    private void setting() {
        Intent intent = new Intent(this, ProcessSetting.class);
        startActivityForResult(intent, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        }
    }

    /**
     * 一键清理
     */
    private void clear() {
        List<ProcessInfo> killProcessInfos = new ArrayList<>();
        for(ProcessInfo processInfo : mCustomProcessInfos) {
            if(processInfo.packageName.equals(getPackageName())) {
                continue;
            }
            if(processInfo.isCheck) {
                killProcessInfos.add(processInfo);
            }
        }

        for(ProcessInfo processInfo : mSystemProcessInfos) {
            if(processInfo.packageName.equals(getPackageName())) {
                continue;
            }
            if(processInfo.isCheck) {
                killProcessInfos.add(processInfo);
            }
        }
        long totalReleaseSpace = 0;
        for(ProcessInfo processInfo : killProcessInfos) {
            if(mCustomProcessInfos.contains(processInfo)) {
                mCustomProcessInfos.remove(processInfo);
            }
            if(mSystemProcessInfos.contains(processInfo)) {
                mSystemProcessInfos.remove(processInfo);
            }
            //杀死进程
            ProcessInfoProvider.killProcess(this, processInfo);
            totalReleaseSpace += processInfo.memSize;
        if(mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        }
            //进程总数更新
            mProcessCount -= killProcessInfos.size();
            //更新可用剩余空间(释放空间+原有剩余空间 == 当前剩余空间)
            mAvailSpace += totalReleaseSpace;
            //11,根据进程总数和剩余空间大小
            mTvProcessAll.setText("进程总数:" + mProcessCount);
            mTvAvailSpaceAndAllSpace.setText("剩余/总共" + Formatter.formatFileSize(this, mAvailSpace) + "/" + mStrTotalSpace);

            ToastUtil.show(getApplicationContext(),
                    String.format("杀死了%d进程,释放了%s空间", killProcessInfos.size(), totalReleaseSpace));
        }
    }

    /**
     * 反选
     */
    private void selectReverse() {
        for(ProcessInfo processInfo : mCustomProcessInfos) {
            if(processInfo.packageName.equals(getPackageName())) {
                continue;
            }
            processInfo.isCheck = !processInfo.isCheck;
        }

        for(ProcessInfo processInfo : mSystemProcessInfos) {
            if(processInfo.packageName.equals(getPackageName())) {
                continue;
            }
            processInfo.isCheck = !processInfo.isCheck;
        }

        if(mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        }
    }



    /**
     * 全选
     */
    private void selectAll() {
        for(ProcessInfo processInfo : mCustomProcessInfos) {
            if(processInfo.packageName.equals(getPackageName())) {
                continue;
            }
            processInfo.isCheck = true;
        }

        for(ProcessInfo processInfo : mSystemProcessInfos) {
            if(processInfo.packageName.equals(getPackageName())) {
                continue;
            }
            processInfo.isCheck = true;
        }

        if(mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        }
    }
}
