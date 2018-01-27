package com.example.gy.musicgame;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import adapter.MusicListAdapter;
import base.BaseActivity;
import bean.RecommendMusic;
import butterknife.BindView;
import butterknife.ButterKnife;
import utils.Constant;
import utils.DownloadUtil;
import utils.HttpUtils;
import utils.MoreDialog;
import utils.MusicUtils;
import utils.NetWorkUtils;
import utils.ToastUtils;
import view.CircleImageView;
import view.LoadListView;

/**
 * Created by Administrator on 2018/1/15.
 */

public class ListenMainActivity extends BaseActivity implements AdapterView.OnItemClickListener, SwipeRefreshLayout.OnRefreshListener, LoadListView.ILoadListener, View.OnClickListener, AdapterView.OnItemLongClickListener {
    @BindView(R.id.music_list)
    LoadListView listView;
    @BindView(R.id.swipe)
    SwipeRefreshLayout swipe;
    @BindView(R.id.back_listen)
    TextView back_listen;
    @BindView(R.id.title_txt)
    TextView title_txt;
    @BindView(R.id.content)
    RelativeLayout content;
    @BindView(R.id.msg)
    TextView msg_t;
    @BindView(R.id.loading)
    ProgressBar loading;
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
    private static boolean flag = true;
    private static int item_position;
    private static final String url = Constant.BASE_URL + "/music/getSongList";
    private static List<RecommendMusic> list = new ArrayList<>();
    private static List<String> playUrls = new ArrayList<>();
    private MusicListAdapter adapter;
    private static int offset = 0;
    private static int type;
    private static String title;
    private static int size = 20;
    private static String tinguid;
    private static int mProgress;


    private static final String TAG = "ListenMainActivity";
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {
                if (list.size() == 0) {
                    loading.setVisibility(View.GONE);
                    msg_t.setText("没有获取到数据...");
                } else {
                    content.setVisibility(View.GONE);
                    adapter = new MusicListAdapter(list, ListenMainActivity.this);
                    listView.setAdapter(adapter);
                    swipe.setRefreshing(false);
                }
                swipe.setRefreshing(false);
            } else if (msg.what == 0) {
                swipe.setRefreshing(false);
                loading.setVisibility(View.GONE);
                msg_t.setText("呀,发生了错误啦,下拉刷新试试...");
                ToastUtils.showToast(ListenMainActivity.this, R.mipmap.music_icon, "发生了错误");
            } else if (msg.what == 3) {
                MusicUtils.play(playUrls.get(0));
            } else if (msg.what == 4) {
                ToastUtils.showToast(ListenMainActivity.this, R.mipmap.music_icon, "下载完成");
            } else if (msg.what == 5) {
                final ProgressBar progressBar = new ProgressBar(ListenMainActivity.this);
                progressBar.setProgress(mProgress);
            } else if (msg.what == 6) {
                ToastUtils.showToast(ListenMainActivity.this, R.mipmap.music_warning, "下载失败");
            } else if (msg.what == 7) {
                content.setVisibility(View.GONE);
                adapter = new MusicListAdapter(list, ListenMainActivity.this);
                listView.setAdapter(adapter);
                listView.loadComplete(list.size());
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listen_main);
        ButterKnife.bind(this);

        listView.setLoadListener(this);
        listView.setOnItemClickListener(this);
        listView.setOnItemLongClickListener(this);
        swipe.setOnRefreshListener(this);
        loading.setVisibility(View.VISIBLE);

        play.setOnClickListener(this);
        music_next.setOnClickListener(this);

        setTitle();

        if (NetWorkUtils.checkNetworkState(this)) {
            sendHttp(url, type, offset, 1);
        } else {
            loading.setVisibility(View.GONE);
            msg_t.setText("貌似没有网哎...");
        }
    }

    /**
     * 设置标题栏
     */
    private void setTitle() {
        type = getIntent().getIntExtra("position", 0);
        title = getIntent().getStringExtra("title");

        back_listen.setOnClickListener(this);
        title_txt.setText(title);
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

    private void sendHttp(String url, int type, int offset, final int flag) {
        HttpUtils httpUtils = new HttpUtils(new HttpUtils.IHttpResponseListener() {
            @Override
            public void onSuccess(String json) {
                parseJson(json);
                handler.sendEmptyMessage(flag);
            }

            @Override
            public void onFail(String error) {
                handler.sendEmptyMessage(0);
            }
        });
        Map<String, Object> map = new HashMap<>();
        map.put("type", type);
        map.put("size", size);
        map.put("offset", offset);
        httpUtils.sendGetHttp(url, map);
    }

    private void parseJson(String s) {
        try {
            JSONObject jsonObject = new JSONObject(s);
            JSONArray song = jsonObject.optJSONArray("song_list");
            for (int i = 0; i < song.length(); i++) {
                Gson gson = new Gson();
                RecommendMusic recommendMusic = gson.fromJson(song.get(i).toString(), RecommendMusic.class);
                list.add(recommendMusic);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //view.setBackgroundColor(Color.rgb(81, 189, 207));
        item_position = position + 1;
        RecommendMusic recommendMusic = list.get(position);
        if (NetWorkUtils.checkNetworkState(ListenMainActivity.this)) {
            Picasso.with(this).load(recommendMusic.getPic_small()).into(music_img);
            singer_name.setText(recommendMusic.getTitle());
            singer.setText(recommendMusic.getAuthor());

            play.setBackgroundResource(R.mipmap.music_stop);

            play.setEnabled(true);
            music_next.setEnabled(true);
            getPlayUrls(position, 3);
        } else {
            ToastUtils.showToast(ListenMainActivity.this, R.mipmap.music_warning, "无网络连接");
        }
    }

    @Override
    public void onRefresh() {
        if (NetWorkUtils.checkNetworkState(ListenMainActivity.this)) {
            sendHttp(url, type, 0, 1);
        } else {
            swipe.setRefreshing(false);
            ToastUtils.showToast(ListenMainActivity.this, R.mipmap.music_warning, "无网络连接");
        }
    }

    @Override
    public void onLoad() {
        if (NetWorkUtils.checkNetworkState(ListenMainActivity.this)) {
            size = size + 10;
            sendHttp(url, type, offset, 7);
        } else {
            ToastUtils.showToast(ListenMainActivity.this, R.mipmap.music_warning, "无网络连接");
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_listen:
                finish();
                break;
            case R.id.find:
                Intent intent = new Intent(ListenMainActivity.this, SingerInfoActivity.class);
                intent.putExtra("tinguid", tinguid);
                ListenMainActivity.this.startActivity(intent);
                MoreDialog.hidden();
                break;
            case R.id.download:
                //下载歌曲
                if (NetWorkUtils.checkNetworkState(ListenMainActivity.this)) {
                    DownloadUtil.get().download(playUrls.get(0), Environment.getExternalStorageDirectory().getAbsolutePath(), new DownloadUtil.OnDownloadListener() {
                        @Override
                        public void onDownloadSuccess() {
                            handler.sendEmptyMessage(4);
                        }

                        @Override
                        public void onDownloading(int progress) {
                            handler.sendEmptyMessage(5);
                            mProgress = progress;
                        }

                        @Override
                        public void onDownloadFailed() {
                            handler.sendEmptyMessage(6);
                        }
                    });
                } else {
                    ToastUtils.showToast(ListenMainActivity.this, R.mipmap.music_warning, "无网络连接");
                }
                MoreDialog.hidden();
                break;
            case R.id.cancel:
                MoreDialog.hidden();
                break;
            case R.id.play:
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
                if (item_position > list.size()) {
                    ToastUtils.showToast(this, R.mipmap.music_warning, "亲,已经是最后一首了");
                } else {
                    RecommendMusic recommendMusic = list.get(item_position);
                    if (NetWorkUtils.checkNetworkState(this)) {
                        Picasso.with(this).load(recommendMusic.getPic_small()).into(music_img);
                        singer_name.setText(recommendMusic.getTitle());
                        singer.setText(recommendMusic.getAuthor());

                        play.setBackgroundResource(R.mipmap.music_stop);

                        getPlayUrls(item_position, 3);

                        item_position++;
                    } else {
                        ToastUtils.showToast(this, R.mipmap.music_warning, "没有网络,无法播放下一首");
                    }
                }
                break;
        }
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        tinguid = list.get(position).getTing_uid();
        if (NetWorkUtils.checkNetworkState(ListenMainActivity.this)) {
            getPlayUrls(position, 7);
        } else {
            ToastUtils.showToast(ListenMainActivity.this, R.mipmap.music_warning, "无网络连接");
        }
        MoreDialog.show(ListenMainActivity.this);
        MoreDialog.find.setOnClickListener(this);
        MoreDialog.download.setOnClickListener(this);
        MoreDialog.cancel.setOnClickListener(this);
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        list.clear();
    }
}
