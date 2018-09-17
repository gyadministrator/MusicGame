package adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.gy.musicgame.R;
import com.squareup.picasso.Picasso;

import java.util.List;

import bean.MyMusic;
import bean.RecommendMusic;

/**
 * Created by Administrator on 2017/10/14.
 */

public class MyMusicListAdapter extends BaseAdapter {
    private List<MyMusic> allValues;
    private Context mContext;

    public MyMusicListAdapter(List<MyMusic> allValues, Context mContext) {
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.music_list_adapter, null);
        }
        ImageView music_image = convertView.findViewById(R.id.music_image);
        TextView music_title = convertView.findViewById(R.id.music_title);
        TextView music_des = convertView.findViewById(R.id.music_des);
        MyMusic myMusic = allValues.get(position);
        Picasso.get().load(myMusic.getImg()).into(music_image);
        String musicTitle = myMusic.getName();
        if (musicTitle.length() > 10) {
            musicTitle = musicTitle.substring(0, 10) + "...";
        }
        music_title.setText(musicTitle);
        music_des.setText(myMusic.getAuthor());
        return convertView;
    }
}
