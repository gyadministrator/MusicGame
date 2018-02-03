package com.example.gy.musicgame;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.SeekBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import base.BaseActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import utils.NetWorkUtils;
import utils.ToastUtils;
import view.CircleImageView;

public class MusicLyricActivity extends BaseActivity {
    @BindView(R.id.lyric_name)
    TextView lyric_name;
    @BindView(R.id.lyric_singer)
    TextView lyric_singer;
    @BindView(R.id.singer_lyric_image)
    CircleImageView singer_lyric_image;
    @BindView(R.id.pre)
    TextView pre;
    @BindView(R.id.play)
    TextView play;
    @BindView(R.id.next)
    TextView next;
    @BindView(R.id.time_start)
    TextView time_start;
    @BindView(R.id.time_end)
    TextView time_end;
    @BindView(R.id.seekBar)
    SeekBar seekBar;
    private static final String TAG = "MusicLyricActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_lyric);
        ButterKnife.bind(this);

        init();

    }

    private void init() {
        Intent intent = getIntent();
        lyric_name.setText(intent.getStringExtra("name"));
        lyric_singer.setText(intent.getStringExtra("singer"));
        play.setBackgroundResource(R.mipmap.music_stop);
        time_end.setText(getDuration(intent.getIntExtra("total", 0)));
        if (NetWorkUtils.checkNetworkState(this)) {
            Picasso.with(this).load(intent.getStringExtra("img")).into(singer_lyric_image);
        } else {
            ToastUtils.showToast(this, R.mipmap.music_warning, "获取网络图片失败");
        }

        Log.e(TAG, "init: " + intent.getIntExtra("total", 0));
    }

    private String getDuration(int total) {
        String buffer = "0" +
                total / 60 +
                ":" +
                total % 60;
        return buffer;
    }
}
