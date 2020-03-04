package com.example.inventrax.falconOMS.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.inventrax.falconOMS.R;
import com.example.inventrax.falconOMS.pojos.NotificationDTO;

import java.util.List;

public class NotificationListAdapter extends BaseAdapter {

    Context context;
    List<NotificationDTO> lstNotifications;
    LayoutInflater inflter;

    public NotificationListAdapter(Context context, List<NotificationDTO> lstNotifications) {
        this.context = context;
        this.lstNotifications = lstNotifications;
        inflter = (LayoutInflater.from(context));
    }

    @Override
    public int getCount() {
        return lstNotifications.size();
    }

    @Override
    public Object getItem(int i) {
        return lstNotifications.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        view = inflter.inflate(R.layout.row_notification, null);

        TextView tvNotifTriggerName = (TextView) view.findViewById(R.id.tvNotifTriggerName);
        TextView tvNotifTriggerDescription = (TextView) view.findViewById(R.id.tvNotifTriggerDescription);
        TextView tvCreatedOn = (TextView) view.findViewById(R.id.tvCreatedOn);
        TextView tvReference = (TextView) view.findViewById(R.id.tvReference);

        NotificationDTO notification = (NotificationDTO) lstNotifications.get(i);

        tvNotifTriggerName.setText(notification.getNotifTriggerName());
        tvNotifTriggerDescription.setText(notification.getNotifTriggerDescription());
        tvCreatedOn.setText("Created On : " + notification.getCreatedOn());

        if (notification.getReference().isEmpty()) {
            tvReference.setVisibility(View.GONE);
        } else {
            tvReference.setVisibility(View.VISIBLE);
        }
        tvReference.setText("Cart Ref No: " + notification.getReference());

        return view;
    }
}