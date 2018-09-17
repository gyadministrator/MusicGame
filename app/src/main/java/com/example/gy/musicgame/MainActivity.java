package com.example.gy.musicgame;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import java.util.ArrayList;
import java.util.List;

import abc.abc.abc.nm.bn.BannerManager;
import abc.abc.abc.nm.cm.ErrorCode;
import abc.abc.abc.nm.sp.SpotListener;
import abc.abc.abc.nm.sp.SpotManager;
import base.BaseActivity;
import base.BaseFragment;
import butterknife.BindView;
import butterknife.ButterKnife;
import fragment.IndexFragment;
import fragment.InfoFragment;
import fragment.ListenFragment;
import fragment.MyFragment;
import utils.NotificationPermissionUtils;

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
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initFragment();
        setListener();

        setupSpotAd();

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

    /**
     * 设置插屏广告
     */
    private void setupSpotAd() {
        SpotManager.getInstance(this).setImageType(SpotManager.IMAGE_TYPE_VERTICAL);
        // 高级动画
        SpotManager.getInstance(this)
                .setAnimationType(SpotManager.ANIMATION_TYPE_ADVANCED);
        // 展示插屏广告
        SpotManager.getInstance(this).showSpot(this, new SpotListener() {
            @Override
            public void onShowSuccess() {
                //logInfo("插屏展示成功");
            }

            @Override
            public void onShowFailed(int errorCode) {
                //logError("插屏展示失败");
                switch (errorCode) {
                    case ErrorCode.NON_NETWORK:
                        //showShortToast("网络异常");
                        break;
                    case ErrorCode.NON_AD:
                        //showShortToast("暂无插屏广告");
                        break;
                    case ErrorCode.RESOURCE_NOT_READY:
                        //showShortToast("插屏资源还没准备好");
                        break;
                    case ErrorCode.SHOW_INTERVAL_LIMITED:
                        //showShortToast("请勿频繁展示");
                        break;
                    case ErrorCode.WIDGET_NOT_IN_VISIBILITY_STATE:
                        //showShortToast("请设置插屏为可见状态");
                        break;
                    default:
                        //showShortToast("请稍后再试");
                        break;
                }
            }

            @Override
            public void onSpotClosed() {
                //logDebug("插屏被关闭");
            }

            @Override
            public void onSpotClicked(boolean isWebPage) {
                //logDebug("插屏被点击");
                //logInfo("是否是网页广告？%s", isWebPage ? "是" : "不是");
            }
        });

    }

    @Override
    public void onBackPressed() {
        // 点击后退关闭插屏广告
        if (SpotManager.getInstance(this).isSpotShowing()) {
            SpotManager.getInstance(this).hideSpot();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // 插屏广告
        SpotManager.getInstance(this).onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        // 插屏广告
        SpotManager.getInstance(this).onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 插屏广告
        SpotManager.getInstance(this).onDestroy();
        // 展示广告条窗口的 onDestroy() 回调方法中调用
        BannerManager.getInstance(this).onDestroy();
    }

}
