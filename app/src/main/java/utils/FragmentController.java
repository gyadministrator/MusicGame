package utils;

import android.app.Activity;
import android.os.Process;
import android.support.v4.app.Fragment;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by 高运 on 2017/5/14.
 */

public class FragmentController {
    private static List<Fragment> fragments = new LinkedList<>();

    public static void addFragment(Fragment fragment) {
        fragments.add(fragment);
    }

    public static void removeFragment(Fragment fragment) {
        fragments.remove(fragment);
    }

    public static void removeAllFragment() {
        for (Fragment fragment : fragments) {
            fragment.onDestroy();
        }
        Process.killProcess(Process.myPid());
    }
}
