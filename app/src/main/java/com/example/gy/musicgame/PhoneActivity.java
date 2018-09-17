package com.example.gy.musicgame;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.SyncStateContract;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.mob.MobSDK;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import base.BaseActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;
import utils.Constant;
import utils.DialogUtils;
import utils.HttpUtils;
import utils.MobUtils;
import utils.NetWorkUtils;
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

    private String url = Constant.BASE_URL + "/user/queryUserByPhone";

    int codeNum;

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
                next.setBackgroundColor(Color.rgb(216, 30, 6));
            }
        });
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.getCode:
                if (PhoneUtils.checkPhone(phone.getText().toString())) {
                    Map<String, Object> params = new HashMap<>();
                    params.put("phone", phone.getText().toString());
                    sendCheckuser(url, params);
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

    @SuppressLint("SetTextI18n")
    private void getCode() {
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
    }

    private void sendCheckuser(String url, Map<String, Object> params) {
        DialogUtils.show(this, "检查用户中...");
        HttpUtils httpUtils = new HttpUtils(new HttpUtils.IHttpResponseListener() {
            @Override
            public void onSuccess(String json) {
                parseCheckJson(json);
                handler.sendEmptyMessage(3);
            }

            @Override
            public void onFail(String error) {
                handler.sendEmptyMessage(0);
            }
        });
        httpUtils.sendGetHttp(url, params);
    }

    private void parseCheckJson(String json) {
        try {
            JSONObject jsonObject = new JSONObject(json);
            codeNum = jsonObject.optInt("code");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        @SuppressLint("SetTextI18n")
        public void handleMessage(Message msg) {
            if (msg.what == 3) {
                DialogUtils.hidden();
                if (codeNum == 200) {
                    ToastUtils.showToast(PhoneActivity.this, R.mipmap.music_warning, "此手机已经被注册了");
                } else {
                    getCode();
                }
            }
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
                        ToastUtils.showToast(PhoneActivity.this, R.mipmap.music_icon, "验证码效验成功");
                        // 验证成功后跳转主界面
                        Intent intent = new Intent(PhoneActivity.this, RegisterActivity.class);
                        intent.putExtra("phone", phone.getText().toString());
                        startActivity(intent);
                        finish();
                    } else {
                        ToastUtils.showToast(PhoneActivity.this, R.mipmap.music_warning, "验证码效验失败");
                    }
                    if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {
                        ToastUtils.showToast(PhoneActivity.this, R.mipmap.music_icon, "验证码已经发送");
                    } else {
                        ToastUtils.showToast(PhoneActivity.this, R.mipmap.music_warning, "验证码发送失败");
                        ((Throwable) data).printStackTrace();
                    }
                } else {
                    ToastUtils.showToast(PhoneActivity.this, R.mipmap.music_warning, "验证码发送失败");
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
