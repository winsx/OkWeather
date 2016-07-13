package net.gility.okweather.ui.cell;

import android.content.Context;
import android.support.annotation.CallSuper;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;

import butterknife.ButterKnife;

/**
 * @author Alimy
 */

public abstract class BaseCardCell extends CardView {

    public BaseCardCell(Context context) {
        super(context);
    }

    public BaseCardCell(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BaseCardCell(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @CallSuper
    @Override
    public void onFinishInflate() {
        super.onFinishInflate();

        ButterKnife.bind(this);
    }
}
