package net.gility.okweather.ui.cell;

import android.content.Context;
import android.support.annotation.CallSuper;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import butterknife.ButterKnife;

/**
 * @author Alimy
 * Created by Michael Li on 7/13/16.
 */

public abstract class BaseRelativeCell extends RelativeLayout {
    public BaseRelativeCell(Context context) {
        super(context);
    }

    public BaseRelativeCell(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BaseRelativeCell(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @CallSuper
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        ButterKnife.bind(this);
    }
}
