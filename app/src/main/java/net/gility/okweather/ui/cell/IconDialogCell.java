package net.gility.okweather.ui.cell;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import net.gility.okweather.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author Alimy
 */

public class IconDialogCell extends RelativeLayout {

    public static final int TYPE_ONE = 0;
    public static final int TYPE_TWO = 1;

    public interface OnDoneClickListenner {
        void onDoneClickListenner(View view, int type);
    }

    @BindView(R.id.layout_one) LinearLayout mTypeOne;
    @BindView(R.id.layout_two) LinearLayout mTypeTwo;
    @BindView(R.id.radio_one) RadioButton mRadioTypeOne;
    @BindView(R.id.radio_two) RadioButton mRadioTypeTwo;
    @BindView(R.id.done) TextView mDone;

    private OnDoneClickListenner mListenner;
    private int mCheckedType;

    public IconDialogCell(Context context) {
        super(context);
    }

    public IconDialogCell(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public IconDialogCell(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setOnDoneClickListenner(OnDoneClickListenner listenner) {
        mListenner = listenner;
    }

    public void checkType(int type) {
        mCheckedType = type;
        switch (type) {
            case TYPE_ONE:
                mRadioTypeOne.setChecked(true);
                mRadioTypeTwo.setChecked(false);
                break;
            case TYPE_TWO:
                mRadioTypeOne.setChecked(false);
                mRadioTypeTwo.setChecked(true);
                break;
            default:
                mCheckedType = TYPE_ONE;
                mRadioTypeOne.setChecked(true);
                mRadioTypeTwo.setChecked(false);
                break;
        }
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        ButterKnife.bind(this);
        init();
    }

    private void init() {

        mTypeOne.setOnClickListener(v -> checkType(TYPE_ONE));
        mTypeTwo.setOnClickListener(v -> checkType(TYPE_TWO));

        mDone.setOnClickListener(view -> {
                if (mListenner != null) {
                    mListenner.onDoneClickListenner(mDone, mCheckedType);
                }
        });
    }

}
