package utils;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.support.v4.app.NotificationCompat;
import com.example.gy.musicgame.R;
import bean.Music;

/**
 * Created by Administrator on 2018/3/28.
 */

public class NotificationUtils {
    private static NotificationManager notificationManager;

    @SuppressLint("ObsoleteSdkInt")
    public static void showNotification(Context context, Music music) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setSmallIcon(R.mipmap.index_img);
        builder.setContentTitle(music.getTitle());
        builder.setContentText(music.getAuthor());
        // 需要VIBRATE权限
        builder.setPriority(Notification.PRIORITY_DEFAULT);
        builder.setOngoing(true);
        notificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        assert notificationManager != null;
        notificationManager.notify(100, builder.build());
    }

    public static void closeNotification() {
        if (notificationManager != null) {
            notificationManager.cancel(100);
        }
    }
}
