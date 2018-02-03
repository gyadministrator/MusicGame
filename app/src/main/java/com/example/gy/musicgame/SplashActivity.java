package com.example.gy.musicgame;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.TextView;

import org.greenrobot.greendao.query.QueryBuilder;

import java.io.File;
import java.util.Calendar;
import java.util.List;

import application.AppCache;
import base.BaseActivity;
import bean.CurrentUser;
import bean.User;
import bean.dao.CurrentUserDao;
import bean.dao.DaoMaster;
import bean.dao.DaoSession;
import http.HttpCallback;
import http.HttpClient;
import model.Splash;
import service.EventCallback;
import service.PlayService;
import utils.NetWorkUtils;
import utils.other.FileUtils;
import utils.other.PermissionReq;
import utils.other.Preferences;
import utils.other.ToastUtils;
import utils.other.binding.Bind;

public class SplashActivity extends BaseActivity {
    private static final String SPLASH_FILE_NAME = "splash";

    @Bind(R.id.iv_splash)
    private ImageView ivSplash;
    @Bind(R.id.tv_copyright)
    private TextView tvCopyright;
    private ServiceConnection mPlayServiceConnection;

    private static CurrentUserDao userDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initDbHelp();
        setContentView(R.layout.activity_splash);

        int year = Calendar.getInstance().get(Calendar.YEAR);
        tvCopyright.setText(getString(R.string.copyright, year));

        checkService();
    }

    private void checkService() {
        if (AppCache.getPlayService() == null) {
            startService();
            showSplash();
            updateSplash();

            new  Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    bindService();
                }
            }, 1000);
        } else {
            //startMusicActivity();
            QueryBuilder<CurrentUser> qb = userDao.queryBuilder();
            List<CurrentUser> list = qb.where(CurrentUserDao.Properties.Id.eq(0)).list();
            if (list.size() == 0) {
                start(LoginActivity.class, list);
            } else {
                start(MainActivity.class, list);
            }
        }
    }

    private void startService() {
        Intent intent = new Intent(this, PlayService.class);
        startService(intent);
    }

    private void bindService() {
        Intent intent = new Intent();
        intent.setClass(this, PlayService.class);
        mPlayServiceConnection = new PlayServiceConnection();
        bindService(intent, mPlayServiceConnection, Context.BIND_AUTO_CREATE);
    }

    private class PlayServiceConnection implements ServiceConnection {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            final PlayService playService = ((PlayService.PlayBinder) service).getService();
            AppCache.setPlayService(playService);
            PermissionReq.with(SplashActivity.this)
                    .permissions(Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    .result(new PermissionReq.Result() {
                        @Override
                        public void onGranted() {
                            scanMusic(playService);
                        }

                        @Override
                        public void onDenied() {
                            ToastUtils.show(getString(R.string.no_permission, "存储空间", "扫描本地歌曲"));
                            finish();
                            playService.quit();
                        }
                    })
                    .request();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
        }
    }

    private void scanMusic(final PlayService playService) {
        playService.updateMusicList(new EventCallback<Void>() {
            @Override
            public void onEvent(Void aVoid) {
                startMusicActivity();
                finish();
            }
        });
    }

    private void showSplash() {
        File splashImg = new File(FileUtils.getSplashDir(this), SPLASH_FILE_NAME);
        if (splashImg.exists()) {
            Bitmap bitmap = BitmapFactory.decodeFile(splashImg.getPath());
            ivSplash.setImageBitmap(bitmap);
        }
    }

    private void updateSplash() {
        HttpClient.getSplash(new HttpCallback<Splash>() {
            @Override
            public void onSuccess(Splash response) {
                if (response == null || TextUtils.isEmpty(response.getUrl())) {
                    return;
                }

                final String url = response.getUrl();
                String lastImgUrl = Preferences.getSplashUrl();
                if (TextUtils.equals(lastImgUrl, url)) {
                    return;
                }

                HttpClient.downloadFile(url, FileUtils.getSplashDir(AppCache.getContext()), SPLASH_FILE_NAME,
                        new HttpCallback<File>() {
                            @Override
                            public void onSuccess(File file) {
                                Preferences.saveSplashUrl(url);
                            }

                            @Override
                            public void onFail(Exception e) {
                            }
                        });
            }

            @Override
            public void onFail(Exception e) {
            }
        });
    }

    private void startMusicActivity() {
        Intent intent = new Intent();
        intent.setClass(this, MainActivity.class);
        intent.putExtras(getIntent());
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
    }

    @Override
    protected void onDestroy() {
        if (mPlayServiceConnection != null) {
            unbindService(mPlayServiceConnection);
        }
        super.onDestroy();
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
                Intent intent = new Intent(SplashActivity.this, c);
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
