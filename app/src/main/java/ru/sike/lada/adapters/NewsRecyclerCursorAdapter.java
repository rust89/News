package ru.sike.lada.adapters;

import android.content.Context;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import ru.sike.lada.R;
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
        setHasStableIds(true);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
        if (viewType == Type.FIRST_ITEM.ordinal()) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_news_first, parent, false);
            vh = new NewsItemViewHolder(itemView, mContext);
        } else if (viewType == Type.ITEM.ordinal()) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_news, parent, false);
            vh = new NewsItemViewHolder(itemView, mContext);
        } else if (viewType == Type.FOOTER.ordinal()) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_spinner, parent, false);
            vh = new NewsSpinnerViewHolder(itemView);
        } else {
            vh = null;
        }

        return vh;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder pViewHolder, Cursor pCursor) {
        if (pViewHolder instanceof NewsItemViewHolder) {

            // получаем значения полей
            NewsItemViewHolder viewHolder = (NewsItemViewHolder)pViewHolder;

            final NewItemCursorWrapper newsItem = new NewItemCursorWrapper(pCursor, new NewsItemColumnMapHelper(pCursor));

            // для первой записи загружаем большую картинку
            if (pCursor.getPosition() == 0) {
                if (!newsItem.getBigImageCachePath().isEmpty()) {
                    viewHolder.getLogo().setScaleType(ImageView.ScaleType.FIT_CENTER);
                    viewHolder.getLogo().setSource(newsItem.getBigImageCachePath(), newsItem.getBigImageCacheMd5());
                } else {
                    viewHolder.getLogo().setImageDrawable(null);
                }
                CacheExecutorProvider.Execute(mContext, CacheTaskFactory.getNewsBigImageCacheTask(newsItem.getId()));
            } else {
                if (newsItem.getSmallImageCachePath().isEmpty()) {
                    viewHolder.getLogo().setScaleType(ImageView.ScaleType.CENTER_CROP);
                    viewHolder.getLogo().setSource(newsItem.getSmallImageCachePath(), newsItem.getSmallImageCacheMd5());
                } else {
                    viewHolder.getLogo().setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                    viewHolder.getLogo().setImageResource(R.drawable.ic_newspaper_black_24dp);
                }
                CacheExecutorProvider.Execute(mContext, CacheTaskFactory.getNewsSmallImageCacheTask(newsItem.getId()));
            }

            // устанавливаем поля
            viewHolder.getTitle().setText(newsItem.getTitle());
            viewHolder.getTitle().setMaxLines(newsItem.isPr() ? 2 : 3);
            viewHolder.getPr().setVisibility(newsItem.isPr() ? View.VISIBLE : View.GONE);
            viewHolder.getViewCount().setText(String.valueOf(newsItem.getViewCount()));
            viewHolder.getDate().setText(DateUtils.formatDate(newsItem.getDate(), mToday, mYesterday, mDayBeforeYesterday));

            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mOnItemClickListener != null)
                        mOnItemClickListener.onItemClick(newsItem.getId(), view,
                                (!newsItem.getBigImageCachePath().isEmpty() ? newsItem.getBigImageCachePath() : newsItem.getSmallImageCachePath()) );
                }
            });

            Drawable d = mContext.getResources().getDrawable(R.drawable.ic_bookmark_black_24dp);
            if (newsItem.getBookmark()) {
                viewHolder.getBookmark().setImageDrawable(DrawableUtils.getTinted(d, mContext.getResources().getColor(R.color.colorPrimary)));
            } else {
                viewHolder.getBookmark().setImageDrawable(DrawableUtils.getTinted(d, mContext.getResources().getColor(R.color.newsItemFooterFontColor)));
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

    @Override
    public Cursor swapCursor(Cursor newCursor) {
        Cursor result = super.swapCursor(newCursor);
        return result;
    }

    public interface IOnItemClickListener {
        void onItemClick(long newsId, View view, String pTransitionImagePath);
    }

}