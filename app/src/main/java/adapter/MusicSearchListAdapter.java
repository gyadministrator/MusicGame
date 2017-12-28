package adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.gy.musicgame.R;
import com.squareup.picasso.Picasso;

import java.util.List;

import bean.RecommendMusic;
import bean.SearchSong;
import utils.MoreDialog;

/**
 * Created by Administrator on 2017/10/14.
 */

public class MusicSearchListAdapter extends BaseAdapter {
    private List<SearchSong> allValues;
    private Context mContext;

    public MusicSearchListAdapter(List<SearchSong> allValues, Context mContext) {
        this.allValues = allValues;
        this.mContext = mContext;
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.music_search_list_adapter, null);
        }
        TextView music_title = (TextView) convertView.findViewById(R.id.music_title);
        TextView music_des = (TextView) convertView.findViewById(R.id.music_des);
        SearchSong searchSong = allValues.get(position);
        music_title.setText(searchSong.getSongname());
        music_des.setText(searchSong.getArtistname());
        return convertView;
    }
}
