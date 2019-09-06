package com.example.inventrax.falconOMS.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.inventrax.falconOMS.R;

import java.util.ArrayList;

/**
 * Created by padmaja on 13/07/19.
 */
public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {
    private ArrayList items;
    private Context context;
    OnItemClickListener listener;

    public CartAdapter(Context applicationContext, ArrayList itemArrayList, OnItemClickListener mlistener) {
        this.context = applicationContext;
        this.items = itemArrayList;
        listener = mlistener;
    }

    @Override
    public CartAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_cart_item, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CartAdapter.ViewHolder viewHolder, int i) {
        viewHolder.txtItemName.setText(items.get(i).toString());
       /* viewHolder.txtItemDesc.setText(items.get(i).getHtmlUrl());
        viewHolder.txtPrice.setText(items.get(i).getHtmlUrl());
        viewHolder.txtDiscount.setText(items.get(i).getHtmlUrl());*/

        /*Picasso.with(context)
                .load(items.get(i).getAvatarUrl())
                .placeholder(R.drawable.load)
                .into(viewHolder.ivItem);*/


        viewHolder.ivItem.setImageResource(R.drawable.load);

    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView txtItemName, txtItemDesc, txtPrice, txtDeliveryDate;
        private EditText etQtyCart;
        private ImageView ivItem, ivDeleteItem;


        public ViewHolder(View view) {
            super(view);
            // Initializing views
            txtItemName = (TextView) view.findViewById(R.id.txtItemName);
            txtItemDesc = (TextView) view.findViewById(R.id.txtItemDesc);
            txtPrice = (TextView) view.findViewById(R.id.txtPrice);
            txtDeliveryDate = (TextView) view.findViewById(R.id.txtDeliveryDate);
            ivItem = (ImageView) view.findViewById(R.id.ivItem);
            ivDeleteItem = (ImageView) view.findViewById(R.id.ivDeleteItem);
            etQtyCart = (EditText) view.findViewById(R.id.etQtyCart);

            //on item click
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int pos = getAdapterPosition();
                        if (pos != RecyclerView.NO_POSITION) {
                            listener.onItemClick(pos);
                        }
                    }
                }

            });

            ivDeleteItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (listener != null) {
                        int pos = getAdapterPosition();
                        if (pos != RecyclerView.NO_POSITION) {
                            //listener.onCartClick(pos);
                        }
                    }

                }
            });
        }
    }

    // Item Click listener interface
    public interface OnItemClickListener {
        void onItemClick(int pos);

        void onCartClick(int pos);

    }
}
