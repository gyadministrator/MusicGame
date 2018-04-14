package com.example.gy.musicgame;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.squareup.picasso.Picasso;

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
import utils.ImmersedStatusbarUtils;
import utils.MD5;
import utils.NetWorkUtils;
import utils.ToastUtils;
import view.CircleImageView;

public class ChangeActivity extends BaseActivity {
    @BindView(R.id.change_user)
    CircleImageView change_user;
    @BindView(R.id.user)
    EditText user;
    @BindView(R.id.old_password)
    EditText old_password;
    @BindView(R.id.new_password)
    EditText new_password;
    @BindView(R.id.change)
    Button change;
    @BindView(R.id.back)
    TextView back;
    private User u;
    @BindView(R.id.lin)
    LinearLayout lin;

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {
                DialogUtils.hidden();
                if (u != null) {
                    ToastUtils.showToast(ChangeActivity.this, R.mipmap.music_icon, "修改成功,请重新登录");
                    Intent intent = new Intent(ChangeActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    ToastUtils.showToast(ChangeActivity.this, R.mipmap.music_warning, "修改失败,用户名已存在");
                }
            } else if (msg.what == 0) {
                DialogUtils.hidden();
                ToastUtils.showToast(ChangeActivity.this, R.mipmap.music_icon, "发生了错误");
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change);
        ButterKnife.bind(this);

        /*设置沉侵式导航栏*/
        ImmersedStatusbarUtils.initAfterSetContentView(this, lin);

        u = (User) getIntent().getBundleExtra("user").getSerializable("user");
        user.setText(u.getUsername());

        if (NetWorkUtils.checkNetworkState(ChangeActivity.this)) {
            if (u.getImage() == null) {
                Picasso.with(this).load(Constant.BASE_URL + "/upload/default.png").into(change_user);
            } else {
                Picasso.with(this).load(u.getImage()).placeholder(R.mipmap.default_user).error(R.mipmap.default_user).resize(120, 120).into(change_user);
            }
        } else {
            ToastUtils.showToast(ChangeActivity.this, R.mipmap.music_warning, "无网络连接");
        }

        change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (old_password.getText().toString().equals("") || new_password.getText().toString().equals("")) {
                    ToastUtils.showToast(ChangeActivity.this, R.mipmap.music_icon, "请输入信息");
                } else {
                    if (old_password.getText().toString().equals(new_password.getText().toString())) {
                        ToastUtils.showToast(ChangeActivity.this, R.mipmap.music_icon, "新密码不能和原密码相同");
                    } else {
                        String md5Pwd = new MD5().getMD5ofStr(old_password.getText().toString());
                        if (md5Pwd.equals(u.getPassword())) {
                            //更改用户密码
                            if (NetWorkUtils.checkNetworkState(ChangeActivity.this)) {
                                String update_url = Constant.BASE_URL + "/user/changePwd";
                                Map<String, Object> params = new HashMap<>();
                                params.put("username", u.getUsername());
                                params.put("password", new_password.getText().toString());
                                sendUpdate(update_url, params);
                            } else {
                                ToastUtils.showToast(ChangeActivity.this, R.mipmap.music_warning, "无网络连接");
                            }
                        } else {
                            ToastUtils.showToast(ChangeActivity.this, R.mipmap.music_warning, "你输入的原密码不正确");
                        }
                    }
                }
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void sendUpdate(String update_url, Map<String, Object> params) {
        DialogUtils.show(this);
        HttpUtils httpUtils = new HttpUtils(new HttpUtils.IHttpResponseListener() {
            @Override
            public void onSuccess(String json) {
                parseUpdateJson(json);
                mHandler.sendEmptyMessage(1);
            }

            @Override
            public void onFail(String error) {
                mHandler.sendEmptyMessage(0);
            }
        });
        httpUtils.sendGetHttp(update_url, params);
    }

    private void parseUpdateJson(String json) {
        try {
            JSONObject jsonObject = new JSONObject(json);
            int code = jsonObject.optInt("code");
            if (code == 100) {
                JSONObject response = jsonObject.optJSONObject("response");
                u = JSON.parseObject(response.optJSONObject("user").toString(), User.class);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
