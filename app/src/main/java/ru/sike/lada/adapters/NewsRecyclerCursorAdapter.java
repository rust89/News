package ru.sike.lada.adapters;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;

import ru.sike.lada.R;
import ru.sike.lada.database.DataContract;
import ru.sike.lada.utils.DateUtils;
import ru.sike.lada.utils.DrawableUtils;
import ru.sike.lada.utils.caching.CacheExecutorProvider;
import ru.sike.lada.utils.caching.CacheTaskFactory;

public class NewsRecyclerCursorAdapter extends CursorRecyclerViewAdapter<RecyclerView.ViewHolder>{

    private static final String LOG_TAG = "NsRrCursorAdapter";

    private IOnItemClickListener mOnItemClickListener;
    private String mToday;
    private String mYesterday;
    private String mDayBeforeYesterday;
    private boolean mFooterVisible = false;

    public NewsRecyclerCursorAdapter(Context pContext, Cursor pCursor, IOnItemClickListener pOnItemClickListener){
        super(pContext, pCursor);
        mOnItemClickListener = pOnItemClickListener;
        mToday = pContext.getString(R.string.text_today);
        mYesterday = pContext.getString(R.string.text_yesterday);
        mDayBeforeYesterday = pContext.getString(R.string.text_day_before_yesterday);
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        public ImageView logo;
        public TextView title;
        public TextView pr;
        public TextView views;
        public TextView date;

        public ImageView bookmark;

        public ItemViewHolder(View view, Context context) {
            super(view);
            logo = (ImageView) view.findViewById(R.id.news_logo);
            title = (TextView) view.findViewById(R.id.news_title);
            pr = (TextView) view.findViewById(R.id.news_pr);
            views = (TextView) view.findViewById(R.id.news_views);
            date = (TextView) view.findViewById(R.id.news_date);
            bookmark = (ImageView) view.findViewById(R.id.news_bookmark);

            Drawable d = context.getResources().getDrawable(R.drawable.ic_eye_black_24dp);
            views.setCompoundDrawablesWithIntrinsicBounds(
                    DrawableUtils.getTinted(d, context.getResources().getColor(R.color.newsItemFooterFontColor)),
                    null, null, null);
        }
    }
    public static class ItemSpinner extends RecyclerView.ViewHolder {
        public ItemSpinner(View view) {
            super(view);
        }
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
        if (viewType == Type.FIRST_ITEM.ordinal()) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_news_first, parent, false);
            vh = new ItemViewHolder(itemView, mContext);

        } else if (viewType == Type.ITEM.ordinal()) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_news, parent, false);
            vh = new ItemViewHolder(itemView, mContext);
        } else if (viewType == Type.FOOTER.ordinal()) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_spinner, parent, false);
            vh = new ItemSpinner(itemView);
        } else {
            vh = null;
        }

        return vh;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder pViewHolder, Cursor pCursor) {
        if (pViewHolder instanceof ItemViewHolder) {

            // получаем значения полей
            ItemViewHolder viewHolder = (ItemViewHolder)pViewHolder;
            final long id = pCursor.getLong(pCursor.getColumnIndex(DataContract.News._ID));
            String title = pCursor.getString(pCursor.getColumnIndex(DataContract.News.COLUMN_NAME_TITLE));
            int pr = pCursor.getInt(pCursor.getColumnIndex(DataContract.News.COLUMN_NAME_PR));
            String smallImageUrl = pCursor.getString(pCursor.getColumnIndex(DataContract.News.COLUMN_NAME_SMALL_PICTURE));
            final String smallImageCachePath = pCursor.getString(pCursor.getColumnIndex(DataContract.News.COLUMN_NAME_SMALL_PICTURE_CACHE_PATH));
            String bigImageUrl = pCursor.getString(pCursor.getColumnIndex(DataContract.News.COLUMN_NAME_BIG_PICTURE));
            final String bigImageCachePath = pCursor.getString(pCursor.getColumnIndex(DataContract.News.COLUMN_NAME_BIG_PICTURE_CACHE_PATH));
            int viewCount = pCursor.getInt(pCursor.getColumnIndex(DataContract.News.COLUMN_NAME_VIEWS));
            long date = pCursor.getLong(pCursor.getColumnIndex(DataContract.News.COLUMN_NAME_DATE));
            int bookmark = pCursor.getInt(pCursor.getColumnIndex("bookmarked"));

            // для первой записи загружаем большую картинку
            if (pCursor.getPosition() == 0) {
                if (bigImageCachePath != null && !bigImageCachePath.isEmpty()) {
                    viewHolder.logo.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    viewHolder.logo.setImageURI(Uri.fromFile(new File(bigImageCachePath)));
                } else {
                    viewHolder.logo.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                    viewHolder.logo.setImageResource(R.drawable.ic_newspaper_black_24dp);
                }
                CacheExecutorProvider.Execute(mContext, CacheTaskFactory.getNewsBigImageCacheTask(id));
            } else {
                if (smallImageCachePath != null && !smallImageCachePath.isEmpty()) {
                    viewHolder.logo.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    viewHolder.logo.setImageURI(Uri.fromFile(new File(smallImageCachePath)));
                } else {
                    viewHolder.logo.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                    viewHolder.logo.setImageResource(R.drawable.ic_newspaper_black_24dp);
                }
                CacheExecutorProvider.Execute(mContext, CacheTaskFactory.getNewsSmallImageCacheTask(id));
            }

            // устанавливаем поля
            viewHolder.title.setText(title);
            viewHolder.title.setMaxLines(pr > 0 ? 2 : 3);
            viewHolder.pr.setVisibility(pr > 0 ? View.VISIBLE : View.GONE);
            viewHolder.views.setText(String.valueOf(viewCount));
            viewHolder.date.setText(DateUtils.formatDate(date, mToday, mYesterday, mDayBeforeYesterday));

            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mOnItemClickListener != null)
                        mOnItemClickListener.onItemClick(id, view,
                                (bigImageCachePath != null && !bigImageCachePath.isEmpty() ? bigImageCachePath : smallImageCachePath) );
                }
            });

            Drawable d = mContext.getResources().getDrawable(R.drawable.ic_bookmark_black_24dp);
            if (bookmark > 0) {
                viewHolder.bookmark.setImageDrawable(DrawableUtils.getTinted(d, mContext.getResources().getColor(R.color.colorPrimary)));
            } else {
                viewHolder.bookmark.setImageDrawable(DrawableUtils.getTinted(d, mContext.getResources().getColor(R.color.newsItemFooterFontColor)));
            }
        }
    }

    @Override
    public boolean hasFooter() {
        return mFooterVisible;
    }

    public void setFooterVisible(boolean pVisible) {
        if (mFooterVisible != pVisible) {
            // get item count before footer enable/disable
            final int positionStart = getItemCount();
            mFooterVisible = pVisible;
            if (mFooterVisible) {
                notifyItemRangeInserted(positionStart + 1, 1);
            } else {
                notifyItemRangeRemoved(positionStart + 1, 1);
            }
        }
    }

    public interface IOnItemClickListener {
        void onItemClick(long newsId, View view, String pTransitionImagePath);
    }

}