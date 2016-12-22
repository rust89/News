package ru.sike.lada.ui;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

public class ImageViewEx extends ImageView {

    private String mPath;
    private String mMd5;

    public ImageViewEx(Context pContext) {
        this(pContext, null, 0);
    }

    public ImageViewEx(Context pContext, AttributeSet pAttrs) {
        this(pContext, pAttrs, 0);
    }


    public ImageViewEx(Context pContext, AttributeSet pAttrs, int pDefStyleAttr) {
        super(pContext, pAttrs, pDefStyleAttr);
        mPath = "";
        mMd5 = "";
    }

    public void setSource(String pPath, String pMd5) {
        if (!mPath.equals(pPath) || !mMd5.equals(pMd5)) {
            mPath = pPath;
            mMd5 = pMd5;
            setImageDrawable(Drawable.createFromPath(mPath));
        }
    }

    @Override
    public void setImageResource(int resId) {
        super.setImageResource(resId);
        mPath = "";
        mMd5 = "";
    }
}
