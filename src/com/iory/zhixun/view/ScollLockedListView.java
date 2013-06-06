package com.iory.zhixun.view;

import android.content.Context;
import android.util.AttributeSet;

public class ScollLockedListView extends OverScrollListView {

	public ScollLockedListView(Context context) {
		super(context);
	}


	public ScollLockedListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}


	public ScollLockedListView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}


	public void setHeaderView(HeaderGallery v) {

	}


	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
	}
}
