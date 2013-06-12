package com.iory.zhixun.ui;


import java.util.ArrayList;
import java.util.zip.Inflater;

import com.iory.zhixun.R;
import com.iory.zhixun.data.NewsItem;
import com.iory.zhixun.data.NewsListAdapter;
import com.iory.zhixun.view.HorizonScrollLayout;
import com.iory.zhixun.view.LeftPanel;
import com.iory.zhixun.view.Left_add_right;
import com.iory.zhixun.view.Panel;
import com.iory.zhixun.view.Panel.PanelClosedEvent;
import com.iory.zhixun.view.Panel.PanelOpenedEvent;
import com.iory.zhixun.view.RightPanel;
import com.iory.zhixun.view.ScollLockedListView;

import android.os.Bundle;
import android.app.Activity;
import android.app.Service;
import android.graphics.Color;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MainActivity extends Activity {

	private HorizonScrollLayout mHorizonScrollLayout = null;
	private NewsListAdapter mNewsListAdapter = null;
	private ScollLockedListView mNewsList = null;
	
	/** 上次选中的TAB项 */
	private int mLastTabIndex = -1;
	
	private LinearLayout topbarAndList;
	
	public RelativeLayout container;
	public Panel panel = null;
	
    public LeftPanel leftPanel;  
    public RightPanel rightPanel;  
    
    private ImageView leftBtn = null;
    private ImageView rightBtn = null;
    
	Left_add_right layout;
	LinearLayout leftlayout;
	
	LinearLayout top;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.main2);
		 initUI();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}
	public boolean isLeftExtended(){
		return layout.isLeftExtends;
	}
	public boolean isRightExtended(){
		return layout.isRightExtended();
	}
	public void showLeft(){
//		btn.setText("收起");
		layout.showLeft();
	}
	public void showCenter(){
//		btn.setText("展开");
		layout.showCenter();
	}
	public void showRight(){
//		btn.setText("收起");
		layout.showRight();
	}
	
	private void initUI(){
		
		container = (RelativeLayout) findViewById(R.id.main);
//		topbarAndList = (LinearLayout) findViewById(R.id.topbar_and_list);
//		
//		leftBtn = (ImageView) findViewById(R.id.topbar_leftbtn);
//		rightBtn = (ImageView) findViewById(R.id.topbar_rightbtn);
		
	/*	mHorizonScrollLayout = (HorizonScrollLayout) findViewById(R.id.tab_content_viewflipper);
		mHorizonScrollLayout.setOnTouchScrollListener(new HorizonScrollLayout.OnTouchScrollListener() {
			@Override
			public void onScreenChange(int displayScreem) {
				//setSelectTabIndex(displayScreem);
			}

			@Override
			public void onScroll(View view, float leftX, float screemWidth) {
//				TLog.v(TAG, "computeScroll, onTouchScrolling" + ", leftX=" + leftX);
				if (QuickActionBar.isShowing()) {
					QuickActionBar.dismiss();
				}
			}

			@Override
			public void onScrollStateChanged(int scrollState, int currentScreem) {
//				TLog.v(TAG, "computeScroll, onScrollStateChanged, scrollState=" + scrollState);
			}
		});*/
		
		
		
		
		LayoutInflater inflater = LayoutInflater.from(this);
		topbarAndList = (LinearLayout) inflater.inflate(R.layout.centerview, null);
		
		leftBtn = (ImageView) topbarAndList.findViewById(R.id.topbar_leftbtn);
		rightBtn = (ImageView) topbarAndList.findViewById(R.id.topbar_rightbtn);
		
		mNewsList = (ScollLockedListView) topbarAndList.findViewById(R.id.listview_recommend_daily_pick);
		createNewsListAdapter();
		
		layout = new Left_add_right(this);
		layout.setLeftWidth(240);
	    container.addView(layout, RelativeLayout.LayoutParams.FILL_PARENT, RelativeLayout.LayoutParams.FILL_PARENT);
		
		layout.addViewCenter(topbarAndList);
		
		TextView content0=new TextView(this);  
        content0.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT));  
        content0.setText("左边的panel");  
        content0.setGravity(Gravity.CENTER);  
        content0.setTextColor(Color.RED);  
        content0.setBackgroundColor(Color.WHITE);  
          
        //新建测试组件  
        TextView content1=new TextView(this);  
        content1.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT));  
        content1.setText("右边的panel");  
        content1.setGravity(Gravity.CENTER);  
        content1.setTextColor(Color.RED);  
        content1.setBackgroundColor(Color.WHITE);  
          
        LinearLayout  left= new LinearLayout(this);  
        left.addView(content0,LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT);  
        
        LinearLayout  right= new LinearLayout(this);  
        right.addView(content1,LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT);  
        
        layout.addViewleft(left);
        layout.addViewRight(right);
        
        
        leftBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (isLeftExtended())
				{
					showCenter();
				}else {
					showLeft();
				}
			}
		});
        rightBtn.setOnClickListener(new View.OnClickListener() {
			
 			@Override
 			public void onClick(View v) {
 				// TODO Auto-generated method stub
 				if (isRightExtended())
				{
					showCenter();
				}else{
					showRight();
				}
 			}
 		});
 		
		
		
		

	}
	
	PanelClosedEvent panelClosedEvent =new PanelClosedEvent(){

		@Override
		public void onPanelClosed(View panel) {
			Log.e("panelClosedEvent","panelClosedEvent");
		}
		
	};
	
	PanelOpenedEvent panelOpenedEvent =new PanelOpenedEvent(){

		@Override
		public void onPanelOpened(View panel) {
			Log.e("panelOpenedEvent","panelOpenedEvent");
		}
		
	};
	
	/**
	 * 创建NewsList适配器
	 */
	private void createNewsListAdapter() {
		if (mNewsListAdapter == null) {
			// 首页二个广告位
			mNewsListAdapter = new NewsListAdapter(this);
			NewsItem item = new NewsItem();
			item.id = 1;
			item.detail = "aaaa";
			item.outline = "jjaja";
			item.url = "jjaja";
			
			ArrayList<NewsItem> arrayList = new ArrayList<NewsItem>();
			arrayList.add(item);
			mNewsListAdapter.setListData(arrayList);
			
			mNewsList.setAdapter(mNewsListAdapter);
			mNewsListAdapter.notifyDataSetChanged();
		}
	}

}
