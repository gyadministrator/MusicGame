package utils;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.widget.RemoteViews;

import com.example.gy.musicgame.LrcActivity;
import com.example.gy.musicgame.R;

import bean.Music;

import static android.app.Notification.VISIBILITY_SECRET;

/**
 * Created by Administrator on 2018/3/28.
 */

public class NotificationUtils {
    private static NotificationManager notificationManager;

    private static NotificationManager getManager(Context context) {
        if (notificationManager == null)
            notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        return notificationManager;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static void sendNormalNotification(Context context) {
        Notification.Builder builder = getNotificationBuilder(context);
        getManager(context).notify(1, builder.build());
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static void sendCustomNotification(Context context, Music music) {
        Notification.Builder builder = getNotificationBuilder(context);
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.back_view);

        remoteViews.setImageViewUri(R.id.back_music_img, Uri.parse(music.getPic_big()));
        remoteViews.setTextViewText(R.id.back_music_title, music.getTitle());
        remoteViews.setTextViewText(R.id.back_music_singer, music.getAuthor());
        //PendingIntent intent = PendingIntent.getActivity(context, -1, new Intent(context, LrcActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);
        //remoteViews.setOnClickPendingIntent(R.id.back_music_play, intent);
        Intent stopIntent = new Intent();
        stopIntent.setAction(Constant.STOP);
        PendingIntent stopbroadcast = PendingIntent.getBroadcast(context, 0, stopIntent, 0);
        remoteViews.setOnClickPendingIntent(R.id.back_music_play, stopbroadcast);
        remoteViews.setTextViewCompoundDrawables(R.mipmap.music_play, 0, 0, 0, 0);

        Intent cancelIntent = new Intent();
        cancelIntent.setAction(Constant.CANCEL);
        PendingIntent cancalbroadcast = PendingIntent.getBroadcast(context, 0, cancelIntent, 0);
        remoteViews.setOnClickPendingIntent(R.id.back_music_close, cancalbroadcast);
        builder.setCustomContentView(remoteViews);
        Notification notification = builder.build();
        notification.flags = Notification.FLAG_ONGOING_EVENT;
        getManager(context).notify(1, notification);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private static Notification.Builder getNotificationBuilder(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("channel_id", "channel_name", NotificationManager.IMPORTANCE_DEFAULT);
            channel.canBypassDnd();//是否绕过请勿打扰模式
            channel.enableLights(true);//闪光
            channel.setLockscreenVisibility(VISIBILITY_SECRET);//锁屏显示通知
            channel.setLightColor(Color.RED);//指定闪光时灯光的颜色
            channel.canShowBadge();//桌面launcher消息角标
            //channel.enableVibration(true);//是否允许震动
            //channel.getAudioAttributes();//获取系统通知响铃声音的配置
            channel.getGroup();//获取通知渠道组
            channel.setBypassDnd(true);//设置可以绕过打扰模式
            //channel.setVibrationPattern(new long[]{100, 100, 200});//震动的模式
            channel.shouldShowLights();//是否会出灯光

            getManager(context).createNotificationChannel(channel);
        }

        Notification.Builder builder = new Notification.Builder(context);
        //builder.setAutoCancel(false);
        builder.setOngoing(true);
        builder.setChannelId("channel_id");
        /*builder.setContentTitle("新消息来了");
        builder.setContentText("明天不上班");*/
        builder.setSmallIcon(R.mipmap.icon);
        return builder;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @SuppressLint("ObsoleteSdkInt")
    public static void showNotification(Context context, Music music) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setSmallIcon(R.mipmap.index_img);
        builder.setContentTitle(music.getTitle());
        builder.setContentText(music.getAuthor());
        // 需要VIBRATE权限
        builder.setPriority(Notification.PRIORITY_DEFAULT);
        builder.setOngoing(true);
        NotificationChannel channel = new NotificationChannel("1", "channel", NotificationManager.IMPORTANCE_DEFAULT);
        channel.enableLights(true);//是否在桌面icon右上角展示小红点
        channel.setLightColor(Color.GREEN);//小红点颜色
        channel.setShowBadge(true);//是否在久按桌面图标时显示此渠道的通知
        notificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        assert notificationManager != null;
        notificationManager.createNotificationChannel(channel);
        assert notificationManager != null;
        notificationManager.notify(100, builder.build());
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static void closeNotification() {
        if (notificationManager != null) {
            notificationManager.deleteNotificationChannel("channel");
            notificationManager.cancel(100);
        }
    }
}
