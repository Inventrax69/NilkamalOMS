package com.example.inventrax.falconOMS.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.inventrax.falconOMS.R;
import com.example.inventrax.falconOMS.model.MainMenu;

import java.util.List;

/**
 * Created by padmaja on 13/07/19.
 */
public class MainMenuViewAdapter extends RecyclerView.Adapter<MainMenuViewAdapter.ViewHolder> {

    private List<MainMenu> items;
    private Context context;
    OnItemClickListener listener;

    public MainMenuViewAdapter(Context applicationContext, List<MainMenu> items, OnItemClickListener mlistener) {
        this.context = applicationContext;
        this.items = items;
        listener = mlistener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.main_menu_layout, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {

        MainMenu mainMenu = items.get(i);
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) viewHolder.cvMainCard.getLayoutParams();
        if ((i % 4) == 0) {
            viewHolder.cvMainCard.setBackgroundResource(R.drawable.card_view_rb);
            params.addRule(RelativeLayout.ALIGN_PARENT_END, RelativeLayout.TRUE);
        } else if ((i % 4) == 1) {
            viewHolder.cvMainCard.setBackgroundResource(R.drawable.card_view_lb);
            params.addRule(RelativeLayout.ALIGN_PARENT_START, RelativeLayout.TRUE);
        } else if ((i % 4) == 2) {
            viewHolder.cvMainCard.setBackgroundResource(R.drawable.card_view_rt);
            params.addRule(RelativeLayout.ALIGN_PARENT_END, RelativeLayout.TRUE);
        } else {
            viewHolder.cvMainCard.setBackgroundResource(R.drawable.card_view_lt);
            params.addRule(RelativeLayout.ALIGN_PARENT_START, RelativeLayout.TRUE);
        }

        viewHolder.mainMenuLayout.setBackground(getDrawableImage(mainMenu.getTileName()));
        viewHolder.titleName.setText(context.getString(mainMenu.getDisplayName()));

    }

    public Drawable getDrawableImage(String tileName) {
        Drawable drawable;
        switch (tileName) {
            case "Customer":
                drawable = ActivityCompat.getDrawable(context, R.drawable.ic_customers_new);
                break;
            case "OrderBooking":
                drawable = ActivityCompat.getDrawable(context, R.drawable.ic_order_booking_new);
                break;
            case "OrderTracking":
                drawable = ActivityCompat.getDrawable(context, R.drawable.ic_order_tracking_new);
                break;
            case "OrderAssistance":
                drawable = ActivityCompat.getDrawable(context, R.drawable.ic_order_assistance_new);
                break;
            case "OrderList":
                drawable = ActivityCompat.getDrawable(context, R.drawable.ic_order_list_new);
                break;
            case "EInformation":
                drawable = ActivityCompat.getDrawable(context, R.drawable.ic_e_info_new);
                break;
            case "Dashboard":
                drawable = ActivityCompat.getDrawable(context, R.drawable.ic_dashboard_new);
                break;
            case "Inventory":
                drawable = ActivityCompat.getDrawable(context, R.drawable.ic_inventory_new);
                break;
            case "Complaints":
                drawable = ActivityCompat.getDrawable(context, R.drawable.ic_complaints_new);
                break;
            case "Approvals":
                drawable = ActivityCompat.getDrawable(context, R.drawable.ic_approval);
                break;
            default:
                drawable = ActivityCompat.getDrawable(context, R.drawable.ic_customers_new);
                break;
        }

        return drawable;

    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        CardView cvMainCard;
        LinearLayout mainMenuLayout;
        TextView titleName;

        public ViewHolder(View view) {
            super(view);
            // Initializing views;
            cvMainCard = (CardView) view.findViewById(R.id.cvMainCard);
            mainMenuLayout = (LinearLayout) view.findViewById(R.id.mainMenuLayout);
            titleName = (TextView) view.findViewById(R.id.titleName);

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

}
