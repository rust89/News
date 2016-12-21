package ru.sike.lada.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Parcel;
import android.os.Parcelable;


public abstract class FullNewsUpdateReceiver extends BroadcastReceiver {
    public static final String BROADCAST_PARAM = "BROADCAST_PARAM";
    public static final String BROADCAST_ACTION = "ru.sike.lada.receivers.FullNewsUpdateReceiver";

    public static void Broadcast(Context pContext, FullNewsUpdateResult pResult) {
        Intent intent = new Intent(BROADCAST_ACTION);
        intent.putExtra(BROADCAST_PARAM, pResult);
        pContext.sendBroadcast(intent);
    }

    public static class FullNewsUpdateResult implements Parcelable {
        private long mNewsId;
        private boolean mIsSuccessfull;
        private String mMessage;

        public FullNewsUpdateResult(long pNewsId, boolean pIsSuccessfull, String pMessage) {
            mNewsId = pNewsId;
            mIsSuccessfull = pIsSuccessfull;
            mMessage = pMessage;
        }

        protected FullNewsUpdateResult(Parcel in) {
            mNewsId = in.readLong();
            mIsSuccessfull = in.readByte() != 0;
            mMessage = in.readString();
        }

        public static final Creator<FullNewsUpdateResult> CREATOR = new Creator<FullNewsUpdateResult>() {
            @Override
            public FullNewsUpdateResult createFromParcel(Parcel in) {
                return new FullNewsUpdateResult(in);
            }

            @Override
            public FullNewsUpdateResult[] newArray(int size) {
                return new FullNewsUpdateResult[size];
            }
        };

        public long getNewsId() {
            return mNewsId;
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
            parcel.writeLong(mNewsId);
            parcel.writeByte((byte) (mIsSuccessfull ? 1 : 0));
            parcel.writeString(mMessage);
        }
    }
}
