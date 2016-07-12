package net.gility.okweather.android;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import net.gility.okweather.R;
import net.gility.okweather.config.BuildVars;
import net.gility.okweather.dagger.Injector;
import net.gility.okweather.model.Weather;
import net.gility.okweather.storage.ACache;
import net.gility.okweather.storage.Preferences;
import net.gility.okweather.ui.MainActivity;
import net.gility.okweather.utils.ApiUtils;
import net.gility.okweather.utils.AppUtils;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import rx.Observable;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;


public class AutoUpdateService extends Service {

    @Inject Preferences mPreferences;
    @Inject ACache mAcache;
    @Inject ApiUtils mApiUtils;

    private CompositeSubscription mCompositeSubscription;
    private Subscription mNetSubcription;

    private boolean isUnsubscribed = true;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        Injector.instance.inject(this);

        mCompositeSubscription = new CompositeSubscription();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        synchronized (this) {
            unSubscribed();
            if (isUnsubscribed) {
                unSubscribed();
                if (mPreferences.getAutoUpdate() != 0) {
                    mNetSubcription = Observable.interval(mPreferences.getAutoUpdate(), TimeUnit.HOURS)
                            .subscribe(aLong -> {
                                isUnsubscribed = false;
                                fetchDataByNetWork();
                            });
                    mCompositeSubscription.add(mNetSubcription);
                }
            }
        }
        return START_REDELIVER_INTENT;
    }

    private void unSubscribed() {
        isUnsubscribed = true;
        mCompositeSubscription.remove(mNetSubcription);
    }

    @Override
    public boolean stopService(Intent name) {
        return super.stopService(name);
    }

    private void fetchDataByNetWork() {
        String cityName = mPreferences.getCityName();
        if (cityName != null) {
            cityName = AppUtils.replaceCity(cityName);
        }
        mApiUtils.fetchWeather(cityName)
                .subscribe(weather -> {
                    mAcache.put(BuildVars.WEATHER_CACHE, weather);
                    normalStyleNotification(weather);
                });
    }

    private void normalStyleNotification(Weather weather) {
        Intent intent = new Intent(AutoUpdateService.this, MainActivity.class);
        //intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent =
                PendingIntent.getActivity(AutoUpdateService.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        Notification.Builder builder = new Notification.Builder(AutoUpdateService.this);
        Notification notification = builder.setContentIntent(pendingIntent)
                .setContentTitle(weather.basic.city)
                .setContentText(String.format("%s 当前温度: %s℃ ", weather.now.cond.txt, weather.now.tmp))
                // 这里部分 ROM 无法成功
                .setSmallIcon(mPreferences.getInt(weather.now.cond.txt, R.mipmap.none))
                .build();
        notification.flags = mPreferences.getNotificationModel();
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        // tag和id都是可以拿来区分不同的通知的
        manager.notify(1, notification);
    }
}
