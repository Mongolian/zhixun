package com.iory.zhixun.app;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.view.Gravity;
import android.widget.Toast;

public class TLog {

	/** 网络返回的调试开关，服务端控制 */
	private static boolean mForDebugfromNetwork = true;

	/**
	 * 本地写死的调试开关,在发布时关掉
	 */
	public static boolean mHardDebugFlag = true; 

	/**	
	 * 内测开关，在启动时会有登录框档在前面,内测版本打开
	 */
	public static boolean mInnerTestFlag = false;

	/** 是否在软件列表里显示自已本身 */
	public static final boolean SHOWSELF = true;

	public static final String NET_LOG = "net_log_";

	public static final String SERVER_ERROR_LOG = "serv_error_log_";

	private static Callback mCallBack = null;
	private static Handler mUiHandler = null;
	private static final int MSG_PROCOTOL_ERROR = 77;
	private static final int MSG_DEBUG_TOAST = 78;

	private static long mT1;
	private static long mT2;

	private static HashMap<String, ArrayList<String>> mUseTimeStringList = new HashMap<String, ArrayList<String>>();
	private static HashMap<String, ArrayList<Long>> mUseTimeLongList = new HashMap<String, ArrayList<Long>>();

	private static TLog mInstance = null;
	
	public static TLog getInstance() {
		if (mInstance == null) {
			mInstance = new TLog();
		}
		return mInstance;
	}

	public static boolean isForDebug() {
		if (mHardDebugFlag) {
			return mHardDebugFlag;
		} else {
			return mForDebugfromNetwork;
		}
	}

	public static void v(String t, String m) {
		if (isForDebug()) {
			if (m == null) {
				m = "............";
			}
			android.util.Log.v(t, m);
		}
	}

	public static void e(String t, String m) {
		if (isForDebug()) {
			android.util.Log.e(t, m);
		}
	}





	public static void debugToast(String text) {
		if (isForDebug()) {
			if (mUiHandler != null) {
				Message msg = Message.obtain();
				msg.what = MSG_DEBUG_TOAST;
				msg.obj = text;
				mUiHandler.sendMessage(msg);
			}
		}
	}

	public static void createCallback() {
		if (isForDebug()) {
			if (mCallBack == null) {
				mCallBack = new Callback() {

					@Override
					public boolean handleMessage(Message msg) {
						switch (msg.what) {
						case MSG_PROCOTOL_ERROR: {
							String alert = (String) msg.obj;
							Toast t = Toast.makeText(DLApp.getContext(), "debug版本Toast，更多信息到" + DLApp.BASE_ROOT_PATH + "/log/serv_error_log_.txt \n\n" + alert,
									Toast.LENGTH_LONG);
							t.setGravity(Gravity.CENTER, 0, 0);
							t.show();
						}
							break;
						case MSG_DEBUG_TOAST: {
							String alert = (String) msg.obj;
							Toast t = Toast.makeText(DLApp.getContext(), "debug版本Toast，" + alert, Toast.LENGTH_LONG);
							t.setGravity(Gravity.CENTER, 0, 0);
							t.show();
						}
							break;
						default:
							break;
						}
						return true;
					}
				};

				mUiHandler = new Handler(mCallBack);
			}
		}
	}

	public static void destory() {
		mCallBack = null;
		mUiHandler = null;
		mInstance = null;
	}

	public static void setForDebug(boolean forDebug) {
		TLog.mForDebugfromNetwork = forDebug;
	}

	public static void start() {
		mT1 = System.currentTimeMillis();
	}

	public static void endPrint(String tag) {
		mT2 = System.currentTimeMillis();
		TLog.v("time", tag + " " + (mT2 - mT1));
	}

//	class PrintStruct {
//		String s = "";
//		Long time = null;
//	}

	public static void time(String logPoint) {
		time("UseTime", logPoint, false);
	}

	public static void time(String logPoint, boolean print) {
		time("UseTime", logPoint, print);
	}

	public static void time(String tag, String logPoint) {
		time(tag, logPoint, false);
	}

	public static void time(String tag, String logPoint, boolean print) {
		if (!isForDebug()) {
			return;
		}
		// Log.v(tag, logPoint);

		ArrayList<String> sList = mUseTimeStringList.get(tag);
		if (sList == null) {
			sList = new ArrayList<String>();
			mUseTimeStringList.put(tag, sList);
		}
		sList.add(logPoint);

		ArrayList<Long> lList = mUseTimeLongList.get(tag);
		if (lList == null) {
			lList = new ArrayList<Long>();
			mUseTimeLongList.put(tag, lList);
		}
		lList.add(System.currentTimeMillis());

		if (print) {
			StringBuffer sb = new StringBuffer();
			long lastT = lList.get(0);
			sb.append("total time:");
			sb.append(lList.get(lList.size() - 1) - lastT);
			sb.append(" ");
			for (int i = 0; i < sList.size(); i++) {
				sb.append(lList.get(i) - lastT);
				lastT = lList.get(i);
				sb.append(" ");
				sb.append(sList.get(i));
				sb.append(" ");
			}
			TLog.v(tag, sb.toString());

			sList.clear();
			lList.clear();
		}
	}
}
