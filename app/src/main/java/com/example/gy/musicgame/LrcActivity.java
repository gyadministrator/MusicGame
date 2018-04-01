package com.example.gy.musicgame;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
import utils.HttpUtils;
import utils.ImmersedStatusbarUtils;
import utils.MusicUtils;
import utils.NetWorkUtils;
import utils.ToastUtils;
import view.CircleImageView;
import view.ILrcBuilder;
import view.ILrcView;
import view.ILrcViewListener;
import view.impl.DefaultLrcBuilder;
import view.impl.LrcRow;

public class LrcActivity extends BaseActivity implements View.OnClickListener {
    @BindView(R.id.lrc_song_name)
    TextView lrc_song_name;
    @BindView(R.id.lrc_singer)
    TextView lrc_singer;
    @BindView(R.id.lrc_img)
    CircleImageView lrc_img;
    @BindView(R.id.lin)
    LinearLayout lin;
    @BindView(R.id.lin_bg)
    LinearLayout lin_bg;
    @BindView(R.id.lrcView)
    ILrcView mLrcView;
    @BindView(R.id.seekBar)
    SeekBar seekBar;
    @BindView(R.id.lrc_pre)
    TextView lrc_pre;
    @BindView(R.id.lrc_play)
    TextView lrc_play;
    @BindView(R.id.lrc_next)
    TextView lrc_next;
    @BindView(R.id.lrc_start)
    TextView lrc_start;
    @BindView(R.id.lrc_end)
    TextView lrc_end;

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
    private int duration = 0;
    private int item_position = 0;

    private static boolean b = false;

    private MyThread myThread;

    private static final String TAG = "LrcActivity";

    private boolean isChanging;

    private Thread thread;

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
                ToastUtils.showToast(LrcActivity.this, R.mipmap.music_warning, "发生了异常");
            } else if (msg.what == 3) {
                sendHttp(url, temp.getSong_id());
                MusicUtils.play(playUrls.get(0));
                Picasso.with(LrcActivity.this).load(temp.getPic_small()).into(lrc_img);
                lrc_song_name.setText(temp.getTitle());
                lrc_singer.setText(temp.getAuthor());
                lrc_play.setBackgroundResource(R.mipmap.music_stop);

                //动画
                Animation animation = AnimationUtils.loadAnimation(LrcActivity.this, R.anim.rotate_anim);
                LinearInterpolator lin = new LinearInterpolator();//设置动画匀速运动
                animation.setInterpolator(lin);
                lrc_img.startAnimation(animation);
                //开启线程播放下一曲
                myThread = new MyThread(temp.getFile_duration() * 1000);
                myThread.start();
            } else if (msg.what == 4) {
                sendHttp(url, temp.getSong_id());
                MusicUtils.play(playUrls.get(0));
                Picasso.with(LrcActivity.this).load(temp.getPic_small()).into(lrc_img);
                lrc_song_name.setText(temp.getTitle());
                lrc_singer.setText(temp.getAuthor());
                lrc_play.setBackgroundResource(R.mipmap.music_stop);

                //动画
                Animation animation = AnimationUtils.loadAnimation(LrcActivity.this, R.anim.rotate_anim);
                LinearInterpolator lin = new LinearInterpolator();//设置动画匀速运动
                animation.setInterpolator(lin);
                lrc_img.startAnimation(animation);
                //开启线程播放下一曲
                myThread = new MyThread(temp.getFile_duration() * 1000);
                myThread.start();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lrc);
        ButterKnife.bind(this);

        lrc_pre.setOnClickListener(this);
        lrc_play.setOnClickListener(this);
        lrc_next.setOnClickListener(this);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                MusicUtils.mediaPlayer.seekTo(i);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                isChanging = true;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                MusicUtils.mediaPlayer.seekTo(seekBar.getProgress());
                isChanging = false;
                thread = new Thread(new SeekBarThread());
                thread.start();
            }
        });

        /*设置沉侵式导航栏*/
        ImmersedStatusbarUtils.initAfterSetContentView(this, lin);

        intent = getIntent();
        duration = intent.getIntExtra("duration", 0);
        //开启线程播放下一曲
        myThread = new MyThread(duration * 1000);
        myThread.start();
        item_position = intent.getIntExtra("position", 0);
        list = (List<Music>) intent.getSerializableExtra("list");
        lrc_song_name.setText(intent.getStringExtra("name"));
        lrc_singer.setText("---" + intent.getStringExtra("singer") + "---");

        setTime(duration * 1000);
        new SeekBarThread().start();
        if (NetWorkUtils.checkNetworkState(this)) {
            sendHttp(url, intent.getStringExtra("songid"));
            Picasso.with(this).load(intent.getStringExtra("url")).placeholder(R.mipmap.music_icon).into(lrc_img);
            //动画
            Animation animation = AnimationUtils.loadAnimation(this, R.anim.rotate_anim);
            LinearInterpolator lin = new LinearInterpolator();//设置动画匀速运动
            animation.setInterpolator(lin);
            lrc_img.startAnimation(animation);
        } else {
            ToastUtils.showToast(this, R.mipmap.music_warning, "没有网络了...");
        }

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

    private void setTime(int time) {
        lrc_start.setText(showTime(0));
        lrc_end.setText(showTime(time));
    }

    @SuppressLint("DefaultLocale")
    private String showTime(int time) {
        time /= 1000;
        int minute = time / 60;
        int hour = minute / 60;
        int second = time % 60;
        minute %= 60;
        return String.format("%02d:%02d", minute, second);
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
                MusicUtils.stop();
                new SeekBarThread().start();
                if (myThread != null) {
                    myThread.interrupt();
                }
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
                }
                break;
            case R.id.lrc_next:
                new SeekBarThread().start();
                MusicUtils.stop();
                if (myThread != null) {
                    myThread.interrupt();
                }
                next();
                break;
            default:
                break;
        }
    }

    private void pre() {
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
                } else {
                    ToastUtils.showToast(this, R.mipmap.music_warning, "没有网络,无法播放上一首");
                }
            }
        }
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

    class SeekBarThread extends Thread {
        @Override
        public void run() {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    seekBar.setProgress(MusicUtils.mediaPlayer.getCurrentPosition());
                }
            }, 1000);
            super.run();
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
        lrc_img.clearAnimation();
        if (b) {
            ToastUtils.showToast(this, R.mipmap.music_warning, "没有下一曲");
        } else {
            if (item_position + 1 > list.size()) {
                ToastUtils.showToast(this, R.mipmap.music_warning, "亲,已经是最后一首了");
            } else {
                temp = list.get(item_position + 1);
                setTime(temp.getFile_duration()*1000);
                if (NetWorkUtils.checkNetworkState(this)) {
                    getPlayUrls(item_position + 1, 4);
                    item_position++;
                } else {
                    ToastUtils.showToast(this, R.mipmap.music_warning, "没有网络,无法播放下一首");
                }
            }
        }
    }

    class MyThread extends Thread {
        private int time;

        MyThread(int time) {
            this.time = time;
        }

        @Override
        public void run() {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    next();
                }
            }, time);
            super.run();
        }
    }
}
