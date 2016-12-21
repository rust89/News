package ru.sike.lada.loaders;

import android.content.Context;
import android.net.Uri;
import android.support.v4.content.CursorLoader;

import ru.sike.lada.database.DataContract;
import ru.sike.lada.providers.LadaContent;


public class BookmarkedListCursorLoader extends CursorLoader {
    public BookmarkedListCursorLoader(Context context) {
        super(context, Uri.parse("content://" + LadaContent.NEWS_URI_SCHEMA),
                new String[] {
                        DataContract.News._ID,
                        DataContract.News.COLUMN_NAME_TITLE,
                        DataContract.News.COLUMN_NAME_SMALL_PICTURE,
                        DataContract.News.COLUMN_NAME_VIEWS,
                        DataContract.News.COLUMN_NAME_DATE,
                        DataContract.News.COLUMN_NAME_PR,
                },
                DataContract.News._ID + " in (select " +
                        "b." + DataContract.NewsBookmarks.COLUMN_NAME_NEWS_ID +
                        " from " + DataContract.NewsBookmarks.TABLE_NAME + " as b)",
                null, DataContract.News._ID + " DESC");
    }
}
