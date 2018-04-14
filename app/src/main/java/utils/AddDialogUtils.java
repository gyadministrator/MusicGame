package utils;

import android.app.Dialog;
import android.content.Context;
import android.widget.Button;
import android.widget.EditText;

import com.example.gy.musicgame.R;

/**
 * Created by Administrator on 2017/8/30.
 */

public class AddDialogUtils {
    private static Dialog dialog = null;
    public static EditText editText;
    public static Button ok;
    public static Button cancel;

    public static void show(Context context) {
        if (dialog == null) {
            dialog = new Dialog(context, R.style.dialog);
            dialog.setContentView(R.layout.add_list);
            dialog.setCancelable(false);

            editText = dialog.findViewById(R.id.add_list_edit);
            ok = dialog.findViewById(R.id.ok);
            cancel = dialog.findViewById(R.id.cancel);
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
