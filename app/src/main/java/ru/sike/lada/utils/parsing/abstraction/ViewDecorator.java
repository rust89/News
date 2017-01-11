package ru.sike.lada.utils.parsing.abstraction;

import android.content.Context;
import android.view.View;

public abstract class ViewDecorator {

    private Context mContext;

    public ViewDecorator(Context pContext) {
        mContext = pContext;
    }

    protected Context getContext() {
        return mContext;
    }

    public abstract void Decorate(View view);
}
