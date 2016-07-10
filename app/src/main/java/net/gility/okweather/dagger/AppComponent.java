package net.gility.okweather.dagger;

import net.gility.okweather.android.AutoUpdateService;
import net.gility.okweather.android.OkWeatherApp;
import net.gility.okweather.model.Weather;
import net.gility.okweather.ui.AboutActivity;
import net.gility.okweather.ui.ChoiceCityActivity;
import net.gility.okweather.ui.MainActivity;
import net.gility.okweather.ui.SettingsActivity;
import net.gility.okweather.ui.adapter.WeatherAdapter;
import net.gility.okweather.ui.cell.WeatherDailyForecastCell;
import net.gility.okweather.ui.cell.WeatherDialogCell;
import net.gility.okweather.ui.cell.WeatherTemperatureCell;
import net.gility.okweather.ui.fragment.AboutFragment;
import net.gility.okweather.ui.fragment.IconDialogFragment;
import net.gility.okweather.ui.fragment.MainFragment;
import net.gility.okweather.ui.fragment.SettingsFragment;
import net.gility.okweather.ui.fragment.WeatherDialogFragment;

import javax.inject.Singleton;

import dagger.Component;

/**
 * @author Alimy
 */

@Singleton
@Component(
        modules = {
                AppModule.class,
                DataModule.class,
                UiModule.class
        }
)
public interface AppComponent {
    void inject(AboutActivity aboutActivity);
    void inject(AutoUpdateService autoUpdateService);
    void inject(SettingsActivity settingsActivity);
    void inject(SettingsFragment settingsFragment);
    void inject(ChoiceCityActivity choiceCityActivity);
    void inject(MainActivity mainActivity3);
    void inject(OkWeatherApp okWeatherApp);
    void inject(IconDialogFragment iconDialogFragment);
    void inject(MainFragment mainFragment);
    void inject(AboutFragment aboutFragment);
    void inject(WeatherAdapter weatherAdapter);
    void inject(WeatherDialogCell weatherDialogCell);
    void inject(WeatherDailyForecastCell weatherDailyForecastCell);
    void inject(WeatherTemperatureCell weatherTemperatureCell);
}
