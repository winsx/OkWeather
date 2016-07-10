package net.gility.okweather.ui.fragment;

import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.SwitchPreference;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import net.gility.okweather.R;
import net.gility.okweather.android.AutoUpdateService;
import net.gility.okweather.dagger.Injector;
import net.gility.okweather.model.ChangeIconTypeEvent;
import net.gility.okweather.storage.ACache;
import net.gility.okweather.storage.Preferences;
import net.gility.okweather.ui.cell.IconDialogCell;
import net.gility.okweather.utils.AndroidUtils;
import net.gility.okweather.utils.AppUtils;
import net.gility.okweather.utils.RxBus;

import javax.inject.Inject;

import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

/**
 * @author Alimy
 */

public class SettingsFragment extends PreferenceFragment implements Preference.OnPreferenceClickListener {
    @Inject Preferences mPreferences;
    @Inject ACache mACache;
    @Inject Picasso mPicasso;
    @Inject RxBus mRxBus;

    private Preference mChangeIcons;
    private Preference mChangeUpdate;
    private Preference mClearCache;
    private SwitchPreference mNotificationType;
    private Context mContext;
    private CompositeSubscription mCompositeSubscription = new CompositeSubscription();
    private String[] mIconsText;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.setting);
        Injector.obtain(getActivity().getApplication()).inject(this);

        mContext = getActivity().getApplicationContext();
        mChangeIcons = findPreference(mPreferences.CHANGE_ICONS);
        mChangeUpdate = findPreference(mPreferences.AUTO_UPDATE);
        mClearCache = findPreference(mPreferences.CLEAR_CACHE);
        mIconsText = getResources().getStringArray(R.array.icons);

        mNotificationType = (SwitchPreference) findPreference(Preferences.NOTIFICATION_MODEL);

        mChangeIcons.setSummary(getResources().getStringArray(R.array.icons)[mPreferences.getIconType()]);

        mChangeUpdate.setSummary(mPreferences.getAutoUpdate() == 0 ? "禁止刷新" : "每" + mPreferences.getAutoUpdate() + "小时更新");
        mClearCache.setSummary(AppUtils.getAutoFileOrFilesSize(mContext, AndroidUtils.getCacheDir(mContext) + "/Data"));


        mChangeIcons.setOnPreferenceClickListener(this);
        mChangeUpdate.setOnPreferenceClickListener(this);
        mClearCache.setOnPreferenceClickListener(this);
        mNotificationType.setOnPreferenceClickListener(this);
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        if (mChangeIcons == preference) {
            showIconDialog();
        } else if (mClearCache == preference) {
            mACache.clear();

            // mPicasso.shutdown(); // TODO: have some bug here

            mClearCache.setSummary(AppUtils.getAutoFileOrFilesSize(mContext, AndroidUtils.getCacheDir(mContext) + "/Data"));
            Snackbar.make(getView(), "缓存已清除", Snackbar.LENGTH_SHORT).show();
        } else if (mChangeUpdate == preference) {
            showUpdateDialog();
        } else if (mNotificationType == preference) {
            mNotificationType.setChecked(mNotificationType.isChecked());
            mPreferences.setNotificationModel(
                    mNotificationType.isChecked() ? Notification.FLAG_AUTO_CANCEL : Notification.FLAG_ONGOING_EVENT);
        }
        return false;
    }

    private void showIconDialog() {
        addSubscription(mRxBus.toObserverable(ChangeIconTypeEvent.class).subscribe(
                changeIconTypeEvent -> {
                    mChangeIcons.setSummary(changeIconTypeEvent.type == 0 ? mIconsText[0] : mIconsText[1]);
                    showSnack();
                }
        ));
        IconDialogFragment.instance().show(getFragmentManager(), "IconDialog");
    }

    private void showSnack() {
        Snackbar.make(getView(), "切换成功,返回主界面看效果", Snackbar.LENGTH_INDEFINITE)
                .setAction("确定", view -> getActivity().onBackPressed())
                .show();
    }

    private void showUpdateDialog() {
        //将 SeekBar 放入 Dialog 的方案 http://stackoverflow.com/questions/7184104/how-do-i-put-a-seek-bar-in-an-alert-dialog
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogLayout = inflater.inflate(R.layout.update_dialog, (ViewGroup) getActivity().findViewById(
                R.id.dialog_root));
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                .setView(dialogLayout);
        final AlertDialog alertDialog = builder.create();

        final SeekBar mSeekBar = (SeekBar) dialogLayout.findViewById(R.id.time_seekbar);
        final TextView tvShowHour = (TextView) dialogLayout.findViewById(R.id.tv_showhour);
        TextView tvDone = (TextView) dialogLayout.findViewById(R.id.done);

        mSeekBar.setMax(24);
        mSeekBar.setProgress(mPreferences.getAutoUpdate());
        tvShowHour.setText(String.format("每%s小时", mSeekBar.getProgress()));
        alertDialog.show();

        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tvShowHour.setText(String.format("每%s小时", mSeekBar.getProgress()));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        tvDone.setOnClickListener(v -> {
            mPreferences.setAutoUpdate(mSeekBar.getProgress());
            mChangeUpdate.setSummary(mPreferences.getAutoUpdate() == 0 ? "禁止刷新" : "每" + mPreferences.getAutoUpdate() + "小时更新");
            //需要再调用一次才能生效设置 不会重复的执行onCreate()， 而是会调用onStart()和onStartCommand()。
            getActivity().startService(new Intent(getActivity(), AutoUpdateService.class));
            alertDialog.dismiss();
        });
    }

    private void addSubscription(Subscription subscription) {
        mCompositeSubscription.add(subscription);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mCompositeSubscription.unsubscribe();
    }
}

