package ru.sike.lada.utils.parsing.abstraction;

import android.content.Context;
import android.view.View;

/**
 * Базовый элемент для представления контента новости
 */
public abstract class ViewItem {

    public ViewItem() {
    }

    public abstract View getView(Context pContext);
}
