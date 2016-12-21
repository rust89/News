package ru.sike.lada.models;

import android.database.Cursor;

import ru.sike.lada.database.DataContract;

public class NewsCategory {

    private long mId;
    private String mName;
    private String mColor;
    private int mSortOrder;

    public NewsCategory(Cursor pCursor) {
        mId = pCursor.getLong(pCursor.getColumnIndex(DataContract.NewsCategories._ID));
        mName = pCursor.getString(pCursor.getColumnIndex(DataContract.NewsCategories.COLUMN_NAME_NAME));
        mColor = pCursor.getString(pCursor.getColumnIndex(DataContract.NewsCategories.COLUMN_NAME_COLOR));
        mSortOrder = pCursor.getInt(pCursor.getColumnIndex(DataContract.NewsCategories.COLUMN_NAME_SORT_ORDER));
    }

    public long getId() {
        return mId;
    }

    public String getName() {
        return mName;
    }

    public String getColor() {
        return mColor;
    }

    public int getSortOrder() {
        return mSortOrder;
    }
}
