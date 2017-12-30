package com.example.gy.musicgame;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import bean.CurrentUser;
import bean.User;
import bean.dao.CurrentUserDao;
import bean.dao.DaoMaster;
import bean.dao.DaoSession;
import butterknife.BindView;
import butterknife.ButterKnife;
import utils.Constant;
import utils.DialogUtils;
import utils.HttpUtils;
import utils.NetWorkUtils;
import utils.PhoneUtils;
import utils.ToastUtils;

public class LoginActivity extends BaseActivity {
    @BindView(R.id.tex_register)
    TextView tex_register;
    @BindView(R.id.user)
    EditText user;
    @BindView(R.id.password)
    EditText password;
    @BindView(R.id.login)
    Button login;
    private static CurrentUserDao userDao;
    private static final String TAG = "LoginActivity";
    private static final String URL = Constant.BASE_URL + "/user/login";
    private static User u = null;
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {
                DialogUtils.hidden();
                if (u != null) {
                    //登录成功
                    //将用户数据保存到数据库中
                    saveUser(u);
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("user", u);
                    intent.putExtra("user", bundle);
                    startActivity(intent);
                    finish();
                } else {
                    ToastUtils.showToast(LoginActivity.this, R.mipmap.music_icon, "用户名或密码错误");
                }

            } else if (msg.what == 0) {
                DialogUtils.hidden();
                ToastUtils.showToast(LoginActivity.this, R.mipmap.music_icon, "发生了错误");
            }
        }
    };

    private void saveUser(User u) {
        userDao.deleteAll();
        userDao.insert(new CurrentUser(0, u.getUsername(), u.getPassword()));
        Log.e(TAG, "saveUser:插入成功 ");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initDbHelp();
        ButterKnife.bind(this);
        tex_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, PhoneActivity.class);
                startActivity(intent);
            }
        });
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (user.getText().toString().equals("") || password.getText().toString().equals("")) {
                    ToastUtils.showToast(LoginActivity.this, R.mipmap.music_icon, "请输入信息");
                } else {
                    if (NetWorkUtils.checkNetworkState(LoginActivity.this)) {
                        Map<String, Object> params = new HashMap<>();
                        if (PhoneUtils.checkPhone(user.getText().toString())) {
                            params.put("phone", user.getText().toString());
                        } else {
                            params.put("username", user.getText().toString());
                        }
                        params.put("password", password.getText().toString());
                        send(URL, params);
                    } else {
                        ToastUtils.showToast(LoginActivity.this, R.mipmap.music_warning, "无网络连接");
                    }
                }
            }
        });
    }

    private void initDbHelp() {
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, "recluse-db", null);
        SQLiteDatabase db = helper.getWritableDatabase();
        DaoMaster daoMaster = new DaoMaster(db);
        DaoSession daoSession = daoMaster.newSession();
        userDao = daoSession.getCurrentUserDao();
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
                JSONObject user = response.optJSONObject("user");
                Gson gson = new Gson();
                u = gson.fromJson(user.toString(), User.class);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
