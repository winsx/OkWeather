package net.gility.okweather.ui.cell;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;

import butterknife.ButterKnife;

/**
 * @author Alimy
 */

public abstract class CardViewCell<T> extends CardView {

    public CardViewCell(Context context) {
        super(context);
    }

    public CardViewCell(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CardViewCell(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void onFinishInflate() {
        super.onFinishInflate();

        ButterKnife.bind(this);
    }

    public abstract void bind(T data);
}
