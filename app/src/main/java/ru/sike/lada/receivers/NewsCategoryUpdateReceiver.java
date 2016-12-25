package ru.sike.lada.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Parcel;
import android.os.Parcelable;


public abstract class NewsCategoryUpdateReceiver extends BroadcastReceiver {
    public static final String BROADCAST_PARAM = "BROADCAST_PARAM";
    public static final String BROADCAST_ACTION = "ru.sike.lada.receivers.NewsCategoryUpdateReceiver";

    public static void Broadcast(Context pContext, NewsCategoryUpdateResult pResult) {
        Intent intent = new Intent(BROADCAST_ACTION);
        intent.putExtra(BROADCAST_PARAM, pResult);
        pContext.sendBroadcast(intent);
    }

    public static class NewsCategoryUpdateResult implements Parcelable {
        private Status mStatus;
        private String mMessage;

        public NewsCategoryUpdateResult(Status pStatus, String pMessage) {
            mStatus = pStatus;
            mMessage = pMessage;
        }

        protected NewsCategoryUpdateResult(Parcel in) {
            mStatus = Status.valueOf(in.readString());
            mMessage = in.readString();
        }

        public static final Creator<NewsCategoryUpdateResult> CREATOR = new Creator<NewsCategoryUpdateResult>() {
            @Override
            public NewsCategoryUpdateResult createFromParcel(Parcel in) {
                return new NewsCategoryUpdateResult(in);
            }

            @Override
            public NewsCategoryUpdateResult[] newArray(int size) {
                return new NewsCategoryUpdateResult[size];
            }
        };


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
