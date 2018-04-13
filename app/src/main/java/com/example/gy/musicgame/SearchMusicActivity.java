package com.example.gy.musicgame;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import adapter.MusicSearchListAdapter;
import base.BaseActivity;
import bean.SearchSong;
import butterknife.BindView;
import butterknife.ButterKnife;
import utils.Constant;
import utils.DialogUtils;
import utils.HttpUtils;
import utils.MusicUtils;
import utils.NetWorkUtils;
import utils.ToastUtils;

public class SearchMusicActivity extends BaseActivity implements AdapterView.OnItemClickListener {
    @BindView(R.id.music_search_cancel)
    TextView music_search_cancel;
    @BindView(R.id.search_edit)
    EditText search_edit;
    @BindView(R.id.search_list)
    ListView search_list;
    private static final String URL = Constant.BASE_URL + "/music/GetSearchSong";
    private List<SearchSong> list = new ArrayList<>();
    private static final String TAG = "SearchMusicActivity";
    private static List<String> playUrls = new ArrayList<>();
    private MusicSearchListAdapter adapter;
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {
                DialogUtils.hidden();
                Log.e(TAG, "handleMessage: " + list.size());
                if (list.size() == 0) {
                    ToastUtils.showToast(SearchMusicActivity.this, R.mipmap.music_icon, "没有搜到相关结果");
                } else {
                    adapter = new MusicSearchListAdapter(list, SearchMusicActivity.this);
                    search_list.setAdapter(adapter);
                }
            } else if (msg.what == 0) {
                DialogUtils.hidden();
                ToastUtils.showToast(SearchMusicActivity.this, R.mipmap.music_icon, "发生了错误");
            } else if (msg.what == 3) {
                MusicUtils.play(playUrls.get(0));
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_music);
        ButterKnife.bind(this);
        music_search_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (search_edit.getText().toString().equals("")) {
                    ToastUtils.showToast(SearchMusicActivity.this, R.mipmap.music_icon, "请输入关键字");
                    return;
                } else {
                    if (NetWorkUtils.checkNetworkState(SearchMusicActivity.this)) {
                        list.clear();
                        Map<String, Object> params = new HashMap<>();
                        params.put("query", search_edit.getText().toString());
                        DialogUtils.show(SearchMusicActivity.this);
                        send(URL, params);
                    } else {
                        ToastUtils.showToast(SearchMusicActivity.this, R.mipmap.music_icon, "无网络连接");
                    }
                }
            }
        });
        search_list.setOnItemClickListener(this);
    }


    private void getPlayUrls(int currentNum) {
        String songid = list.get(currentNum).getSongid();
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
            playUrls.add(show_link);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void send(String url, Map<String, Object> map) {
        HttpUtils httpUtils = new HttpUtils(new HttpUtils.IHttpResponseListener() {
            @Override
            public void onSuccess(String json) {
                Log.e(TAG, json);
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

    private void parseJson(String s) {
        try {
            JSONObject jsonObject = new JSONObject(s);
            JSONArray song = jsonObject.optJSONArray("song");
            if (song != null) {
                for (int i = 0; i < song.length(); i++) {
                    SearchSong searchSong = JSON.parseObject(song.get(i).toString(), SearchSong.class);
                    list.add(searchSong);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (NetWorkUtils.checkNetworkState(SearchMusicActivity.this)) {
            getPlayUrls(position);
        } else {
            ToastUtils.showToast(SearchMusicActivity.this, R.mipmap.music_warning, "无网络连接");
        }
    }
}
