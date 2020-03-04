package com.example.inventrax.falconOMS.adapters;

import android.content.Context;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.inventrax.falconOMS.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by padmaja on 13/07/19.
 */
public class OffersAdapter extends RecyclerView.Adapter<OffersAdapter.ViewHolder> {
    private List mCode;
    private List discountDesc;
    private Context context;
    NestedScrollView nested;


    public OffersAdapter(Context applicationContext, List discountDesc, List mCode, NestedScrollView nested) {
        this.context = applicationContext;
        this.mCode = mCode;
        this.discountDesc = discountDesc;
        this.nested = nested;

    }

    public OffersAdapter(Context applicationContext, List discountDesc, NestedScrollView nested) {
        this.context = applicationContext;
        this.discountDesc = discountDesc;
        mCode = new ArrayList();
        this.nested = nested;

    }

    public OffersAdapter() {

    }

    @Override
    public OffersAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.offers_list, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(OffersAdapter.ViewHolder viewHolder, int i) {

        if (mCode.size() > 0)
            viewHolder.skuName.setText((CharSequence) mCode.get(i));

        viewHolder.label.setText((CharSequence) discountDesc.get(i));

        if (i == (discountDesc.size() - 1))
            nested.smoothScrollTo(0, 0);

    }

    @Override
    public int getItemCount() {
        return discountDesc.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView label, skuName;


        public ViewHolder(View view) {
            super(view);

            // Initializing views
            label = (TextView) view.findViewById(R.id.label);
            skuName = (TextView) view.findViewById(R.id.skuName);


        }
    }


}
