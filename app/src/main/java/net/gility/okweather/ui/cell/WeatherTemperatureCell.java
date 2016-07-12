package net.gility.okweather.ui.cell;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import net.gility.okweather.R;
import net.gility.okweather.dagger.Injector;
import net.gility.okweather.model.Weather;
import net.gility.okweather.storage.Preferences;
import net.gility.okweather.utils.AppUtils;

import javax.inject.Inject;

import butterknife.BindView;

/**
 * @author Alimy
 */

public class WeatherTemperatureCell extends CardViewCell<Weather> {

    @BindView(R.id.weather_icon) ImageView weatherIcon;
    @BindView(R.id.temp_flu) TextView tempFlu;
    @BindView(R.id.temp_max) TextView tempMax;
    @BindView(R.id.temp_min) TextView tempMin;
    @BindView(R.id.temp_pm) TextView tempPm;
    @BindView(R.id.temp_quality) TextView tempQuality;

    @Inject Picasso mPicasso;
    @Inject Preferences mPreferences;

    public WeatherTemperatureCell(Context context) {
        this(context, null);
    }

    public WeatherTemperatureCell(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WeatherTemperatureCell(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        Injector.instance.inject(this);
    }

    @Override
    public void bind(Weather weather) {
        try {
            tempFlu.setText(String.format("%s℃", weather.now.tmp));
            tempMax.setText(String.format("↑ %s °", weather.dailyForecast.get(0).tmp.max));
            tempMin.setText(String.format("↓ %s °", weather.dailyForecast.get(0).tmp.min));
            tempPm.setText(AppUtils.safeText("PM25： ", weather.aqi.city.pm25));
            tempQuality.setText(AppUtils.safeText("空气质量： ", weather.aqi.city.qlty));
            mPicasso.load(mPreferences.getInt(weather.now.cond.txt, R.mipmap.none)).into(weatherIcon);
        } catch (Exception e) {
        }
    }
}
