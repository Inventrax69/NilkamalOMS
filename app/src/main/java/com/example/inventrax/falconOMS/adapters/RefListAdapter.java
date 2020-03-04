package com.example.inventrax.falconOMS.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.inventrax.falconOMS.R;
import com.example.inventrax.falconOMS.pojos.CartHeaderListDTO;
import com.example.inventrax.falconOMS.util.ColorGenerator;
import com.example.inventrax.falconOMS.util.TextDrawable;

import java.util.List;

/**
 * Created by padmaja on 22/07/19.
 */
public class RefListAdapter extends RecyclerView.Adapter<RefListAdapter.ViewHolder> {

    private List<CartHeaderListDTO> cartHeaderListDTOS;
    private Context context;
    OnItemClickListener listener;

    public RefListAdapter(Context applicationContext, List<CartHeaderListDTO> cartHeaderListDTOS, OnItemClickListener mlistener) {
        this.context = applicationContext;
        this.cartHeaderListDTOS = cartHeaderListDTOS;
        listener = mlistener;
    }

    public RefListAdapter() {
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view;
        view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.approvals_list, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {

        CartHeaderListDTO cartHeaderListDTO = cartHeaderListDTOS.get(i);

        viewHolder.txtCRef.setText(cartHeaderListDTO.getCartRef());
        viewHolder.txtCName.setText(cartHeaderListDTO.getPartnerName());
        viewHolder.txtCCode.setText(cartHeaderListDTO.getPartnerCode());
        viewHolder.txtDate.setText(cartHeaderListDTO.getCreatedOn());

        ColorGenerator generator = ColorGenerator.MATERIAL; // or use DEFAULT
        // generate random color
        int color = generator.getRandomColor();

        TextDrawable drawable = TextDrawable.builder()
                .buildRound(cartHeaderListDTO.getPartnerName().substring(0, 1), color);

        viewHolder.ivItem.setImageDrawable(drawable);

    }

    @Override
    public int getItemCount() {
        return cartHeaderListDTOS.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView txtCRef, txtCName, txtCCode, txtDate;
        ImageView ivItem;

        public ViewHolder(View view) {
            super(view);

            txtCRef = (TextView) view.findViewById(R.id.txtCRef);
            txtCCode = (TextView) view.findViewById(R.id.txtCCode);
            txtCName = (TextView) view.findViewById(R.id.txtCName);
            txtDate = (TextView) view.findViewById(R.id.txtDate);
            ivItem = (ImageView) view.findViewById(R.id.ivItem);

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
