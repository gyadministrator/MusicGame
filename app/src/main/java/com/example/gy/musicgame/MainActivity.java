package com.example.gy.musicgame;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import java.util.ArrayList;
import java.util.List;

import base.BaseActivity;
import base.BaseFragment;
import butterknife.BindView;
import butterknife.ButterKnife;
import cn.waps.AppConnect;
import cn.waps.AppListener;
import fragment.IndexFragment;
import fragment.InfoFragment;
import fragment.ListenFragment;
import fragment.MyFragment;
import utils.ToastUtils;

public class MainActivity extends BaseActivity {
    @BindView(R.id.music_index)
    RadioButton music_index;
    @BindView(R.id.music_listen)
    RadioButton music_listen;
    @BindView(R.id.music_me)
    RadioButton music_me;
    @BindView(R.id.rg)
    RadioGroup rg;
    @BindView(R.id.now_play)
    FloatingActionButton now_play;
    private List<BaseFragment> fragments = new ArrayList<>();
    private int position;
    private Fragment mContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initFragment();
        setListener();

        AppConnect.getInstance(this).initPopAd(this);
        AppConnect.getInstance(this).showPopAd(this, new AppListener() {
            @Override
            public void onPopClose() {
                super.onPopClose();
            }
        });


        //设置广告
        setAds();

        now_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, RecentActivity.class);
                startActivity(intent);
            }
        });
    }

    private void setAds() {
        final LinearLayout adlayout = (LinearLayout) findViewById(R.id.AdLinearLayout);
        AppConnect.getInstance(this).setBannerAdNoDataListener(new AppListener() {
            @Override
            public void onBannerNoData() {
                super.onBannerNoData();
                adlayout.setVisibility(View.GONE);
            }

            @Override
            public void onBannerClose() {
                super.onBannerClose();
                adlayout.setVisibility(View.GONE);
            }
        });
        AppConnect.getInstance(this).showBannerAd(this, adlayout);
    }

    private void initFragment() {
        fragments.add(new IndexFragment());
        fragments.add(new ListenFragment());
        fragments.add(new InfoFragment());
        fragments.add(new MyFragment());
    }

    private void setListener() {
        rg.setOnCheckedChangeListener(new MyCheckedChangeListener());
        rg.check(R.id.music_index);
    }

    class MyCheckedChangeListener implements RadioGroup.OnCheckedChangeListener {

        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            switch (checkedId) {
                case R.id.music_index:
                    position = 0;
                    break;
                case R.id.music_listen:
                    position = 1;
                    break;
                case R.id.music_info:
                    position = 2;
                    break;
                case R.id.music_me:
                    position = 3;
                    break;
                default:
                    position = 0;
                    break;
            }
            Fragment to = getFragment();
            switchFragment(mContent, to);
        }
    }

    private void switchFragment(Fragment from, Fragment to) {
        if (from != to) {
            mContent = to;
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            if (!to.isAdded()) {
                if (from != null) {
                    fragmentTransaction.hide(from);
                }
                fragmentTransaction.add(R.id.main_container, to).commit();
            } else {
                if (from != null) {
                    fragmentTransaction.hide(from);
                }
                fragmentTransaction.show(to).commit();
            }
        }
    }

    private Fragment getFragment() {
        return fragments.get(position);
    }

}
