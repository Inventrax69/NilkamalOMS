package com.example.inventrax.falconOMS.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.inventrax.falconOMS.R;
import com.example.inventrax.falconOMS.room.CustomerTable;
import com.example.inventrax.falconOMS.util.ColorGenerator;
import com.example.inventrax.falconOMS.util.TextDrawable;

import java.util.ArrayList;

/**
 * Created by padmaja on 22/07/19.
 */
public class CustomerListAdapter extends RecyclerView.Adapter<CustomerListAdapter.ViewHolder> {
    private ArrayList items;
    private Context context;
    OnItemClickListener listener;


    public CustomerListAdapter(Context applicationContext, ArrayList itemArrayList, OnItemClickListener mlistener) {
        this.context = applicationContext;
        this.items = itemArrayList;
        listener = mlistener;

    }

    @Override
    public CustomerListAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {

        View view;

        view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_customerlist, viewGroup, false);


        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CustomerListAdapter.ViewHolder viewHolder, int i) {

        CustomerTable custList = (CustomerTable) items.get(i);
        viewHolder.txtCustmerName.setText((custList.custName));
        viewHolder.txtPersonName.setText((custList.customerId));
        viewHolder.txtPersonName.setText((custList.custType));

        ColorGenerator generator = ColorGenerator.MATERIAL; // or use DEFAULT
        // generate random color
        int color = generator.getRandomColor();

        TextDrawable drawable = TextDrawable.builder()
                .buildRound(custList.custName.substring(0, 1), color);

        viewHolder.CustImg.setImageDrawable(drawable);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView txtCustmerName, txtPersonName, txtPlace;
        private ImageView CustImg;


        public ViewHolder(View view) {
            super(view);
            // Initializing Views
            txtCustmerName = (TextView) view.findViewById(R.id.txtCustmerName);
            txtPersonName = (TextView) view.findViewById(R.id.txtPersonName);
            txtPlace = (TextView) view.findViewById(R.id.txtPlace);
            CustImg = (ImageView) view.findViewById(R.id.CustImg);


            //on item click
            itemView.setOnClickListener(new View.OnClickListener() {
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
