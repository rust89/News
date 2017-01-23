package ru.sike.lada.utils.parsing.abstraction;

import android.content.Context;
import android.os.Build;
import android.view.View;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Базовый элемент для представления контента новости
 */
public abstract class ViewItem {

    private static final AtomicInteger sNextGeneratedId = new AtomicInteger(1);

    protected static int generateViewId() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
            for (;;) {
                // ID number larger than 0x00FFFFFF is reserved for static
                // views defined in the /res xml files.
                final int result = sNextGeneratedId.get();
                // aapt-generated IDs have the high byte nonzero; clamp to the range under that.
                int newValue = result + 1;
                if (newValue > 0x00FFFFFF)
                    newValue = 1; // Roll over to 1, not 0.
                if (sNextGeneratedId.compareAndSet(result, newValue)) {
                    return result;
                }
            }
        } else {
            return View.generateViewId();
        }
    }

    public ViewItem() {
    }

    public abstract View getView(Context pContext);
}
