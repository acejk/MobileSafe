package com.oscar.mobilesafe.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.animation.AlphaAnimation;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.oscar.mobilesafe.R;
import com.oscar.mobilesafe.utils.ConstentValue;
import com.oscar.mobilesafe.utils.SpUtil;
import com.oscar.mobilesafe.utils.StreamUtils;
import com.oscar.mobilesafe.utils.ToastUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class SplashActivity extends AppCompatActivity {
    private static final int UPDATE_VERSION = 100;
    private static final int ENTER_HOME = 101;
    private static final int URL_ERROR = 102;
    private static final int IO_ERROR = 103;
    private static final int JSON_ERROR = 104;


    private TextView mTvVersion;
    private RelativeLayout mRlRoot;

    private Context mContext;

    private int mLocalVersionCode;
    private static String TAG = "SplashActivity";

    private String mVersionCode;//版本号
    private String mVersionDes;//版本描述
    private String mDownloadUrl;//更新版本下载地址



    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case UPDATE_VERSION:
                    showUpdateDialog();
                    break;
                case ENTER_HOME:
                    enterHome();
                    break;
                case URL_ERROR:
                    ToastUtil.show(mContext, "url异常");
                    break;
                case IO_ERROR:
                    ToastUtil.show(mContext, "读取流异常");
                    break;
                case JSON_ERROR:
                    ToastUtil.show(mContext, "json异常");
                    break;
            }
        }
    };

    /**
     * 进入主界面
     */
    private void enterHome() {
        Intent intent = new Intent(mContext, HomeActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     * 提示版本更新对话框
     */
    private void showUpdateDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setIcon(R.mipmap.ic_launcher);
        builder.setTitle("版本更新");
        builder.setMessage(mVersionDes);

        builder.setPositiveButton("立即更新", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                downloadApk();
            }
        });

        builder.setNegativeButton("稍后再说", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                enterHome();
            }
        });

        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                enterHome();
                dialog.dismiss();
            }
        });

        builder.show();
    }

    /**
     * 下载apk
     */
    private void downloadApk() {
        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            String sdcardPath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "mobilesafe.apk";
            HttpUtils httpUtils = new HttpUtils();
            httpUtils.download(mDownloadUrl, sdcardPath, new RequestCallBack<File>() {
                @Override
                public void onSuccess(ResponseInfo<File> responseInfo) {
                    Log.i(TAG, "下载成功");
                    File file = responseInfo.result;
                    installApk(file);
                }

                @Override
                public void onFailure(HttpException e, String s) {
                    Log.i(TAG, "下载失败");
                }

                @Override
                public void onStart() {
                    Log.i(TAG, "刚刚开始下载");
                }

                @Override
                public void onLoading(long total, long current, boolean isUploading) {
                    Log.i(TAG, "正在下载中....");
                }

            });
        }
    }

    /**
     * 安装apk
     */
    private void installApk(File file) {
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
		intent.setType("application/vnd.android.package-archive");
        intent.setDataAndType(Uri.fromFile(file),"application/vnd.android.package-archive");
        startActivityForResult(intent, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        enterHome();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //初始化控件
        initViews();
        //初始化数据
        initDatas();
        //初始化动画
        initAnimation();
    }

    private void initAnimation() {
        AlphaAnimation alphaAnimation = new AlphaAnimation(0, 1);
        alphaAnimation.setDuration(3000);
        mRlRoot.startAnimation(alphaAnimation);

    }

    private void initDatas() {
        mContext = this;

        mTvVersion.setText("版本名称：" + getVersionName());

        mLocalVersionCode = getLocalVersionCode();
        boolean isUpdate = SpUtil.getBoolean(mContext, ConstentValue.OPEN_UPDATE, false);
        if(isUpdate) {
            checkVersion();
        } else {
            mHandler.sendEmptyMessageDelayed(ENTER_HOME, 4000);
        }

    }

    /**
     * 检查版本更新
     */
    private void checkVersion() {
        final String urlStr = "http://192.168.0.10:8080/version.json";
        final Message msg = Message.obtain();
        new Thread(){
            @Override
            public void run() {
                //开始时间
                long startTime = System.currentTimeMillis();
                try {
                    URL url = new URL(urlStr);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setConnectTimeout(2000);
                    conn.setReadTimeout(2000);
                    conn.setRequestMethod("GET");
                    if(conn.getResponseCode() == 200) {
                        InputStream is = conn.getInputStream();
                        String jsonStr = StreamUtils.streamToString(is);

                        JSONObject jsonObject = new JSONObject(jsonStr);
                        String versionName = jsonObject.getString("versionName");
                        mVersionDes = jsonObject.getString("versionDes");
                        mVersionCode = jsonObject.getString("versionCode");
                        mDownloadUrl = jsonObject.getString("downloadUrl");

                        Log.i(TAG, versionName);
                        Log.i(TAG, mVersionCode);
                        Log.i(TAG, mVersionDes);
                        Log.i(TAG, mDownloadUrl);

                        if(mLocalVersionCode < Integer.parseInt(mVersionCode)) {
                            msg.what = UPDATE_VERSION;
                        } else {
                            msg.what = ENTER_HOME;
                        }
                    }
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                    msg.what = URL_ERROR;
                } catch (IOException e) {
                    e.printStackTrace();
                    msg.what = IO_ERROR;
                } catch (JSONException e) {
                    e.printStackTrace();
                    msg.what = JSON_ERROR;
                } finally {
                    //结束时间
                    long endTime = System.currentTimeMillis();
                    if(endTime - startTime < 4000) {
                        try {
                            Thread.sleep(4000 - (endTime - startTime));
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    mHandler.sendMessage(msg);
                }

            }
        }.start();
    }

    /**
     * 获取本地版本号
     * @return
     */
    private int getLocalVersionCode() {
        PackageManager pm = getPackageManager();
        try {
            PackageInfo packageInfo = pm.getPackageInfo(getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 获取版本名
     * @return
     */
    private String getVersionName() {
        PackageManager pm = getPackageManager();
        try {
            PackageInfo packageInfo = pm.getPackageInfo(getPackageName(), 0);
            return packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void initViews() {
        mTvVersion = (TextView) findViewById(R.id.tv_version);
        mRlRoot = (RelativeLayout) findViewById(R.id.rl_root);
    }
}
