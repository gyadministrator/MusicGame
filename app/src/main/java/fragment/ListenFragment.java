package fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.gy.musicgame.ListenMainActivity;
import com.example.gy.musicgame.R;
import com.example.gy.musicgame.SearchMusicActivity;
import com.google.gson.Gson;
import com.youth.banner.Banner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import base.BaseFragment;
import bean.RecommendMusic;
import butterknife.BindView;
import butterknife.ButterKnife;
import utils.Constant;
import utils.GlideImageLoader;
import utils.HttpUtils;
import utils.ImmersedStatusbarUtils;
import utils.NetWorkUtils;
import utils.ToastUtils;

/**
 * Created by Administrator on 2017/9/12.
 */

public class ListenFragment extends BaseFragment implements View.OnClickListener {
    @BindView(R.id.music_search)
    TextView search;
    @BindView(R.id.xinge)
    LinearLayout xinge;
    @BindView(R.id.rege)
    LinearLayout rege;
    @BindView(R.id.yaogun)
    LinearLayout yaogun;
    @BindView(R.id.jueshi)
    LinearLayout jueshi;
    @BindView(R.id.liuxing)
    LinearLayout liuxing;
    @BindView(R.id.oumei)
    LinearLayout oumei;
    @BindView(R.id.qinge)
    LinearLayout qinge;
    @BindView(R.id.yinshi)
    LinearLayout yinshi;
    @BindView(R.id.wangluo)
    LinearLayout wangluo;
    @BindView(R.id.banner)
    Banner banner;
    @BindView(R.id.lin)
    LinearLayout lin;

    private int[] nums;
    private String[] types;

    List<String> images = new ArrayList<>();
    private static final String url = Constant.BASE_URL + "/music/getSongList";

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler(
    ) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {
                initBanner();
            } else {
                ToastUtils.showToast(mContext, R.mipmap.music_warning, "获取网络图片错误...");
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

        /*设置沉侵式导航栏*/
        //ImmersedStatusbarUtils.initAfterSetContentView(getActivity(), lin);
        nums = mContext.getResources().getIntArray(R.array.types_num);
        types = mContext.getResources().getStringArray(R.array.types);

        if (NetWorkUtils.checkNetworkState(mContext)) {
            sendHttp(url, 1, 0);
        } else {
            ToastUtils.showToast(mContext, R.mipmap.music_warning, "貌似没有网哎...");
        }

        xinge.setOnClickListener(this);
        rege.setOnClickListener(this);
        yaogun.setOnClickListener(this);
        jueshi.setOnClickListener(this);
        liuxing.setOnClickListener(this);
        oumei.setOnClickListener(this);
        qinge.setOnClickListener(this);
        yinshi.setOnClickListener(this);
        wangluo.setOnClickListener(this);

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, SearchMusicActivity.class);
                startActivity(intent);
            }
        });
    }

    private void initBanner() {
        //设置图片加载器
        banner.setImageLoader(new GlideImageLoader());
        //设置图片集合
        banner.setImages(images);
        //banner设置方法全部调用完毕时最后调用
        banner.start();
    }

    private void sendHttp(String url, int type, int offset) {
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
        map.put("type", type);
        map.put("size", 6);
        map.put("offset", offset);
        httpUtils.sendGetHttp(url, map);
    }

    private void parseJson(String json) {
        try {
            JSONObject jsonObject = new JSONObject(json);
            JSONArray song = jsonObject.optJSONArray("song_list");
            for (int i = 0; i < song.length(); i++) {
                Gson gson = new Gson();
                RecommendMusic recommendMusic = gson.fromJson(song.get(i).toString(), RecommendMusic.class);
                images.add(recommendMusic.getPic_small());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.xinge:
                startListenActivity(nums[0], types[0]);
                break;
            case R.id.rege:
                startListenActivity(nums[1], types[1]);
                break;
            case R.id.yaogun:
                startListenActivity(nums[2], types[2]);
                break;
            case R.id.jueshi:
                startListenActivity(nums[3], types[3]);
                break;
            case R.id.liuxing:
                startListenActivity(nums[4], types[4]);
                break;
            case R.id.oumei:
                startListenActivity(nums[5], types[5]);
                break;
            case R.id.wangluo:
                startListenActivity(nums[6], types[6]);
                break;
            case R.id.qinge:
                startListenActivity(nums[7], types[7]);
                break;
            case R.id.yinshi:
                startListenActivity(nums[8], types[8]);
                break;
        }
    }

    /*跳转*/
    private void startListenActivity(int position, String title) {
        Intent intent = new Intent(mContext, ListenMainActivity.class);
        intent.putExtra("position", position);
        intent.putExtra("title", title);
        startActivity(intent);
    }
}
