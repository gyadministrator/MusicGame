package utils;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.view.View;

/**
 * Created by Administrator on 2017/8/24.
 */

public class NetWorkUtils {
    private static ConnectivityManager manager;

    /**
     * 检测网络是否连接
     *
     * @return
     */
    public static boolean checkNetworkState(Context context) {
        boolean flag;
        ConnectivityManager con = (ConnectivityManager) context.getSystemService(Activity.CONNECTIVITY_SERVICE);
        boolean wifi = con.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnectedOrConnecting();
        boolean internet = con.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).isConnectedOrConnecting();
        if (wifi | internet) {
            //执行相关操作
            flag = true;
        } else {
            flag = false;
        }
        return flag;
    }


    /**
     * 网络未连接时，调用设置方法
     */
    public static void setNetwork(final Context context, final View view) {
        Intent intent = null;
        /**
         * 判断手机系统的版本！如果API大于10 就是3.0+
         * 因为3.0以上的版本的设置和3.0以下的设置不一样，调用的方法不同
         */
        if (android.os.Build.VERSION.SDK_INT > 10) {
            intent = new Intent(android.provider.Settings.ACTION_SETTINGS);
        } else {
            intent = new Intent();
            ComponentName component = new ComponentName(
                    "com.android.settings",
                    "com.android.settings.WirelessSettings");
            intent.setComponent(component);
            intent.setAction("android.intent.action.VIEW");
        }
        context.startActivity(intent);
        view.setVisibility(View.GONE);
    }
}
