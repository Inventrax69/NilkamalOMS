package com.example.inventrax.falconOMS.adapters;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.example.inventrax.falconOMS.R;
import com.example.inventrax.falconOMS.fragments.CartFragment;
import com.example.inventrax.falconOMS.pojos.CustomerPartnerDTO;

import java.util.List;

public class CartMaterialHeaderAdapter extends RecyclerView.Adapter<CartMaterialHeaderAdapter.ItemViewHolder> {

    private static final String classCode = "OMS_Android_CartMaterialHeaderAdapter";
    private RecyclerView.RecycledViewPool viewPool = new RecyclerView.RecycledViewPool();
    private List<CustomerPartnerDTO> customerPartnerDTOS;
    private Context context;
    private Activity activity;
    private CartFragment cartFragment;
    OnItemClickListener mlistener;

    public CartMaterialHeaderAdapter(List<CustomerPartnerDTO> customerPartnerDTOS, Context context, Activity activity, CartFragment cartFragment, OnItemClickListener mlistener) {
        this.customerPartnerDTOS = customerPartnerDTOS;
        this.context = context;
        this.activity = activity;
        this.cartFragment = cartFragment;
        this.mlistener = mlistener;
    }

    public CartMaterialHeaderAdapter() {
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_cartmaterialheader, viewGroup, false);
        return new ItemViewHolder(view);
    }

    private CartMaterialdetailsAdapter cartMaterialdetailsAdapter;

    @Override
    public void onBindViewHolder(@NonNull final ItemViewHolder itemViewHolder, final int i) {

        final CustomerPartnerDTO headerListDTO = customerPartnerDTOS.get(i);
        itemViewHolder.customerName.setText(headerListDTO.getPartnerName());

        // Create layout manager with initial prefetch item count
        LinearLayoutManager layoutManager = new LinearLayoutManager(
                itemViewHolder.rvSubItem.getContext(),
                LinearLayoutManager.VERTICAL,
                false
        );

        layoutManager.setInitialPrefetchItemCount(headerListDTO.getMaterials().size());

        // Create sub item view adapter
        cartMaterialdetailsAdapter = new CartMaterialdetailsAdapter(context, headerListDTO.getMaterials(), new CartMaterialdetailsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int pos) {

            }
        });

        itemViewHolder.rvSubItem.setLayoutManager(layoutManager);
        itemViewHolder.rvSubItem.setAdapter(cartMaterialdetailsAdapter);
        itemViewHolder.rvSubItem.setRecycledViewPool(viewPool);

        if (customerPartnerDTOS.get(i).getIsProcessID().equals("1")) {
            itemViewHolder.rOQ.setChecked(true);
        }
        if (customerPartnerDTOS.get(i).getIsProcessID().equals("2")) {
            itemViewHolder.rAQ.setChecked(true);
        }

        itemViewHolder.rIsProcessId.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int pos) {
                if (itemViewHolder.rOQ.getId() == radioGroup.getCheckedRadioButtonId()) {
                    customerPartnerDTOS.get(i).setIsProcessID("1");
                    mlistener.onChangeIsProcessID(i, "1");
                }
                if (itemViewHolder.rAQ.getId() == radioGroup.getCheckedRadioButtonId()) {
                    customerPartnerDTOS.get(i).setIsProcessID("2");
                    mlistener.onChangeIsProcessID(i, "2");
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return customerPartnerDTOS.size();
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {

        private TextView customerName;
        private RecyclerView rvSubItem;
        private RadioGroup rIsProcessId;
        private RadioButton rOQ, rAQ;

        ItemViewHolder(View itemView) {
            super(itemView);
            customerName = itemView.findViewById(R.id.tv_customerName);
            rvSubItem = itemView.findViewById(R.id.rv_cartDetails);
            rIsProcessId = itemView.findViewById(R.id.rIsProcessId);
            rOQ = itemView.findViewById(R.id.rOQ);
            rAQ = itemView.findViewById(R.id.rAQ);


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (mlistener != null) {
                        int pos = getAdapterPosition();
                        if (pos != RecyclerView.NO_POSITION) {
                            mlistener.onItemClick(pos);
                        }
                    }

                }
            });
        }
    }

    public interface OnItemClickListener {
        void onItemClick(int pos);

        void onChangeIsProcessID(int pos, String IsProcessID);
    }

}
