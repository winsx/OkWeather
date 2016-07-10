package net.gility.okweather.ui.cell;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import net.gility.okweather.R;

import butterknife.BindView;

import static net.gility.okweather.model.Weather.SuggestionEntity;

/**
 * @author Alimy
 */

public class WeatherSuggestionCell extends CardViewCell<SuggestionEntity> {
    @BindView(R.id.cloth_brief) TextView clothBrief;
    @BindView(R.id.cloth_txt) TextView clothTxt;
    @BindView(R.id.sport_brief) TextView sportBrief;
    @BindView(R.id.sport_txt) TextView sportTxt;
    @BindView(R.id.travel_brief) TextView travelBrief;
    @BindView(R.id.travel_txt) TextView travelTxt;
    @BindView(R.id.flu_brief) TextView fluBrief;
    @BindView(R.id.flu_txt) TextView fluTxt;

    public WeatherSuggestionCell(Context context) {
        super(context);
    }

    public WeatherSuggestionCell(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public WeatherSuggestionCell(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void bind(SuggestionEntity suggestion) {
        try {
            clothBrief.setText(String.format("穿衣指数---%s", suggestion.drsg.brf));
            clothTxt.setText(suggestion.drsg.txt);

            sportBrief.setText(String.format("运动指数---%s", suggestion.sport.brf));
            sportTxt.setText(suggestion.sport.txt);

            travelBrief.setText(String.format("旅游指数---%s", suggestion.trav.brf));
            travelTxt.setText(suggestion.trav.txt);

            fluBrief.setText(String.format("感冒指数---%s", suggestion.flu.brf));
            fluTxt.setText(suggestion.flu.txt);
        } catch (Exception e) {
        }
    }
}
