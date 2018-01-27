package com.example.gy.musicgame;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import java.util.ArrayList;
import java.util.List;

import base.BaseActivity;
import base.BaseFragment;
import butterknife.BindView;
import butterknife.ButterKnife;
import fragment.IndexFragment;
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

        now_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, RecentActivity.class);
                startActivity(intent);
            }
        });
    }

    private void initFragment() {
        fragments.add(new IndexFragment());
        fragments.add(new ListenFragment());
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
                case R.id.music_me:
                    position = 2;
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
