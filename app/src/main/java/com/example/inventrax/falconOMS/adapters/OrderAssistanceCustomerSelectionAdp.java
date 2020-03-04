package com.example.inventrax.falconOMS.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.inventrax.falconOMS.R;

import java.util.List;

/**
 * Created by padmaja on 22/07/19.
 */
public class OrderAssistanceCustomerSelectionAdp extends RecyclerView.Adapter<OrderAssistanceCustomerSelectionAdp.ViewHolder> {
    private List items;
    private Context context;
    OnItemClickListener listener;


    public OrderAssistanceCustomerSelectionAdp(Context applicationContext, List itemArrayList, OnItemClickListener mlistener) {
        this.context = applicationContext;
        this.items = itemArrayList;
        listener = mlistener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {

        View view;
        view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_customerlist_orderassist, viewGroup, false);
        return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {

        String custList = (String) items.get(i);
        if (custList.split("-").length == 2) {
            viewHolder.txtCustmerName.setText(custList.split("-")[0]);
            viewHolder.txtCode.setText(custList.split("-")[1]);
        } else {
            viewHolder.txtCustmerName.setText(custList.split("-")[0] == null ? "" : custList.split("-")[0]);
            viewHolder.txtCode.setText(custList.split("-")[1] == null ? "" : custList.split("-")[1]);
        }

    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView txtCustmerName, txtCode;

        public ViewHolder(View view) {
            super(view);
            // Initializing Views
            txtCustmerName = (TextView) view.findViewById(R.id.txtCustmerName);
            txtCode = (TextView) view.findViewById(R.id.txtCode);


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
