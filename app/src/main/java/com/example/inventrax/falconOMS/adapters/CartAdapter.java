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
import com.example.inventrax.falconOMS.room.CartDetails;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by padmaja on 13/07/19.
 */
public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {
    private List<CartDetails> cartList;
    private Context context;
    OnItemClickListener listener;

    public CartAdapter(Context applicationContext, List<CartDetails> cartList, OnItemClickListener mlistener) {
        this.context = applicationContext;
        this.cartList = cartList;
        listener = mlistener;
    }

    public CartAdapter(){

    }


    @Override
    public CartAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_cart_item, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CartAdapter.ViewHolder viewHolder, int i) {

        CartDetails itemList = (CartDetails) cartList.get(i);

        viewHolder.txtItemName.setText(cartList.get(i).mCode);
        viewHolder.txtItemDesc.setText(cartList.get(i).mDescription);
        viewHolder.txtDeliveryDate.setText(cartList.get(i).expectedDeliveryDate);
        viewHolder.etQtyCart.setText(cartList.get(i).quantity);
        viewHolder.txtPrice.setText(cartList.get(i).price);

        Picasso.with(context)
                .load(cartList.get(i).imgPath)
                .placeholder(R.drawable.load)
                .into(viewHolder.ivItem);



    }

    @Override
    public int getItemCount() {
        return cartList.size();
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


            ivDeleteItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (listener != null) {
                        int pos = getAdapterPosition();
                        if (pos != RecyclerView.NO_POSITION) {
                            listener.onDeletClick(pos);
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
