package ru.sike.lada.adapters;


import android.content.Context;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import ru.sike.lada.R;
import ru.sike.lada.database.DataContract;
import ru.sike.lada.utils.DrawableUtils;

public class BookmarkCursorAdapter extends CursorRecyclerViewAdapter<BookmarkCursorAdapter.ViewHolder> {

    private IOnItemClickListener onItemClickListener;

    public BookmarkCursorAdapter(Context context, Cursor cursor, IOnItemClickListener onItemClickListener){
        super(context,cursor);
        this.onItemClickListener = onItemClickListener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView logo;
        public TextView title;
        public TextView pr;
        public TextView views;
        public TextView date;

        public ViewHolder(View view, Context context) {
            super(view);
            logo = (ImageView) view.findViewById(R.id.news_logo);
            title = (TextView) view.findViewById(R.id.news_title);
            pr = (TextView) view.findViewById(R.id.news_pr);
            views = (TextView) view.findViewById(R.id.news_views);
            date = (TextView) view.findViewById(R.id.news_date);

            Drawable d = context.getResources().getDrawable(R.drawable.ic_eye_black_24dp);
            views.setCompoundDrawablesWithIntrinsicBounds(
                    DrawableUtils.getTinted(d, context.getResources().getColor(R.color.newsItemFooterFontColor)),
                    null, null, null);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_news_bookmark, parent, false);
        return new ViewHolder(itemView, mContext);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, Cursor cursor) {
        final long id = cursor.getLong(cursor.getColumnIndex(DataContract.News._ID));
        String title = cursor.getString(cursor.getColumnIndex(DataContract.News.COLUMN_NAME_TITLE));
        int pr = cursor.getInt(cursor.getColumnIndex(DataContract.News.COLUMN_NAME_PR));
        String smallImageUrl = cursor.getString(cursor.getColumnIndex(DataContract.News.COLUMN_NAME_SMALL_PICTURE));
        int viewCount = cursor.getInt(cursor.getColumnIndex(DataContract.News.COLUMN_NAME_VIEWS));
        long date = cursor.getLong(cursor.getColumnIndex(DataContract.News.COLUMN_NAME_DATE));

        viewHolder.title.setText(title);
        viewHolder.title.setMaxLines(pr > 0 ? 2 : 3);
        viewHolder.pr.setVisibility(pr > 0 ? View.VISIBLE : View.GONE);
        Glide.with(mContext).load(smallImageUrl).diskCacheStrategy(DiskCacheStrategy.SOURCE).into(viewHolder.logo);
        viewHolder.views.setText(String.valueOf(viewCount));
        viewHolder.date.setText(formatNewsDate(date));

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onItemClickListener != null)
                    onItemClickListener.onItemClick(id, view);
            }
        });
    }

    @Override
    public boolean hasFooter() {
        return false;
    }


    private String formatNewsDate(long timestamp) {
        Date dateTime = new Date(timestamp);

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(dateTime);

        Calendar today = Calendar.getInstance();
        Calendar yesterday = Calendar.getInstance();
        Calendar beforeYesterday = Calendar.getInstance();

        yesterday.add(Calendar.DATE, -1);
        beforeYesterday.add(Calendar.DATE, -2);

        DateFormat timeFormatter = new SimpleDateFormat("HH:mm", Locale.getDefault());
        DateFormat dateFormatterWithoutYear = new SimpleDateFormat("dd MMMM HH:mm", Locale.getDefault());
        DateFormat dateFormatterWithYear = new SimpleDateFormat("dd MMMM yyyy", Locale.getDefault());

        if (calendar.get(Calendar.YEAR) == today.get(Calendar.YEAR) && calendar.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR)) {
            return "Сегодня " + timeFormatter.format(dateTime);
        } else if (calendar.get(Calendar.YEAR) == yesterday.get(Calendar.YEAR) && calendar.get(Calendar.DAY_OF_YEAR) == yesterday.get(Calendar.DAY_OF_YEAR)) {
            return "Вчера " + timeFormatter.format(dateTime);
        } else if (calendar.get(Calendar.YEAR) == beforeYesterday.get(Calendar.YEAR) && calendar.get(Calendar.DAY_OF_YEAR) == beforeYesterday.get(Calendar.DAY_OF_YEAR)) {
            return "Позавчера " + timeFormatter.format(dateTime);
        } else if (calendar.get(Calendar.YEAR) == today.get(Calendar.YEAR)) {
            return dateFormatterWithoutYear.format(dateTime);
        } else {
            return dateFormatterWithYear.format(dateTime);
        }
    }

    public interface IOnItemClickListener {
        void onItemClick(long newsId, View view);
    }

}
