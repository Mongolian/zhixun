package com.iory.zhixun.adapter;

import java.util.ArrayList;

import com.iory.zhixun.R;
import com.iory.zhixun.app.DLApp;
import com.iory.zhixun.data.NewsKind;
import com.iory.zhixun.jce.ClientNewsSummary;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class PopupListAdapter extends BaseAdapter {
	private Context context;
	private ArrayList<NewsKind> list = null;
	
	// 列表是否已停止滑动
	public boolean isScrollStateIdle = true;

	public PopupListAdapter(Context context) {
		this.context = context;
		
	}
	
	public void setListData(ArrayList<NewsKind> addlist) {
		if (this.list == null) {
			this.list = new ArrayList<NewsKind>();
		} else {
			this.list.clear();
		}
		DLApp.kindsFilter(this.list, addlist);
	}
	
	public void clearListData() {
		if (list != null) {
			list.clear();
		}
	}

	public void addListData(ArrayList<NewsKind> addlist) {
		if (this.list == null) {
			this.list = new ArrayList<NewsKind>();
		}
		DLApp.kindsFilter(this.list, addlist);
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
			view = LayoutInflater.from(context).inflate(R.layout.popuplist_row, null);
		} else {
			view = convertView;
		}
		
		if (list != null && position < list.size()) {
			NewsKind item = list.get(position);

			view.setTag(item);

			ImageView icon = (ImageView) view
					.findViewById(R.id.icon);

			icon.setImageResource(item.iconId);

			TextView title = (TextView) view.findViewById(R.id.name);
			title.setText(item.kindName);
		}
		return view;
	}

}
