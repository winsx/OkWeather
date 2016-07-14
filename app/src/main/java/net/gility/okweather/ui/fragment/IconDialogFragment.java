package net.gility.okweather.ui.fragment;

import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hwangjr.rxbus.Bus;

import net.gility.okweather.R;
import net.gility.okweather.dagger.Injector;
import net.gility.okweather.model.BusAction;
import net.gility.okweather.storage.Preferences;
import net.gility.okweather.ui.cell.IconDialogCell;

import javax.inject.Inject;

/**
 * @author Alimy
 */

public class IconDialogFragment extends DialogFragment {
    @Inject Preferences mPreferences;
    @Inject Bus mBus;

    public static IconDialogFragment instance() {
        return new IconDialogFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Injector.instance.inject(this);
        mBus.register(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        mBus.unregister(this);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);

        LayoutInflater inflater = LayoutInflater.from(getActivity());
        IconDialogCell iconDialogCell = (IconDialogCell) inflater.inflate(R.layout.cell_dialog_icon, (ViewGroup) getActivity().findViewById(R.id.dialog_root));
        iconDialogCell.checkType(mPreferences.getIconType());
        iconDialogCell.setOnDoneClickListenner(new IconDialogCell.OnDoneClickListenner() {
            @Override
            public void onDoneClickListenner(View view, int type) {
                mPreferences.setIconType(type);
                mBus.post(BusAction.CHANGE_ICONS_TYPE, type);

                switch (type) {
                    case IconDialogCell.TYPE_ONE:
                        mPreferences.begin()
                                .putInt("未知", R.mipmap.none)
                                .putInt("晴", R.mipmap.type_one_sunny)
                                .putInt("阴", R.mipmap.type_one_cloudy)
                                .putInt("多云", R.mipmap.type_one_cloudy)
                                .putInt("少云", R.mipmap.type_one_cloudy)
                                .putInt("晴间多云", R.mipmap.type_one_cloudytosunny)
                                .putInt("小雨", R.mipmap.type_one_light_rain)
                                .putInt("中雨", R.mipmap.type_one_light_rain)
                                .putInt("大雨", R.mipmap.type_one_heavy_rain)
                                .putInt("阵雨", R.mipmap.type_one_thunderstorm)
                                .putInt("雷阵雨", R.mipmap.type_one_thunder_rain)
                                .putInt("霾", R.mipmap.type_one_fog)
                                .putInt("雾", R.mipmap.type_one_fog)
                                .apply();
                        break;
                    case IconDialogCell.TYPE_TWO:
                        mPreferences.begin()
                                .putInt("未知", R.mipmap.none)
                                .putInt("晴", R.mipmap.type_two_sunny)
                                .putInt("阴", R.mipmap.type_two_cloudy)
                                .putInt("多云", R.mipmap.type_two_cloudy)
                                .putInt("少云", R.mipmap.type_two_cloudy)
                                .putInt("晴间多云", R.mipmap.type_two_cloudytosunny)
                                .putInt("小雨", R.mipmap.type_two_light_rain)
                                .putInt("中雨", R.mipmap.type_two_rain)
                                .putInt("大雨", R.mipmap.type_two_rain)
                                .putInt("阵雨", R.mipmap.type_two_rain)
                                .putInt("雷阵雨", R.mipmap.type_two_thunderstorm)
                                .putInt("霾", R.mipmap.type_two_haze)
                                .putInt("雾", R.mipmap.type_two_fog)
                                .putInt("雨夹雪", R.mipmap.type_two_snowrain)
                                .apply();
                        break;
                }

                IconDialogFragment.this.dismiss();
            }
        });
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity()).setView(iconDialogCell);
        return builder.create();
    }
}
