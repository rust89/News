package ru.sike.lada.models;

import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import ru.sike.lada.database.DataContract;

public class News implements Parcelable {
    private long mId;
    private String mTitle;
    private String mShortStory;
    private String mHtml;
    private String mBigPicture;
    private String mSmallPicture;
    private String mAuthor;
    private long mDate;
    private int mCommNum;
    private int mViews;
    private String mSource;
    private String mSourceName;
    private String mFullLink;
    private String mCategoryName;
    private int mPr;
    private String mBigPictureCachePath;

    public News(Cursor pCursor) {
        mId = pCursor.getLong(pCursor.getColumnIndex(DataContract.News._ID));
        mTitle = pCursor.getString(pCursor.getColumnIndex(DataContract.News.COLUMN_NAME_TITLE));
        mShortStory = pCursor.getString(pCursor.getColumnIndex(DataContract.News.COLUMN_NAME_SHORT_STORY));
        mHtml = pCursor.getString(pCursor.getColumnIndex(DataContract.News.COLUMN_NAME_HTML));
        mBigPicture = pCursor.getString(pCursor.getColumnIndex(DataContract.News.COLUMN_NAME_BIG_PICTURE));
        mSmallPicture = pCursor.getString(pCursor.getColumnIndex(DataContract.News.COLUMN_NAME_SMALL_PICTURE));
        mAuthor = pCursor.getString(pCursor.getColumnIndex(DataContract.News.COLUMN_NAME_AUTHOR));
        mDate = pCursor.getLong(pCursor.getColumnIndex(DataContract.News.COLUMN_NAME_DATE));
        mCommNum = pCursor.getInt(pCursor.getColumnIndex(DataContract.News.COLUMN_NAME_COMM_NUM));
        mViews = pCursor.getInt(pCursor.getColumnIndex(DataContract.News.COLUMN_NAME_VIEWS));
        mSource = pCursor.getString(pCursor.getColumnIndex(DataContract.News.COLUMN_NAME_SOURCE));
        mSourceName = pCursor.getString(pCursor.getColumnIndex(DataContract.News.COLUMN_NAME_SOURCE_NAME));
        mFullLink = pCursor.getString(pCursor.getColumnIndex(DataContract.News.COLUMN_NAME_FULL_LINK));
        mCategoryName = pCursor.getString(pCursor.getColumnIndex(DataContract.News.COLUMN_NAME_CATEGORY_NAME));
        mPr = pCursor.getInt(pCursor.getColumnIndex(DataContract.News.COLUMN_NAME_PR));
        mBigPictureCachePath = pCursor.getString(pCursor.getColumnIndex(DataContract.News.COLUMN_NAME_BIG_PICTURE_CACHE_PATH));
    }

    protected News(Parcel in) {
        mId = in.readLong();
        mTitle = in.readString();
        mShortStory = in.readString();
        mHtml = in.readString();
        mBigPicture = in.readString();
        mSmallPicture = in.readString();
        mAuthor = in.readString();
        mDate = in.readLong();
        mCommNum = in.readInt();
        mViews = in.readInt();
        mSource = in.readString();
        mSourceName = in.readString();
        mFullLink = in.readString();
        mCategoryName = in.readString();
        mPr = in.readInt();
        mBigPictureCachePath = in.readString();
    }

    public static final Creator<News> CREATOR = new Creator<News>() {
        @Override
        public News createFromParcel(Parcel in) {
            return new News(in);
        }

        @Override
        public News[] newArray(int size) {
            return new News[size];
        }
    };

    public long getId() {
        return mId;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getShortStory() {
        return mShortStory;
    }

    public String getHtml() {
        return mHtml;
    }

    public String getBigPicture() {
        return mBigPicture;
    }

    public String getSmallPicture() {
        return mSmallPicture;
    }

    public String getAuthor() {
        return mAuthor;
    }

    public long getDate() {
        return mDate;
    }

    public int getCommNum() {
        return mCommNum;
    }

    public int getViews() {
        return mViews;
    }

    public String getSource() {
        return mSource;
    }

    public String getSourceName() {
        return mSourceName;
    }

    public String getFullLink() {
        return mFullLink;
    }

    public String getCategoryName() {
        return mCategoryName;
    }

    public int getPr() {
        return mPr;
    }

    public String getBigPictureCachePath() {
        return mBigPictureCachePath;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeLong(mId);
        parcel.writeString(mTitle);
        parcel.writeString(mShortStory);
        parcel.writeString(mHtml);
        parcel.writeString(mBigPicture);
        parcel.writeString(mSmallPicture);
        parcel.writeString(mAuthor);
        parcel.writeLong(mDate);
        parcel.writeInt(mCommNum);
        parcel.writeInt(mViews);
        parcel.writeString(mSource);
        parcel.writeString(mSourceName);
        parcel.writeString(mFullLink);
        parcel.writeString(mCategoryName);
        parcel.writeInt(mPr);
        parcel.writeString(mBigPictureCachePath);
    }
}
