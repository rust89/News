package ru.sike.lada.services.updaters;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;

import java.util.List;

import ru.sike.lada.backend.json.Category;
import ru.sike.lada.database.DataContract;
import ru.sike.lada.providers.LadaContent;

public class CategoryUpdater extends BaseUpdater {

    public CategoryUpdater(Context context) {
        super(context);
    }

    public void Update(List<Category> list) {
        String idList = "";
        for (Category item : list) {
            idList += (idList.length() > 0 ? ", " : "") + String.valueOf(item.getId());
            Uri uri = Uri.withAppendedPath(Uri.parse("content://" + LadaContent.NEWS_CATEGORY_URI_SCHEMA), String.valueOf(item.getId()));
            final ContentResolver contentResolver = context.getContentResolver();
            ContentValues values = packContent(item);
            if (isRowExists(uri, DataContract.NewsCategories._ID)) {
                Uri updateUri = Uri.withAppendedPath(Uri.parse("content://" + LadaContent.NEWS_CATEGORY_URI_SCHEMA), String.valueOf(item.getId()));
                contentResolver.update(updateUri, values, null, null);
            } else {
                Uri insertUri = Uri.parse("content://" + LadaContent.NEWS_CATEGORY_URI_SCHEMA);
                contentResolver.insert(insertUri, values);
            }
        }

        // удаляем несуществующие, исключая главную вкладку
        idList += (idList.length() > 0 ? ", " : "") + String.valueOf(Long.MAX_VALUE);
        Uri uri = Uri.parse("content://" + LadaContent.NEWS_CATEGORY_URI_SCHEMA);
        final ContentResolver contentResolver = context.getContentResolver();
        contentResolver.delete(uri, "(" + DataContract.NewsCategories._ID + " NOT IN (" + idList + "))", null);
    }

    private ContentValues packContent(Category item) {
        ContentValues result = new ContentValues();
        result.put(DataContract.NewsCategories._ID, item.getId());
        result.put(DataContract.NewsCategories.COLUMN_NAME_NAME, item.getName());
        result.put(DataContract.NewsCategories.COLUMN_NAME_ALT_NAME, item.getAltName());
        result.put(DataContract.NewsCategories.COLUMN_NAME_COLOR, item.getColor());
        result.put(DataContract.NewsCategories.COLUMN_NAME_SORT_ORDER, item.getSortOrder());
        return result;
    }

}
