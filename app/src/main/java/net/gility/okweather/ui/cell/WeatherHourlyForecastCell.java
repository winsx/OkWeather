package net.gility.okweather.ui.cell;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import net.gility.okweather.R;

import butterknife.BindView;

import static net.gility.okweather.model.Weather.HourlyForecastEntity;

/**
 * @author Alimy
 */

public class WeatherHourlyForecastCell extends LinearCell<HourlyForecastEntity> {

    @BindView(R.id.one_clock) TextView mClock;
    @BindView(R.id.one_temp) TextView mTemp;
    @BindView(R.id.one_humidity) TextView mHumidity;
    @BindView(R.id.one_wind) TextView mWind;

    public WeatherHourlyForecastCell(Context context) {
        super(context);
    }

    public WeatherHourlyForecastCell(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public WeatherHourlyForecastCell(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void bind(HourlyForecastEntity data) {
        try {
                //s.subString(s.length-3,s.length);
                //第一个参数是开始截取的位置，第二个是结束位置。
                String mDate = data.date;
                mClock.setText(mDate.substring(mDate.length() - 5, mDate.length()));
                mTemp.setText(String.format("%s°", data.tmp));
                mHumidity.setText(String.format("%s%%", data.hum));
                mWind.setText(String.format("%sKm", data.wind.spd));
        } catch (Exception e) {
        }
    }
}
