package net.gility.okweather.ui;

import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import net.gility.okweather.R;
import net.gility.okweather.dagger.Injector;
import net.gility.okweather.storage.Preferences;
import net.gility.okweather.ui.fragment.SettingsFragment;

import javax.inject.Inject;

import butterknife.BindView;

public class SettingsActivity extends BaseActivity {
    @Inject Preferences mPreferences;
    @BindView(R.id.toolbar) Toolbar mToolbar;
    @BindView(R.id.appbar_layout) AppBarLayout mAppBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //mToolbar.setTitle("设置"); don't work and i find this
        //http://stackoverflow.com/questions/26486730/in-android-app-toolbar-settitle-method-has-no-effect-application-name-is-shown
        setContentView(R.layout.activity_setting);

        Injector.obtain(getApplication()).inject(this);

        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("设置");
        }

        setStatusBarColor(R.color.colorPrimary);
        if (mPreferences.getCurrentHour() < 6 || mPreferences.getCurrentHour() > 18) {
            mToolbar.setBackgroundColor(ContextCompat.getColor(this, R.color.colorSunset));
            setStatusBarColor(R.color.colorSunset);
        }

        if (Build.VERSION.SDK_INT >= 21) {
            mAppBar.setElevation(10.6f);
        }

        getFragmentManager().beginTransaction().replace(R.id.framelayout, new SettingsFragment()).commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }


}
