package ru.sike.lada.services;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;

import ru.sike.lada.services.updaters.BookmarksUpdater;
import ru.sike.lada.services.updaters.NewsUpdater;


public class NewsHelperService extends IntentService {

    private static final String ACTION_ADD_TO_BOOKMARKS = "ru.sike.lada.services.NewsHelperService.action.ADD_TO_BOOKMARKS";

    private static final String EXTRA_PARAM_NEWS_ID = "ru.sike.lada.services.NewsHelperService.extra.PARAM_NEWS_ID";
    private static final String EXTRA_PARAM_NEWS_BOOKMARKED = "ru.sike.lada.services.NewsHelperService.extra.PARAM_NEWS_BOOKMARKED";

    public NewsHelperService() {
        super("NewsHelperService");
    }

    public static void startActionAddToBookmarks(Context context, long newsId, int bookmarked) {
        Intent intent = new Intent(context, NewsHelperService.class);
        intent.setAction(ACTION_ADD_TO_BOOKMARKS);
        intent.putExtra(EXTRA_PARAM_NEWS_ID, newsId);
        intent.putExtra(EXTRA_PARAM_NEWS_BOOKMARKED, bookmarked);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_ADD_TO_BOOKMARKS.equals(action)) {
                if (intent.hasExtra(EXTRA_PARAM_NEWS_ID)) {
                    long newsId = intent.getLongExtra(EXTRA_PARAM_NEWS_ID, 0);
                    int bookmarked = intent.getIntExtra(EXTRA_PARAM_NEWS_BOOKMARKED, 0);
                    handleActionAddToBookmarks(newsId, bookmarked);
                }
            }
        }
    }

    private void handleActionAddToBookmarks(long newsId, int bookmarked) {
        BookmarksUpdater updater = new BookmarksUpdater(this);
        updater.Update(newsId, bookmarked);
    }

}
