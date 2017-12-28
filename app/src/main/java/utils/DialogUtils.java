package utils;

import android.app.Dialog;
import android.content.Context;

import com.example.gy.musicgame.R;

/**
 * Created by Administrator on 2017/8/30.
 */

public class DialogUtils {
    private static Dialog dialog = null;

    public static void show(Context context) {
        if (dialog == null) {
            dialog = new Dialog(context, R.style.dialog);
            dialog.setContentView(R.layout.activity_dialog);
            dialog.setCancelable(false);
        }
        dialog.show();
    }

    public static void hidden() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
            dialog = null;
        }
    }
}
