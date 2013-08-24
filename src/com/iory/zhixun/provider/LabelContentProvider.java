package com.iory.zhixun.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

public class LabelContentProvider extends ContentProvider {

	public static final Uri CONTENT_URI = Uri.parse("content://com.iory.zhixun");
	public static final Uri CONTENT_URI_ITEM = Uri.parse("content://com.iory.zhixun/#");
	public static final String AUTHORITY = "com.iory.zhixun";

	public static final int ALL_LABELS = 1;
	public static final int SINGLE_LABELS = 2;

	public static final String TEMPLATES_TYPE = "vnd.android.cursor.dir/labels";
	public static final String TEMPLATES_ITEM_TYPE = "vnd.android.cursor.item/label";

	private NewsSqliteHelper mDbHelper;

	static final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
	static {
		matcher.addURI(AUTHORITY, null, ALL_LABELS);
		matcher.addURI(AUTHORITY, "/#", SINGLE_LABELS);
	}

	@Override
	public String getType(Uri uri) {
		int match = matcher.match(uri);

		switch (match) {
		case ALL_LABELS:
			return TEMPLATES_TYPE;
		case SINGLE_LABELS:
			return TEMPLATES_ITEM_TYPE;
		}

		return null;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		SQLiteDatabase db = mDbHelper.getWritableDatabase();
		long rowId = 0;
		switch (matcher.match(uri)) {
		case ALL_LABELS:
			rowId = db.insert(NewsSqliteHelper.TABLE_NEWS, "", values);
		}
		if (rowId > 0) {
			Uri returnUri = Uri.parse("content://" + AUTHORITY + "/" + rowId);
			return returnUri;
		} else {
			return null;
		}
	}

	@Override
	public boolean onCreate() {
		mDbHelper = new NewsSqliteHelper(this.getContext());
		if (mDbHelper == null)
			return false;
		else{
			return true;
		}
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		SQLiteQueryBuilder builder = new SQLiteQueryBuilder();

		builder.setTables(NewsSqliteHelper.TABLE_NEWS);

		Cursor result = null;
		int match = matcher.match(uri);

		switch (match) {
		case ALL_LABELS:
			result = builder.query(mDbHelper.getReadableDatabase(), projection,
					selection, selectionArgs, null, null, sortOrder);
			break;
		}

		if (result != null)
			result.setNotificationUri(getContext().getContentResolver(), uri);

		return result;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		SQLiteDatabase db = mDbHelper.getWritableDatabase();
		int rowId = 0;
		rowId = db.update(NewsSqliteHelper.TABLE_NEWS, values, selection,
				selectionArgs);
		return rowId;
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		SQLiteDatabase db = mDbHelper.getWritableDatabase();
		int rowId = 0;
		rowId = db.delete(NewsSqliteHelper.TABLE_NEWS, selection, selectionArgs);
		return rowId;
	}
	/**
	 * @return the mDbHelper
	 */
	public NewsSqliteHelper getmDbHelper() {
		return mDbHelper;
	}

}
