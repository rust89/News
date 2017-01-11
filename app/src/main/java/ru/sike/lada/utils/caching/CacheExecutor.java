package ru.sike.lada.utils.caching;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import ru.sike.lada.utils.Constants;

public class CacheExecutor {

    private final static String LOG_TAG = "CacheExecutor";

    public static void Execute(Context pContext, CacheTask pTask) {
        CacheInfo cacheInfo = getCurrentCache(pContext, pTask);
        if (cacheInfo != null) {
            if (!cacheInfo.isUrlEmpty()) {
                long currentTime = System.currentTimeMillis();
                if (currentTime - cacheInfo.getLastUpdate() > Constants.news_image_cache_expire_period) {
                    // скачиваем файл
                    byte[] buffer = Downloader.Download(cacheInfo.getUrl().replace("www.lada.kz", "www.7292.kz"));
                    if (buffer != null && buffer.length > 0) {
                        String buffer_md5 = Downloader.getMD5(buffer);
                        String previousFilePath = cacheInfo.getCachePath();

                        File previousFile = new File(previousFilePath != null ? previousFilePath : "");
                        // Пересохраняем файл, если хешсуммы не совпадают
                        // или файла не существует
                        if (!buffer_md5.equals(cacheInfo.getMd5()) || !previousFile.exists()) {
                            File newFile = previousFile.exists() ? previousFile : new File(getCacheDir(pContext), pTask.createCacheFileName());
                            if (writeFile(newFile, buffer)) {
                                if (!updateCacheValues(pContext, pTask,
                                        createUpdateValues(pTask, newFile.getAbsolutePath(), buffer_md5))) {
                                    if (!newFile.delete()) {
                                        Log.e(LOG_TAG, "File deletion failed " + newFile.getAbsolutePath());
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private static File getCacheDir(Context pContext) {
        File result = pContext.getExternalCacheDir();
        if (result == null)
            result = pContext.getCacheDir();
        return result;
    }

    private static ContentValues createUpdateValues(CacheTask pTask, String pFilePath, String pMd5) {
        ContentValues values = new ContentValues();
        values.put(pTask.getColumnNameCachePath(), pFilePath);
        values.put(pTask.getColumnNameMD5(), pMd5);
        values.put(pTask.getColumnNameLastUpdate(), System.currentTimeMillis());
        return values;
    }

    private static boolean updateCacheValues(Context pContext, CacheTask pTask, ContentValues pValues) {
        ContentResolver contentResolver = pContext.getContentResolver();
        return contentResolver.update(
                ContentUris.withAppendedId(Uri.parse(pTask.getUri()), pTask.getRowId()),
                pValues, null, null) > 0;
    }

    private static boolean writeFile(File pFile, byte[] pBuffer) {
        boolean result = false;
        try {
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(pFile, false));
            try {
                bos.write(pBuffer);
                result = true;
            } catch (IOException Ex) {
                Ex.printStackTrace();
            } finally {
                try {
                    bos.close();
                } catch (IOException Ex) {
                    result = false;
                    Ex.printStackTrace();
                }
            }
        } catch (FileNotFoundException Ex) {
            Ex.printStackTrace();
        }
        return result;
    }

    private static CacheInfo getCurrentCache(Context pContext, CacheTask pTask) {
        ContentResolver contentResolver = pContext.getContentResolver();
        Cursor cursor = contentResolver.query(
                ContentUris.withAppendedId(Uri.parse(pTask.getUri()), pTask.getRowId()),
                new String[] {
                        pTask.getColumnNameUrl(),
                        pTask.getColumnNameCachePath(),
                        pTask.getColumnNameMD5(),
                        pTask.getColumnNameLastUpdate()
                },
                null, null, null);

        CacheInfo result = null;
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                result = new CacheInfo(
                        cursor.getString(cursor.getColumnIndex(pTask.getColumnNameUrl())),
                        cursor.getString(cursor.getColumnIndex(pTask.getColumnNameCachePath())),
                        cursor.getString(cursor.getColumnIndex(pTask.getColumnNameMD5())),
                        cursor.getLong(cursor.getColumnIndex(pTask.getColumnNameLastUpdate()))
                );
            }
            cursor.close();
        }
        return result;
    }

    private static class CacheInfo {
        private final String mUrl;
        private final String mCachePath;
        private final String mMd5;
        private final long mLastUpdate;

        public CacheInfo(String pUrl, String pCachePath, String pMd5, long pLastUpdate) {
            mUrl = pUrl;
            mCachePath = pCachePath;
            mMd5 = pMd5;
            mLastUpdate = pLastUpdate;
        }

        public String getUrl() {
            return mUrl;
        }

        public String getCachePath() {
            return mCachePath;
        }

        public boolean isUrlEmpty() {
            return mUrl == null || mUrl.isEmpty();
        }

        public String getMd5() {
            return mMd5;
        }

        public long getLastUpdate() {
            return mLastUpdate;
        }
    }
}
