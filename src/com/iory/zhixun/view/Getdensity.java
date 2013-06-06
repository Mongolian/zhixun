package com.iory.zhixun.view;

import android.content.Context;
import android.widget.LinearLayout;

public class Getdensity {

	
	
	public static int dip2px(Context context,float dipValue){
		
		//获取屏幕分辨率
		//px: pixels(像素)，不同的设备不同的显示屏显示效果是相同的，这是绝对像素，是多少就永远是多少不会改变。
		float scale = context.getResources().getDisplayMetrics().density;
		return (int)(dipValue*scale+0.5f);
	}
}
