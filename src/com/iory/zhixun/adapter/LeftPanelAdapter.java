package com.iory.zhixun.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.iory.zhixun.R;

import java.util.List;
import java.util.Map;

/**
 * Created by 0 on 13-7-3.
 */
public class LeftPanelAdapter extends BaseAdapter{
    private Context mContext;

    public LeftPanelAdapter(Context context){
        mContext = context;
    }


    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.row, null);
        }
        ImageView icon = (ImageView) convertView.findViewById(R.id.row_icon);

        return convertView;
    }


}
