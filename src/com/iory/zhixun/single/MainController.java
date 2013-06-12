package com.iory.zhixun.single;

import android.graphics.Bitmap;
import android.os.Handler;
import android.widget.ImageView;

import com.iory.zhixun.R;
import com.iory.zhixun.app.DLApp;
import com.iory.zhixun.app.TLog;


public class MainController {
	private static final String TAG = MainController.class.getName();

	private static MainController instance = null;

	public static boolean isRunning = false;
	
	public static final int MSG_getIconBitmap = 1012;
	public static MainController getInstance() {

		if (instance == null) {
//			TLog.v("Service", "MainLogicController getInstance new");
			instance = new MainController();
		}
		return instance;
	}
	
	/**
	 * 开启线程，载入数据库的数据，本地安装软件等，耗时小的放前面
	 */
	public void init() {
//		TLog.v(TAG, "init:" + isRunning);
		if (!isRunning) {
			isRunning = true;
		}
	}
	
	public static boolean isNULL() {
//		TLog.v("MainLogicController", "instance=null?" + (instance == null));
		if (instance == null) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 主要用来停止线程，释放资源，然后结束进程
	 */
	public void destory() {
		TLog.v(TAG, "destory()");

		isRunning = false;

		instance = null;

	}
	
	
	// 为该图标加圆角处理
		public void iconAddRoundMap(String url) {
			int roundSize = DLApp.getContext().getResources().getDimensionPixelSize(R.dimen.icon_round_size);
			IconManager.getInstance().addRoundCornerIconMap(url, roundSize);
		}

		public Bitmap getIcon(String url, Handler iconHandler) {
			return IconManager.getInstance().getIcon(url, null, 0, iconHandler, true);
		}

		public Bitmap getIcon(String url, Handler iconHandler, boolean isListScorllStateIdle) {
			return IconManager.getInstance().getIcon(url, null, 0, iconHandler, isListScorllStateIdle);
		}

		public Bitmap getIcon(String url, int productid, Handler iconHandler, boolean isListScorllStateIdle) {
			return IconManager.getInstance().getIcon(url, null, productid, iconHandler, isListScorllStateIdle);
		}

		public Bitmap getIcon(String url, ImageView iv, int productid, Handler iconHandler, boolean isListScorllStateIdle, int iconType) {
			/*if (!(DataManager.getInstance().getSaveNetTrafficFlag() == SettingActivity.SETTING_SAVE_NET_TRAFFIC_NORMAL)) {
				// 软件图标在省流量模式下清空
				if (iconType == IconManager.ICON_TYPE_SOFTWARE_ICON) {
					iv.setBackgroundResource(R.drawable.trans);
				}
				return getSaveNetTrafficIcon(iconType);
			}*/
			// 软件图标在非省流量模式下加背景
			//if (iconType == IconManager.ICON_TYPE_SOFTWARE_ICON) {
				iv.setBackgroundResource(R.drawable.software_icon_bg);
			//}
			return IconManager.getInstance().getIcon(url, iv, productid, iconHandler, isListScorllStateIdle);
		}

		public Bitmap getIcon(String url, ImageView iv, int productid, Handler iconHandler, boolean isListScorllStateIdle) {
			return IconManager.getInstance().getIcon(url, iv, productid, iconHandler, isListScorllStateIdle);
		}


		/**
		 * 内存低时,在这儿释放一些资源
		 */
		public void onLowMemory() {
			IconManager.getInstance().clear();
		}
		
		
		public int sendDownloadIcon(String url, Handler h) {
			return -1;//FileHttpEngine.getInstance(this).sendDownloadIcon(url, h);
		}

		public int sendDownloadIcon(String url) {
			return -1;//FileHttpEngine.getInstance(this).sendDownloadIcon(url, null);
		}
	
}
