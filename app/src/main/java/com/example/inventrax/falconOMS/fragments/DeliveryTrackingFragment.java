package com.example.inventrax.falconOMS.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.inventrax.falconOMS.R;
import com.example.inventrax.falconOMS.adapters.DeliveryTrackingAdapter;
import com.example.inventrax.falconOMS.model.OrderStatus;
import com.example.inventrax.falconOMS.model.TimeLineModel;

import java.util.ArrayList;

public class DeliveryTrackingFragment extends Fragment implements View.OnClickListener {

    private static final String classCode = "OMS_Android_DeliveryTrackingFragment";
    private View rootView;

    private RecyclerView rvTracking;
    private LinearLayoutManager linearLayoutManager;

    private ArrayList<TimeLineModel> mDataList;
    DeliveryTrackingAdapter adapter;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.delivery_tracking_fragment, container, false);

        loadFormControls();

        return rootView;
    }

    private void loadFormControls() {

        mDataList = new ArrayList<TimeLineModel>();

        // To disable Bottom navigation bar
        //((MainActivity) getActivity()).SetNavigationVisibility(false);

        rvTracking = (RecyclerView) rootView.findViewById(R.id.rvTracking);
        linearLayoutManager = new LinearLayoutManager(getContext());
        rvTracking.setLayoutManager(linearLayoutManager);

        adapter = new DeliveryTrackingAdapter();
        setDataListItems();

        initRecyclerView();
    }


    @Override
    public void onClick(View v) {

        switch (v.getId()) {

        }

    }
    private void setDataListItems() {

        mDataList.add(new TimeLineModel("Item has reached courier facility at New Delhi", "2017-02-11 21:00", OrderStatus.COMPLETED));
        mDataList.add(new TimeLineModel("Item has been given to the courier", "2017-02-11 18:00", OrderStatus.ACTIVE));
        mDataList.add(new TimeLineModel("Courier is out to delivery your order", "2017-02-12 08:00", OrderStatus.PENDING));
        mDataList.add(new TimeLineModel("Item successfully delivered", "", OrderStatus.INACTIVE));
    }

    private void initRecyclerView(){

       /* DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(rvTracking.getContext(), linearLayoutManager.getOrientation());
        rvTracking.addItemDecoration(dividerItemDecoration);*/

        adapter = new DeliveryTrackingAdapter(getContext(),mDataList);
        rvTracking.setAdapter(adapter);

    }

    @Override
    public void onResume() {
        super.onResume();
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(getString(R.string.toolbar_deliverytracking));
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayUseLogoEnabled(false);
    }


}
