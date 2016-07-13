package net.gility.okweather.dagger;

import net.gility.okweather.android.OkWeatherApp;

/**
 * @author Alimy
 */

public final class Injector {
    public static AppComponent instance;

    public static void initStatic(OkWeatherApp app) {
        instance = DaggerAppComponent.builder()
                .appModule(new AppModule(app))
                .build();
    }

    private Injector() {
        throw new AssertionError("No instances.");
    }
}