package com.example.inventrax.falconOMS.adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
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
public class RefSCMDetailsAdapter extends RecyclerView.Adapter<RefSCMDetailsAdapter.ViewHolder> {

    private List<ApprovalListDTO> approvalListDTOS;
    private Context context;
    OnItemClickListener listener;
    TextWatcher textWatcher;
    String type;

    public RefSCMDetailsAdapter(Context applicationContext, List<ApprovalListDTO> approvalListDTOS, String type, OnItemClickListener mlistener) {
        this.context = applicationContext;
        this.approvalListDTOS = approvalListDTOS;
        this.type = type;
        listener = mlistener;
    }

    public RefSCMDetailsAdapter() {
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view;
        view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.approval_scm_details, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, final int i) {

        final ApprovalListDTO approvalListDTO = approvalListDTOS.get(i);
        viewHolder.txtItemName.setText(approvalListDTO.getMaterialcode());
        viewHolder.txtItemDesc.setText(approvalListDTO.getDescription());
        viewHolder.txtQty.setText(String.valueOf(approvalListDTO.getQuantity()));

        if (!approvalListDTOS.get(i).isStatus()) {
            viewHolder.txtAdd.setText(context.getString(R.string.add));
            Drawable drawable = context.getResources().getDrawable(R.drawable.button_shape);
            viewHolder.txtAdd.setBackground(drawable);
        } else {
            viewHolder.txtAdd.setText(context.getString(R.string.done));
            Drawable drawable = context.getResources().getDrawable(R.drawable.button_shape_green);
            viewHolder.txtAdd.setBackground(drawable);
        }

    }

    @Override
    public int getItemCount() {
        return approvalListDTOS.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView txtItemName, txtItemDesc, txtQty, txtAdd;

        public ViewHolder(View view) {
            super(view);

            txtItemName = (TextView) view.findViewById(R.id.txtItemName);
            txtItemDesc = (TextView) view.findViewById(R.id.txtItemDesc);
            txtQty = (TextView) view.findViewById(R.id.txtQty);
            txtAdd = (TextView) view.findViewById(R.id.txtAdd);

            //on item click
            txtAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int pos = getAdapterPosition();
                        if (pos != RecyclerView.NO_POSITION) {
                            if (!approvalListDTOS.get(pos).isStatus())
                                listener.onItemClick(pos);
                            else
                                SnackbarUtils.showSnackbarLengthShort((CoordinatorLayout) ((Activity) context).findViewById(R.id.snack_bar_action_layout), "Material already sent", ContextCompat.getColor(context, R.color.dark_red), Snackbar.LENGTH_SHORT);
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
