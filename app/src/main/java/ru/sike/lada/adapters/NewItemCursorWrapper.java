package ru.sike.lada.adapters;


import android.database.Cursor;

public class NewItemCursorWrapper {

    private Cursor mCursor;
    private NewsItemColumnMapHelper mColumnMediator;

    private String wrapNullableString(String pValue) {
        return pValue == null ? "" : pValue;
    }

    public NewItemCursorWrapper(Cursor pCursor, NewsItemColumnMapHelper pColumnMediator) {
        mCursor = pCursor;
        mColumnMediator = pColumnMediator;
    }

    public long getId() {
        return mCursor.getLong(mColumnMediator.getIdIndex());
    }

    public String getTitle() {
        return wrapNullableString(mCursor.getString(mColumnMediator.getTitleIndex()));
    }

    public boolean isPr() {
        return mCursor.getInt(mColumnMediator.getPrIndex()) == 1;
    }

    public String getSmallImageCachePath() {
        return wrapNullableString(mCursor.getString(mColumnMediator.getSmallImageCachePathIndex()));
    }

    public String getSmallImageCacheMd5() {
        return wrapNullableString(mCursor.getString(mColumnMediator.getSmallImageCacheMd5Index()));
    }

    public String getBigImageCachePath() {
        return wrapNullableString(mCursor.getString(mColumnMediator.getBigImageCachePathIndex()));
    }

    public String getBigImageCacheMd5() {
        return wrapNullableString(mCursor.getString(mColumnMediator.getBigImageCacheMd5Index()));
    }

    public int getViewCount() {
        return mCursor.getInt(mColumnMediator.getViewCountIndex());
    }

    public long getDate() {
        return mCursor.getLong(mColumnMediator.getDateIndex());
    }

    public boolean getBookmark() {
        return mCursor.getInt(mColumnMediator.getBookmarkIndex()) == 1;
    }
}
