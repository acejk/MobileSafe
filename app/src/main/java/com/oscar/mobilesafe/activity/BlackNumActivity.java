package com.oscar.mobilesafe.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.oscar.mobilesafe.R;
import com.oscar.mobilesafe.dao.BlackNumDao;
import com.oscar.mobilesafe.model.BlackNumInfo;
import com.oscar.mobilesafe.utils.ToastUtil;

import java.util.List;

public class BlackNumActivity extends AppCompatActivity {
    private Button mBtnAdd;
    private ListView mLvBlackNum;

    private BlackNumDao mDao;

    private List<BlackNumInfo> mBlackNumList;

    private BlackNumAdapter mAdapter;

    private LayoutInflater mInflater;

    private int mode = 1;//默认选中短信

    private int mCount = 0;

    private boolean mIsLoad = false;

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(mAdapter == null) {
                mAdapter = new BlackNumAdapter();
                mLvBlackNum.setAdapter(mAdapter);
            } else {
                mAdapter.notifyDataSetChanged();
            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_black_num);

        initViews();

        initDatas();

        initEvents();
    }

    private void initEvents() {
        mBtnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //显示对话框
                showDialog();
            }
        });

        mLvBlackNum.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if(mBlackNumList != null) {
                    if(scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE
                            && mLvBlackNum.getLastVisiblePosition() >= mBlackNumList.size() - 1
                            && !mIsLoad) {
                        if(mCount > mBlackNumList.size()) {
                            new Thread(){
                                @Override
                                public void run() {
                                    mDao = BlackNumDao.getInstance(getApplicationContext());
                                    List<BlackNumInfo> moreData = mDao.findByIndex(mBlackNumList.size());
                                    mBlackNumList.addAll(moreData);
                                    mHandler.sendEmptyMessage(0);
                                }
                            }.start();
                        }
                    }
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });
    }

    /**
     * 显示添加对话框
     */
    private void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final AlertDialog dialog = builder.create();
        View view = View.inflate(getApplicationContext(), R.layout.dialog_add_blacknum, null);
        dialog.setView(view, 0, 0, 0, 0);

        final EditText etPhone = (EditText) view.findViewById(R.id.et_input_phone);
        RadioGroup rg = (RadioGroup) view.findViewById(R.id.rg);
        Button btnSubmit = (Button) view.findViewById(R.id.btn_submit);
        Button btnCancel = (Button) view.findViewById(R.id.btn_cancel);

        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rb_sms:
                        mode = 1;
                        break;
                    case R.id.rb_phone:
                        mode = 2;
                        break;
                    case R.id.rb_all:
                        mode = 3;
                        break;
                }
            }
        });


        //确认
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phone = etPhone.getText().toString();
                if(!TextUtils.isEmpty(phone)) {
                    mDao.insert(phone, mode + "");
                    BlackNumInfo blackNumInfo = new BlackNumInfo();
                    blackNumInfo.setPhone(phone);
                    blackNumInfo.setMode(mode + "");
                    mBlackNumList.add(0, blackNumInfo);
                    if(mAdapter != null) {
                        mAdapter.notifyDataSetChanged();
                    }
                    dialog.dismiss();
                } else {
                    ToastUtil.show(getApplicationContext(), "请输入拦截号码");
                }
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void initDatas() {
        mInflater = LayoutInflater.from(getApplicationContext());

        new Thread(){
            @Override
            public void run() {
                mDao = BlackNumDao.getInstance(getApplicationContext());
                mBlackNumList = mDao.findByIndex(0);
                mCount = mDao.getCount();
                mHandler.sendEmptyMessage(0);
            }
        }.start();


    }

    private void initViews() {
        mBtnAdd = (Button) findViewById(R.id.btn_add);
        mLvBlackNum = (ListView) findViewById(R.id.lv_blackNum);
    }

    /**
     * 黑名单适配器
     */
    class BlackNumAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mBlackNumList.size();
        }

        @Override
        public Object getItem(int position) {
            return mBlackNumList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder = null;
            if(convertView == null) {
                viewHolder = new ViewHolder();
                convertView = mInflater.inflate(R.layout.lv_blacknum, parent, false);
                viewHolder.tvPhone = (TextView) convertView.findViewById(R.id.tv_phone);
                viewHolder.tvMode = (TextView) convertView.findViewById(R.id.tv_mode);
                viewHolder.ivDelete = (ImageView) convertView.findViewById(R.id.iv_delete);

                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            //删除拦截号码
            viewHolder.ivDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //将数据插入到数据库
                    mDao.delete(mBlackNumList.get(position).getPhone());
                    //移除ListView数据
                    mBlackNumList.remove(position);
                    //更新ListView
                    if(mAdapter != null) {
                        mAdapter.notifyDataSetChanged();
                    }
                }
            });
            viewHolder.tvPhone.setText(mBlackNumList.get(position).getPhone());
            final int mode = Integer.parseInt(mBlackNumList.get(position).getMode());
            switch (mode) {
                case 1:
                    viewHolder.tvMode.setText("拦截短信");
                    break;
                case 2:
                    viewHolder.tvMode.setText("拦截电话");
                    break;
                case 3:
                    viewHolder.tvMode.setText("拦截所有");
                    break;
            }
            return convertView;
        }
    }

    static class ViewHolder {
        public TextView tvPhone;
        public TextView tvMode;
        public ImageView ivDelete;
    }
}
