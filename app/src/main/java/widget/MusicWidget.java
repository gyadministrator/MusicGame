package widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.graphics.Bitmap;
import android.widget.RemoteViews;

import com.example.gy.musicgame.R;

/**
 * Implementation of App Widget functionality.
 */
public class MusicWidget extends AppWidgetProvider {
//保存各个小工具的id
    private static int [] sAppWidgetIds;
    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        //  the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.music_widget);
        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
        //保存各个小工具的id，以备将来其它组件调用performUpdates()接口时能用到
        sAppWidgetIds = appWidgetIds;
        //更新小工具的界面
        performUpdates(context, context.getString(R.string.no_song), false, null);
    }
    //对外提供的更新所有小工具的界面接口，需要传入音乐的名称、当前是否播放、音乐封面等参数
    public static void performUpdates(Context context, String musicName, boolean isPlaying, Bitmap thumb) {
        //如果没有小工具的id，就没法更新界面
        if(sAppWidgetIds == null || sAppWidgetIds.length == 0) {

            return;
        }

        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);

        //遍历每个桌面上的小工具，根据id逐个更新界面
        for (int appWidgetId : sAppWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId, musicName, isPlaying, thumb);
        }
    }
    //更新指定id的小工具界面
    private static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                        int appWidgetId, String musicName, boolean isPlaying, Bitmap thumb) {

        //创建RemoteViews
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.music_widget);

        //添加界面元素的逻辑控制代码，例如按钮、文字、图片等等
        //通过appWidgetId，为指定的小工具界面更新
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }
    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

