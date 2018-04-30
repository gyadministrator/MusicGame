package com.example.gy.musicgame;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import base.BaseActivity;
import bean.Music;
import butterknife.BindView;
import butterknife.ButterKnife;
import utils.Constant;
import utils.CurrentMusicUtils;
import utils.HttpUtils;
import utils.ImmersedStatusbarUtils;
import utils.MusicUtils;
import utils.NetWorkUtils;
import utils.TimeFormat;
import utils.ToastUtils;
import view.CircleImageView;
import view.ILrcBuilder;
import view.ILrcView;
import view.ILrcViewListener;
import view.MyPopWindow;
import view.impl.DefaultLrcBuilder;
import view.impl.LrcRow;

public class LrcActivity extends BaseActivity implements View.OnClickListener, SeekBar.OnSeekBarChangeListener {
    @BindView(R.id.lrc_song_name)
    TextView lrc_song_name;
    @BindView(R.id.lrc_singer)
    TextView lrc_singer;
    @BindView(R.id.lin)
    LinearLayout lin;
    @BindView(R.id.lin_bg)
    LinearLayout lin_bg;
    @BindView(R.id.lrcView)
    ILrcView mLrcView;
    @BindView(R.id.lrc_pre)
    TextView lrc_pre;
    @BindView(R.id.lrc_play)
    TextView lrc_play;
    @BindView(R.id.lrc_next)
    TextView lrc_next;
    @BindView(R.id.lrc_img)
    CircleImageView lrc_img;
    @BindView(R.id.music_start)
    TextView music_start;
    @BindView(R.id.music_end)
    TextView music_end;
    @BindView(R.id.seekBar)
    SeekBar seekBar;
    @BindView(R.id.lrc_back)
    TextView lrc_back;
    @BindView(R.id.lrc_more)
    TextView lrc_more;

    private String url = Constant.BASE_URL + "/music/GetLrc";
    //更新歌词的频率，每秒更新一次
    private int mPalyTimerDuration = 1000;
    //更新歌词的定时器
    private Timer mTimer;
    //更新歌词的定时任务
    private TimerTask mTask;
    private static List<String> playUrls = new ArrayList<>();

    private List<Music> list = null;

    private Music temp = null;

    private Intent intent = null;
    private String lrcContent = "";
    private int item_position = 0;

    private static boolean b = false;

    private SeekBarThread seekBarThread;


    private static final String TAG = "LrcActivity";

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {
                //解析歌词构造器
                ILrcBuilder builder = new DefaultLrcBuilder();
                //解析歌词返回LrcRow集合
                List<LrcRow> rows = builder.getLrcRows(lrcContent);
                //将得到的歌词集合传给mLrcView用来展示
                mLrcView.setLrc(rows);
            } else if (msg.what == 0) {
                //ToastUtils.showToast(LrcActivity.this, R.mipmap.music_warning, "发生了异常");
            } else if (msg.what == 3) {
                sendHttp(url, temp.getSong_id());
                MusicUtils.play(playUrls.get(0), LrcActivity.this);
                lrc_song_name.setText(temp.getTitle());
                lrc_singer.setText(temp.getAuthor());
                lrc_play.setBackgroundResource(R.mipmap.music_stop);

            } else if (msg.what == 4) {
                sendHttp(url, temp.getSong_id());
                MusicUtils.play(playUrls.get(0), LrcActivity.this);
                lrc_song_name.setText(temp.getTitle());
                lrc_singer.setText(temp.getAuthor());
                lrc_play.setBackgroundResource(R.mipmap.music_stop);
            } else if (msg.what == 20) {
                seekBar.setProgress(MusicUtils.mediaPlayer.getCurrentPosition());
                music_start.setText(TimeFormat.ShowTime(MusicUtils.mediaPlayer.getCurrentPosition()));
            }
        }
    };

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lrc);
        ButterKnife.bind(this);

        lrc_pre.setOnClickListener(this);
        lrc_play.setOnClickListener(this);
        lrc_next.setOnClickListener(this);
        lrc_back.setOnClickListener(this);
        lrc_more.setOnClickListener(this);


        /*设置沉侵式导航栏*/
        ImmersedStatusbarUtils.initAfterSetContentView(this, lin);

        intent = getIntent();

        item_position = intent.getIntExtra("position", 0);
        list = (List<Music>) intent.getSerializableExtra("list");
        lrc_song_name.setText(intent.getStringExtra("name"));
        lrc_singer.setText(intent.getStringExtra("singer"));

        if (NetWorkUtils.checkNetworkState(this)) {
            Picasso.with(this).load(intent.getStringExtra("url")).placeholder(R.mipmap.default_music).error(R.mipmap.default_music).into(lrc_img);
            sendHttp(url, intent.getStringExtra("songid"));
        } else {
            ToastUtils.showToast(this, R.mipmap.music_warning, "没有网络了...");
        }

        initLrc();

        initSeekBar();

    }

    private void initSeekBar() {
        music_start.setText(TimeFormat.ShowTime(0));
        music_end.setText(TimeFormat.ShowTime(MusicUtils.mediaPlayer.getDuration()));

        seekBar.setOnSeekBarChangeListener(this);
        seekBar.setMax(MusicUtils.mediaPlayer.getDuration());

        if (seekBarThread == null) {
            seekBarThread = new SeekBarThread();
            seekBarThread.start();
        } else {
            seekBarThread.interrupt();
            seekBarThread = null;
            seekBar.setProgress(0);
        }
    }

    private void initLrc() {
        if (mTimer == null) {
            mTimer = new Timer();
            mTask = new LrcTask();
            mTimer.scheduleAtFixedRate(mTask, 0, mPalyTimerDuration);
        }

        //歌曲播放完毕监听
        MusicUtils.mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            public void onCompletion(MediaPlayer mp) {
                stopLrcPlay();
            }
        });

        //设置自定义的LrcView上下拖动歌词时监听
        mLrcView.setListener(new ILrcViewListener() {
            //当歌词被用户上下拖动的时候回调该方法,从高亮的那一句歌词开始播放
            public void onLrcSeeked(int newPosition, LrcRow row) {
                if (MusicUtils.mediaPlayer != null) {
                    MusicUtils.mediaPlayer.seekTo((int) row.time);
                }
            }
        });
    }

    /**
     * 停止展示歌曲
     */
    public void stopLrcPlay() {
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
    }
    private void sendHttp(String url, String songid) {
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
        Map<String, Object> map = new HashMap<>();
        map.put("songid", songid);
        httpUtils.sendGetHttp(url, map);
    }

    private void parseJson(String s) {
        try {
            JSONObject jsonObject = new JSONObject(s);
            lrcContent = jsonObject.optString("lrcContent");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.lrc_pre:
                pre();
                break;
            case R.id.lrc_play:
                if (b) {
                    //播放
                    MusicUtils.pause();
                    lrc_play.setBackgroundResource(R.mipmap.music_play);
                    b = false;
                } else {
                    MusicUtils.playContinue();
                    lrc_play.setBackgroundResource(R.mipmap.music_stop);
                    b = true;

                    MusicUtils.mediaPlayer.stop();
                    seekBar.setProgress(0);
                }
                break;
            case R.id.lrc_next:
                next();
                break;
            case R.id.lrc_back:
                finish();
                break;
            case R.id.lrc_more:
                //打开popwindow
                MyPopWindow myPopWindow = new MyPopWindow(this);
                myPopWindow.showPopupWindow(lrc_more);
                break;
            default:
                break;
        }
    }

    @SuppressLint("Assert")
    private void pre() {
        initSeekBar();
        stopLrcPlay();
        if (b) {
            ToastUtils.showToast(this, R.mipmap.music_warning, "没有上一曲");
        } else {
            if (item_position - 1 < 0) {
                ToastUtils.showToast(this, R.mipmap.music_warning, "亲,已经是第一首了");
            } else {
                temp = list.get(item_position - 1);
                if (NetWorkUtils.checkNetworkState(this)) {
                    getPlayUrls(item_position - 1, 3);
                    item_position--;
                    initLrc();
                    if (list.size() > 0) {
                        Picasso.with(this).load(list.get(item_position).getPic_big()).placeholder(R.mipmap.default_music).error(R.mipmap.default_music).into(lrc_img);
                    }
                } else {
                    ToastUtils.showToast(this, R.mipmap.music_warning, "没有网络,无法播放上一首");
                }
            }
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
        music_start.setText(TimeFormat.ShowTime(i));
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        MusicUtils.mediaPlayer.seekTo(seekBar.getProgress());
    }

    /**
     * 展示歌曲的定时任务
     */
    class LrcTask extends TimerTask {
        @Override
        public void run() {
            //获取歌曲播放的位置
            if (MusicUtils.mediaPlayer != null) {
                final long timePassed = MusicUtils.mediaPlayer.getCurrentPosition();
                LrcActivity.this.runOnUiThread(new Runnable() {
                    public void run() {
                        //滚动歌词
                        mLrcView.seekLrcToTime(timePassed);
                    }
                });
            }
        }
    }

    // 自定义的线程
    class SeekBarThread extends Thread {

        @Override
        public void run() {
            while (MusicUtils.mediaPlayer.isPlaying()) {
                // 将SeekBar位置设置到当前播放位置
                handler.sendEmptyMessage(20);
                try {
                    // 每100毫秒更新一次位置
                    Thread.sleep(1000);
                    //播放进度
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void getPlayUrls(final int currentNum, final int what) {
        String songid = list.get(currentNum).getSong_id();
        Map<String, Object> map = new HashMap<>();
        map.put("songid", songid);
        final HttpUtils httpUtils = new HttpUtils(new HttpUtils.IHttpResponseListener() {
            @Override
            public void onSuccess(String json) {
                parseJsonUrl(json);
                handler.sendEmptyMessage(what);
            }

            @Override
            public void onFail(String error) {
                handler.sendEmptyMessage(0);
            }
        });
        String url = Constant.BASE_URL + "/music/PlaySong";
        httpUtils.sendGetHttp(url, map);
    }

    private void parseJsonUrl(String json) {
        try {
            JSONObject jsonObject = new JSONObject(json);
            JSONObject bitrate = jsonObject.optJSONObject("bitrate");
            String show_link = bitrate.optString("show_link");
            playUrls.clear();
            playUrls.add(show_link);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void next() {
        initSeekBar();
        stopLrcPlay();
        if (b) {
            ToastUtils.showToast(this, R.mipmap.music_warning, "没有下一曲");
        } else {
            if (item_position + 1 > list.size()) {
                ToastUtils.showToast(this, R.mipmap.music_warning, "亲,已经是最后一首了");
            } else {
                temp = list.get(item_position + 1);
                if (NetWorkUtils.checkNetworkState(this)) {
                    getPlayUrls(item_position + 1, 4);
                    item_position++;
                    if (list.size() > 0) {
                        Picasso.with(this).load(list.get(item_position).getPic_big()).placeholder(R.mipmap.default_music).error(R.mipmap.default_music).into(lrc_img);
                    }
                    initLrc();
                } else {
                    ToastUtils.showToast(this, R.mipmap.music_warning, "没有网络,无法播放下一首");
                }
            }
        }
    }
}
