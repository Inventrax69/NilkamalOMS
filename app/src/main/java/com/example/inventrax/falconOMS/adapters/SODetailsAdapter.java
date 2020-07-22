package com.example.inventrax.falconOMS.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.inventrax.falconOMS.R;
import com.example.inventrax.falconOMS.pojos.SODetails;

import java.util.List;

/**
 * Created by anil.ch on 17/03/2020.
 */
public class SODetailsAdapter extends RecyclerView.Adapter<SODetailsAdapter.ViewHolder> {

    private List<SODetails> items;
    private Context context;
    OnItemClickListener listener;

    public SODetailsAdapter(Context applicationContext, List<SODetails> items) {
        this.context = applicationContext;
        this.items = items;
        //listener = mlistener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_so_added_items, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, @SuppressLint("RecyclerView") final int i) {

        final SODetails soDetails = items.get(i);

        viewHolder.txtMaterial.setText(" "+soDetails.getMaterial());
        viewHolder.txtQty.setText(soDetails.getQuantity() + "");
        viewHolder.txtMaterialDesc.setText(soDetails.getItemDescription());
        viewHolder.txtUnitPrice.setText(soDetails.getUnitPrice() == null ? "":soDetails.getUnitPrice());
        viewHolder.txtTotalPriceWithTax.setText(soDetails.getTotalValueWithTax()==null ? "":soDetails.getTotalValueWithTax());
        viewHolder.txtDescription.setText(soDetails.getTotalValueWithTax()==null ? "":soDetails.getTotalValueWithTax());

        if(soDetails.getDiscountID()!=null &&  ((int)Double.parseDouble(soDetails.getDiscountID()))>0){
            viewHolder.txtMaterial.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_apply_offer, 0,0 , 0);
            viewHolder.txtMaterial.setTextColor(Color.parseColor("#0000FF"));
            viewHolder.txtDescription.setVisibility(View.VISIBLE);
            viewHolder.txtDescription.setText(items.get(i).getDiscountText() +" of discount price(Rs.) : "+items.get(i).getDiscountedPrice());
        }else{
            viewHolder.txtMaterial.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, 0, 0);
            viewHolder.txtMaterial.setTextColor(Color.parseColor("#000000"));
            viewHolder.txtDescription.setVisibility(View.GONE);
            viewHolder.txtDescription.setText("");
        }



    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView txtMaterial, txtQty, txtMaterialDesc,txtUnitPrice,txtTotalPriceWithTax,txtDescription;

        public ViewHolder(View view) {
            super(view);
            // Initializing views
            txtMaterial = (TextView) view.findViewById(R.id.txtMaterial);
            txtQty = (TextView) view.findViewById(R.id.txtQty);
            txtMaterialDesc = (TextView) view.findViewById(R.id.txtMaterialDesc);
            txtUnitPrice = (TextView) view.findViewById(R.id.txtUnitPrice);
            txtTotalPriceWithTax = (TextView) view.findViewById(R.id.txtTotalPriceWithTax);
            txtDescription = (TextView) view.findViewById(R.id.txtDescription);

        }
    }

    // Item click listener interface
    public interface OnItemClickListener {
        void onItemClick(int pos);
    }

}
