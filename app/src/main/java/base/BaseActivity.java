package base;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Process;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;

import com.example.gy.musicgame.MainActivity;
import com.example.gy.musicgame.R;

import abc.abc.abc.nm.sp.SpotManager;
import bean.Music;
import bean.RecommendMusic;
import utils.ActivityController;
import utils.CurrentMusicUtils;
import utils.MusicUtils;
import utils.NotificationUtils;
import utils.ToastUtils;

/**
 * Created by 高运 on 2017/5/14.
 */
public class BaseActivity extends FragmentActivity {
    private boolean flag = false;
    private myThread myThread;

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityController.addActivity(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityController.removeActivity(this);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && getClass().getName().equals(MainActivity.class.getName())) {
            if (!flag) {
                flag = true;
                ToastUtils.showToast(this, R.mipmap.music_warning, "再按一次返回键回到桌面");
                new Handler().postDelayed(r, 2000);
                return true;
            } else {
                RecommendMusic recommendMusic = CurrentMusicUtils.getRecommendMusic();
                Music music = new Music();
                if (myThread != null) {
                    myThread.interrupt();
                }
                if (recommendMusic != null) {
                    music.setTitle(recommendMusic.getTitle());
                    music.setAuthor(recommendMusic.getAuthor());
                    music.setPic_big(recommendMusic.getPic_big());
                    music.setFile_duration(MusicUtils.mediaPlayer.getDuration());
                    NotificationUtils.showNotification(this, music);

                    //开启线程监听
                    myThread = new myThread(MusicUtils.mediaPlayer.getDuration());
                    myThread.start();
                    //ActivityController.removeAllActivity();
                }
                SpotManager.getInstance(this).onAppExit();
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    private Runnable r = new Runnable() {
        @Override
        public void run() {
            flag = false;
        }
    };

    class myThread extends Thread {
        private int time;

        myThread(int time) {
            this.time = time;
        }

        @Override
        public void run() {
            super.run();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    NotificationUtils.closeNotification();
                    Process.killProcess(Process.myPid());
                }
            }, time * 1000);
        }
    }

    /***
     * 带动画启动  activity
     * @param intent
     */
    protected void startActivityWithAnim(Intent intent){
        startActivity(intent);
        overridePendingTransition(R.anim.default_fromright_in,R.anim.default_toleft_out);
    }

    /***
     * 带动画退出  activity
     */
    protected void finishWithAnim(){
        finish();
        overridePendingTransition(R.anim.default_fromright_in,R.anim.default_toleft_out);
    }
}
