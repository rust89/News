package ru.sike.lada.database;


import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import ru.sike.lada.R;

public class DbHelper extends SQLiteOpenHelper {

    Context mContext;

    // константы
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "database.db";

    private static final String SQL_CREATE_NEWS_CATEGORIES =
            "CREATE TABLE " + DataContract.NewsCategories.TABLE_NAME + " (" +
                DataContract.NewsCategories._ID + " INTEGER PRIMARY KEY," +
                DataContract.NewsCategories.COLUMN_NAME_NAME + " TEXT, " +
                DataContract.NewsCategories.COLUMN_NAME_ALT_NAME + " TEXT, " +
                DataContract.NewsCategories.COLUMN_NAME_COLOR + " TEXT, " +
                DataContract.NewsCategories.COLUMN_NAME_SORT_ORDER + " INTEGER);";

    private static final String SQL_CREATE_NEWS_SHORT =
            "CREATE TABLE " + DataContract.News.TABLE_NAME + " (" +
                    DataContract.News._ID + " INTEGER PRIMARY KEY," +
                    DataContract.News.COLUMN_NAME_TITLE + " TEXT, " +
                    DataContract.News.COLUMN_NAME_SHORT_STORY + " TEXT, " +
                    DataContract.News.COLUMN_NAME_HTML + " TEXT, " +
                    DataContract.News.COLUMN_NAME_BIG_PICTURE + " TEXT, " +
                    DataContract.News.COLUMN_NAME_SMALL_PICTURE + " TEXT, " +
                    DataContract.News.COLUMN_NAME_AUTHOR + " TEXT, " +
                    DataContract.News.COLUMN_NAME_DATE + " INTEGER, " +
                    DataContract.News.COLUMN_NAME_COMM_NUM + " INTEGER, " +
                    DataContract.News.COLUMN_NAME_VIEWS + " INTEGER, " +
                    DataContract.News.COLUMN_NAME_SOURCE + " TEXT, " +
                    DataContract.News.COLUMN_NAME_SOURCE_NAME + " TEXT, " +
                    DataContract.News.COLUMN_NAME_FULL_LINK  + " TEXT, " +
                    DataContract.News.COLUMN_NAME_CATEGORY_NAME + " TEXT, " +
                    DataContract.News.COLUMN_NAME_PR + " INTEGER, " +
                    DataContract.News.COLUMN_NAME_BIG_PICTURE_CACHE_PATH + " TEXT, " +
                    DataContract.News.COLUMN_NAME_BIG_PICTURE_CACHE_MD5 + " TEXT, " +
                    DataContract.News.COLUMN_NAME_BIG_PICTURE_CACHE_LAST_UPDATE + " INTEGER, " +
                    DataContract.News.COLUMN_NAME_SMALL_PICTURE_CACHE_PATH + " TEXT, " +
                    DataContract.News.COLUMN_NAME_SMALL_PICTURE_CACHE_MD5 + " TEXT, " +
                    DataContract.News.COLUMN_NAME_SMALL_PICTURE_CACHE_LAST_UPDATE + " INTEGER);";

    private static final String SQL_CREATE_NEWS_CATEGORY_BINDER =
            "CREATE TABLE " + DataContract.NewsCategoryBinder.TABLE_NAME + " (" +
                    DataContract.NewsCategoryBinder._ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                    DataContract.NewsCategoryBinder.COLUMN_NAME_NEWS_ID + " INTEGER, " +
                    DataContract.NewsCategoryBinder.COLUMN_NAME_CATEGORY_ID + " INTEGER);";

    private static final String SQL_CREATE_NEWS_BOOKMARKS =
            "CREATE TABLE " + DataContract.NewsBookmarks.TABLE_NAME + " (" +
                    DataContract.NewsBookmarks._ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                    DataContract.NewsBookmarks.COLUMN_NAME_NEWS_ID + " INTEGER);";

    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mContext = context;
    }

    public void onCreate(SQLiteDatabase db) {
        db.beginTransaction();
        try {
            db.execSQL(SQL_CREATE_NEWS_CATEGORIES);
            db.execSQL(SQL_CREATE_NEWS_SHORT);
            db.execSQL(SQL_CREATE_NEWS_CATEGORY_BINDER);
            db.execSQL(SQL_CREATE_NEWS_BOOKMARKS);

            // добавляем встроенную категорию
            ContentValues mainCategory = new ContentValues();
            mainCategory.put(DataContract.NewsCategories._ID, Long.MAX_VALUE);
            mainCategory.put(DataContract.NewsCategories.COLUMN_NAME_NAME, mContext.getResources().getString(R.string.main_news_tab_name));
            mainCategory.put(DataContract.NewsCategories.COLUMN_NAME_ALT_NAME, "");
            mainCategory.put(DataContract.NewsCategories.COLUMN_NAME_COLOR, 0);
            mainCategory.put(DataContract.NewsCategories.COLUMN_NAME_SORT_ORDER, Integer.MAX_VALUE);
            db.insert(DataContract.NewsCategories.TABLE_NAME, null, mainCategory);

            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //
    }

}