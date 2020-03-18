package com.example.inventrax.falconOMS.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.inventrax.falconOMS.R;
import com.example.inventrax.falconOMS.adapters.SoListAdapter;
import com.example.inventrax.falconOMS.common.Common;
import com.example.inventrax.falconOMS.common.constants.EndpointConstants;
import com.example.inventrax.falconOMS.common.constants.ErrorMessages;
import com.example.inventrax.falconOMS.interfaces.ApiInterface;
import com.example.inventrax.falconOMS.listeners.PaginationScrollListener;
import com.example.inventrax.falconOMS.model.KeyValues;
import com.example.inventrax.falconOMS.pojos.ItemListDTO;
import com.example.inventrax.falconOMS.pojos.OMSCoreMessage;
import com.example.inventrax.falconOMS.pojos.OMSExceptionMessage;
import com.example.inventrax.falconOMS.pojos.Paging;
import com.example.inventrax.falconOMS.pojos.SOListDTO;
import com.example.inventrax.falconOMS.room.AppDatabase;
import com.example.inventrax.falconOMS.room.CustomerTable;
import com.example.inventrax.falconOMS.room.RoomAppDatabase;
import com.example.inventrax.falconOMS.services.RestService;
import com.example.inventrax.falconOMS.util.DialogUtils;
import com.example.inventrax.falconOMS.util.ExceptionLoggerUtils;
import com.example.inventrax.falconOMS.util.NetworkUtils;
import com.example.inventrax.falconOMS.util.ProgressDialogUtils;
import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderHistoryFragment extends Fragment implements View.OnClickListener {

    private static final String classCode = "OMS_Android_OrderHistoryFragment";
    private View rootView;

    private RecyclerView rvSOList;
    private LinearLayoutManager layoutManager;

    private Common common;
    private ExceptionLoggerUtils exceptionLoggerUtils;
    private ErrorMessages errorMessages;
    private Gson gson;
    RestService restService;
    private OMSCoreMessage core;

    private String userId = "";

    private SoListAdapter soListAdapter;
    AppDatabase db;


    // Index from which pagination should start (0 is 1st page in our case)
    private static final int PAGE_START = 1;

    // Indicates if footer ProgressBar is shown (i.e. next page is loading)
    private boolean isLoading = false;

    // If current page is the last page (Pagination will stop after this page load)
    private boolean isLastPage = false;

    // total no. of pages to load.
    private int TOTAL_PAGES = 0;
    // indicates the current page which Pagination is fetching.
    private int currentPage = PAGE_START;
    private int pageSize = 30;

    private List<SOListDTO> lstItem;
    private List<SOListDTO> updatedList;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.order_history_fragment, container, false);

        try{
            loadFormControls();
        }catch (Exception e){

            Log.d("Exception",e.toString());

        }


        return rootView;
    }

    private void loadFormControls() {

        common = new Common();
        errorMessages = new ErrorMessages();
        exceptionLoggerUtils = new ExceptionLoggerUtils();
        restService = new RestService();
        core = new OMSCoreMessage();

        lstItem = new ArrayList<>();
        updatedList = new ArrayList<>();
        rvSOList = (RecyclerView) rootView.findViewById(R.id.rvSOList);
        layoutManager = new LinearLayoutManager(getContext());

        SharedPreferences sp = getContext().getSharedPreferences(KeyValues.MY_PREFS, Context.MODE_PRIVATE);
        userId = sp.getString(KeyValues.USER_ID, "");

        db = new RoomAppDatabase(getActivity()).getAppDatabase();


        rvSOList.setLayoutManager(layoutManager);


        rvSOList.addOnScrollListener(new PaginationScrollListener(layoutManager) {
            @Override
            protected void loadMoreItems() {
                isLoading = true;
                currentPage += 1;

                // mocking network delay for API call
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        loadNextPage();
                    }
                }, 1000);
            }

            @Override
            public int getTotalPageCount() {
                return TOTAL_PAGES;
            }

            @Override
            public boolean isLastPage() {
                return isLastPage;
            }

            @Override
            public boolean isLoading() {
                return isLoading;
            }
        });

        loadFirstPage();

        soListAdapter = new SoListAdapter(getContext(),  new SoListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int pos) {

                if(lstItem.size()>0) {
                    Bundle bundle = new Bundle();
                    //bundle.putSerializable(KeyValues.SELECTED_CUSTOMER, soListAdapter.getItem(pos));
                    //FragmentUtils.replaceFragmentWithBackStackWithBundle(getActivity(), R.id.container, new CustomerHomeFragment(), bundle);
                }
            }
        });


        rvSOList.setAdapter(soListAdapter);

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }


    public void loadFirstPage() {

        if (NetworkUtils.isInternetAvailable(getContext())) {
        } else {
            DialogUtils.showAlertDialog(getActivity(), errorMessages.EMC_0007);
            // soundUtils.alertSuccess(LoginActivity.this,getBaseContext());
            return;
        }

        OMSCoreMessage message = new OMSCoreMessage();
        message = common.SetAuthentication(EndpointConstants.SO_FPS_DTO, getContext());
        final ItemListDTO itemListDTO = new ItemListDTO();
        itemListDTO.setSearchString("");
        itemListDTO.setPageIndex(currentPage);
        itemListDTO.setPageSize(pageSize);
        message.setEntityObject(itemListDTO);


        Call<OMSCoreMessage> call = null;
        ApiInterface apiService =
                RestService.getClient().create(ApiInterface.class);


        call = apiService.SOListMOB(message);
        ProgressDialogUtils.showProgressDialog("Please Wait");


        try {
            //Getting response from the method
            call.enqueue(new Callback<OMSCoreMessage>() {

                @Override
                public void onResponse(Call<OMSCoreMessage> call, Response<OMSCoreMessage> response) {
                    ProgressDialogUtils.closeProgressDialog();

                    if (response.body() != null) {

                        core = response.body();

                        if (core != null) {


                            if ((core.getType().toString().equals("Exception"))) {
                                List<OMSExceptionMessage> _lExceptions = new ArrayList<OMSExceptionMessage>();
                                _lExceptions = core.getOMSMessages();

                                OMSExceptionMessage omsExceptionMessage = null;

                                for (OMSExceptionMessage oms : core.getOMSMessages()) {

                                    omsExceptionMessage = oms;
                                    ProgressDialogUtils.closeProgressDialog();
                                    common.showAlertType(omsExceptionMessage, getActivity(), getContext());
                                }


                            } else {

                                ProgressDialogUtils.closeProgressDialog();

                                LinkedTreeMap<?, ?> _lstItem = new LinkedTreeMap<String, String>();
                                _lstItem = (LinkedTreeMap<String, String>) core.getEntityObject();


                                Paging paging;

                                try {

                                    paging = new Paging(_lstItem.entrySet());

                                    lstItem = paging.getResults();

                                    double totalPages = Double.parseDouble(paging.getPageNumber());
                                    TOTAL_PAGES = Integer.parseInt(String.valueOf(totalPages).split("[.]", 2)[0]);

                                } catch (Exception e) {
                                    common.showUserDefinedAlertType("No items found", getActivity(), getContext(), "Warning");
                                    // logException();
                                }

                                updatedList.clear();
                                soListAdapter.addAll(lstItem);
                                updatedList.addAll(lstItem);

                                if (currentPage <= TOTAL_PAGES) soListAdapter.addLoadingFooter();
                                else isLastPage = true;

                                ProgressDialogUtils.closeProgressDialog();

                            }
                            ProgressDialogUtils.closeProgressDialog();
                        }
                        ProgressDialogUtils.closeProgressDialog();
                    }
                    ProgressDialogUtils.closeProgressDialog();
                }

                // response object fails
                @Override
                public void onFailure(Call<OMSCoreMessage> call, Throwable throwable) {
                    Toast.makeText(getContext(), throwable.toString(), Toast.LENGTH_LONG).show();
                    ProgressDialogUtils.closeProgressDialog();
                }
            });
        } catch (Exception ex) {

            try {
                exceptionLoggerUtils.createExceptionLog(ex.toString(), classCode, "001", getContext());

            } catch (IOException e) {
                e.printStackTrace();
            }
            ProgressDialogUtils.closeProgressDialog();
            DialogUtils.showAlertDialog(getActivity(), errorMessages.EMC_0003);
        }
    }


    public void loadNextPage() {

        if (NetworkUtils.isInternetAvailable(getContext())) {
        } else {
            DialogUtils.showAlertDialog(getActivity(), errorMessages.EMC_0007);
            // soundUtils.alertSuccess(LoginActivity.this,getBaseContext());
            return;
        }

        OMSCoreMessage message = new OMSCoreMessage();
        message = common.SetAuthentication(EndpointConstants.SO_FPS_DTO, getContext());
        ItemListDTO itemListDTO = new ItemListDTO();
        itemListDTO.setPageIndex(currentPage);
        itemListDTO.setSearchString("");
        itemListDTO.setPageSize(pageSize);
        message.setEntityObject(itemListDTO);


        Call<OMSCoreMessage> call = null;
        ApiInterface apiService =
                RestService.getClient().create(ApiInterface.class);


        call = apiService.SOListMOB(message);
        ProgressDialogUtils.showProgressDialog("Please Wait");


        try {
            //Getting response from the method
            call.enqueue(new Callback<OMSCoreMessage>() {

                @Override
                public void onResponse(Call<OMSCoreMessage> call, Response<OMSCoreMessage> response) {
                    ProgressDialogUtils.closeProgressDialog();

                    soListAdapter.removeLoadingFooter();
                    isLoading = false;


                    if (response.body() != null) {

                        core = response.body();

                        if (core != null) {


                            if ((core.getType().toString().equals("Exception"))) {
                                List<OMSExceptionMessage> _lExceptions = new ArrayList<OMSExceptionMessage>();
                                _lExceptions = core.getOMSMessages();

                                OMSExceptionMessage omsExceptionMessage = null;

                                for (OMSExceptionMessage oms : core.getOMSMessages()) {

                                    omsExceptionMessage = oms;
                                    ProgressDialogUtils.closeProgressDialog();
                                    common.showAlertType(omsExceptionMessage, getActivity(), getContext());
                                }


                            } else {

                                ProgressDialogUtils.closeProgressDialog();

                                ProgressDialogUtils.closeProgressDialog();

                                LinkedTreeMap<?, ?> _lstItem = new LinkedTreeMap<String, String>();
                                _lstItem = (LinkedTreeMap<String, String>) core.getEntityObject();


                                Paging paging;
                                final List<ItemListDTO> lstDto = new ArrayList<ItemListDTO>();

                                try {

                                    updatedList.clear();
                                    paging = new Paging(_lstItem.entrySet());
                                    updatedList = lstItem;

                                    lstItem = paging.getResults();

                                    double totalPages = Double.parseDouble(paging.getPageNumber());
                                    TOTAL_PAGES = Integer.parseInt(String.valueOf(totalPages).split("[.]", 2)[0]);

                                } catch (Exception e) {
                                    common.showUserDefinedAlertType("No items found", getActivity(), getContext(), "Warning");
                                    // logException();
                                }

                                soListAdapter.addAll(lstItem);
                                updatedList.addAll(lstItem);


                                if (currentPage != TOTAL_PAGES) soListAdapter.addLoadingFooter();
                                else isLastPage = true;

                                ProgressDialogUtils.closeProgressDialog();

                            }
                            ProgressDialogUtils.closeProgressDialog();
                        }
                        ProgressDialogUtils.closeProgressDialog();
                    }
                    ProgressDialogUtils.closeProgressDialog();
                }

                // response object fails
                @Override
                public void onFailure(Call<OMSCoreMessage> call, Throwable throwable) {
                    Toast.makeText(getContext(), throwable.toString(), Toast.LENGTH_LONG).show();
                    ProgressDialogUtils.closeProgressDialog();
                }
            });
        } catch (Exception ex) {

            try {
                exceptionLoggerUtils.createExceptionLog(ex.toString(), classCode, "001", getContext());

            } catch (IOException e) {
                e.printStackTrace();
            }
            ProgressDialogUtils.closeProgressDialog();
            DialogUtils.showAlertDialog(getActivity(), errorMessages.EMC_0003);
        }
    }



    @Override
    public void onClick(View v) {

        switch (v.getId()) {

        }

    }

    private void loadSOList(final List<CustomerTable> lst) {


    }




    @Override
    public void onResume() {
        super.onResume();
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(getString(R.string.toolbar_orderhistory));
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayUseLogoEnabled(false);
    }


}
