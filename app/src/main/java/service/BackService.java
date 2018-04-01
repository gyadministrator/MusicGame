package service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import bean.RecommendMusic;
import utils.MusicUtils;

/**
 * Created by Administrator on 2018/3/28.
 */

public class BackService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        RecommendMusic music= (RecommendMusic) intent.getBundleExtra("currentMusic").getSerializable("currentMusic");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }
}
