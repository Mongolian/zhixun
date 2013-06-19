package com.iory.zhixun.ui;


import android.os.Bundle;
import android.view.Window;

import com.iory.zhixun.R;
import com.iory.zhixun.fragments.ColorFragment;
import com.iory.zhixun.fragments.PersonalListFragment;
import com.iory.zhixun.slidingmenu.lib.SlidingMenu;


public class MainActivity extends BaseActivity {

	public MainActivity() {
		super(R.string.app_name);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getSlidingMenu().setMode(SlidingMenu.LEFT_RIGHT);
		getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
		
		setContentView(R.layout.content_frame);
		getSupportFragmentManager()
		.beginTransaction()
		.replace(R.id.content_frame, new PersonalListFragment())
		.commit();
		
		getSlidingMenu().setSecondaryMenu(R.layout.menu_frame_two);
		getSlidingMenu().setSecondaryShadowDrawable(R.drawable.shadowright);
		getSupportFragmentManager()
		.beginTransaction()
		.replace(R.id.menu_frame_two, new ColorFragment())
		.commit();
	}

}
