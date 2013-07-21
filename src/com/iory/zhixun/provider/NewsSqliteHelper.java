package com.iory.zhixun.provider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

class NewsSqliteHelper extends SQLiteOpenHelper {
	private static NewsSqliteHelper mdbHelper;

	public static final String DATABASE_NAME = "zhixun";
    // 新闻表
	public static final String TABLE_NEWS = "news";
    // 新闻id
    public static final String TABLE_NEWS_FEILD_ID = "_id";
    // 新闻标题
    public static final String TABLE_NEWS_FEILD_TITLE = "title";
    // 新闻内容
    public static final String TABLE_NEWS_FEILD_CONTENT = "content";
    // 新闻时间
    public static final String TABLE_NEWS_FEILD_TIME = "time";
    // 新闻来源
    public static final String TABLE_NEWS_FEILD_FROM = "from";
    //新闻的类型，图片，视频
    public static final String TABLE_NEWS_FEILD_NEWS_TYPE = "news_type";
    //新闻是否被收藏
    public static final String TABLE_NEWS_FEILD_FAVORITE = "favorite";
    //新闻的图片地址
    public static final String TABLE_NEWS_FEILD_IMAGE_URL = "image_url";
    // 新闻视频地址
    public static final String TABLE_NEWS_FEILD_VEDIO_URL = "vedio_url";
    // 新闻类型，是头条，娱乐，科技，财经等
    public static final String TABLE_NEWS_FEILD_TYPE = "type";
    // 新闻关键字
    public static final String TABLE_NEWS_FEILD_KEY = "key";

    // 用户订阅的新闻类型
    public static final String TABLE_NEWS_TYPES = "types";
    public static final String TABLE_NEWS_TYPES_FIELD_ID = "_id";
    public static final String TABLE_NEWS_TYPES_FIELD_NAME = "name";
    public static final String TABLE_NEWS_TYPES_FIELD_VALUE = "value";
    public static final String TABLE_NEWS_TYPES_FIELD_IS_FAV = "is_fav";

	public static final int DATABASE_VERSION = 1;
	public static Context mcontext;

	NewsSqliteHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		mcontext = context;
	}
	
	public static NewsSqliteHelper getInstance(){
		if(mdbHelper == null){
			mdbHelper = new NewsSqliteHelper(mcontext);
		}
		return mdbHelper;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_NEWS + " ("
                + NewsSqliteHelper.TABLE_NEWS_FEILD_TITLE + " TEXT,"
                + NewsSqliteHelper.TABLE_NEWS_FEILD_CONTENT + " TEXT,"
                + NewsSqliteHelper.TABLE_NEWS_FEILD_TIME + " TEXT,"
                + NewsSqliteHelper.TABLE_NEWS_FEILD_FROM + " TEXT,"
                + NewsSqliteHelper.TABLE_NEWS_FEILD_NEWS_TYPE + " TEXT,"
                + NewsSqliteHelper.TABLE_NEWS_FEILD_FAVORITE + " TEXT,"
                + NewsSqliteHelper.TABLE_NEWS_FEILD_IMAGE_URL + " TEXT,"
                + NewsSqliteHelper.TABLE_NEWS_FEILD_VEDIO_URL + " TEXT,"
                + NewsSqliteHelper.TABLE_NEWS_FEILD_KEY + " TEXT,"
                + NewsSqliteHelper.TABLE_NEWS_FEILD_TYPE + " TEXT,"
                + NewsSqliteHelper.TABLE_NEWS_FEILD_ID
                + " INTEGER PRIMARY KEY AUTOINCREMENT );");

        db.execSQL("CREATE TABLE " + TABLE_NEWS_TYPES + " ("
                + NewsSqliteHelper.TABLE_NEWS_TYPES_FIELD_NAME + " TEXT,"
                + NewsSqliteHelper.TABLE_NEWS_TYPES_FIELD_VALUE + " TEXT,"
                + NewsSqliteHelper.TABLE_NEWS_TYPES_FIELD_IS_FAV + " TEXT,"
                + NewsSqliteHelper.TABLE_NEWS_TYPES_FIELD_ID
                + " INTEGER PRIMARY KEY AUTOINCREMENT );");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// db.execSQL("DROP TABLE IF EXISTS " + DATABASE_NAME);
		// onCreate(db);
	}

}
