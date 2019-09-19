package com.example.inventrax.falconOMS.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
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

import java.util.ArrayList;
import java.util.List;

/**
 * Created by padmaja on 13/07/19.
 */
public class PaginationAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    OnItemClickListener listener;
    private List<ItemTable> itemsList;
    private boolean isgrid;

    private static final int ITEM = 0;
    private static final int LOADING = 1;
    private boolean isLoadingAdded = false;


    public PaginationAdapter(Context applicationContext, OnItemClickListener mlistener, boolean isgrid) {
        this.context = applicationContext;
        listener = mlistener;
        this.isgrid = isgrid;
        itemsList=new ArrayList<>();
    }

    public PaginationAdapter(boolean isgrid) {

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        switch (viewType) {
            case ITEM:
                viewHolder = getViewHolder(parent, inflater);
                break;
            case LOADING:
                View v2 = inflater.inflate(R.layout.progress, parent, false);
                viewHolder = new LoadingVH(v2);
                break;
        }
        return viewHolder;
    }


    @NonNull
    private RecyclerView.ViewHolder getViewHolder(ViewGroup parent, LayoutInflater inflater) {
        RecyclerView.ViewHolder viewHolder;

        View view;

        if (isgrid)
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_product_catalog_grid, parent, false);
        else
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_product_catalog, parent, false);

        viewHolder = new ItemListView(view);
        return viewHolder;
    }



    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        ItemTable result = itemsList.get(position); // Item

        switch (getItemViewType(position)) {
            case ITEM:
                final ItemListView itemListView = (ItemListView) holder;

                itemListView.txtItemName.setText(result.modelCode);
                itemListView.txtItemDesc.setText(result.modelDescription);

                if(result.imgPath.equals("")){
                    itemListView.ivItem.setImageResource(R.drawable.no_img);
                }else {
                    Picasso.with(context)
                            .load(result.imgPath)
                            .placeholder(R.drawable.no_img)
                            .into(itemListView.ivItem);
                }




                break;

            case LOADING:
//                Do nothing

                break;
        }

    }

    @Override
    public int getItemCount() {
        return itemsList == null ? 0 : itemsList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return (position == itemsList.size() - 1 && isLoadingAdded) ? LOADING : ITEM;
    }

    /*
   Helpers
   _________________________________________________________________________________________________
    */

    public void add(ItemTable r) {
        itemsList.add(r);
        notifyItemInserted(itemsList.size() - 1);

    }

    public void addAll(List<ItemTable> itemTables) {
        for (ItemTable result : itemTables) {
            add(result);
        }
    }

    public void remove(ItemTable r) {
        int position = itemsList.indexOf(r);
        if (position > -1) {
            itemsList.remove(position);
            notifyItemRemoved(position);
        }
    }

    public void clear() {
        isLoadingAdded = false;
        while (getItemCount() > 0) {
            remove(getItem(0));
        }
    }

    public boolean isEmpty() {
        return getItemCount() == 0;
    }


    public void addLoadingFooter() {
        isLoadingAdded = true;
        add(new ItemTable());
    }

    public void removeLoadingFooter() {
        isLoadingAdded = false;

        int position = itemsList.size() - 1;
        ItemTable result = getItem(position);

        if (result != null) {
            itemsList.remove(position);
            notifyItemRemoved(position);
        }
    }

    public ItemTable getItem(int position) {
        return itemsList.get(position);
    }


   /*
   View Holders
   _________________________________________________________________________________________________
    */

    /**
     * Main list's content ViewHolder
     */
    public class ItemListView extends RecyclerView.ViewHolder {
        private TextView txtItemName, txtItemDesc, txtPrice, txtDiscount;
        private ImageView ivItem;
        private Button ivAddToCart;


        public ItemListView(View view) {
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


    protected class LoadingVH extends RecyclerView.ViewHolder {

        public LoadingVH(View itemView) {
            super(itemView);
        }
    }


    // Item click listener interface
    public interface OnItemClickListener {

        void onItemClick(int pos);

        void onCartClick(int pos);

    }
}
