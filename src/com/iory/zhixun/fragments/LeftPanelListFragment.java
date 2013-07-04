package com.iory.zhixun.fragments;


import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.iory.zhixun.R;
import com.iory.zhixun.adapter.LeftPanelAdapter;
import com.iory.zhixun.data.LeftPanelItem;

import java.util.ArrayList;
import java.util.List;

public class LeftPanelListFragment extends ListFragment {


    private List<LeftPanelItem> panelItems = null;


	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.activity_main_left_list, null);
	}

	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		LeftPanelAdapter adapter = new LeftPanelAdapter(getActivity());
        panelItems = new ArrayList<LeftPanelItem>();
		for (int i = 0; i < 5; i++) {
            panelItems
		}
		setListAdapter(adapter);
	}
}
