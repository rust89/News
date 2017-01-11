package ru.sike.lada.utils.parsing.abstraction;

import android.content.Context;
import android.view.View;

/**
 * Базовый элемент для представления контента новости
 */
public abstract class ViewItem {

    private Context mContext;

    public ViewItem(Context pContext) {
        mContext = pContext;
    }

    protected Context getContext() {
        return mContext;
    }

    public abstract View getView();
}
