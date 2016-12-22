package ru.sike.lada.loaders;

import android.content.Context;
import android.net.Uri;
import android.support.v4.content.CursorLoader;

import ru.sike.lada.database.DataContract;
import ru.sike.lada.providers.LadaContent;


public class NewsShortCursorLoader extends CursorLoader {
    public NewsShortCursorLoader(Context context, long categoryId) {
        super(context, Uri.parse("content://" + LadaContent.NEWS_URI_SCHEMA),
                new String[] {
                        DataContract.News._ID,
                        DataContract.News.COLUMN_NAME_TITLE,
                        DataContract.News.COLUMN_NAME_SMALL_PICTURE,
                        DataContract.News.COLUMN_NAME_BIG_PICTURE,
                        DataContract.News.COLUMN_NAME_VIEWS,
                        DataContract.News.COLUMN_NAME_DATE,
                        DataContract.News.COLUMN_NAME_PR,
                        DataContract.News.COLUMN_NAME_SMALL_PICTURE_CACHE_PATH,
                        DataContract.News.COLUMN_NAME_SMALL_PICTURE_CACHE_MD5,
                        DataContract.News.COLUMN_NAME_BIG_PICTURE_CACHE_PATH,
                        DataContract.News.COLUMN_NAME_BIG_PICTURE_CACHE_MD5,
                        "(select count(" + DataContract.NewsBookmarks.TABLE_NAME + "." + DataContract.NewsBookmarks._ID +
                                ") from " + DataContract.NewsBookmarks.TABLE_NAME + " where " +
                                DataContract.NewsBookmarks.TABLE_NAME + "." + DataContract.NewsBookmarks.COLUMN_NAME_NEWS_ID +
                                " = " + DataContract.News.TABLE_NAME + "." + DataContract.News._ID + ") AS bookmarked"
                },
                DataContract.News._ID + " in (select " +
                        "b." + DataContract.NewsCategoryBinder.COLUMN_NAME_NEWS_ID +
                        " from " + DataContract.NewsCategoryBinder.TABLE_NAME + " as b" +
                        " where b." + DataContract.NewsCategoryBinder.COLUMN_NAME_CATEGORY_ID + " = " + String.valueOf(categoryId) + ")",
                null, DataContract.News._ID + " DESC");
    }
}


