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
        private Status mStatus;
        private String mMessage;

        public ShortNewsListUpdateResult(long pCategoryId, Status pStatus, String pMessage) {
            mCategoryId = pCategoryId;
            mStatus = pStatus;
            mMessage = pMessage;
        }

        protected ShortNewsListUpdateResult(Parcel in) {
            mCategoryId = in.readLong();
            mStatus = Status.valueOf(in.readString());
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

        public Status getStatus() {
            return mStatus;
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
            parcel.writeString(mStatus.name());
            parcel.writeString(mMessage);
        }
    }

    public enum Status {
        Success,
        Fail,
        NoInternet
    }
}
