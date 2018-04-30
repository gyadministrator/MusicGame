package application;

import com.mob.MobApplication;

public class MyApplication extends MobApplication {
    @Override
    public void onCreate() {
        super.onCreate();
        CrashHandler.getInstance().init(this);
    }
}
