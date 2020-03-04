package com.example.inventrax.falconOMS.adapters;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.inventrax.falconOMS.R;
import com.example.inventrax.falconOMS.model.OrderStatus;
import com.example.inventrax.falconOMS.model.TimeLineModel;
import com.github.vipulasri.timelineview.TimelineView;

import java.util.ArrayList;

/**
 * Created by padmaja on 13/07/19.
 */
public class DeliveryTrackingAdapter extends RecyclerView.Adapter<DeliveryTrackingAdapter.ViewHolder> {

    private ArrayList items;
    private Context context;

    public DeliveryTrackingAdapter(Context applicationContext, ArrayList itemArrayList) {
        this.context = applicationContext;
        this.items = itemArrayList;
    }

    public DeliveryTrackingAdapter() {

    }

    @Override
    public DeliveryTrackingAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_delivery, viewGroup, false);
        view.setClickable(false);
        return new ViewHolder(view);
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {

        TimeLineModel timeLineModel = (TimeLineModel) items.get(i);

        ((ViewHolder) viewHolder).text_timeline_date.setText(timeLineModel.getDate());
        ((ViewHolder) viewHolder).text_timeline_title.setText(timeLineModel.getMessage());


        if (timeLineModel.getStatus().equals(OrderStatus.INACTIVE)) {
            ((ViewHolder) viewHolder).timeline.setMarkerColor(R.color.gray);
        } else if (timeLineModel.getStatus().equals(OrderStatus.ACTIVE)) {
            ((ViewHolder) viewHolder).timeline.setMarker(context.getDrawable(R.drawable.check_circle_yellow));
        } else if (timeLineModel.getStatus().equals(OrderStatus.PENDING)) {
            ((ViewHolder) viewHolder).timeline.setMarker(context.getDrawable(R.drawable.pending));
        } else {
            ((ViewHolder) viewHolder).timeline.setMarker(context.getDrawable(R.drawable.check_circle_img));
        }

        int j = items.size() - 1;
        if (j == i) {
            ((ViewHolder) viewHolder).timeline.setEndLineColor(0, 0);
        }


    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TimelineView timeline;
        private TextView text_timeline_date, text_timeline_title;


        public ViewHolder(View view) {
            super(view);
            // Initializing views

            timeline = (TimelineView) view.findViewById(R.id.timeline);
            text_timeline_title = (TextView) view.findViewById(R.id.text_timeline_title);
            text_timeline_date = (TextView) view.findViewById(R.id.text_timeline_date);


            timeline.setMarkerColor(R.color.yellow);
            timeline.setEndLineColor(R.color.colorAccent, 1);
            timeline.setStartLineColor(R.color.colorAccent, 1);
            timeline.setMarkerSize(90);
        }
    }


}
