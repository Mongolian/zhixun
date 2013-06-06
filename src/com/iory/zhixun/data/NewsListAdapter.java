package com.iory.zhixun.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.iory.zhixun.R;
import com.iory.zhixun.app.TLog;
import com.iory.zhixun.single.IconManager;
import com.iory.zhixun.single.MainController;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

public class NewsListAdapter extends BaseAdapter {

	private Activity mContext = null;
	private ArrayList<NewsItem> mList = null;

	// 列表是否已停止滑动
	public boolean mIsScrollStateIdle = true;
	
	private Map<String, TextView> mPackageName2TextView = null;

	ImageView mIv_icon = null;
	TextView mTvName = null;
	TextView mTvDownloadCount = null;
	TextView mTvFree = null;
	RatingBar mRbStar = null;
//	TextView tv_category = null;
	TextView mTvSize = null;
	
	//独家首发
	ImageView mFirstRelease = null;
	
	//统计
	
	/** 是否为二级分类的软件列表，这种列表需要隐藏分类字段的显示  */
	private boolean mIsCategoryTwoAdapter = false;
	
	/** 是否为二级分类的软件列表的最新列表，这种列表需要把下载量改为时间显示  */
	private boolean mIsCategoryTwoLastestAdapter = false;


	private Handler mIconHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (mContext == null || mContext.isFinishing()) {
				return;
			}
		}
	};

	/** 点击列表项 */
	private View.OnClickListener mItemListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			TLog.time("soft onClick");
			Object obj = v.getTag();
			if (obj != null && obj instanceof NewsItem) {
				NewsItem item = (NewsItem) obj;
				// 打开软件详情界面
				
			}
		}
	};


	public NewsListAdapter(Activity context) {
		this.mContext = context;
	}

	public void clear() {
		if (mList != null) {
			mList.clear();
		}
		if (mPackageName2TextView != null) {
			mPackageName2TextView.clear();
		}
		mIv_icon = null;
		mTvName = null;
		mTvDownloadCount = null;
		mTvFree = null;
		mRbStar = null;
		mTvSize = null;
		mFirstRelease = null;
	}

	public void setListData(ArrayList<NewsItem> addlist) {
		if (this.mList == null) {
			this.mList = new ArrayList<NewsItem>();
		} else {
			this.mList.clear();
		}
		newsItemFilter(this.mList, addlist);
	}
	
	private void newsItemFilter(ArrayList<NewsItem> totalList, final ArrayList<NewsItem> list){
		if (totalList == null || list == null) {
			return;
		}
		for (int i = 0; i < list.size(); i++) {
			NewsItem newsItem = list.get(i);
			if (true) {
				totalList.add(newsItem);
			} else {
				if (true) {
					totalList.add(newsItem);
				}
			}
		}
	}

	public void clearListData() {
		if (mList != null) {
			mList.clear();
		}
	}

	public void addListData(ArrayList<NewsItem> addlist) {
		if (this.mList == null) {
			this.mList = new ArrayList<NewsItem>();
		}
		newsItemFilter(this.mList, addlist);
	}
	
	
	public boolean hasData() {
		return mList != null && mList.size() > 0;
	}

	@Override
	public int getCount() {
		if (mList == null) {
			return 0;
		} else {
			return mList.size();
		}
	}

	@Override
	public Object getItem(int position) {
		if (mList == null) {
			return null;
		} else {
			return mList.get(position);
		}
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		long t1 = System.currentTimeMillis();
		View view = null;
		if (convertView == null) {
			view = LayoutInflater.from(mContext).inflate(R.layout.general_list_item, null);
		} else {
			view = convertView;
		}

		if (mList != null && position < mList.size()) {
			NewsItem item = mList.get(position);

			view.setTag(item);
			
			//收藏夹详情的点击和长按事件自定义，这里不设了
				view.setOnClickListener(mItemListener);

			mIv_icon = (ImageView) view.findViewById(R.id.software_icon);
			
//			if(mIv_icon!=null){
//				((QuickActionBar)mIv_icon).setShowDownloadAnimation(!mIsCategoryTwoAdapter);
//			}
			

			// 把ListItem窗体传给QuickAction
		//	QuickActionBar.bindListItem(mIv_icon, view, parent, position);
			

			mTvName = (TextView) view.findViewById(R.id.software_item_name);
			mTvName.setSelected(true);
			mTvDownloadCount = (TextView) view.findViewById(R.id.share_way);
			mTvFree = (TextView) view.findViewById(R.id.software_fees);
			//mRbStar = (RatingBar) view.findViewById(R.id.RatingBar01);
			mTvSize = (TextView) view.findViewById(R.id.software_size);

			//名称
			mTvName.setText(item.title);
//			tv_name.setMaxWidth(1000);
			//星星
		//	mRbStar.setRating(item.nScore / 2f);
			
			mTvSize.setText(item.outline);
			
			// 获取图标
/*			MainController.getInstance().iconAddRoundMap(item.logourl);
			Bitmap bm = MainController.getInstance().getIcon(item.logourl, mIv_icon, item.id, mIconHandler, mIsScrollStateIdle,IconManager.ICON_TYPE_SOFTWARE_ICON);
			if (bm != null) {
				mIv_icon.setImageBitmap(bm);
			} else {
				mIv_icon.setImageResource(R.drawable.sw_default_icon);
			}*/
			
			
			//DownloadStatusHandler.bindPackageName2StatusView(item, mTvFree, mPackageName2TextView);
		}
		long t2 = System.currentTimeMillis();
		Log.v("test", "getview time:"+(t2-t1));
		return view;
	}

	public void destroy() {
		clear();
		mContext = null;
	}

}
