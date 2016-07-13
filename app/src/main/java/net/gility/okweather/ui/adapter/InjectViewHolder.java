package net.gility.okweather.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import butterknife.ButterKnife;

/**
 * @author Alimy
 * Created by Michael Li on 7/13/16.
 */

public class InjectViewHolder extends RecyclerView.ViewHolder {

    public InjectViewHolder(View itemView) {
        super(itemView);

        ButterKnife.bind(this, itemView);
    }
}
