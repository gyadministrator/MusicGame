package fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.example.gy.musicgame.InfoDetailActivity;
import com.example.gy.musicgame.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import adapter.InfoAdapter;
import base.BaseFragment;
import bean.Info;
import butterknife.BindView;
import butterknife.ButterKnife;
import utils.Constant;
import utils.DialogUtils;
import utils.HttpUtils;
import utils.ImmersedStatusbarUtils;
import utils.TimeUtils;
import utils.ToastUtils;

/**
 * Created by Administrator on 2018/3/18.
 */

public class InfoFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener, AdapterView.OnItemClickListener {
    @BindView(R.id.lin)
    LinearLayout lin;
    @BindView(R.id.listView)
    ListView listView;
    @BindView(R.id.rel_no_msg)
    RelativeLayout rel_no_msg;
    @BindView(R.id.swipe)
    SwipeRefreshLayout swipeRefreshLayout;


    List<Info> infoList = new ArrayList<>();
    InfoAdapter adapter;

    String url = Constant.BASE_URL + "/info/queryAllInfo";
    Map<String, Object> map;

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {
                DialogUtils.hidden();
                if (infoList.size() == 0) {
                    rel_no_msg.setVisibility(View.VISIBLE);
                } else {
                    adapter = new InfoAdapter(mContext, infoList);
                    listView.setAdapter(adapter);
                    rel_no_msg.setVisibility(View.GONE);
                }
            } else if (msg.what == 0) {
                rel_no_msg.setVisibility(View.VISIBLE);
                DialogUtils.hidden();
                ToastUtils.showToast(mContext, R.mipmap.music_icon, "获取消息错误");
            }
            if (msg.what == 2) {
                DialogUtils.hidden();
                if (infoList.size() == 0) {
                    rel_no_msg.setVisibility(View.VISIBLE);
                } else {
                    adapter = new InfoAdapter(mContext, infoList);
                    listView.setAdapter(adapter);
                    rel_no_msg.setVisibility(View.GONE);
                }
                swipeRefreshLayout.setRefreshing(false);
            }
        }
    };

    @Override
    protected View initView() {
        View view = LayoutInflater.from(mContext).inflate(R.layout.activity_info, null);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    protected void initData() {
        super.initData();

        /*设置沉侵式导航栏*/
        ImmersedStatusbarUtils.initAfterSetContentView(getActivity(), lin);

        swipeRefreshLayout.setOnRefreshListener(this);

        listView.setOnItemClickListener(this);

        map = new HashMap<>();
        map.put("param", 0);
        send(url, map, 1);
    }

    private void send(String url, Map<String, Object> map, final int i) {
        DialogUtils.show(mContext);
        HttpUtils httpUtils = new HttpUtils(new HttpUtils.IHttpResponseListener() {
            @Override
            public void onSuccess(String json) {
                parseJson(json);
                handler.sendEmptyMessage(i);
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
            JSONObject response = jsonObject.optJSONObject("response");
            JSONArray list = response.optJSONArray("list");
            if (list != null) {
                for (int i = 0; i < list.length(); i++) {
                    JSONObject o = (JSONObject) list.get(i);
                    int id = o.optInt("id");
                    String title = o.optString("title");
                    String content = o.optString("content");
                    String time = o.optString("time");
                    String date = TimeUtils.stampToDate(time);
                    Info info = new Info();
                    info.setId(id);
                    info.setTitle(title);
                    info.setContent(content);
                    info.setTime(date);
                    infoList.add(info);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRefresh() {
        infoList.clear();
        send(url, map, 2);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Info info = infoList.get(position);
        Bundle bundle = new Bundle();
        bundle.putSerializable("info", info);
        Intent intent = new Intent(mContext, InfoDetailActivity.class);
        intent.putExtra("info", bundle);
        startActivity(intent);
    }
}
