package com.iory.zhixun.adapter;

import java.util.ArrayList;

import zhi_xun.ClientNewsSummary;

import com.iory.zhixun.R;
import com.iory.zhixun.app.DLApp;
import com.iory.zhixun.view.ScollLockedListView;

import android.R.integer;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class PersonalListAdapter extends BaseAdapter {
	private Context context;
	private ArrayList<ClientNewsSummary> list = null;
	ScollLockedListView listView = null;
	private int kindId;
	// 列表是否已停止滑动
	public boolean isScrollStateIdle = true;

	public PersonalListAdapter(Context context ,ScollLockedListView listView,int kindId) {
		this.context = context;
	    this.listView = listView;
		this.kindId = kindId;
	}
	
	public void setListData(ArrayList<ClientNewsSummary> addlist) {
		if (this.list == null) {
			this.list = new ArrayList<ClientNewsSummary>();
		} else {
			this.list.clear();
		}
		DLApp.newsFilter(this.list, addlist);
	}
	
	public void clearListData() {
		if (list != null) {
			list.clear();
		}
	}

	public void addListData(ArrayList<ClientNewsSummary> addlist) {
		if (this.list == null) {
			this.list = new ArrayList<ClientNewsSummary>();
		}
		DLApp.newsFilter(this.list, addlist);
	}
	
	
	public boolean hasData() {
		return list != null && list.size() > 0;
	}
	
	@Override
	public int getCount() {
		if (list == null) {
			return 0;
		} else {
			return list.size();
		}
	}

	@Override
	public Object getItem(int position) {
		if (list == null) {
			return null;
		} else {
			return list.get(position);
		}
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		
		View view = null;
		if (convertView == null) {
			view = LayoutInflater.from(context).inflate(R.layout.personal_row, null);
		} else {
			view = convertView;
		}
		
		if (list != null && position < list.size()) {
			ClientNewsSummary item = list.get(position);

			if(item ==null){
				return null;
			}
			view.setTag(item);

			ImageView icon = (ImageView) view
					.findViewById(R.id.fav_icon);

			// icon.setImageResource(getItem(position).favIconRes);
			TextView source = (TextView) view.findViewById(R.id.source);
			source.setText(item.link);

			TextView title = (TextView) view.findViewById(R.id.title);
			title.setText(item.title);
			
			RelativeLayout actionBar1 = (RelativeLayout) view.findViewById(R.id.actionbar1);
			
			switch(item.getItemStatus()){
			case 0:
                 actionBar1.setVisibility(View.GONE);
				break;
			case 1:
				  actionBar1.setVisibility(View.VISIBLE);
				break;
			case 2:
				break;
			
			
			}
		
		}
		return view;
	}

}
