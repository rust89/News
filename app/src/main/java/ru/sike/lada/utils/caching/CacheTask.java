package ru.sike.lada.utils.caching;

/**
 * Объект описывающий задание для кеширования
 */
public class CacheTask {
    private String mUri;
    private long mRowId;
    private String mColumnNameUrl;
    private String mColumnNameCachePath;
    private String mColumnNameMD5;
    private String mColumnNameLastUpdate;
    private String mFileNamePrefix;

    /**
     * Конструктор задания
     * @param pUri Uri для доступа к данным через ContentResolver
     * @param pRowId Идентификатор строки
     * @param pColumnNameUrl Имя колонки, которая содержит ссылку на кешируемый ресурс
     * @param pColumnNameCachePath Имя колонки в которую будет записан путь до закешированного ресурса
     * @param pColumnNameMD5 Имя колонки с MD5 отпечатком закешированного ресурса
     * @param pColumnNameLastUpdate Имя колонки, которая содержит время последнего обновления ресурса
     */
    public CacheTask(
        String pUri,
        long pRowId,
        String pColumnNameUrl,
        String pColumnNameCachePath,
        String pColumnNameMD5,
        String pColumnNameLastUpdate,
        String pFileNamePrefix) {

        mUri = pUri;
        mRowId = pRowId;
        mColumnNameUrl = pColumnNameUrl;
        mColumnNameCachePath = pColumnNameCachePath;
        mColumnNameMD5 = pColumnNameMD5;
        mColumnNameLastUpdate = pColumnNameLastUpdate;
        mFileNamePrefix = pFileNamePrefix;
    }

    @Override
    public boolean equals (Object obj) {
        return (obj instanceof CacheTask) &&
            ((CacheTask) obj).mUri.equals(mUri) &&
            (((CacheTask) obj).mRowId == mRowId) &&
            ((CacheTask) obj).mColumnNameUrl.equals(mColumnNameUrl) &&
            ((CacheTask) obj).mColumnNameCachePath.equals(mColumnNameCachePath) &&
            ((CacheTask) obj).mColumnNameMD5.equals(mColumnNameMD5) &&
            ((CacheTask) obj).mColumnNameLastUpdate.equals(mColumnNameLastUpdate);
    }

    /**
     * Возвращает Uri для доступа к данным через ContentResolver
     * @return
     */
    public String getUri() {
        return mUri;
    }

    /**
     * Возвращает идентификатор строки
     * @return
     */
    public long getRowId() {
        return mRowId;
    }

    /**
     * Возвращает имя колонки, которая содержит ссылку на кешируемый ресурс
     * @return
     */
    public String getColumnNameUrl() {
        return mColumnNameUrl;
    }

    /**
     * Возвращает имя колонки в которую будет записан путь до закешированного ресурса
     * @return
     */
    public String getColumnNameCachePath() {
        return mColumnNameCachePath;
    }

    /**
     * Возвращает имя колонки с MD5 отпечатком закешированного ресурса
     * @return
     */
    public String getColumnNameMD5() {
        return mColumnNameMD5;
    }

    /**
     * Возвращает имя колонки, которая содержит время последнего обновления ресурса
     * @return
     */
    public String getColumnNameLastUpdate() {
        return mColumnNameLastUpdate;
    }

    /**
     * Возвращает префис имени файла для кеширования
     * @return
     */
    public String getFileNamePrefix() {
        return mFileNamePrefix;
    }

    /**
     * Генерирует имя кеш-файла
     * @return
     */
    public String createCacheFileName() {
        return getFileNamePrefix() + "_" + String.valueOf(mRowId);
    }
}
