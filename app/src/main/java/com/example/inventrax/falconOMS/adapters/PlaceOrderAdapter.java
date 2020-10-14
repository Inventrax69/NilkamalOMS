package com.example.inventrax.falconOMS.adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.inventrax.falconOMS.R;
import com.example.inventrax.falconOMS.pojos.AvailableStockDTO;
import com.example.inventrax.falconOMS.pojos.PlaceOrderResponce;

import java.util.List;

/**
 * Created by padmaja on 13/07/19.
 */
public class PlaceOrderAdapter extends RecyclerView.Adapter<PlaceOrderAdapter.ViewHolder> {

    private List<PlaceOrderResponce> placeOrderResponces;
    private Context context;
    private OnItemClickListener mlistener;
    Button btnClOSE;

    public PlaceOrderAdapter(Context context, List<PlaceOrderResponce> placeOrderResponces, OnItemClickListener mlistener) {
        this.context = context;
        this.placeOrderResponces = placeOrderResponces;
        this.mlistener = mlistener;
    }

    @Override
    public PlaceOrderAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.place_order_view, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {

        PlaceOrderResponce placeOrderResponce = placeOrderResponces.get(i);

        viewHolder.txtCardRefNo.setText(placeOrderResponce.getCustomerName());
        viewHolder.txtCustomer.setText(placeOrderResponce.getCartRefNo());
        if(placeOrderResponce.getIsCreditLimit()==1){
            if(placeOrderResponce.getExceptionError()==null){
                viewHolder.txtStatus.setText("Credit Limit Approval (Initiated)");
                viewHolder.txtStatus.setBackgroundColor(Color.YELLOW);
                viewHolder.txtStatus.setTextColor(Color.BLACK);
            }else{
                viewHolder.txtStatus.setText(""+placeOrderResponce.getExceptionError());
                viewHolder.txtStatus.setBackgroundColor(Color.YELLOW);
                viewHolder.txtStatus.setTextColor(Color.BLACK);
            }
        }
        else{
            viewHolder.txtStatus.setText("Order Created");
            viewHolder.txtStatus.setBackgroundColor(Color.GREEN);
            viewHolder.txtStatus.setTextColor(Color.BLACK);
        }


        if(placeOrderResponces.get(i).getOrdersList()==null){
                viewHolder.SolistLinear.setVisibility(View.GONE);
        }else{
            viewHolder.SolistLinear.setVisibility(View.VISIBLE);
            String[] list =new String[placeOrderResponces.get(i).getOrdersList().size()];
            for(int p=0;p<placeOrderResponces.get(i).getOrdersList().size();p++){
                list[p]=placeOrderResponces.get(i).getOrdersList().get(p).getSONumber();
            }

            viewHolder.txtSoList.setText(""+arrayToString(list));
        }

    }

    public static String arrayToString(String array[]) {
        if (array.length == 0) return "";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < array.length; ++i) {
            sb.append(",").append(array[i]).append("");
        }
        return sb.substring(1);
    }

    @Override
    public int getItemCount() {
        return placeOrderResponces.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView txtCardRefNo, txtCustomer, txtStatus,txtSoList;
        LinearLayout SolistLinear;

        public ViewHolder(View view) {
            super(view);
            // Initializing views
            txtCardRefNo = (TextView) view.findViewById(R.id.txtCardRefNo);
            txtCustomer = (TextView) view.findViewById(R.id.txtCustomer);
            txtStatus = (TextView) view.findViewById(R.id.txtStatus);
            txtSoList = (TextView) view.findViewById(R.id.txtSoList);
            SolistLinear = (LinearLayout) view.findViewById(R.id.SolistLinear);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mlistener != null) {
                        int pos = getAdapterPosition();
                        if (pos != RecyclerView.NO_POSITION) {
                            mlistener.OnItemClick(pos);
                        }
                    }
                }
            });

        }
    }

    // Item Click listener interface
    public interface OnItemClickListener {
        void OnItemClick(int pos);
    }

}
