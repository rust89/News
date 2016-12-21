package ru.sike.lada.loaders;

import android.content.Context;
import android.net.Uri;
import android.support.v4.content.CursorLoader;

import ru.sike.lada.database.DataContract;
import ru.sike.lada.providers.LadaContent;


public class BookmarkCursorLoader extends CursorLoader {
    public BookmarkCursorLoader(Context context, long newsId) {
        super(context, Uri.parse("content://" + LadaContent.NEWS_BOOKMARKS_URI_SCHEMA),
                new String[] {
                        DataContract.NewsBookmarks._ID,
                        DataContract.NewsBookmarks.COLUMN_NAME_NEWS_ID
                },
                DataContract.NewsBookmarks.COLUMN_NAME_NEWS_ID + " = ?",
                new String[] {String.valueOf(newsId)}, null);
    }
}
