package com.iory.zhixun.net;

import java.util.HashMap;

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
	
	public static final int ERROR_SocketException = 1;
	public static final int ERROR_SocketTimeoutException = 2;
	public static final int ERROR_IOException = 3;
	public static final int ERROR_ClientProtocolException = 4;
	public static final int ERROR_Throwable = 5;
	public static final int ERROR_URI_format_error = 6;
	public static final int ERROR_ClassCastException = 7;
	public static final int ERROR_Decode = 10;
	//以下CMD仅供客户端使用，用来，非协议定制
	
	//beta1协议
	public static final int CMD_handshake = 101;				//握手
	public static final int CMD_getCategorys = 102;				//分类列表
//	public static final int CMD_getSoftPageByRank = 103;		//某个分类排序后的软件列表
	public static final int CMD_getSoftDetail = 104;			//软件详情
	public static final int CMD_listComment = 105;				//评论列表
	public static final int CMD_addCommentAndScore = 106;		//评论评分
	public static final int CMD_checkUpdate = 107;				//检查更新
//	public static final int CMD_reportSoft = 108;				//软件使用信息上报
	public static final int CMD_reportMobile = 109; 			//手机信息上报
//	public static final int CMD_getIconBytes = 110;				//批量请求ICON
	public static final int CMD_getTopicList = 111;				//专题列表
	public static final int CMD_getTopic = 112;					//专题详情
	public static final int CMD_checkUpdateSeft = 113;				//检查自更新
	public static final int CMD_checkUpdateForPkgNameSearch = 114;	//检查更新协议做包名搜索使用
	
	//beta2 个人中心协议
	public static final int CMD_shareSoft = 213;             	 //分享
	public static final int CMD_getShare = 214;             	 //获取分享
	public static final int CMD_addFavoriten = 215;         	 //新建收藏夹
	public static final int CMD_DelFavoriten = 216;           	 //删除收藏夹
	public static final int CMD_editFavoriten = 217;             //编辑收藏夹
	public static final int CMD_scoreFavoriten = 218;            //给收藏夹打分
	public static final int CMD_addFavoritenSoft = 219;        //收藏软件
	public static final int CMD_delFavoritenSoft = 220;      //删除收藏软件
	public static final int CMD_listFavoriten = 221;             //获取收藏夹
	public static final int CMD_listFavoritenSoft = 222;     	 //获取收藏夹详情
//	public static final int CMD_addLabel = 223;                  //增加标签
//	public static final int CMD_labelSearch = 224;             //根据标签查找软件
	
	//beta2 栏目运营
//	public static final int CMD_getStablePicAdv = 300;            //获取固定广告位
	public static final int CMD_getScrollablePicAdv = 301;        //获取滑动广告位
	public static final int CMD_getFlashScreen = 302;             //获取闪屏
//	public static final int CMD_getLoadingText = 303;             //获取loading文本
//	public static final int CMD_getSoftwaresLatest = 304;         //获取新品上架Latest
	public static final int CMD_getSoftwaresOnTopByScroe = 305;   //获取新品上架OnTop评分榜
//	public static final int CMD_getSoftwaresOnTopByDownloadCount = 306;   //获取新品上架OnTop下载榜
//	public static final int CMD_getHotSoftwares = 307;            //获取热门软件列表
//	public static final int CMD_getUserCommends = 308;            //获取玩家推荐
	public static final int CMD_guessIt = 309;             		  //让你猜猜	
	
	//获取头像
	public static final int CMD_getUserInfo = 400;             		//获取用户信息	
//	public static final int CMD_getUserInfoBatch = 401;             //批量获取用户信息	
	
	//详情界面的获取相关软件
	public static final int CMD_getRelatedSoftwares = 501;             //获取相关软件	
	//详情界面的举报
	public static final int CMD_report = 502;             //详情界面的举报	

	//获取本地管理列表的后台软件收录情况
	public static final int CMD_getSoftwaresInAppCenter = 600;             

	//统计数据上报
	public static final int CMD_reportStatData = 700;
	
	// 分类广告位统计
	public static final int CMD_reportB4AdvStatData = 701;
	
	//统计软件下载情况
	public static final int CMD_reportDownSoft = 702;
	
	//获取每日精选
	public static final int CMD_getDayRecommend = 800; 
	
	//获取分类的装机必备
	public static final int CMD_requiredSoftwares = 900; 

	//获取分类的游戏
	public static final int CMD_getGameSoftwares = 901;

	public static final int CMD_delAllShare = 902;
	
	//获取独家首发软件
	public static final int CMD_randomFirstRelease = 1000;
	
	//获取用户0--31状态位的状态
	public static final int CMD_getUserStatus = 1001;
	//设置用户0--31状态位的状态
	public static final int CMD_setUserStatus = 1002;
//   //获取热词
//	public static final int CMD_getHotwords = 1003;
	
	//widget
	public static final int CMD_getWidgetSoftwareList = 1004;
	
	// 搜索关键字
	public static final int CMD_getSoftByKeyWork = 1005;
	
	// 获取分类软件
	public static final int CMD_getSortSoftWare = 1006;
	
	//好友列表
	public static final int CMD_listFriend = 1100;
	//获取广告平台广告
	public static final int CMD_getAdvert = 1101;
	
	//获取单个好友的应用中心feed
	public static final int CMD_getFeed = 1200;	
	
//	//获取我的好友的应用中心feed
//	public static final int CMD_friendFeed = 1201;	
	
	//获取我的好友的应用中心feed 1.3.4
	public static final int CMD_getFriendsFeed = 1202;	
	//获取配置
	public static final int CMD_getConfig = 1300;
	
	//获得机型相近的最近最多下载软件列表
	public static final int CMD_modelSoftwares = 1400;
	
	//顶、踩评论
	public static final int CMD_agreeComment = 1500;

	//后置渠道的统计上报
	public static final int CMD_reportPromotionLog = 1600;
	
//	获取活动弹窗
	public static final int CMD_getTips = 1700;
	
//	获取是否允许该账号进入内测版本
	public static final int CMD_checkInnerTest = 1800;
	
	public static final int CMD_checkNetwork = 1900;
	
	//统计页面停留时间
	public static final int CMD_reportViewBehaviour = 2000;
	//统计上报用户的客户端设置
	public static final int CMD_reportClientSettings = 2001;
	
	//大厅
	public static final int CMD_getHallFeeds = 2002;
	
	//专题顶踩
	public static final int CMD_agreeTopic = 2100;
	
	//联想搜索
	public static final int CMD_SuggestSearch = 2200;
	
	//获取不同类型提示语
	public static final int CMD_ListLoadingText = 2300;
	
	//获取不同类型提示语
	public static final int CMD_rankHotwords = 2400;
	
	//编辑评论(置顶，删除)
	public static final int CMD_editComment = 2500;

	
	//下载图标和apk，非前后台协议
	public static final int CMD_DownloadIcon = 10000;
	public static final int CMD_DownloadApk = 10001;
	
	
	void onReceiveResponseData(int cmdType, HashMap<String, Integer> funName2cmdTypeMap, HttpResponse response, HttpTask httpTask) throws Exception;
	
	void onDealHttpError(int cmdType, HashMap<String, Integer> funName2cmdTypeMap, int errorCode, String errorStr, HttpTask httpTask);

}
