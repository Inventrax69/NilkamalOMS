package com.example.inventrax.falconOMS.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.inventrax.falconOMS.R;
import com.example.inventrax.falconOMS.room.ItemTable;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by padmaja on 13/07/19.
 */
public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ViewHolder> {

    private Context context;
    OnItemClickListener listener;
    private List<ItemTable> itemsList;
    private boolean isgrid;


    public ItemAdapter(Context applicationContext, List<ItemTable> itemArrayList, OnItemClickListener mlistener, boolean isgrid) {
        this.context = applicationContext;
        this.itemsList = itemArrayList;
        listener = mlistener;
        this.isgrid = isgrid;

    }

    public ItemAdapter(boolean isgrid) {

    }

    @Override
    public ItemAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {

        View view;

        if (isgrid)
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_product_catalog_grid, viewGroup, false);
        else
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_product_catalog, viewGroup, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ItemAdapter.ViewHolder viewHolder, int i) {

        ItemTable itemList = (ItemTable) itemsList.get(i);
        viewHolder.txtItemName.setText((itemList.itemname));
        viewHolder.txtItemDesc.setText((itemList.itemdesc));
       /* viewHolder.txtItemDesc.setText(items.get(i).getHtmlUrl());
        viewHolder.txtPrice.setText(items.get(i).getHtmlUrl());
        viewHolder.txtDiscount.setText(items.get(i).getHtmlUrl());*/

        Picasso.with(context)
                .load(itemList.imageurl)
                .placeholder(R.drawable.load)
                .into(viewHolder.ivItem);

    }

    @Override
    public int getItemCount() {
        return itemsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView txtItemName, txtItemDesc, txtPrice, txtDiscount;
        private ImageView ivItem;
        private Button ivAddToCart;


        public ViewHolder(View view) {
            super(view);
            // Initializing Views
            txtItemName = (TextView) view.findViewById(R.id.txtItemName);
            txtItemDesc = (TextView) view.findViewById(R.id.txtItemDesc);
            txtPrice = (TextView) view.findViewById(R.id.txtPrice);
            txtDiscount = (TextView) view.findViewById(R.id.txtDiscount);
            ivItem = (ImageView) view.findViewById(R.id.ivItem);
            ivAddToCart = (Button) view.findViewById(R.id.ivAddToCart);

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

            ivAddToCart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (listener != null) {
                        int pos = getAdapterPosition();
                        if (pos != RecyclerView.NO_POSITION) {
                            listener.onCartClick(pos);
                        }
                    }

                }
            });
        }
    }

    // Item click listener interface
    public interface OnItemClickListener {
        void onItemClick(int pos);

        void onCartClick(int pos);

    }
}
