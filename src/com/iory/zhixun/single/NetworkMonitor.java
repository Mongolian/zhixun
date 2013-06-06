package com.iory.zhixun.single;

import com.iory.zhixun.app.DLApp;
import com.iory.zhixun.app.TLog;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;


public class NetworkMonitor {

	private final String TAG = NetworkMonitor.class.getName();

	private static NetworkMonitor mInstance = null;

	public static final int CONNECT_TYPE_WIFI = 0;
	public static final int CONNECT_TYPE_MOBILE_3G = 1;
	public static final int CONNECT_TYPE_MOBILE_2G = 2;
	public static final int CONNECT_TYPE_NO_NETWORK = -1;
	
	private int mNetState = CONNECT_TYPE_NO_NETWORK;

	private NetworkMonitor() {

	}

	protected static NetworkMonitor getInstance() {
		if (mInstance == null) {
			mInstance = new NetworkMonitor();
		}
		return mInstance;
	}

	public void init() {
		getNetworkState();
	}

	public void destory() {
		mInstance = null;
	}

	/**
	 * 启动时获取网络状态
	 */
	private void getNetworkState() {
		ConnectivityManager cm = (ConnectivityManager) DLApp.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetInfo = cm.getActiveNetworkInfo();
		if (activeNetInfo != null) {
			networkConnectTypeChanged(activeNetInfo);
			TLog.v(TAG, activeNetInfo.toString());
		}
	}

	/**
	 * 网络状态切换回调
	 * 
	 * @param ctx
	 * @param intent
	 */
	public void onNetworkChanged(Context ctx, Intent intent) {
		String action = intent.getAction();
		if (ConnectivityManager.CONNECTIVITY_ACTION.equals(action)) {
			NetworkInfo activeNetInfo = (NetworkInfo) intent.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);
			if (activeNetInfo != null && activeNetInfo.getState() == State.CONNECTED) {
				networkConnectTypeChanged(activeNetInfo);
				TLog.v(TAG, "onNetworkChanged:" + activeNetInfo.toString());
			}

			if (activeNetInfo == null) {
				mNetState = CONNECT_TYPE_NO_NETWORK;
			}
		}
	}

	private void networkConnectTypeChanged(NetworkInfo activeNetInfo) {
		if (activeNetInfo != null) {
			ConnectivityManager conMan = (ConnectivityManager) DLApp.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo mobileInfo = conMan.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
			NetworkInfo wifiInfo = conMan.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
			State mobile = null; // 网络状态要判断一下，防止为空
			State wifi = null;
			if (mobileInfo != null) {
				mobile = mobileInfo.getState();
			}
			if (wifiInfo != null) {
				wifi = wifiInfo.getState();
			}
			if (wifi == State.CONNECTED) {
				mNetState = CONNECT_TYPE_WIFI;
			} else if (mobile == State.CONNECTED) {
				String subTypeName = activeNetInfo.getSubtypeName();
				if (subTypeName != null) {
					// 3G
					if (subTypeName.indexOf("HSPDA") > -1 || subTypeName.indexOf("EVDO") > -1 || subTypeName.indexOf("SCDMA") > -1) {
						mNetState = CONNECT_TYPE_MOBILE_3G;
					}
					// 2.5G 2G
					else if (subTypeName.indexOf("EDGE") > -1 || subTypeName.indexOf("GPRS") > -1 || subTypeName.indexOf("CDMA") > -1) {
						mNetState = CONNECT_TYPE_MOBILE_2G;
					} else {
						mNetState = CONNECT_TYPE_MOBILE_3G;
					}
				}
			}

			TLog.v(TAG, "onNetworkChanged:" + activeNetInfo.toString());

			// 传到mainCtrl
/*			if (!MainController.isNULL()) {
				MainController.getInstance().onNetConnectTypeChanged(mNetState, activeNetInfo.getExtraInfo());
			}*/
		} else {
			mNetState = CONNECT_TYPE_NO_NETWORK;
		}
	}

	public int getNetworkStateFlag() {
		return mNetState;
	}

	public String getNetworkType() {
		ConnectivityManager cm = (ConnectivityManager) DLApp.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetInfo = cm.getActiveNetworkInfo();
		if (activeNetInfo != null) {
			String netType = activeNetInfo.getTypeName();
			TLog.v(TAG, netType);
			return netType;
		} else {
			mNetState = CONNECT_TYPE_NO_NETWORK;
		}
		return "";
	}

	public void setNetworkStateFlag(int flag) {
		this.mNetState = flag;
	}
}
