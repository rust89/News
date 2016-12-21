package ru.sike.lada.providers;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import ru.sike.lada.database.DataContract;
import ru.sike.lada.database.DbHelper;

public class LadaContent extends ContentProvider {

    private static final String NEWS_CATEGORIES_STR = "news_categories";
    private final int NEWS_CATEGORIES = 1;
    private final int NEWS_CATEGORIES_ID = 2;

    private static final String NEWS_STR = "news";
    private final int NEWS = 3;
    private final int NEWS_ID = 4;

    private static final String NEWS_CATEGORY_BINDER_STR = "news_category_binder";
    private final int NEWS_CATEGORY_BINDER = 5;
    private final int NEWS_CATEGORY_BINDER_ID = 6;

    private static final String NEWS_BOOKMARKS_STR = "news_bookmarks";
    private final int NEWS_BOOKMARKS = 7;
    private final int NEWS_BOOKMARKS_ID = 8;

    public static final String AUTHORITIES = "ru.sike.lada.content";
    public static final String NEWS_CATEGORY_URI_SCHEMA = AUTHORITIES + "/" + NEWS_CATEGORIES_STR;
    public static final String NEWS_URI_SCHEMA = AUTHORITIES + "/" + NEWS_STR;
    public static final String NEWS_CATEGORY_BINDER_URI_SCHEMA = AUTHORITIES + "/" + NEWS_CATEGORY_BINDER_STR;
    public static final String NEWS_BOOKMARKS_URI_SCHEMA = AUTHORITIES + "/" + NEWS_BOOKMARKS_STR;

    private SQLiteDatabase mDb;
    private UriMatcher mUriMatcher;

    public LadaContent() {
        mUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    }

    @Override
    public boolean onCreate() {
        mUriMatcher.addURI(AUTHORITIES, NEWS_CATEGORIES_STR, NEWS_CATEGORIES);
        mUriMatcher.addURI(AUTHORITIES, NEWS_CATEGORIES_STR + "/*", NEWS_CATEGORIES_ID);
        mUriMatcher.addURI(AUTHORITIES, NEWS_STR, NEWS);
        mUriMatcher.addURI(AUTHORITIES, NEWS_STR + "/*", NEWS_ID);
        mUriMatcher.addURI(AUTHORITIES, NEWS_CATEGORY_BINDER_STR, NEWS_CATEGORY_BINDER);
        mUriMatcher.addURI(AUTHORITIES, NEWS_CATEGORY_BINDER_STR + "/*", NEWS_CATEGORY_BINDER_ID);
        mUriMatcher.addURI(AUTHORITIES, NEWS_BOOKMARKS_STR, NEWS_BOOKMARKS);
        mUriMatcher.addURI(AUTHORITIES, NEWS_BOOKMARKS_STR + "/*", NEWS_BOOKMARKS_ID);

        DbHelper dbHelper = new DbHelper(getContext());
        mDb = dbHelper.getWritableDatabase();
        return mDb != null;
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        String table;
        switch (mUriMatcher.match(uri)) {
            case NEWS_CATEGORIES: {
                table = DataContract.NewsCategories.TABLE_NAME;
                break;
            }
            case NEWS_CATEGORIES_ID: {
                table = DataContract.NewsCategories.TABLE_NAME;
                String removeCondition = "(" + DataContract.NewsCategories._ID + " = " + uri.getLastPathSegment() + ")";
                if (TextUtils.isEmpty(selection))
                    selection = removeCondition;
                else
                    selection = "(" + selection + ")" + " AND " + removeCondition;
                break;
            }
            case NEWS: {
                table = DataContract.News.TABLE_NAME;
                break;
            }
            case NEWS_ID: {
                table = DataContract.News.TABLE_NAME;
                String removeCondition = "(" + DataContract.News._ID + " = " + uri.getLastPathSegment() + ")";
                if (TextUtils.isEmpty(selection))
                    selection = removeCondition;
                else
                    selection = "(" + selection + ")" + " AND " + removeCondition;
                break;
            }
            case NEWS_CATEGORY_BINDER: {
                table = DataContract.NewsCategoryBinder.TABLE_NAME;
                break;
            }
            case NEWS_CATEGORY_BINDER_ID: {
                table = DataContract.NewsCategoryBinder.TABLE_NAME;
                String removeCondition = "(" + DataContract.NewsCategoryBinder._ID + " = " + uri.getLastPathSegment() + ")";
                if (TextUtils.isEmpty(selection))
                    selection = removeCondition;
                else
                    selection = "(" + selection + ")" + " AND " + removeCondition;
                break;
            }
            case NEWS_BOOKMARKS: {
                table = DataContract.NewsBookmarks.TABLE_NAME;
                break;
            }
            case NEWS_BOOKMARKS_ID: {
                table = DataContract.NewsBookmarks.TABLE_NAME;
                String removeCondition = "(" + DataContract.NewsBookmarks._ID + " = " + uri.getLastPathSegment() + ")";
                if (TextUtils.isEmpty(selection))
                    selection = removeCondition;
                else
                    selection = "(" + selection + ")" + " AND " + removeCondition;
                break;
            }
            default: {
                throw new IllegalArgumentException("Unknown URI " + uri);
            }
        }

        int cnt = mDb.delete(table, selection, selectionArgs);
        Context context = getContext();
        if (context != null) {
            ContentResolver contentResolver = context.getContentResolver();
            contentResolver.notifyChange(uri, null);
        }
        return cnt;
    }

    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        String table;
        switch (mUriMatcher.match(uri)) {
            case NEWS_CATEGORIES: {
                table = DataContract.NewsCategories.TABLE_NAME;
                break;
            }
            case NEWS: {
                table = DataContract.News.TABLE_NAME;
                break;
            }
            case NEWS_CATEGORY_BINDER: {
                table = DataContract.NewsCategoryBinder.TABLE_NAME;
                break;
            }
            case NEWS_BOOKMARKS: {
                table = DataContract.NewsBookmarks.TABLE_NAME;
                break;
            }
            default: {
                throw new IllegalArgumentException("Unknown URI " + uri);
            }
        }

        long rowID = mDb.insert(table, null, values);
        Uri resultUri = ContentUris.withAppendedId(uri, rowID);
        Context context = getContext();
        if (context != null) {
            ContentResolver contentResolver = context.getContentResolver();
            contentResolver.notifyChange(resultUri, null);
        }
        return resultUri;
    }

    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        String table;
        switch (mUriMatcher.match(uri)) {
            case NEWS_CATEGORIES: {
                table = DataContract.NewsCategories.TABLE_NAME;
                break;
            }
            case NEWS_CATEGORIES_ID: {
                table = DataContract.NewsCategories.TABLE_NAME;

                String queryCondition = "(" + DataContract.NewsCategories._ID + " = " + uri.getLastPathSegment() + ")";
                if (TextUtils.isEmpty(selection))
                    selection = queryCondition;
                else
                    selection = "(" + selection + ")" + " AND " + queryCondition;
                break;
            }
            case NEWS: {
                table = DataContract.News.TABLE_NAME;
                break;
            }
            case NEWS_ID: {
                table = DataContract.News.TABLE_NAME;

                String queryCondition = "(" + DataContract.News._ID + " = " + uri.getLastPathSegment() + ")";
                if (TextUtils.isEmpty(selection))
                    selection = queryCondition;
                else
                    selection = "(" + selection + ")" + " AND " + queryCondition;
                break;
            }
            case NEWS_CATEGORY_BINDER: {
                table = DataContract.NewsCategoryBinder.TABLE_NAME;
                break;
            }
            case NEWS_CATEGORY_BINDER_ID: {
                table = DataContract.NewsCategoryBinder.TABLE_NAME;
                String queryCondition = "(" + DataContract.NewsCategoryBinder._ID + " = " + uri.getLastPathSegment() + ")";
                if (TextUtils.isEmpty(selection))
                    selection = queryCondition;
                else
                    selection = "(" + selection + ")" + " AND " + queryCondition;
                break;
            }
            case NEWS_BOOKMARKS: {
                table = DataContract.NewsBookmarks.TABLE_NAME;
                break;
            }
            case NEWS_BOOKMARKS_ID: {
                table = DataContract.NewsBookmarks.TABLE_NAME;
                String queryCondition = "(" + DataContract.NewsBookmarks._ID + " = " + uri.getLastPathSegment() + ")";
                if (TextUtils.isEmpty(selection))
                    selection = queryCondition;
                else
                    selection = "(" + selection + ")" + " AND " + queryCondition;
                break;
            }
            default: {
                throw new IllegalArgumentException("Unknown URI " + uri);
            }
        }

        Cursor cursor = mDb.query(table, projection, selection, selectionArgs, null, null, sortOrder);
        Context context = getContext();
        if (context != null) {
            ContentResolver contentResolver = context.getContentResolver();
            cursor.setNotificationUri(contentResolver, uri);
        }
        return cursor;
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        String table;
        switch (mUriMatcher.match(uri)) {
            case NEWS_CATEGORIES: {
                table = DataContract.NewsCategories.TABLE_NAME;
                break;
            }
            case NEWS_CATEGORIES_ID: {
                table = DataContract.NewsCategories.TABLE_NAME;
                String updateCondition = "(" + DataContract.NewsCategories._ID + " = " + uri.getLastPathSegment() + ")";
                if (TextUtils.isEmpty(selection))
                    selection = updateCondition;
                else
                    selection = "(" + selection + ")" + " AND " + updateCondition;
                break;
            }
            case NEWS: {
                table = DataContract.News.TABLE_NAME;
                break;
            }
            case NEWS_ID: {
                table = DataContract.News.TABLE_NAME;
                String updateCondition = "(" + DataContract.News._ID + " = " + uri.getLastPathSegment() + ")";
                if (TextUtils.isEmpty(selection))
                    selection = updateCondition;
                else
                    selection = "(" + selection + ")" + " AND " + updateCondition;
                break;
            }
            case NEWS_CATEGORY_BINDER: {
                table = DataContract.NewsCategoryBinder.TABLE_NAME;
                break;
            }
            case NEWS_CATEGORY_BINDER_ID: {
                table = DataContract.NewsCategoryBinder.TABLE_NAME;
                String updateCondition = "(" + DataContract.NewsCategoryBinder._ID + " = " + uri.getLastPathSegment() + ")";
                if (TextUtils.isEmpty(selection))
                    selection = updateCondition;
                else
                    selection = "(" + selection + ")" + " AND " + updateCondition;
                break;
            }
            case NEWS_BOOKMARKS: {
                table = DataContract.NewsBookmarks.TABLE_NAME;
                break;
            }
            case NEWS_BOOKMARKS_ID: {
                table = DataContract.NewsBookmarks.TABLE_NAME;
                String updateCondition = "(" + DataContract.NewsBookmarks._ID + " = " + uri.getLastPathSegment() + ")";
                if (TextUtils.isEmpty(selection))
                    selection = updateCondition;
                else
                    selection = "(" + selection + ")" + " AND " + updateCondition;
                break;
            }
            default: {
                throw new IllegalArgumentException("Unknown URI " + uri);
            }
        }

        int cnt = mDb.update(table, values, selection, selectionArgs);
        Context context = getContext();
        if (context != null) {
            ContentResolver contentResolver = context.getContentResolver();
            contentResolver.notifyChange(uri, null);
        }
        return cnt;
    }
}
