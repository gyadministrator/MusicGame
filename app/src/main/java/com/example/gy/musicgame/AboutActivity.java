package com.example.gy.musicgame;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import base.BaseActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import utils.ImmersedStatusbarUtils;

public class AboutActivity extends BaseActivity {
    @BindView(R.id.back)
    TextView back;
    @BindView(R.id.lin)
    LinearLayout lin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        ButterKnife.bind(this);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        /*设置沉侵式导航栏*/
        ImmersedStatusbarUtils.initAfterSetContentView(this, lin);
    }
}
