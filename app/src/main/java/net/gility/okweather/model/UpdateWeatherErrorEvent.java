package net.gility.okweather.model;

/**
 * @author Alimy
 */
public class UpdateWeatherErrorEvent {
    public Throwable throwable;

    public UpdateWeatherErrorEvent(Throwable throwable) {
        this.throwable = throwable;
    }
}
