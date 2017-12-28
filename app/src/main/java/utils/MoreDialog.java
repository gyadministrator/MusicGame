package utils;

import android.app.Dialog;
import android.content.Context;
import android.widget.LinearLayout;

import com.example.gy.musicgame.R;

/**
 * Created by Administrator on 2017/10/18.
 */

public class MoreDialog {
    private static Dialog dialog = null;
    public static LinearLayout find;
    public static LinearLayout download;
    public static LinearLayout cancel;

    public static void show(Context context) {
        if (dialog == null) {
            dialog = new Dialog(context, R.style.dialog);
            dialog.setContentView(R.layout.activity_more_dialog);
            //dialog.setCancelable(false);
            find = (LinearLayout) dialog.findViewById(R.id.find);
            download = (LinearLayout) dialog.findViewById(R.id.download);
            cancel = (LinearLayout) dialog.findViewById(R.id.cancel);
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
