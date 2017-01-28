package ru.sike.lada.adapters.holders;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import ru.sike.lada.R;
import ru.sike.lada.ui.ImageViewEx;
import ru.sike.lada.utils.DrawableUtils;

public class NewsItemViewHolder extends RecyclerView.ViewHolder {
    private ImageViewEx mLogo;
    private TextView mTitle;
    private TextView mPr;
    private TextView mViewCount;
    private TextView mDate;
    private ImageView mBookmark;

    public NewsItemViewHolder(View view, Context context) {
        super(view);
        mLogo = (ImageViewEx) view.findViewById(R.id.news_logo);
        mTitle = (TextView) view.findViewById(R.id.news_title);
        mPr = (TextView) view.findViewById(R.id.news_pr);
        mViewCount = (TextView) view.findViewById(R.id.news_views);
        mDate = (TextView) view.findViewById(R.id.news_date);
        mBookmark = (ImageView) view.findViewById(R.id.news_bookmark);

        Drawable d = context.getResources().getDrawable(R.drawable.ic_eye_black_24dp);
        mViewCount.setCompoundDrawablesWithIntrinsicBounds(
                DrawableUtils.getTinted(d, context.getResources().getColor(R.color.newsItemFooterFontColor)),
                null, null, null);
    }

    public ImageViewEx getLogo() {
        return mLogo;
    }

    public TextView getTitle() {
        return mTitle;
    }

    public TextView getPr() {
        return mPr;
    }

    public TextView getViewCount() {
        return mViewCount;
    }

    public TextView getDate() {
        return mDate;
    }

    public ImageView getBookmark() {
        return mBookmark;
    }
}