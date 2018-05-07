package utils;

import android.app.Dialog;
import android.content.Context;
import android.widget.TextView;

import com.example.gy.musicgame.R;

/**
 * Created by Administrator on 2017/8/30.
 */

public class DialogUtils {
    private static Dialog dialog = null;

    public static void show(Context context, String msg) {
        if (dialog == null) {
            dialog = new Dialog(context, R.style.dialog);
            dialog.setContentView(R.layout.activity_dialog);
            dialog.setCancelable(false);
            TextView msg_info = dialog.findViewById(R.id.msg_info);
            msg_info.setText(msg);
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
