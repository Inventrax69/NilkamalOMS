package com.example.inventrax.falconOMS.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.inventrax.falconOMS.R;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by padmaja on 13/07/19.
 */
public class ImagesAdapter extends RecyclerView.Adapter<ImagesAdapter.ViewHolder> {
    private List items;
    private Context context;
    OnItemClickListener listener;


    public ImagesAdapter(Context applicationContext, List itemArrayList, OnItemClickListener mlistener) {
        this.context = applicationContext;
        this.items = itemArrayList;
        listener = mlistener;
    }

    public ImagesAdapter() {

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_image_imagedetails, viewGroup, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {


        Picasso.with(context)
                .load(String.valueOf(items.get(i)))
                .placeholder(R.drawable.load)
                .into(viewHolder.imgview);


    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView imgview;


        public ViewHolder(View view) {
            super(view);

            // Initializing views
            imgview = (ImageView) view.findViewById(R.id.imgview);

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

    // item click listener
    public interface OnItemClickListener {
        void onItemClick(int pos);

    }
}
