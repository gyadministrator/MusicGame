package utils;

import android.content.Context;
import android.view.WindowManager;

/**
 * Created by Administrator on 2017/9/6.
 */

public class ScreenUtils {
    public static int width;
    public static int height;

    public static void getScreen(Context context) {
        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        width = wm.getDefaultDisplay().getWidth();
        height = wm.getDefaultDisplay().getHeight();
    }
}
