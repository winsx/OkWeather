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

import com.squareup.picasso.Picasso;

import net.gility.okweather.BuildConfig;
import net.gility.okweather.R;
import net.gility.okweather.android.AutoUpdateService;
import net.gility.okweather.dagger.Injector;
import net.gility.okweather.model.ChangeCityEvent;
import net.gility.okweather.model.UpdateWeatherErrorEvent;
import net.gility.okweather.model.UpdateWeatherEvent;
import net.gility.okweather.storage.Preferences;
import net.gility.okweather.ui.fragment.MainFragment;
import net.gility.okweather.utils.AndroidUtils;
import net.gility.okweather.utils.DoubleClickExit;
import net.gility.okweather.utils.RxBus;
import net.gility.okweather.utils.RxDrawer;

import java.util.Calendar;

import javax.inject.Inject;

import butterknife.BindView;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.subscriptions.CompositeSubscription;

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

    // @BindView(R.id.header_background) FrameLayout mNavHeaderView;
    @BindView(R.id.drawer_layout) DrawerLayout mDrawerLayout;
    @BindView(R.id.nav_view) NavigationView mNavigationView;

    @Inject Preferences mPreferences;
    @Inject RxBus mRxBus;
    @Inject Picasso mPicasso;

    private CompositeSubscription mCompositeSubscription = new CompositeSubscription();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Injector.obtain(getApplication()).inject(this);

        initView();
        initDrawer();
        initRx();

        FragmentTransaction fm = getFragmentManager().beginTransaction();
        fm.replace(R.id.main_content, MainFragment.instance());
        fm.commit();

        startService(new Intent(this, AutoUpdateService.class));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        mCompositeSubscription.unsubscribe();
    }

    private void initRx() {
        addSubscription(mRxBus.toObserverable(UpdateWeatherErrorEvent.class)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe( error -> {
                    Snackbar.make(mFab, "网络不好", Snackbar.LENGTH_INDEFINITE).setAction("重试", v -> {
                        mRxBus.post(new UpdateWeatherEvent());
                    }).show();
                }));

        addSubscription(mRxBus.toObserverable(ChangeCityEvent.class)
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(event -> {
                                    mCollapsingToolbarLayout.setTitle(event.city);
                                }));
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
        mPreferences.setCurrentHour(calendar.get(Calendar.HOUR_OF_DAY));
        setStatusBarColorForKitkat(R.color.colorSunrise);
        mPicasso.load(R.mipmap.sunrise).into(mBanner);
        if (mPreferences.getCurrentHour() < 6 || mPreferences.getCurrentHour() > 18) {
            mPicasso.load(R.mipmap.sunset).into(mBanner);
            mCollapsingToolbarLayout.setContentScrimColor(ContextCompat.getColor(this, R.color.colorSunset));
            setStatusBarColorForKitkat(R.color.colorSunset);
            // mNavHeaderView.setBackgroundResource(R.mipmap.header_back_night );
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
                    switch (item.getItemId()) {
                        case R.id.nav_set:
                            Intent intentSetting = new Intent(MainActivity.this, SettingsActivity.class);
                            startActivity(intentSetting);
                            break;
                        case R.id.nav_about:
                            startActivity(new Intent(MainActivity.this, AboutActivity.class));
                            break;
                        case R.id.nav_city:
                            Intent intentCity = new Intent(MainActivity.this, ChoiceCityActivity.class);
                            startActivity(intentCity);
                            break;
                    }
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

    private void addSubscription(Subscription subscription) {
        mCompositeSubscription.add(subscription);
    }
}
