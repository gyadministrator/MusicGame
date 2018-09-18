package service;

import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.os.Process;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;

import utils.Constant;
import utils.MusicUtils;
import utils.NotificationUtils;

public class BackService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String action = intent.getAction();
        if (Constant.CANCEL.equals(action)) {
            NotificationUtils.closeNotification();
        }
        return super.onStartCommand(intent, flags, startId);
    }
}
