package com.iory.zhixun.provider;


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


//
//	/**
//	 * insert CBin
//	 *
//	 * @param
//	 */
//	private void addCBin(List<NewsItem> newsItemList) {
//		for (int i = 0; i < newsItemList.size(); ++i) {
//			ContentValues mycont = new ContentValues();
//			if(!isNum((cBin.get(i)).mCbin))continue;
//			mycont.put(NewsSqliteHelper.TABLE_CARD_CBIN, (cBin.get(i)).mCbin.trim());
//			mycont.put(NewsSqliteHelper.TABLE_CARD_NAME_BANK, (cBin.get(i)).mBname.trim());
//			mycont.put(NewsSqliteHelper.TABLE_CARD_LOG_BANK, (cBin.get(i)).mBlog.trim());
//			mycont.put(NewsSqliteHelper.TABLE_CARD_PHONE_BANK, (cBin.get(i)).mBphone.trim());
//			mycont.put(NewsSqliteHelper.TABLE_CARD_NAME_CARD, (cBin.get(i)).mCname.trim());
//			mycont.put(NewsSqliteHelper.TABLE_CARD_TYPE_CARD, (cBin.get(i)).mCtype.trim());
//			if(!isNum(cBin.get(i).mCnumlength)) cBin.get(i).mCnumlength = "19";
//			mycont.put(NewsSqliteHelper.TABLE_CARD_LENGTH_CARDNUM,
//					(cBin.get(i)).mCnumlength.trim());
//			mycont.put(NewsSqliteHelper.TABLE_CARD_IS_QUERY_MONEY,
//					(cBin.get(i)).mIsQmoney.trim());
//			mycont.put(NewsSqliteHelper.TABLE_CARD_IS_QUNCUN_MONEY,
//					(cBin.get(i)).mIsQuncun.trim());
//			if(!isNum((cBin.get(i)).mMinCount)) (cBin.get(i)).mMinCount = "0";
//			mycont.put(NewsSqliteHelper.TABLE_CARD_MIN_MONEY, (cBin.get(i)).mMinCount.trim());
//			if(!isNum((cBin.get(i)).mMaxCount)) (cBin.get(i)).mMaxCount = "100000";
//			mycont.put(NewsSqliteHelper.TABLE_CARD_MAX_MONEY, (cBin.get(i)).mMaxCount.trim());
//			mContext.getContentResolver().insert(
//					NewsContentProvider.CONTENT_URI, mycont);
//		}
//	}
//
//
//	//更新卡bin
//
//	public synchronized void updateCBin(Vector<CBinInfo> cBin, int versionCbin) {
//		if (cBin.size() < 1) return;
//		delAllCbin();
//		addCBin(cBin);
//		NewsSqliteHelper.getInstance().setVersion(versionCbin);
//	}
//
//	/**
//	 * del all CBin
//	 *
//	 * @param
//	 */
//	private void delAllCbin() {
//		mContext.getContentResolver().delete(
//				NewsContentProvider.CONTENT_URI, null, null);
//	}
//
//	/**
//	 * query CBin by key
//	 *
//	 * @return CBinInfo null if not exit
//	 */
//	public CBinInfo getCBinByKey(String key_cbin) {
//		if(!isNum(key_cbin)) return null;
//		CBinInfo cbinfo = null;
//		Cursor cursor = mContext.getContentResolver().query(
//				NewsContentProvider.CONTENT_URI, null,
//				" " + NewsSqliteHelper.TABLE_CARD_CBIN + " = " + key_cbin,
//				null, null);
//		if((cursor != null) && (cursor.getCount() > 0)){
//			cursor.moveToFirst();
//			cbinfo = new CBinInfo();
//			cbinfo.mCbin = cursor.getString(cursor
//					.getColumnIndex(NewsSqliteHelper.TABLE_CARD_CBIN));
//			cbinfo.mBname = cursor.getString(cursor
//					.getColumnIndex(NewsSqliteHelper.TABLE_CARD_NAME_BANK));
//			cbinfo.mBlog = cursor.getString(cursor
//					.getColumnIndex(NewsSqliteHelper.TABLE_CARD_LOG_BANK));
//			cbinfo.mBphone = cursor.getString(cursor
//					.getColumnIndex(NewsSqliteHelper.TABLE_CARD_PHONE_BANK));
//			cbinfo.mCname = cursor.getString(cursor
//					.getColumnIndex(NewsSqliteHelper.TABLE_CARD_NAME_CARD));
//			cbinfo.mCtype = cursor.getString(cursor
//					.getColumnIndex(NewsSqliteHelper.TABLE_CARD_TYPE_CARD));
//			cbinfo.mCnumlength = cursor.getString(cursor
//					.getColumnIndex(NewsSqliteHelper.TABLE_CARD_LENGTH_CARDNUM));
//			cbinfo.mIsQmoney = cursor.getString(cursor
//					.getColumnIndex(NewsSqliteHelper.TABLE_CARD_IS_QUERY_MONEY));
//			cbinfo.mIsQuncun = cursor.getString(cursor
//					.getColumnIndex(NewsSqliteHelper.TABLE_CARD_IS_QUNCUN_MONEY));
//			cbinfo.mMinCount = cursor.getString(cursor
//					.getColumnIndex(NewsSqliteHelper.TABLE_CARD_MIN_MONEY));
//			cbinfo.mMaxCount = cursor.getString(cursor
//					.getColumnIndex(NewsSqliteHelper.TABLE_CARD_MAX_MONEY));
//			cursor.close();
//		}
//		return cbinfo;
//	}
//
//	/**
//	 * 判断某个卡bin是否存在;
//	 *
//	 * @param key_cbin
//	 * @return
//	 */
//	public boolean existCount(String key_cbin) {
//		if(!isNum(key_cbin)) return false;
//		Cursor cursor = mContext.getContentResolver().query(
//				NewsContentProvider.CONTENT_URI, null,
//				" " + NewsSqliteHelper.TABLE_CARD_CBIN + " = " + key_cbin,
//				null, null);
//		if((cursor != null) && (cursor.getCount() > 0)){
//			cursor.close();
//			return true;
//		}else{
//			return false;
//		}
//	}

//	private static boolean isNum(String str) {
//		Pattern p = Pattern.compile("^[\\s]*[0-9]+[\\s]*$");
//		Matcher m = p.matcher(str);
//		return m.matches();
//	}

}
