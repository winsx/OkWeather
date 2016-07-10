package net.gility.okweather.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import net.gility.okweather.R;
import net.gility.okweather.dagger.Injector;
import net.gility.okweather.model.Weather;
import net.gility.okweather.storage.Preferences;
import net.gility.okweather.ui.cell.WeatherDailyForecastCell;
import net.gility.okweather.ui.cell.WeatherHourlyForecastCell;
import net.gility.okweather.ui.cell.WeatherSuggestionCell;
import net.gility.okweather.ui.cell.WeatherTemperatureCell;
import net.gility.okweather.utils.AppUtils;

import java.util.List;

import javax.inject.Inject;

/**
 * @author Alimy
 */
public class WeatherAdapter extends AnimRecyclerViewAdapter<RecyclerView.ViewHolder> {
    private static String TAG = WeatherAdapter.class.getSimpleName();

    private Context mContext;
    private final int TYPE_ONE = 0;

    private final int TYPE_TWO = 1;
    private final int TYPE_THREE = 2;
    private final int TYPE_FORE = 3;

    private Weather mWeather;

    public WeatherAdapter(Context context) {
        mContext = context;
        this.mWeather = new Weather();
    }

    public void updateData(Weather weather) {
        mWeather = weather;
        this.notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        if (position == TYPE_ONE) {
            return TYPE_ONE;
        }
        if (position == TYPE_TWO) {
            return TYPE_TWO;
        }
        if (position == TYPE_THREE) {
            return TYPE_THREE;
        }
        if (position == TYPE_FORE) {
            return TYPE_FORE;
        }
        return super.getItemViewType(position);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case TYPE_ONE:
                return new NowWeatherViewHolder(
                        LayoutInflater.from(mContext).inflate(R.layout.cell_weather_temperature, parent, false));
            case TYPE_TWO:
                return new HoursWeatherViewHolder(
                        LayoutInflater.from(mContext).inflate(R.layout.item_hour_info, parent, false));
            case TYPE_THREE:
                return new SuggestionViewHolder(
                        LayoutInflater.from(mContext).inflate(R.layout.cell_weather_suggestion, parent, false));
            case TYPE_FORE:
                return new ForecastViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_forecast, parent, false));
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        int itemType = getItemViewType(position);
        switch (itemType) {
            case TYPE_ONE:
                ((NowWeatherViewHolder) holder).bind(mWeather);
                break;
            case TYPE_TWO:
                ((HoursWeatherViewHolder) holder).bind(mWeather.hourlyForecast);
                break;
            case TYPE_THREE:
                ((SuggestionViewHolder) holder).bind(mWeather.suggestion);
                break;
            case TYPE_FORE:
                ((ForecastViewHolder) holder).bind(mWeather.dailyForecast);
                break;
            default:
                break;
        }
        showItemAnim(holder.itemView, position);
    }

    @Override
    public int getItemCount() {
        return mWeather.status != null ? 4 : 0;
    }

    /**
     * 当前天气情况
     */
    class NowWeatherViewHolder extends RecyclerView.ViewHolder {

        public NowWeatherViewHolder(View itemView) {
            super(itemView);
        }

        public void bind(Weather weather) {
            ((WeatherTemperatureCell) itemView).bind(weather);

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onItemClick(mWeather);
                }
            });
        }
    }

    /**
     * 当日小时预告
     */
    class HoursWeatherViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout mHourInfoLinearlayout;

        public HoursWeatherViewHolder(View itemView) {
            super(itemView);
            mHourInfoLinearlayout = (LinearLayout) itemView.findViewById(R.id.item_hour_info_linearlayout);
        }

        public void bind(List<Weather.HourlyForecastEntity> hourlyForecasts) {
            mHourInfoLinearlayout.removeAllViews();
            for (int i = 0; i < hourlyForecasts.size(); i++) {
                View view = View.inflate(mContext, R.layout.cell_weather_hourly_forecast, null);
                ((WeatherHourlyForecastCell) view).bind(hourlyForecasts.get(i));

                mHourInfoLinearlayout.addView(view);
            }
        }
    }

    /**
     * 当日建议
     */
    class SuggestionViewHolder extends RecyclerView.ViewHolder {

        public SuggestionViewHolder(View itemView) {
            super(itemView);
        }

        public void bind(Weather.SuggestionEntity suggestion) {
            ((WeatherSuggestionCell) itemView).bind(suggestion);
        }
    }

    /**
     * 未来天气
     */
    class ForecastViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout mForecastLinear;

        public ForecastViewHolder(View itemView) {
            super(itemView);
            mForecastLinear = (LinearLayout) itemView.findViewById(R.id.forecast_linear);
        }

        public void bind(List<Weather.DailyForecastEntity> dailyForecasts) {
            final int items = dailyForecasts.size();
            WeatherDailyForecastCell[] mCells = new WeatherDailyForecastCell[items];

            mForecastLinear.removeAllViews();

            for (int i = 0; i < dailyForecasts.size(); i++) {
                mCells[i] = (WeatherDailyForecastCell) View.inflate(mContext, R.layout.cell_weather_daily_forecast, null);
                mCells[i].bind(dailyForecasts.get(i));

                mForecastLinear.addView(mCells[i]);
            }

            if (items > 1) {
                mCells[0].setDate("今日");
                mCells[1].setDate("明日");
            } else {
                mCells[0].setDate("今日");
            }
        }
    }

    public interface OnItemClickListener {
        void onItemClick(Weather mWeather);
    }

    private OnItemClickListener listener;

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }
}
