package net.gility.okweather.ui.adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.gility.okweather.R;

import java.util.ArrayList;

import butterknife.BindView;

public class CityAdapter extends AnimRecyclerViewAdapter<CityAdapter.CityViewHolder> {

    private Context mContext;
    private ArrayList<String> dataList;
    private OnRecyclerViewItemClickListener mOnItemClickListener = null;

    public CityAdapter(Context context, ArrayList<String> dataList) {
        mContext = context;
        this.dataList = dataList;
    }

    @Override
    public CityViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new CityViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_city, parent, false));
    }

    @Override
    public void onBindViewHolder(final CityViewHolder holder, final int position) {

        holder.bindTo(dataList.get(position));
        holder.cardView.setOnClickListener(v -> mOnItemClickListener.onItemClick(v, position));
        // showItemAnim(holder.itemView,position);
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public void setOnItemClickListener(OnRecyclerViewItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    public interface OnRecyclerViewItemClickListener {
        void onItemClick(View view, int pos);
    }

    class CityViewHolder extends InjectViewHolder {

        @BindView(R.id.item_city) TextView itemCity;
        @BindView(R.id.cardView) CardView cardView;

        public CityViewHolder(View itemView) {
            super(itemView);
        }

        public void bindTo(String name) {
            itemCity.setText(name);
        }
    }
}

