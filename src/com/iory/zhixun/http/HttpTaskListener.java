package com.iory.zhixun.http;


import org.apache.http.HttpResponse;

/**
 *
 * @author dragonlin
 * @date 2011-3-10	
 * @version 1.0
 * 
 * 
 * @author dragonlin 添加个人中心协议
 * @date 2011-3-24
 * @version 2.0 
 * 
 * 
 * @author dragonlin 添加栏目运营协议
 * @date 2011-4-22
 * @version 3.0 
 * 
 * @author dragonlin 首页软件排行添加请求字段
 * @date 2011-5-1
 * @version 3.1
 * 
 * 1.添加获取用户信息协议（头像，昵称），
 * 2.详情界面的获取相关软件，举报  
 * 3.固件列表获取
 * @author dragonlin 
 * @date 2011-5-9
 * @version 3.2
 * 
 * 1.添加获取本地管理列表的后台软件收录情况
 * @author dragonlin 
 * @date 2011-5-13
 * @version 3.3
 * 
 * 添加统计上报
 * @author dragonlin 
 * @date 2011-5-17
 * @version 3.4
 * 
 */
public interface HttpTaskListener {
	
	public static final int ERROR_SocketException = 0x7f001;
	public static final int ERROR_SocketTimeoutException = 0x7f002;
	public static final int ERROR_IOException = 0x7f003;
	public static final int ERROR_ClientProtocolException = 0x7f004;
	public static final int ERROR_Throwable = 0x7f010;
	public static final int ERROR_URI_format_error = 0x7f020;
	public static final int ERROR_Decode = 0x7f030;
	//以下CMD仅供客户端使用，用来，非协议定制
	
	/**正常接收数据*/
	void onReceiveResponseData(HttpResponse response, HttpTask httpTask) throws Exception;
	
	/**抛了异常处理*/
	void onDealHttpError(int errorCode, HttpTask httpTask);

//	/**完成连接*/
//	void onHttpDone(HttpTask httpTask);
	
}
