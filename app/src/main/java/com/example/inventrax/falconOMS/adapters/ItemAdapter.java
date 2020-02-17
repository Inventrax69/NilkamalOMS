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
import com.example.inventrax.falconOMS.room.VariantTable;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by padmaja on 22/07/19.
 */
public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ViewHolder> {

    private List<VariantTable> items;
    private Context context;
    OnItemClickListener listener;

    public ItemAdapter(Context applicationContext, List<VariantTable> items, OnItemClickListener mlistener) {
        this.context = applicationContext;
        this.items = items;
        listener = mlistener;

    }

    public ItemAdapter(){

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view;

        view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_intellisearch_row, viewGroup, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {

        VariantTable variantTable = items.get(i);

        viewHolder.txtItemName.setText(variantTable.mCode);
        viewHolder.txtItemDesc.setText(variantTable.mDescription);
        //itemListView.txtPrice.setText("Rs" + " " + result.price + "/-");

        if(variantTable.materialImgPath.equals("")){
            viewHolder.ivItem.setImageResource(R.drawable.no_img);
        }else {

            if(variantTable.materialImgPath.split("[|]").length>0){
                Picasso.with(context)
                        .load(variantTable.materialImgPath.split("[|]")[0])
                        .placeholder(R.drawable.no_img)
                        .into(viewHolder.ivItem);
            }else {
                Picasso.with(context)
                        .load(variantTable.materialImgPath)
                        .placeholder(R.drawable.no_img)
                        .into(viewHolder.ivItem);
            }
        }


    }

    @Override
    public int getItemCount() {
        return items.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView txtItemName, txtItemDesc;
        private ImageView ivItem;


        public ViewHolder(View view) {
            super(view);
            // Initializing Views
            txtItemName = (TextView) view.findViewById(R.id.txtItemName);
            txtItemDesc = (TextView) view.findViewById(R.id.txtItemDesc);
            ivItem = (ImageView) view.findViewById(R.id.ivItem);

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
