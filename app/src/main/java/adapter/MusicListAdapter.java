package adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.gy.musicgame.R;
import com.example.gy.musicgame.SingerInfoActivity;
import com.example.gy.musicgame.StartActivity;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.concurrent.ThreadFactory;

import bean.RecommendMusic;
import utils.DialogUtils;
import utils.MoreDialog;

/**
 * Created by Administrator on 2017/10/14.
 */

public class MusicListAdapter extends BaseAdapter {
    private List<RecommendMusic> allValues;
    private Context mContext;

    public MusicListAdapter(List<RecommendMusic> allValues, Context mContext) {
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
        RecommendMusic recommendMusic = allValues.get(position);
        Picasso.get().load(recommendMusic.getPic_big()).into(music_image);
        String musicTitle = recommendMusic.getTitle();
        if (musicTitle.length() > 15) {
            musicTitle = musicTitle.substring(0, 15) + "...";
        }
        music_title.setText(musicTitle);
        music_des.setText(recommendMusic.getAuthor());
        return convertView;
    }
}
