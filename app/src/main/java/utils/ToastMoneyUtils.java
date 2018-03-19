package utils;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gy.musicgame.R;

/**
 * Created by Administrator on 2017/9/7.
 */

public class ToastMoneyUtils extends Toast {
    private static Toast mToast;

    public ToastMoneyUtils(Context context) {
        super(context);
    }

    private static Toast makeText(Context context, int imageId, String msg, int duration) {
        Toast toast = new Toast(context);
        View view = LayoutInflater.from(context).inflate(R.layout.activity_toast_money, null);
        TextView toast_image = (TextView) view.findViewById(R.id.toast_image);
        TextView toast_text = (TextView) view.findViewById(R.id.toast_text);
        toast_image.setBackgroundResource(imageId);
        toast_text.setText(msg);
        toast.setView(view);
        toast.setDuration(duration);
        toast.setGravity(Gravity.CENTER, 0, 0);
        return toast;
    }

    public static void showToast(Context context, int imageId, String content) {
        mToast = ToastMoneyUtils.makeText(context, imageId, content, 100);
        mToast.show();
    }
}
