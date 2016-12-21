package ru.sike.lada;


import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ru.sike.lada.adapters.BookmarkCursorAdapter;
import ru.sike.lada.adapters.NewsRecyclerCursorAdapter;
import ru.sike.lada.loaders.BookmarkCursorLoader;
import ru.sike.lada.loaders.BookmarkedListCursorLoader;
import ru.sike.lada.loaders.NewsShortCursorLoader;
import ru.sike.lada.utils.ConnectionChecker;
import ru.sike.lada.utils.SpacesItemDecoration;


public class BookmarkFragment
        extends
            Fragment
        implements
            LoaderManager.LoaderCallbacks<Cursor> {

    private static final int BOOKMARK_LOADER = 1;

    public BookmarkFragment() {
        // Required empty public constructor
    }

    public static BookmarkFragment newInstance() {
        return new BookmarkFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_bookmark, container, false);

        RecyclerView listNews = (RecyclerView)  rootView.findViewById(R.id.list_news);
        if (listNews != null) {
            int space = (int) getResources().getDimension(R.dimen.item_news_vertical_margin);
            listNews.addItemDecoration(new SpacesItemDecoration(space));
        }

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Activity contextActivity = getActivity();
        // настройка вкладок
        if (contextActivity != null) {
            TabLayout tabLayout = (TabLayout) contextActivity.findViewById(R.id.tab_layout);
            if (tabLayout != null)
                tabLayout.setVisibility(View.GONE);
            View rootView = getView();
            if (rootView != null) {
                RecyclerView listNews = (RecyclerView) rootView.findViewById(R.id.list_news);
                if (listNews != null) {
                    listNews.setLayoutManager(new LinearLayoutManager(contextActivity));
                    listNews.setAdapter(new BookmarkCursorAdapter(contextActivity, null, new BookmarkCursorAdapter.IOnItemClickListener() {
                        @Override
                        public void onItemClick(long newsId, View view) {
                            /*
                            Context context = getContext();
                            if (context != null) {
                                try {
                                    NewsActivity.openNews(NewsListFragment.this, startNewsActivityRequestCode,
                                            newsId, getCategoryName(), view.findViewById(R.id.news_logo));
                                } catch (Exception Ex) {
                                    Log.e(LOG_TAG, Ex.getMessage());
                                }
                            }
                            */
                        }
                    }));
                }
            }
        }

        // отображаем новости из базы данных
        LoaderManager lm = getLoaderManager();
        if (lm != null)
            lm.initLoader(BOOKMARK_LOADER, null, BookmarkFragment.this).forceLoad();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (id == BOOKMARK_LOADER) {
            return new BookmarkedListCursorLoader(BookmarkFragment.this.getContext());
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (loader.getId() == BOOKMARK_LOADER) {
            View rootView = getView();
            if (rootView != null) {
                RecyclerView listNews = (RecyclerView) rootView.findViewById(R.id.list_news);
                if (listNews != null) {
                    BookmarkCursorAdapter adapter = (BookmarkCursorAdapter) listNews.getAdapter();
                    if (adapter != null)
                        adapter.swapCursor(data);
                }
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
