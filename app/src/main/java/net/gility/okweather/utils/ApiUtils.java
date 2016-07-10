package net.gility.okweather.utils;

import net.gility.okweather.config.BuildVars;
import net.gility.okweather.model.Weather;
import net.gility.okweather.network.HeWeatherService;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * @author Alimy
 */

public class ApiUtils {
    HeWeatherService heWeatherService;

    public ApiUtils(HeWeatherService service) {
        this.heWeatherService = service;
    }

    public Observable<Weather> fetchWeather(String city) {
        return heWeatherService.updateWeather(city, BuildVars.KEY)
                //.filter(weatherAPI -> weatherAPI.mHeWeatherDataService30s.get(0).status.equals("ok"))
                .flatMap(weathers -> {
                    Weather weather = weathers.weatherList.get(0);
                    if (weather == null) {
                        return Observable.error(new RuntimeException("API免费次数已用完"));
                    }
                    if (weather.equals("no more requests")) {
                        return Observable.error(new RuntimeException("API免费次数已用完"));
                    } else if (weather.status.equals("unknown city")) {
                        return Observable.error(new RuntimeException("未知城市"));
                    }
                    return Observable.just(weathers);
                })
                .map(weathers -> weathers.weatherList.get(0))
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
