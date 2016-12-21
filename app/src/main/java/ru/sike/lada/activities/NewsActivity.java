package ru.sike.lada.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import ru.sike.lada.R;
import ru.sike.lada.loaders.BookmarkCursorLoader;
import ru.sike.lada.loaders.NewsFullCursorLoader;
import ru.sike.lada.models.News;
import ru.sike.lada.receivers.FullNewsUpdateReceiver;
import ru.sike.lada.services.DataUpdateService;
import ru.sike.lada.services.NewsHelperService;
import ru.sike.lada.utils.ConnectionChecker;
import ru.sike.lada.utils.DateUtils;
import ru.sike.lada.utils.DrawableUtils;
import ru.sike.lada.utils.NewsContentBuilder;

public class NewsActivity
        extends
            AppCompatActivity
        implements
            LoaderManager.LoaderCallbacks<Cursor> {

    private static final int NEWS_LOADER = 1;
    private static final int BOOKMARK_LOADER = 2;

    enum BookmarkStatus {
        UNDEFINED,
        ADDED,
        NOT_ADDED
    }

    private static final String LOG_TAG = "NewsActivity";
    private static final String EXTRA_NEWS_ID = "EXTRA_NEWS_ID";
    private static final String EXTRA_CATEGORY_NAME = "EXTRA_CATEGORY_NAME";
    private static final String TRANSITION_NAME = "NEWS_OPEN_TRANSITION";
    private static final String EXTRA_TRANSITION_IMAGE_PATH = "EXTRA_TRANSITION_IMAGE_PATH";
    private static final String NEWS_BUNDLE_NAME = "NEWS_BUNDLE";
    private BookmarkStatus currentBookMarkStatus = BookmarkStatus.UNDEFINED;
    private News mNews = null;
    FullNewsUpdateReceiver mFullNewsUpdateReceiver = new FullNewsUpdateReceiver() {
        // действия при получении сообщений
        public void onReceive(Context context, Intent intent) {
            FullNewsUpdateResult result = intent.getParcelableExtra(FullNewsUpdateReceiver.BROADCAST_PARAM);
            if (result.getNewsId() == getNewsId())
                UpdateFullNewsComplete(result.isIsSuccessfull(), result.getMessage());
        }
    };

    private long getNewsId() {
        long defaultNewsId = -1;
        Intent intent = getIntent();
        if (intent != null)
            return intent.getLongExtra(EXTRA_NEWS_ID, defaultNewsId);
        return defaultNewsId;
    }

    private String getCategoryName() {
        String defaultCategoryName = "";
        Intent intent = getIntent();
        if (intent != null)
            return intent.getStringExtra(EXTRA_CATEGORY_NAME);
        return defaultCategoryName;
    }

    public static void openNews(Fragment fragment, int requestCode, long newsId, String categoryName, View sourceView, String pTransitionImagePath) {

        Context context = fragment.getContext();
        if ((context != null) && (context instanceof Activity)) {

            Activity contextActivity = (Activity) context;

            Intent intent = new Intent(contextActivity, NewsActivity.class);
            intent.putExtra(EXTRA_NEWS_ID, newsId);
            intent.putExtra(EXTRA_CATEGORY_NAME, categoryName);
            intent.putExtra(EXTRA_TRANSITION_IMAGE_PATH, pTransitionImagePath);

            ViewCompat.setTransitionName(sourceView, TRANSITION_NAME);
            ActivityOptionsCompat options = ActivityOptionsCompat
                    .makeSceneTransitionAnimation(contextActivity, sourceView, TRANSITION_NAME);
            fragment.startActivityForResult(intent, requestCode, options.toBundle());
        }
    }

    protected void setHeaderImage(String pPath) {
        Drawable transitionDrawable = Drawable.createFromPath(pPath);
        if (transitionDrawable != null) {
            ImageView newsLogo = (ImageView) findViewById(R.id.news_logo);
            AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.app_bar);

            // извлекаем размеры картинки
            int transitionDrawableWidth = transitionDrawable.getIntrinsicWidth();
            int transitionDrawableHeight = transitionDrawable.getIntrinsicHeight();
            // вычисляем пропорцию сторон
            float drawableProportion = ((float)transitionDrawableWidth) / transitionDrawableHeight;
            // вычисляем новую высоту appBar-а исходя из вычисленной пропорции картинки
            int appBarWidth = getResources().getDisplayMetrics().widthPixels;
            int appBarHeight = (int) Math.floor(appBarWidth / drawableProportion);
            // устанавливаем новые размеры appBar-а
            CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) appBarLayout.getLayoutParams();
            layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
            layoutParams.height = appBarHeight;
            appBarLayout.setLayoutParams(layoutParams);
            // устнавливаем картинку
            newsLogo.setImageDrawable(transitionDrawable);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(NEWS_BUNDLE_NAME))
                mNews = savedInstanceState.getParcelable(NEWS_BUNDLE_NAME);
        }

        setContentView(R.layout.activity_news);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(getCategoryName());
        }

        ImageView newsLogo = (ImageView) findViewById(R.id.news_logo);
        Intent intent = getIntent();
        if (intent != null) {
            if (intent.hasExtra(EXTRA_TRANSITION_IMAGE_PATH)) {
                String transitionImagePath = intent.getStringExtra(EXTRA_TRANSITION_IMAGE_PATH);
                if (transitionImagePath != null && !transitionImagePath.isEmpty())
                    setHeaderImage(transitionImagePath);
            }
        }
        ViewCompat.setTransitionName(newsLogo, TRANSITION_NAME);

        // регистрируем BroadcastReceiver
        registerReceiver(mFullNewsUpdateReceiver, new IntentFilter(FullNewsUpdateReceiver.BROADCAST_ACTION));
        // загружаем новость
        if (mNews != null) {
            InitActivityView(mNews);
            LoaderManager lm = getSupportLoaderManager();
            if (lm != null)
                lm.initLoader(BOOKMARK_LOADER, null, NewsActivity.this);
        } else {
            // Если есть соедиение, то сначала обновляем новость
            // Если соединения нет, то пробуем загрузить данные из БД
            if (ConnectionChecker.check(this)) {
                DataUpdateService.startActionUpdateNewsFull(this, getNewsId());
            } else {
                // запускаем загрузку категорий из базы данных
                LoaderManager lm = getSupportLoaderManager();
                if (lm != null) {
                    lm.initLoader(NEWS_LOADER, null, NewsActivity.this);
                    lm.initLoader(BOOKMARK_LOADER, null, NewsActivity.this);
                }
            }
        }
    }

    @Override
    protected void onDestroy () {
        unregisterReceiver(mFullNewsUpdateReceiver);
        super.onDestroy();
    }

    private void UpdateFullNewsComplete(boolean pResult, String pMessage) {
        if (pResult) {
            // запускаем загрузку категорий из базы данных
            LoaderManager lm = getSupportLoaderManager();
            if (lm != null) {
                lm.initLoader(NEWS_LOADER, null, NewsActivity.this);
                lm.initLoader(BOOKMARK_LOADER, null, NewsActivity.this);
            }
        } else {
            Log.e(LOG_TAG, pMessage);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_news, menu);
        for (int i = 0; i < menu.size(); i++) {
            MenuItem item = menu.getItem(i);
            if (item.getItemId() == R.id.action_news_add_bookmark) {
                if ((currentBookMarkStatus == BookmarkStatus.ADDED) || (currentBookMarkStatus == BookmarkStatus.UNDEFINED))
                    item.setVisible(false);
            } else if (item.getItemId() == R.id.action_news_remove_bookmark) {
                item.setIcon(DrawableUtils.getTinted(item.getIcon(), getResources().getColor(R.color.colorAccent)));
                if ((currentBookMarkStatus == BookmarkStatus.NOT_ADDED) || (currentBookMarkStatus == BookmarkStatus.UNDEFINED))
                    item.setVisible(false);
            }

        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        switch(item.getItemId()) {
            case android.R.id.home: {
                onBackPressed();
                return true;
            }
            case R.id.action_news_add_bookmark: {
                NewsHelperService.startActionAddToBookmarks(this, getNewsId(), 1);
                return true;
            }
            case R.id.action_news_remove_bookmark: {
                NewsHelperService.startActionAddToBookmarks(this, getNewsId(), 0);
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (id == NEWS_LOADER) {
            return new NewsFullCursorLoader(NewsActivity.this, getNewsId());
        } else if (id == BOOKMARK_LOADER) {
            return new BookmarkCursorLoader(NewsActivity.this, getNewsId());
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (loader.getId() == NEWS_LOADER) {
            // инициализация активности
            if (data.moveToFirst()) {
                mNews = new News(data);
                InitActivityView(mNews);
            }

        } else if (loader.getId() == BOOKMARK_LOADER) {
            BookmarkStatus prevStatus = currentBookMarkStatus;
            if (data.moveToFirst())
                currentBookMarkStatus = BookmarkStatus.ADDED;
            else
                currentBookMarkStatus = BookmarkStatus.NOT_ADDED;

            if (prevStatus != currentBookMarkStatus) {
                invalidateOptionsMenu();
                if (prevStatus != BookmarkStatus.UNDEFINED) {
                    if (currentBookMarkStatus == BookmarkStatus.ADDED)
                        Snackbar.make(findViewById(android.R.id.content), R.string.activity_news_msg_bookmark_add, Snackbar.LENGTH_LONG)
                                .setAction(R.string.text_cancel, new View.OnClickListener() {
                                    @Override
                                    public void onClick(View pView) {
                                        NewsHelperService.startActionAddToBookmarks(NewsActivity.this, getNewsId(), 0);
                                    }
                                })
                                .show();
                    else if (currentBookMarkStatus == BookmarkStatus.NOT_ADDED)
                        Snackbar.make(findViewById(android.R.id.content), R.string.activity_news_msg_bookmark_remove, Snackbar.LENGTH_SHORT).show();
                }
            }

        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // Called when a previously created loader is being reset, and thus making its data unavailable.
    }

    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mNews != null)
            outState.putParcelable(NEWS_BUNDLE_NAME, mNews);
    }

    private void InitActivityView(News pNewsItem) {
        // устанавливаем картинку для новости
        String pictureCachePath = pNewsItem.getBigPictureCachePath();
        if (pictureCachePath != null && !pictureCachePath.isEmpty())
            setHeaderImage(pictureCachePath);
        //Glide.with(this).load(pNewsItem.getBigPicture()).diskCacheStrategy(DiskCacheStrategy.SOURCE).into(newsTitleImage);

        // устнавливаем категорию для новости
        TextView newsCategory = (TextView) findViewById(R.id.news_category);
        newsCategory.setText(pNewsItem.getCategoryName());

        // устанавливаем дату новости
        TextView newsDate = (TextView) findViewById(R.id.news_date);
        newsDate.setText(DateUtils.formatDate(
                pNewsItem.getDate(), getString(R.string.text_today),
                getString(R.string.text_yesterday), getString(R.string.text_day_before_yesterday)));

        // устанавливаем просмотры
        TextView newsView = (TextView) findViewById(R.id.news_views);
        newsView.setText(String.valueOf(pNewsItem.getViews()));
        newsView.setCompoundDrawablesWithIntrinsicBounds(
                DrawableUtils.getTinted(getResources().getDrawable(R.drawable.ic_eye_black_24dp),
                        getResources().getColor(R.color.fullNewsHeaderTextColor)), null, null, null);

        // устанавливаем комментарии
        TextView newsCommNum = (TextView) findViewById(R.id.news_comm_num);
        newsCommNum.setText(String.valueOf(pNewsItem.getCommNum()));
        newsCommNum.setCompoundDrawablesWithIntrinsicBounds(
                DrawableUtils.getTinted(getResources().getDrawable(R.drawable.ic_comment_black_24dp),
                        getResources().getColor(R.color.fullNewsHeaderTextColor)), null, null, null);

        // Устнавливаем заголовок
        TextView newsTitle = (TextView) findViewById(R.id.news_title);
        newsTitle.setText(pNewsItem.getTitle());

        FrameLayout newContentContainer = (FrameLayout) findViewById(R.id.new_content);
        NewsContentBuilder newsContentBuilder = new NewsContentBuilder();
        newContentContainer.addView(newsContentBuilder.Build(this, pNewsItem.getHtml()));


        View progressView = findViewById(R.id.progressContainer);
        View contentContainer = findViewById(R.id.nested_scroll);

        progressView.setVisibility(View.GONE);
        contentContainer.setVisibility(View.VISIBLE);
    }
}