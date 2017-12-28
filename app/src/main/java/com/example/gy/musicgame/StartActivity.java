package com.example.gy.musicgame;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import org.greenrobot.greendao.query.QueryBuilder;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import base.BaseActivity;
import bean.CurrentUser;
import bean.User;
import bean.dao.CurrentUserDao;
import bean.dao.DaoMaster;
import bean.dao.DaoSession;
import butterknife.BindView;
import butterknife.ButterKnife;
import utils.HttpUtils;
import utils.NetWorkUtils;
import utils.ToastUtils;

public class StartActivity extends BaseActivity {
    @BindView(R.id.start_image)
    ImageView imageView;
    private static final String URL = "http://cn.bing.com/HPImageArchive.aspx";
    private static final String BASE_URL = "http://cn.bing.com";
    private static Map<String, Object> map = new HashMap<>();
    private static CurrentUserDao userDao;
    private static final String TAG = "StartActivity";
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {
                String url = parseJson(msg.obj.toString());
                Picasso.with(StartActivity.this).load(url).resize(900, 1550).into(imageView);
            } else if (msg.what == 0) {
                ToastUtils.showToast(StartActivity.this, R.mipmap.music_icon, "发生了错误");
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        initDbHelp();
        ButterKnife.bind(this);

        if (NetWorkUtils.checkNetworkState(this)) {
            send();
        } else {
            ToastUtils.showToast(this, R.mipmap.music_warning, "无网络连接");
        }
        QueryBuilder qb = userDao.queryBuilder();
        List<CurrentUser> list = qb.where(CurrentUserDao.Properties.Id.eq(0)).list();
        if (list.size() == 0) {
            start(LoginActivity.class, list);
        } else {
            start(MainActivity.class, list);
        }
    }

    private void initDbHelp() {
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, "recluse-db", null);
        SQLiteDatabase db = helper.getWritableDatabase();
        DaoMaster daoMaster = new DaoMaster(db);
        DaoSession daoSession = daoMaster.newSession();
        userDao = daoSession.getCurrentUserDao();
    }

    private void start(final Class c, final List<CurrentUser> list) {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(StartActivity.this, c);
                if (list.size() > 0) {
                    Bundle bundle = new Bundle();
                    User user = new User();
                    user.setUsername(list.get(0).getUsername());
                    user.setPassword(list.get(0).getPassword());
                    bundle.putSerializable("user", user);
                    intent.putExtra("user", bundle);
                }
                startActivity(intent);
                finish();
            }
        }, 2000);
    }

    private void send() {
        HttpUtils httpUtils = new HttpUtils(new HttpUtils.IHttpResponseListener() {
            @Override
            public void onSuccess(String json) {
                Message message = Message.obtain();
                message.obj = json;
                message.what = 1;
                mHandler.sendMessage(message);
            }

            @Override
            public void onFail(String error) {
                mHandler.sendEmptyMessage(0);
            }
        });
        map.put("format", "js");
        map.put("idx", "0");
        map.put("n", "1");
        httpUtils.sendGetHttp(URL, map);
    }

    private String parseJson(String s) {
        String imageUrl = "";
        try {
            JSONObject jsonObject = new JSONObject(s);
            JSONArray images = jsonObject.optJSONArray("images");
            JSONObject o = (JSONObject) images.get(0);
            String url = o.optString("url");
            imageUrl = BASE_URL + url;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return imageUrl;
    }
}
