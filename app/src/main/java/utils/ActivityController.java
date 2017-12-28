package utils;

import android.app.Activity;
import android.os.Process;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by 高运 on 2017/5/14.
 */

public class ActivityController {
    private static List<Activity> activities = new LinkedList<>();

    public static void addActivity(Activity activity) {
        activities.add(activity);
    }

    public static void removeActivity(Activity activity) {
        activities.remove(activity);
    }

    public static void removeAllActivity() {
        for (Activity activity : activities) {
            activity.finish();
        }
        Process.killProcess(Process.myPid());
    }
}
