package ru.sike.lada.utils.parsing;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import ru.sike.lada.utils.parsing.abstraction.ViewItem;

public class ImageViewItem extends ViewItem  {

    private String mPreviewImage;
    private String mFullImage;

    public ImageViewItem(String pPreviewImage, String pFullImage) {
        super();
        mPreviewImage = pPreviewImage;
        mFullImage = pFullImage;
    }

    @Override
    public View getView(Context pContext) {
        ImageView result = new ImageView(pContext);
        result.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        result.setAdjustViewBounds(true);
        result.setScaleType(ImageView.ScaleType.FIT_CENTER);
        Glide.with(pContext)
             .load(mPreviewImage)
             .diskCacheStrategy(DiskCacheStrategy.NONE)
             .skipMemoryCache(true)
             .into(result);
        return result;
    }
}
