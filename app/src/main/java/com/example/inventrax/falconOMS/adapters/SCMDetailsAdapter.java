package com.example.inventrax.falconOMS.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.inventrax.falconOMS.R;
import com.example.inventrax.falconOMS.pojos.CartDetailsListDTO;
import com.example.inventrax.falconOMS.util.NetworkUtils;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by padmaja on 13/07/19.
 */
public class SCMDetailsAdapter extends RecyclerView.Adapter<SCMDetailsAdapter.ViewHolder> {

    private List<CartDetailsListDTO> cartItemList;
    private Context context;
    private OnItemClickListener mlistener;

    public SCMDetailsAdapter(Context context, List<CartDetailsListDTO> cartItemList, OnItemClickListener mlistener) {
        this.context = context;
        this.cartItemList = cartItemList;
        this.mlistener = mlistener;
    }

    @Override
    public SCMDetailsAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.approval_scm_details_view, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SCMDetailsAdapter.ViewHolder viewHolder, int i) {

        viewHolder.txtItemName.setText(cartItemList.get(i).getMCode());
        viewHolder.txtQty.setText(cartItemList.get(i).getQuantity());
        viewHolder.txtDeliveryDate.setText(cartItemList.get(i).getActualDeliveryDate());

        if (NetworkUtils.isInternetAvailable(context)) {
            Picasso.with(context)
                    .load(cartItemList.get(i).getFileNames())
                    .placeholder(R.drawable.no_img)
                    .into(viewHolder.ivItem);
        } else {
            viewHolder.ivItem.setImageResource(R.drawable.no_img);
        }

        if (cartItemList.get(i).getIsInActive() != null) {
            if (cartItemList.get(i).getIsInActive()) {
                viewHolder.isItemInactive.setVisibility(View.VISIBLE);
            } else {
                viewHolder.isItemInactive.setVisibility(View.GONE);
            }
        } else {
            viewHolder.isItemInactive.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        return cartItemList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView txtItemName, txtQty, txtDeliveryDate, isItemInactive;
        private ImageView ivItem, ivDeleteItem;

        public ViewHolder(View view) {
            super(view);
            // Initializing views
            txtItemName = (TextView) view.findViewById(R.id.txtItemName);
            txtQty = (TextView) view.findViewById(R.id.txtQty);
            txtDeliveryDate = (TextView) view.findViewById(R.id.txtDeliveryDate);
            ivItem = (ImageView) view.findViewById(R.id.ivItem);
            ivDeleteItem = (ImageView) view.findViewById(R.id.ivDeleteItem);
            isItemInactive = (TextView) view.findViewById(R.id.isItemInactive);

            ivDeleteItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (mlistener != null) {
                        int pos = getAdapterPosition();
                        if (pos != RecyclerView.NO_POSITION) {
                            mlistener.onDeletClick(pos);
                        }
                    }

                }
            });

        }
    }

    // Item Click listener interface
    public interface OnItemClickListener {
        void onDeletClick(int pos);

    }

}
