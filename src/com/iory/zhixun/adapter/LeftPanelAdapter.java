package com.iory.zhixun.adapter;

import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.iory.zhixun.R;
import com.iory.zhixun.data.LeftPanelItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by 0 on 13-7-3.
 */
public class LeftPanelAdapter extends BaseAdapter{
    private Context mContext;

    private List<LeftPanelItem> mLeftPanelItems;
    private Handler mUpdateHandler = null;
    private static int MSG_OPEN_CLOSE_ITEM = 100;

    public LeftPanelAdapter(Context context ,ArrayList<LeftPanelItem> leftPanelItems, Handler updateHandler){
        mContext = context;
        mLeftPanelItems = leftPanelItems;
        mUpdateHandler = updateHandler;
    }
    @Override
    public int getCount() {
        return mLeftPanelItems.size();
    }

    @Override
    public Object getItem(int i) {
        return mLeftPanelItems.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup viewGroup) {
        ViewHolder mViewHolder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.activity_main_left_list_item, null);
            mViewHolder = new ViewHolder();
            mViewHolder.iconImageView = (ImageView) convertView.findViewById(R.id.item_icon);
            mViewHolder.nameTextView = (TextView) convertView.findViewById(R.id.item_name);
            mViewHolder.mNoLoginLayout = (LinearLayout) convertView.findViewById(R.id.user_no_login_layout);
            mViewHolder.arrowImageView = (ImageView) convertView.findViewById(R.id.arrow);
            convertView.setTag(mViewHolder);
        }else {
            mViewHolder = (ViewHolder) convertView.getTag();
        }
        mViewHolder.iconImageView.setImageResource(mLeftPanelItems.get(position).mIcon);
        mViewHolder.nameTextView.setText(mLeftPanelItems.get(position).mName);


        if(mLeftPanelItems.get(position).mIsShow){
            mViewHolder.mNoLoginLayout.setVisibility(View.VISIBLE);
        }else{
            mViewHolder.mNoLoginLayout.setVisibility(View.GONE);
        }
        if(position == 0 || position == 4){
            mViewHolder.arrowImageView.setVisibility(View.VISIBLE);
        }else{
            mViewHolder.arrowImageView.setVisibility(View.GONE);
        }
        convertView.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                mUpdateHandler.sendMessage(mUpdateHandler.obtainMessage(MSG_OPEN_CLOSE_ITEM,position,position));
            }
        });
        return convertView;
    }

    public class ViewHolder {
        public ImageView iconImageView;
        public TextView nameTextView;
        public ImageView arrowImageView;
        public LinearLayout mNoLoginLayout;
    }
    public List<LeftPanelItem> getmLeftPanelItems() {
        return mLeftPanelItems;
    }


}
