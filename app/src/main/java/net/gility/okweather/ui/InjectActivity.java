package net.gility.okweather.ui;

import butterknife.ButterKnife;

/**
 * @author Alimy
 * Created by Michael Li on 7/16/16.
 */

public abstract class InjectActivity extends BaseActivity {

    @Override
    public void onContentChanged() {
        super.onContentChanged();

        ButterKnife.bind(this);
    }
}
