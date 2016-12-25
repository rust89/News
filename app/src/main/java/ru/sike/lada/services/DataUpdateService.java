package ru.sike.lada.services;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.util.Log;

import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Response;
import ru.sike.lada.backend.LadaKzService;
import ru.sike.lada.backend.ServiceGenerator;
import ru.sike.lada.backend.json.Category;
import ru.sike.lada.backend.json.NewsFull;
import ru.sike.lada.backend.json.NewsShort;
import ru.sike.lada.receivers.FullNewsUpdateReceiver;
import ru.sike.lada.receivers.NewsCategoryUpdateReceiver;
import ru.sike.lada.receivers.ShortNewsListUpdateCompleteReceivers;
import ru.sike.lada.services.updaters.CategoryUpdater;
import ru.sike.lada.services.updaters.NewsUpdater;
import ru.sike.lada.utils.ConnectionChecker;
import ru.sike.lada.utils.caching.CacheExecutorProvider;
import ru.sike.lada.utils.caching.CacheTaskFactory;


public class DataUpdateService extends IntentService {

    private static final String LOG_TAG = "DataUpdateService";

    private static final String ACTION_UPDATE_NEWS_CATEGORIES = "ru.sike.lada.services.DataUpdateService.action.ACTION_UPDATE_NEWS_CATEGORIES";
    private static final String ACTION_UPDATE_NEWS_SHORT = "ru.sike.lada.services.DataUpdateService.action.ACTION_UPDATE_NEWS_SHORT";
    private static final String ACTION_UPDATE_NEWS_FULL = "ru.sike.lada.services.DataUpdateService.action.ACTION_UPDATE_NEWS_FULL";

    private static final String EXTRA_PARAM_NEWS_ID = "ru.sike.lada.services.DataUpdateService.extra.PARAM_NEWS_ID";
    private static final String EXTRA_PARAM_CATEGORY_ID = "ru.sike.lada.services.DataUpdateService.extra.PARAM_CATEGORY_ID";
    private static final String EXTRA_PARAM_FETCH_FROM = "ru.sike.lada.services.DataUpdateService.extra.PARAM_FETCH_FROM";
    private static final String EXTRA_PARAM_FETCH_COUNT = "ru.sike.lada.services.DataUpdateService.extra.PARAM_FETCH_COUNT";

    public DataUpdateService() {
        super("DataUpdateService");
    }

    public static void startActionUpdateNewsCategories(Context context) {
        Intent intent = new Intent(context, DataUpdateService.class);
        intent.setAction(ACTION_UPDATE_NEWS_CATEGORIES);
        context.startService(intent);
    }

    public static void startActionUpdateNewsShort(Context context, long categoryId, int fetchFrom, int fetchCount) {
        Intent intent = new Intent(context, DataUpdateService.class);
        intent.setAction(ACTION_UPDATE_NEWS_SHORT);
        intent.putExtra(EXTRA_PARAM_CATEGORY_ID, categoryId);
        intent.putExtra(EXTRA_PARAM_FETCH_FROM, fetchFrom);
        intent.putExtra(EXTRA_PARAM_FETCH_COUNT, fetchCount);
        context.startService(intent);
    }

    public static void startActionUpdateNewsFull(Context context, long newsId) {
        Intent intent = new Intent(context, DataUpdateService.class);
        intent.setAction(ACTION_UPDATE_NEWS_FULL);
        intent.putExtra(EXTRA_PARAM_NEWS_ID, newsId);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_UPDATE_NEWS_CATEGORIES.equals(action)) {
                handleActionUpdateNewsCategories();
            } else if (ACTION_UPDATE_NEWS_SHORT.equals(action)) {
                long categoryId = intent.getLongExtra(EXTRA_PARAM_CATEGORY_ID, -1);
                int fetchFrom = intent.getIntExtra(EXTRA_PARAM_FETCH_FROM, 0);
                int fetchCount = intent.getIntExtra(EXTRA_PARAM_FETCH_COUNT, 0);
                handleActionUpdateNewsShort(categoryId, fetchFrom, fetchCount);
            } else if (ACTION_UPDATE_NEWS_FULL.equals(action)) {
                long newsId = intent.getLongExtra(EXTRA_PARAM_NEWS_ID, -1);
                handleActionUpdateNewsFull(newsId);
            }
        }
    }

    private void handleActionUpdateNewsCategories() {
        NewsCategoryUpdateReceiver.Status result = NewsCategoryUpdateReceiver.Status.Fail;
        String message = "";

        if (ConnectionChecker.check(this)) {
            try {
                LadaKzService service = ServiceGenerator.getApiInterface();
                Call<List<Category>> call = service.getCategories();
                Response<List<Category>> response = call.execute();
                if (response.isSuccessful()) {
                    CategoryUpdater updater = new CategoryUpdater(DataUpdateService.this);
                    updater.Update(response.body());
                    result = NewsCategoryUpdateReceiver.Status.Success;
                } else {
                    throw new Exception(response.message());
                }
            } catch (Exception Ex) {
                message = Ex.getMessage();
                Ex.printStackTrace();
            }
        } else {
            result = NewsCategoryUpdateReceiver.Status.NoInternet;
        }

        NewsCategoryUpdateReceiver.Broadcast(this,
                new NewsCategoryUpdateReceiver.NewsCategoryUpdateResult(result, message));
    }

    private void handleActionUpdateNewsShort(long categoryId, int fetchFrom, int fetchCount) {
        ShortNewsListUpdateCompleteReceivers.Status result = ShortNewsListUpdateCompleteReceivers.Status.Fail;
        String message = "";

        if (ConnectionChecker.check(this)) {
            try {
                LadaKzService service = ServiceGenerator.getApiInterface();
                Call<List<NewsShort>> call = categoryId != Long.MAX_VALUE ?
                        service.getNewsShort(categoryId, fetchFrom, fetchCount) : service.getMainNewsShort(fetchFrom, fetchCount);
                Response<List<NewsShort>> response = call.execute();
                if (response.isSuccessful()) {
                    NewsUpdater newsShortUpdater = new NewsUpdater(DataUpdateService.this);
                    newsShortUpdater.Update(response.body(), categoryId);
                } else {
                    throw new Exception(response.message());
                }
                result = ShortNewsListUpdateCompleteReceivers.Status.Success;
            } catch (Exception Ex) {
                Ex.printStackTrace();
                message = Ex.getMessage();
            }
        } else {
            result = ShortNewsListUpdateCompleteReceivers.Status.NoInternet;
        }

        ShortNewsListUpdateCompleteReceivers.Broadcast(this,
                new ShortNewsListUpdateCompleteReceivers.ShortNewsListUpdateResult(categoryId, result, message));
    }

    private void handleActionUpdateNewsFull(long newsId) {
        boolean result = false;
        String message = "";
        try {
            LadaKzService service = ServiceGenerator.getApiInterface();
            Call<List<NewsFull>> call = service.getNewsFull(newsId);
            Response<List<NewsFull>> response = call.execute();
            if (response.isSuccessful()) {
                NewsUpdater newsFullUpdater = new NewsUpdater(DataUpdateService.this);
                List<NewsFull> body = response.body();
                result = !body.isEmpty() && newsFullUpdater.Update(body.get(0));
                if (result) {
                    final Object monitor = new Object();
                    synchronized (monitor) {
                        CacheExecutorProvider.Execute(this, CacheTaskFactory.getNewsBigImageCacheTask(newsId), monitor);
                        monitor.wait(); // ждем пока не обновится картинка
                    }
                }
            } else {
                throw new Exception(response.message());
            }
        } catch (Exception Ex) {
            Ex.printStackTrace();
            message = Ex.getMessage();
        }

        FullNewsUpdateReceiver.Broadcast(this,
                new FullNewsUpdateReceiver.FullNewsUpdateResult(newsId, result, message));
    }
}
