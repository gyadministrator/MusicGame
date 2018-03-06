package com.example.gy.musicgame;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;

import com.awesome.api.AwesomeFSManager;
import com.awesome.api.listener.AwesomeSplashAdListener;

import base.BaseActivity;

public class SplashActivity extends BaseActivity {
    private final String APPKEY = "cd7a3678e92f33f1affad511486f68da";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);


        AwesomeFSManager.getInstance().init(this, APPKEY);
        SharedPreferences shared = getSharedPreferences("test", MODE_PRIVATE);
        //首次执行预加载，不显示广告
        if (!TextUtils.isEmpty(shared.getString("first", ""))) {
            //开屏广告自动消失模式（此方法默认广告请求超时时间是5000ms,如果你想自定义广告请求超时时间（请调用三个参数的方法(FullScreenManager.loadAdButtonStyle(arg0, arg1, arg2);)，arg2单位是ms毫秒））
            AwesomeFSManager.getInstance().loadAdSplashStyle(this, AwesomeFSManager.COUNTDOWN_STYLE, new AwesomeSplashAdListener() {

                @Override
                public void onSplashPresent() {
                    // TODO Auto-generated method stub
                    //广告开始加载前
                    Log.d("tag", "onSplashPresent()");
                }

                @Override
                public void onSplashLoadFailed() {
                    // TODO Auto-generated method stub
                    //广告加载失败，
                    Log.d("tag", "onSplashLoadFailed()");
                    startActivity(new Intent(SplashActivity.this, StartActivity.class));
                    finish();
                }

                @Override
                public void onSplashDismiss() {
                    // TODO Auto-generated method stub
                    Log.d("tag", "onSplashDismiss()");
                    //广告展示成功
                    startActivity(new Intent(SplashActivity.this, StartActivity.class));
                    finish();
                }
            }, 5 * 1000, true);
        } else {
            shared.edit().putString("first", "one").apply();
            //FullScreenManager.preLoadAdList(this);
            startActivity(new Intent(SplashActivity.this, StartActivity.class));
            finish();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
