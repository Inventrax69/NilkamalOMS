package com.example.inventrax.falconOMS.adapters;

import android.content.Context;
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
    public void onBindViewHolder(ViewHolder viewHolder, int i) {

        SODetails soDetails = items.get(i);

        viewHolder.txtMaterial.setText(soDetails.getMaterial());
        viewHolder.txtQty.setText(soDetails.getQuantity() + "");
        viewHolder.txtMaterialDesc.setText(soDetails.getItemDescription());
        viewHolder.txtPrice.setText(soDetails.getUnitPrice());

    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView txtMaterial, txtQty, txtMaterialDesc,txtPrice;
        private ImageView ivDelete;

        public ViewHolder(View view) {
            super(view);
            // Initializing views
            txtMaterial = (TextView) view.findViewById(R.id.txtMaterial);
            txtQty = (TextView) view.findViewById(R.id.txtQty);
            txtMaterialDesc = (TextView) view.findViewById(R.id.txtMaterialDesc);
            txtPrice = (TextView) view.findViewById(R.id.txtPrice);
            ivDelete = (ImageView) view.findViewById(R.id.ivDelete);

            //on item click
            ivDelete.setOnClickListener(new View.OnClickListener() {
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

        }
    }

    // Item click listener interface
    public interface OnItemClickListener {
        void onItemClick(int pos);
    }

}
