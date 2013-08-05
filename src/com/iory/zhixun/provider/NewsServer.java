package com.iory.zhixun.provider;


import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.iory.zhixun.data.NewsItem;

public class NewsServer {

	private Context mContext;

	public NewsServer(Context context) {
		this.mContext = NewsSqliteHelper.mcontext;
	}



	/**
	 * 插入新闻列表
	 *
	 * @param
	 */
	private void addNewsList(List<NewsItem> newsItemList) {
        ContentValues contentValues = null ;
		for (int i = 0; i < newsItemList.size(); ++i) {
            contentValues = new ContentValues();
            contentValues.put(NewsSqliteHelper.TABLE_NEWS_FEILD_TITLE, newsItemList.get(i).title);
            contentValues.put(NewsSqliteHelper.TABLE_NEWS_FEILD_CONTENT, newsItemList.get(i).detail);
            contentValues.put(NewsSqliteHelper.TABLE_NEWS_FEILD_FROM,newsItemList.get(i).mNewsFrom);
            contentValues.put(NewsSqliteHelper.TABLE_NEWS_FEILD_KEY, newsItemList.get(i).mNewsKey);
            contentValues.put(NewsSqliteHelper.TABLE_NEWS_FEILD_FAVORITE, newsItemList.get(i).mIsFav);
            contentValues.put(NewsSqliteHelper.TABLE_NEWS_FEILD_IMAGE_URL, newsItemList.get(i).mImageUrl);
            contentValues.put(NewsSqliteHelper.TABLE_NEWS_FEILD_VEDIO_URL, newsItemList.get(i).mVedioUrl);
            contentValues.put(NewsSqliteHelper.TABLE_NEWS_FEILD_NEWS_TYPE, newsItemList.get(i).mNewsKey);
            contentValues.put(NewsSqliteHelper.TABLE_NEWS_FEILD_TYPE, newsItemList.get(i).mType);
            contentValues.put(NewsSqliteHelper.TABLE_NEWS_FEILD_TIME, newsItemList.get(i).mNewsTime);
            mContext.getContentResolver().insert(NewsContentProvider.CONTENT_URI, contentValues);
        }
    }


	//更新卡bin

	public synchronized void updateCBin(List<NewsItem> mNews) {
	}

	/**
	 * 删除新闻列表
	 *
	 * @param
	 */
	private void delAllNews() {
		mContext.getContentResolver().delete(NewsContentProvider.CONTENT_URI, NewsSqliteHelper.TABLE_NEWS_FEILD_ID + " > ? ", new String[]{"0"} );
	}

	/**
	 * query CBin by key
	 *
	 * @return CBinInfo null if not exit
	 */
	public List<NewsItem> getAllListNewsByType(String newsType) {
		Cursor cursor = mContext.getContentResolver().query(
				NewsContentProvider.CONTENT_URI, null,
				" " + NewsSqliteHelper.TABLE_NEWS_FEILD_TYPE + " = " + newsType,
				null, null);
        List<NewsItem> newsItems = new ArrayList<NewsItem>();
        NewsItem newsItem ;
        if (cursor != null ){
            while (cursor.moveToNext()){
                newsItem = new NewsItem();
                newsItem.id = cursor.getString(cursor
                        .getColumnIndex(NewsSqliteHelper.TABLE_NEWS_FEILD_ID));
                newsItem.title = cursor.getString(cursor
                        .getColumnIndex(NewsSqliteHelper.TABLE_NEWS_FEILD_TITLE));
                newsItem.detail = cursor.getString(cursor
                        .getColumnIndex(NewsSqliteHelper.TABLE_NEWS_FEILD_CONTENT));
                newsItem.mImageUrl = cursor.getString(cursor
                        .getColumnIndex(NewsSqliteHelper.TABLE_NEWS_FEILD_IMAGE_URL));
                newsItem.mIsFav = cursor.getString(cursor
                        .getColumnIndex(NewsSqliteHelper.TABLE_NEWS_TYPES_FIELD_IS_FAV));
                newsItem.mNewsFrom = cursor.getString(cursor
                        .getColumnIndex(NewsSqliteHelper.TABLE_NEWS_FEILD_FROM));
                newsItem.mNewsKey = cursor.getString(cursor
                        .getColumnIndex(NewsSqliteHelper.TABLE_NEWS_FEILD_KEY));
                newsItem.mNewsTime = cursor.getString(cursor
                        .getColumnIndex(NewsSqliteHelper.TABLE_NEWS_FEILD_TIME));
                newsItem.mNewsType = cursor.getString(cursor
                        .getColumnIndex(NewsSqliteHelper.TABLE_NEWS_FEILD_NEWS_TYPE));
                newsItem.mVedioUrl = cursor.getString(cursor
                        .getColumnIndex(NewsSqliteHelper.TABLE_NEWS_FEILD_VEDIO_URL));
                newsItems.add(newsItem);
            }
        }

        if(cursor != null){
            cursor.close();
        }
		return newsItems;
	}

}
