package com.iory.zhixun.net;

import java.util.ArrayList;
import java.util.Vector;

import com.iory.zhixun.app.DLApp;
import com.iory.zhixun.app.TLog;

import android.content.Context;
import android.os.PowerManager;


/**
 *
 * @author dragonlin
 * @date 2011-3-10	
 *
 * @version 1.0
 */
public class HttpThreadPool {
	
	private static final String TAG = "HttpThreadPool";
	
	private Object threadlock = new Object();
	
	private Object sendingListLock = new Object();
	private Object execListLock = new Object();
	
	private Vector<HttpTask> sendingList = new Vector<HttpTask>();
	private Vector<HttpTask> execList = new Vector<HttpTask>();
	
	private ArrayList<HttpThread> threadPool = null;
	
//	private int threadPoolNum = 2;
	private int threadPoolType = HttpThreadPoolController.POOL_TYPE_NORMAL;
	
	//取消任务的结果，不存在、在发送列表、在执行联网列表里，用来在cancelTask时，提供返回值信息
	public static final int CANCEL_NOT_EXIST = -1;
	public static final int CANCEL_SENDLIST = 0;
	public static final int CANCEL_EXELIST_NOT_RECEIVE_DATA = 1;
	public static final int CANCEL_EXELIST_HAS_RECEIVE_DATA = 2;
	
	private PowerManager pm = null;
	
	public HttpThreadPool(int num, int type) {
		int threadPoolNum = ((num <= 0) ?  2 : num);
		threadPoolType = type;
		
		threadPool = new ArrayList<HttpThread>();
		for(int i = 0; i < threadPoolNum; i++) {
			threadPool.add(new HttpThread());
		}
	}
	
	public int getThreadPoolType() {
		return threadPoolType;
	}
	
	public int cancelHttpTask(int taskId, boolean bCausePause) {
		if(taskId < 0) {
			return CANCEL_NOT_EXIST;
		}
		synchronized(sendingListLock) {
			if(sendingList.size() > 0) {
				for(int i = 0; i < sendingList.size(); i++) {
					HttpTask h = sendingList.get(i);
					if(taskId == h.getmSerialId()) {
						h.bNeedStopCauseByPause = bCausePause;
						h.cancel();
						return CANCEL_SENDLIST;
					}
				}
			}
		}
		synchronized(execListLock) {
			if(execList.size() > 0) {
				for(int i = 0; i < execList.size(); i++) {
					HttpTask h = execList.get(i);
					if(taskId == h.getmSerialId()) {
						h.bNeedStopCauseByPause = bCausePause;
						h.cancel();
						return h.bHasReceiveData ? CANCEL_EXELIST_HAS_RECEIVE_DATA : CANCEL_EXELIST_NOT_RECEIVE_DATA;
					}
				}
			}
		}
		return CANCEL_NOT_EXIST;
	}
	
	public void start() {
		for(int i = 0; i < threadPool.size(); i++) {
			HttpThread ht = threadPool.get(i);
			ht.start();
		}
	}
	
	public void stop() {
		for(int i = 0; i < threadPool.size(); i++) {
			HttpThread ht = threadPool.get(i);
			ht.bRun = false;
		}
		
		sendingList.clear();
		
		synchronized(threadlock){
			threadlock.notifyAll();
		}
	}
	
	public int addHttpTask(HttpTask task) {
		sendingList.add(task);
		synchronized(threadlock){
			threadlock.notifyAll();
		}
		return task.getmSerialId();
	}
	
	private PowerManager getPowerManager() {
		if (pm == null) {
			pm = (PowerManager) DLApp.getContext().getSystemService(Context.POWER_SERVICE);
		}
		return pm;
	}
	
	private void execHttpMessage(HttpTask task) {
		//联网时禁止休眠
		PowerManager.WakeLock wakeLock = getPowerManager().newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "httpNet");
		
		wakeLock.acquire();

		//http连接
		task.exec();

		wakeLock.release();
	}
	
	/**
	 * 重新设定联网的线程池大小
	 * 
	 * @param size
	 * @return 被停掉的多余的正在联网任务的url，只有正在联网的任务数  > 新设定的大小时，有数据返回
	 */
	public ArrayList<String> resetThreadPoolSize(int size) {
		TLog.v(TAG, "resetThreadPoolSize:"+threadPool.size()+"|"+size);
		
		ArrayList<String> connectingTaskUrls = null;
		if (threadPool.size() == size) {
			
		} 
		else if(threadPool.size() > size) {
			connectingTaskUrls = new ArrayList<String>();
			int connectingNum = 0;
			for (int i = 0; i < threadPool.size(); i++) {
				HttpThread ht = threadPool.get(i);
				if (ht.bConnecting) {
					connectingNum++;
					if (connectingNum > size) {
						//把正在联网的多余的线程停掉，记录下载的Url，用于在下载管理类进行下载相关的数据保存
						connectingTaskUrls.add(ht.taskUrl);
						ht.bRun = false;
						threadPool.remove(ht);
						i--;
						TLog.v(TAG, "把正在联网的多余的线程停掉:"+ht.taskUrl);
					}
				}
			}
			//把空闲的线程停掉
			int keepAliveNum = size - (connectingNum > size ? size : connectingNum);
			TLog.v(TAG, "keepAliveNum:"+keepAliveNum+" connectingNum:"+connectingNum);
			for (int i = 0; i < threadPool.size(); i++) {
				HttpThread ht = threadPool.get(i);
				if (!ht.bConnecting) {
					if (keepAliveNum > 0) {
						keepAliveNum--;
					} else {
						ht.bRun = false;
						threadPool.remove(ht);
						i--;
						TLog.v(TAG, "把空闲的线程停掉:"+ht.getId());
					}
				}
			}
			//唤醒线程，退出循环
			synchronized(threadlock){
				threadlock.notifyAll();
			}
		} 
		else if(threadPool.size() < size) {
			int increase = size - threadPool.size();
			for (int i = 0; i < increase; i++) {
				HttpThread ht = new HttpThread();
				ht.start();
				threadPool.add(ht);
				TLog.v(TAG, "添加新的空闲线程:"+ht.getId());
			}
			
			//让新的空闲线程都run起来
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			//唤醒线程，让新的线程去执行HttpTask
			synchronized(threadlock){
				threadlock.notifyAll();
			}
		}
		TLog.v(TAG, "newThreadPoolSize:"+threadPool.size());
		return connectingTaskUrls;
	}

	
	/**
	 * 联网线程，用于执行各种HttpTask
	 * 
	 * @author dragonlin
	 * @version 1.0
	 */
	class HttpThread extends Thread {
		
		/** 线程是否运行  */
		private boolean bRun = true;
		
		/** 是否正在执行HttpTask */
		private boolean bConnecting = false;
		
		/** 执行HttpTask时的TaskID */
		private String taskUrl = "";
		
		public void run() {
			TLog.v(TAG, "threadPoolType:"+threadPoolType+" HttpThread start"+Thread.currentThread().getId());
			while(bRun) {
				synchronized(threadlock) {
					try {
						threadlock.wait();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				if (!bRun) {
					return;
				}
				HttpTask httpTask = null;
				while(sendingList.size() > 0) {
					if (!bRun) {
						return;
					}
					synchronized(sendingListLock) {
//						if (threadPoolType == HttpThreadPoolController.POOL_TYPE_DOWNLOAD_APK) {
							TLog.v(TAG, "threadPoolType:" + threadPoolType + " HttpThreadID:" + Thread.currentThread().getId() + " sendingList:"
									+ sendingList.size());
//						}
						if(sendingList.size() > 0) {
							httpTask = sendingList.get(0);
							sendingList.remove(httpTask);
						}
					}
					
					if(httpTask != null) {
						synchronized(execListLock) {
							execList.add(httpTask);
						}
						
						bConnecting = true;
						taskUrl = httpTask.url;
						
						execHttpMessage(httpTask);
						
						bConnecting = false;
						taskUrl = "";
						
						synchronized(execListLock) {
							execList.remove(httpTask);	
						}
					}
				}
				
			}
		}
	}
}
