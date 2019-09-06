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

/**
 * Created by padmaja on 13/07/19.
 */
public class OffersAdapter extends RecyclerView.Adapter<OffersAdapter.ViewHolder> {
    private ArrayList items;
    private Context context;
    NestedScrollView nested;



    public OffersAdapter(Context applicationContext, ArrayList itemArrayList, NestedScrollView nested) {
        this.context = applicationContext;
        this.items = itemArrayList;
        this.nested=nested;

    }

    public OffersAdapter() {

    }

    @Override
    public OffersAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_offer, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(OffersAdapter.ViewHolder viewHolder, int i) {


        viewHolder.label.setText((CharSequence) items.get(i));
        if(i==(items.size()-1))
            nested.smoothScrollTo(0,0);


    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView label;


        public ViewHolder(View view) {
            super(view);

            // Initializing views
            label = (TextView) view.findViewById(R.id.label);



        }
    }


}
