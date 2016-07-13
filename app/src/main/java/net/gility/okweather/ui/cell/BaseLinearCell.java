package net.gility.okweather.ui.cell;

import android.content.Context;
import android.support.annotation.CallSuper;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import butterknife.ButterKnife;

/**
 * @author Alimy
 */

public abstract class BaseLinearCell extends LinearLayout {
    public BaseLinearCell(Context context) {
        super(context);
    }

    public BaseLinearCell(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BaseLinearCell(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @CallSuper
    @Override
    public void onFinishInflate() {
        super.onFinishInflate();

        ButterKnife.bind(this);
    }
}
