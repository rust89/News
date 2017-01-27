package ru.sike.lada.adapters;


import android.database.Cursor;

import ru.sike.lada.database.DataContract;

public class NewsItemColumnMapHelper {

    private int mColumnIndexId = -1;
    private int mColumnIndexTitle = -1;
    private int mColumnIndexPr = -1;
    private int mColumnIndexSmallImageCachePath = -1;
    private int mColumnIndexSmallImageCacheMd5 = -1;
    private int mColumnIndexBigImageCachePath = -1;
    private int mColumnIndexBigImageCacheMd5 = -1;
    private int mColumnIndexViewCount = -1;
    private int mColumnIndexDate = -1;
    private int mColumnIndexBookMark = -1;

    public NewsItemColumnMapHelper(Cursor pCursor) {
        mColumnIndexId = pCursor.getColumnIndex(DataContract.News._ID);
        mColumnIndexTitle = pCursor.getColumnIndex(DataContract.News.COLUMN_NAME_TITLE);
        mColumnIndexPr = pCursor.getColumnIndex(DataContract.News.COLUMN_NAME_PR);
        mColumnIndexSmallImageCachePath = pCursor.getColumnIndex(DataContract.News.COLUMN_NAME_SMALL_PICTURE_CACHE_PATH);
        mColumnIndexSmallImageCacheMd5 = pCursor.getColumnIndex(DataContract.News.COLUMN_NAME_SMALL_PICTURE_CACHE_MD5);
        mColumnIndexBigImageCachePath = pCursor.getColumnIndex(DataContract.News.COLUMN_NAME_BIG_PICTURE_CACHE_PATH);
        mColumnIndexBigImageCacheMd5 = pCursor.getColumnIndex(DataContract.News.COLUMN_NAME_BIG_PICTURE_CACHE_MD5);
        mColumnIndexViewCount = pCursor.getColumnIndex(DataContract.News.COLUMN_NAME_VIEWS);
        mColumnIndexDate = pCursor.getColumnIndex(DataContract.News.COLUMN_NAME_DATE);
        mColumnIndexBookMark = pCursor.getColumnIndex("bookmarked");
    }

    public int getIdIndex() {
        return mColumnIndexId;
    }

    public int getTitleIndex() {
        return mColumnIndexTitle;
    }

    public int getPrIndex() {
        return mColumnIndexPr;
    }

    public int getSmallImageCachePathIndex () {
        return mColumnIndexSmallImageCachePath;
    }

    public int getSmallImageCacheMd5Index() {
        return mColumnIndexSmallImageCacheMd5;
    }

    public int getBigImageCachePathIndex() {
        return mColumnIndexBigImageCachePath;
    }

    public int getBigImageCacheMd5Index() {
        return mColumnIndexBigImageCacheMd5;
    }

    public int getViewCountIndex() {
        return mColumnIndexViewCount;
    }

    public int getDateIndex() {
        return mColumnIndexDate;
    }

    public int getBookmarkIndex() {
        return mColumnIndexBookMark;
    }

}
