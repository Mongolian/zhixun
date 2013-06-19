package com.iory.zhixun.fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.iory.zhixun.R;

public class PersonalListFragment extends ListFragment {

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.personal_list, null);
	}

	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		SampleAdapter adapter = new SampleAdapter(getActivity());
		for (int i = 0; i < 20; i++) {
			adapter.add(new SampleItem("www.qq.com", "今天又个新闻，舅子被爆菊花了。",android.R.drawable.ic_menu_search));
		}
		setListAdapter(adapter);
	}

	private class SampleItem {
		public String source;
		public String title;
		public int favIconRes;
		public SampleItem(String source, String title,int iconRes) {
			this.source = source; 
			this.title = title; 
			this.favIconRes = iconRes;
		}
	}

	public class SampleAdapter extends ArrayAdapter<SampleItem> {

		public SampleAdapter(Context context) {
			super(context, 0);
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = LayoutInflater.from(getContext()).inflate(R.layout.personal_row, null);
			}
			ImageView icon = (ImageView) convertView.findViewById(R.id.fav_icon);
			
//			icon.setImageResource(getItem(position).favIconRes);
			TextView source = (TextView) convertView.findViewById(R.id.source);
			source.setText(getItem(position).source);
			
			TextView title = (TextView) convertView.findViewById(R.id.title);
			title.setText(getItem(position).title);

			return convertView;
		}

	}
}
