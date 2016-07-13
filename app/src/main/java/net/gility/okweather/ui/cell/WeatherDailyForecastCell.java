package net.gility.okweather.ui.cell;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import net.gility.okweather.R;
import net.gility.okweather.dagger.Injector;
import net.gility.okweather.storage.Preferences;
import net.gility.okweather.utils.AppUtils;

import javax.inject.Inject;

import butterknife.BindView;

import static net.gility.okweather.model.Weather.DailyForecastEntity;

/**
 * @author Alimy
 */

public class WeatherDailyForecastCell extends LinearCell<DailyForecastEntity> {

    @BindView(R.id.forecast_date) public TextView mDate;
    @BindView(R.id.forecast_temp) TextView mTemp;
    @BindView(R.id.forecast_txt) TextView mTxt;
    @BindView(R.id.forecast_icon) ImageView mIcon;

    @Inject Picasso mPicasso;
    @Inject Preferences mPreferences;

    public WeatherDailyForecastCell(Context context) {
        this(context, null);
    }

    public WeatherDailyForecastCell(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WeatherDailyForecastCell(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        Injector.instance.inject(this);
    }

    @Override
    public void bindTo(DailyForecastEntity data) {
        try {
            try {
                mDate.setText(AppUtils.dayForWeek(data.date));
            } catch (Exception e) {
            }
                mPicasso.load(mPreferences.getInt(data.cond.txtD, R.mipmap.none)).into(mIcon);
                mTemp.setText(String.format("%s° %s°", data.tmp.min, data.tmp.max));
                mTxt.setText(String.format("%s。 最高%s℃。 %s %s %s km/h。 降水几率 %s%%。",
                                data.cond.txtD, data.tmp.max, data.wind.sc,
                                data.wind.dir, data.wind.spd, data.pop));
            } catch (Exception e) {
        }
    }

    public void setDate(@NonNull  final String date) {
        mDate.setText(date);
    }
}
