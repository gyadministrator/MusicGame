package fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.gy.musicgame.R;
import com.example.gy.musicgame.SearchMusicActivity;
import com.example.gy.musicgame.SingerInfoActivity;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import adapter.MusicListAdapter;
import base.BaseFragment;
import bean.RecommendMusic;
import butterknife.BindView;
import butterknife.ButterKnife;
import utils.Constant;
import utils.DialogUtils;
import utils.DownloadUtil;
import utils.HttpUtils;
import utils.MoreDialog;
import utils.MusicUtils;
import utils.NetWorkUtils;
import utils.ToastUtils;
import view.LoadListView;

/**
 * Created by Administrator on 2017/9/12.
 */

public class ListenFragment extends BaseFragment implements AdapterView.OnItemClickListener, SwipeRefreshLayout.OnRefreshListener, LoadListView.ILoadListener, View.OnClickListener, AdapterView.OnItemLongClickListener {
    @BindView(R.id.music_list)
    LoadListView listView;
    @BindView(R.id.swipe)
    SwipeRefreshLayout swipe;
    @BindView(R.id.spinner)
    Spinner spinner;
    @BindView(R.id.music_search)
    TextView search;
    private static final String url = Constant.BASE_URL + "/music/getSongList";
    private static final String TAG = "ListenFragment";
    private static List<RecommendMusic> list = new ArrayList<>();
    private static List<String> playUrls = new ArrayList<>();
    private MusicListAdapter adapter;
    private static int offset = 0;
    private static int[] nums;
    private static int type;
    private static String tinguid;
    private static int mProgress;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {
                DialogUtils.hidden();
                adapter = new MusicListAdapter(list, mContext);
                listView.setAdapter(adapter);
                offset = offset + 1;
                listView.loadComplete();
                swipe.setRefreshing(false);
            } else if (msg.what == 0) {
                DialogUtils.hidden();
                swipe.setRefreshing(false);
                ToastUtils.showToast(mContext, R.mipmap.music_icon, "发生了错误");
            } else if (msg.what == 3) {
                MusicUtils.play(playUrls.get(0));
            } else if (msg.what == 4) {
                ToastUtils.showToast(mContext, R.mipmap.music_icon, "下载完成");
            } else if (msg.what == 5) {
                final ProgressBar progressBar = new ProgressBar(mContext);
                progressBar.setProgress(mProgress);
            } else if (msg.what == 6) {
                ToastUtils.showToast(mContext, R.mipmap.music_warning, "下载失败");
            }
        }
    };

    @Override
    protected View initView() {
        View view = LayoutInflater.from(mContext).inflate(R.layout.activity_listen, null);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    protected void initData() {
        super.initData();
        nums = mContext.getResources().getIntArray(R.array.types_num);
        listView.setOnItemClickListener(this);
        listView.setOnItemLongClickListener(this);
        listView.setLoadListener(this);
        swipe.setOnRefreshListener(this);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (NetWorkUtils.checkNetworkState(mContext)) {
                    list.clear();
                    type = nums[position];
                    DialogUtils.show(mContext);
                    sendHttp(url, type, 0);
                } else {
                    ToastUtils.showToast(mContext, R.mipmap.music_warning, "无网络连接");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, SearchMusicActivity.class);
                startActivity(intent);
            }
        });
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

    private void sendHttp(String url, int type, int offset) {
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
        Map<String, Object> map = new HashMap<>();
        map.put("type", type);
        map.put("size", 20);
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
        if (NetWorkUtils.checkNetworkState(mContext)) {
            getPlayUrls(position, 3);
        } else {
            ToastUtils.showToast(mContext, R.mipmap.music_warning, "无网络连接");
        }
    }

    @Override
    public void onRefresh() {
        if (NetWorkUtils.checkNetworkState(mContext)) {
            list.clear();
            DialogUtils.show(mContext);
            sendHttp(url, type, 0);
        } else {
            swipe.setRefreshing(false);
            ToastUtils.showToast(mContext, R.mipmap.music_warning, "无网络连接");
        }
    }

    @Override
    public void onLoad() {
        if (NetWorkUtils.checkNetworkState(mContext)) {
            sendHttp(url, type, offset);
        } else {
            ToastUtils.showToast(mContext, R.mipmap.music_warning, "无网络连接");
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.find:
                Intent intent = new Intent(mContext, SingerInfoActivity.class);
                intent.putExtra("tinguid", tinguid);
                mContext.startActivity(intent);
                MoreDialog.hidden();
                break;
            case R.id.download:
                //下载歌曲
                Log.e("url---", playUrls.get(0));
                if (NetWorkUtils.checkNetworkState(mContext)) {
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
                    ToastUtils.showToast(mContext, R.mipmap.music_warning, "无网络连接");
                }
                MoreDialog.hidden();
                break;
            case R.id.cancel:
                MoreDialog.hidden();
                break;
        }
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        tinguid = list.get(position).getTing_uid();
        if (NetWorkUtils.checkNetworkState(mContext)) {
            getPlayUrls(position, 7);
        } else {
            ToastUtils.showToast(mContext, R.mipmap.music_warning, "无网络连接");
        }
        MoreDialog.show(mContext);
        MoreDialog.find.setOnClickListener(this);
        MoreDialog.download.setOnClickListener(this);
        MoreDialog.cancel.setOnClickListener(this);
        return true;
    }
}
