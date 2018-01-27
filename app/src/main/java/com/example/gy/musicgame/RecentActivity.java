package com.example.gy.musicgame;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import base.BaseActivity;
import butterknife.BindView;
import butterknife.ButterKnife;

public class RecentActivity extends BaseActivity implements View.OnClickListener {
    @BindView(R.id.cha)
    TextView cha;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recent);
        ButterKnife.bind(this);

        cha.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cha:
                finish();
                break;
            default:
                break;
        }
    }
}
