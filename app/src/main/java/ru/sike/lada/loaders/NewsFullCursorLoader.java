package ru.sike.lada.loaders;

import android.content.Context;
import android.net.Uri;
import android.support.v4.content.CursorLoader;

import ru.sike.lada.database.DataContract;
import ru.sike.lada.providers.LadaContent;


public class NewsFullCursorLoader extends CursorLoader {
    public NewsFullCursorLoader(Context context, long newsId) {
        super(context, Uri.parse("content://" + LadaContent.NEWS_URI_SCHEMA),
                new String[]{
                        DataContract.News._ID,
                        DataContract.News.COLUMN_NAME_TITLE,
                        DataContract.News.COLUMN_NAME_SHORT_STORY,
                        DataContract.News.COLUMN_NAME_HTML,
                        DataContract.News.COLUMN_NAME_BIG_PICTURE,
                        DataContract.News.COLUMN_NAME_SMALL_PICTURE,
                        DataContract.News.COLUMN_NAME_AUTHOR,
                        DataContract.News.COLUMN_NAME_DATE,
                        DataContract.News.COLUMN_NAME_COMM_NUM,
                        DataContract.News.COLUMN_NAME_VIEWS,
                        DataContract.News.COLUMN_NAME_SOURCE,
                        DataContract.News.COLUMN_NAME_SOURCE_NAME,
                        DataContract.News.COLUMN_NAME_FULL_LINK,
                        DataContract.News.COLUMN_NAME_CATEGORY_NAME,
                        DataContract.News.COLUMN_NAME_PR,
                        DataContract.News.COLUMN_NAME_BIG_PICTURE_CACHE_PATH,
                },
                DataContract.News._ID + " = ?", new String[]{String.valueOf(newsId)}, null);
    }
}
