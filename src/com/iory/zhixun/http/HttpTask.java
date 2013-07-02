package com.iory.zhixun.http;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
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

import com.iory.zhixun.app.TLog;

import android.os.Handler;


/**
 * 
 * @author dragonlin
 * @date 2011-3-10
 * 
 * @version 1.0
 */
public class HttpTask {
	
	private static final String TAG = "HttpTask";

	private HttpTaskListener listener = null;

//	private int mSerialId = 0;

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

	public boolean bNeedStop = false;
	
	public String url = "";
	
	private boolean bPost = true;
	
	
	private byte[] requestData = null;
	
	public int cmdType = 0;
	public Handler handler = null;
	HttpPost httpPost = null;
	HttpGet httpGet = null;

	private Map<String, String> httpHeader = new HashMap<String, String>();

	private URI uri = null;

	public HttpTask(String sUrl, boolean bPost, int cmdType, byte[] requestData, HttpTaskListener listener, Handler handler) {
		this.url = sUrl;
		this.bPost = bPost;
		this.cmdType = cmdType;
		this.requestData = requestData;
		this.listener = listener;
		this.handler = handler;
		try {
			uri = new URI(url);
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
	}


//
//	public int getmSerialId() {
//		return mSerialId;
//	}
//
//	public void setmSerialId(int id) {
//		mSerialId = id;
//	}

	public void cancel() {
		bNeedStop = true;
//		Log.v(TAG, "canceled");
	}

	public void addHeader(String name, String value) {
		if (name != null && name.length() > 0) {
			httpHeader.put(name, value);
		}
	}

	private boolean isUrlAvailable(URI uri) {
		if (uri.getScheme() == null || !uri.getScheme().startsWith("http")) {
			return false;
		} else {
			return true;
		}
	}

	public void exec() {
		HttpClient httpClient = null;
		HttpResponse response = null;
		int status = 0;
		if (bNeedStop) {
			return;
		}
		// 判断地址是否有效,如无效,则直接返回错误
		if (!isUrlAvailable(uri)) {
			listener.onDealHttpError(HttpTaskListener.ERROR_URI_format_error, this);
//			Log.v(TAG, "ERROR_URI_format_error:" + uri);
			return;
		}
		while (tryCounts < MAX_TRY_COUNT) {
			try {
				httpClient = createHttpClient();

				if (bNeedStop) {
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
					TLog.e(TAG, "httpSend " + Thread.currentThread().getName() + "URL:" + httpPost.getURI());
					response = httpClient.execute(httpPost, localContext);
				} else {
					TLog.e(TAG, "httpSend " + Thread.currentThread().getName() + "URL:" + httpGet.getURI());
					response = httpClient.execute(httpGet, localContext);
				}

				if (bNeedStop) {
//					httpClient.getConnectionManager().shutdown();
//					httpClient = null;
					return;
				}

				status = response.getStatusLine().getStatusCode();

//				Date date = new Date(System.currentTimeMillis());
//				TLog.e(TAG, "httpRes " + Thread.currentThread().getName() + " StatusCode:" + status + " " + date.toLocaleString());

				// 如果返回码不是200,就直接给出错误信息
				if ((status != HttpStatus.SC_OK) && (status != HttpStatus.SC_PARTIAL_CONTENT)) {
					if (bPost) {
						httpPost.abort();
					} else {
						httpGet.abort();
					}
				
					if (listener != null) {
						listener.onDealHttpError(status, this);
					}
					return;
				}

				if (bNeedStop) {
					return;
				}

				if (listener != null) {
					listener.onReceiveResponseData(response, this);
				}

				// 正常结束,
				return;

			}
			catch (DecodeException e)
			{

				e.printStackTrace();
				if (listener != null && tryCounts >= MAX_TRY_COUNT - 1) {
					listener.onDealHttpError(HttpTaskListener.ERROR_Decode, this);
				}
			}
			catch (Throwable e) {

				e.printStackTrace();
				if (listener != null && tryCounts >= MAX_TRY_COUNT - 1) {
					listener.onDealHttpError(HttpTaskListener.ERROR_Throwable, this);
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
			tryCounts++;
		}
		return;

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
//		TLog.v("Network","Network" + TargetData.NETWORK);
//		if (TargetData.NETWORK == NET_TYPE._CMWAP || TargetData.NETWORK == NET_TYPE.__3GWAP || TargetData.NETWORK == NET_TYPE._UNIWAP) {
//			proxy = new HttpHost( "10.0.0.172", 80, "http");
//			httpClient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);
//		}

		return httpClient;
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
