package com.example.gy.musicgame;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.mob.MobSDK;

import base.BaseActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;
import utils.MobUtils;
import utils.PhoneUtils;
import utils.ToastUtils;

public class PhoneActivity extends BaseActivity implements View.OnClickListener {
    @BindView(R.id.phone)
    EditText phone;
    @BindView(R.id.code)
    EditText code;
    @BindView(R.id.getCode)
    Button getCode;
    @BindView(R.id.next)
    Button next;
    @BindView(R.id.back_login)
    TextView back_login;

    int i = 30; // 设置短信验证提示时间为30s


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone);
        ButterKnife.bind(this);

        phone.setOnClickListener(this);
        code.setOnClickListener(this);
        getCode.setOnClickListener(this);
        next.setOnClickListener(this);
        back_login.setOnClickListener(this);

        // 启动短信验证sdk
        MobSDK.init(this, MobUtils.APP_KEY, MobUtils.APP_SECRET);
        EventHandler eventHandler = new EventHandler() {
            @Override
            public void afterEvent(int event, int result, Object data) {
                Message msg = new Message();
                msg.arg1 = event;
                msg.arg2 = result;
                msg.obj = data;
                handler.sendMessage(msg);
            }
        };
        SMSSDK.registerEventHandler(eventHandler); // 注册回调监听接口


        code.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                next.setEnabled(true);
                next.setBackgroundColor(Color.GREEN);
            }
        });
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.getCode:
                if (PhoneUtils.checkPhone(phone.getText().toString())) {
                    SMSSDK.getVerificationCode("86", phone.getText().toString()); // 调用sdk发送短信验证
                    getCode.setClickable(false);// 设置按钮不可点击 显示倒计时
                    getCode.setText("重新发送(" + i + ")");
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            for (i = 30; i > 0; i--) {
                                handler.sendEmptyMessage(-9);
                                if (i <= 0) {
                                    break;
                                }
                                try {
                                    Thread.sleep(1000);// 线程休眠实现读秒功能
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                            handler.sendEmptyMessage(-8);// 在30秒后重新显示为获取验证码
                        }
                    }).start();
                } else {
                    ToastUtils.showToast(this, R.mipmap.music_warning, "手机号格式不合法");
                }
                break;
            case R.id.next:
                SMSSDK.submitVerificationCode("86", phone.getText().toString(), code.getText()
                        .toString());
                break;
            case R.id.back_login:
                finish();
                break;
        }
    }

    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == -9) {
                getCode.setText("重新发送(" + i + ")");
            } else if (msg.what == -8) {
                getCode.setText("获取验证码");
                getCode.setClickable(true); // 设置可点击
                i = 30;
            } else {
                int event = msg.arg1;
                int result = msg.arg2;
                Object data = msg.obj;
                if (result == SMSSDK.RESULT_COMPLETE) {
                    // 短信注册成功后，返回PhoneActivity,然后提示
                    if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {// 提交验证码成功
                        ToastUtils.showToast(PhoneActivity.this, R.mipmap.music_icon, "提交验证码成功");
                        // 验证成功后跳转主界面
                        Intent intent = new Intent(PhoneActivity.this, RegisterActivity.class);
                        intent.putExtra("phone", phone.getText().toString());
                        startActivity(intent);
                        finish();
                    } else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {
                        ToastUtils.showToast(PhoneActivity.this, R.mipmap.music_icon, "验证码已经发送");
                    } else {
                        ToastUtils.showToast(PhoneActivity.this, R.mipmap.music_warning, "验证码验证不成功");
                        ((Throwable) data).printStackTrace();
                    }
                }
            }
        }
    };

    @Override
    protected void onDestroy() {
        SMSSDK.unregisterAllEventHandler();
        super.onDestroy();
    }
}
