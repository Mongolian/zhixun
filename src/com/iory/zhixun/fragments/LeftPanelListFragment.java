package com.iory.zhixun.fragments;


import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.iory.zhixun.R;
import com.iory.zhixun.adapter.LeftPanelAdapter;
import com.iory.zhixun.data.LeftPanelItem;

import java.util.ArrayList;
import java.util.List;

public class LeftPanelListFragment extends ListFragment {


    private ArrayList<LeftPanelItem> panelItems = null;
    private LeftPanelAdapter mLeftPanelAdapter;


	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.activity_main_left_list, null);
	}

	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

        panelItems = new ArrayList<LeftPanelItem>();
        LeftPanelItem leftPanelItem = null;
        leftPanelItem = new LeftPanelItem();
        leftPanelItem.mName = (String)getText(R.string.user_no_login);
        leftPanelItem.mIcon = R.drawable.left_avater_small_d;
        panelItems.add(leftPanelItem);

        leftPanelItem = new LeftPanelItem();
        leftPanelItem.mName = (String)getText(R.string.left_news);
        leftPanelItem.mIcon = R.drawable.left_panel_news_d;
        panelItems.add(leftPanelItem);

        leftPanelItem = new LeftPanelItem();
        leftPanelItem.mName = (String)getText(R.string.left_person_subscibe);
        leftPanelItem.mIcon = R.drawable.left_panel_subscibe_d;
        panelItems.add(leftPanelItem);

        leftPanelItem = new LeftPanelItem();
        leftPanelItem.mName = (String)getText(R.string.left_fav);
        leftPanelItem.mIcon = R.drawable.left_panel_fav_d;
        panelItems.add(leftPanelItem);

        leftPanelItem = new LeftPanelItem();
        leftPanelItem.mName = (String)getText(R.string.left_setting);
        leftPanelItem.mIcon = R.drawable.left_panel_setting_d;
        panelItems.add(leftPanelItem);
        mLeftPanelAdapter = new LeftPanelAdapter(getActivity(),panelItems);
        setListAdapter(mLeftPanelAdapter);
	}

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        if(mLeftPanelAdapter != null){
            for (int i = 0 ;i<mLeftPanelAdapter.getmLeftPanelItems().size() ; i++){
                mLeftPanelAdapter.getmLeftPanelItems().get(i).mIsShow = false;
            }
        }
        mLeftPanelAdapter.getmLeftPanelItems().get(position).mIsShow = true;
        mLeftPanelAdapter.notifyDataSetChanged();
    }
}
