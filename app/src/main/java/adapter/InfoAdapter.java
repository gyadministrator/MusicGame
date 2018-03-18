package adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.gy.musicgame.R;

import java.util.List;

import bean.Info;

/**
 * Created by Administrator on 2018/3/18.
 */

public class InfoAdapter extends BaseAdapter {
    private Context mContext;
    private List<Info> allValues;

    public InfoAdapter(Context mContext, List<Info> allValues) {
        this.mContext = mContext;
        this.allValues = allValues;
    }

    @Override
    public int getCount() {
        return allValues.size();
    }

    @Override
    public Object getItem(int position) {
        return allValues.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.activity_info_adapter, null);
        }
        Info info = allValues.get(position);
        TextView title = (TextView) convertView.findViewById(R.id.info_title);
        String title_txt = info.getTitle();
        if (title_txt.length() > 20) {
            title_txt = title_txt.substring(0, 20) + "...";
            title.setText(title_txt);
        } else {
            title.setText(title_txt);
        }
        return convertView;
    }
}
