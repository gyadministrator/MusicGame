package application;

import com.mob.MobApplication;
import com.mob.MobSDK;

public class MyApplication extends MobApplication {
    @Override
    public void onCreate() {
        super.onCreate();
        CrashHandler.getInstance().init(this);
        //初始化mobIM
        MobSDK.init(this);
    }
}
