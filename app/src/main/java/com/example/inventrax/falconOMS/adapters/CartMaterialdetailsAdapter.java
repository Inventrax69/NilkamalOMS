package com.example.inventrax.falconOMS.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.inventrax.falconOMS.R;
import com.example.inventrax.falconOMS.pojos.CardMaterialDTO;

import java.util.List;

/**
 * Created by padmaja on 22/07/19.
 */
public class CartMaterialdetailsAdapter extends RecyclerView.Adapter<CartMaterialdetailsAdapter.ViewHolder> {

    private List<CardMaterialDTO> cardMaterialDTOS;
    private Context context;
    OnItemClickListener listener;

    public CartMaterialdetailsAdapter(Context applicationContext, List<CardMaterialDTO> cardMaterialDTOS, OnItemClickListener mlistener) {
        this.context = applicationContext;
        this.cardMaterialDTOS = cardMaterialDTOS;
        listener = mlistener;
    }

    public CartMaterialdetailsAdapter(){ }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view;
        view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.cart_material_view, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {

        CardMaterialDTO cardMaterialDTO = cardMaterialDTOS.get(i);

        viewHolder.txtMCode.setText(cardMaterialDTO.getMCode().trim());
        viewHolder.txtMDescription.setText(cardMaterialDTO.getMDescription().trim());
        viewHolder.txtOrderQuantity.setText(""+cardMaterialDTO.getOrderQuantity());
        viewHolder.txtAvailableQuantity.setText(""+cardMaterialDTO.getAvailableQuantity());

    }

    @Override
    public int getItemCount() {
        return cardMaterialDTOS.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView  txtMCode,txtMDescription,txtOrderQuantity,txtAvailableQuantity;

        public ViewHolder(View view) {
            super(view);

            txtMCode =(TextView)view.findViewById(R.id.txtMCode);
            txtMDescription =(TextView)view.findViewById(R.id.txtMDescription);
            txtOrderQuantity =(TextView)view.findViewById(R.id.txtOrderQuantity);
            txtAvailableQuantity =(TextView)view.findViewById(R.id.txtAvailableQuantity);


            //on item click
            view.setOnClickListener(new View.OnClickListener() {
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
