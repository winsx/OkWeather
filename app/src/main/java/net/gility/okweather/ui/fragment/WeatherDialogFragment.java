package net.gility.okweather.ui.fragment;

import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import net.gility.okweather.R;
import net.gility.okweather.model.Weather;
import net.gility.okweather.ui.cell.WeatherDialogCell;

/**
 * @author Alimy
 */

public class WeatherDialogFragment extends DialogFragment {

    private Weather mWeather;

    public static WeatherDialogFragment instance(@NonNull Weather weather) {
        WeatherDialogFragment fragment = new  WeatherDialogFragment();
        fragment.bindTo(weather);

        return fragment;
    }

    private void bindTo(Weather weather) {
        mWeather = weather;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);

        LayoutInflater inflater = LayoutInflater.from(getActivity());
        WeatherDialogCell cell  = (WeatherDialogCell) inflater.inflate(R.layout.cell_dialog_weather,
                (ViewGroup) getActivity().findViewById(R.id.weather_dialog_root));
        cell.bind(mWeather);

        return new AlertDialog.Builder(getActivity()).setView(cell).create();
    }
}