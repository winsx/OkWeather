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
import net.gility.okweather.ui.fragment.AboutFragment;
import net.gility.okweather.ui.fragment.WebviewFragment;

import javax.inject.Inject;

import butterknife.BindView;

/**
 * @author Alimy
 */

public class AboutActivity extends InjectActivity {

    AboutFragment mAboutFragment = new AboutFragment();

    @BindView(R.id.appbar_layout) AppBarLayout mAppBar;
    @BindView(R.id.toolbar) Toolbar mToolbar;

    @Inject Preferences mPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        Injector.instance.inject(this);

        initActionBar();

        getFragmentManager().beginTransaction()
                    .add(R.id.framelayout, mAboutFragment)
                    .commit();
    }

    private void initActionBar() {
        setSupportActionBar(mToolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("关于");
        }
        if (Build.VERSION.SDK_INT >= 21) {
            mAppBar.setElevation(10.6f);
        }

        setStatusBarColor(R.color.colorPrimary);
        if (mPreferences.getCurrentHour() < 6 || mPreferences.getCurrentHour() > 18) {
            mToolbar.setBackgroundColor(ContextCompat.getColor(this, R.color.colorSunset));
            setStatusBarColor(R.color.colorSunset);
        }
    }

    @Override public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        WebviewFragment fragment = (WebviewFragment) getFragmentManager().findFragmentByTag(WebviewFragment.TAG);
        if (fragment != null && fragment.mWebView.canGoBack()) {
            fragment.mWebView.goBack();
            return;
        }
        super.onBackPressed();
    }
}
