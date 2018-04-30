package view;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.example.gy.musicgame.R;
import com.example.gy.musicgame.SingerInfoActivity;

import bean.RecommendMusic;
import cn.sharesdk.onekeyshare.OnekeyShare;
import utils.CurrentMusicUtils;

import static com.mob.tools.utils.Strings.getString;

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
                showShare();
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

    private void showShare() {
        RecommendMusic recommendMusic = CurrentMusicUtils.getRecommendMusic();
        OnekeyShare oks = new OnekeyShare();
        //关闭sso授权
        oks.disableSSOWhenAuthorize();

        // 分享时Notification的图标和文字  2.5.9以后的版本不调用此方法
        //oks.setNotification(R.drawable.ic_launcher, getString(R.string.app_name));
        // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
        oks.setTitle(getString(R.string.share));
        // titleUrl是标题的网络链接，仅在人人网和QQ空间使用
        //oks.setTitleUrl();
        // text是分享文本，所有平台都需要这个字段
        oks.setText(recommendMusic.getTitle());
        // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
        oks.setImagePath(recommendMusic.getPic_big());//确保SDcard下面存在此张图片
        // url仅在微信（包括好友和朋友圈）中使用
        //oks.setUrl("http://sharesdk.cn");
        // comment是我对这条分享的评论，仅在人人网和QQ空间使用
        oks.setComment("说点啥吧...");
        // site是分享此内容的网站名称，仅在QQ空间使用
        oks.setSite(getString(R.string.app_name));
        // siteUrl是分享此内容的网站地址，仅在QQ空间使用
        //oks.setSiteUrl(recommendMusic.get);

        // 启动分享GUI
        oks.show(mContext);
    }
}