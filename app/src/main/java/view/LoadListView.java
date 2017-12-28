package view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;

import com.example.gy.musicgame.R;

/**
 * Created by Administrator on 2017/8/17.
 */

public class LoadListView extends ListView implements AbsListView.OnScrollListener {
    public View footer;//底部布局
    private int lastVisibleItem;//最后一个可见的item
    private int totalItemCount;//总数量
    private boolean isLoading;//正在加载
    private ILoadListener loadListener;
    //记录滑到底部的那个位置
    private int count;

    public void setLoadListener(ILoadListener loadListener) {
        this.loadListener = loadListener;
    }

    public LoadListView(Context context) {
        super(context);
        initView(context);
    }

    public LoadListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public LoadListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    /**
     * 添加底部布局到listview
     *
     * @param context
     */
    private void initView(Context context) {
        LayoutInflater inflater = LayoutInflater.from(context);
        footer = inflater.inflate(R.layout.activity_loading, null);
        this.addFooterView(footer);
        footer.findViewById(R.id.load_layout).setVisibility(GONE);
        this.setOnScrollListener(this);
    }

    @Override
    public void onScrollStateChanged(AbsListView absListView, int scrollState) {
        if (totalItemCount == lastVisibleItem
                && scrollState == SCROLL_STATE_IDLE) {
            if (!isLoading) {
                isLoading = true;
                footer.findViewById(R.id.load_layout).setVisibility(VISIBLE);
                count = this.getCount() - 1;
                //加载更多
                loadListener.onLoad();
            }
        }
    }

    /**
     * 加载完成
     */
    public void loadComplete() {
        isLoading = false;
        footer.findViewById(R.id.load_layout).setVisibility(GONE);
        //设置选中项
        this.setSelection(count);
    }

    @Override
    public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        this.lastVisibleItem = firstVisibleItem + visibleItemCount;
        this.totalItemCount = totalItemCount;
    }

    //加载更多数据的回调接口
    public interface ILoadListener {
        void onLoad();
    }
}
