package ru.sike.lada;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;

import ru.sike.lada.loaders.NewsCategoriesCursorLoader;
import ru.sike.lada.models.NewsCategory;
import ru.sike.lada.services.DataUpdateService;
import ru.sike.lada.utils.ConnectionChecker;

public class NewsCategoriesFragment
        extends
          Fragment
        implements
            LoaderManager.LoaderCallbacks<Cursor> {

    private static final String LOG_TAG = "NewsCategoriesFragment";
    private static final String FIRST_UPDATE_COMPLETE_BUNDLE_NAME = "firstUpdateComplete";
    private static final String SELECTED_PAGE_INDEX_BUNDLE_NAME = "selectedPageIndex";
    private static final String WAIT_TO_RESTORE_BUNDLE_NAME = "waitToRestore";

    private static final int NEWS_CATEGORIES_LOADER = 1;

    private boolean firstUpdateComplete = false;
    private int selectedPageIndex = 0;
    private boolean waitToRestore = false;

    public NewsCategoriesFragment() {
        // Required empty public constructor
    }

    public static NewsCategoriesFragment newInstance() {
        return new NewsCategoriesFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_news_categories, container, false);
        Button btnRefresh = (Button) rootView.findViewById(R.id.new_categories_fragment_refresh);
        btnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View pView) {
                Context context = getContext();
                if ((context != null) && ConnectionChecker.check(context)) {
                    View rootView = getView();
                    if (rootView != null) {
                        ViewPager viewPager = (ViewPager) rootView.findViewById(R.id.news_categories_pager);
                        View progressView = rootView.findViewById(R.id.news_categories_progress);
                        View offlineView = rootView.findViewById(R.id.news_categories_offline);
                        viewPager.setVisibility(View.GONE);
                        progressView.setVisibility(View.VISIBLE);
                        offlineView.setVisibility(View.GONE);
                    }
                    DataUpdateService.startActionUpdateNewsCategories(context);
                }
            }
        });
        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(FIRST_UPDATE_COMPLETE_BUNDLE_NAME))
                firstUpdateComplete = savedInstanceState.getBoolean(FIRST_UPDATE_COMPLETE_BUNDLE_NAME);
            if (savedInstanceState.containsKey(SELECTED_PAGE_INDEX_BUNDLE_NAME))
                selectedPageIndex = savedInstanceState.getInt(SELECTED_PAGE_INDEX_BUNDLE_NAME);
            if (savedInstanceState.containsKey(WAIT_TO_RESTORE_BUNDLE_NAME))
                waitToRestore = savedInstanceState.getBoolean(WAIT_TO_RESTORE_BUNDLE_NAME);
        }

        Activity contextActivity = getActivity();
        // настройка вкладок
        if (contextActivity != null) {
            TabLayout tabLayout = (TabLayout) getActivity().findViewById(R.id.tab_layout);
            if (tabLayout != null) {
                tabLayout.setVisibility(View.VISIBLE);
                View rootView = getView();
                if (rootView != null) {
                    ViewPager viewPager = (ViewPager) rootView.findViewById(R.id.news_categories_pager);
                    tabLayout.setupWithViewPager(viewPager);
                } else {
                    Log.e(LOG_TAG, "rootView is null on onActivityCreated callback");
                }

            }
            // инициализация вкладок с категориями
            View rootView = getView();
            if (rootView != null) {
                ViewPager viewPager = (ViewPager) rootView.findViewById(R.id.news_categories_pager);
                NewsCategoriesPagerAdapter adapter = new NewsCategoriesPagerAdapter(
                        getChildFragmentManager(), null);
                viewPager.setAdapter(adapter);
            } else {
                Log.e(LOG_TAG, "rootView is null on onActivityCreated callback");
            }
        } else {
            Log.e(LOG_TAG, "contextActivity is null on onActivityCreated callback");
        }

        // запускаем загрузку категорий из базы данных
        LoaderManager lm = getLoaderManager();
        if (lm != null)
            lm.initLoader(NEWS_CATEGORIES_LOADER, null, NewsCategoriesFragment.this).forceLoad();

        // запускаем обновление категорий с сервера
        if (contextActivity != null)
            DataUpdateService.startActionUpdateNewsCategories(contextActivity);
        else {
            Log.e(LOG_TAG, "rootView is null on onActivityCreated callback");
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(FIRST_UPDATE_COMPLETE_BUNDLE_NAME, firstUpdateComplete);
        View rootView = getView();
        if (rootView != null) {
            ViewPager viewPager = (ViewPager) rootView.findViewById(R.id.news_categories_pager);
            outState.putInt(SELECTED_PAGE_INDEX_BUNDLE_NAME, viewPager.getCurrentItem());
            outState.putBoolean(WAIT_TO_RESTORE_BUNDLE_NAME, true);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (id == NEWS_CATEGORIES_LOADER)
            return new NewsCategoriesCursorLoader(NewsCategoriesFragment.this.getContext());
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (loader.getId() == NEWS_CATEGORIES_LOADER) {
            View rootView = getView();
            if (rootView != null) {
                ArrayList<NewsCategory> newData = new ArrayList<>();
                if (data.moveToFirst())
                    do {
                        newData.add(new NewsCategory(data));
                    } while (data.moveToNext());
                ViewPager viewPager = (ViewPager) rootView.findViewById(R.id.news_categories_pager);
                NewsCategoriesPagerAdapter adapter = (NewsCategoriesPagerAdapter) viewPager.getAdapter();
                adapter.updateData(newData);
                if (newData.size() > 0) {

                    if (waitToRestore) {
                        waitToRestore = false;
                        viewPager.setCurrentItem(selectedPageIndex);
                    }

                    // если категории есть, то скрываем прогресс
                    View progressView = rootView.findViewById(R.id.news_categories_progress);
                    View offlineView = rootView.findViewById(R.id.news_categories_offline);
                    viewPager.setVisibility(View.VISIBLE);
                    progressView.setVisibility(View.GONE);
                    offlineView.setVisibility(View.GONE);
                    // запускаем фоновое обновление списка категорий
                    if (!firstUpdateComplete) {
                        firstUpdateComplete = true;
                        Context context = getContext();
                        if ((context != null) && ConnectionChecker.check(context)) {
                            DataUpdateService.startActionUpdateNewsCategories(context);
                        }
                    }
                } else {
                    // если категорий нет, то оставляем pager скрытым
                    Context context = getContext();
                    if ((context != null) && ConnectionChecker.check(context)) {
                        View progressView = rootView.findViewById(R.id.news_categories_progress);
                        View offlineView = rootView.findViewById(R.id.news_categories_offline);
                        viewPager.setVisibility(View.GONE);
                        progressView.setVisibility(View.VISIBLE);
                        offlineView.setVisibility(View.GONE);
                        if (!firstUpdateComplete) {
                            firstUpdateComplete = true;
                            DataUpdateService.startActionUpdateNewsCategories(context);
                        }
                    }
                    else {
                        View progressView = rootView.findViewById(R.id.news_categories_progress);
                        View offlineView = rootView.findViewById(R.id.news_categories_offline);
                        viewPager.setVisibility(View.GONE);
                        progressView.setVisibility(View.GONE);
                        offlineView.setVisibility(View.VISIBLE);
                    }
                }
            } else {
                Log.e(LOG_TAG, "rootView is null on onLoaderReset callback");
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        if (loader.getId() == NEWS_CATEGORIES_LOADER) {
            View rootView = getView();
            if (rootView != null) {
                ViewPager viewPager = (ViewPager) rootView.findViewById(R.id.news_categories_pager);
                NewsCategoriesPagerAdapter adapter = (NewsCategoriesPagerAdapter) viewPager.getAdapter();
                adapter.updateData(null);
            } else {
                Log.e(LOG_TAG, "rootView is null on onLoaderReset callback");
            }
        }
    }

    public static class NewsCategoriesPagerAdapter extends FragmentStatePagerAdapter {

        List<NewsCategory> mData;

        public NewsCategoriesPagerAdapter(FragmentManager pFragmentManager, List<NewsCategory> pData) {
            super(pFragmentManager);
            mData = pData;
        }

        @Override
        public int getCount() {
            return mData != null ? mData.size() : 0;
        }

        @Override
        public Fragment getItem(int pPosition) {
            NewsCategory item = mData.get(pPosition);
            return NewsListFragment.newInstance(item.getId(), item.getName());
        }

        @Override
        public CharSequence getPageTitle(int pPosition) {
            return mData.get(pPosition).getName();
        }

        public void updateData(List<NewsCategory> pData) {
            mData = pData;
            notifyDataSetChanged();
        }
    }
}
