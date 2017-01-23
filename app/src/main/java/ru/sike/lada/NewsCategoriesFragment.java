package ru.sike.lada;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
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
import ru.sike.lada.receivers.FullNewsUpdateReceiver;
import ru.sike.lada.receivers.NewsCategoryUpdateReceiver;
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

    /**
     * Broadcast ресивер слушающий обновление списка категорий
     */
    NewsCategoryUpdateReceiver mNewsCategoryUpdateReceiver = new NewsCategoryUpdateReceiver() {
        // действия при получении сообщений
        public void onReceive(Context context, Intent intent) {
            NewsCategoryUpdateResult result = intent.getParcelableExtra(NewsCategoryUpdateReceiver.BROADCAST_PARAM);
            if (result.getStatus() == Status.NoInternet) {
                View rootView = getView();
                if (rootView != null)
                    Snackbar.make(rootView, getString(R.string.text_no_internet), Snackbar.LENGTH_SHORT).show();
            } else if (result.getStatus() == Status.Fail) {
                View rootView = getView();
                if (rootView != null)
                    Snackbar.make(rootView, result.getMessage(), Snackbar.LENGTH_SHORT).show();
            }

            LoaderManager lm = getLoaderManager();
            if (lm != null)
                lm.initLoader(NEWS_CATEGORIES_LOADER, null, NewsCategoriesFragment.this).forceLoad();
        }
    };

    public NewsCategoriesFragment() {
        // Required empty public constructor
    }

    public static NewsCategoriesFragment newInstance() {
        return new NewsCategoriesFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().registerReceiver(mNewsCategoryUpdateReceiver, new IntentFilter(NewsCategoryUpdateReceiver.BROADCAST_ACTION));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(mNewsCategoryUpdateReceiver);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_news_categories, container, false);

        ViewPager viewPager = (ViewPager) rootView.findViewById(R.id.news_categories_pager);
        NewsCategoriesPagerAdapter adapter = new NewsCategoriesPagerAdapter(
                getChildFragmentManager(), null);
        viewPager.setAdapter(adapter);

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

        configTabLayout();

        // запускаем обновление категорий с сервера
        // но только один раз за всё время существования приложения
        Context context = getContext();
        ApplicationController app = (ApplicationController) context.getApplicationContext();
        if (!app.getGlobalValues().getNewsCategoriesWasRequested()) {
            DataUpdateService.startActionUpdateNewsCategories(context);
            app.getGlobalValues().setNewsCategoriesWasRequested(true);
        } else {
            LoaderManager lm = getLoaderManager();
            if (lm != null)
                lm.initLoader(NEWS_CATEGORIES_LOADER, null, NewsCategoriesFragment.this).forceLoad();
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

    private void configTabLayout() {
        Activity contextActivity = getActivity();
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
        }
    }

    private void updateViewPagerData(Cursor pCursor) {
        View rootView = getView();
        if (rootView != null) {
            ArrayList<NewsCategory> newData = new ArrayList<>();
            if (pCursor.moveToFirst())
                do {
                    newData.add(new NewsCategory(pCursor));
                } while (pCursor.moveToNext());
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
                viewPager.setVisibility(View.VISIBLE);
                progressView.setVisibility(View.GONE);
                // делаем, если это самое первое обновление данных
                if (!firstUpdateComplete) {
                    firstUpdateComplete = true;
                }
            }
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (id == NEWS_CATEGORIES_LOADER)
            return new NewsCategoriesCursorLoader(NewsCategoriesFragment.this.getContext());
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (loader.getId() == NEWS_CATEGORIES_LOADER)
            updateViewPagerData(data);
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
