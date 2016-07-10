package net.gility.okweather.dagger;

import net.gility.okweather.config.BuildVars;
import net.gility.okweather.network.HeWeatherService;
import net.gility.okweather.utils.ApiUtils;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;


/**
 * @author Alimy
 */

@Module
public final class ApiModule {
    public static final HttpUrl PRODUCTION_API_URL = HttpUrl.parse(BuildVars.Weather_HOST);

    @Provides @Singleton
    HttpUrl provideBaseUrl() {
        return PRODUCTION_API_URL;
    }

    @Provides @Singleton
    Retrofit provideRetrofit(HttpUrl baseUrl, OkHttpClient client) {
        return new Retrofit.Builder()
                .client(client)
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();
    }


    @Provides @Singleton
    HeWeatherService provideHeWeatherService(Retrofit retrofit) {
        return retrofit.create(HeWeatherService.class);
    }

    @Provides @Singleton
    ApiUtils provideApiUtils(HeWeatherService service) {
        return new ApiUtils(service);
    }
}

