package com.oscar.mobilesafe.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;

import com.oscar.mobilesafe.R;
import com.oscar.mobilesafe.adapter.GridViewAdapter;
import com.oscar.mobilesafe.utils.ConstentValue;
import com.oscar.mobilesafe.utils.Md5Util;
import com.oscar.mobilesafe.utils.SpUtil;
import com.oscar.mobilesafe.utils.ToastUtil;

public class HomeActivity extends AppCompatActivity {
    private GridView mGv;
    private GridViewAdapter mAdapter;

    private Context mContext;

    private String[] mTitles = {"手机防盗","通信卫士","软件管理","进程管理","流量统计","手机杀毒","缓存清理","高级工具","设置中心"};
    private int[] mIcons = {R.drawable.home_safe,R.drawable.home_callmsgsafe,
            R.drawable.home_apps,R.drawable.home_taskmanager,
            R.drawable.home_netmanager,R.drawable.home_trojan,
            R.drawable.home_sysoptimize,R.drawable.home_tools,R.drawable.home_settings};

    private Button mBtnSubmit;
    private Button mBtnCancel;
    private EditText mEtInputPwd;
    private EditText mEtConfirmPwd;




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

        //设置单个条目的点击事件
        mGv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        String savePwd = SpUtil.getString(mContext, ConstentValue.SET_PWD, "");
                        if(TextUtils.isEmpty(savePwd)) {
                            //设置密码
                            showSetPwdDialog();
                        } else {
                            //确认密码
                            showConfirmPwdDialog();
                        }
                        break;
                    case 8:
                        Intent intent = new Intent(mContext, SettingActivity.class);
                        startActivity(intent);
                        break;
                }


            }
        });
    }

    /**
     * 确认对话框
     */
    private void showConfirmPwdDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        final AlertDialog dialog = builder.create();
        View viewSetDialog = View.inflate(mContext, R.layout.view_confirm_dialog, null);
        dialog.setView(viewSetDialog);
        dialog.show();

        mBtnSubmit = (Button) viewSetDialog.findViewById(R.id.btn_submit);
        mBtnCancel = (Button) viewSetDialog.findViewById(R.id.btn_cancel);
        mEtConfirmPwd = (EditText) viewSetDialog.findViewById(R.id.et_confirm_pwd);

        mBtnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String confirmPwd = mEtConfirmPwd.getText().toString();
                if(!TextUtils.isEmpty(confirmPwd)) {
                    String savePwd = SpUtil.getString(mContext, ConstentValue.SET_PWD, "");
                    if(savePwd.equals(Md5Util.encoder(confirmPwd))) {
                        Intent intent = new Intent(mContext, SetOverActivity.class);
                        startActivity(intent);
                        dialog.dismiss();
                    } else {
                        ToastUtil.show(mContext, "保存密码和确认密码不一致");
                    }
                } else {
                    ToastUtil.show(mContext, "确认密码不能为空！");
                }
            }
        });

        mBtnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    /**
     * 显示设置密码对话框
     */
    private void showSetPwdDialog() {
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            final AlertDialog dialog = builder.create();
            View viewSetDialog = View.inflate(mContext, R.layout.view_set_dialog, null);
            dialog.setView(viewSetDialog);
            dialog.show();

            mBtnSubmit = (Button) viewSetDialog.findViewById(R.id.btn_submit);
            mBtnCancel = (Button) viewSetDialog.findViewById(R.id.btn_cancel);
            mEtInputPwd = (EditText) viewSetDialog.findViewById(R.id.et_input_pwd);
            mEtConfirmPwd = (EditText) viewSetDialog.findViewById(R.id.et_confirm_pwd);

            mBtnSubmit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String inputPwd = mEtInputPwd.getText().toString();
                    String confirmPwd = mEtConfirmPwd.getText().toString();
                    if(!TextUtils.isEmpty(inputPwd) && !TextUtils.isEmpty(confirmPwd)) {
                        if(inputPwd.equals(confirmPwd)) {
                            Intent intent = new Intent(mContext, SetOverActivity.class);
                            startActivity(intent);
                            dialog.dismiss();

                            SpUtil.putString(mContext, ConstentValue.SET_PWD, Md5Util.encoder(inputPwd));
                        } else {
                            ToastUtil.show(mContext, "输入密码和确认密码不一致");
                        }
                    } else {
                        ToastUtil.show(mContext, "输入密码和确认密码不能为空！");
                    }
                }
            });

            mBtnCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
    }

    private void initViews() {
        mGv = (GridView) findViewById(R.id.gv);

    }
}
