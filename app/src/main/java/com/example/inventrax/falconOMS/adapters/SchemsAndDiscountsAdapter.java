package com.example.inventrax.falconOMS.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.inventrax.falconOMS.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by padmaja on 14/11/19.
 */
public class SchemsAndDiscountsAdapter extends RecyclerView.Adapter<SchemsAndDiscountsAdapter.ViewHolder> {

    private List items;
    private Context context;

    public SchemsAndDiscountsAdapter(Context applicationContext, ArrayList items) {
        this.context = applicationContext;
        this.items = items;

    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view;

        view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_schemes_discounts, viewGroup, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {

        List discountStrings = items;

        viewHolder.txtDiscountString.setText(discountStrings.get(i).toString());

    }

    @Override
    public int getItemCount() {
        return items.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView txtDiscountString;

        public ViewHolder(View view) {
            super(view);
            // Initializing Views
            txtDiscountString = (TextView) view.findViewById(R.id.txtDiscountString);

        }
    }

    // Item click listener interface
    public interface OnItemClickListener {
        void onItemClick(int pos);


    }
}
