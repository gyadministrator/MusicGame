package application;

import android.app.Activity;
import android.app.Application;

import com.example.gy.musicgame.R;

import java.util.ArrayList;

import utils.ToastUtils;

public class MyApplication extends Application {
    ArrayList<Activity> list = new ArrayList<Activity>();

    public void init() {
        //设置该CrashHandler为程序的默认处理器
        UnCeHandler catchExcep = new UnCeHandler(this);
        Thread.setDefaultUncaughtExceptionHandler(catchExcep);
    }

    /**
     * Activity关闭时，删除Activity列表中的Activity对象
     */
    public void removeActivity(Activity a) {
        list.remove(a);
    }

    /**
     * 向Activity列表中添加Activity对象
     */
    public void addActivity(Activity a) {
        list.add(a);
    }

    /**
     * 关闭Activity列表中的所有Activity
     */
    public void finishActivity() {
        for (Activity activity : list) {
            if (null != activity) {
                activity.finish();
            }
        }
        ToastUtils.showToast(getApplicationContext(), R.mipmap.music_warning, "很抱歉,程序出现异常,即将退出.");
        //杀死该应用进程
        android.os.Process.killProcess(android.os.Process.myPid());
    }
}
