package com.iory.zhixun.single;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;

import com.iory.zhixun.app.DLApp;
import com.iory.zhixun.app.TLog;
import com.iory.zhixun.utils.Tools.ImgTool;
import com.iory.zhixun.utils.Utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.widget.ImageView;


public class IconManager {

	private static final String TAG = IconManager.class.getName();

	/** 小图标的最大尺寸设为50K,超过50K就算大图标 */
	private static final int MAX_ICON_SIZE = 50 * 1024;

	/** 小图标最大缓存数 */
	private static final int MAX_ICON_COUNT = 1000;

	/** 大图标最大缓存数 */
	private static final int MAX_BIGICON_COUNT = 300;

	/** 图标扩展名 */
	private static final String ICON_EXT_NAME = ".qqmm";

	// 不同网络下的最大下载图标线程数
	private static final int MAX_DOWNLOAD_ICON_THREAD_WIFI = 6;
	private static final int MAX_DOWNLOAD_ICON_THREAD_3G = 6;
	private static final int MAX_DOWNLOAD_ICON_THREAD_2G = 3;

	private static final int MSG_DELAY_TIME_NET_CHANGED = 3000;

	public static final int MSG_REFRESH_ICON = 0x0001;
	public static final int MSG_ICON_THREAD_NUM_CHANGE = 0x0002;
	public static final int MSG_REMOVE_PINDINGVIEW = 0x0003;
	
	private static IconManager mInstence = null;
	private Context mContext = null;

	// 用于控制解析本地图片和网络拉取图片的线程运行
	private Object mDecodeLock = new Object();
	private Object mSaveFileLock = new Object();
	private Object mDownloadLock = new Object();

	private DecoderThread mDecoderThread = null;
	private DownloadThread mDownloadThread = null;
	private SaveIconThread mSaveIconThread = null;

	private static IconCache mCache = null;
	private static Object mCacheLock = new Object();

	// 图片保存路径
	public final static String ICON_CACHE_PATH = DLApp.BASE_ROOT_PATH + "/Icon/";

	// 放大图片的路径
	public final static String ICON_BIG_CACHE_PATH = DLApp.BASE_ROOT_PATH + "/IconCache/";

	private Map<String, Handler> mUrl2IconHandlerMap = new ConcurrentHashMap<String, Handler>();
	private Map<String, Handler> mUrl2IconHandlerSoftListMap = new ConcurrentHashMap<String, Handler>();

	/** 网络拉取图片列表，用于判断多次重复地址的请求拉取 */
	private Vector<String> mDownloadIconList = new Vector<String>();
	private Vector<String> mDownloadingList = new Vector<String>();

	// 这2个容器用来解析本地图片，第一个存url，第2个存url和path
	private Vector<String> mUrl2DecodeList = new Vector<String>();
	private Map<String, String> mUrl2PathMap = new ConcurrentHashMap<String, String>();

	// 这2个用来保存网络拉取的图片到本地
	private Vector<String> mOnIconDownloadedList = new Vector<String>();
	private Map<String, byte[]> mOnIconDownloadedMap = new ConcurrentHashMap<String, byte[]>();

	/** ImageView对应url的Map */
	private final ConcurrentHashMap<ImageView, String> mImageView2UrlMap = new ConcurrentHashMap<ImageView, String>();

	/** 当前网络状态，是否为移动网络 */
	private boolean mIsNetMobile = true;

	/** url对应http请求数据的seqId,用于取消请求使用 */
	private Map<String, Integer> mUrl2SeqIdMap = new ConcurrentHashMap<String, Integer>();

	// long t1 = 0;
	// long t2 = 0;
	// long t3 = 0;
	// long t4 = 0;

	// 省流量模式下默认图片
	public static final int ICON_TYPE_SOFTWARE_ICON = 0;
	public static final int ICON_TYPE_SOFTWARE_DETAIL_ICON = 1;
	public static final int ICON_TYPE_TOPIC_CATEGORY_ICON = 2;
	public static final int ICON_TYPE_TOPIC_DETAIL_ICON = 3;
	public static final int ICON_TYPE_USER_HEADER_ICON = 4;

	private Bitmap mSoftwareIcon = null;
	private Bitmap mSoftwareDetailIcon = null;
	private Bitmap mUserHeaderIcon = null;
	private Bitmap mTopicDetailIcon = null;
	private Bitmap mTopicCategoryIcon = null;

	/** 发送验证码图片 */
	ArrayList<String> mVerifyCodeList = new ArrayList<String>();

	private Map<String, Float> mRoundCornerWithSidelineIconMap = new ConcurrentHashMap<String, Float>();

	class DecoderThread extends Thread {

		private boolean bRun = true;

		@Override
		public void run() {
			while (bRun) {
				synchronized (mDecodeLock) {
					try {
						// Log.v(decodeLocktag, "decodeLock.wait");
						mDecodeLock.wait();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}

				while (mUrl2DecodeList.size() > 0) {
//					long t1 = System.currentTimeMillis();

					String url = null;
					String path = null;

					synchronized (mUrl2DecodeList) {
						url = mUrl2DecodeList.get(0);
						path = mUrl2PathMap.get(url);
						mUrl2DecodeList.remove(url);
						mUrl2PathMap.remove(url);
					}

					if (path != null && path.length() > 0) {
						Bitmap bitmap = null;
						try {
							bitmap = BitmapFactory.decodeFile(getAvaiableStorePath(false) + path); // 先在小图片中查找
						} catch (Throwable e) {
							MainController.getInstance().onLowMemory();
							// e.printStackTrace();
						}
						if (bitmap != null) {
							getCache().put(url, bitmap);
							doCallback(url, bitmap);
						}
						// 文件已不存在或已不是图片文件
						else {
							try {
								bitmap = BitmapFactory.decodeFile(getAvaiableStorePath(true) + path); // 再在大图片中查找
							} catch (Throwable e) {
								MainController.getInstance().onLowMemory();
								// e.printStackTrace();
							}
							if (bitmap != null) {
								getCache().put(url, bitmap);
								doCallback(url, bitmap);
							} else {
								addDownloadIconQueue(url); // 都找不到,就下载
							}
						}
					}

//					long t2 = System.currentTimeMillis();
//					TLog.v(TAG, "BitmapFactory.decodeFile time:" + (t2 - t1));
				}
			}
		}

		public void destory() {
			bRun = false;
			synchronized (mUrl2DecodeList) {
				mUrl2DecodeList.clear();
			}
			synchronized (mDecodeLock) {
				mDecodeLock.notify();
			}
		}
	}

	class SaveIconThread extends Thread {

		private boolean bRun = true;

		public void run() {
			while (bRun) {
				synchronized (mSaveFileLock) {
					try {
						// Log.v(saveFileTag, "saveFileLock.wait();");
						mSaveFileLock.wait();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				String url = "";
				byte[] data = null;
				while (mOnIconDownloadedList.size() > 0) {
					synchronized (mOnIconDownloadedList) {
						url = mOnIconDownloadedList.get(0);
						data = mOnIconDownloadedMap.get(url);
						mOnIconDownloadedList.remove(0);
						mOnIconDownloadedMap.remove(url);
					}
					if (data != null && data.length > 0) {
						// Log.v(saveFileTag, "start save file");
						iconDownloadedAndSaveFile(url, data);
					}
				}
			}
		}

		public void destory() {
			bRun = false;
			synchronized (mOnIconDownloadedList) {
				mOnIconDownloadedList.clear();
			}
			synchronized (mSaveFileLock) {
				mSaveFileLock.notify();
			}
		}
	}

	class DownloadThread extends Thread {

		private boolean bRun = true;

		// private ArrayList<Integer> listIdItems = new ArrayList<Integer>();

		public void run() {
			try {
				while (bRun) {
					synchronized (mDownloadLock) {
						try {
							// Log.v(saveFileTag, "downloadLock.wait();");
							mDownloadLock.wait();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}

					// if (openGetIconBytesFlag) {
					// if (!isNetMobile) {
					// Thread.sleep(150);//
					// 为了使这个时间内的所有getIcon请求存起来，批量操作，addDownloadIconQueue()方法在存请求
					// listIdItems = new
					// ArrayList<Integer>();//重新new对象，因为可能存入容器里
					// }
					// }

					// listIdItems.clear();
					while (mDownloadingList.size() > 0) {
						String url = mDownloadingList.remove(0);
						// if (openGetIconBytesFlag) {
						// // 移动网络不需要批量拉取
						// if (isNetMobile) {
						// int seqId =
						// MainLogicController.getInstance().sendDownloadIcon(url);
						// url2SeqIdMap.put(url, seqId);
						// } else {
						// // 是否可以走批量拉取协议
						// if (url2ProductIdMap.containsKey(url)) {
						// listIdItems.add(url2ProductIdMap.get(url));
						// Log.v(TAG, "getIconbytes pid:" +
						// url2ProductIdMap.get(url));
						// } else {
						// // 截图、广告位等不支持批量拉取，走普通拉取
						// int seqId =
						// MainLogicController.getInstance().sendDownloadIcon(url);
						// url2SeqIdMap.put(url, seqId);
						// }
						// }
						// } else {
						int seqId = MainController.getInstance().sendDownloadIcon(url);
						mUrl2SeqIdMap.put(url, seqId);
						// }
					}

					// if (openGetIconBytesFlag) {
					// if (!isNetMobile && listIdItems.size() > 0) {
					// if (listIdItems.size() == 1) {
					// // 只有一张图，普通拉取即可，节省协议封装的额外数据量
					// String url = productId2UrlMap.get(listIdItems.get(0));
					// int seqId =
					// MainLogicController.getInstance().sendDownloadIcon(url);
					// url2SeqIdMap.put(url, seqId);
					// } else {
					// // 批量拉取
					// int seqId =
					// MainLogicController.getInstance().sendGetIconBytes(null,
					// listIdItems, (byte) 1);
					// seqId2listIdItemsMap.put(seqId, listIdItems);
					// Log.v(TAG, "sendGetIconBytes seqId:" + seqId);
					// }
					// }
					// }
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

		}

		public void destory() {
			bRun = false;
			mDownloadingList.clear();
			synchronized (mDownloadLock) {
				mDownloadLock.notify();
			}
		}
	}

	private IconCache getCache() {
		if (mCache == null) {
			synchronized (mCacheLock) {
				if (mCache == null) {
					mCache = new IconCache();
				}
			}
		}
		return mCache;
	}

	private void addDecodeBitmapQueue(String url, String path) {
		boolean needNofity = false;
		synchronized (mUrl2DecodeList) {
			if (!mUrl2DecodeList.contains(url)) {
				mUrl2DecodeList.add(url);
				mUrl2PathMap.put(url, path);
				needNofity = true;
			}
		}
		if (needNofity) {
			synchronized (mDecodeLock) {
				// Log.v(decodeLocktag,
				// (j++)+"decodeLock.notify()"+"\n"+url+"\n"+path);
				mDecodeLock.notify();
			}
		}
	}

	private IconManager() {
		mContext = DLApp.getContext();
		// cache = new IconCache();

		mDecoderThread = new DecoderThread();
		mDecoderThread.start();

		mSaveIconThread = new SaveIconThread();
		mSaveIconThread.start();

		mDownloadThread = new DownloadThread();
		mDownloadThread.start();

		removeIcon();
	}

	/**
	 * 获取图标的储存路径
	 * 
	 * @param isBig 是大图标的路径?
	 * @return
	 */
	private String getAvaiableStorePath(boolean isBig) {
		if (isBig) {
			return Utils.getStorePath(mContext, ICON_BIG_CACHE_PATH);
		} else {
			return Utils.getStorePath(mContext, ICON_CACHE_PATH);
		}
	}

	/**
	 * 删除缓存图片
	 */
	public void deleteIconCache() {
		Thread t = new Thread() {
			public void run() {
				// 删除手机内部缓存 大图片
				String path = DLApp.getContext().getFilesDir() + ICON_BIG_CACHE_PATH;
				File files = new File(path);
				deleteFiles(files.listFiles());

				// 删除手机内部缓存 小图片
				path = DLApp.getContext().getFilesDir() + ICON_CACHE_PATH;
				files = new File(path);
				deleteFiles(files.listFiles());

				// 删除手机外部缓存 大图片
				path = getAvaiableStorePath(true);
				files = new File(path);
				deleteFiles(files.listFiles());

				// 删除手机外部缓存 小图片
				path = getAvaiableStorePath(false);
				files = new File(path);
				deleteFiles(files.listFiles());
			}
		};
		t.start();

	}

	public void init() {

		// loadIconPath();
	}

	protected static IconManager getInstance() {
		if (mInstence == null) {
			mInstence = new IconManager();
		}
		return mInstence;
	}

/*	// 省流量模式下获取默认图片
	public Bitmap getSaveNetTrafficDefaultIcon(int type) {
		try {
			switch (type) {
			case ICON_TYPE_SOFTWARE_ICON:
				if (mSoftwareIcon == null) {
					mSoftwareIcon = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.save_net_traffic_icon_default);
				}
				return mSoftwareIcon;
			case ICON_TYPE_TOPIC_CATEGORY_ICON:
				if (mTopicCategoryIcon == null) {
					mTopicCategoryIcon = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.save_net_traffic_topic_icon_default);
				}
				return mTopicCategoryIcon;
			case ICON_TYPE_TOPIC_DETAIL_ICON:
				if (mTopicDetailIcon == null) {
					mTopicDetailIcon = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.save_net_traffic_topic_detail_icon_default);
				}
				return mTopicDetailIcon;
			case ICON_TYPE_USER_HEADER_ICON:
				if (mUserHeaderIcon == null) {
					mUserHeaderIcon = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.comment_user_default_icon);
				}
				return mUserHeaderIcon;
			default:
				break;
			}
			if (type == ICON_TYPE_SOFTWARE_ICON) {

			} else if (type == ICON_TYPE_SOFTWARE_DETAIL_ICON) {
				if (mSoftwareDetailIcon == null) {
					mSoftwareDetailIcon = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.save_net_traffic_icon_default);
				}
				return mSoftwareDetailIcon;
			}
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return null;
	}
*/
	public Bitmap getIcon(String url, ImageView iv, int productid, Handler iconHandler, boolean isListScorllStateIdle) {
		// 有效性检查
		if (url != null) {
			if (!url.startsWith("http://")) {
				TLog.e(TAG, "url invalid:" + url);
				return null;
			}

			// boolean needDownLoadIcon = false;
			Bitmap bm = null;
			try {
				// 先去urlToBitmapMap找bitmap对象
				bm = getCache().get(url);
				if (bm == null) {

					// if (openGetIconBytesFlag) {
					// // 记录相应的productID和url，后台保证2者的唯一性，均可作key，用来做批量拉取协议使用
					// if (productid > 0) {
					// //
					// 这里需要把可能冲突的productID和url的一对多的情况做处理，不能保存在id2url和url2id的表里
					// if (!productId2CollisionUrlMap.containsKey(productid)) {
					// boolean existCollisionUrl = false;
					// String oldurl = productId2UrlMap.get(productid);
					// if (oldurl == null) {
					// productId2UrlMap.put(productid, url);
					// } else {
					// if (!oldurl.equals(url)) {
					// productId2CollisionUrlMap.put(productid, "");
					// productId2UrlMap.remove(productid);
					// existCollisionUrl = true;
					// }
					// }
					// if (existCollisionUrl) {
					// url2ProductIdMap.remove(oldurl);
					// } else {
					// url2ProductIdMap.put(url, productid);
					// }
					// }
					// }
					// }

					if (iv != null) {
						cachePindingImageView(url, iv, iconHandler);
					}

					// 如果是列表在滑动中，不要去本地解析图片或网络拉取图片（需要耗时），使用列表滑动更为顺畅，
					// 在列表停下来时，此标志改为true,然后进行列表的notifyDataChange来刷图标
					// 对于非列表滑动触发的getIcon，把这个标志置为true进行调用即可
					if (!isListScorllStateIdle) {
						return null;
					}

					// 去解析本地图片的列表里找
					if (!mUrl2DecodeList.contains(url)) {
						// 先把callback保存起来，用于后面的本地图片解析完或网络下载完后，通知UI刷新界面
						if (iconHandler != null) {
							mUrl2IconHandlerMap.put(url, iconHandler);
						}

						String path = convertUrlToLocalFile(url);
						if (path != null && path.length() > 0) {
							// 如果已在本地保存了图片，加到解析本地图片的队列里,
							addDecodeBitmapQueue(url, path);
						} else {
							// 本地也没找到，需要从网络拉取
							// needDownLoadIcon = true;
							//if (DataManager.getInstance().getSaveNetTrafficFlag() == SettingActivity.SETTING_SAVE_NET_TRAFFIC_NORMAL) {
								addDownloadIconQueue(url);
							//}
						}
					} else {
						// 还在本地图片解析队列里，请稍候
						return null;
					}
				} else {
					if (iv != null) {
						removePindingImageView(iv);
					}
					return bm;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return null;

	}

	private void addSaveDataFileQueue(String url, byte[] data) {
		synchronized (mOnIconDownloadedList) {
			if (!mOnIconDownloadedList.contains(url)) {
				mOnIconDownloadedList.add(url);
				mOnIconDownloadedMap.put(url, data);
			}
		}

		synchronized (mSaveFileLock) {
			mSaveFileLock.notify();
		}
	}

	// private int iconNum = 1000;

	/**
	 * 保存图片文件
	 * 
	 * @param url
	 * @param data
	 */
	private void iconDownloadedAndSaveFile(String url, byte[] data) {
		String lastName = convertUrlToLocalFile(url);
		String path = null;
		String dir = null;
		if (data.length < MAX_ICON_SIZE) {
			// 如果图片尺寸小于50KB,就算小图标,否则算大图标
			dir = getAvaiableStorePath(false);
			path = dir + lastName;
		} else {
			dir = getAvaiableStorePath(true);
			path = dir + lastName;

		}
		FileOutputStream fos = null;
		try {
			File fileDir = new File(dir);
			if (!fileDir.exists()) {
				fileDir.mkdirs();
			}

			File file = new File(path);
			if (file.exists()) {
				file.delete();
			}
			file.createNewFile();

			fos = new FileOutputStream(file, true);
			fos.write(data);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				fos = null;
			}
		}

		// SqlAdapter.getInstance().saveIcon(url, path);
		// if (!url2LocalPathMap.containsKey(url)) {
		// url2LocalPathMap.put(url, path);
		// }
	}

	private void doCallback(String url, Bitmap bm) {
		Message msg = Message.obtain();
		Object[] obj = new Object[2];
		obj[0] = url;
		obj[1] = bm;
		msg.what = MainController.MSG_getIconBitmap;
		msg.obj = obj;

		if (bm != null) {
			// 通知UI界面处理Icon请求完成事件
			Handler iconHandler = mUrl2IconHandlerMap.remove(url);
			if (iconHandler != null) {
				iconHandler.sendMessage(msg);
			}
		}

		Handler iconSoftListHandler = mUrl2IconHandlerSoftListMap.remove(url);
		if (iconSoftListHandler != null) {
			Message msg2 = Message.obtain();
			msg2.what = msg.what;
			msg2.obj = msg.obj;
			iconSoftListHandler.sendMessage(msg2);
		}

		// 刷新图标
		if (mHandler != null) {
			Message msg3 = Message.obtain();
			msg3.what = MSG_REFRESH_ICON;
			msg3.obj = url;
			mHandler.sendMessage(msg3);
		}

	}

	/**
	 * 把数据库里的url对应的path载入
	 * 
	 * @author dragonlin 2010.2.28
	 */
	// private void loadIconPath() {
	// long t1 = System.currentTimeMillis();
	// url2LocalPathMap.clear();
	// url2LocalPathMap = SqlAdapter.getInstance().getAllIcon();

	// 加载的时候，判断数据库存的路径下的图标还在否，清除不存在的url2path对应关系
	// String path = "";
	// File f = null;
	// ArrayList<String> needRemove = new ArrayList<String>();
	// for (String url : url2LocalPathMap.keySet()) {
	// path = url2LocalPathMap.get(url);
	// f = new File(path);
	// if (!f.exists()) {
	// needRemove.add(url);
	// // url2LocalPathMap.remove(url);
	// // SqlAdapter.getInstance().removeIconPath(url);
	// }
	// }
	//
	// for( int i =0;i< needRemove.size();i++)
	// {
	// url2LocalPathMap.remove(needRemove.get(i));
	// SqlAdapter.getInstance().removeIconPath(needRemove.get(i));
	// }

	// long t2 = System.currentTimeMillis();
	// Log.v(TAG, "loadIconPath time:"+(t2-t1));
	// }

	public void destory() {
		mUrl2DecodeList.clear();
		mUrl2PathMap.clear();

		mUrl2IconHandlerMap.clear();
		mUrl2IconHandlerSoftListMap.clear();

		// url2ProductIdMap.clear();
		// productId2UrlMap.clear();
		// productId2CollisionUrlMap.clear();
		// seqId2listIdItemsMap.clear();

		mDownloadIconList.clear();
		mDownloadingList.clear();
		mImageView2UrlMap.clear();

		mUrl2SeqIdMap.clear();

		mRoundCornerWithSidelineIconMap.clear();
		roundCornerIconMap.clear();

		if (mDecoderThread != null) {
			mDecoderThread.destory();
			mDecoderThread = null;
		}
		if (mSaveIconThread != null) {
			mSaveIconThread.destory();
			mSaveIconThread = null;
		}
		if (mDownloadThread != null) {
			mDownloadThread.destory();
			mDownloadThread = null;
		}

		mContext = null;

		// if (cache != null) {
		// cache.destory();
		// }
		// cache = null;
		getCache().destory();

		mHandler = null;

		mInstence = null;
	}

	/**
	 * 添加到下载列表
	 * 
	 * @param url
	 */
	private void addDownloadIconQueue(String url) {
		if (!mDownloadIconList.contains(url)) {
			mDownloadIconList.add(url);
		}
		// 下载
		if (!mDownloadingList.contains(url)) {
			mDownloadingList.add(url);
			synchronized (mDownloadLock) {
				mDownloadLock.notify();
			}
		}

	}

	public void onDownloadIconFinish(String url, byte[] data) {
		if (mDownloadIconList.contains(url)) {
			mDownloadIconList.remove(url);
		}

		mUrl2SeqIdMap.remove(url);

		boolean isContain = getCache().containsKey(url);
		// TLog.v("ICON", "onDownloadIconFinish, contain=" + isContain + "url=" + url);
		Bitmap bm;
		try {
			bm = BitmapFactory.decodeByteArray(data, 0, data.length);
		} catch (Throwable e) {
			e.printStackTrace();
			
			// 清除cache的imageVeiw
			Message msg3 = Message.obtain();
			msg3.what = MSG_REMOVE_PINDINGVIEW;
			msg3.obj = url;
			mHandler.sendMessage(msg3);
			return;
		}
		
		// if(!isContain) {
		getCache().put(url, bm);
		addSaveDataFileQueue(url, data);
		doCallback(url, bm);
		// }
	}

	/**
	 * 普通HTTP拉取图片异常
	 * 
	 * @param url
	 * @param errorCode
	 * @param errorStr
	 */
	public void onDownloadIconException(String url, int errorCode, String errorStr) {
		if (mDownloadIconList.contains(url)) {
			mDownloadIconList.remove(url);
		}

		mUrl2SeqIdMap.remove(url);

		doCallback(url, null);
	}

	// /**
	// * 批量拉取图片成功,里面的结果可能只有部分图片数据，需要检查
	// *
	// * @param mIconBytes
	// * @param seqId
	// */
	// public void onReceiveGetIconBytes(Map<Integer, byte[]> mIconBytes, int
	// seqId) {
	// Log.v(TAG, "onReceiveGetIconBytes seqId:" + seqId + " |" + mIconBytes);
	//
	// // 清除批量拉取记录
	// if (seqId2listIdItemsMap.containsKey(seqId)) {
	// ArrayList<Integer> listIdItems = seqId2listIdItemsMap.remove(seqId);
	//
	// if (mIconBytes != null && mIconBytes.size() > 0) {
	// byte[] data = null;
	// String url = "";
	// for (Integer i : mIconBytes.keySet()) {
	//
	// listIdItems.remove(i);
	//
	// data = mIconBytes.get(i);
	// url = productId2UrlMap.get(i);
	//
	// if (url != null && url.length() > 0) {
	// if (data != null && data.length > 0) {
	// onDownloadIconFinish(url, data);
	// } else {
	// // 数据为空，尝试用普通方法获取
	// int seq = MainLogicController.getInstance().sendDownloadIcon(url);
	// url2SeqIdMap.put(url, seq);
	// }
	// }
	// }
	// }
	//
	// // 有些图片没有返回数据，使用普通方法获取
	// if (listIdItems.size() > 0) {
	// Log.v(TAG, "listIdItems.size():" + listIdItems.size());
	// String url = "";
	// for (int i = 0; i < listIdItems.size(); i++) {
	// url = productId2UrlMap.get(listIdItems.get(i));
	// Log.v(TAG, "listIdItems url:" + url);
	// int seq = MainLogicController.getInstance().sendDownloadIcon(url);
	// url2SeqIdMap.put(url, seq);
	// }
	// }
	// }
	//
	// }
	//
	// /**
	// * 批量拉取图片返回联网异常后，尝试用普通拉取方法获取图片
	// *
	// * @param errorCode
	// * @param errorStr
	// * @param seqId
	// */
	// public void onReceiveGetIconBytesError(int errorCode, String errorStr,
	// int seqId) {
	// Log.v(TAG, "onReceiveGetIconBytesError seqId:" + seqId);
	//
	// if (seqId2listIdItemsMap.containsKey(seqId)) {
	// // 清除批量拉取记录
	// ArrayList<Integer> listIdItems = seqId2listIdItemsMap.remove(seqId);
	// if (listIdItems != null && listIdItems.size() > 0) {
	// for (int i = 0; i < listIdItems.size(); i++) {
	// // 找对应的URL
	// String url = productId2UrlMap.get(listIdItems.get(i));
	// if (url != null && url.length() > 0) {
	// // 拉取
	// int seq = MainLogicController.getInstance().sendDownloadIcon(url);
	// url2SeqIdMap.put(url, seq);
	// }
	// }
	// }
	// }
	// }

	public void onNetConnectTypeChanged(int connectType, String apnName) {

		int size = 0;
		switch (connectType) {
		case NetworkMonitor.CONNECT_TYPE_WIFI:
			mIsNetMobile = false;
			size = MAX_DOWNLOAD_ICON_THREAD_WIFI;
			break;
		case NetworkMonitor.CONNECT_TYPE_MOBILE_3G:
			mIsNetMobile = true;
			size = MAX_DOWNLOAD_ICON_THREAD_3G;
			break;
		case NetworkMonitor.CONNECT_TYPE_MOBILE_2G:
			mIsNetMobile = true;
			size = MAX_DOWNLOAD_ICON_THREAD_2G;
			break;
		default:
			mIsNetMobile = true;
			size = MAX_DOWNLOAD_ICON_THREAD_WIFI;
			break;
		}
		// 变更拉取图标的线程数，
		// 3G和wifi速度快，线程数多些，可以加快列表的图标加载
		// 2G带宽小，应该减小线程数，避免列表的图标加载过慢，逐个刷出来感觉比较好
		if (mHandler != null) {
			Message msg = Message.obtain();
			msg.what = MSG_ICON_THREAD_NUM_CHANGE;
			msg.arg1 = size;
			mHandler.removeMessages(MSG_ICON_THREAD_NUM_CHANGE);
			mHandler.sendMessageDelayed(msg, MSG_DELAY_TIME_NET_CHANGED);
		}

		// TLog.v(TAG, "onNetConnectTypeChanged icon thread size:" + size);
	}

	// 把大图和小图分开存
	private class IconCache {
		public static final int BIG_BITMAP_SIZE = 200;
		public static final int MAX_ICON_NUMBER = 300;
		public static final int MAX_BIGICON_NUMBER = 15;
		private Map<String, Bitmap> url2BitmapMap = new WeakHashMap<String, Bitmap>();

		private Map<String, Bitmap> urlBigBitmap = new WeakHashMap<String, Bitmap>();

		public IconCache() {

		}

		public void destory() {
			url2BitmapMap.clear();
			// url2BitmapMap = null;
			urlBigBitmap.clear();
			// urlBigBitmap = null;
		}

		public void put(String url, Bitmap bitmap) {
			if (bitmap == null) {
				return;
			}
			// 圆角+描边处理
			Float roundPx = mRoundCornerWithSidelineIconMap.get(url);
			if (roundPx != null) {

				bitmap = ImgTool.getSidelineBitmap(bitmap, roundPx, 0xff7e7f80);
			} else {
				// 圆角处理
				roundPx = roundCornerIconMap.get(url);
				if (roundPx != null) {
					float widthRound = bitmap.getWidth() / 9f;
					float heightRound = bitmap.getHeight() / 9f;
					roundPx = widthRound > heightRound ? heightRound : widthRound;
					bitmap = ImgTool.getRoundCornedBitmap(bitmap, roundPx);
				}
			}
			if (bitmap != null && bitmap.getHeight() > BIG_BITMAP_SIZE && bitmap.getWidth() > BIG_BITMAP_SIZE) {
				// 清理缓存
				if (urlBigBitmap.size() > MAX_BIGICON_NUMBER) {
					urlBigBitmap.clear();
				}
				urlBigBitmap.put(url, bitmap);
			} else {
				if (url2BitmapMap.size() > MAX_ICON_NUMBER) {
					url2BitmapMap.clear();
				}
				url2BitmapMap.put(url, bitmap);
			}
		}

		public Bitmap get(String url) {
			if (url == null || url.length() == 0) {
				return null;
			}
			Bitmap map = url2BitmapMap.get(url);

			if (map != null) {
				return map;
			} else {
				return urlBigBitmap.get(url);
			}
		}

		public boolean containsKey(String url) {
			if (url2BitmapMap.containsKey(url)) {
				return true;
			} else if (urlBigBitmap.containsKey(url)) {
				return true;
			}
			return false;
		}
	}

	/**
	 * 按规则清理图标文件 对于小图标,个数大于4000个清空 对于大图标,个数大于500个清空
	 */
	public void removeIcon() {
		Thread thread = new Thread() {
			public void run() {
				// 删除SD卡上的缓存
				handleIconFile(getAvaiableStorePath(false), MAX_ICON_COUNT, getAvaiableStorePath(true), MAX_BIGICON_COUNT);
			}
		};

		thread.start();
	}

	/**
	 * 处理图标目录 策略是: 如果小图标数量>maxCount,或者是大图标数量大于maxBigCount,就清空所有缓存图片,并且清空数据库
	 * 
	 * @param pathIcon 小图标路径
	 * @param maxCount 小图标最大限制数量
	 * @param pathBigIcon 大图标路径
	 * @param maxBigCount 大图标最大限制数量
	 */
	private void handleIconFile(String pathIcon, int maxCount, String pathBigIcon, int maxBigCount) {
		File normalFiles = new File(pathIcon);
		File bigFiles = new File(pathBigIcon);
		if (normalFiles.exists() || bigFiles.exists()) {
			File[] nordoc = normalFiles.listFiles();
			File[] bigdoc = bigFiles.listFiles();

			if ((nordoc != null && nordoc.length > maxCount) || (bigdoc != null && bigdoc.length > maxBigCount)) // 大于约定个数,删
			{
				deleteFiles(nordoc);
				deleteFiles(bigdoc);
				// // 删除数据库
				// SqlAdapter.getInstance().clearAllIcon();
			}

		}

	}

	/**
	 * 把网络路径转化为本地路径,把其中的特殊符号用_表示
	 * 
	 * @param url
	 * @return
	 */
	private String convertUrlToLocalFile(String url) {
		Uri uri = Uri.parse(url);
		String path = uri.getPath() + "_" + uri.getQuery();
		if (path != null) {
			path = path.replace("/", "_");
			path = path.replace("\\", "_");
			path = path.replace("*", "_");
			path = path.replace("?", "_");
			path = path.replace("=", "_");
			path = path.replace(".", "_");
			path += ICON_EXT_NAME;
		}
		// Log.v(TAG, path);
		return path;

	}

	/**
	 * 删除目录下的文件
	 * 
	 * @param files
	 */
	private void deleteFiles(File[] files) {
		if (files == null) {
			return;
		}
		for (int i = 0; i < files.length; i++) {
			File icons = files[i];
			icons.delete();
		}
	}

	/**
	 * 清除内存中的图片
	 */
	public void clear() {
		getCache().destory();
	}

	private void cachePindingImageView(String url, ImageView iv, Handler iconHandler) {
		if (iconHandler != null) {
			mUrl2IconHandlerSoftListMap.put(url, iconHandler);
		}
		String oldUrl = mImageView2UrlMap.get(iv);
		if (oldUrl != null && !oldUrl.equals(url)) {
			mUrl2IconHandlerSoftListMap.remove(oldUrl);
		}
		mImageView2UrlMap.put(iv, url);

	}

	private void removePindingImageView(ImageView iv) {
		if (mImageView2UrlMap.size() > 0) {
			mImageView2UrlMap.remove(iv);
		}
	}

	/**
	 * 请求等待的Icon
	 */
	public synchronized void requestPindingIcon() {
		// TLog.v("ICON", "requestPindingIcon mPendingReqImageView.size=" + mImageView2UrlMap.size());
		if (mImageView2UrlMap.size() == 0) {
			return;
		}
		Iterator<ImageView> iterator = mImageView2UrlMap.keySet().iterator();
		if (null == iterator) {
			return;
		}
		ImageView view = null;
		String iconUrl = null;
		while (iterator.hasNext()) {
			view = iterator.next();
			if (null == view) {
				continue;
			}
			iconUrl = mImageView2UrlMap.get(view);
			// 去解析本地图片的列表里找
			if (!mUrl2DecodeList.contains(iconUrl)) {
				String path = convertUrlToLocalFile(iconUrl);
				if (path != null && path.length() > 0) {
					// 如果已在本地保存了图片，加到解析本地图片的队列里,
					addDecodeBitmapQueue(iconUrl, path);
				} else {
					// 本地也没找到，需要从网络拉取
					addDownloadIconQueue(iconUrl);
				}
			}
		}
	}

	private void refreshPindingImageView(String url) {
		if (mImageView2UrlMap.size() == 0) {
			return;
		}
		Iterator<ImageView> iterator = mImageView2UrlMap.keySet().iterator();
		if (null == iterator) {
			return;
		}
		ImageView view = null;
		Bitmap bmp = null;
		String iconUrl = null;
		while (iterator.hasNext()) {
			view = iterator.next();
			if (null == view) {
				continue;
			}
			iconUrl = mImageView2UrlMap.get(view);
			bmp = getCache().get(iconUrl);
			if (bmp != null) {
				view.setImageBitmap(bmp);
				iterator.remove();
			}
		}
	}

	private void removePindingImageView(String url) {
		Iterator<ImageView> iterator = mImageView2UrlMap.keySet().iterator();
		if (null == iterator) {
			return;
		}
		ImageView view = null;
		String iconUrl = null;
		while (iterator.hasNext()) {
			view = iterator.next();
			if (null == view) {
				continue;
			}
			iconUrl = mImageView2UrlMap.get(view);
			if (url.equals(iconUrl)) {
				iterator.remove();
				break;
			}
		}
	}

	private RefreshImageViewHandler mHandler = null;

	public synchronized void newRefreshImageViewHandler() {
		if (mHandler == null) {
			mHandler = new RefreshImageViewHandler();
		}
	}

	class RefreshImageViewHandler implements Callback {

		private Handler h = new Handler(this);

		public void sendMessage(Message msg) {
			h.sendMessage(msg);
		}

		public void sendEmptyMessage(int what) {
			h.sendEmptyMessage(what);
		}

		public void sendMessageDelayed(Message msg, long delayMillis) {
			h.sendMessageDelayed(msg, delayMillis);
		}

		public void removeMessages(int what) {
			h.removeMessages(what);
		}

		@Override
		public boolean handleMessage(Message msg) {
			String url;
			switch (msg.what) {
			case MSG_REFRESH_ICON:
				url = (String) msg.obj;
				refreshPindingImageView(url);
				break;
			case MSG_REMOVE_PINDINGVIEW:
				url = (String) msg.obj;
				removePindingImageView(url);
				break;
			case MSG_ICON_THREAD_NUM_CHANGE:
				int size = msg.arg1;
				// TLog.v(TAG, "MSG_ICON_THREAD_NUM_CHANGE size:" + size);
//				HttpThreadPoolController.getInstance().resetThreadPoolSize(size, HttpThreadPoolController.POOL_TYPE_DOWNLOAD_ICON);
				break;
			default:
				break;
			}
			return false;
		}
	}

	// ---------- 发送验证码图片不需要保存图片地址 --------
	// ArrayList<Handler> uiHandlerList = new ArrayList<Handler>();

	/**
	 * 网络回调完成或异常时，是否为拉取验证码的url
	 * 
	 * @param url
	 * @return
	 */
	public boolean isVerifyCodeUrl(String url) {
		return mVerifyCodeList.size() == 0 ? false : mVerifyCodeList.contains(url);
	}

	/**
	 * 验证码拉取完成
	 * 
	 * @param url
	 * @param data
	 * @param h
	 */
/*	public void onVerifyCodeFinish(String url, byte[] data, Handler h) {
		mVerifyCodeList.remove(url);

		mUrl2SeqIdMap.remove(url);

		if (h != null) {
			Message msg = h.obtainMessage();
			Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
			Object[] obj = new Object[2];
			obj[0] = bitmap;
			obj[1] = url;
			msg.obj = obj;
			msg.what = MainController.MSG_getVerifyCode;
			h.sendMessage(msg);
		}
	}*/

	/**
	 * 验证码拉取异常
	 * 
	 * @param url
	 * @param errorCode
	 * @param errorStr
	 * @param h
	 */
/*	public void onVerifyCodeException(String url, int errorCode, String errorStr, Handler h) {
		mVerifyCodeList.remove(url);

		mUrl2SeqIdMap.remove(url);

		if (h != null) {
			Message msg = h.obtainMessage();
			msg.what = MainLogicController.MSG_getVerifyCodeError;
			msg.arg1 = errorCode;
			msg.obj = errorStr;
			h.sendMessage(msg);
		}
	}*/

	/**
	 * 发送获取验证码请求
	 * 
	 * @param url
	 * @param h
	 */
/*	public void sendVerifyCode(String url, Handler h) {
		if (!mVerifyCodeList.contains(url)) {
			mVerifyCodeList.add(url);
			int seq = MainLogicController.getInstance().sendDownloadIcon(url, h);
			mUrl2SeqIdMap.put(url, seq);
		}
	}*/

	// ----------------------------------------------

	/**
	 * 取消图片下载
	 * 
	 * @param url
	 * @return
	 */
/*	public int cancelGetPicRequest(String url) {
		if (mUrl2SeqIdMap.containsKey(url)) {
			int seqId = mUrl2SeqIdMap.remove(url);
			return MainController.getInstance().cancelRequestTask(seqId);
		} else {
			return HttpThreadPool.CANCEL_NOT_EXIST;
		}
	}*/

	public void addRoundCornerWithSidelineIconMap(String url, float roundPx) {
		mRoundCornerWithSidelineIconMap.put(url, roundPx);
	}

	private Map<String, Float> roundCornerIconMap = new ConcurrentHashMap<String, Float>();

	public void addRoundCornerIconMap(String url, float roundPx) {
		roundCornerIconMap.put(url, roundPx);
	}

	public boolean checkCacheContainIconByUrl(String url) {
		if (/* cache != null && */getCache().containsKey(url)) {
			return true;
		} else {
			boolean flag = mOnIconDownloadedMap.containsKey(url);
			return flag;
		}
	}
}
