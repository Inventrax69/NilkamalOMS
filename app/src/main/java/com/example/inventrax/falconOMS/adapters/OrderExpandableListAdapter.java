package com.example.inventrax.falconOMS.adapters;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.inventrax.falconOMS.R;
import com.example.inventrax.falconOMS.pojos.CartDetailsListDTO;
import com.example.inventrax.falconOMS.pojos.CartHeaderListDTO;
import com.example.inventrax.falconOMS.pojos.DeliveryDateDTO;
import com.example.inventrax.falconOMS.room.AppDatabase;
import com.example.inventrax.falconOMS.room.CartDetails;
import com.example.inventrax.falconOMS.room.RoomAppDatabase;
import com.example.inventrax.falconOMS.util.NetworkUtils;
import com.squareup.picasso.Picasso;

import java.util.List;

public class OrderExpandableListAdapter extends BaseExpandableListAdapter {

    Context context;
    private List<DeliveryDateDTO> deliveryDateDTOS;
    private CartHeaderListDTO cartHeaderListDTO;
    private OnDeleteClickListener onDeleteClickListener;
    AppDatabase db;

    public OrderExpandableListAdapter(Context context, CartHeaderListDTO cartHeaderListDTO, OnDeleteClickListener onDeleteClickListener) {
        this.cartHeaderListDTO = cartHeaderListDTO;
        this.deliveryDateDTOS = cartHeaderListDTO.getDeliveryDate();
        this.context = context;
        this.onDeleteClickListener = onDeleteClickListener;
        db = new RoomAppDatabase(this.context).getAppDatabase();
    }

    @Override
    public int getGroupCount() {
        return this.deliveryDateDTOS.size();
    }

    @Override
    public int getChildrenCount(int i) {
        return this.deliveryDateDTOS.get(i).getListCartDetailsList().size();
    }

    @Override
    public Object getGroup(int i) {
        return this.deliveryDateDTOS.get(i);
    }

    @Override
    public Object getChild(int i, int i1) {
        return this.deliveryDateDTOS.get(i).getListCartDetailsList().get(i1);
    }

    @Override
    public long getGroupId(int i) {
        return i;
    }

    @Override
    public long getChildId(int i, int i1) {
        return i1;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View getGroupView(int i, boolean b, View view, ViewGroup viewGroup) {
        DeliveryDateDTO deliveryDateDTO = (DeliveryDateDTO) getGroup(i);
        if (view == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = layoutInflater.inflate(R.layout.excepted_delivery_date, null);
        }
        TextView ex_delivery_date = view.findViewById(R.id.ex_delivery_date);
        if (cartHeaderListDTO.getIsStockNotAvailable().equals("1")) {
            ex_delivery_date.setVisibility(View.INVISIBLE);
        } else {
            ex_delivery_date.setVisibility(View.VISIBLE);
        }
        ex_delivery_date.setText(deliveryDateDTO.getFromDate() + " To " + deliveryDateDTO.getToDate());
        TextView item_count = view.findViewById(R.id.item_count);
        item_count.setText("" + deliveryDateDTO.getListCartDetailsList().size());
        return view;
    }


    @Override
    public View getChildView(final int i, final int i1, boolean b, View view, ViewGroup viewGroup) {
        final CartDetailsListDTO cartDetailsListDTO = (CartDetailsListDTO) getChild(i, i1);
        if (view == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = layoutInflater.inflate(R.layout.row_order_confirmation, null);
        }

/*        if(cartDetailsListDTO.getOfferItemCartDetailsID()!=null && !cartDetailsListDTO.getOfferItemCartDetailsID().equals("-1")){
            view.setVisibility(View.GONE);
        }else{
            view.setVisibility(View.VISIBLE);
        }*/


        TextView txtItemName = view.findViewById(R.id.txtItemName);
        TextView txtQty = view.findViewById(R.id.txtQty);
        TextView txtDeliveryDate = view.findViewById(R.id.txtDeliveryDate);
        TextView isItemInactive = view.findViewById(R.id.isItemInactive);
        TextView txtOfferAvaiable = view.findViewById(R.id.txtOfferAvaiable);

        if (cartHeaderListDTO.getOfferCartDetailsDTOList().get(cartHeaderListDTO.getDeliveryDate().get(i).getListCartDetailsList().get(i1).getCartDetailsID()) != null) {
            if (cartHeaderListDTO.getOfferCartDetailsDTOList().get(cartHeaderListDTO.getDeliveryDate().get(i).getListCartDetailsList().get(i1).getCartDetailsID()).size() > 0) {
                txtOfferAvaiable.setText(cartHeaderListDTO.getOfferCartDetailsDTOList().get(cartHeaderListDTO.getDeliveryDate().get(i).getListCartDetailsList().get(i1).getCartDetailsID()).size() + " offer items");
                txtOfferAvaiable.setVisibility(View.VISIBLE);
            } else {
                txtOfferAvaiable.setVisibility(View.INVISIBLE);
            }
        } else {
            txtOfferAvaiable.setVisibility(View.INVISIBLE);
        }

        txtOfferAvaiable.setOnClickListener(new View.OnClickListener() {
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

                final OfferItemsObjectAdapter offerItemsAdapter = new OfferItemsObjectAdapter(context, cartHeaderListDTO.getOfferCartDetailsDTOList().get(cartHeaderListDTO.getDeliveryDate().get(i).getListCartDetailsList().get(i1).getCartDetailsID()), new OfferItemsObjectAdapter.OnItemClickListener() {
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

                dialog.show();

            }
        });

        ImageView ivItem = view.findViewById(R.id.ivItem);
        ImageView ivDeleteItem = view.findViewById(R.id.ivDeleteItem);


        txtItemName.setText(cartDetailsListDTO.getMCode());
        txtQty.setText(cartDetailsListDTO.getQuantity());
        txtDeliveryDate.setText(cartDetailsListDTO.getActualDeliveryDate());

        if (NetworkUtils.isInternetAvailable(context)) {
            String filePath = cartDetailsListDTO.getFileNames().split("[|]").length > 0 ?
                    cartDetailsListDTO.getFileNames().split("[|]")[0] : "";
            Picasso.with(context)
                    .load(filePath)
                    .placeholder(R.drawable.no_img)
                    .into(ivItem);
        } else {
            ivItem.setImageResource(R.drawable.no_img);
        }

        if (cartDetailsListDTO.getIsInActive() != null) {
            if (cartDetailsListDTO.getIsInActive()) {
                isItemInactive.setVisibility(View.VISIBLE);
            } else {
                isItemInactive.setVisibility(View.GONE);
            }
        } else {
            isItemInactive.setVisibility(View.GONE);
        }

        ivDeleteItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onDeleteClickListener.onDeleteClickListener(i, i1);
            }
        });

        return view;
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return true;
    }

    public interface OnDeleteClickListener {
        public void onDeleteClickListener(int group, int child);
    }
}
