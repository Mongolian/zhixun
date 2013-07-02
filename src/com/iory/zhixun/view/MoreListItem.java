package com.iory.zhixun.view;

import com.iory.zhixun.R;
import com.iory.zhixun.app.TLog;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;


/**
 * 用于列表底部的加载按钮操作事件以及超时计数
 * 
 * @author ioryli
 * @date 2013-4-28
 * @version 1.0
 */
public class MoreListItem {

	/** 还没开始加载 */
	public static final int NOT_LOAD = 0;
	/** 加载中 */
	public static final int LOADING = 1;
	/** 加载失败或超时 */
	public static final int LOAD_FAIL = 2;
	/** 整个列表已加载结束 */
	public static final int END_LOAD = 3;

	public int loadState = NOT_LOAD;

	/** 当前列表的页数 */
	public int pageNo = 1;

	/** 此ID用于判断重试按钮的事件处理 */
	// private int listViewId = 0;

	protected ListView listView = null;

	protected View footerView = null;

	protected TextView tvLoading = null;
	protected ProgressBar pbar = null;
	protected TextView reTryBtn = null;

	private static final int MSG_TIME_OUT = 0;
	private static final int MSG_NO_DATA = 1;

	private static final int TIME_OUT = 30 * 1000; // 超时时间
	public String pagerParam = "";
	/**
	 * 拉取数据
	 */
	private IMoreDateListener moreDataListener;

	/**
	 * 拉取数据事件
	 * 
	 * @author rexzou
	 * 
	 */
	public interface IMoreDateListener {
		/**
		 * 获取下一页数据
		 * 
		 * @param view
		 *            列表
		 * @param pageNo
		 *            ,第几页
		 */
		public void getMoreData(AbsListView view, int pageNo);

		/**
		 * 列表滑动状态改变
		 * 
		 * @param view
		 * @param scrollState
		 * @author dragonlin
		 */
		public void onScrollStateChanged(AbsListView view, int scrollState);
		

	}

	/**
	 * 
	 * @param listView
	 * @param footerView
	 *            必须包括以下几个ID 显示菊花时的文字 (TextView)
	 *            footerView.findViewById(R.id.TextView01); 菊花(ProgressBar)
	 *            footerView.findViewById(R.id.ProgressBar01); 超时的重试按钮 (Button)
	 *            footerView.findViewById(R.id.WaitingBtn);
	 * @param moreDataListener
	 *            拉取数据的监听器
	 */
	public MoreListItem(ListView listView, View footerView, IMoreDateListener moreDataListener) {
		this.moreDataListener = moreDataListener;
		this.footerView = footerView;
		setListView(listView);
		setWaittingFooterView(footerView);
	}

	private void setListView(ListView listView) {
		this.listView = listView;
		this.listView.addFooterView(footerView);
		this.listView.setOnScrollListener(scorllListener);
	}

	/** 超时计数Handler，借用UI线程进行计时 */
	private Handler timeOutHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			if( moreDataListener == null) //如果已结束，不处理消息
			{
				return;
			}
			super.handleMessage(msg);
			switch (msg.what) {
			case MSG_TIME_OUT: {
				TLog.e("moreListItem:checkTimeOut", "timeout:excuteTimeOut");
				if (footerView != null) {
					tvLoading.setVisibility(View.GONE);
					pbar.setVisibility(View.GONE);
					reTryBtn.setVisibility(View.VISIBLE);
					reTryBtn.setOnClickListener(retryListener);
				}
			}
				break;
			case MSG_NO_DATA: {
				if (footerView != null) {
					tvLoading.setVisibility(View.GONE);
					pbar.setVisibility(View.GONE);
					reTryBtn.setVisibility(View.VISIBLE);
					reTryBtn.setText("暂无数据");
					reTryBtn.setEnabled(false);
					footerView.setEnabled(false);
				}
			}
				break;
			
			default:
				break;
			}
		}
	};

	/** 用于列表滑动到最后一项时，自动拉取更多的列表 */
	private OnScrollListener scorllListener = new OnScrollListener() {

		@Override
		public void onScrollStateChanged(AbsListView view, int scrollState) {
			Log.v("TAG", "onScroll," + "scrollState" + scrollState);
			moreDataListener.onScrollStateChanged(view, scrollState);
			if( scrollState == OnScrollListener.SCROLL_STATE_TOUCH_SCROLL){
//				if( QuickActionBar.isShowing() ){
//					QuickActionBar.dismiss();
//				}
			}
		}

		@Override
		public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
			// 自动拉取数据，在当前列表的最后一项露出来，或列表第一次初始化时，会触发
			Log.v("TAG", "onScroll");
			if (firstVisibleItem == totalItemCount - visibleItemCount || (totalItemCount == 1 && visibleItemCount == 0)) {
				if (loadState == NOT_LOAD) {
					loadState = LOADING;

					// 开始超时检测
					startTimeOutChecking();
					view.setSelection(firstVisibleItem);
					moreDataListener.getMoreData(view, pageNo);
				}
				
			}
		}
	};

	/** 加载超时或失败后的重试按钮事件，点击重新发起请求 */
	private View.OnClickListener retryListener = new OnClickListener() {

		@Override
		public void onClick(View v) {

			loadState = LOADING;
			tvLoading.setVisibility(View.VISIBLE);
			pbar.setVisibility(View.VISIBLE);
			reTryBtn.setVisibility(View.GONE);
			// 开始超时检测
			 startTimeOutChecking();
			moreDataListener.getMoreData(listView, pageNo);

		}
	};
public void refresh(){
	loadState = LOADING;
	pageNo = 1;
	tvLoading.setVisibility(View.VISIBLE);
	pbar.setVisibility(View.VISIBLE);
	reTryBtn.setVisibility(View.GONE);
	// 开始超时检测
	 startTimeOutChecking();
	moreDataListener.getMoreData(listView, pageNo);
}
	 /**
	 * 开始加载的超时计数
	 */
	 protected void startTimeOutChecking() {
	 Message msg = Message.obtain();
	 msg.what = MSG_TIME_OUT;
//	 if(!Tools.testNetworkIsAvailable()&&pageNo>1){
//		 	loadState = LOAD_FAIL;
//			Toast.makeText(DLApp.getContext(), TContext.getHttpErrorMsg(HttpTaskListener.ERROR_Throwable), Toast.LENGTH_SHORT).show();
//			timeOutHandler.sendMessage(msg);
//		}else{
			TLog.e("moreListItem:checkTimeOut", "timeout:startChecking");
			timeOutHandler.sendMessageDelayed(msg, TIME_OUT);
//		}
	 }

	// 显示超时或失败
	public void showTimeOutOrFail() {
		timeOutHandler.removeMessages(MSG_TIME_OUT);
		Message msg = Message.obtain();
		msg.what = MSG_TIME_OUT;
		timeOutHandler.sendMessage(msg);
	}

	public void showNoData() {
		timeOutHandler.removeMessages(MSG_TIME_OUT);
		Message msg = Message.obtain();
		msg.what = MSG_NO_DATA;
		timeOutHandler.sendMessage(msg);
	}
	/**
	 * 停止加载的超时计数
	 */
	public void stopTimeOutChecking() {
		timeOutHandler.removeMessages(MSG_TIME_OUT);
	}

	/**
	 * 设置加载中的VIEW
	 * 
	 * @param view
	 */
	protected void setWaittingFooterView(View view) {
		footerView = view;
		tvLoading = (TextView) footerView.findViewById(R.id.TextView01);
		pbar = (ProgressBar) footerView.findViewById(R.id.ProgressBar01);
		reTryBtn = (TextView) footerView.findViewById(R.id.WaitingBtn);
		tvLoading.setVisibility(View.VISIBLE);
		pbar.setVisibility(View.VISIBLE);
		reTryBtn.setVisibility(View.GONE);
	}

	/**
	 * 服务器返回的更多列表为空的时候调用，已到列表的尽头， 此时把状态改为结束，加载中的view去掉（藏起来达不到效果，会留一下项空行在最后）
	 */
	public void hasEndLoadList() {
		loadState = END_LOAD;

		// tvLoading.setVisibility(View.VISIBLE);
		// //test
		// tvLoading.setText("已经到列表最底部");
		// pbar.setVisibility(View.GONE);
		// reTryBtn.setVisibility(View.GONE);
		if (listView.getAdapter() != null) {
			listView.removeFooterView(footerView);
		}

	}

	/**
	 * 取到更多的列表，状态变为未加载，等待下一次的拉取 页数++; 把加载中的view显示出来
	 */
	public void hasLoadMoreList() {
		loadState = NOT_LOAD;
		// if( pageNo == 1)
		// {
		// this.listView.addFooterView(footerView);
		// }
		pageNo++;
		tvLoading.setVisibility(View.VISIBLE);
		pbar.setVisibility(View.VISIBLE);
		reTryBtn.setVisibility(View.GONE);
	}

	public void destroy()
	{
		stopTimeOutChecking();
		if (listView!= null)
		{
			listView.setOnScrollListener(null);
			if(footerView != null )
			{
				listView.removeFooterView(footerView);
			}
		}
		pbar.setBackgroundDrawable(null);
		pbar.clearAnimation();
		pbar.destroyDrawingCache();
		footerView = null;
		listView = null;
		moreDataListener =null;
		tvLoading = null;
		pbar = null;
		reTryBtn = null;
	}
}
