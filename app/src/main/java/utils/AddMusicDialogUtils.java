package utils;

import android.app.Dialog;
import android.content.Context;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.example.gy.musicgame.R;

/**
 * Created by Administrator on 2017/8/30.
 */

public class AddMusicDialogUtils {
    private static Dialog dialog = null;
    public static ListView listView;
    public static RelativeLayout add_music_list_rel;

    public static void show(Context context) {
        if (dialog == null) {
            dialog = new Dialog(context, R.style.dialog);
            dialog.setContentView(R.layout.add_music_list);
            //dialog.setCancelable(false);

            listView = dialog.findViewById(R.id.add_music_list);
            add_music_list_rel = dialog.findViewById(R.id.add_music_list_rel);
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
