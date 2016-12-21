package ru.sike.lada;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity
        implements
            NavigationView.OnNavigationItemSelectedListener {

    private static final String mainActivityContentFragmentTag = "mainActivityContentFragmentTag";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        FragmentManager fm = getSupportFragmentManager();
        if (fm != null) {
            if (fm.findFragmentByTag(mainActivityContentFragmentTag) == null) {
                Menu drawerMenu = navigationView.getMenu();
                if (drawerMenu != null) {
                    MenuItem newsMenuItem = drawerMenu.findItem(R.id.nav_news);
                    if (newsMenuItem != null)
                        newsMenuItem.setChecked(true);
                }

                fm.beginTransaction()
                        .replace(R.id.container, new NewsCategoriesFragment(), mainActivityContentFragmentTag)
                        .commit();
            }
        }
    }

    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_news) {
            FragmentManager fm = getSupportFragmentManager();
            if (fm != null) {
                Fragment fragment = fm.findFragmentByTag(mainActivityContentFragmentTag);
                if ((fragment == null) || !(fragment instanceof NewsCategoriesFragment)) {
                    fm.beginTransaction()
                            .replace(R.id.container, NewsCategoriesFragment.newInstance(), mainActivityContentFragmentTag)
                            .commit();
                }
            }
        } else if (id == R.id.nav_bookmarks) {
            FragmentManager fm = getSupportFragmentManager();
            if (fm != null) {
                Fragment fragment = fm.findFragmentByTag(mainActivityContentFragmentTag);
                if ((fragment == null) || !(fragment instanceof BookmarkFragment)) {
                    fm.beginTransaction()
                            .replace(R.id.container, BookmarkFragment.newInstance(), mainActivityContentFragmentTag)
                            .commit();
                }
            }
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
