package com.example.gy.musicgame;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;

import org.greenrobot.greendao.query.QueryBuilder;

import java.util.List;

import base.BaseActivity;
import bean.CurrentUser;
import bean.User;
import bean.dao.CurrentUserDao;
import bean.dao.DaoMaster;
import bean.dao.DaoSession;
import utils.NetWorkUtils;
import utils.ToastUtils;

public class StartActivity extends BaseActivity {
    private static CurrentUserDao userDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        initDbHelp();

        QueryBuilder<CurrentUser> qb = userDao.queryBuilder();
        List<CurrentUser> list = qb.where(CurrentUserDao.Properties.Id.eq(0)).list();

        if (!NetWorkUtils.checkNetworkState(this)) {
            ToastUtils.showToast(this, R.mipmap.music_warning, "无网络连接");
        }
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
        new Handler().postDelayed(new Runnable() {
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
}
