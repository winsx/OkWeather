package net.gility.okweather.network;

import net.gility.okweather.model.Weathers;

import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

/**
 * @author Alimy
 */
public interface HeWeatherService {

    @GET("weather")
    Observable<Weathers> updateWeather(@Query("city") String city, @Query("key") String key);
}
