package com.iory.zhixun.utils;


import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.os.Environment;
import android.os.Parcelable;
import android.os.StatFs;
import android.util.DisplayMetrics;
import android.util.Log;
import com.iory.zhixun.R;
import com.iory.zhixun.app.DLApp;
import com.iory.zhixun.ui.MainActivity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class Utils {
	
	private static final String CREATE_SHORTCUT_ACTION = "com.android.launcher.action.INSTALL_SHORTCUT";
	public static final int GET_MEMORY_ALREADY_USE = 0;
	public static final int GET_SDCARD_ALREADY_USE = 1;
	public static final int GET_MOBILE_ALREADY_USE = 2;
	/**
	 * 获取屏幕宽度
	 * 
	 * @return
	 */
	public static int getScreenWidth(Activity context) {
		DisplayMetrics md = new DisplayMetrics();
		(context).getWindowManager().getDefaultDisplay().getMetrics(md);
		return md.widthPixels;
	}


//	public static int getDpi() {
//		DisplayMetrics md = new DisplayMetrics();
//		WindowManager wm = (WindowManager) DLApp.getContext().getSystemService(Context.WINDOW_SERVICE);
//		wm.getDefaultDisplay().getMetrics(md);
//		return md.densityDpi;
//	}


	public static float getDensity(Context context) {
		return context.getResources().getDisplayMetrics().density;
	}


	/**
	 * 获取屏幕高度
	 * 
	 * @return
	 */
	public static int getScreenHeight(Activity context) {
		DisplayMetrics md = new DisplayMetrics();
		((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(md);
		return md.heightPixels;
	}


	public static Rect getIconBounds(Activity act) {
		int size = 45;
		int boundSize = (int) (Math.ceil(getDensity(act) * size));
		return new Rect(0, 0, boundSize, boundSize);

	}
	

	/**
	 * 添加自己快捷方式
	 * @param context
	 */
	public static void addSelfShortCut(Context context) {
		Intent intent = new Intent(context, MainActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);

		// 快捷方式的名称
		Intent shortcut = new Intent(CREATE_SHORTCUT_ACTION);
		shortcut.putExtra(Intent.EXTRA_SHORTCUT_NAME, context.getString(R.string.app_name));
		shortcut.putExtra("duplicate", false);

		// 快捷方式的图标
		Parcelable icon = Intent.ShortcutIconResource.fromContext(context, R.drawable.ic_launcher);
		shortcut.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, icon);
		shortcut.putExtra(Intent.EXTRA_SHORTCUT_INTENT, intent);
		context.sendBroadcast(shortcut);

	}
	
	/**
	 * 是否在用wifi
	 * 
	 * @return
	 */
	public static boolean isUsingWifi() {
		ConnectivityManager conMan = (ConnectivityManager) DLApp.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);

		// mobile 3G Data Network
		NetworkInfo mobile = conMan.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

		// wifi
		NetworkInfo wifi = conMan.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		if (wifi != null && wifi.getState() == State.CONNECTED) {
			return true;
		} else {
			return false;
		}
	}
	
	public static String getStorePath(Context context, String path,boolean downloadToSdCard) {
		if (downloadToSdCard) {
			// 获取SdCard状态
			String state = android.os.Environment.getExternalStorageState();
			// 判断SdCard是否存在并且是可用的
			if (android.os.Environment.MEDIA_MOUNTED.equals(state)) {
				if (android.os.Environment.getExternalStorageDirectory().canWrite()) {
					File file = new File(android.os.Environment.getExternalStorageDirectory().getPath() + path);
					if (!file.exists()) {
						file.mkdirs();
					}
					String absolutePath = file.getAbsolutePath();
					if (!absolutePath.endsWith("/")) {// 保证以"/"结尾
						absolutePath += "/";
					}
					return absolutePath;
				}
			}
		}
		String absolutePath = context.getFilesDir().getAbsolutePath();
		if (absolutePath!=null && !absolutePath.endsWith("/")) {// 保证以"/"结尾
			absolutePath += "/";
		}
		return absolutePath;
	}



	public static long getAvailableMemory() {
		ActivityManager am = (ActivityManager) DLApp.getContext().getSystemService(Context.ACTIVITY_SERVICE);
		ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
		am.getMemoryInfo(mi);
		return mi.availMem;
	}


	public static long getTotalMemory() {
		String str1 = "/proc/meminfo";// 系统内存信息文件
		String str2;
		String[] arrayOfString = new String[]{};
		long initial_memory = 0;
		try {
			FileReader localFileReader = new FileReader(str1);
			BufferedReader localBufferedReader = new BufferedReader(localFileReader, 8192);
			str2 = localBufferedReader.readLine();// 读取meminfo第一行，系统总内存大小
			if(str2!=null){
				arrayOfString = str2.split("\\s+");
			}
			for (String num : arrayOfString) {
				Log.i(str2, num + "\t");
			}
			initial_memory = Integer.valueOf(arrayOfString[1]).intValue() * 1024;// 获得系统总内存，单位是KB，乘以1024转换为Byte
			localBufferedReader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return initial_memory;// Byte转换为KB或者MB，内存大小规格化
	}


	/**
	 * 获得sd卡容量
	 * 
	 * @return
	 */
	public static long getTotalExternalMemorySize() {

		if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
			File path = Environment.getExternalStorageDirectory();
			StatFs stat = new StatFs(path.getPath());
			long blockSize = stat.getBlockSize();
			long totalBlocks = stat.getBlockCount();
			return totalBlocks * blockSize;
		} else {
			return -1;
		}
	}


	/**
	 * 获得手机可用容量
	 * 
	 * @return
	 */
	public static long getTotalInternalMemorySize() {

		File path = Environment.getDataDirectory();
		StatFs stat = new StatFs(path.getPath());
		long blockSize = stat.getBlockSize();
		long totalBlocks = stat.getBlockCount();
		return totalBlocks * blockSize;
	}


	/**
	 * 获得手机容量
	 * 
	 * @return
	 */
	public static long getPhoneStorageCapacity() {
		File path = Environment.getDataDirectory();
		StatFs stat = new StatFs(path.getPath());
		// long blockSize = stat.getBlockSize();
		// long availableBlocks = stat.getAvailableBlocks();
		// return (availableBlocks * blockSize)*1.0f/(1024*1024);

		// String root = Environment.getDataDirectory().getPath();
		// File base = new File(root);
		// StatFs stat = new StatFs(Environment.getDataDirectory().getPath());
		long nAvailableCount = stat.getBlockSize() * ((long) stat.getAvailableBlocks() - 4);
		if (nAvailableCount < 0l) {
			return 0l;
		}
		return nAvailableCount;
	}


	/**
	 * 获得sd卡容量
	 * 
	 * @return
	 */
	public static long getExternalStorageCapacity() {
		String root = null;
		if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
			root = Environment.getExternalStorageDirectory().getPath();
		} else {
			return 0;
		}

		File base = new File(root);
		StatFs stat = new StatFs(base.getPath());
		long nAvailableCount = stat.getBlockSize() * ((long) stat.getAvailableBlocks() - 4);
		if (nAvailableCount < 0l) {
			return 0l;
		}
		
//		TLog.v("TContext", "sdcard size:" + nAvailableCount);
		return nAvailableCount;
	}


	public static int getStorageStatusByType(int type) {
		int temp = 0;
		switch (type) {
		case GET_MEMORY_ALREADY_USE:
			temp = (int) (((getTotalMemory() - getAvailableMemory()) / 1024d) / (getTotalMemory() / 1024d) * 100);
			break;
		case GET_MOBILE_ALREADY_USE:
			temp = (int) (((getTotalInternalMemorySize() - getPhoneStorageCapacity()) / 1024d) / (getTotalInternalMemorySize() / 1024d) * 100);
			break;
		case GET_SDCARD_ALREADY_USE:
			if (getTotalExternalMemorySize() == -1) {
				temp = -1;
			} else {
				temp = (int) (((getTotalExternalMemorySize() - getExternalStorageCapacity()) / 1024d) / (getTotalExternalMemorySize() / 1024d) * 100);
			}
		default:
			break;
		}
		return temp;
	}
	
	/**
	 * 获取图片和安装包的存储路径, 以"/"结尾
	 * 
	 * @param context
	 * @param path
	 * @return
	 */
	public static String getStorePath(Context context, String path) {
			// 获取SdCard状态
			String state = android.os.Environment.getExternalStorageState();
			// 判断SdCard是否存在并且是可用的
			if (android.os.Environment.MEDIA_MOUNTED.equals(state)) {
				if (android.os.Environment.getExternalStorageDirectory().canWrite()) {
					File file = new File(android.os.Environment.getExternalStorageDirectory().getPath() + path);
					if (!file.exists()) {
						file.mkdirs();
					}
					String absolutePath = file.getAbsolutePath();
					if (!absolutePath.endsWith("/")) {// 保证以"/"结尾
						absolutePath += "/";
					}
					return absolutePath;
				}
		}
		String absolutePath = context.getFilesDir().getAbsolutePath();
		if (absolutePath!=null && !absolutePath.endsWith("/")) {// 保证以"/"结尾
			absolutePath += "/";
		}
		return absolutePath;
	}
	
	
	public static void clearnSubFiles(String path) {
		File f = new File(path);
		if (f.exists()) {
			File[] files = f.listFiles();
			if (files != null) {
				for (File file : files) {
					if (file.exists()) {
						file.delete();
					}
				}
			}
		}
	}



}
