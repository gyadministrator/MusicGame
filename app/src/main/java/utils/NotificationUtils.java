package utils;

import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.app.NotificationCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.TextView;

import com.example.gy.musicgame.R;
import com.squareup.picasso.Picasso;

import bean.RecommendMusic;
import view.CircleImageView;

/**
 * Created by Administrator on 2018/3/28.
 */

public class NotificationUtils {
    public static void sendNotification(RecommendMusic music, Context context) {
        //获取NotificationManager实例
        NotificationManager notifyManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        //实例化NotificationCompat.Builde并设置相关属性
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        //设置小图标
        RemoteViews view = new RemoteViews(context.getPackageName(), R.layout.back_view);
        View back_view = LayoutInflater.from(context).inflate(R.layout.back_view, null);
        CircleImageView back_music_img = (CircleImageView) back_view.findViewById(R.id.back_music_img);
        TextView back_music_title = (TextView) back_view.findViewById(R.id.back_music_title);
        TextView back_music_singer = (TextView) back_view.findViewById(R.id.back_music_singer);
        TextView back_music_play = (TextView) back_view.findViewById(R.id.back_music_play);
        TextView back_music_next= (TextView) back_view.findViewById(R.id.back_music_next);
        TextView back_music_close= (TextView) back_view.findViewById(R.id.back_music_close);
        Picasso.with(context).load(music.getPic_small()).error(R.mipmap.music_warning).placeholder(R.mipmap.music_icon).into(back_music_img);
        back_music_title.setText(music.getTitle());
        back_music_singer.setText(music.getAuthor());
        if (MusicUtils.mediaPlayer.isPlaying()){
            //后台显示播放
            back_music_play.setBackgroundResource(R.mipmap.music_stop);
        }else {
            back_music_play.setBackgroundResource(R.mipmap.music_play);
        }
        builder.setContent(view);
        //设置通知标题
        //builder.setContentTitle("最简单的Notification");
        //设置通知内容
        //builder.setContentText("只有小图标、标题、内容");
        //设置通知时间，默认为系统发出通知的时间，通常不用设置
        //.setWhen(System.currentTimeMillis());
        //通过builder.build()方法生成Notification对象,并发送通知,id=1
        notifyManager.notify(1, builder.build());
    }
}
