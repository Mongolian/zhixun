package com.iory.zhixun.fragments;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import java.util.zip.Inflater;

import android.R.integer;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.YuvImage;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.AbsListView.OnScrollListener;

import com.iory.zhixun.R;
import com.iory.zhixun.adapter.PersonalListAdapter;
import com.iory.zhixun.adapter.PopupListAdapter;
import com.iory.zhixun.app.DLApp;
import com.iory.zhixun.app.TLog;
import com.iory.zhixun.data.NewsKind;
import com.iory.zhixun.jce.ClientNewsSummary;
import com.iory.zhixun.utils.Tools;
import com.iory.zhixun.utils.Utils;
import com.iory.zhixun.view.MoreListItem;
import com.iory.zhixun.view.MoreListItem.IMoreDateListener;
import com.iory.zhixun.net.LinkData;

public class PersonalListFragment extends Fragment {
	
	 com.iory.zhixun.view.ScollLockedListView mListView;
     MoreListItem mMoreListItemPersonalListPacker;
     LayoutInflater mInflater;
     PersonalListAdapter personalListAdapter;
     ImageView centerBtn;
     
     ArrayList<NewsKind> kindsList ;
     
 	private PopupWindow popupWindow;
 	private LinearLayout popupLayout;  // popupwindow 中的 布局
 	private ListView popupListView; 
 	private PopupListAdapter popupListAdapter;
 	
 	
 	private Map<Integer, PersonalListAdapter> adapterMap = new HashMap<Integer, PersonalListAdapter>();
 	private int currentKindId = -1;
 			
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mInflater = inflater;
		return inflater.inflate(R.layout.personal_list, null);
	}
	
	public void onDestroy() {
		personalListAdapter.clearListData();
		kindsList.clear();
		super.onDestroy();
	}
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		
		
	
		centerBtn = (ImageView)getView().findViewById(R.id.topbar_centerbtn);
		centerBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
			         
				    int x = -Tools.getPixFromDip(40, getActivity());
				    int y = 20;
					popupWindow.showAsDropDown(v,x,y);
			}
		});
		
		initList();
		initPopupWindow();
	}
	
	public void showPopupWindow(int x, int y) {
		
		

	
		// showAsDropDown会把里面的view作为参照物，所以要那满屏幕parent
		// popupWindow.showAsDropDown(findViewById(R.id.tv_title), x, 10);
		popupWindow.showAtLocation(centerBtn, Gravity.CENTER
				| Gravity.TOP, x, y);//需要指定Gravity，默认情况是center.


	}
	
	
	void changeKind(int position){
		
		        NewsKind kind = kindsList.get(position);
				currentKindId = kind.id;
				PersonalListAdapter adapter = adapterMap.get(currentKindId);
				if(adapter==null){
					adapter = new PersonalListAdapter(getActivity(),kind.id);
					adapterMap.put(currentKindId, adapter);
				}
				mListView.setAdapter(adapter);
				
		
	}


    private void initPopupWindow(){
    	
		kindsList = new ArrayList<NewsKind>();
		
		NewsKind kind1 = new NewsKind(1,"个人订阅",R.drawable.topbar_popup_yule_n);
		NewsKind kind2 = new NewsKind(2, "头条",R.drawable.topbar_popup_toutiao_n);
		NewsKind kind3 = new NewsKind(3, "娱乐",R.drawable.topbar_popup_yule_n);
		NewsKind kind4 = new NewsKind(4, "科技",R.drawable.topbar_popup_keji_n);
		NewsKind kind5 = new NewsKind(5, "财经",R.drawable.topbar_popup_caijing_n);
		NewsKind kind6 = new NewsKind(6, "体育",R.drawable.topbar_popup_tiyu_n);
		kindsList.add(kind1);
		kindsList.add(kind2);
		kindsList.add(kind3);
		kindsList.add(kind4);
		kindsList.add(kind5);
		kindsList.add(kind6);
    	
    	popupLayout = (LinearLayout)mInflater.inflate(
				R.layout.popup, null);
    	popupListView = (ListView) popupLayout.findViewById(R.id.lv_dialog);
    	
    	
    	popupListAdapter = new PopupListAdapter(getActivity());
    	popupListAdapter.setListData(kindsList);
    	
    	popupListView.setAdapter(popupListAdapter);

		
		popupListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> paramAdapterView,
					View paramView, int position, long id) {
				// TODO Auto-generated method stub
			    if(id<-1) {
			    		        // 点击的是headerView或者footerView
			    		        return;
			    		 }
			    int realPosition=(int)id;
				changeKind(realPosition);
				popupWindow.dismiss();
			}
			
		});
		popupWindow = new PopupWindow(getActivity());
		popupWindow.setBackgroundDrawable(new BitmapDrawable());
		popupWindow	.setWidth(Tools.getPixFromDip(200, getActivity()));
		popupWindow.setHeight(Tools.getPixFromDip(400, getActivity()));
		popupWindow.setOutsideTouchable(true);
		popupWindow.setFocusable(true);
		popupWindow.setContentView(popupLayout);
    	
    }
	
	private void initList(){
		
	personalListAdapter = new PersonalListAdapter(getActivity(),2);
		
		
		
		mListView = (com.iory.zhixun.view.ScollLockedListView) (this.getView().findViewById(R.id.personal_list));
		// 每日精选的脚部
		View footer = mInflater.inflate(R.layout.list_waiting, null);
		mMoreListItemPersonalListPacker = new MoreListItem(mListView, footer, moreDateListener);
		// 添加adapter
		mListView.setAdapter(personalListAdapter);
		currentKindId = 1;
		adapterMap.put(1, personalListAdapter);
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
			switch (view.getId()) {
			case R.id.personal_list :{
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
/*			case R.id.ListView_ontop_by_score: {
				// 获取新品上架(OnTop)评分榜
				onTopScoreListAdapter.isScrollStateIdle = (scrollState == OnScrollListener.SCROLL_STATE_IDLE);
				if (onTopScoreListAdapter.isScrollStateIdle) {
					// onTopScoreListAdapter.notifyDataSetChanged();
				}
			}
				break;*/
			default:
				break;
			}
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
					
					PersonalListAdapter adapter = adapterMap.get(currentKindId);
					if(adapter != null){
						boolean flag = adapter.getCount() == 0;
						
						if(currentKindId==1){
						    newsList = new ArrayList<ClientNewsSummary>();
							
							for (int i=0;i<5;i++){
								ClientNewsSummary newsSummary = new ClientNewsSummary(1, "分类ID是1的模拟数据", "2013-07-02",null, "分类ID是1的模拟数据", "11111", "www.qq.com", false, 5, null);
								newsList.add(newsSummary);
							}
						}	else if(currentKindId==2){
						    newsList = new ArrayList<ClientNewsSummary>();
							
							for (int i=0;i<5;i++){
								ClientNewsSummary newsSummary = new ClientNewsSummary(1, "分类ID是2的模拟数据", "2013-07-02",null, "分类ID是2的模拟数据", "22222", "www.baidu.com", false, 5, null);
								newsList.add(newsSummary);
							}
						}else if(currentKindId==3){
						    newsList = new ArrayList<ClientNewsSummary>();
							
							for (int i=0;i<5;i++){
								ClientNewsSummary newsSummary = new ClientNewsSummary(1, "分类ID是3的模拟数据", "2013-07-02",null, "分类ID是3的模拟数据", "33333", "www.360.com", false, 5, null);
								newsList.add(newsSummary);
							}
						}
						
						adapter.addListData(newsList);
						adapter.notifyDataSetChanged();
					}
					
			
				}
			}
				break;
			default:
				break;
			}
		}
	};

	



}
