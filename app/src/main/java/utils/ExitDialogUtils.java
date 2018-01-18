package utils;

import android.app.Dialog;
import android.content.Context;
import android.widget.Button;

import com.example.gy.musicgame.R;

/**
 * Created by Administrator on 2017/8/30.
 */

public class ExitDialogUtils {
    private static Dialog dialog = null;

    public static Button cancel;
    public static Button sure;

    public static void show(Context context) {
        if (dialog == null) {
            dialog = new Dialog(context, R.style.dialog);
            dialog.setContentView(R.layout.activity_exit);
            dialog.setCancelable(false);

            cancel = (Button) dialog.findViewById(R.id.cancel);
            sure = (Button) dialog.findViewById(R.id.sure);
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
