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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import adapter.MyMusicListAdapter;
import base.BaseActivity;
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

    private static Integer typeId;
    private static Integer userId;
    private static List<MyMusic> myMusicList = new ArrayList<>();
    private static MyMusicListAdapter adapter;

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
                }
            } else if (msg.what == 0) {
                rel_net.setVisibility(View.VISIBLE);
                content.setVisibility(View.GONE);
                ToastUtils.showToast(MyCollectMusicActivity.this, R.mipmap.music_icon, "发生了错误");
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
            default:
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        MyMusic myMusic = myMusicList.get(i);
        if (NetWorkUtils.checkNetworkState(this)) {
            MusicUtils.play(myMusic.getUrl(),this);

            RecommendMusic recommendMusic = new RecommendMusic();
            recommendMusic.setAuthor(myMusic.getAuthor());
            recommendMusic.setFile_duration(Integer.parseInt(myMusic.getDuration()));
            recommendMusic.setTitle(myMusic.getName());
            recommendMusic.setPic_big(myMusic.getImg());

            CurrentMusicUtils.setRecommendMusic(recommendMusic);
        } else {
            ToastUtils.showToast(MyCollectMusicActivity.this, R.mipmap.music_warning, "无网络连接");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        myMusicList.clear();
    }
}
