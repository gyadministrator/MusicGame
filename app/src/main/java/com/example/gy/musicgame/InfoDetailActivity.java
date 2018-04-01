package com.example.gy.musicgame;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import base.BaseActivity;
import bean.Info;
import butterknife.BindView;
import butterknife.ButterKnife;
import utils.ImmersedStatusbarUtils;

public class InfoDetailActivity extends BaseActivity {
    @BindView(R.id.back)
    TextView back;
    @BindView(R.id.title_info)
    TextView title_info;
    @BindView(R.id.content_info)
    TextView content_info;
    @BindView(R.id.time_info)
    TextView time_info;
    @BindView(R.id.lin)
    LinearLayout lin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_detail);
        ButterKnife.bind(this);

        /*设置沉侵式导航栏*/
        ImmersedStatusbarUtils.initAfterSetContentView(this, lin);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Info info = (Info) getIntent().getBundleExtra("info").getSerializable("info");

        if (info != null) {
            title_info.setText(info.getTitle());
            content_info.setText(info.getContent());
            time_info.setText(info.getTime());
        } else {
            title_info.setText("无标题");
            content_info.setText("无内容");
            title_info.setText("无发布时间");
        }

    }
}
