package com.example.inventrax.falconOMS.adapters;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.inventrax.falconOMS.R;
import com.example.inventrax.falconOMS.pojos.CartDetailsListDTO;
import com.example.inventrax.falconOMS.pojos.CartHeaderListDTO;
import com.example.inventrax.falconOMS.util.NetworkUtils;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by padmaja on 13/07/19.
 */
public class ApprovalDetailsAdapter extends RecyclerView.Adapter<ApprovalDetailsAdapter.ViewHolder> {

    private CartHeaderListDTO cartHeaderListDTO;
    private List<CartDetailsListDTO> cartItemList;
    private Context context;
    private OnItemClickListener mlistener;

    public ApprovalDetailsAdapter(Context context, CartHeaderListDTO cartHeaderListDTO, OnItemClickListener mlistener) {
        this.context = context;
        this.cartHeaderListDTO = cartHeaderListDTO;
        this.cartItemList = cartHeaderListDTO.getListCartDetailsList();
        this.mlistener = mlistener;
    }

    @Override
    public ApprovalDetailsAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.approval_details_view, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ApprovalDetailsAdapter.ViewHolder viewHolder, final int i) {

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


        if (cartItemList.get(i).getOfferItemCartDetailsID() == null) {
            viewHolder.ivOffer.setVisibility(View.GONE);
        } else {
            viewHolder.ivOffer.setVisibility(View.VISIBLE);
        }

        viewHolder.ivOffer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final Dialog dialog = new Dialog(context);
                dialog.setContentView(R.layout.offer_item_recycler);
                dialog.setCancelable(false);
                Window window = dialog.getWindow();
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                final RecyclerView recyclerView;
                recyclerView = dialog.findViewById(R.id.recyclerView);
                recyclerView.setHasFixedSize(true);

                ImageView btnClOSE = dialog.findViewById(R.id.btnClOSE);

                LinearLayoutManager layoutManager = new LinearLayoutManager(context);
                recyclerView.setLayoutManager(layoutManager);

                // Log.v("ABCDE",cartItemList.get(i).getCartDetailsID());

                final OfferItemsObjectAdapter offerItemsAdapter = new OfferItemsObjectAdapter(context, cartHeaderListDTO.getOfferCartDetailsDTOList().get(cartItemList.get(i).getCartDetailsID()), new OfferItemsObjectAdapter.OnItemClickListener() {
                    @Override
                    public void OnItemClick(int pos) {

                    }
                });

                recyclerView.setAdapter(offerItemsAdapter);

                btnClOSE.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });

                if (cartHeaderListDTO.getOfferCartDetailsDTOList().get(cartItemList.get(i).getCartDetailsID()) != null)
                    dialog.show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return cartItemList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView txtItemName, txtQty, txtDeliveryDate, isItemInactive;
        private ImageView ivItem, ivDeleteItem, ivOffer;
        View view;

        public ViewHolder(View view) {
            super(view);
            this.view = view;
            // Initializing views
            txtItemName = (TextView) view.findViewById(R.id.txtItemName);
            txtQty = (TextView) view.findViewById(R.id.txtQty);
            txtDeliveryDate = (TextView) view.findViewById(R.id.txtDeliveryDate);
            ivItem = (ImageView) view.findViewById(R.id.ivItem);
            ivDeleteItem = (ImageView) view.findViewById(R.id.ivDeleteItem);
            ivOffer = (ImageView) view.findViewById(R.id.ivOffer);
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
