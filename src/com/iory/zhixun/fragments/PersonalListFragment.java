package com.iory.zhixun.fragments;


import java.util.ArrayList;
import java.util.Vector;
import java.util.zip.Inflater;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ListFragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.AbsListView.OnScrollListener;

import com.iory.zhixun.R;
import com.iory.zhixun.adapter.PersonalListAdapter;
import com.iory.zhixun.app.DLApp;
import com.iory.zhixun.app.TLog;
import com.iory.zhixun.jce.ClientNewsSummary;
import com.iory.zhixun.view.MoreListItem;
import com.iory.zhixun.view.MoreListItem.IMoreDateListener;
import com.iory.zhixun.net.LinkData;

public class PersonalListFragment extends ListFragment {
	
     MoreListItem mMoreListItemPersonalListPacker;
     LayoutInflater mInflater;
     PersonalListAdapter personalListAdapter;

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mInflater = inflater;
		return inflater.inflate(R.layout.personal_list, null);
	}

	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		personalListAdapter = new PersonalListAdapter(getActivity());
//		for (int i = 0; i < 20; i++) {
//			adapter.add(new SampleItem("www.qq.com", "今天又个新闻，舅子被爆菊花了。",android.R.drawable.ic_menu_search));
//		}
		
		// 每日精选的脚部
		View footer = mInflater.inflate(R.layout.list_waiting, null);
		mMoreListItemPersonalListPacker = new MoreListItem(getListView(), footer, moreDateListener);
		// 添加adapter
		setListAdapter(personalListAdapter);
	}
	
	
	private IMoreDateListener moreDateListener = new IMoreDateListener() {
		
		@Override
		public void onScrollStateChanged(AbsListView view, int scrollState) {
			// TODO Auto-generated method stub

			// AsynIconLoader.getInstance().setState(scrollState ==
			// OnScrollListener.SCROLL_STATE_IDLE);
			if (scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
			//MainLogicController.getInstance().requestPindingIcon();
			}
			personalListAdapter.isScrollStateIdle = (scrollState == OnScrollListener.SCROLL_STATE_IDLE);
/*			switch (view.getId()) {
			case R.id.listview_recommend_daily_pick: {
				// 获取每日精选
				personalListAdapter.isScrollStateIdle = (scrollState == OnScrollListener.SCROLL_STATE_IDLE);
			}
				break;
			// case R.id.listview_recommend_require_software: {
			// // 装机必备
			// recommendRequireSoftwareAdapter.isScrollStateIdle = (scrollState
			// == OnScrollListener.SCROLL_STATE_IDLE);
			// }
			// break;
			case R.id.ListView_ontop_by_score: {
				// 获取新品上架(OnTop)评分榜
				onTopScoreListAdapter.isScrollStateIdle = (scrollState == OnScrollListener.SCROLL_STATE_IDLE);
				if (onTopScoreListAdapter.isScrollStateIdle) {
					// onTopScoreListAdapter.notifyDataSetChanged();
				}
			}
				break;
			default:
				break;
			}*/
		}
		
		@Override
		public void getMoreData(AbsListView view, int pageNo) {
			// TODO Auto-generated method stub

			TLog.v("getMoreData", "personalList get more data");
			LinkData.getInstance().sendGetNewsList(getActivity(), "url", 1, 1, loadListNetHandler);
//			MainLogicController ctrl = MainLogicController.getInstance();
//			if (pageNo != 1) {
//				ctrl.sendDayRecommend(true, loadListNetHandler, ctrl.pcGetSelfUin(), pageNo);
//				TLog.v("getdata", "sendDayRecommend");
//			} else {
//				ctrl.homeGetEveryDataSoft(loadListNetHandler);
//				TLog.v("getdata", "homeGetEveryDataSoft");
//			}
//			
		/*	switch (view.getId()) {
			// 获取每日精选
			case R.id.listview_recommend_daily_pick: {
				TLog.v("getMoreData", "sendDayRecommend");
				MainLogicController ctrl = MainLogicController.getInstance();
				if (pageNo != 1) {
					ctrl.sendDayRecommend(true, loadListNetHandler, ctrl.pcGetSelfUin(), pageNo);
					TLog.v("getdata", "sendDayRecommend");
				} else {
					ctrl.homeGetEveryDataSoft(loadListNetHandler);
					TLog.v("getdata", "homeGetEveryDataSoft");
				}
			}
				break;
			// // 装机必备
			// case R.id.listview_recommend_require_software: {
			// MainLogicController ctrl = MainLogicController.getInstance();
			// ctrl.sendRequiredSoftwares(true, loadListNetHandler);
			// Log.v("getdata", "sendRequiredSoftwares");
			// }
				// break;
			// 获取排列
			case R.id.ListView_ontop_by_score: {
				MainLogicController ctrl = MainLogicController.getInstance();
				ctrl.sendGetSoftwaresOnTopByScore(loadListNetHandler, ctrl.pcGetSelfUin(), pageNo, 10);
				TLog.v("getdata", "sendGetSoftwaresOnTopByScore");
				TLog.v(TAG, "OnTop score");
			}
				break;
			default:
				break;
			}*/
		
		}
	};
	
	
	
	/** 处理网络层返回的数据 */
	protected Handler loadListNetHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
//			if (isFinishing()) // 如果已结束，不处理消息
//			{
//				return;
//			}
			super.handleMessage(msg);
			switch (msg.what) {
//			case MainLogicController.MSG_getSoftwaresLatest: {
//				TLog.v(TAG, "MSG_getSoftwaresLatest");
//
//			}
//				break;
			case LinkData.GETNEWSLIST_SUCCESS: {
				TLog.v("loadListNetHandler handleMessage", "MSG_guessIt");
				

				ArrayList<ClientNewsSummary> newsList =  (ArrayList<ClientNewsSummary>) msg.obj;

				if (newsList == null || newsList.size() == 0) {
					mMoreListItemPersonalListPacker.hasEndLoadList();
				} else {
					mMoreListItemPersonalListPacker.hasLoadMoreList();
					boolean flag = personalListAdapter.getCount() == 0;
					personalListAdapter.addListData(newsList);
					personalListAdapter.notifyDataSetChanged();
				}
			}
				break;
			default:
				break;
			}
		}
	};

	



}
