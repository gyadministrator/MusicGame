package adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.gy.musicgame.R;

import java.util.List;

import bean.Type;

public class TypeAdapter extends BaseAdapter {
    private List<Type> allValues;
    private Context mContext;

    public TypeAdapter(List<Type> allValues, Context mContext) {
        this.allValues = allValues;
        this.mContext = mContext;
    }

    @Override
    public int getCount() {
        return allValues.size();
    }

    @Override
    public Object getItem(int i) {
        return allValues.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = LayoutInflater.from(mContext).inflate(R.layout.music_type_adapter, null);
        }
        TextView title = view.findViewById(R.id.type_title);
        Type type = allValues.get(i);
        title.setText(type.getTitle());
        return view;
    }
}
