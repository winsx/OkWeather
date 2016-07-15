package net.gility.okweather.ui;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import com.hwangjr.rxbus.Bus;
import com.hwangjr.rxbus.annotation.Subscribe;
import com.hwangjr.rxbus.annotation.Tag;
import com.hwangjr.rxbus.thread.EventThread;
import com.squareup.picasso.Picasso;

import net.gility.okweather.BuildConfig;
import net.gility.okweather.R;
import net.gility.okweather.android.AutoUpdateService;
import net.gility.okweather.dagger.Injector;
import net.gility.okweather.model.BusAction;
import net.gility.okweather.storage.Preferences;
import net.gility.okweather.ui.fragment.MainFragment;
import net.gility.okweather.utils.AndroidUtils;
import net.gility.okweather.utils.DoubleClickExit;
import net.gility.okweather.utils.RxDrawer;

import java.util.Calendar;

import javax.inject.Inject;

import butterknife.BindView;
import rx.android.schedulers.AndroidSchedulers;

import static android.widget.Toast.LENGTH_SHORT;

/**
 * @author Alimy
 */

public class MainActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener {

    @BindView(R.id.app_bar) AppBarLayout mAppBarLayout;
    @BindView(R.id.toolbar) Toolbar mToolbar;
    @BindView(R.id.banner) ImageView mBanner;

    @BindView(R.id.toolbar_layout) CollapsingToolbarLayout mCollapsingToolbarLayout;
    @BindView(R.id.fab) FloatingActionButton mFab;

    @BindView(R.id.drawer_layout) DrawerLayout mDrawerLayout;
    @BindView(R.id.nav_view) NavigationView mNavigationView;

    @Inject Preferences mPreferences;
    @Inject Picasso mPicasso;
    @Inject Bus mBus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Injector.instance.inject(this);
        mBus.register(this);

        initView();
        initDrawer();

        FragmentTransaction fm = getFragmentManager().beginTransaction();
        fm.replace(R.id.main_content, MainFragment.instance());
        fm.commit();

        startService(new Intent(this, AutoUpdateService.class));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        mBus.unregister(this);
    }

    @Subscribe(
            thread = EventThread.MAIN_THREAD,
            tags = {
                    @Tag(BusAction.CHANGE_CITY)
            }
    )
    public void changeCityAction(String city) {
        mCollapsingToolbarLayout.setTitle(city);
    }

    @Subscribe(
            tags = {
                    @Tag(BusAction.UPDATE_WEATHER_ERROR)
            }
    )
    public void updateWeatherErrorAction(Throwable e) {
        Snackbar.make(mFab, "网络不好", Snackbar.LENGTH_INDEFINITE).setAction("重试", v -> {
            mBus.post(BusAction.UPDATE_WEATHER, e);
        }).show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        showEggs();
    }

    /**
     * 初始化基础View
     */
    private void initView() {
        // http://stackoverflow.com/questions/30655939/programmatically-collapse-or-expand-mCollapsingToolbarLayout

        setSupportActionBar(mToolbar);
        mAppBarLayout.setExpanded(false);
        mCollapsingToolbarLayout.setTitle(mPreferences.getCityName());
        mNavigationView.setNavigationItemSelectedListener(this);

        //fab
        mFab.setOnClickListener(v -> showFabDialog());
        if (AndroidUtils.checkDeviceHasNavigationBar(this) || BuildConfig.DEBUG) {
            CoordinatorLayout.LayoutParams lp = (CoordinatorLayout.LayoutParams) mFab.getLayoutParams();
            Resources res = getResources();
            int fabMargin = AndroidUtils.dip2px(this, res.getDimension(R.dimen.fab_margin)) / 3;
            lp.setMargins(fabMargin, fabMargin, fabMargin, AndroidUtils.getNavigationBarHeight(this) + fabMargin);
        }
    }

    private void showEggs() {
        //彩蛋-夜间模式
        Calendar calendar = Calendar.getInstance();
        int currentHour = calendar.get(Calendar.HOUR_OF_DAY);
        mPreferences.setCurrentHour(currentHour);

        if (currentHour < 6 || currentHour > 18) {
            mPicasso.load(R.mipmap.sunset).into(mBanner);
            mCollapsingToolbarLayout.setContentScrimColor(ContextCompat.getColor(this, R.color.colorSunset));
            setStatusBarColorForKitkat(R.color.colorSunset);
            mNavigationView.getHeaderView(0).setBackgroundResource(R.mipmap.header_back_night );
        } else {
            setStatusBarColorForKitkat(R.color.colorSunrise);
            mPicasso.load(R.mipmap.sunrise).into(mBanner);
            mCollapsingToolbarLayout.setContentScrimColor(ContextCompat.getColor(this, R.color.colorSunrise));
            mNavigationView.getHeaderView(0).setBackgroundResource(R.mipmap.header_back_day);
        }
    }

    /**
     * 初始化抽屉
     */

    private void initDrawer() {
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        mDrawerLayout.addDrawerListener(toggle);
        toggle.syncState();
    }

    private void showFabDialog() {
        new AlertDialog.Builder(MainActivity.this).setTitle(getString(R.string.fab_dialog_title))
                .setMessage(getString(R.string.fab_dialog_message))
                .setPositiveButton(getString(R.string.fab_dialog_positive), (dialog, which) -> {
                    Uri uri = Uri.parse(getString(R.string.app_html));   //指定网址
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_VIEW);           //指定Action
                    intent.setData(uri);                            //设置Uri
                    MainActivity.this.startActivity(intent);        //启动Activity
                })
                .show();
    }

    /**
     * Called when an item in the navigation menu is selected.
     *
     * @param item The selected item
     * @return true to display the item as the selected item
     */
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        RxDrawer.close(mDrawerLayout)
                .subscribeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(aVoid -> {
                    Class clazz = null;
                    switch (item.getItemId()) {
                        case R.id.nav_set:
                            clazz = SettingsActivity.class;
                            break;
                        case R.id.nav_about:
                            clazz = AboutActivity.class;
                            break;
                        case R.id.nav_city:
                            clazz = ChoiceCityActivity.class;
                            break;
                    }
                    startActivity(new Intent(MainActivity.this, clazz));
                });
        return false;
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            if (!DoubleClickExit.check()) {
                Toast.makeText(this, getString(R.string.double_exit), LENGTH_SHORT).show();
            } else {
                finish();
            }
        }
    }
}
