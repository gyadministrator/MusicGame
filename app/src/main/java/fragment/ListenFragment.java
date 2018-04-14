package fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.example.gy.musicgame.ListenMainActivity;
import com.example.gy.musicgame.R;
import com.example.gy.musicgame.SearchMusicActivity;
import com.example.gy.musicgame.SingerInfoActivity;
import com.youth.banner.Banner;
import com.youth.banner.listener.OnBannerListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import adapter.TypeAdapter;
import base.BaseFragment;
import bean.RecommendMusic;
import bean.Type;
import bean.User;
import butterknife.BindView;
import butterknife.ButterKnife;
import utils.AddDialogUtils;
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
    @BindView(R.id.progress_lin)
    LinearLayout progress;
    @BindView(R.id.add_tv)
    TextView add_tv;
    @BindView(R.id.no_list)
    TextView no_list;
    @BindView(R.id.music_list_progress)
    ProgressBar music_list_progress;
    @BindView(R.id.myListView)
    ListView myListView;
    @BindView(R.id.total)
    TextView total;
    private TypeAdapter typeAdapter;
    private int[] nums;
    private String[] types;
    private List<Type> typeNames = new ArrayList<>();
    private static final String TAG = "ListenFragment";
    List<String> images = new ArrayList<>();
    List<RecommendMusic> list = new ArrayList<>();
    private static final String url = Constant.BASE_URL + "/music/getSongList";
    private String type_url = Constant.BASE_URL + "/type/queryAllType";
    private static String add_title;

    private static Integer userId;

    private Integer addCode;

    private static String add_url = Constant.BASE_URL + "/type/addType";


    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler(
    ) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {
                progress.setVisibility(View.GONE);
                banner.setVisibility(View.VISIBLE);
                initBanner();

                banner.setOnBannerListener(new OnBannerListener() {
                    @Override
                    public void OnBannerClick(int position) {
                        Intent intent = new Intent(mContext, SingerInfoActivity.class);
                        String tinguid = list.get(position).getTing_uid();
                        intent.putExtra("tinguid", tinguid);
                        startActivity(intent);
                    }
                });
            } else if (msg.what == 8) {
                if (typeNames.size() > 0) {
                    total.setText(typeNames.size() + "个歌单");
                    music_list_progress.setVisibility(View.GONE);
                    no_list.setVisibility(View.GONE);
                    myListView.setVisibility(View.VISIBLE);
                    typeAdapter = new TypeAdapter(typeNames, mContext);
                    myListView.setAdapter(typeAdapter);
                } else {
                    music_list_progress.setVisibility(View.GONE);
                    no_list.setVisibility(View.VISIBLE);
                }
            } else if (msg.what == 9) {
                if (addCode == 100) {
                    AddDialogUtils.hidden();
                    typeNames.clear();
                    getTypes();
                    ToastUtils.showToast(mContext, R.mipmap.about, "添加歌单成功...");
                }
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

        SharedPreferences sharedPreferences = mContext.getSharedPreferences("userId", Context.MODE_PRIVATE);
        userId = sharedPreferences.getInt("userId", 0);

        //获取轮播图
        getBanner();
        //获取歌单
        getTypes();

        /*设置沉侵式导航栏*/
        ImmersedStatusbarUtils.initAfterSetContentView(getActivity(), lin);
        nums = mContext.getResources().getIntArray(R.array.types_num);
        types = mContext.getResources().getStringArray(R.array.types);

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

        add_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddDialogUtils.show(mContext);

                //设置事件
                setAddListEvent();
            }
        });

    }

    private void setAddListEvent() {
        AddDialogUtils.cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddDialogUtils.hidden();
            }
        });

        AddDialogUtils.ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (AddDialogUtils.editText.getText().toString().equals("")) {
                    ToastUtils.showToast(mContext, R.mipmap.music_warning, "请输入歌单名");
                } else {
                    add_title = AddDialogUtils.editText.getText().toString();
                    //添加歌单到数据库中
                    sendAddHttp(add_url, add_title, userId);
                }
            }
        });
    }

    private void getBanner() {
        if (NetWorkUtils.checkNetworkState(mContext)) {
            sendHttp(url, 1, 0);
        } else {
            ToastUtils.showToast(mContext, R.mipmap.music_warning, "貌似没有网哎...");
        }
    }

    private void getTypes() {
        if (NetWorkUtils.checkNetworkState(mContext)) {
            sendTypeHttp(type_url, userId);
        } else {
            ToastUtils.showToast(mContext, R.mipmap.music_warning, "貌似没有网哎...");
        }
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

    private void sendTypeHttp(String url, Integer userId) {
        HttpUtils httpUtils = new HttpUtils(new HttpUtils.IHttpResponseListener() {
            @Override
            public void onSuccess(String json) {
                parseTypeJson(json);
                handler.sendEmptyMessage(8);
            }

            @Override
            public void onFail(String error) {
                handler.sendEmptyMessage(0);
            }
        });
        Map<String, Object> map = new HashMap<>();
        map.put("userId", userId);
        httpUtils.sendGetHttp(url, map);
    }

    private void sendAddHttp(String url, String title, Integer userId) {
        HttpUtils httpUtils = new HttpUtils(new HttpUtils.IHttpResponseListener() {
            @Override
            public void onSuccess(String json) {
                parseAddJson(json);
                handler.sendEmptyMessage(9);
            }

            @Override
            public void onFail(String error) {
                handler.sendEmptyMessage(0);
            }
        });
        Map<String, Object> map = new HashMap<>();
        map.put("title", title);
        map.put("userId", userId);
        httpUtils.sendPostHttp(url, map);
    }

    private void parseAddJson(String json) {
        try {
            JSONObject jsonObject = new JSONObject(json);
            int code = jsonObject.optInt("code");
            addCode = code;
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void parseTypeJson(String json) {
        try {
            JSONObject jsonObject = new JSONObject(json);
            JSONObject response = jsonObject.optJSONObject("response");
            JSONArray list = response.optJSONArray("list");
            for (int i = 0; i < list.length(); i++) {
                JSONObject o = (JSONObject) list.get(i);
                String title = o.optString("title");
                int id = o.optInt("id");
                Type type = new Type(id, title);
                typeNames.add(type);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void parseJson(String json) {
        try {
            JSONObject jsonObject = new JSONObject(json);
            JSONArray song = jsonObject.optJSONArray("song_list");
            for (int i = 0; i < song.length(); i++) {
                RecommendMusic recommendMusic = JSON.parseObject(song.get(i).toString(), RecommendMusic.class);
                images.add(recommendMusic.getPic_big());
                list.add(recommendMusic);
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


