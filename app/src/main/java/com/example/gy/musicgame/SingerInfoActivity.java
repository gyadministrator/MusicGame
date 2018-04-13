package com.example.gy.musicgame;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

import bean.Singer;
import butterknife.BindView;
import butterknife.ButterKnife;
import utils.Constant;
import utils.DialogUtils;
import utils.HttpUtils;
import utils.ImmersedStatusbarUtils;
import utils.NetWorkUtils;
import utils.ToastUtils;
import view.CircleImageView;

public class SingerInfoActivity extends AppCompatActivity {
    @BindView(R.id.singer_image)
    CircleImageView singer_image;
    @BindView(R.id.name)
    TextView name;
    @BindView(R.id.sex)
    TextView sex;
    @BindView(R.id.country)
    TextView country;
    @BindView(R.id.source)
    TextView source;
    @BindView(R.id.collect_btn)
    Button collect_btn;
    @BindView(R.id.song_list_btn)
    Button song_list_btn;
    @BindView(R.id.album_btn)
    Button album_btn;
    @BindView(R.id.MV_btn)
    Button MV_btn;
    @BindView(R.id.singer_introduce)
    TextView singer_introduce;
    @BindView(R.id.main_rel)
    RelativeLayout main_rel;
    @BindView(R.id.main_linear)
    LinearLayout main_linear;
    @BindView(R.id.reload)
    Button reload;
    @BindView(R.id.back_listen)
    TextView back_listen;
    @BindView(R.id.lin)
    LinearLayout lin;

    private Singer singer;
    private static final String url = Constant.BASE_URL + "/music/GetSongerInfo";

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {
                DialogUtils.hidden();
                if (singer != null) {
                    Picasso.with(SingerInfoActivity.this).load(singer.getAvatar_middle()).into(singer_image);
                    name.setText("姓名:" + singer.getName());
                    if (singer.getGender().equals("1")) {
                        sex.setText("性别:女");
                    } else {
                        sex.setText("性别:男");
                    }
                    country.setText("国家:" + singer.getCountry());
                    source.setText("来源:" + singer.getSource());
                    collect_btn.setText("收藏(" + singer.getCollect_num() + ")");
                    song_list_btn.setText("歌曲(" + singer.getSongs_total() + ")");
                    album_btn.setText("专辑(" + singer.getAlbums_total() + ")");
                    MV_btn.setText("MV(" + singer.getMv_total() + ")");
                    singer_introduce.setText(singer.getIntro());
                }
            } else if (msg.what == 0) {
                DialogUtils.hidden();
                ToastUtils.showToast(SingerInfoActivity.this, R.mipmap.music_icon, "发生了错误");
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_singer_info);
        ButterKnife.bind(this);

        /*设置沉侵式导航栏*/
        ImmersedStatusbarUtils.initAfterSetContentView(this, lin);

        final String tinguid = getIntent().getStringExtra("tinguid");
        if (NetWorkUtils.checkNetworkState(this)) {
            main_linear.setVisibility(View.VISIBLE);
            main_rel.setVisibility(View.GONE);
            Map<String, Object> params = new HashMap<>();
            params.put("tinguid", tinguid);
            send(url, params);
        } else {
            main_rel.setVisibility(View.VISIBLE);
            main_linear.setVisibility(View.GONE);
            ToastUtils.showToast(this, R.mipmap.music_warning, "无网络连接");
        }

        back_listen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        reload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (NetWorkUtils.checkNetworkState(SingerInfoActivity.this)) {
                    main_linear.setVisibility(View.VISIBLE);
                    main_rel.setVisibility(View.GONE);
                    Map<String, Object> params = new HashMap<>();
                    params.put("tinguid", tinguid);
                    send(url, params);
                } else {
                    main_rel.setVisibility(View.VISIBLE);
                    main_linear.setVisibility(View.GONE);
                    ToastUtils.showToast(SingerInfoActivity.this, R.mipmap.music_warning, "无网络连接");
                }
            }
        });
    }

    private void send(String url, Map<String, Object> map) {
        DialogUtils.show(this);
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
        httpUtils.sendGetHttp(url, map);
    }

    private void parseJson(String json) {
        try {
            singer = JSON.parseObject(json, Singer.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
