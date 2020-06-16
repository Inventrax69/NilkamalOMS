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
import com.example.inventrax.falconOMS.pojos.SalesBOMDTO;
import com.example.inventrax.falconOMS.room.CartDetails;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by padmaja on 13/07/19.
 */
public class SalesBOMAdapter extends RecyclerView.Adapter<SalesBOMAdapter.ViewHolder> {

    private List<SalesBOMDTO> salesBOMDTOList;
    private Context context;
    private OnItemClickListener mlistener;
    Button btnClOSE;

    public SalesBOMAdapter(Context context, List<SalesBOMDTO> salesBOMDTOList, OnItemClickListener mlistener) {
        this.context = context;
        this.salesBOMDTOList = salesBOMDTOList;
        this.mlistener = mlistener;

    }

    @Override
    public SalesBOMAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.salebom_item, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {

        SalesBOMDTO salesBOMDTO = salesBOMDTOList.get(i);

        viewHolder.txtItemName.setText(salesBOMDTO.getMcode());
        viewHolder.txtItemDesc.setText(salesBOMDTO.getMdescription());
        viewHolder.txtQty.setText(""+salesBOMDTO.getBOMQuantity());

    }

    @Override
    public int getItemCount() {
        return salesBOMDTOList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {


        private TextView txtItemName, txtItemDesc, txtQty;


        public ViewHolder(View view) {
            super(view);
            // Initializing views
            txtItemName = (TextView) view.findViewById(R.id.txtItemName);
            txtItemDesc = (TextView) view.findViewById(R.id.txtItemDesc);
            txtQty = (TextView) view.findViewById(R.id.txtQty);

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
