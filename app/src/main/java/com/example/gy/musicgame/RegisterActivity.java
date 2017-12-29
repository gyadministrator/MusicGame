package com.example.gy.musicgame;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import base.BaseActivity;
import bean.User;
import butterknife.BindView;
import butterknife.ButterKnife;
import utils.Constant;
import utils.DialogUtils;
import utils.HttpUtils;
import utils.NetWorkUtils;
import utils.ToastUtils;

public class RegisterActivity extends BaseActivity {
    @BindView(R.id.user)
    EditText user;
    @BindView(R.id.password)
    EditText password;
    @BindView(R.id.repassword)
    EditText repassword;
    @BindView(R.id.register)
    Button register;
    @BindView(R.id.back_login)
    TextView back_login;
    private static final String URL = Constant.BASE_URL + "/user/register";
    private static final String url = Constant.BASE_URL + "/user/checkUser";
    private static boolean flag = false;
    private static String message = "";
    private static final String TAG = "RegisterActivity";
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {
                DialogUtils.hidden();
                if (flag) {
                    ToastUtils.showToast(RegisterActivity.this, R.mipmap.music_icon, "注册成功");
                    finish();
                } else {
                    ToastUtils.showToast(RegisterActivity.this, R.mipmap.music_icon, "注册失败");
                }
            } else if (msg.what == 0) {
                DialogUtils.hidden();
                ToastUtils.showToast(RegisterActivity.this, R.mipmap.music_icon, "发生了错误");
            } else if (msg.what == 3) {
                DialogUtils.hidden();
                if (!"用户名已存在".equals(message)) {
                    register.setEnabled(true);
                    register.setBackgroundColor(Color.GREEN);
                }
                ToastUtils.showToast(RegisterActivity.this, R.mipmap.music_icon, message);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);
        back_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        user.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    if (NetWorkUtils.checkNetworkState(RegisterActivity.this)) {
                        if (user.getText().toString().equals("")) {
                            ToastUtils.showToast(RegisterActivity.this, R.mipmap.music_icon, "请输入用户名");
                        } else {
                            Map<String, Object> params = new HashMap<>();
                            params.put("username", user.getText().toString());
                            sendCheckuser(url, params);
                        }
                    } else {
                        ToastUtils.showToast(RegisterActivity.this, R.mipmap.music_icon, "无网络连接");
                    }
                }
            }
        });
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (user.getText().toString().equals("") || password.getText().toString().equals("") || repassword.getText().toString().equals("")) {
                    ToastUtils.showToast(RegisterActivity.this, R.mipmap.music_icon, "请输入信息");
                } else {
                    if (!password.getText().toString().equals(repassword.getText().toString())) {
                        ToastUtils.showToast(RegisterActivity.this, R.mipmap.music_warning, "两次密码不一致");
                    } else {
                        if (NetWorkUtils.checkNetworkState(RegisterActivity.this)) {
                            String phone = getIntent().getStringExtra("phone");
                            Map<String, Object> params = new HashMap<>();
                            params.put("username", user.getText().toString());
                            params.put("password", password.getText().toString());
                            params.put("phone", phone);
                            send(URL, params);
                        } else {
                            ToastUtils.showToast(RegisterActivity.this, R.mipmap.music_icon, "无网络连接");
                        }
                    }
                }
            }
        });
    }

    private void sendCheckuser(String url, Map<String, Object> params) {
        DialogUtils.show(this);
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
            JSONObject response = jsonObject.optJSONObject("response");
            message = response.optString("message");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void send(String url, Map<String, Object> map) {
        DialogUtils.show(this);
        HttpUtils httpUtils = new HttpUtils(new HttpUtils.IHttpResponseListener() {
            @Override
            public void onSuccess(String json) {
                parseJson(json);
                handler.sendEmptyMessage(1);
            }

            @Override
            public void onFail(String error) {
                handler.sendEmptyMessage(0);
            }
        });
        httpUtils.sendGetHttp(url, map);
    }

    private void parseJson(String json) {
        try {
            JSONObject jsonObject = new JSONObject(json);
            int code = jsonObject.optInt("code");
            if (code == 100) {
                JSONObject response = jsonObject.optJSONObject("response");
                flag = response.optBoolean("flag");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
