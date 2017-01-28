package ru.sike.lada;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ru.sike.lada.activities.NewsActivity;
import ru.sike.lada.adapters.NewsRecyclerCursorAdapter;
import ru.sike.lada.adapters.interfaces.IOnNewsItemClickListener;
import ru.sike.lada.loaders.NewsShortCursorLoader;
import ru.sike.lada.receivers.ShortNewsListUpdateCompleteReceivers;
import ru.sike.lada.services.DataUpdateService;
import ru.sike.lada.utils.ConnectionChecker;
import ru.sike.lada.utils.SpacesItemDecoration;


/**
 * A simple {@link Fragment} subclass.
 */
public class NewsListFragment
        extends
            Fragment
        implements
            LoaderManager.LoaderCallbacks<Cursor>,
            SwipeRefreshLayout.OnRefreshListener {

    private static final String LOG_TAG = "NewsListFragment";
    private static final String CATEGORY_ID_BUNDLE_NAME = "categoryId";
    private static final String CATEGORY_NAME_BUNDLE_NAME = "categoryName";
    private static final int fetchNewsCountPerRequest = 20;
    private static final String FETCH_NEWS_FROM_BUNDLE_NAME = "fetchNewsFrom";
    private static final String FIRST_TIME_LOADED_BUNDLE_NAME = "firstTimeLoaded";
    private static final String WAIT_FOR_UPDATE_COMPLETE_BUNDLE_NAME = "waitForUpdateComplete";
    private static final int startNewsActivityRequestCode = 1;
    private static final int newsListLoaderId = 0;

    private int fetchNewsFrom = 1;
    private boolean firstTimeLoaded = false;
    private boolean isVisibleForUser = false;
    private boolean waitForUpdateComplete = false;
    static int mPreviousLastVisibleItemPostion = -1;

    // создаем BroadcastReceiver отслеживающий обновление списка новостей
    ShortNewsListUpdateCompleteReceivers mShortNewsListUpdateCompleteReceivers = new ShortNewsListUpdateCompleteReceivers() {
        // действия при получении сообщений
        public void onReceive(Context context, Intent intent) {
            ShortNewsListUpdateResult result = intent.getParcelableExtra(ShortNewsListUpdateCompleteReceivers.BROADCAST_PARAM);
            try {
                if (result.getCategoryId() == getCategoryId())
                    UpdateShortNewsListComplete(result.getStatus(), result.getMessage());
            } catch (Exception Ex) {
                Log.e(LOG_TAG, Ex.getMessage());
            }
        }
    };

    public NewsListFragment() {
        // Required empty public constructor
    }

    /**
     * Фабричный метод
     * @param categoryId Идентификатор категории
     * @param categoryName Имя категории
     * @return
     */
    public static NewsListFragment newInstance(long categoryId, String categoryName) {
        NewsListFragment result = new NewsListFragment();
        Bundle b = new Bundle();
        b.putLong(CATEGORY_ID_BUNDLE_NAME, categoryId);
        b.putString(CATEGORY_NAME_BUNDLE_NAME, categoryName);
        result.setArguments(b);
        return result;
    }

    /**
     * Запускает сервис обновления списка новостей
     * @param pCategoryId
     * @param pFetchNewsFrom Получить новость начиная с
     * @param pFetchNewsCountPerRequest Количество новостей в ответе
     */
    private void startUpdateService(long pCategoryId, int pFetchNewsFrom, int pFetchNewsCountPerRequest) {
        Log.d(LOG_TAG, "startUpdateService");
        if (!waitForUpdateComplete) {
            waitForUpdateComplete = true;
            DataUpdateService.startActionUpdateNewsShort(getContext(), pCategoryId, pFetchNewsFrom, pFetchNewsCountPerRequest);
        }
    }

    /**
     * Возвращает идентификатор категории из ассоциированного Bundle объекта
     * @return
     * @throws Exception
     */
    private long getCategoryId() throws Exception {
        Bundle args = getArguments();
        if ((args != null) && args.containsKey(CATEGORY_ID_BUNDLE_NAME)) {
            return args.getLong(CATEGORY_ID_BUNDLE_NAME);
        } else {
            throw new Exception(CATEGORY_ID_BUNDLE_NAME + " param is undefined for NewsListFragment");
        }
    }

    /**
     * Возвращает имя категории из ассоциированного Bundle объекта
     * @return
     * @throws Exception
     */
    private String getCategoryName() throws Exception {
        Bundle args = getArguments();
        if ((args != null) && args.containsKey(CATEGORY_NAME_BUNDLE_NAME)) {
            return args.getString(CATEGORY_NAME_BUNDLE_NAME);
        } else {
            throw new Exception(CATEGORY_NAME_BUNDLE_NAME + " param is undefined for NewsListFragment");
        }
    }

    /**
     * Скрывает индикатор загрузки и отображает дополнительное сообщение в случае необходимости
     * @param pShowMessage
     * @param pMessage
     */
    private void HideProgressWithMessage(boolean pShowMessage, String pMessage) {
        // останавливаем индикатор загрузки,
        // убираем индикатор загрузки из списка
        View rootView = getView();
        if (rootView != null) {
            SwipeRefreshLayout refresher = (SwipeRefreshLayout) rootView.findViewById(R.id.refresher);
            refresher.setRefreshing(false);

            RecyclerView listNews = (RecyclerView) rootView.findViewById(R.id.list_news);
            NewsRecyclerCursorAdapter adapter = (NewsRecyclerCursorAdapter) listNews.getAdapter();
            if (adapter != null)
                adapter.setFooterVisible(false);
            // если во время загрузки произошла ошибка, то оповещаем об этом пользователя
            if (pShowMessage)
                Snackbar.make(rootView, pMessage, Snackbar.LENGTH_SHORT).show();

            waitForUpdateComplete = false;
        }
    }

    /**
     * Вызывается при окончании попытки обновления списка новостей
     * @param pStatus Успешность операции
     * @param pMessage Дополнительное текстовое сообщение.
     */
    private void UpdateShortNewsListComplete(ShortNewsListUpdateCompleteReceivers.Status pStatus, String pMessage) {
        if (pStatus == ShortNewsListUpdateCompleteReceivers.Status.Success
                || pStatus == ShortNewsListUpdateCompleteReceivers.Status.Fail) {
            HideProgressWithMessage(pStatus == ShortNewsListUpdateCompleteReceivers.Status.Fail, pMessage);
        } else if (pStatus == ShortNewsListUpdateCompleteReceivers.Status.NoInternet) {
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    HideProgressWithMessage(true, getString(R.string.text_no_internet));
                }
            }, 1000);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_news_list, container, false);

        SwipeRefreshLayout refresher = (SwipeRefreshLayout) rootView.findViewById(R.id.refresher);
        if (refresher != null) {
            refresher.setOnRefreshListener(this);
            refresher.setColorSchemeResources(
                    android.R.color.holo_red_dark,
                    android.R.color.holo_green_dark,
                    android.R.color.holo_blue_dark);
        }


        RecyclerView listNews = (RecyclerView)  rootView.findViewById(R.id.list_news);
        if (listNews != null) {
            int space = (int) getResources().getDimension(R.dimen.item_news_vertical_margin);
            listNews.addItemDecoration(new SpacesItemDecoration(space));
        }

        // регистрируем BroadcastReceiver
        Activity contextActivity = getActivity();
        if (contextActivity != null)
            contextActivity.registerReceiver(mShortNewsListUpdateCompleteReceivers, new IntentFilter(ShortNewsListUpdateCompleteReceivers.BROADCAST_ACTION));

        return rootView;
    }

    @Override
    public void onDestroyView(){
        Activity contextActivity = getActivity();
        if (contextActivity != null)
            contextActivity.unregisterReceiver(mShortNewsListUpdateCompleteReceivers);
        super.onDestroyView();
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(FETCH_NEWS_FROM_BUNDLE_NAME))
                fetchNewsFrom = savedInstanceState.getInt(FETCH_NEWS_FROM_BUNDLE_NAME);
            if (savedInstanceState.containsKey(FIRST_TIME_LOADED_BUNDLE_NAME))
                firstTimeLoaded = savedInstanceState.getBoolean(FIRST_TIME_LOADED_BUNDLE_NAME);
            if (savedInstanceState.containsKey(WAIT_FOR_UPDATE_COMPLETE_BUNDLE_NAME))
                waitForUpdateComplete = savedInstanceState.getBoolean(WAIT_FOR_UPDATE_COMPLETE_BUNDLE_NAME);
        }

        Activity contextActivity = getActivity();
        if (contextActivity != null) {
            View rootView = getView();
            if (rootView != null) {
                RecyclerView listNews = (RecyclerView) rootView.findViewById(R.id.list_news);
                if (listNews != null) {
                    listNews.setLayoutManager(new LinearLayoutManager(contextActivity));
                    listNews.setAdapter(new NewsRecyclerCursorAdapter(contextActivity, null, new IOnNewsItemClickListener() {
                        @Override
                        public void onItemClick(long newsId, View view, String pTransitionImagePath) {
                            Context context = getContext();
                            if (context != null) {
                                try {
                                    NewsActivity.openNews(
                                            NewsListFragment.this, startNewsActivityRequestCode, newsId,
                                            getCategoryName(), view.findViewById(R.id.news_logo), pTransitionImagePath);
                                } catch (Exception Ex) {
                                    Log.e(LOG_TAG, Ex.getMessage());
                                }
                            }

                        }
                    }));
                    listNews.addOnScrollListener(new RecyclerView.OnScrollListener() {
                        @Override
                        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                            super.onScrolled(recyclerView, dx, dy);
                            LinearLayoutManager lm = (LinearLayoutManager) recyclerView.getLayoutManager();
                            int pastVisibleItemPosition = lm.findLastVisibleItemPosition();
                            int totalItemCount = lm.getItemCount();

                            if (mPreviousLastVisibleItemPostion < 0)
                                mPreviousLastVisibleItemPostion = pastVisibleItemPosition;

                            if (!waitForUpdateComplete && (pastVisibleItemPosition + 1 == totalItemCount)) {

                                if (pastVisibleItemPosition > mPreviousLastVisibleItemPostion) {
                                    fetchNewsFrom += fetchNewsCountPerRequest;
                                    recyclerView.post(new Runnable() {
                                        @Override
                                        public void run() {

                                            // включаем индикатор загрузки
                                            RecyclerView listNews = (RecyclerView) getView().findViewById(R.id.list_news);
                                            NewsRecyclerCursorAdapter adapter = (NewsRecyclerCursorAdapter) listNews.getAdapter();
                                            if (adapter != null)
                                                adapter.setFooterVisible(true);
                                            // запускаем сервис обновления
                                            try {
                                                startUpdateService(getCategoryId(), fetchNewsFrom, fetchNewsCountPerRequest);
                                            } catch (Exception Ex) {
                                                Log.e(LOG_TAG, Ex.getMessage());
                                            }
                                        }
                                    });
                                }
                            }

                            mPreviousLastVisibleItemPostion = pastVisibleItemPosition;
                        }
                    });
                }
            }
        }

        // отображаем новости из базы данных
        LoaderManager lm = getLoaderManager();
        if (lm != null) {
            lm.initLoader(newsListLoaderId, null, NewsListFragment.this);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(FETCH_NEWS_FROM_BUNDLE_NAME, fetchNewsFrom);
        outState.putBoolean(FIRST_TIME_LOADED_BUNDLE_NAME, firstTimeLoaded);
        outState.putBoolean(WAIT_FOR_UPDATE_COMPLETE_BUNDLE_NAME, waitForUpdateComplete);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (id == newsListLoaderId) {
            try {
                long categoryId = getCategoryId();
                return new NewsShortCursorLoader(NewsListFragment.this.getContext(), categoryId);
            } catch (Exception Ex) {
                Log.e(LOG_TAG, Ex.getMessage());
            }
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (loader.getId() == newsListLoaderId) {
            View rootView = getView();
            if (rootView != null) {
                RecyclerView listNews = (RecyclerView) rootView.findViewById(R.id.list_news);
                if (listNews != null) {
                    NewsRecyclerCursorAdapter adapter = (NewsRecyclerCursorAdapter) listNews.getAdapter();
                    if (adapter != null)
                        adapter.swapCursor(data);
                }
            }

            if (isVisibleForUser) {
                if (!firstTimeLoaded) {
                    firstTimeLoaded = true;
                    if (data.getCount() == 0) {
                        Context context = getContext();
                        if ((context != null) && ConnectionChecker.check(context))
                            onRefresh();
                    }
                }
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        if (loader.getId() == newsListLoaderId) {
            Activity contextActivity = getActivity();
            if (contextActivity == null) {
                Log.e(LOG_TAG, "context is null on onLoaderReset callback");
                return;
            }

            View rootView = getView();
            if (rootView != null) {
                RecyclerView listNews = (RecyclerView) rootView.findViewById(R.id.list_news);
                if (listNews == null)
                    return;
                NewsRecyclerCursorAdapter adapter = (NewsRecyclerCursorAdapter) listNews.getAdapter();
                adapter.swapCursor(null);
            }
        }
    }

    @Override
    public void onRefresh() {
        fetchNewsFrom = 1;
        try {
            View rootView = getView();
            if (rootView != null) {
                SwipeRefreshLayout refresher = (SwipeRefreshLayout) rootView.findViewById(R.id.refresher);
                if (!refresher.isRefreshing())
                    refresher.setRefreshing(true);
            }
            startUpdateService(getCategoryId(), fetchNewsFrom, fetchNewsCountPerRequest);
        } catch (Exception Ex) {
            Log.e(LOG_TAG, Ex.getMessage());
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == startNewsActivityRequestCode) {
            LoaderManager lm = getLoaderManager();
            if (lm != null)
                lm.restartLoader(newsListLoaderId, null, NewsListFragment.this);
        }
    }

    @Override
    public void setMenuVisibility(final boolean visible) {
        super.setMenuVisibility(visible);
        boolean prevVisibleStatus = isVisibleForUser;
        isVisibleForUser = visible;
        if (!prevVisibleStatus && visible && isAdded()) {
            LoaderManager lm = getLoaderManager();
            if (lm != null)
                getLoaderManager().restartLoader(newsListLoaderId, null, this);
        }
    }
}
