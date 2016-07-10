package net.gility.okweather.model;

/**
 * @author Alimy
 */
public class UpdateCityWeatherEvent {
    public Weather weather;

    public UpdateCityWeatherEvent(Weather weather) {
        this.weather = weather;
    }
}
