package com.example.gy.musicgame;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import adapter.MusicListAdapter;
import base.BaseActivity;
import bean.RecommendMusic;
import bean.dao.RecommendMusicDao;
import butterknife.BindView;
import butterknife.ButterKnife;
import utils.Constant;
import utils.HttpUtils;
import utils.MusicDaoUtils;
import utils.MusicUtils;
import utils.NetWorkUtils;
import utils.ToastUtils;
import view.CircleImageView;
import view.LoadListView;

public class RecentActivity extends BaseActivity implements View.OnClickListener, LoadListView.ILoadListener, AdapterView.OnItemClickListener {
    @BindView(R.id.cha)
    TextView cha;
    @BindView(R.id.list)
    LoadListView listView;
    @BindView(R.id.no_recored)
    TextView no_recored;
    @BindView(R.id.loading_rel)
    RelativeLayout loading_rel;
    @BindView(R.id.total)
    TextView total;
    @BindView(R.id.delete)
    TextView delete;
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

    private static int item_position;

    private static List<String> playUrls = new ArrayList<>();
    private static boolean flag = true;

    private MusicListAdapter adapter;
    private RecommendMusicDao musicDao;
    private List<RecommendMusic> list = new ArrayList<>();
    private int pageNum = 0;
    private int allPage;
    private static final String TAG = "RecentActivity";
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {
                total.setText("共0首");
                queryRecored(0);
                adapter.notifyDataSetChanged();
                listView.setVisibility(View.GONE);
                ToastUtils.showToast(RecentActivity.this, R.mipmap.music_warning, "清空成功");
            } else if (msg.what == 2) {
                MusicUtils.play(playUrls.get(0));
            } else if (msg.what == 3) {

            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recent);
        musicDao = MusicDaoUtils.initDbHelp(this);
        ButterKnife.bind(this);

        listView.setLoadListener(this);
        listView.setOnItemClickListener(this);

        cha.setOnClickListener(this);
        delete.setOnClickListener(this);
        play.setOnClickListener(this);
        music_next.setOnClickListener(this);
        /*查询记录
        * */
        allPage = MusicDaoUtils.getPage(MusicDaoUtils.queryAllMusic(musicDao));
        queryRecored(pageNum);
    }

    @Override
    protected void onStart() {
        super.onStart();
        queryRecored(pageNum);
    }

    private void queryRecored(int pageNum) {
        list = MusicDaoUtils.getMusicByPageSize(pageNum, musicDao);
        if (list.size() == 0) {
            loading_rel.setVisibility(View.GONE);
            no_recored.setVisibility(View.VISIBLE);
        } else {
            listView.setVisibility(View.VISIBLE);
            loading_rel.setVisibility(View.GONE);
            adapter = new MusicListAdapter(list, this);
            listView.setAdapter(adapter);
            total.setText("共" + list.size() + "首");
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cha:
                finish();
                break;
            case R.id.delete:
                if (list.size() == 0) {
                    ToastUtils.showToast(this, R.mipmap.music_warning, "没有记录呀.");
                } else {
                    MusicDaoUtils.deleteAllMusic(musicDao);
                    handler.sendEmptyMessage(1);
                }
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
                if (item_position + 1 > list.size()) {
                    ToastUtils.showToast(this, R.mipmap.music_warning, "亲,已经是最后一首了");
                } else {
                    RecommendMusic recommendMusic = list.get(item_position);
                    if (NetWorkUtils.checkNetworkState(this)) {
                        Picasso.with(this).load(recommendMusic.getPic_small()).into(music_img);
                        singer_name.setText(recommendMusic.getTitle());
                        singer.setText(recommendMusic.getAuthor());

                        play.setBackgroundResource(R.mipmap.music_stop);

                        getPlayUrls(item_position, 2);

                        item_position++;
                    } else {
                        ToastUtils.showToast(this, R.mipmap.music_warning, "没有网络,无法播放下一首");
                    }
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onLoad() {
        if (pageNum == allPage - 1) {
            listView.loadComplete(list.size() - 1);
            ToastUtils.showToast(this, R.mipmap.music_warning, "没有更多数据了...");
        } else {
            list.addAll(MusicDaoUtils.getMusicByPageSize(++pageNum, musicDao));
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        item_position = position + 1;
        RecommendMusic recommendMusic = list.get(position);
        if (NetWorkUtils.checkNetworkState(this)) {
            Picasso.with(this).load(recommendMusic.getPic_small()).into(music_img);
            singer_name.setText(recommendMusic.getTitle());
            singer.setText(recommendMusic.getAuthor());

            play.setBackgroundResource(R.mipmap.music_stop);

            play.setEnabled(true);
            music_next.setEnabled(true);
            getPlayUrls(position, 2);
        } else {
            ToastUtils.showToast(this, R.mipmap.music_warning, "无网络连接");
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
}
