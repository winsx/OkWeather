package net.gility.okweather.ui.cell;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;

import butterknife.ButterKnife;

/**
 * @author Alimy
 */

public abstract class CardCell<T> extends BaseCardCell implements BindCell<T>{

    public CardCell(Context context) {
        super(context);
    }

    public CardCell(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CardCell(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
}
