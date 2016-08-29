package com.oscar.mobilesafe.activity;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.oscar.mobilesafe.R;
import com.oscar.mobilesafe.utils.StreamUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    private TextView mTvVersion;

    private int mLocalVersionCode;
    private static String TAG = "MainActivity";

    private String mVersionCode;//版本号
    private String mVersionDes;//版本描述
    private String mDownloadUrl;//更新版本下载地址

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();

        initDatas();
    }

    private void initDatas() {
        mTvVersion.setText("版本号：" + getVersionName());

        mLocalVersionCode = getLocalVersionCode();

        checkVersion();
    }

    private void checkVersion() {
        final String urlStr = "http://192.168.0.10:8080/version.json";
        new Thread(){
            @Override
            public void run() {
                try {
                    URL url = new URL(urlStr);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setConnectTimeout(1000 * 10);
                    conn.setReadTimeout(1000 * 10);
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
                    }
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }.start();
    }

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
    }
}
