package ru.sike.lada.services.updaters;


import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import ru.sike.lada.database.DataContract;
import ru.sike.lada.providers.LadaContent;

public class BookmarksUpdater extends BaseUpdater {

    public BookmarksUpdater(Context context) {
        super(context);
    }

    public void Update(long newsId, int bookmarked) {

        final ContentResolver contentResolver = context.getContentResolver();
        if (bookmarked > 0) {
            ContentValues values = packContent(newsId);
            Long existedBookMark = getBookMark(newsId);
            if (existedBookMark == null) {
                Uri insertUri = Uri.parse("content://" + LadaContent.NEWS_BOOKMARKS_URI_SCHEMA);
                contentResolver.insert(insertUri, values);
            }
        } else {
            Uri deleteUri = Uri.parse("content://" + LadaContent.NEWS_BOOKMARKS_URI_SCHEMA);
            contentResolver.delete(deleteUri,
                    DataContract.NewsBookmarks.COLUMN_NAME_NEWS_ID + " = ?",
                    new String[] {String.valueOf(newsId)});
        }

    }

    private ContentValues packContent(long newsId) {
        ContentValues result = new ContentValues();
        result.put(DataContract.NewsBookmarks.COLUMN_NAME_NEWS_ID, newsId);
        return result;
    }

    private Long getBookMark(long newsId) {
        Long result = null;
        final ContentResolver contentResolver = context.getContentResolver();
        Cursor cursor = contentResolver.query(
                Uri.parse("content://" + LadaContent.NEWS_BOOKMARKS_URI_SCHEMA),
                new String[] { DataContract.NewsBookmarks._ID },
                DataContract.NewsBookmarks.COLUMN_NAME_NEWS_ID + " = ?",
                new String[] {String.valueOf(newsId)}, null);
        if (cursor != null) {
            try {
                if (cursor.moveToFirst())
                    result = cursor.getLong(cursor.getColumnIndex(DataContract.NewsBookmarks._ID));
            } finally {
                cursor.close();
            }
        }
        return result;
    }
}
