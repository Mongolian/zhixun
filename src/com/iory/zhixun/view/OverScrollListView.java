package com.iory.zhixun.view;


import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ListView;

/**
 * 
 * @author deliangzhou
 *
 */
public class OverScrollListView extends ListView {
	private OverScroller mScroller;
	public static final String TAG = OverScrollListView.class.getName();


	public OverScrollListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mScroller = new OverScroller(this);
	}


	public OverScrollListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mScroller = new OverScroller(this);
	}


	public OverScrollListView(Context context) {
		super(context);
		mScroller = new OverScroller(this);
	}


	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		mScroller.dispatchTouchEvent(ev);
		return super.dispatchTouchEvent(ev);
	}


	@Override
	public void draw(Canvas canvas) {
		super.draw(canvas);
		mScroller.draw(canvas);
//		TLog.v(TAG, TAG+"draw");
	}

}
