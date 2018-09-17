package fragment;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.example.gy.musicgame.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import base.BaseFragment;
import bean.SearchSong;
import bean.User;
import butterknife.BindView;
import butterknife.ButterKnife;
import utils.Constant;
import utils.DialogUtils;
import utils.HttpUtils;
import utils.MusicUtils;
import utils.NetWorkUtils;
import utils.NotificationPermissionUtils;
import utils.ToastMoneyUtils;
import utils.ToastUtils;
import view.CircleImageView;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Administrator on 2017/9/12.
 */

public class IndexFragment extends BaseFragment implements View.OnClickListener {
    @BindView(R.id.time_tv)
    TextView time_tv;
    @BindView(R.id.type_edit)
    EditText type_edit;
    @BindView(R.id.search_type)
    TextView search_type;
    @BindView(R.id.answer_text)
    RelativeLayout answer_text;
    @BindView(R.id.msg)
    TextView msg;
    @BindView(R.id.ok_answer)
    TextView ok_answer;
    @BindView(R.id.next)
    Button next;
    @BindView(R.id.answer_rel)
    RelativeLayout answer_rel;
    @BindView(R.id.answer_edit)
    EditText answer_edit;
    @BindView(R.id.ok)
    Button ok;
    @BindView(R.id.answer_linear)
    LinearLayout answer_linear;
    @BindView(R.id.lin)
    LinearLayout lin;
    @BindView(R.id.index_img)
    CircleImageView index_img;
    private static final String URL = Constant.BASE_URL + "/music/GetSearchSong";
    private static Map<String, Object> map = new HashMap<>();
    private List<SearchSong> list = new ArrayList<>();
    private static List<String> playUrls = new ArrayList<>();
    private static int time = 30;
    private static CountDownTimer timer;
    private static User user;
    private static int code;
    private static int star;
    //用来播放的变量
    private static int num = 1;
    private boolean b = false;
    private static final String TAG = "IndexFragment";
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {
                DialogUtils.hidden();
                if (list.size() == 0) {
                    ToastUtils.showToast(mContext, R.mipmap.music_icon, "没有搜到相关结果");
                } else {
                    ToastUtils.showToast(mContext, R.mipmap.music_icon, "可以开始了,为你加载了" + list.size() + "条数据");
                    //默认播放第一首
                    if (NetWorkUtils.checkNetworkState(mContext)) {
                        getPlayUrls(0, 3);
                        initTime();
                    } else {
                        ToastUtils.showToast(mContext, R.mipmap.music_warning, "无网络连接");
                    }
                }
            } else if (msg.what == 0) {
                DialogUtils.hidden();
                ToastUtils.showToast(mContext, R.mipmap.music_icon, "发生了错误");
            } else if (msg.what == 3) {
                //播放音乐
                if (NetWorkUtils.checkNetworkState(mContext)) {
                    for (int i = 0; i < playUrls.size(); i++) {
                        MusicUtils.play(playUrls.get(i), mContext);
                    }
                } else {
                    ToastUtils.showToast(mContext, R.mipmap.music_warning, "无网络连接");
                }
                initTime();
            } else if (msg.what == 4) {
                if (code == 100) {
                    DialogUtils.hidden();
                }
            } else if (msg.what == 5) {
                DialogUtils.hidden();
            }
        }
    };

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (hidden) {
            if (timer != null) {
                timer.cancel();
                time_tv.setText("");
                index_img.clearAnimation();
                if (!b) {
                    MusicUtils.stop();
                    ToastUtils.showToast(mContext, R.mipmap.music_icon, "结束了挑战");
                    b = true;
                }
            }
        }
    }

    private void answer() {
        answer_edit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                ok.setBackgroundColor(Color.rgb(158, 158, 158));
                ok.setEnabled(false);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                ok.setBackgroundColor(Color.rgb(47, 207, 175));
                ok.setEnabled(true);
            }
        });
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MusicUtils.mediaPlayer != null) {
                    MusicUtils.stop();
                }
                answer_text.setVisibility(View.VISIBLE);
                answer_rel.setVisibility(View.GONE);
                String name = answer_edit.getText().toString();
                if (name.equals(list.get(num - 1).getSongname())) {
                    if (timer != null) {
                        timer.cancel();
                    }
                    msg.setText("恭喜你,答对了");
                    time_tv.setText("");
                    ToastMoneyUtils.showToast(mContext, R.mipmap.money, "获得奖励+10");
                    user = (User) getActivity().getIntent().getBundleExtra("user").getSerializable("user");
                    if (NetWorkUtils.checkNetworkState(mContext)) {
                        String url = Constant.BASE_URL + "/user/changeStar";
                        Map<String, Object> params = new HashMap<>();
                        params.put("username", user.getUsername());
                        Log.e("star", star + "");
                        params.put("star", star + 10);
                        star = star + 10;
                        sendUpdate(url, params);
                    } else {
                        ToastUtils.showToast(mContext, R.mipmap.music_warning, "无网络连接");
                    }

                } else {
                    msg.setText("哎呀,答错了");
                    time_tv.setText("");
                    if (timer != null) {
                        timer.cancel();
                    }
                }
                ok_answer.setText("正确答案:" + list.get(num - 1).getSongname());
                //获取下一首的地址
                if (NetWorkUtils.checkNetworkState(mContext)) {
                    getPlayUrls(num, 6);
                } else {
                    ToastUtils.showToast(mContext, R.mipmap.music_warning, "无网络，无法加载播放地址");
                }
                //下一首
                next.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        answer_text.setVisibility(View.GONE);
                        answer_rel.setVisibility(View.VISIBLE);
                        answer_edit.setText("");
                        ok.setEnabled(false);
                        ok.setBackgroundColor(Color.rgb(158, 158, 158));
                        next();
                    }
                });
            }
        });
    }

    private void setAnim(View view) {
        //动画
        Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.rotate_anim);
        LinearInterpolator lin = new LinearInterpolator();//设置动画匀速运动
        animation.setInterpolator(lin);
        view.startAnimation(animation);
    }

    private void initTime() {
        setAnim(index_img);
        /** 倒计时60秒，一次1秒 */
        if (timer != null) {
            timer.cancel();
        }
        answer_linear.setVisibility(View.VISIBLE);
        answer();
        time = 30;
        time_tv.setTextColor(Color.GREEN);
        timer = new CountDownTimer(time * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                time = (int) (millisUntilFinished / 1000);
                time_tv.setText("还剩" + millisUntilFinished / 1000 + "秒");
            }

            @Override
            public void onFinish() {
                index_img.clearAnimation();
                time_tv.setTextColor(Color.RED);
                time_tv.setText("时间结束了");
                index_img.clearAnimation();
                //判断是否正确
                MusicUtils.stop();
                answer();
            }
        }.start();
    }

    private void next() {
        initTime();
        //下一首
        if (num == list.size()) {
            ToastUtils.showToast(mContext, R.mipmap.music_icon, "没有更多歌曲了");
        } else {
            if (NetWorkUtils.checkNetworkState(mContext)) {
                MusicUtils.play(playUrls.get(num), mContext);
                num = num + 1;
            } else {
                ToastUtils.showToast(mContext, R.mipmap.music_warning, "无网络连接");
            }
        }
    }

    private void getPlayUrls(int currentNum, final int what) {
        String songid = list.get(currentNum).getSongid();
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
            playUrls.add(show_link);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected View initView() {
        View view = LayoutInflater.from(mContext).inflate(R.layout.activity_index, null);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    protected void initData() {
        super.initData();
        SharedPreferences sharedPreferences = mContext.getSharedPreferences("notice", MODE_PRIVATE);
        boolean b = sharedPreferences.getBoolean("b", true);
        if (b) {
            if (NotificationPermissionUtils.isNotificationEnabled(mContext)) {
                NotificationPermissionUtils.openPermission(mContext);
            }
            SharedPreferences.Editor edit = sharedPreferences.edit();
            edit.putBoolean("b", false);
            edit.apply();
        }
        if (NetWorkUtils.checkNetworkState(mContext)) {
            //重新获取用户最新数据
            user = (User) getActivity().getIntent().getBundleExtra("user").getSerializable("user");
            Map<String, Object> params = new HashMap<>();
            params.put("username", user.getUsername());
            String queryUserUrl = Constant.BASE_URL + "/user/queryUserByName";
            sendQuery(queryUserUrl, params);
        } else {
            ToastUtils.showToast(mContext, R.mipmap.music_warning, "无网络连接");
        }
        search_type.setOnClickListener(this);
    }

    private void sendQuery(String queryUserUrl, Map<String, Object> params) {
        DialogUtils.show(mContext, "获取用户数据中...");
        HttpUtils httpUtils = new HttpUtils(new HttpUtils.IHttpResponseListener() {
            @Override
            public void onSuccess(String json) {
                parseQueryJson(json);
                handler.sendEmptyMessage(5);
            }

            @Override
            public void onFail(String error) {
                handler.sendEmptyMessage(0);
            }
        });
        httpUtils.sendGetHttp(queryUserUrl, params);
    }

    private void parseQueryJson(String json) {
        try {
            JSONObject jsonObject = new JSONObject(json);
            JSONObject response = jsonObject.optJSONObject("response");
            user = JSON.parseObject(response.optJSONObject("user").toString(), User.class);
            star = user.getStar();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.search_type:
                if (NetWorkUtils.checkNetworkState(mContext)) {
                    list.clear();
                    if (type_edit.getText().toString().equals("")) {
                        ToastUtils.showToast(mContext, R.mipmap.music_warning, "请输入挑战类型");
                        return;
                    } else {
                        map.put("query", type_edit.getText().toString());
                        send(URL, map);
                        type_edit.setText("");
                    }
                } else {
                    ToastUtils.showToast(mContext, R.mipmap.music_warning, "无网络连接");
                }
                break;
        }
    }

    private void send(String url, Map<String, Object> map) {
        DialogUtils.show(mContext, "查询中...");
        HttpUtils httpUtils = new HttpUtils(new HttpUtils.IHttpResponseListener() {
            @Override
            public void onSuccess(String json) {
                parseJson(json);
                //准备播放
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

    private void sendUpdate(String url, Map<String, Object> map) {
        HttpUtils httpUtils = new HttpUtils(new HttpUtils.IHttpResponseListener() {
            @Override
            public void onSuccess(String json) {
                parseUpdateJson(json);
                handler.sendEmptyMessage(4);
            }

            @Override
            public void onFail(String error) {
                handler.sendEmptyMessage(0);
            }
        });
        httpUtils.sendGetHttp(url, map);
    }

    private void parseUpdateJson(String json) {
        try {
            JSONObject jsonObject = new JSONObject(json);
            code = jsonObject.getInt("code");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
