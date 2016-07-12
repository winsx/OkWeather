package net.gility.okweather.android;

import android.app.Application;
import android.os.Build;
import android.os.StrictMode;
import android.support.annotation.NonNull;

import net.gility.okweather.BuildConfig;
import net.gility.okweather.R;
import net.gility.okweather.dagger.AppComponent;
import net.gility.okweather.dagger.AppModule;
import net.gility.okweather.dagger.DaggerAppComponent;
import net.gility.okweather.dagger.Injector;
import net.gility.okweather.storage.Preferences;
import net.gility.okweather.ui.cell.IconDialogCell;
import net.gility.okweather.ui.misc.ActivityHierarchyServer;

import javax.inject.Inject;


/**
 * @author Alimy
 */

public class OkWeatherApp extends Application {
    @Inject ActivityHierarchyServer activityHierarchyServer;
    @Inject Preferences mPreferences;

    @Override
    public void onCreate() {
        super.onCreate();

        Injector.build(this);
        Injector.instance.inject(this);

        registerActivityLifecycleCallbacks(activityHierarchyServer);

        if (BuildConfig.DEBUG) {
            StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                    .detectAll()
                    .penaltyLog()
                    .build());
            StrictMode.ThreadPolicy.Builder threadPolicyBuilder = new StrictMode.ThreadPolicy.Builder()
                    .detectAll()
                    .penaltyLog();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                threadPolicyBuilder.penaltyDeathOnNetwork();
            }
            StrictMode.setThreadPolicy(threadPolicyBuilder.build());
        }

        checkFirstStart();
    }

    private void checkFirstStart() {
        if (mPreferences.getInt(Preferences.FIRST_START, 1) == 1) {
            mPreferences.setIconType(IconDialogCell.TYPE_ONE);
            mPreferences.begin()
                    .putInt("未知", R.mipmap.none)
                    .putInt("晴", R.mipmap.type_one_sunny)
                    .putInt("阴", R.mipmap.type_one_cloudy)
                    .putInt("多云", R.mipmap.type_one_cloudy)
                    .putInt("少云", R.mipmap.type_one_cloudy)
                    .putInt("晴间多云", R.mipmap.type_one_cloudytosunny)
                    .putInt("小雨", R.mipmap.type_one_light_rain)
                    .putInt("中雨", R.mipmap.type_one_light_rain)
                    .putInt("大雨", R.mipmap.type_one_heavy_rain)
                    .putInt("阵雨", R.mipmap.type_one_thunderstorm)
                    .putInt("雷阵雨", R.mipmap.type_one_thunder_rain)
                    .putInt("霾", R.mipmap.type_one_fog)
                    .putInt("雾", R.mipmap.type_one_fog)
                    .apply();
        }
    }

}
