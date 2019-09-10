package com.example.inventrax.falconOMS.fragments;

/*
 * Created by Padmaja on 04/07/2019.
 */

import android.app.ProgressDialog;
import android.arch.persistence.room.Room;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.inventrax.falconOMS.R;
import com.example.inventrax.falconOMS.adapters.CustomerListAdapter;
import com.example.inventrax.falconOMS.common.Common;
import com.example.inventrax.falconOMS.common.constants.ErrorMessages;
import com.example.inventrax.falconOMS.pojos.OMSCoreMessage;
import com.example.inventrax.falconOMS.room.AppDatabase;
import com.example.inventrax.falconOMS.services.RestService;
import com.example.inventrax.falconOMS.util.ExceptionLoggerUtils;
import com.google.gson.Gson;

import java.util.ArrayList;

public class CustomerListFragment extends Fragment {

    private View rootView;
    private static final String classCode = "OMS_Android_CustomerListFragmnet";

    private RecyclerView rvCustomerList;
    TextView Disconnected;
    ProgressDialog pd;
    private SwipeRefreshLayout swipeContainer;
    private TextView txtView;


    private Common common;
    private ExceptionLoggerUtils exceptionLoggerUtils;
    private ErrorMessages errorMessages;
    private Gson gson;
    RestService restService;
    private OMSCoreMessage core;


    AppDatabase db;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.customerlist_fragment, container, false);

        loadFormControl();

        swipeContainer = (SwipeRefreshLayout) rootView.findViewById(R.id.swipeContainer);

        int c1 = getResources().getColor(R.color.colorAccent);
        int c2 = getResources().getColor(android.R.color.holo_orange_dark);

        swipeContainer.setColorSchemeColors(c1, c2);

        //swipeContainer.setColorSchemeResources(android.R.color.holo_orange_dark);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                rvCustomerList.smoothScrollToPosition(0);
                swipeContainer.setRefreshing(false);
            }


        });
        return rootView;
    }

    private void loadFormControl() {

        common = new Common();
        errorMessages = new ErrorMessages();
        exceptionLoggerUtils = new ExceptionLoggerUtils();
        restService = new RestService();
        core = new OMSCoreMessage();


        db = Room.databaseBuilder(getActivity(),
                AppDatabase.class, "room_oms").allowMainThreadQueries().build();

        Log.v("ABCDE", new Gson().toJson(db.customerDAO().getAll()));


        pd = new ProgressDialog(getContext());
        pd.setMessage("Fetching Customers...");
        pd.setCancelable(false);
        //pd.show();
        Disconnected = (TextView) rootView.findViewById(R.id.disconnected);


        rvCustomerList = (RecyclerView) rootView.findViewById(R.id.rvCustomerList);
        rvCustomerList.setLayoutManager(new LinearLayoutManager(getContext()));
        rvCustomerList.smoothScrollToPosition(0);

        loadCustomers();



    }

    private void loadCustomers() {

        CustomerListAdapter customerListAdapter = new CustomerListAdapter(getContext(), (ArrayList) db.customerDAO().getAll(), new CustomerListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int pos) {

            }
        });


        rvCustomerList.setAdapter(customerListAdapter);
    }



    @Override
    public void onResume() {
        super.onResume();

        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(getString(R.string.toolbar_customerList));
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayUseLogoEnabled(false);


    }


}
