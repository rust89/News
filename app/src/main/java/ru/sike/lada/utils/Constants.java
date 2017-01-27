package ru.sike.lada.utils;

public final class Constants {

    /**
     * Период в течении которого обновление скешированной картинки для новости не требуется
     */
    public static final long news_image_cache_expire_period = 1000 * 60 * 120; // 120 минут

    /**
     * Доменное имя сервера
     */
    public static final String backend_domain_name = "www.lada.kz"; // "www.7292.kz"

    /**
     * Адрес сервера
     */
    public static final String backend_url = "http://" + backend_domain_name + "/";
}
