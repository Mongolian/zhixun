package com.iory.zhixun.net;

import java.io.IOException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.URI;
import java.net.URLEncoder;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

import com.iory.zhixun.app.DLApp;
import com.iory.zhixun.app.TLog;

import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Handler;


/**
 * 
 * @author ioryli
 * @date 2013-6-10
 * 
 * @version 1.0
 */
public class HttpTask {

	private static final String TAG = "HttpTask";

	private HttpTaskListener listener = null;

	private int mSerialId = 0;

	public static final int PRIORITY_NORMAL = 0;

	public static final int PRIORITY_DOWNLOAD_ICON = 1;

	public static final int PRIORITY_DOWNLOAD_APK = 2;

	private int priority = HttpTask.PRIORITY_NORMAL;

	public final static int HTTP_CONNECTIONTIMEOUT = 30 * 1000; // 30 seconds
	public final static int HTTP_SOTIMEOUT = 30 * 1000; // 30 seconds
	public final static int HTTP_SOCKETBUFFERSIZE = 4 * 1024; // 4k, the default

	/**
	 * 重试次数
	 */
	private int tryCounts = 0;

	/**
	 * 最大重试次数
	 */
	public final static int MAX_TRY_COUNT = 3;
	private boolean is_use_proxy = false;
	private String proxy_host = "10.0.0.172";
	private int proxy_port = 80;

	public volatile boolean bNeedStop = false;
	public volatile boolean bNeedStopCauseByPause = false;
	public volatile boolean bHasReceiveData = false;

	public String url = "";
	private boolean bPost = true;

	// 协议或下载图标APK的类型
	protected int cmdType = -1;
	// 批量的协议合并时类型的集合
	protected HashMap<String, Integer> funName2cmdTypeMap = null;

	private byte[] requestData = null;

	public Handler handler = null;
	public HashMap<String, Handler> funName2HandlerMap = null;

	HttpPost httpPost = null;
	HttpGet httpGet = null;

	private Map<String, String> httpHeader = new HashMap<String, String>();

	protected int dataRangeIndex = -1;

	private URI uri = null;

	private String network;

	private HttpTask(String sUrl, boolean bPost, byte[] requestData, HttpTaskListener listener) {
		sUrl = sUrl.replace("\r", "");
		sUrl = sUrl.replace("\n", "");
		this.url = sUrl;
		this.bPost = bPost;
		this.requestData = requestData;
		this.listener = listener;

		try {
			//为了防止url里面的图片名含有空格或其它不合理字符导致url非法的问题,将其进行转义
			//http://softfile.3g.qq.com:8080/msoft/icon/180/15537/icon_AND_60*60.png?p_t=20111101155222
			Uri tempUri = Uri.parse(sUrl);
			String encodeUrl = new String(sUrl);
			String oldPath = tempUri.getLastPathSegment();
			if (oldPath != null && oldPath.length() > 0) {
				String path = tempUri.getLastPathSegment();
				path = URLEncoder.encode(path);
				path = path.replace("+", "%20");
				encodeUrl = encodeUrl.replace(oldPath, path);
			}
			uri = new URI(encodeUrl);
		} catch (Exception e) {
			e.printStackTrace();
			TLog.v(TAG, "new URI Exception:" + e.getMessage());
		}
	}

	public HttpTask(String sUrl, boolean bPost, int cmdType, byte[] requestData, HttpTaskListener listener,Handler handler) {
		this(sUrl, bPost, requestData, listener);
		this.cmdType = cmdType;
		this.handler = handler;
	}

	public HttpTask(String sUrl, boolean bPost, HashMap<String, Integer> funName2cmdTypeMap, byte[] requestData, HttpTaskListener listener) {
		this(sUrl, bPost, requestData, listener);
		this.funName2cmdTypeMap = funName2cmdTypeMap;
	}

	public int getmSerialId() {
		return mSerialId;
	}

	public void setmSerialId(int id) {
		mSerialId = id;
	}

	public void setHandler(Handler handler) {
		this.handler = handler;
	}

	public void setHandler(HashMap<String, Handler> funName2HandlerMap) {
		this.funName2HandlerMap = funName2HandlerMap;
	}

	public int getPriority() {
		return priority;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}

	public void setDataRangeIndex(int dataRangeIndex) {
		this.dataRangeIndex = dataRangeIndex;
	}

	public boolean isUseProxy() {
		return is_use_proxy;
	}

	public void useProxy(boolean isUseProxy) {
		is_use_proxy = isUseProxy;
	}

	public void setProxy(String proxyHost, int proxyPort) {
		proxy_host = proxyHost;
		proxy_port = proxyPort;
	}

	public void cancel() {
		bNeedStop = true;
		TLog.v(TAG, "canceled");
	}

	public void addHeader(String name, String value) {
		if (name != null && name.length() > 0) {
			httpHeader.put(name, value);
		}
	}

	private boolean isUrlAvailable(URI uri) {
		if (uri == null || uri.getScheme() == null || !uri.getScheme().startsWith("http")) {
			return false;
		} else {
			return true;
		}
	}

	public void exec() {
		HttpClient httpClient = null;
		HttpResponse response = null;
		int status = 0;
		bHasReceiveData = false;
		if (bNeedStop) {
			return;
		}
		// 判断地址是否有效,如无效,则直接返回错误
//		if (!isUrlAvailable(uri)) {
//			listener.onDealHttpError(cmdType, funName2cmdTypeMap, HttpTaskListener.ERROR_URI_format_error, "", this);
//			TLog.v(TAG, "ERROR_URI_format_error:" + uri);
//			return;
//		}
		while (tryCounts < MAX_TRY_COUNT) {
			
			//下载APK包和软件图标不作重试
			if (cmdType == HttpTaskListener.CMD_DownloadApk || cmdType == HttpTaskListener.CMD_DownloadIcon) {
				tryCounts = MAX_TRY_COUNT;
			}
			
			try {
				httpClient = createHttpClient();

				if (bNeedStop) {
					httpClient.getConnectionManager().shutdown();
					httpClient = null;
					return;
				}

				if (bPost) {
					httpPost = new HttpPost();
					httpPost.setURI(uri);
					if (requestData != null && requestData.length > 0) {
						httpPost.setEntity(new ByteArrayEntity(requestData));
					}
				} else {
					httpGet = new HttpGet();
					httpGet.setURI(uri);
				}

				if (httpHeader.size() > 0) {
					for (String name : httpHeader.keySet()) {
						if (bPost) {
							httpPost.addHeader(name, httpHeader.get(name));
						} else {
							httpGet.addHeader(name, httpHeader.get(name));
						}
					}
				}

				HttpContext localContext = new BasicHttpContext();

				if (bPost) {
					TLog.v(TAG, "httpSend " + Thread.currentThread().getName() + "URL:" + httpPost.getURI());
					response = httpClient.execute(httpPost, localContext);
				} else {
					TLog.v(TAG, "httpSend " + Thread.currentThread().getName() + "URL:" + httpGet.getURI());
					response = httpClient.execute(httpGet, localContext);
				}

				if (bNeedStop) {
					httpClient.getConnectionManager().shutdown();
					httpClient = null;
					return;
				}

				status = response.getStatusLine().getStatusCode();

				Date date = new Date(System.currentTimeMillis());
				TLog.v(TAG, "httpRes " + Thread.currentThread().getName() + " StatusCode:" + status + " " + date.toLocaleString());

				// 如果返回码不是200,就直接给出错误信息
				if ((status != HttpStatus.SC_OK) && (status != HttpStatus.SC_PARTIAL_CONTENT)) {
					if (bPost) {
						httpPost.abort();
					} else {
						httpGet.abort();
					}
					if (httpClient != null) {
						httpClient.getConnectionManager().shutdown();
						httpClient = null;
					}
					if (response != null) {
						response = null;
					}
					if (listener != null) {
						listener.onDealHttpError(cmdType, funName2cmdTypeMap, status, "", this);
					}
					return;
				}

				// 进行Content-Type判断，预防text/html等类型的收费提示页面
				Header[] headers = response.getHeaders("Content-Type");
				Header ct = null;
				if (headers != null) {
					for (int i = 0; i < headers.length; i++) {
						if (headers[i].getName().equalsIgnoreCase("Content-Type")) {
							ct = headers[i];
							break;
						}
					}
				}
				TLog.v(TAG, "contentType:" + ct);
				//只有wifi才判断content type
				if (ct != null && "wifi".equals(network)) {
					if (ct.getValue().startsWith("text")) {
						throw new Exception("Content-Type Error" + ct.getValue());
					}
				} else {
					TLog.v(TAG, "no content-type");
				}

				if (bNeedStop) {
					httpClient.getConnectionManager().shutdown();
					httpClient = null;
					return;
				}

				if (listener != null) {
					listener.onReceiveResponseData(cmdType, funName2cmdTypeMap, response, this);
				}
				
				if (httpClient != null) {
					httpClient.getConnectionManager().shutdown();
					httpClient = null;
				}
				if (response != null) {
					response = null;
				}
				// 正常结束,
				return;
			} catch (Exception e) {
				e.printStackTrace();
				int errorCode = HttpTaskListener.ERROR_Throwable;
				if (e instanceof ClientProtocolException) {
					errorCode = HttpTaskListener.ERROR_ClientProtocolException;
				} else if(e instanceof SocketException) {
					errorCode = HttpTaskListener.ERROR_SocketException;
				} else if(e instanceof SocketTimeoutException) {
					errorCode = HttpTaskListener.ERROR_SocketTimeoutException;
				} else if(e instanceof IOException) {
					errorCode = HttpTaskListener.ERROR_IOException;
				} else if(e instanceof ClassCastException) {
					errorCode = HttpTaskListener.ERROR_ClassCastException;
				}
				// 最后一次才抛异常,以下皆是
				log("HttpTask.exec(), Exception : " + e.toString());
				
				if (listener != null && tryCounts >= MAX_TRY_COUNT - 1) {
					listener.onDealHttpError(cmdType, funName2cmdTypeMap, errorCode, e.toString(), this);
				}
			} catch (Throwable e) {
				log("HttpTask.exec(), Throwable : " + e.toString());
				e.printStackTrace();
				if (listener != null && tryCounts >= MAX_TRY_COUNT - 1) {
					listener.onDealHttpError(cmdType, funName2cmdTypeMap, HttpTaskListener.ERROR_Throwable, e.toString(), this);
				}
			} finally {
				if (httpClient != null) {
					httpClient.getConnectionManager().shutdown();
					httpClient = null;
				}
				if (response != null) {
					response = null;
				}
			}
			
			//下载APK包和软件图标不作重试
			if (cmdType != HttpTaskListener.CMD_DownloadApk && cmdType != HttpTaskListener.CMD_DownloadIcon) {
				tryCounts++;
			}
			
			TLog.v(TAG, "try count" + tryCounts);
		}
		return;

	}

	/** 获取网络信息 */
	public static String getNetStatus(Context context) {
		if (context.checkCallingOrSelfPermission("android.permission.ACCESS_NETWORK_STATE") == PackageManager.PERMISSION_DENIED) {
			// TLog.v(TAG ,
			// "httpUrlConnect 3:Cannot request an ad without ACCESS_NETWORK_STATE permissions!  Open manifest.xml and just before the final");
			return "";
		}
		ConnectivityManager connectivitymanager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkinfo = connectivitymanager.getActiveNetworkInfo();
		if (networkinfo == null) {
			// TLog.v(TAG ,
			// "httpUrlConnect 4:can not connect to network,please check the network configuration");
			return "";
		}
		// if (!networkinfo.isAvailable()){
		// TLog.v(TAG ,
		// "httpUrlConnect 5:can not connect to network,please check the network configuration");
		// return;
		// }
		if (networkinfo.getType() == ConnectivityManager.TYPE_WIFI) {
			// TLog.v(TAG , "httpUrlConnect 6:ACCESS_NETWORK_STATE:wifi");

			return "wifi";

		}
		String netInfo = networkinfo.getExtraInfo();
		if (netInfo == null) {
			return "";
		}
		netInfo = netInfo.toLowerCase();
		// TLog.v(TAG , "httpUrlConnect 8:ACCESS_NETWORK_STATE:" + netInfo);
		return netInfo;
	}

	private HttpClient createHttpClient() {
		HttpClient httpClient = null;
		HttpHost proxy = null;

		HttpParams params = new BasicHttpParams();

		HttpConnectionParams.setConnectionTimeout(params, HTTP_CONNECTIONTIMEOUT);
		HttpConnectionParams.setSoTimeout(params, HTTP_SOTIMEOUT);
		HttpConnectionParams.setSocketBufferSize(params, HTTP_SOCKETBUFFERSIZE);
		HttpClientParams.setRedirecting(params, true);

		httpClient = new DefaultHttpClient(params);

		network = getNetStatus(DLApp.getContext());

		if (network != null && (network.equalsIgnoreCase("cmwap") || network.equalsIgnoreCase("3gwap") || network.equalsIgnoreCase("uniwap"))) {
			proxy = new HttpHost(proxy_host, proxy_port);
			httpClient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);
		}

		return httpClient;
	}

	protected void log(String s) {
		TLog.v(TAG, s + "\n");
	}

	public int getCmdType() {
		return cmdType;
	}
	
	public static void send(final HttpTask task)
	{
		if( task == null)
		{
			return;
		}
		Thread s = new Thread()
		{
			public void run()
			{
				task.exec();
			}
		};
		s.start();
     }
	
}
