package view;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.example.gy.musicgame.R;
import com.example.gy.musicgame.SingerInfoActivity;

import utils.CurrentMusicUtils;

public class MyPopWindow extends PopupWindow implements View.OnClickListener {

    private float density = 1.0f;
    private Context mContext;

    public MyPopWindow(Context context) {
        mContext = context;
        initPopupWindow();
        View view = View.inflate(context, R.layout.lrc_more, null);
        setContentView(view);
        //设置popwindow的宽高，这个数字是多少就设置多少dp，注意单位是dp
        setHeight((int) (150 * density));
        setWidth((int) (100 * density));

        LinearLayout lrc_more_share = view.findViewById(R.id.lrc_more_share);
        LinearLayout lrc_more_find = view.findViewById(R.id.lrc_more_find);

        lrc_more_share.setOnClickListener(this);
        lrc_more_find.setOnClickListener(this);
    }

    //初始化popwindow
    private void initPopupWindow() {
        setAnimationStyle(R.style.popwindowAnim);//设置动画
        setFocusable(true);
        setOutsideTouchable(true);
        setBackgroundDrawable(new BitmapDrawable());
        density = mContext.getResources().getDisplayMetrics().density;//
    }

    /**
     * 显示popupWindow
     */
    public void showPopupWindow(View parent) {
        if (!this.isShowing()) {
            // 以下拉方式显示popupwindow
            this.showAsDropDown(parent, parent.getLayoutParams().width / 2, 18);
        } else {
            this.dismiss();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.lrc_more_share:
                this.dismiss();
                //分享
                break;
            case R.id.lrc_more_find:
                this.dismiss();
                //查看歌手
                Intent intent = new Intent(mContext, SingerInfoActivity.class);
                String tinguid = CurrentMusicUtils.getRecommendMusic().getTing_uid();
                intent.putExtra("tinguid", tinguid);
                mContext.startActivity(intent);
                break;
            default:
                break;
        }
    }
}