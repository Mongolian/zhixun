package com.iory.zhixun.net;

import java.util.ArrayList;

import com.iory.zhixun.app.DLApp;

import android.content.SharedPreferences;





/**
 *
 * @author dragonlin
 * @date 2011-3-10	
 *
 * @version 1.0
 */
public class HttpThreadPoolController {
	
	private static int taskSeed = 0;
	
	public static final int POOL_TYPE_NORMAL = 0;
	public static final int POOL_TYPE_DOWNLOAD_ICON = 1;
	public static final int POOL_TYPE_DOWNLOAD_APK = 2;
	
	private HttpThreadPool normalThreadPool = null;
	private HttpThreadPool getIconThreadPool = null;
	private HttpThreadPool downloadAppThreadPool = null;
	
	private int normalThreadPoolNum = 2;//普通的协议线程数
	private int getIconThreadPoolNum = 6;//下载应用的icon线程数
	private int downloadAppThreadPoolNum = 2;//最大下载任务数
	
	private static HttpThreadPoolController instance = null;
	
	private HttpThreadPoolController() {
		//取设置里的最大下载任务数
		SharedPreferences sp = DLApp.getContext().getSharedPreferences("zhixun_config", 0);
		String maxDownloadThread = sp.getString("max_download_thread_preference", "2");
		try {
			downloadAppThreadPoolNum = Integer.parseInt(maxDownloadThread);
		}catch (Exception e) {
			e.printStackTrace();
			downloadAppThreadPoolNum = 2;
		}
		
		normalThreadPool = new HttpThreadPool(normalThreadPoolNum, POOL_TYPE_NORMAL);
		getIconThreadPool = new HttpThreadPool(getIconThreadPoolNum, POOL_TYPE_DOWNLOAD_ICON);
		downloadAppThreadPool = new HttpThreadPool(downloadAppThreadPoolNum, POOL_TYPE_DOWNLOAD_APK);
		
		normalThreadPool.start();
		getIconThreadPool.start();
		downloadAppThreadPool.start();
	}
	
	public static HttpThreadPoolController getInstance() {
		if(instance == null) {
			instance = new HttpThreadPoolController();
		}
		return instance;
	}
	
	public void destory() {
		if (normalThreadPool != null) {
			normalThreadPool.stop();
			normalThreadPool = null;
		}
		if (getIconThreadPool != null) {
			getIconThreadPool.stop();
			getIconThreadPool = null;
		}
		if (downloadAppThreadPool != null) {
			downloadAppThreadPool.stop();
			downloadAppThreadPool = null;
		}
		instance = null;
	}
	
	public int addHttpTask(HttpTask task) {
////		目前先这样屏蔽离线模式下的链接
//		if(!Tools.testNetworkIsAvailable()&&task.getCmdType()!=HttpTaskListener.CMD_checkNetwork&&task.getCmdType()!=HttpTaskListener.CMD_DownloadApk){
//			task.setmSerialId(-1);
////			Handler hander = task.handler;
////			if(handler != null){
////				Message msg = hander.obtainMessage();
////				msg.what = MainLogicController.MSG_HTTP_EXCEPTION;
////				msg.arg1 = ;
////				hander.sendMessage(msg);
////			}
//			return -1;
//		}
		synchronized(this) {
			task.setmSerialId(taskSeed++);
		}
		if(task.getPriority() == HttpTask.PRIORITY_NORMAL) {
			normalThreadPool.addHttpTask(task);
		}
		else if(task.getPriority() == HttpTask.PRIORITY_DOWNLOAD_ICON) {
			getIconThreadPool.addHttpTask(task);
		}
		else if(task.getPriority() == HttpTask.PRIORITY_DOWNLOAD_APK) {
			downloadAppThreadPool.addHttpTask(task);
		}
		return task.getmSerialId();
	}
	
	public int cancelTask(int taskId, boolean bCausePause) {
		int result = HttpThreadPool.CANCEL_NOT_EXIST;
		
		if(result == HttpThreadPool.CANCEL_NOT_EXIST) {
			result = normalThreadPool.cancelHttpTask(taskId, bCausePause);
		}
		if(result == HttpThreadPool.CANCEL_NOT_EXIST) {
			result = getIconThreadPool.cancelHttpTask(taskId, bCausePause);
		}
		if(result == HttpThreadPool.CANCEL_NOT_EXIST) {
			result = downloadAppThreadPool.cancelHttpTask(taskId, bCausePause);
		}
		return result;
	}
	
	public ArrayList<String> resetThreadPoolSize(int size, int poolType) {
		if (size <= 0) {
			return null;
		}
		if(poolType == POOL_TYPE_NORMAL) {
			return normalThreadPool.resetThreadPoolSize(size);
		} 
		else if(poolType == POOL_TYPE_DOWNLOAD_ICON) {
			return getIconThreadPool.resetThreadPoolSize(size);
		} 
		else if(poolType == POOL_TYPE_DOWNLOAD_APK) {
			return downloadAppThreadPool.resetThreadPoolSize(size);
		}
		return null;
	}

	
}





	
