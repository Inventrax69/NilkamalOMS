package com.example.inventrax.falconOMS.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import com.example.inventrax.falconOMS.R;
import com.example.inventrax.falconOMS.pojos.AvailableStockDTO;

import java.util.List;

/**
 * Created by padmaja on 13/07/19.
 */
public class AvailableStockAdapter extends RecyclerView.Adapter<AvailableStockAdapter.ViewHolder> {

    private List<AvailableStockDTO> availableStockDTOS;
    private Context context;
    private OnItemClickListener mlistener;
    Button btnClOSE;

    public AvailableStockAdapter(Context context, List<AvailableStockDTO> availableStockDTOS, OnItemClickListener mlistener) {
        this.context = context;
        this.availableStockDTOS = availableStockDTOS;
        this.mlistener = mlistener;
    }

    @Override
    public AvailableStockAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.available_stock_view, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {

        AvailableStockDTO availableStockDTO = availableStockDTOS.get(i);

        viewHolder.txtSiteNo.setText(availableStockDTO.getSiteCode());
        viewHolder.txtSiteName.setText(availableStockDTO.getSiteName());
        viewHolder.txtAvaQty.setText("" + (int) Double.parseDouble("" + availableStockDTO.getAvailableQty()));


    }

    @Override
    public int getItemCount() {
        return availableStockDTOS.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView txtSiteNo, txtSiteName, txtAvaQty;

        public ViewHolder(View view) {
            super(view);
            // Initializing views
            txtSiteNo = (TextView) view.findViewById(R.id.txtSiteNo);
            txtSiteName = (TextView) view.findViewById(R.id.txtSiteName);
            txtAvaQty = (TextView) view.findViewById(R.id.txtAvaQty);

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
