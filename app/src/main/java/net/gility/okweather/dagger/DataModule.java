package net.gility.okweather.dagger;

import android.app.Application;
import android.support.constraint.BuildConfig;

import com.hwangjr.rxbus.Bus;
import com.jakewharton.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;
import com.tbruyelle.rxpermissions.RxPermissions;

import net.gility.okweather.storage.ACache;
import net.gility.okweather.storage.LocationDB;
import net.gility.okweather.storage.Preferences;
import net.gility.okweather.utils.AndroidUtils;

import java.io.File;
import java.util.concurrent.TimeUnit;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import rx.subscriptions.CompositeSubscription;

import static com.jakewharton.byteunits.DecimalByteUnit.MEGABYTES;

/**
 * @author Alimy
 */

@Module(
        includes = ApiModule.class
)
public class DataModule {
    static final int DISK_CACHE_SIZE = (int) MEGABYTES.toBytes(50);

    @Provides CompositeSubscription provideCompositeSubscription() {
        return new CompositeSubscription();
    }

    @Provides @Singleton RxPermissions provideRxPermissions(Application app) {
        return RxPermissions.getInstance(app);
    }

    @Provides @Singleton Bus ProvideBus() {
        return new Bus();
    }

    @Provides @Singleton
    Preferences providePreferences(Application app) {
        return new Preferences(app);
    }

    @Provides @Singleton
    ACache provideACache(Application app) {
        String cacheDir = AndroidUtils.getCacheDir(app);
        File cacheFile = new File(cacheDir, "Data");
        return ACache.get(cacheFile);
    }

    @Provides @Singleton
    LocationDB provideLocationDB(Application app) {
        return new LocationDB(app);
    }

    @Provides @Singleton
    OkHttpClient provideOkHttpClient(Application app) {
        return createOkHttpClient(app).build();
    }

    @Provides @Singleton Picasso providePicasso(Application app, OkHttpClient client) {
        return new Picasso.Builder(app)
                .downloader(new OkHttp3Downloader(client))
                // .listener((picasso, uri, e) -> Timber.e(e, "Failed to load image: %s", uri))
                .build();
    }

    static OkHttpClient.Builder createOkHttpClient(Application app) {

        OkHttpClient.Builder builder = new  OkHttpClient.Builder();
        if (BuildConfig.DEBUG){
            // https://drakeet.me/retrofit-2-0-okhttp-3-0-config
            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            builder.addInterceptor(loggingInterceptor);
        }

        // Install an HTTP cache in the application cache directory.
        File cacheDir = new File(app.getCacheDir(), "OkWeatherCache");
        Cache cache = new Cache(cacheDir, DISK_CACHE_SIZE);

        Interceptor cacheInterceptor = chain -> {
            Request request = chain.request();
            if (!AndroidUtils.isNetworkConnected(app.getApplicationContext())) {
                request = request.newBuilder()
                        .cacheControl(CacheControl.FORCE_CACHE)
                        .build();
            }
            Response response = chain.proceed(request);
            if (AndroidUtils.isNetworkConnected(app.getApplicationContext())) {
                // 有网络时 设置缓存超时时间0个小时
                response.newBuilder()
                        .header("Cache-Control", "public, max-age=0")
                        .build();
            } else {
                // 无网络时，设置超时为4周
                int maxStale = 60 * 60 * 24 * 28;
                response.newBuilder()
                        .header("Cache-Control", "public, only-if-cached, max-stale=" + maxStale)
                        .build();
            }
            return response;
        };
        builder.cache(cache).addInterceptor(cacheInterceptor);
        //设置超时
        builder.connectTimeout(15, TimeUnit.SECONDS);
        builder.readTimeout(20, TimeUnit.SECONDS);
        builder.writeTimeout(20, TimeUnit.SECONDS);
        //错误重连
        builder.retryOnConnectionFailure(true);

        return builder;
    }
}
