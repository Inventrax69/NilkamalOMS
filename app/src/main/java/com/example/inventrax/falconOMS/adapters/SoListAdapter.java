package com.example.inventrax.falconOMS.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.inventrax.falconOMS.R;
import com.example.inventrax.falconOMS.pojos.SOListDTO;

import java.util.ArrayList;
import java.util.List;


public class SoListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int ITEM = 0;
    private static final int LOADING = 1;

    OnItemClickListener listener;

    private List<SOListDTO> soListItems;
    private Context context;

    private boolean isLoadingAdded = false;

    public SoListAdapter(Context context, OnItemClickListener mlistener) {
        this.context = context;
        soListItems = new ArrayList<>();
        listener = mlistener;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        switch (viewType) {
            case ITEM:
                viewHolder = getViewHolder(parent, inflater);
                break;
            case LOADING:
                View v2 = inflater.inflate(R.layout.progress, parent, false);
                viewHolder = new LoadingVH(v2);
                break;
        }
        return viewHolder;
    }

    @NonNull
    private RecyclerView.ViewHolder getViewHolder(ViewGroup parent, LayoutInflater inflater) {
        RecyclerView.ViewHolder viewHolder;
        View v1 = inflater.inflate(R.layout.row_so_list, parent, false);

        viewHolder = new ItemListView(v1);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        SOListDTO result = soListItems.get(position); // Movie

        switch (getItemViewType(position)) {
            case ITEM:
                final ItemListView itemListView = (ItemListView) holder;

                itemListView.tvCustomerName.setText(result.getCustomer());
                itemListView.tv_OMS_SONum.setText(result.getOMSSONumber());
                itemListView.tvCreatedOn.setText(result.getOrderDate());
                itemListView.tvValue.setText(result.getSOValue());

                switch (result.getStatus()) {
                    case "OPEN":
                        itemListView.tvStatus.setTextColor(context.getResources().getColor(R.color.safron));
                        break;

                    case "INPROCESS":
                        itemListView.tvStatus.setTextColor(context.getResources().getColor(R.color.green));
                        break;

                    case "REJECTED":
                        itemListView.tvStatus.setTextColor(context.getResources().getColor(R.color.red));
                        break;

                    default:
                        itemListView.tvStatus.setTextColor(context.getResources().getColor(R.color.yellow));
                        break;
                }

                itemListView.tvStatus.setText(result.getStatus());

                break;

            case LOADING:
//                Do nothing

                break;
        }

    }

    @Override
    public int getItemCount() {
        return soListItems == null ? 0 : soListItems.size();
    }

    @Override
    public int getItemViewType(int position) {
        return (position == soListItems.size() - 1 && isLoadingAdded) ? LOADING : ITEM;
    }


   /*Helpers
   _________________________________________________________________________________________________*/


    public void add(SOListDTO r) {
        soListItems.add(r);
        notifyItemInserted(soListItems.size() - 1);

    }

    public void addAll(List<SOListDTO> list) {
        if (list.size() > 0)
            for (SOListDTO result : list)
                add(result);

    }

    public void remove(SOListDTO r) {
        int position = soListItems.indexOf(r);
        if (position > -1) {
            soListItems.remove(position);
            notifyItemRemoved(position);
        }
    }

    public void clear() {
        isLoadingAdded = false;
        while (getItemCount() > 0) {
            remove(getItem(0));
        }
    }

    public boolean isEmpty() {
        return getItemCount() == 0;
    }


    public void addLoadingFooter() {
        isLoadingAdded = true;
        add(new SOListDTO());
    }

    public void removeLoadingFooter() {
        isLoadingAdded = false;

        int position = soListItems.size() - 1;
        SOListDTO result = getItem(position);

        if (result != null) {
            soListItems.remove(position);
            notifyItemRemoved(position);
        }
    }

    public SOListDTO getItem(int position) {
        return soListItems.get(position);
    }


  /* View Holders
   _________________________________________________________________________________________________*/


    /**
     * Main list's content ViewHolder
     */


    public class ItemListView extends RecyclerView.ViewHolder {
        private TextView tv_OMS_SONum, tvStatus, tvCustomerName, tvValue, tvCreatedOn;


        public ItemListView(View view) {
            super(view);
            // Initializing Views
            tv_OMS_SONum = (TextView) view.findViewById(R.id.tv_OMS_SONum);
            tvStatus = (TextView) view.findViewById(R.id.tvStatus);
            tvCustomerName = (TextView) view.findViewById(R.id.tvCustomerName);
            tvValue = (TextView) view.findViewById(R.id.tvValue);
            tvCreatedOn = (TextView) view.findViewById(R.id.tvCreatedOn);

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


    protected class LoadingVH extends RecyclerView.ViewHolder {

        public LoadingVH(View itemView) {
            super(itemView);
        }
    }


    // Item click listener interface
    public interface OnItemClickListener {

        void onItemClick(int pos);


    }

}
