package ru.sike.lada.loaders;

import android.content.Context;
import android.net.Uri;
import android.support.v4.content.CursorLoader;

import ru.sike.lada.database.DataContract;
import ru.sike.lada.providers.LadaContent;

public class NewsCategoriesCursorLoader extends CursorLoader {
    public NewsCategoriesCursorLoader(Context context) {
        super(context, Uri.parse("content://" + LadaContent.NEWS_CATEGORY_URI_SCHEMA),
                new String[] {
                        DataContract.NewsCategories._ID,
                        DataContract.NewsCategories.COLUMN_NAME_NAME,
                        DataContract.NewsCategories.COLUMN_NAME_COLOR,
                        DataContract.NewsCategories.COLUMN_NAME_SORT_ORDER},
                null, null,
                DataContract.NewsCategories.COLUMN_NAME_SORT_ORDER + " DESC");
    }
}

