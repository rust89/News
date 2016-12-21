package ru.sike.lada.database;

import android.provider.BaseColumns;

public class DataContract {

    // категории новостей
    public static abstract class NewsCategories implements BaseColumns {
        public static final String TABLE_NAME = "news_categories";
        public static final String COLUMN_NAME_NAME = "name";
        public static final String COLUMN_NAME_ALT_NAME = "alt_name";
        public static final String COLUMN_NAME_COLOR = "color";
        public static final String COLUMN_NAME_SORT_ORDER = "sort_order";
    }


    // краткие новости - для списков
    public static abstract class News implements BaseColumns {
        public static final String TABLE_NAME = "news";
        public static final String COLUMN_NAME_TITLE = "title";
        public static final String COLUMN_NAME_SHORT_STORY = "short_story";
        public static final String COLUMN_NAME_HTML = "html";
        public static final String COLUMN_NAME_BIG_PICTURE = "big_picture";
        public static final String COLUMN_NAME_SMALL_PICTURE = "small_picture";
        public static final String COLUMN_NAME_AUTHOR = "author";
        public static final String COLUMN_NAME_DATE = "date";
        public static final String COLUMN_NAME_COMM_NUM = "comm_num";
        public static final String COLUMN_NAME_VIEWS = "views";
        public static final String COLUMN_NAME_SOURCE = "source";
        public static final String COLUMN_NAME_SOURCE_NAME = "source_name";
        public static final String COLUMN_NAME_FULL_LINK = "full_link";
        public static final String COLUMN_NAME_CATEGORY_NAME = "category_name";
        public static final String COLUMN_NAME_PR = "pr";
        public static final String COLUMN_NAME_SMALL_PICTURE_CACHE_PATH = "small_picture_cache_path";
        public static final String COLUMN_NAME_BIG_PICTURE_CACHE_PATH = "big_picture_cache_path";
        public static final String COLUMN_NAME_SMALL_PICTURE_CACHE_MD5 = "small_picture_cache_md5";
        public static final String COLUMN_NAME_BIG_PICTURE_CACHE_MD5= "big_picture_cache_md5";
        public static final String COLUMN_NAME_SMALL_PICTURE_CACHE_LAST_UPDATE = "small_picture_cache_last_update";
        public static final String COLUMN_NAME_BIG_PICTURE_CACHE_LAST_UPDATE= "big_picture_cache_last_update";
    }

    public static abstract class NewsCategoryBinder implements BaseColumns {
        public static final String TABLE_NAME = "news_category_binder";
        public static final String COLUMN_NAME_NEWS_ID = "news_id";
        public static final String COLUMN_NAME_CATEGORY_ID = "category_id";
    }

    public static abstract class NewsBookmarks implements BaseColumns {
        public static final String TABLE_NAME = "news_bookmarks";
        public static final String COLUMN_NAME_NEWS_ID = "news_id";
    }

}
