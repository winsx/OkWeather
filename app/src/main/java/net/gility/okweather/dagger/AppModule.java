package net.gility.okweather.dagger;

import android.app.Application;
import android.content.ClipboardManager;
import android.content.Context;

import net.gility.okweather.android.OkWeatherApp;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * @author Alimy
 */

@Module
public final class AppModule {
    private final OkWeatherApp app;

    public AppModule(OkWeatherApp app) {
        this.app = app;
    }

    @Provides @Singleton
    Application provideApplication() {
        return app;
    }

    @Provides
    ClipboardManager provideClipboardManager(Application application) {
        return (ClipboardManager) application.getSystemService(Context.CLIPBOARD_SERVICE);
    }
}
