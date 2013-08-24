package com.iory.zhixun.ui;


import android.os.Bundle;
import android.widget.Toast;

import com.iory.zhixun.R;
import com.iory.zhixun.fragments.PersonalListFragment;
import com.iory.zhixun.slidingmenu.lib.SlidingMenu;


public class MainActivity extends BaseActivity {

	public MainActivity() {
		super(R.string.app_name);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getSlidingMenu().setMode(SlidingMenu.LEFT_RIGHT);
		getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);

        getSlidingMenu().setOnClosedListener(new SlidingMenu.OnClosedListener() {
            @Override
            public void onClosed() {
                Toast.makeText(MainActivity.this, "close", 0).show();
            }
        });
        getSlidingMenu().setOnOpenListener(new SlidingMenu.OnOpenListener() {
            @Override
            public void onOpen() {
                Toast.makeText(MainActivity.this, "open", 0).show();
            }
        });
        getSlidingMenu().setOnOpenedListener(new SlidingMenu.OnOpenedListener() {
            @Override
            public void onOpened() {
                Toast.makeText(MainActivity.this, "Opened", 0).show();
            }
        });
        getSlidingMenu().setOnClosedListener(new SlidingMenu.OnClosedListener() {
            @Override
            public void onClosed() {
                Toast.makeText(MainActivity.this, "Closed", 0).show();
            }
        });

		setContentView(R.layout.content_frame);
		getSupportFragmentManager()
		.beginTransaction()
		.replace(R.id.content_frame, new PersonalListFragment())
		.commit();
		
		getSlidingMenu().setSecondaryMenu(R.layout.menu_frame_two);
		getSlidingMenu().setSecondaryShadowDrawable(R.drawable.shadowright);
		getSupportFragmentManager()
		.beginTransaction()
		.replace(R.id.menu_frame_two, new PersonalListFragment())
		.commit();
	}

}
