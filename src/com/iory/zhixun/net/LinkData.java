package com.iory.zhixun.net;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Vector;

import org.apache.http.HttpResponse;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.format.Time;

import com.iory.zhixun.data.NewsItem;
import com.iory.zhixun.jce.ClientNewsSummary;
import com.iory.zhixun.jce.ReqGetNewsContent;
import com.iory.zhixun.jce.ReqGetNewsList;
import com.iory.zhixun.jce.ReqUserLogin;
import com.iory.zhixun.jce.ResGetNewsList;
import com.qq.jce.wup.UniPacket;
import com.qq.taf.jce.JceStruct;

/**
 * 用于网络请求
 * 
 * @author ioryli
 * @version 0.1
 */
public final class LinkData {

	private static final String TAG = "LinkData";
	
	
	
	public static final int GETNEWSLIST_SUCCESS = 1;
	
	
	
	
	
	
	

	/** 激活成功 */
	public static final int ACTIVE_SUCCESS = 1;
	/** 激活失败 */
	public static final int ACTIVE_FAILED = 2;

	/**
	 * 应用程序启动上报
	 */
	public static final int APP_LAUNCH_REPORT_SUCCESS = 3;

	/**
	 * 应用程序启动上报失败
	 */
	public static final int APP_LAUNCH_REPORT_FAILED = 4;
	/** 拉取广告成功 */
	public static final int GET_AD_SUCCESS = 5;
	/** 拉取广告失败 */
	public static final int GET_AD_FAILED = 6;

	/** 拉取图片成功 */
	public static final int GET_IMG_SUCCESS = 7;

	/** 拉取图片失败 */
	public static final int GET_IMG_FAILED = 8;

	/** 点击广告上报成功 */
	public static final int GET_CLICK_AD_SUCCESS = 9;

	/** 点击广告上报失败 */
	public static final int GET_CLICK_AD_FAILED = 10;

	/** 发送广告播放数据成功 */
	public static final int SEND_AD_DATA_SUCCESS = 11;

	/** 发送播放数据失败 */
	public static final int SEND_AD_DATA_FAILED = 12;

	/** 拉取点击效果图片成功 */
	public static final int GET_EFFECT_IMG_SUCCESS = 13;
	
	/** 拉取点击效果图片失败 */
	public static final int GET_EFFECT_IMG_FAILED = 14;
	
	/** 拉取点击mobwinlogo资源图片成功 */
	public static final int GET_MOBWINLOGO_IMG_SUCCESS = 15;
	
	/** 拉取点击mobwinlogo资源图片失败 */
	public static final int GET_MOBWINLOGO_IMG_FAILED = 16;
	
	/** 拉取点击确认button资源图片成功 */
	public static final int GET_CONFIRM_BUTTON_IMG_SUCCESS = 17;
	
	/** 拉取点击确认button资源图片失败 */
	public static final int GET_CONFIRM_BUTTON_IMG_FAILED = 18;
	
	/** 拉取点击确认button资源图片成功 */
	public static final int GET_CONFIRM_BUTTON_PRESSED_IMG_SUCCESS = 19;
	
	/** 拉取点击确认button资源图片失败 */
	public static final int GET_CONFIRM_BUTTON_PRESSED_IMG_FAILED = 20;

	/** 拉取BANNER广告边框资源图片成功 */
	public static final int GET_BANNER_FRAME_IMG_SUCCESS = 21;
	
	/** 拉取BANNER广告边框资源图片失败 */
	public static final int GET_BANNER_FRAME_IMG_FAILED = 22;
	
	/** 普通图片 */
	public static final int BITMAP_STATIC = 0;

	/** gif图片 */
	public static final int BITMAP_GIF = 1;

	/** 广告图片 */
	public static final int AD_BITMAP = 0;

	/** 效果触发图片 */
	public static final int EFFECT_BITMAP = 1;

	/** 资源图片 */
	public static final int RESOURCE_BITMAP = 2;

//	/**banner条资源图片*/
//	public static final int BANNER_RESOURCE_BITMAP = 3;
	
	/**mobwinlogo资源图片*/
	public static final int MOBWINLOGO_BITMAP = 3;
	
	/**点击确认button资源图片*/
	public static final int CONFIRM_BUTTON_BITMAP = 4;
	
	/**点击确认按下button资源图片*/
	public static final int CONFIRM_BUTTON_PRESSED_BITMAP = 5;
	
	/**BANNER广告边框资源图片*/
	public static final int BANNER_FRAME_BITMAP = 6;
	
	/** UniPackage 获取getByClass用参数 */
	static ReqUserLogin instance_ReqUserLogin = new ReqUserLogin(); 
	static ReqGetNewsList instance_ReqGetNewsList = new ReqGetNewsList();
	static ReqGetNewsContent instance_ReqGetNewsContent = new ReqGetNewsContent();	
	
	
	
	private static int seed = 1;

	// 命令字
	public final static int CMD_USERLOGIN = 1;
	public final static int CMD_GETNEWSLIST = 2;
	public final static int CMD_GETNEWSCONTENT = 3;

	
	private static LinkData instance = null;

	public static LinkData getInstance() {
		if (instance == null) {
			instance = new LinkData();
		}
		return instance;
	}
	
	public LinkData() {
		
	}

	/**
	 * 退出程序时取消网络请求
	 */
	public void onDestroy() {
		instance_ReqUserLogin.recyle();
		instance_ReqGetNewsList.recyle();
		instance_ReqGetNewsContent.recyle();
	}

	/**
	 * 打包公共代码
	 * 
	 * @param context
	 * @param unipkg
	 */
	private void packageUniPkg(Context context, UniPacket unipkg) {
		unipkg.setEncodeName("UTF-8");
		unipkg.setRequestId(seed++);
		unipkg.setServantName("zhixun");
	}

	/**
	 * 解包公共代码
	 * 
	 * @param data
	 * @return
	 */
	private static JceStruct unpackage(byte[] data ,  int cmdType) {
		if (data == null) {
			return null;
		}
		UniPacket client = new UniPacket();
		client.setEncodeName("UTF-8");
		client.decode(data);
		switch( cmdType ){
		case CMD_USERLOGIN:
			return client.getByClass("res", instance_ReqUserLogin);
		case CMD_GETNEWSLIST:
			return client.getByClass("res", instance_ReqGetNewsList);
		case CMD_GETNEWSCONTENT:
			return client.getByClass("res", instance_ReqGetNewsContent);
		default:
			return null;
		}
		//return client.get(RESPONSE_KEY.value);
	}

	/**
	 * 应用起动上报
	 * 
	 * @param context
	 * @param sysVersion
	 * @param appVersion
	 * @param s
	 * @return result
	 */
	public void sendGetNewsList(Context context, String requestUrl, int lastNewsId, int categoryId, final Handler handler) {

		// 测试模式下不上报
		// if (AdModel.isTestFlag()) {
		// return;
		// }
/*		String url = requestUrl + FUNCTION_APP_LAUNCH.value;

		UniPacket uniPacket = new UniPacket();
		packageUniPkg(context, uniPacket);
		uniPacket.setFuncName(FUNCTION_APP_LAUNCH.value);

		ReqAppLaunch req = new ReqAppLaunch();
		req.setUser_info(formatData.getUserInfo(context));
		req.setApp_info(formatData.getAppInfo(context));
		SettingVersions sv = new SettingVersions(appVersion, sysVersion);
		// TLog.v(TAG , "getAdActivateData 2:" + req.toString());
		req.settingVerions = sv;
		uniPacket.put(REQUEST_KEY.value, req);
		byte[] data = uniPacket.encode();

		HttpTask httptask = new HttpTask(url, true, CMD_APP_START, data, taskListener, handler);

		HttpTask.send(httptask);*/
		
		
		ArrayList<ClientNewsSummary> newsList = new ArrayList<ClientNewsSummary>();
		
		for (int i=0;i<5;i++){
			ClientNewsSummary newsSummary = new ClientNewsSummary(1, "title", "2013-07-02",null, "这是一条模拟的数据", "舅子", "www.qq.com", false, 5, null);
			newsList.add(newsSummary);
			
		}
		
		Message msg = handler.obtainMessage(GETNEWSLIST_SUCCESS);
		msg.arg1 = 1;
		msg.obj = newsList;
		handler.sendMessage(msg);
		
		return;
	}

	// private static ResAppLaunch resAppStartData(byte[] data) {
	// if (data == null) {
	// return null;
	// }
	//
	//		
	// // 获取响应数据
	// ResAppLaunch res = (ResAppLaunch) unpackage(data);
	// return res;
	// }

/*	public void sendADPlayData(final Context context, String requestUrl, final AdPlayStatus adStatus, int currentShowAdId, BannerInfo bannerInfo, final Handler handler) {
		// 测试模式下不上报
		if (AdModel.isTestFlag()) {
			return;
		}
		//如果以前的ID和现在ID都为0 ， 为无效数据，不上报
		if(adStatus.adId ==0 && currentShowAdId ==0){
			return;
		}
		String url = requestUrl + FUNCTION_REPORT_AD_PLAY_INFO.value;

		UniPacket uniPacket = new UniPacket();
		packageUniPkg(context, uniPacket);
		uniPacket.setFuncName(FUNCTION_REPORT_AD_PLAY_INFO.value);

		ReqReportAdPlayInfo req = new ReqReportAdPlayInfo();
		req.setUser_info(formatData.getUserInfo(context));
		req.setApp_info(formatData.getAppInfo(context));

		// TLog.v(TAG , "sendADPlayData 2:" + "adid"+adStatus.adId +
		// " lastTime:"+adStatus.lastTime);
		req.cur_ad_id = currentShowAdId;
		req.playingSeconds = (int) (adStatus.lastTime / 1000); // 把ms换算成s
		req.costmillseconds = adStatus.getAdMillSeconds;
		req.pre_ad_id = adStatus.adId;
		req.bannerInfo = bannerInfo;
		req.adPlayResultCode = adStatus.adPlayResultCode;
		req.sid  =  adStatus.currentSid;
		
		SimpleDateFormat   formatter   =   new   SimpleDateFormat   ("yyyy-MM-dd HH:mm:ss");     
		Date   curDate   =   new   Date(System.currentTimeMillis());//获取当前时间     
        req.sdkTimeStamp = formatter.format(curDate);     
		uniPacket.put(REQUEST_KEY.value, req);
		byte[] data = uniPacket.encode();

		HttpTask httptask = new HttpTask(url, true, CMD_AD_PLAY_INFO, data, taskListener, handler);

		HttpTask.send(httptask);
		return;
	}

	*//**
	 * 激活
	 * 
	 * @param context
	 * @param s
	 * @return result
	 *//*
	public void sendAdActivateData(Context context, String s, final Handler handler) {
		// 测试模式下不上报
		// if (AdModel.isTestFlag()) {
		// return;
		// }
		String url = s + FUNCTION_ACTIVATE_APP.value;

		UniPacket uniPacket = new UniPacket();
		packageUniPkg(context, uniPacket);
		uniPacket.setFuncName(FUNCTION_ACTIVATE_APP.value);

		ReqActivateApp req = new ReqActivateApp();
		req.setUser_info(formatData.getUserInfo(context));
		req.setApp_info(formatData.getAppInfo(context));

		TLog.v(TAG, "getAdActivateData 2:" + req.toString());

		uniPacket.put(REQUEST_KEY.value, req);
		byte[] data = uniPacket.encode();

		HttpTask httptask = new HttpTask(url, true, CMD_AD_ACTIVE, data, taskListener, handler);

		HttpTask.send(httptask);
		return;
	}

	*//**
	 * 获取广告信息
	 * 
	 * @param context
	 * @param s
	 * @return result
	 *//*
	public ResGetAD sendGetAdData(Context context, String s,BannerInfo bannerInfo,SettingVersions settingVersions,  final Handler handler) {
		String url = s + FUNCTION_GETAD.value;

		ResGetAD result = null;

		UniPacket uniPacket = new UniPacket();
		packageUniPkg(context, uniPacket);
		uniPacket.setFuncName(FUNCTION_GETAD.value);

		ReqGetAD req = new ReqGetAD();
		req.setUser_info(formatData.getUserInfo(context));
		req.setApp_info(formatData.getAppInfo(context));
		req.setSettingVerions(settingVersions);
		// 这句话没用。。。
		req.setAd_type(AD_TYPE._LOGO_WORDS);
		// 限制每次请求一条
		req.setAd_amount(1);
		// 看不懂
		req.setView_count_list(AdCount.getADViewAccInfoList(context));
		req.setClick_count_list(AdCount.getADClickAccInfoList(context));
		req.setLoc(formatData.getUserLocation(context));
		req.bannerInfo = bannerInfo;

		// TLog.v(TAG , "getUrlData 2:" + req.toString());
		uniPacket.put(REQUEST_KEY.value, req);
		byte[] data = uniPacket.encode();

		HttpTask httptask = new HttpTask(url, true, CMD_GET_AD_DATA, data, taskListener, handler);

		HttpTask.send(httptask);

		return result;
	}*/

	// /**
	// * post请求
	// *
	// * @param conn
	// * @param data
	// * @return read
	// */
	// private static byte[] httpRequestData(HttpURLConnection conn, byte[]
	// data){
	// byte read[] = null;
	// try{
	// conn.setDoOutput(true);
	// conn.setDoInput(true);
	// conn.setUseCaches(false);
	// conn.setRequestMethod("POST");
	// conn.setRequestProperty("Connection", "Keep-Alive");
	// conn.setRequestProperty("Charset", "UTF-8");
	// conn.setRequestProperty("Content-Length", String.valueOf(data.length));
	// conn.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
	// conn.setConnectTimeout(1000*20);
	// try{
	// conn.connect();
	// DataOutputStream outStream = new
	// DataOutputStream(conn.getOutputStream());
	// outStream.write(data);
	// outStream.flush();
	// outStream.close();
	// if(conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
	// // TLog.v(TAG , "httpRequestData 1:HTTP_OK");
	// read = readData(conn.getInputStream());
	// }else{
	// return null;
	// }
	// }catch(IOException ex){
	// ex.printStackTrace();
	// return null;
	// }
	// }catch(ProtocolException ex){
	// ex.printStackTrace();
	// return null;
	// }
	// return read;
	// }

	/**
	 * 广告点击上报
	 * 
	 * @param context
	 * @param s
	 * @param aid
	 * @param key
	 * @return result
	 */
	/*public void sendGetAdClickData(Context context, String s, int aid, int activated, String key,BannerInfo bannerInfo,  final Handler handler) {

		// 测试模式下不上报
		if (AdModel.isTestFlag()) {
			return;
		}

		String url = s + FUNCTION_CLICK.value;

		UniPacket uniPacket = new UniPacket();
		packageUniPkg(context, uniPacket);
		uniPacket.setFuncName(FUNCTION_CLICK.value);

		ReqClickAD req = new ReqClickAD();
		req.setUser_info(formatData.getUserInfo(context));
		req.setApp_info(formatData.getAppInfo(context));
		ADClickInfo clickinfo = new ADClickInfo();
		clickinfo.setAd_id(aid);
		clickinfo.setVri_key(key);
		clickinfo.setActivated(activated);
		req.setClick_info(clickinfo);
		req.bannerInfo = bannerInfo;
		req.setLoc(formatData.getUserLocation(context));

		TLog.v(TAG, "getAdClickData 2:" + req.toString());

		uniPacket.put(REQUEST_KEY.value, req);
		byte[] data = uniPacket.encode();

		HttpTask task = new HttpTask(url, true, CMD_AD_CLICK, data, taskListener, handler);
		HttpTask.send(task);
		// result = resClickAD(httpRequestData(httpurlconnection, data));

		return;
	}
*/
	//
	// /**
	// * 解析激活上报返回码
	// *
	// * @param data
	// * @return res
	// */
	// private static ResActivateApp resActivateApp(byte[] data) {
	// if (data == null) {
	// return null;
	// }
	//
	// UniPacket client = new UniPacket();
	// client.setEncodeName(PROTOCOL_ENCODING.value);
	// client.decode(data);
	// // 获取响应数据
	// ResActivateApp res = client.get(RESPONSE_KEY.value);
	// return res;
	// }

	// private static ResReportAdPlayInfo resADPlayData(byte[] data) {
	// if (data == null) {
	// return null;
	// }
	//
	// UniPacket client = new UniPacket();
	// client.setEncodeName(PROTOCOL_ENCODING.value);
	// client.decode(data);
	// // 获取响应数据
	// ResReportAdPlayInfo res = client.get(RESPONSE_KEY.value);
	// return res;
	// }

	// /**
	// * 解析广告数据
	// *
	// * @param data
	// * @return res
	// */
	// private static ResGetAD resGetAd(byte[] data) {
	// if (data == null) {
	// return null;
	// }
	// UniPacket client = new UniPacket();
	// client.setEncodeName(PROTOCOL_ENCODING.value);
	// client.decode(data);
	// // 获取响应数据
	// ResGetAD res = client.get(RESPONSE_KEY.value);
	// return res;
	// }

	// /**
	// * 解析广告点击上报返回码
	// *
	// * @param data
	// * @return res
	// */
	// private static ResClickAD resClickAD(byte[] data) {
	// if (data == null) {
	// return null;
	// }
	//
	// UniPacket client = new UniPacket();
	// client.setEncodeName(PROTOCOL_ENCODING.value);
	// client.decode(data);
	// // 获取响应数据
	// ResClickAD res = client.get(RESPONSE_KEY.value);
	// return res;
	// }

	/**
	 * 输入流格式转换
	 * 
	 * @param is
	 * @return read
	 */
	private static byte[] readData(InputStream is) {
		if (is == null) {
			return null;
		}
		// TLog.v(TAG , "readData 1:" + is.toString());
		byte read[] = null;
		ByteArrayOutputStream bytestream = new ByteArrayOutputStream();
		try {
			int ch;
			while ((ch = is.read()) != -1) {
				bytestream.write(ch);
			}
			read = bytestream.toByteArray();

		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		} finally {
			try {

				bytestream.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return read;
	}

	/**
	 * 获取图片
	 * 
	 * @param context
	 *            可为空
	 * @param url
	 *            图片网络地址
	 * @param handler
	 *            回调消息
	 * @param bitmapType
	 *            图片类型
	 * 
	 *            AD_BITMAP = 0; EFFECT_BITMAP = 1; RESOURCE_BITMAP =2; BANNER_RESOURCE_BITMAP=3;
	 */
/*	public void sendGetImage(final Context context, String url, final Handler handler, final int bitmapType) {
		HttpTask httpTask = new HttpTask(url, false, CMD_IMAGE, null, new HttpTaskListener() {

			@Override
			public void onReceiveResponseData(HttpResponse response, HttpTask httpTask) throws Exception {
				try {
					byte[] imageData = readData(response.getEntity().getContent());
					OperateCard.saveAdBitmap(Utils.convertUrlToLocalFile(httpTask.url), imageData, context);
					Message msg = Message.obtain();
					// 区分广告图标和点击效果图标,mobwinLogo与点击确认button
					if (bitmapType == EFFECT_BITMAP) {
						msg.what = GET_EFFECT_IMG_SUCCESS;
					} else if(bitmapType == MOBWINLOGO_BITMAP){
						msg.what = GET_MOBWINLOGO_IMG_SUCCESS;
					} else if(bitmapType == CONFIRM_BUTTON_BITMAP){
						msg.what = GET_CONFIRM_BUTTON_IMG_SUCCESS;
					}else if(bitmapType == CONFIRM_BUTTON_PRESSED_BITMAP){
						msg.what = GET_CONFIRM_BUTTON_PRESSED_IMG_SUCCESS;
					}else if(bitmapType == BANNER_FRAME_BITMAP){
						msg.what = GET_BANNER_FRAME_IMG_SUCCESS;
					}else{
						msg.what = GET_IMG_SUCCESS;
					}
					
					if (Utils.getFileFromUrl(httpTask.url).toLowerCase().endsWith(".gif")) {
						// GIF图片
						msg.arg1 = BITMAP_GIF;
						msg.obj = imageData;

					} else {
						// 静态图片
						Bitmap pic = BitmapFactory.decodeByteArray(imageData, 0, imageData.length);
						msg.arg1 = BITMAP_STATIC;
						msg.obj = pic;

					}
					Bundle bud = new Bundle();
					bud.putString("url", httpTask.url);
					msg.setData(bud);
					handler.sendMessage(msg);
				} catch (Exception e) {
					throw new DecodeException();
				}
			}

			@Override
			public void onDealHttpError(int errorCode, HttpTask httpTask) {

				// Message msg = Message.obtain();
				// msg.what = GET_IMG_SUCCESS;
				// if (httpTask.url.toLowerCase().endsWith(".gif")) {
				// //GIF图片
				// InputStream is;
				// try {
				// is = context.getAssets().open("aa.gif");
				//
				// byte[] data = new byte[is.available()];
				// is.read(data);
				// msg.arg1 = BITMAP_GIF;
				// msg.obj = data;
				// } catch (IOException e) {
				// // TODO Auto-generated catch block
				// e.printStackTrace();
				// }
				//
				// } else {
				// //静态图片
				// Bitmap pic =
				// BitmapFactory.decodeResource(context.getResources(),
				// R.drawable.bg);
				// msg.arg1 = BITMAP_STATIC;
				// msg.obj = pic;
				// }
				//				
				// Bundle bud = new Bundle();
				// bud.putString("url", httpTask.url);
				// msg.setData(bud);
				// handler.sendMessage(msg);
				Message msg = handler.obtainMessage(GET_IMG_FAILED);
				msg.arg1 = errorCode;
				handler.sendMessage(msg);

			}
		}, handler);
		HttpTask.send(httpTask);
	}*/

	
	
	com.iory.zhixun.http.HttpTaskListener taskListener = new com.iory.zhixun.http.HttpTaskListener() {

		@Override
		public void onReceiveResponseData(HttpResponse response, com.iory.zhixun.http.HttpTask httpTask) throws Exception {
			try {
				Handler handler = httpTask.handler;
				if (handler == null) {
					return;
				}
				byte[] data = readData(response.getEntity().getContent());
				switch (httpTask.cmdType) {
				case CMD_GETNEWSLIST: { // 应用启动

					ResGetNewsList result = (ResGetNewsList) unpackage(data , CMD_GETNEWSLIST);
					if (handler == null) {
						return;
					}
					if (result != null && result.code == 0) {
						Message msg = handler.obtainMessage(APP_LAUNCH_REPORT_SUCCESS);
						msg.arg1 = result.code;
						msg.obj = result;
						handler.sendMessage(msg);
					} else {
						Message msg = handler.obtainMessage(APP_LAUNCH_REPORT_FAILED);
						msg.arg1 = result.code;
						handler.sendMessage(msg);
					}

				}
					break;
				}
			} catch (Exception e) {
			}
		}


		@Override
		public void onDealHttpError(int errorCode,
				com.iory.zhixun.http.HttpTask httpTask) {
			// TODO Auto-generated method stub

			Handler handler = httpTask.handler;
			if (handler == null) {
				return;
			}
			switch (httpTask.cmdType) {
			case CMD_GETNEWSLIST: {
				

					Message msg = handler.obtainMessage(APP_LAUNCH_REPORT_FAILED);
					msg.arg1 = errorCode;
					handler.sendMessage(msg);
				
			}
				break;

			}
		
		}
	};
}
