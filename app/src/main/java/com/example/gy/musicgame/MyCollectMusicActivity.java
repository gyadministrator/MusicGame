package com.example.gy.musicgame;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import adapter.MyMusicListAdapter;
import base.BaseActivity;
import bean.Music;
import bean.MyMusic;
import bean.RecommendMusic;
import butterknife.BindView;
import butterknife.ButterKnife;
import utils.Constant;
import utils.CurrentMusicUtils;
import utils.HttpUtils;
import utils.ImmersedStatusbarUtils;
import utils.MusicUtils;
import utils.NetWorkUtils;
import utils.ToastUtils;
import view.CircleImageView;
import view.MyListView;

public class MyCollectMusicActivity extends BaseActivity implements View.OnClickListener, AdapterView.OnItemClickListener {
    @BindView(R.id.back_listen)
    TextView back;
    @BindView(R.id.title_txt)
    TextView title_txt;
    @BindView(R.id.lin)
    LinearLayout lin;
    @BindView(R.id.reload)
    Button reload;
    @BindView(R.id.rel_net)
    RelativeLayout rel_net;
    @BindView(R.id.content)
    RelativeLayout content;
    @BindView(R.id.music_list)
    ListView listView;
    @BindView(R.id.no_data_rel)
    RelativeLayout no_data_rel;

    @BindView(R.id.bar_lin)
    LinearLayout bar_lin;
    @BindView(R.id.music_img)
    CircleImageView music_img;
    @BindView(R.id.singer_name)
    TextView singer_name;
    @BindView(R.id.singer)
    TextView singer;
    @BindView(R.id.play)
    TextView play;
    @BindView(R.id.music_next)
    TextView music_next;

    private RecommendMusic recommendMusic;
    private static boolean b = false;
    private static boolean flag = true;
    private static int item_position;
    private static RecommendMusic temp;

    private static Integer typeId;
    private static Integer userId;
    private static List<MyMusic> myMusicList = new ArrayList<>();
    private static MyMusicListAdapter adapter;

    private static List<String> playUrls = new ArrayList<>();

    private static String url = Constant.BASE_URL + "/person/queryAllMusicByTypeAndUserId";

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {
                if (myMusicList.size() == 0) {
                    //没数据
                    content.setVisibility(View.GONE);
                    rel_net.setVisibility(View.GONE);
                    no_data_rel.setVisibility(View.VISIBLE);
                } else {
                    rel_net.setVisibility(View.GONE);
                    content.setVisibility(View.GONE);
                    no_data_rel.setVisibility(View.GONE);
                    adapter = new MyMusicListAdapter(myMusicList, MyCollectMusicActivity.this);
                    listView.setAdapter(adapter);

                    if (CurrentMusicUtils.getClick()) {
                        List<RecommendMusic> list = new ArrayList<>();
                        for (int i = 0; i < myMusicList.size(); i++) {
                            MyMusic myMusic = myMusicList.get(i);
                            RecommendMusic recommendMusic = new RecommendMusic();
                            recommendMusic.setTitle(myMusic.getName());
                            recommendMusic.setPic_big(myMusic.getImg());
                            recommendMusic.setSong_id(myMusic.getUrl());
                            recommendMusic.setFile_duration(Integer.parseInt(myMusic.getDuration()));
                            recommendMusic.setAuthor(myMusic.getAuthor());

                            list.add(recommendMusic);
                        }
                        CurrentMusicUtils.setRecommendMusics(list);
                        CurrentMusicUtils.setClick(false);
                    }
                }
            } else if (msg.what == 0) {
                rel_net.setVisibility(View.VISIBLE);
                content.setVisibility(View.GONE);
                ToastUtils.showToast(MyCollectMusicActivity.this, R.mipmap.music_icon, "发生了错误");
            } else if (msg.what == 3) {
                MusicUtils.play(playUrls.get(0), MyCollectMusicActivity.this);
                Picasso.with(MyCollectMusicActivity.this).load(temp.getPic_big()).into(music_img);
                String tempTitle = temp.getTitle();
                if (tempTitle.length() > 15) {
                    tempTitle = tempTitle.substring(0, 15);
                }
                singer_name.setText(tempTitle);
                singer.setText(temp.getAuthor());
                play.setBackgroundResource(R.mipmap.music_stop);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_collect_music);
        ButterKnife.bind(this);

        /*设置沉侵式导航栏*/
        ImmersedStatusbarUtils.initAfterSetContentView(this, lin);

        Intent intent = getIntent();
        typeId = intent.getIntExtra("typeId", 0);
        userId = intent.getIntExtra("userId", 0);
        //设置标题
        title_txt.setText(intent.getStringExtra("title"));

        back.setOnClickListener(this);

        listView.setOnItemClickListener(this);

        initPlayBar();

        if (NetWorkUtils.checkNetworkState(this)) {
            sendHttp(url, userId, typeId);
        } else {
            content.setVisibility(View.GONE);
            rel_net.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        myMusicList.clear();
    }

    private void sendHttp(String url, int userId, int typeId) {
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
        map.put("typeId", typeId);
        map.put("userId", userId);
        httpUtils.sendGetHttp(url, map);
    }

    private void parseJson(String s) {
        try {
            JSONObject jsonObject = new JSONObject(s);
            JSONObject response = jsonObject.optJSONObject("response");
            JSONArray list = response.optJSONArray("list");
            for (int i = 0; i < list.length(); i++) {
                MyMusic myMusic = JSON.parseObject(list.get(i).toString(), MyMusic.class);
                myMusicList.add(myMusic);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back_listen:
                finish();
                break;
            case R.id.reload:
                if (NetWorkUtils.checkNetworkState(MyCollectMusicActivity.this)) {
                    sendHttp(url, userId, typeId);
                } else {
                    ToastUtils.showToast(MyCollectMusicActivity.this, R.mipmap.music_warning, "无网络连接");
                }
                break;
            case R.id.play:
                if (b) {
                    //播放
                    MusicUtils.pause();
                    play.setBackgroundResource(R.mipmap.music_play);
                    b = false;
                } else {
                    MusicUtils.playContinue();
                    play.setBackgroundResource(R.mipmap.music_stop);
                    b = true;
                }
                if (flag) {
                    //播放
                    MusicUtils.pause();
                    play.setBackgroundResource(R.mipmap.music_play);
                    flag = false;
                } else {
                    MusicUtils.playContinue();
                    play.setBackgroundResource(R.mipmap.music_stop);
                    flag = true;
                }
                break;
            case R.id.music_next:
                //下一首
                next();
                break;
            case R.id.bar_lin:
                recommendMusic = CurrentMusicUtils.getRecommendMusic();
                if (recommendMusic != null) {
                    Intent intent1 = new Intent(this, LrcActivity.class);
                    intent1.putExtra("name", recommendMusic.getTitle());
                    intent1.putExtra("singer", recommendMusic.getAuthor());
                    intent1.putExtra("url", recommendMusic.getPic_big());
                    intent1.putExtra("songid", recommendMusic.getSong_id());
                    intent1.putExtra("duration", recommendMusic.getFile_duration());
                    intent1.putExtra("position", item_position - 1);
                    List<Music> musicList = new ArrayList<>();
                    for (int i = 0; i < myMusicList.size(); i++) {
                        MyMusic myMusic = myMusicList.get(i);
                        Music music = new Music();
                        music.setFile_duration(Integer.parseInt(myMusic.getDuration()));
                        music.setPic_big(myMusic.getImg());
                        music.setAuthor(myMusic.getAuthor());
                        music.setTitle(myMusic.getName());
                        music.setSong_id(myMusic.getUrl());

                        musicList.add(music);

                    }
                    intent1.putExtra("list", (Serializable) musicList);
                    startActivity(intent1);
                }
                break;
            default:
                break;
        }
    }

    private void next() {
        List<RecommendMusic> list = CurrentMusicUtils.getRecommendMusics();
        if (item_position + 1 > myMusicList.size()) {
            ToastUtils.showToast(this, R.mipmap.music_warning, "亲,已经是最后一首了");
        } else {
            temp = list.get(item_position);
            if (NetWorkUtils.checkNetworkState(this)) {
                getPlayUrls(item_position);
                CurrentMusicUtils.setRecommendMusic(temp);
                item_position++;
            } else {
                ToastUtils.showToast(this, R.mipmap.music_warning, "没有网络,无法播放下一首");
            }
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        CurrentMusicUtils.setClick(true);
        if (CurrentMusicUtils.getClick()) {
            List<RecommendMusic> list = new ArrayList<>();
            for (int j = 0; j < myMusicList.size(); j++) {
                MyMusic myMusic = myMusicList.get(j);
                RecommendMusic recommendMusic = new RecommendMusic();
                recommendMusic.setTitle(myMusic.getName());
                recommendMusic.setPic_big(myMusic.getImg());
                recommendMusic.setSong_id(myMusic.getUrl());
                recommendMusic.setFile_duration(Integer.parseInt(myMusic.getDuration()));
                recommendMusic.setAuthor(myMusic.getAuthor());

                list.add(recommendMusic);
            }
            CurrentMusicUtils.setRecommendMusics(list);
            CurrentMusicUtils.setClick(false);
        }

        item_position = i + 1;

        bar_lin.setOnClickListener(this);

        MyMusic myMusic = myMusicList.get(i);
        RecommendMusic recommendMusic = new RecommendMusic();
        recommendMusic.setTitle(myMusic.getName());
        recommendMusic.setPic_big(myMusic.getImg());
        recommendMusic.setSong_id(myMusic.getUrl());
        recommendMusic.setFile_duration(Integer.parseInt(myMusic.getDuration()));
        recommendMusic.setAuthor(myMusic.getAuthor());
        temp = recommendMusic;

        b = false;
        if (NetWorkUtils.checkNetworkState(MyCollectMusicActivity.this)) {
            play.setEnabled(true);
            music_next.setEnabled(true);

            play.setOnClickListener(this);
            music_next.setOnClickListener(this);

            getPlayUrls(i);

            CurrentMusicUtils.setRecommendMusic(temp);
        } else {
            ToastUtils.showToast(MyCollectMusicActivity.this, R.mipmap.music_warning, "无网络连接");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        myMusicList.clear();
    }


    private void getPlayUrls(final int currentNum) {
        String songid = myMusicList.get(currentNum).getUrl();
        Map<String, Object> map = new HashMap<>();
        map.put("songid", songid);
        final HttpUtils httpUtils = new HttpUtils(new HttpUtils.IHttpResponseListener() {
            @Override
            public void onSuccess(String json) {
                parseJsonUrl(json);
                handler.sendEmptyMessage(3);
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

    private void initPlayBar() {
        if (NetWorkUtils.checkNetworkState(this)) {
            recommendMusic = CurrentMusicUtils.getRecommendMusic();
            if (recommendMusic != null) {
                Picasso.with(MyCollectMusicActivity.this).load(recommendMusic.getPic_big()).into(music_img);
                singer_name.setText(recommendMusic.getTitle());
                singer.setText(recommendMusic.getAuthor());
                play.setEnabled(true);
                music_next.setEnabled(true);

                if (MusicUtils.playState()) {
                    play.setBackgroundResource(R.mipmap.music_stop);
                }
                b = true;
            }
        } else {
            ToastUtils.showToast(this, R.mipmap.music_warning, "无网络连接");
        }
    }
}
