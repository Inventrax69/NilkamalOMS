package com.example.inventrax.falconOMS.adapters;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.inventrax.falconOMS.R;
import com.example.inventrax.falconOMS.pojos.ApprovalListDTO;
import com.example.inventrax.falconOMS.util.SnackbarUtils;

import java.util.List;

/**
 * Created by padmaja on 22/07/19.
 */
public class RefDetailsAdapter extends RecyclerView.Adapter<RefDetailsAdapter.ViewHolder> {

    private List<ApprovalListDTO> approvalListDTOS;
    private Context context;
    OnItemClickListener listener;
    TextWatcher textWatcher;
    OnApprovalUpdate onApprovalListener;
    OnCheckValues onCheckValuesListener;
    String type;

    public RefDetailsAdapter(Context applicationContext, List<ApprovalListDTO> approvalListDTOS, String type, OnItemClickListener mlistener, OnApprovalUpdate onApprovalListener, OnCheckValues onCheckValuesListener) {
        this.context = applicationContext;
        this.approvalListDTOS = approvalListDTOS;
        this.type = type;
        listener = mlistener;
        this.onApprovalListener = onApprovalListener;
        this.onCheckValuesListener = onCheckValuesListener;
    }

    public RefDetailsAdapter() {
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view;
        if (type.equals("4")) {
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.approval_credit_details, viewGroup, false);
        } else {
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.approval_inactive_details, viewGroup, false);
        }
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, final int i) {

        final ApprovalListDTO approvalListDTO = approvalListDTOS.get(i);
        viewHolder.txtItemName.setText(approvalListDTO.getMaterialcode());
        viewHolder.txtItemDesc.setText(approvalListDTO.getDescription());
        viewHolder.txtQty.setText(String.valueOf(approvalListDTO.getQuantity()));
        viewHolder.txtPrice.setText(String.valueOf(approvalListDTO.getPrice()));
        viewHolder.txtBasePrice.setText(String.valueOf(approvalListDTO.getBasePrice()));

        if (type.equals("4")) {
            viewHolder.txtPrice.setEnabled(true);
            viewHolder.txtPrice.setVisibility(View.VISIBLE);
            viewHolder.txtBasePrice.setVisibility(View.VISIBLE);
            viewHolder.lblPrice.setVisibility(View.VISIBLE);
            viewHolder.lblBasePrice.setVisibility(View.VISIBLE);
        } else {
            viewHolder.txtPrice.setEnabled(false);
            viewHolder.txtPrice.setVisibility(View.INVISIBLE);
            viewHolder.txtBasePrice.setVisibility(View.INVISIBLE);
            viewHolder.lblPrice.setVisibility(View.INVISIBLE);
            viewHolder.lblBasePrice.setVisibility(View.INVISIBLE);
        }

/*        viewHolder.txtPrice.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                    approvalListDTOS.get(i).setPrice(viewHolder.txtPrice.getText().toString());
                    notifyItemChanged(i);
                    notifyDataSetChanged();
                    onApprovalListener.onApprovalUpdate(approvalListDTOS);
            }
        });*/

        viewHolder.txtPrice.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (!viewHolder.txtPrice.getText().toString().isEmpty() && ((int) Double.parseDouble(viewHolder.txtPrice.getText().toString())) != 0) {
                    approvalListDTOS.get(i).setPrice(viewHolder.txtPrice.getText().toString());
                    approvalListDTOS.get(i).setCorrectValue(false);
                    onApprovalListener.onApprovalUpdate(approvalListDTOS);
                } else {
                    onCheckValuesListener.onCheckValues(i);
                    SnackbarUtils.showSnackbarLengthShort((CoordinatorLayout) ((Activity) context).findViewById(R.id.snack_bar_action_layout), "Enter correct value", ContextCompat.getColor(context, R.color.dark_red), Snackbar.LENGTH_SHORT);
                }
            }

        });

    }

    @Override
    public int getItemCount() {
        return approvalListDTOS.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView txtItemName, txtItemDesc, txtQty, txtPrice, txtBasePrice, lblBasePrice, lblPrice;

        public ViewHolder(View view) {
            super(view);

            txtItemName = (TextView) view.findViewById(R.id.txtItemName);
            txtItemDesc = (TextView) view.findViewById(R.id.txtItemDesc);
            txtQty = (TextView) view.findViewById(R.id.txtQty);
            txtBasePrice = (TextView) view.findViewById(R.id.txtBasePrice);
            txtPrice = (TextView) view.findViewById(R.id.txtPrice);
            lblBasePrice = (TextView) view.findViewById(R.id.lblBasePrice);
            lblPrice = (TextView) view.findViewById(R.id.lblPrice);

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

    // Item click listener interface
    public interface OnApprovalUpdate {
        void onApprovalUpdate(List<ApprovalListDTO> approvalListDTOS);
    }

    // Item click listener interface
    public interface OnCheckValues {
        void onCheckValues(int pos);
    }
}
