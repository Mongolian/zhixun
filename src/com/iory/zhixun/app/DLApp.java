package com.iory.zhixun.app;

import java.util.ArrayList;

import com.iory.zhixun.data.NewsKind;
import com.iory.zhixun.jce.ClientNewsSummary;
import android.app.Application;
import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.widget.Toast;


/**
 * 提供一些程序的全局接口
 * 
 * @author dragonlin
 * @date 2011-3-10
 * 
 * @version 1.0
 */
public class DLApp extends Application {
	
	public static final String TAG = DLApp.class.getName();

	// public static final String BASE_ROOT_PATH = "/Tencent/QQDownload";
	public static final String BASE_ROOT_PATH = "/zhixun";

//	private String mTipMsg = "";

	private static Context mAppContext = null;

	public static String mHttpError = "";

	public static String mStrFileNotExist = "";


	public static String mPackageName = "";

	private static Handler mMsfErrorhandler = null;

	// add by dragonlin 单独的Handler线程，用于处理一些耗时操作
	private HandlerThread mHandlerThread = null;
	private static Handler mHandler = null;
	// 提供给非UI线程里进行Handler对象构造的时候使用
	public static Looper mLooper = null;

	

	public static Context getContext() {
		return mAppContext;
	}

	public static void setMsfHandler(Handler handler) {
		DLApp.mMsfErrorhandler = handler;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		TLog.time(TAG, "start dlapp");
		
		mAppContext = this.getApplicationContext();
		mPackageName = getPackageName();
		mHandlerThread = new HandlerThread("zhixun-ext");
		mHandlerThread.start();
		mLooper = mHandlerThread.getLooper();
		mHandler = new Handler(mLooper);
		TLog.time(TAG, "end dlapp");
	}

	//耗时操作的代码用Runnable包装一下，post进来单独执行
	public static void postRunnable(Runnable r) {
		mHandler.post(r);
	}
	
	// 过滤自已的software，检查icon地址
	public static void newsFilter(ArrayList<ClientNewsSummary> totalList, final ArrayList<ClientNewsSummary> list) {
			if (totalList == null || list == null) {
				return;
			}
			ArrayList<ClientNewsSummary> alertList = null;
			for (int i = 0; i < list.size(); i++) {
				ClientNewsSummary news = list.get(i);
					totalList.add(news);
				// 图标地址检查
//				if (TLog.isForDebug()) {
//					if (alertList == null) {
//						alertList = new ArrayList<Software>();
//					}
//					if (software.sLogoUrl == null || software.sLogoUrl.length() == 0 || !software.sLogoUrl.startsWith("http://")) {
//						alertList.add(software);
//					}
//				}
			}
		}
	
	// 过滤自已的software，检查icon地址
	public static void kindsFilter(ArrayList<NewsKind> totalList, final ArrayList<NewsKind> list) {
			if (totalList == null || list == null) {
				return;
			}
			ArrayList<NewsKind> alertList = null;
			for (int i = 0; i < list.size(); i++) {
				    NewsKind kind = list.get(i);
					totalList.add(kind);
			}
		}


}
