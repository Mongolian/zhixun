package com.iory.zhixun.fragments;


import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.iory.zhixun.R;
import com.iory.zhixun.adapter.LeftPanelAdapter;
import com.iory.zhixun.data.LeftPanelItem;

import java.util.ArrayList;

public class LeftPanelListFragment extends ListFragment {
    private static final  int MSG_OPEN_CLOSE_ITEM = 100;

    private ArrayList<LeftPanelItem> panelItems = null;
    private LeftPanelAdapter mLeftPanelAdapter;
    private Handler updateHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case MSG_OPEN_CLOSE_ITEM:
                    Toast.makeText(getActivity(),"what"+msg.arg1,0).show();
                    if(msg.arg1 == 0 || msg.arg1 == 4){
                        if(mLeftPanelAdapter != null){
                            mLeftPanelAdapter.getmLeftPanelItems().get(msg.arg1).mIsShow = ! mLeftPanelAdapter.getmLeftPanelItems().get(msg.arg1).mIsShow;
                            mLeftPanelAdapter.notifyDataSetChanged();
                        }
                    }else {

                    }
                    break;
            }
        }
    };


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
        mLeftPanelAdapter = new LeftPanelAdapter(getActivity(),panelItems,updateHandler);
        setListAdapter(mLeftPanelAdapter);
	}

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        if(position == 1 || position == 2 || position == 3){
            return;
        }

        if(mLeftPanelAdapter != null){
            for (int i = 0 ;i<mLeftPanelAdapter.getmLeftPanelItems().size() ; i++){
                mLeftPanelAdapter.getmLeftPanelItems().get(i).mIsShow = false;
            }
        }
        mLeftPanelAdapter.getmLeftPanelItems().get(position).mIsShow = true;
        mLeftPanelAdapter.notifyDataSetChanged();
    }
}
