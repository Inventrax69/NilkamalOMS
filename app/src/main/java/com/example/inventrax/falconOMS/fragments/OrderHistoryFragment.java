package com.example.inventrax.falconOMS.fragments;

import android.annotation.SuppressLint;
import android.app.SearchManager;
import android.app.Service;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.InputType;
import android.text.method.DigitsKeyListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.inventrax.falconOMS.R;
import com.example.inventrax.falconOMS.activities.MainActivity;
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
import com.example.inventrax.falconOMS.room.RoomAppDatabase;
import com.example.inventrax.falconOMS.services.RestService;
import com.example.inventrax.falconOMS.util.DialogUtils;
import com.example.inventrax.falconOMS.util.ExceptionLoggerUtils;
import com.example.inventrax.falconOMS.util.FragmentUtils;
import com.example.inventrax.falconOMS.util.NetworkUtils;
import com.example.inventrax.falconOMS.util.ProgressDialogUtils;
import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderHistoryFragment extends Fragment implements View.OnClickListener, SearchView.OnQueryTextListener {

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
    private int pageSize = 50;

    private List<SOListDTO> lstItem;


    SearchView.SearchAutoComplete searchAutoComplete;
    MenuItem item;
    SearchView searchView;
    SearchManager searchManager;


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (getActivity() != null) {
            ((MainActivity) getActivity()).isSearchOpened = false;
        }

        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Service.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(searchAutoComplete.getWindowToken(), 0);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.order_history_fragment, container, false);

        try {
            loadFormControls();
        } catch (Exception e) {

            Log.d("Exception", e.toString());

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
        rvSOList = (RecyclerView) rootView.findViewById(R.id.rvSOList);
        layoutManager = new LinearLayoutManager(getContext());

        SharedPreferences sp = getContext().getSharedPreferences(KeyValues.MY_PREFS, Context.MODE_PRIVATE);
        userId = sp.getString(KeyValues.USER_ID, "");

        db = new RoomAppDatabase(getActivity()).getAppDatabase();


        rvSOList.setLayoutManager(layoutManager);


        rvSOList.addOnScrollListener(new PaginationScrollListener(layoutManager) {
            @Override
            protected void loadMoreItems() {
                if (!searchAutoComplete.isFocused()) {
                    isLoading = true;
                    currentPage += 1;

                    // mocking network delay for API cal
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {

                            loadNextPage();

                        }
                    }, 1000);
                } else {
                    isLoading = false;
                }
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

        loadFirstPage("", false);

        soListAdapter = new SoListAdapter(getContext(), new SoListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int pos) {

                if (lstItem.size() > 0) {
                    Bundle bundle = new Bundle();
                    searchView.setIconified(true);
                    searchView.clearFocus();
                    searchView.onActionViewCollapsed();
                    bundle.putInt(KeyValues.SELECTED_SOHEADER, soListAdapter.getItem(pos).getSOHeaderID());
                    bundle.putString(KeyValues.SELECTED_CUSTOMER, soListAdapter.getItem(pos).getCustomer());
                    bundle.putString(KeyValues.SO_PRICE, soListAdapter.getItem(pos).getSOValue());
                    FragmentUtils.addFragmentWithBackStackBundle(getActivity(), R.id.container, new OrderHistoryDetailsFragment(), bundle);
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


    @SuppressLint("RestrictedApi")
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        if (menu != null) {

            item = menu.findItem(R.id.cust_action_search);
            item.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
            item.setVisible(true);

            final MenuItem item1 = menu.findItem(R.id.action_home);
            item1.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
            item1.setVisible(false);


            //searchView = (SearchView) item.getActionView();
            searchView = (SearchView) MenuItemCompat.getActionView(item);
            searchView.setMaxWidth(android.R.attr.width);
            searchView.setOnQueryTextListener(this);
            searchView.setIconifiedByDefault(true);
            //searchView.setIconified(false);
            //searchView.setFocusable(true);

            // Get SearchView autocomplete object.
            searchAutoComplete = (SearchView.SearchAutoComplete) searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
            searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
            searchAutoComplete.setKeyListener(DigitsKeyListener.getInstance("abcdefghijklmnopqrstuvwxyz 0123456789"));
            searchAutoComplete.setInputType(InputType.TYPE_CLASS_TEXT);
            searchAutoComplete.setTextColor(Color.WHITE);
            searchAutoComplete.setThreshold(3);


            Field f = null;
            try {
                f = TextView.class.getDeclaredField("mCursorDrawableRes");
                f.setAccessible(true);
                f.set(searchAutoComplete, R.drawable.cursor);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
            }

            //searchAutoComplete.setHintTextColor(Color.WHITE); ABCDEFGHIJKLMNOPQRSTUVWXYZ
            searchAutoComplete.setDropDownBackgroundResource(R.color.white);

            searchAutoComplete.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    String searchString = (String) parent.getItemAtPosition(position);
                    searchAutoComplete.setText("" + searchString);

                    // Check if no view has focus:
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Service.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(searchAutoComplete.getWindowToken(), 0);

                   /* // Toast.makeText(getContext(), "Your search result is " + " " + searchString, Toast.LENGTH_LONG).show();
                    mmId = hashMap.get(searchString);
                    loadView();*/

                }
            });

        }

        super.onCreateOptionsMenu(menu, inflater);

    }

    // When enter pressed
    @Override
    public boolean onQueryTextSubmit(String searchString) {

        loadFirstPage(searchString, true);
        searchView.setFocusable(true);
        searchAutoComplete.setFocusable(true);
        return false;
    }

    @Override
    public boolean onQueryTextChange(String searchText) {

        if (searchText != null && !searchText.equals("") && searchText.length() >= 2) {

            if (!Character.isWhitespace(searchText.charAt(0))) {

                String lastChar = searchText.substring(searchText.length() - 1);
                if (lastChar.equals(" ")) {

                    searchText.trim();

                    loadFirstPage(searchText, true);
                  /*  Toast.makeText(getContext(), "space bar pressed" + "" + searchText,
                            Toast.LENGTH_SHORT).show();*/
                } else {
                    loadFirstPage(searchText, true);
                }
            } else {
                Toast.makeText(getContext(), "space at starting is not valid", Toast.LENGTH_SHORT).show();
                searchAutoComplete.setText("");
            }
        }

        return true;
    }

    public void loadFirstPage(String searchString, final boolean fromSearch) {

        if (NetworkUtils.isInternetAvailable(getContext())) {
        } else {
            DialogUtils.showAlertDialog(getActivity(), errorMessages.EMC_0007);
            // soundUtils.alertSuccess(LoginActivity.this,getBaseContext());
            return;
        }

        OMSCoreMessage message = new OMSCoreMessage();
        message = common.SetAuthentication(EndpointConstants.SO_FPS_DTO, getContext());
        final ItemListDTO itemListDTO = new ItemListDTO();
        itemListDTO.setSearchString(searchString);
        itemListDTO.setPageIndex(1);
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

                                soListAdapter.addAll(lstItem);

                                if (fromSearch) {
                                    soListAdapter.clear();
                                    soListAdapter.addAll(lstItem);
                                    isLastPage = false;
                                } else {

                                    if (currentPage <= TOTAL_PAGES)
                                        soListAdapter.addLoadingFooter();
                                    else isLastPage = true;
                                }
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

                                LinkedTreeMap<?, ?> _lstItem = new LinkedTreeMap<String, String>();
                                _lstItem = (LinkedTreeMap<String, String>) core.getEntityObject();

                                Paging paging;
                                final List<ItemListDTO> lstDto = new ArrayList<ItemListDTO>();

                                try {

                                    paging = new Paging(_lstItem.entrySet());

                                    lstItem = paging.getResults();

                                    double totalPages = Double.parseDouble(paging.getPageNumber());
                                    TOTAL_PAGES = Integer.parseInt(String.valueOf(totalPages).split("[.]", 2)[0]);

                                } catch (Exception e) {
                                    common.showUserDefinedAlertType("No items found", getActivity(), getContext(), "Warning");
                                    // logException();
                                }

                                soListAdapter.addAll(lstItem);

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


    @Override
    public void onResume() {
        super.onResume();
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(getString(R.string.toolbar_orderhistory));
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayUseLogoEnabled(false);
    }


}
