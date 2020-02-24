package com.example.inventrax.falconOMS.fragments;

/*
 * Created by Padmaja on 04/07/2019.
 */

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.inventrax.falconOMS.R;
import com.example.inventrax.falconOMS.adapters.CustomerListAdapter;
import com.example.inventrax.falconOMS.common.Common;
import com.example.inventrax.falconOMS.common.Log.Logger;
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

import java.util.ArrayList;
import java.util.List;

public class CustomerListFragment extends Fragment implements SearchView.OnQueryTextListener {

    private View rootView;
    private static final String classCode = "OMS_Android_CustomerListFragmnet";

    private RecyclerView rvCustomerList;
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

    CustomerListAdapter customerListAdapter;

    AppDatabase db;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.customerlist_fragment, container, false);

        loadFormControl();

        return rootView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        if (menu != null) {

            MenuItem item = menu.findItem(R.id.cust_action_search);
            item.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
            item.setVisible(true);

            final SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);
            searchView.setMaxWidth(android.R.attr.width);
            searchView.setOnQueryTextListener(this);

            final MenuItem item1 = menu.findItem(R.id.action_home);
            item1.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
            item1.setVisible(false);

            super.onCreateOptionsMenu(menu, inflater);
        }
    }


    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String searchText) {
        List<CustomerTable> filteredModelList = null;

        filteredModelList = filter(db.customerDAO().getAll(), searchText);

        rvCustomerList.setLayoutManager(new LinearLayoutManager(getActivity()));


        // List<ItemTable> sample= db.itemDAO().getFilterAll(searchText);

        try {
            // data set changed
            if (filteredModelList != null)
                loadCustomers(filteredModelList);
            return true;
        } catch (Exception ex) {
            Logger.Log(ProductCatalogFragment.class.getName(), ex);
            return false;
        }
    }

    private List<CustomerTable> filter(List<CustomerTable> models, String query) {

        query = query.toLowerCase();

        final List<CustomerTable> filteredModelList = new ArrayList<>();
        try {

            for (CustomerTable model : models) {
                if (model != null) {
                    if (model.customerName.toLowerCase().contains(query.trim()) || model.customerCode.toLowerCase().contains(query.trim())) {
                        filteredModelList.add(model);
                    }
                }
            }

            return filteredModelList;

        } catch (Exception ex) {
            Logger.Log(ProductCatalogFragment.class.getName(), ex);
            return filteredModelList;
        }
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

            rvCustomerList = (RecyclerView) rootView.findViewById(R.id.rvCustomerList);
            rvCustomerList.setLayoutManager(layoutManager);
            rvCustomerList.smoothScrollToPosition(0);

            customers = new ArrayList();

            customers = db.customerDAO().getTop500Customers();

            if (customers.size() != 0) {
                loadCustomers(customers);
            }

        } catch (Exception ex) {

        }

    }

    private void loadCustomers(final List<CustomerTable> lst) {

        customerListAdapter = new CustomerListAdapter(getContext(), (ArrayList) lst, new CustomerListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int pos) {

                String customerId = lst.get(pos).customerId;
                String customerName = lst.get(pos).customerName;
                String divisionId = lst.get(pos).divisionId;

                Bundle bundle = new Bundle();
                bundle.putSerializable(KeyValues.SELECTED_CUSTOMER, lst.get(pos));
                FragmentUtils.replaceFragmentWithBackStackWithBundle(getActivity(), R.id.container, new CustomerHomeFragment(), bundle);

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
