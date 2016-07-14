package net.gility.okweather.ui;

import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.squareup.picasso.Picasso;

import net.gility.okweather.R;
import net.gility.okweather.dagger.Injector;
import net.gility.okweather.model.ChangeCityEvent;
import net.gility.okweather.model.City;
import net.gility.okweather.model.Province;
import net.gility.okweather.storage.LocationDB;
import net.gility.okweather.storage.Preferences;
import net.gility.okweather.ui.adapter.CityAdapter;
import net.gility.okweather.utils.AppUtils;
import net.gility.okweather.utils.RxBus;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import jp.wasabeef.recyclerview.animators.FadeInAnimator;
import jp.wasabeef.recyclerview.animators.FadeInUpAnimator;
import jp.wasabeef.recyclerview.animators.FlipInRightYAnimator;
import jp.wasabeef.recyclerview.animators.LandingAnimator;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * @author Alimy
 */

public class ChoiceCityActivity extends BaseActivity {
    public static final int LEVEL_PROVINCE = 1;
    public static final int LEVEL_CITY = 2;

    private int currentLevel;
    private ArrayList<String> dataList = new ArrayList<>();
    private Province selectedProvince;
    private City selectedCity;
    private List<Province> provincesList = new ArrayList<>();
    private List<City> cityList;
    private CityAdapter mAdapter;
    private CompositeSubscription mCompositeSubscription = new CompositeSubscription();

    @BindView(R.id.coord) CoordinatorLayout mCoord;
    @BindView(R.id.toolbar_layout) CollapsingToolbarLayout mToolbarLayout;
    @BindView(R.id.banner) ImageView mBanner;
    @BindView(R.id.toolbar) Toolbar mToolbar;
    @BindView(R.id.recyclerview) RecyclerView mRecyclerview;
    @BindView(R.id.progressBar) ProgressBar mProgressBar;
    @BindView(R.id.iv_erro) ImageView mIvErro;

    @Inject LocationDB mLocationDB;
    @Inject Picasso mPicasso;
    @Inject Preferences mPreferences;
    @Inject RxBus mRxBus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choice_city);

        Injector.instance.inject(this);

        initView();

        addSubscription(Observable.defer(() -> Observable.just(1))
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(integer -> {
                    initRecyclerView();
                    queryProvinces();
                }));
    }

    private void initView() {

        setStatusBarColorForKitkat(R.color.colorSunrise);
        if (mBanner != null) {
            mPicasso.load(R.mipmap.city_day).fit().into(mBanner);
            if (mPreferences.getCurrentHour() < 6 || mPreferences.getCurrentHour() > 18) {
                mToolbarLayout.setContentScrimColor(ContextCompat.getColor(this, R.color.colorSunset));
                mPicasso.load(R.mipmap.city_night).into(mBanner);
                setStatusBarColorForKitkat(R.color.colorSunset);
            }
        }
        if (mProgressBar != null) {
            mProgressBar.setVisibility(View.VISIBLE);
        }
    }

    private void initRecyclerView() {
        mRecyclerview.setHasFixedSize(true);
        mRecyclerview.setItemAnimator(new FadeInUpAnimator());
        mAdapter = new CityAdapter(this, dataList);
        mRecyclerview.setAdapter(mAdapter);
        mRecyclerview.setItemAnimator(new LandingAnimator());

        mAdapter.setOnItemClickListener((view, pos) -> {
            if (currentLevel == LEVEL_PROVINCE) {
                selectedProvince = provincesList.get(pos);
                mRecyclerview.smoothScrollToPosition(0);
                queryCities();
            } else if (currentLevel == LEVEL_CITY) {
                selectedCity = cityList.get(pos);
                String cityName = AppUtils.replaceCity(selectedCity.CityName);
                mPreferences.setCityName(cityName);
                mRxBus.post(new ChangeCityEvent(cityName));
                finish();
            }
        });
    }

    /**
     * 查询全国所有的省，从数据库查询
     */
    private void queryProvinces() {
        mToolbarLayout.setTitle("选择省份");
        addSubscription(Observable.defer(() -> {
            if (provincesList.isEmpty()) {
                provincesList.addAll(mLocationDB.loadProvinces());
            }
            dataList.clear();
            return Observable.from(provincesList);
        })
                .map(province -> province.ProName)
                //.delay(60, TimeUnit.MILLISECONDS, Schedulers.immediate())
                //.onBackpressureBuffer() // 会缓存所有当前无法消费的数据，直到 Observer 可以处理为止
                .toList()
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnTerminate(() -> mProgressBar.setVisibility(View.GONE))
                .subscribe(
                        province -> dataList.addAll(province),
                        throwable -> {},
                        () -> {
                            currentLevel = LEVEL_PROVINCE;
                            mAdapter.notifyDataSetChanged();
                        }
                ));
    }

    /**
     * 查询选中省份的所有城市，从数据库查询
     */
    private void queryCities() {
        dataList.clear();
        mAdapter.notifyDataSetChanged();
        mToolbarLayout.setTitle(selectedProvince.ProName);
        addSubscription(Observable.defer(() -> {
            cityList = mLocationDB.loadCities(selectedProvince.ProSort);
            return Observable.from(cityList);
        })
                .map(city -> city.CityName)
                .toList()
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(city -> dataList.addAll(city),
                        throwable -> {},
                        () -> {
                            currentLevel = LEVEL_CITY;
                            mAdapter.notifyDataSetChanged();
                            mRecyclerview.smoothScrollToPosition(0);
                        }));
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();  http://www.eoeandroid.com/thread-275312-1-1.html 这里的坑
        if (currentLevel == LEVEL_PROVINCE) {
            finish();
        } else {
            queryProvinces();
            mRecyclerview.smoothScrollToPosition(0);
        }
    }

    private void addSubscription(Subscription subscription) {
        mCompositeSubscription.add(subscription);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mCompositeSubscription.unsubscribe();
    }

}

