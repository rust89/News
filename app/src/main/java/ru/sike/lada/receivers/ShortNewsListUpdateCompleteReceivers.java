package ru.sike.lada.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Parcel;
import android.os.Parcelable;

public abstract class ShortNewsListUpdateCompleteReceivers extends BroadcastReceiver {
    public static final String BROADCAST_PARAM = "BROADCAST_PARAM";
    public static final String BROADCAST_ACTION = "ru.sike.lada.receivers.ShortNewsListUpdateCompleteReceivers";

    public static void Broadcast(Context pContext, ShortNewsListUpdateResult pResult) {
        Intent intent = new Intent(BROADCAST_ACTION);
        intent.putExtra(BROADCAST_PARAM, pResult);
        pContext.sendBroadcast(intent);
    }

    public static class ShortNewsListUpdateResult implements Parcelable {
        private long mCategoryId;
        private boolean mIsSuccessfull;
        private String mMessage;

        public ShortNewsListUpdateResult(long pCategoryId, boolean pIsSuccessfull, String pMessage) {
            mCategoryId = pCategoryId;
            mIsSuccessfull = pIsSuccessfull;
            mMessage = pMessage;
        }

        protected ShortNewsListUpdateResult(Parcel in) {
            mCategoryId = in.readLong();
            mIsSuccessfull = in.readByte() != 0;
            mMessage = in.readString();
        }

        public static final Creator<ShortNewsListUpdateResult> CREATOR = new Creator<ShortNewsListUpdateResult>() {
            @Override
            public ShortNewsListUpdateResult createFromParcel(Parcel in) {
                return new ShortNewsListUpdateResult(in);
            }

            @Override
            public ShortNewsListUpdateResult[] newArray(int size) {
                return new ShortNewsListUpdateResult[size];
            }
        };

        public long getCategoryId() {
            return mCategoryId;
        }

        public boolean isIsSuccessfull() {
            return mIsSuccessfull;
        }

        public String getMessage() {
            return mMessage;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel parcel, int i) {
            parcel.writeLong(mCategoryId);
            parcel.writeByte((byte) (mIsSuccessfull ? 1 : 0));
            parcel.writeString(mMessage);
        }
    }
}
