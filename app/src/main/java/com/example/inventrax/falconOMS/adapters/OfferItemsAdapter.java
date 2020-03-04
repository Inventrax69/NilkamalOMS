package com.example.inventrax.falconOMS.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.inventrax.falconOMS.R;
import com.example.inventrax.falconOMS.room.CartDetails;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by padmaja on 13/07/19.
 */
public class OfferItemsAdapter extends RecyclerView.Adapter<OfferItemsAdapter.ViewHolder> {

    private List<CartDetails> cartDetails;
    private Context context;
    private OnItemClickListener mlistener;
    Button btnClOSE;

    public OfferItemsAdapter(Context context, List<CartDetails> cartDetails, OnItemClickListener mlistener) {
        this.context = context;
        this.cartDetails = cartDetails;
        this.mlistener = mlistener;

    }

    @Override
    public OfferItemsAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.offer_item_view, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {

        CartDetails cartDetails1 = cartDetails.get(i);

        viewHolder.txtItemName.setText(cartDetails1.mCode);
        viewHolder.txtItemDesc.setText(cartDetails1.mDescription);
        viewHolder.etQtyCart.setText(cartDetails1.quantity);
        viewHolder.txtPrice.setText("RS: " + cartDetails1.offerValue);
        Picasso.with(context)
                .load(cartDetails1.imgPath.split("[|]")[0])
                .placeholder(R.drawable.no_img)
                .into(viewHolder.ivItem);


/*
        viewHolder.txtSiteNo.setText(availableStockDTO.getSiteCode());
        viewHolder.txtSiteName.setText(availableStockDTO.getSiteName());
        viewHolder.txtAvaQty.setText(""+(int)Double.parseDouble(""+availableStockDTO.getAvailableQty()));*/


    }

    @Override
    public int getItemCount() {
        return cartDetails.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {


        private TextView txtItemName, txtItemDesc, txtPrice;
        private EditText etQtyCart;
        private ImageView ivItem;

        public ViewHolder(View view) {
            super(view);
            // Initializing views
            txtItemName = (TextView) view.findViewById(R.id.txtItemName);
            txtItemDesc = (TextView) view.findViewById(R.id.txtItemDesc);
            txtPrice = (TextView) view.findViewById(R.id.txtPrice);
            ivItem = (ImageView) view.findViewById(R.id.ivItem);
            etQtyCart = (EditText) view.findViewById(R.id.etQtyCart);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mlistener != null) {
                        int pos = getAdapterPosition();
                        if (pos != RecyclerView.NO_POSITION) {
                            mlistener.OnItemClick(pos);
                        }
                    }
                }
            });

        }
    }

    // Item Click listener interface
    public interface OnItemClickListener {
        void OnItemClick(int pos);
    }

}
