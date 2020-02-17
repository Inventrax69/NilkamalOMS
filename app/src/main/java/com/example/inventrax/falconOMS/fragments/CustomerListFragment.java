package com.example.inventrax.falconOMS.fragments;

/*
 * Created by Padmaja on 04/07/2019.
 */

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.inventrax.falconOMS.R;
import com.example.inventrax.falconOMS.adapters.CustomerListAdapter;
import com.example.inventrax.falconOMS.common.Common;
import com.example.inventrax.falconOMS.common.constants.ErrorMessages;
import com.example.inventrax.falconOMS.model.KeyValues;
import com.example.inventrax.falconOMS.pojos.OMSCoreMessage;
import com.example.inventrax.falconOMS.room.AppDatabase;
import com.example.inventrax.falconOMS.room.CustomerTable;
import com.example.inventrax.falconOMS.room.RoomAppDatabase;
import com.example.inventrax.falconOMS.services.RestService;
import com.example.inventrax.falconOMS.util.ExceptionLoggerUtils;
import com.example.inventrax.falconOMS.util.FragmentUtils;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class CustomerListFragment extends Fragment {

    private View rootView;
    private static final String classCode = "OMS_Android_CustomerListFragmnet";

    private RecyclerView rvCustomerList;
    TextView Disconnected;
    ProgressDialog pd;
    private SwipeRefreshLayout swipeContainer;
    private TextView txtView;
    LinearLayoutManager layoutManager;

    private Common common;
    private ExceptionLoggerUtils exceptionLoggerUtils;
    private ErrorMessages errorMessages;
    private Gson gson;
    RestService restService;
    private OMSCoreMessage core;

    private String cusomerIDs = "", userId = "";
    List<CustomerTable> customers;
    CustomerTable custTable;

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

        try {

            common = new Common();
            errorMessages = new ErrorMessages();
            exceptionLoggerUtils = new ExceptionLoggerUtils();
            restService = new RestService();
            core = new OMSCoreMessage();
            layoutManager = new LinearLayoutManager(getContext());

            SharedPreferences sp = getContext().getSharedPreferences(KeyValues.MY_PREFS, Context.MODE_PRIVATE);
            userId = sp.getString(KeyValues.USER_ID, "");
            cusomerIDs = sp.getString(KeyValues.CUSTOMER_IDS, "");

            db = new RoomAppDatabase(getActivity()).getAppDatabase();

            pd = new ProgressDialog(getContext());
            pd.setMessage("Fetching Customers...");
            pd.setCancelable(false);
            //pd.show();
            Disconnected = (TextView) rootView.findViewById(R.id.disconnected);

            rvCustomerList = (RecyclerView) rootView.findViewById(R.id.rvCustomerList);
            rvCustomerList.setLayoutManager(layoutManager);
            rvCustomerList.smoothScrollToPosition(0);

            JSONArray jsonArray = null;
            try {
                jsonArray = new JSONArray(cusomerIDs);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            customers = new ArrayList();

            for (int i = 0; i < jsonArray.length(); i++) {

                try {
                    if (jsonArray.getString(i) != null && db.customerDAO().getCustomer(jsonArray.getString(i))!= null) {

                        if (db.customerDAO().getCustomer(jsonArray.getString(i)).customerId != null && db.customerDAO().getCustomer(jsonArray.getString(i)).customerName != null) {
                            custTable = db.customerDAO().getCustomer(jsonArray.getString(i));
                            customers.add(custTable);
                        }

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            if (customers.size() != 0) {
                loadCustomers(customers);
            }

        }catch (Exception ex){

        }

    }

    private void loadCustomers(final List<CustomerTable> lst) {

        CustomerListAdapter customerListAdapter = new CustomerListAdapter(getContext(), (ArrayList) lst, new CustomerListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int pos) {

                String customerId = lst.get(pos).customerId;
                String customerName= lst.get(pos).customerName;
                String divisionId= lst.get(pos).divisionId;

                Bundle bundle=new Bundle();
                bundle.putSerializable(KeyValues.SELECTED_CUSTOMER,lst.get(pos));
                FragmentUtils.replaceFragmentWithBackStackWithBundle(getActivity(), R.id.container, new CustomerHomeFragment(),bundle);

            }
        });


        rvCustomerList.setAdapter(customerListAdapter);
        //rvCustomerList.scrollToPosition(0);
    }


    @Override
    public void onResume() {
        super.onResume();

        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(getString(R.string.toolbar_customerList));
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayUseLogoEnabled(false);


    }


}
