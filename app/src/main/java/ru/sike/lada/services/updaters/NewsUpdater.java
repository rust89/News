package ru.sike.lada.services.updaters;


import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;

import java.util.List;

import ru.sike.lada.backend.json.NewsFull;
import ru.sike.lada.backend.json.NewsShort;
import ru.sike.lada.database.DataContract;
import ru.sike.lada.providers.LadaContent;

public class NewsUpdater extends BaseUpdater {

    public NewsUpdater(Context context) {
        super(context);
    }

    public void Update(List<NewsShort> list, long categoryId) {
        for (NewsShort item : list) {
            Uri uri = Uri.withAppendedPath(Uri.parse("content://" + LadaContent.NEWS_URI_SCHEMA), String.valueOf(item.getId()));
            final ContentResolver contentResolver = context.getContentResolver();
            ContentValues values = packContent(item);
            if (isRowExists(uri, DataContract.News._ID)) {
                Uri updateUri = Uri.withAppendedPath(Uri.parse("content://" + LadaContent.NEWS_URI_SCHEMA), String.valueOf(item.getId()));
                contentResolver.update(updateUri, values, null, null);
            } else {
                Uri insertUri = Uri.parse("content://" + LadaContent.NEWS_URI_SCHEMA);
                contentResolver.insert(insertUri, values);
            }

            // добавляем связь
            if (!getBinding(item.getId(), categoryId)) {
                ContentValues bindedValues = packBinding(item.getId(), categoryId);
                Uri insertUri = Uri.parse("content://" + LadaContent.NEWS_CATEGORY_BINDER_URI_SCHEMA);
                contentResolver.insert(insertUri, bindedValues);
            }
        }
    }

    public boolean Update(NewsFull pNewsItem) {
        Uri uri = Uri.withAppendedPath(Uri.parse("content://" + LadaContent.NEWS_URI_SCHEMA), String.valueOf(pNewsItem.getId()));
        final ContentResolver contentResolver = context.getContentResolver();
        ContentValues values = packContent(pNewsItem);
        if (isRowExists(uri, DataContract.News._ID)) {
            Uri updateUri = Uri.withAppendedPath(Uri.parse("content://" + LadaContent.NEWS_URI_SCHEMA), String.valueOf(pNewsItem.getId()));
            return contentResolver.update(updateUri, values, null, null) != 0;
        } else {
            return false;
        }
    }

    private ContentValues packContent(NewsShort item) {
        ContentValues result = new ContentValues();
        result.put(DataContract.News._ID, item.getId());
        result.put(DataContract.News.COLUMN_NAME_BIG_PICTURE, item.getBigPicture());
        result.put(DataContract.News.COLUMN_NAME_SMALL_PICTURE, item.getSmallPicture());
        result.put(DataContract.News.COLUMN_NAME_DATE, getTimestamp(item.getDate()));
        result.put(DataContract.News.COLUMN_NAME_COMM_NUM, item.getCommNum());
        result.put(DataContract.News.COLUMN_NAME_VIEWS, item.getViews());
        result.put(DataContract.News.COLUMN_NAME_TITLE, item.getTitle());
        result.put(DataContract.News.COLUMN_NAME_SHORT_STORY, item.getShortStory());
        result.put(DataContract.News.COLUMN_NAME_PR, item.getPr());
        return result;
    }

    private ContentValues packContent(NewsFull item) {
        ContentValues result = new ContentValues();
        result.put(DataContract.News._ID, item.getId());
        result.put(DataContract.News.COLUMN_NAME_BIG_PICTURE, item.getBigPicture());
        result.put(DataContract.News.COLUMN_NAME_SMALL_PICTURE, item.getSmallPicture());
        result.put(DataContract.News.COLUMN_NAME_DATE, getTimestamp(item.getDate()));
        result.put(DataContract.News.COLUMN_NAME_COMM_NUM, item.getCommNum());
        result.put(DataContract.News.COLUMN_NAME_VIEWS, item.getViews());
        result.put(DataContract.News.COLUMN_NAME_TITLE, item.getTitle());
        result.put(DataContract.News.COLUMN_NAME_SHORT_STORY, item.getShortStory());
        result.put(DataContract.News.COLUMN_NAME_PR, item.getPr());
        result.put(DataContract.News.COLUMN_NAME_HTML, item.getHtml());
        result.put(DataContract.News.COLUMN_NAME_AUTHOR, item.getAuthor());
        result.put(DataContract.News.COLUMN_NAME_SOURCE, item.getSource());
        result.put(DataContract.News.COLUMN_NAME_SOURCE_NAME, item.getSourceName());
        result.put(DataContract.News.COLUMN_NAME_FULL_LINK, item.getFullLink());
        result.put(DataContract.News.COLUMN_NAME_CATEGORY_NAME, item.getCategoryName());
        return result;
    }

    private ContentValues packBinding(long newsId, long categoryId) {
        ContentValues result = new ContentValues();
        result.put(DataContract.NewsCategoryBinder.COLUMN_NAME_NEWS_ID, newsId);
        result.put(DataContract.NewsCategoryBinder.COLUMN_NAME_CATEGORY_ID, categoryId);
        return result;
    }

    private boolean getBinding(long newsId, long categoryId) {
        boolean result = false;
        final ContentResolver contentResolver = context.getContentResolver();
        Cursor cursor = contentResolver.query(
                Uri.parse("content://" + LadaContent.NEWS_CATEGORY_BINDER_URI_SCHEMA),
                new String[] { DataContract.NewsCategoryBinder._ID },
                DataContract.NewsCategoryBinder.COLUMN_NAME_NEWS_ID + " = " + String.valueOf(newsId) +
                        " AND " + DataContract.NewsCategoryBinder.COLUMN_NAME_CATEGORY_ID + " = " + String.valueOf(categoryId),
                null, null);
        if (cursor != null) {
            try {
                if (cursor.moveToFirst())
                    result = cursor.moveToFirst();
            } finally {
                cursor.close();
            }
        }
        return result;
    }
}
