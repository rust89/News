package ru.sike.lada.services.updaters;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public abstract class BaseUpdater {

    protected Context context;

    public BaseUpdater(Context context) {
        this.context = context;
    }

    protected boolean isRowExists(Uri uri, String idFieldName) {
        boolean result = false;
        final ContentResolver contentResolver = context.getContentResolver();
        Cursor cursor = contentResolver.query(
                uri,
                new String[] { idFieldName },
                null, null, null);
        if (cursor != null) {
            try {
                result = cursor.moveToFirst();
            } finally {
                cursor.close();
            }
        }
        return result;
    }

    protected long getTimestamp(String date){
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.getDefault());
        long result;
        try {
            Date convertedDate = dateFormat.parse(date);
            result = convertedDate.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
            result = 0;
        }
        return result;
    }
}
