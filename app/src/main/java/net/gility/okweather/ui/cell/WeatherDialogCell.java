package net.gility.okweather.ui.cell;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import net.gility.okweather.R;
import net.gility.okweather.dagger.Injector;
import net.gility.okweather.model.Weather;
import net.gility.okweather.storage.Preferences;
import net.gility.okweather.utils.AppUtils;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author Alimy
 */

public class WeatherDialogCell extends RelativeLayout {

    @BindView(R.id.weather_dialog_root) RelativeLayout mRootView;
    @BindView(R.id.dialog_city) TextView mCity;
    @BindView(R.id.dialog_temp) TextView mTemp;
    @BindView(R.id.dialog_icon) ImageView mIcon;

    @Inject Preferences mPreferences;
    @Inject Picasso mPicasso;

    public WeatherDialogCell(Context context) {
        super(context);
    }

    public WeatherDialogCell(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public WeatherDialogCell(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void bind(Weather weather) {
        switch (AppUtils.getWeatherType(Integer.parseInt(weather.now.cond.code))) {
            case "晴":
                mRootView.setBackgroundResource(R.mipmap.dialog_bg_sunny);
                break;
            case "阴":
                mRootView.setBackgroundResource(R.mipmap.dialog_bg_cloudy);
                break;
            case "雨":
                mRootView.setBackgroundResource(R.mipmap.dialog_bg_rainy);
                break;
            default:
                break;
        }

        mCity.setText(weather.basic.city);
        mTemp.setText(String.format("%s°", weather.now.tmp));

        mPicasso.with(getContext())
                .load(mPreferences.getInt(weather.now.cond.txt, R.mipmap.none))
                .into(mIcon, new Callback() {
                    @Override
                    public void onSuccess() {
                        mIcon.setColorFilter(Color.WHITE);
                    }

                    @Override
                    public void onError() {

                    }
                });
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        ButterKnife.bind(this);
        Injector.instance.inject(this);
    }
}
