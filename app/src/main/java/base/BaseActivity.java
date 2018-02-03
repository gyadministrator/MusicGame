package base;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Process;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;

import com.example.gy.musicgame.MainActivity;
import com.example.gy.musicgame.R;
import utils.ActivityController;
import utils.ToastUtils;

/**
 * Created by 高运 on 2017/5/14.
 */
public class BaseActivity extends FragmentActivity {
    private boolean flag = false;

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
                ActivityController.removeAllActivity();
                Process.killProcess(Process.myPid());
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

}
