package ru.sike.lada.utils.caching;

import ru.sike.lada.database.DataContract;
import ru.sike.lada.providers.LadaContent;

public final class CacheTaskFactory {

    private static final String mFilePrefixNewsSmallImage = "NewsSmallImage";
    private static final String mFilePrefixNewsBigImage = "NewsBigImage";

    /**
     * Создаёт задание для кеширования маленького изображения новости
     * @param pNewsId - идентификатор новости
     * @return Задание для кеширования маленького изображения новости
     */
    public static CacheTask getNewsSmallImageCacheTask(long pNewsId) {
        return new CacheTask(
            "content://" + LadaContent.NEWS_URI_SCHEMA,
            pNewsId, DataContract.News.COLUMN_NAME_SMALL_PICTURE,
            DataContract.News.COLUMN_NAME_SMALL_PICTURE_CACHE_PATH,
            DataContract.News.COLUMN_NAME_SMALL_PICTURE_CACHE_MD5,
            DataContract.News.COLUMN_NAME_SMALL_PICTURE_CACHE_LAST_UPDATE,
            mFilePrefixNewsSmallImage
        );
    }

    /**
     * Создаёт задание для кеширования полного изображения новости
     * @param pNewsId - идентификатор новости
     * @return Задание для кеширования полного изображения новости
     */
    public static CacheTask getNewsBigImageCacheTask(long pNewsId) {
        return new CacheTask(
            "content://" + LadaContent.NEWS_URI_SCHEMA,
            pNewsId, DataContract.News.COLUMN_NAME_BIG_PICTURE,
            DataContract.News.COLUMN_NAME_BIG_PICTURE_CACHE_PATH,
            DataContract.News.COLUMN_NAME_BIG_PICTURE_CACHE_MD5,
            DataContract.News.COLUMN_NAME_BIG_PICTURE_CACHE_LAST_UPDATE,
            mFilePrefixNewsBigImage
        );
    }

}
