package net.gility.okweather.ui.fragment;

import android.Manifest;
import android.app.Fragment;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.tbruyelle.rxpermissions.RxPermissions;

import net.gility.okweather.R;
import net.gility.okweather.config.BuildVars;
import net.gility.okweather.dagger.Injector;
import net.gility.okweather.model.ChangeCityEvent;
import net.gility.okweather.model.ChangeIconTypeEvent;
import net.gility.okweather.model.UpdateCityWeatherEvent;
import net.gility.okweather.model.UpdateWeatherErrorEvent;
import net.gility.okweather.model.UpdateWeatherEvent;
import net.gility.okweather.model.Weather;
import net.gility.okweather.storage.ACache;
import net.gility.okweather.storage.Preferences;
import net.gility.okweather.ui.MainActivity;
import net.gility.okweather.ui.adapter.WeatherAdapter;
import net.gility.okweather.utils.ApiUtils;
import net.gility.okweather.utils.AppUtils;
import net.gility.okweather.utils.RxBus;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.subscriptions.CompositeSubscription;

import static android.widget.Toast.LENGTH_SHORT;

/**
 * @author Alimy
 */

public class MainFragment extends Fragment implements AMapLocationListener {

    public AMapLocationClient mLocationClient = null;
    public AMapLocationClientOption mLocationOption = null;

    @BindView(R.id.swiprefresh) SwipeRefreshLayout mRefreshLayout;
    @BindView(R.id.recyclerview) RecyclerView mRecyclerView;
    @BindView(R.id.progressBar) ProgressBar mProgressBar;
    @BindView(R.id.iv_erro) ImageView mErrorImageView;

    @Inject Preferences mPreferences;
    @Inject RxBus mRxBus;
    @Inject RxPermissions mRxPermissions;
    @Inject ACache mACache;
    @Inject ApiUtils mApiUtils;

    private WeatherAdapter mAdapter;
    private CompositeSubscription mCompositeSubscription = new CompositeSubscription();

    public static MainFragment instance() {
        return new MainFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        ButterKnife.bind(this, rootView);
        Injector.instance.inject(this);

        initView();
        initRx();

        return rootView;
    }

    private void initView() {
        mRefreshLayout.setOnRefreshListener(() -> mRefreshLayout.postDelayed(this::load, 1000));
        mProgressBar.setVisibility(View.VISIBLE);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setHasFixedSize(true);
        mAdapter = new WeatherAdapter(getActivity());
        mRecyclerView.setAdapter(mAdapter);

        mAdapter.setOnItemClickListener(weather -> WeatherDialogFragment.instance(weather)
                .show(getFragmentManager(), "weather_dialog"));
    }

    private void initRx() {
        // CheckVersion.checkVersion(this);
        // https://github.com/tbruyelle/RxPermissions
        mRxPermissions.request(Manifest.permission.ACCESS_COARSE_LOCATION)
                .subscribe(granted -> {
                    if (granted) {
                        location();
                    } else {
                        load();
                    }
                });


        addSubscription(mRxBus.toObserverable(ChangeCityEvent.class)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(changeCityEvent -> {
                    mRefreshLayout.setRefreshing(true);
                    load(changeCityEvent.city);
                }));

        addSubscription(mRxBus.toObserverable(ChangeIconTypeEvent.class)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(changeIconTypeEvent -> {
                    load();
                    //mIconChanged = true;
                }));

        addSubscription(mRxBus.toObserverable(UpdateWeatherEvent.class)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(event -> {
                    load();
                }));
    }

    private void load() {
        load(mPreferences.getCityName());
    }

    /**
     * 优化网络+缓存逻辑
     * 优先网络
     */
    private void load(String cityName) {
        addSubscription(fetchDataFromNetWork(cityName)
                .doOnError(throwable -> {
                    mErrorImageView.setVisibility(View.VISIBLE);
                    mRecyclerView.setVisibility(View.GONE);
                })
                .doOnNext(weather -> {
                    mErrorImageView.setVisibility(View.GONE);
                    mRecyclerView.setVisibility(View.VISIBLE);
                })
                .doOnTerminate(() -> {
                    mRefreshLayout.setRefreshing(false);
                    mProgressBar.setVisibility(View.GONE);
                })
                .subscribe(mObserver));
    }

    /**
     * 从本地获取
     */
    private Observable<Weather> fetchDataByCache() {
        return Observable.defer(() -> {
            Weather weather = (Weather) mACache.getAsObject(BuildVars.WEATHER_CACHE);
            return Observable.just(weather);
        });
    }

    /**
     * 从网络获取
     */
    private Observable<Weather> fetchDataFromNetWork(String cityName) {
        return mApiUtils.fetchWeather(cityName)
                .onErrorReturn(throwable -> {
                    Toast.makeText(getActivity(), throwable.getMessage(), Toast.LENGTH_SHORT).show();
                    return null;
                });
    }

    /**
     * 高德定位
     */
    private void location() {
        //初始化定位
        mLocationClient = new AMapLocationClient(getActivity());
        //设置定位回调监听
        mLocationClient.setLocationListener(this);
        mLocationOption = new AMapLocationClientOption();
        //设置定位模式为高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Battery_Saving);
        //设置是否返回地址信息（默认返回地址信息）
        mLocationOption.setNeedAddress(true);
        //设置是否只定位一次,默认为false
        mLocationOption.setOnceLocation(false);
        //设置是否强制刷新WIFI，默认为强制刷新
        mLocationOption.setWifiActiveScan(true);
        //设置是否允许模拟位置,默认为false，不允许模拟位置
        mLocationOption.setMockEnable(false);
        //设置定位间隔 单位毫秒
        int tempTime = mPreferences.getAutoUpdate();
        if (tempTime == 0) {
            tempTime = 100;
        }
        mLocationOption.setInterval(tempTime * Preferences.ONE_HOUR);
        //给定位客户端对象设置定位参数
        mLocationClient.setLocationOption(mLocationOption);
        //启动定位
        mLocationClient.startLocation();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mLocationClient != null) {
            mLocationClient.unRegisterLocationListener(this);
        }
        mCompositeSubscription.unsubscribe();
    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (aMapLocation != null) {
            if (aMapLocation.getErrorCode() == 0) {
                //定位成功回调信息，设置相关消息
                aMapLocation.getLocationType();//获取当前定位结果来源，如网络定位结果，详见定位类型表
                mPreferences.setCityName(AppUtils.replaceCity(aMapLocation.getCity()));
            } else {
                Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.errorLocation), LENGTH_SHORT).show();
            }
            load();
        }
    }

    private void normalStyleNotification(Weather weather) {
        Intent intent = new Intent(getActivity(), MainActivity.class);
        //intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent =
                PendingIntent.getActivity(getActivity(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        //Notification.Builder builder = new Notification.Builder(MainActivity2.this);
        // Notification notification = builder.setContentIntent(pendingIntent)
        //        .setContentTitle(weather.basic.city)
        //        .setContentText(String.format("%s 当前温度: %s℃ ", weather.now.cond.txt, weather.now.tmp))
        // 这里部分 ROM 无法成功
        //        .setSmallIcon(mPreferences.getInt(weather.now.cond.txt, R.mipmap.none))
        //       .build();
        // notification.flags = mPreferences.getNotificationModel();
        // NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        // tag和id都是可以拿来区分不同的通知的
        // manager.notify(1, notification);
    }

    private void addSubscription(Subscription subscription) {
        mCompositeSubscription.add(subscription);
    }


    /**
     * 初始化 observer (观察者)
     * 拿到数据后的操作
     */
    private Observer<Weather> mObserver = new Observer<Weather>() {

        @Override
        public void onCompleted() {
            Toast.makeText(getActivity(), getString(R.string.complete), Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onError(Throwable e) {
            mErrorImageView.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.GONE);
            mRxBus.post(new UpdateWeatherErrorEvent(e));
        }

        @Override
        public void onNext(Weather weather) {
            if (weather == null) {
                onError(new Throwable("未知错误"));
                return;
            }
            if (mPreferences.getAutoUpdate() == 0) {
                mACache.put(BuildVars.WEATHER_CACHE, weather);
            } else {
                mACache.put(BuildVars.WEATHER_CACHE, weather,
                        (mPreferences.getAutoUpdate() * Preferences.ONE_HOUR));//默认3小时后缓存失效
            }
            mRxBus.post(new UpdateCityWeatherEvent(weather));
            mAdapter.updateData(weather);
            normalStyleNotification(weather);
        }
    };


}
