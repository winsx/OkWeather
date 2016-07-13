package net.gility.okweather.ui.cell;

import android.content.Context;
import android.util.AttributeSet;

/**
 * @author Alimy
 * Created by Michael Li on 7/13/16.
 */

public abstract class RelativeCell<T> extends BaseRelativeCell implements BindCell<T> {
    public RelativeCell(Context context) {
        super(context);
    }

    public RelativeCell(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RelativeCell(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
}
