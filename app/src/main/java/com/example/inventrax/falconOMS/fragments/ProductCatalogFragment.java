package com.example.inventrax.falconOMS.fragments;

/*
 * Created by Padmaja on 04/07/2019.
 */

import android.annotation.TargetApi;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.inventrax.falconOMS.R;
import com.example.inventrax.falconOMS.activities.MainActivity;
import com.example.inventrax.falconOMS.adapters.OffersAdapter;
import com.example.inventrax.falconOMS.adapters.PaginationAdapter;
import com.example.inventrax.falconOMS.common.Common;
import com.example.inventrax.falconOMS.common.Log.Logger;
import com.example.inventrax.falconOMS.common.constants.EndpointConstants;
import com.example.inventrax.falconOMS.common.constants.ErrorMessages;
import com.example.inventrax.falconOMS.interfaces.ApiInterface;
import com.example.inventrax.falconOMS.model.KeyValues;
import com.example.inventrax.falconOMS.pojos.CartDTO;
import com.example.inventrax.falconOMS.pojos.CartResponseListDTO;
import com.example.inventrax.falconOMS.pojos.OMSCoreMessage;
import com.example.inventrax.falconOMS.pojos.OMSExceptionMessage;
import com.example.inventrax.falconOMS.pojos.PriceDTO;
import com.example.inventrax.falconOMS.room.AppDatabase;
import com.example.inventrax.falconOMS.room.CartDetails;
import com.example.inventrax.falconOMS.room.CartHeader;
import com.example.inventrax.falconOMS.room.CustomerTable;
import com.example.inventrax.falconOMS.room.ItemTable;
import com.example.inventrax.falconOMS.room.UserDivisionCustTable;
import com.example.inventrax.falconOMS.room.VariantTable;
import com.example.inventrax.falconOMS.services.RestService;
import com.example.inventrax.falconOMS.util.DateUtils;
import com.example.inventrax.falconOMS.util.DialogUtils;
import com.example.inventrax.falconOMS.util.ExceptionLoggerUtils;
import com.example.inventrax.falconOMS.util.FragmentUtils;
import com.example.inventrax.falconOMS.util.NetworkUtils;
import com.example.inventrax.falconOMS.util.ProgressDialogUtils;
import com.example.inventrax.falconOMS.util.SnackbarUtils;
import com.example.inventrax.falconOMS.util.searchableSpinner.SearchableSpinner;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductCatalogFragment extends Fragment implements SearchView.OnQueryTextListener, AdapterView.OnItemSelectedListener, View.OnClickListener {

    private View rootView;
    private static final String classCode = "OMS_Android_ProductCatalog";

    private FrameLayout frame;
    private RecyclerView recyclerView;

    //private SwipeRefreshLayout swipeContainer;
    private CardView txtViewType;
    private ImageView ivViewType,ivSort;
    private LinearLayout llSort;

    private ImageView ivAddToCartBottom, ivItemBottom;
    BottomSheetBehavior behavior;
    CoordinatorLayout coordinatorLayout;
    private SearchableSpinner spinnerColorSelectionBottom;
    private TextView txtItemNameBottom;
    private EditText etQtyBottom, deliveryDatePicker,etPrice;

    private PaginationAdapter mAdapter;

    private Common common;
    private ExceptionLoggerUtils exceptionLoggerUtils;
    private ErrorMessages errorMessages;
    private Gson gson;
    RestService restService;
    private OMSCoreMessage core;
    List<ItemTable> items;
    List<VariantTable> variantTables = null;
    private boolean isgrid = false;
    private boolean isDesc = false;

    private VariantTable selectedVariant;
    private String selectedVariantImage;
    private String selected;
    private String materialId = "",price = "";

    boolean loading = true;
    int pastVisiblesItems, visibleItemCount, totalItemCount;

    LinearLayoutManager layoutManager;
    int pagecount = 0;

    AppDatabase db;
    String myFormat = "dd/MMM/yyyy";

    private CustomerTable custTable;
    private  String userId = "", cusomerIDs = "",partnerId = "";
    UserDivisionCustTable userDivisionCustTable;
    private boolean isCustomerMatched = false;
    List<CartDTO> cartList = null;
    AlertDialog dialog;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.productcatalog_fragment, container, false);
        isgrid = false;
        loadFormControl();


        return rootView;
    }

    private void loadFormControl() {


        // To enable Bottom navigation bar
        ((MainActivity) getActivity()).SetNavigationVisibiltity(true);
        SharedPreferences sp = getContext().getSharedPreferences(KeyValues.MY_PREFS, Context.MODE_PRIVATE);
        userId = sp.getString(KeyValues.USER_ID, "");
        cusomerIDs = sp.getString(KeyValues.CUSTOMER_IDS, "");

        frame = (FrameLayout) rootView.findViewById(R.id.frame);

        recyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);

        llSort = (LinearLayout) rootView.findViewById(R.id.llSort);
        llSort.setOnClickListener(this);

        mAdapter = new PaginationAdapter(isgrid);

        db = Room.databaseBuilder(getActivity(),
                AppDatabase.class, "room_oms").allowMainThreadQueries().build();


        coordinatorLayout = (CoordinatorLayout) rootView.findViewById(R.id.coordinatorLayout);
        ivAddToCartBottom = (ImageView) rootView.findViewById(R.id.ivAddToCartBottom);
        ivItemBottom = (ImageView) rootView.findViewById(R.id.ivItemBottom);
        txtItemNameBottom = (TextView) rootView.findViewById(R.id.txtItemNameBottom);
        etQtyBottom = (EditText) rootView.findViewById(R.id.etQtyBottom);
        etPrice = (EditText) rootView.findViewById(R.id.etPrice);
        deliveryDatePicker = (EditText) rootView.findViewById(R.id.deliveryDatePicker);

        spinnerColorSelectionBottom = (SearchableSpinner) rootView.findViewById(R.id.spinnerColorSelectionBottom);
        spinnerColorSelectionBottom.setOnItemSelectedListener(this);

        common = new Common();
        errorMessages = new ErrorMessages();
        exceptionLoggerUtils = new ExceptionLoggerUtils();
        restService = new RestService();
        core = new OMSCoreMessage();
        cartList = new ArrayList<>();

        View bottomSheet = rootView.findViewById(R.id.bottom_sheet);
        behavior = BottomSheetBehavior.from(bottomSheet);
        behavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                // React to state change

            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                // React to dragging events
                if (String.valueOf(slideOffset).equalsIgnoreCase("0.0")) {
                    frame.setVisibility(View.GONE);
                }
            }
        });

        txtViewType = (CardView) rootView.findViewById(R.id.txtViewType);
        txtViewType.setOnClickListener(this);
        ivViewType = (ImageView) rootView.findViewById(R.id.ivViewType);
        ivSort = (ImageView) rootView.findViewById(R.id.ivSort);


        if (!db.itemDAO().getAllItems(pagecount).isEmpty()) {
            layoutManager = new LinearLayoutManager(getContext());
            recyclerView.setLayoutManager(layoutManager);
            // shows the items which are there in sqlite item table
            updateToRecyclerView(db.itemDAO().getAllItems(pagecount));
        } else {
            layoutManager = new LinearLayoutManager(getContext());
            recyclerView.setLayoutManager(layoutManager);

        }


       /* recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) {
                    ((MainActivity) getActivity()).SetNavigationVisibiltity(false);
                    visibleItemCount = layoutManager.getChildCount();
                    totalItemCount = layoutManager.getItemCount();
                    pastVisiblesItems = layoutManager.findFirstVisibleItemPosition();

                    if (loading) {
                        if ((visibleItemCount + pastVisiblesItems) >= totalItemCount) {
                            //loading = true;
                            //Log.v("pagination", "Last Item Wow !");
                            //Do pagination.. i.e. fetch new data
                            mAdapter.addLoadingFooter();

                            pagecount++;
                            //updateToRecyclerView(db.itemDAO().getAllItems(pagecount));

                        }
                    }
                } else if (dy < 0) {
                    ((MainActivity) getActivity()).SetNavigationVisibiltity(true);
                }
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {

                super.onScrollStateChanged(recyclerView, newState);

            }
        });*/


        // Date Picker Dailog integration and validation
        final Calendar myCalendar = Calendar.getInstance();
        final DatePickerDialog.OnDateSetListener datePickerListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.UK);

                String date = sdf.format(myCalendar.getTime());

                Date today = null;
                Date selectedDate = null;

                try {
                    today = sdf.parse(DateUtils.getDate(myFormat));       // gets the current date
                    selectedDate = sdf.parse(date);                       // gets the date from date picker
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                // shows err when selected date before the current date
                if (selectedDate.before(today)) {
                    deliveryDatePicker.setText("");
                    deliveryDatePicker.setError("Please select valid date");
                    Toast.makeText(getContext(), "Date specified [" + selectedDate + "] is before today [" + today + "]", Toast.LENGTH_SHORT).show();
                } else {
                    deliveryDatePicker.setError(null);
                    deliveryDatePicker.setText(sdf.format(myCalendar.getTime()));
                }

            }

        };

        // onClick for edit text to popup the date picker
        deliveryDatePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), datePickerListener, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH));
                String myDate = "2019/08/12";
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MMM/yyyy");
                Date date = null;
                try {
                    //date = sdf.parse(myDate);   // If a it requires selected date is mentioned
                    date = sdf.parse(DateUtils.getDate(myFormat));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                long millis = date.getTime();
                datePickerDialog.getDatePicker().setMinDate(millis);
                datePickerDialog.show();

            }
        });


        recyclerView.smoothScrollToPosition(0);


        JSONArray jsonArray = null;
        try {
            jsonArray = new JSONArray(cusomerIDs);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        for (int i = 0; i < jsonArray.length(); i++) {

            try {
                if (jsonArray.getString(i) != null) {

                    if (db.customerDAO().getCustomer(jsonArray.getString(i)).customerId != null && db.customerDAO().getCustomer(jsonArray.getString(i)).custName != null) {
                        custTable = db.customerDAO().getCustomer(jsonArray.getString(i));
                        if (db.userDivisionCustDAO().getAll().size() <= 0) {
                            db.userDivisionCustDAO().insert(new UserDivisionCustTable(userId, custTable.customerId, custTable.divisionId));
                        }
                    }

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        if (menu != null) {

            final MenuItem item = menu.findItem(R.id.cust_action_search);
            item.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
            item.setVisible(true);
            final SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);
            searchView.setMaxWidth(android.R.attr.width);
            searchView.setOnQueryTextListener(this);
        }

        super.onCreateOptionsMenu(menu, inflater);

    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String searchText) {

        final List<ItemTable> filteredModelList = filter(db.itemDAO().getAll(), searchText);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        try {
            // data set changed
            updateToRecyclerView(filteredModelList);

            if (TextUtils.isEmpty(searchText)) {
                updateToRecyclerView(items);

            }
            mAdapter.notifyDataSetChanged();
            return true;
        } catch (Exception ex) {
            Logger.Log(ProductCatalogFragment.class.getName(), ex);
            return false;
        }
    }

    private List<ItemTable> filter(List<ItemTable> models, String query) {

        query = query.toLowerCase();

        final List<ItemTable> filteredModelList = new ArrayList<>();
        try {

            for (ItemTable model : models) {
                if (model != null) {
                    if (model.modelCode.toLowerCase().contains(query.trim()) || model.modelDescription.toLowerCase().contains(query.trim())) {
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

    public void updateToRecyclerView(List<ItemTable> itemList) {

        items = new ArrayList<>();
        items.addAll(itemList);


        ProgressDialogUtils.showProgressDialog("Please wait..");

        mAdapter = new PaginationAdapter(getContext(), new PaginationAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int pos) {
                Toast.makeText(getContext(), String.valueOf(items.get(pos).modelCode), Toast.LENGTH_SHORT).show();
                behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

                frame.setVisibility(View.GONE);
                //go to item details page
                navigateToItemDetails(pos);
            }

            @Override
            public void onCartClick(int pos) {

                behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                frame.setVisibility(View.VISIBLE);
                txtItemNameBottom.setText(String.valueOf(items.get(pos).modelCode));

                ArrayList<String> varient = new ArrayList<>();


                db = Room.databaseBuilder(getActivity(),
                        AppDatabase.class, "room_oms").allowMainThreadQueries().build();


                variantTables = db.variantDAO().getVariants(items.get(pos).modelID);

                for (VariantTable var : variantTables) {

                    varient.add(var.modelColor);

                }

                ArrayAdapter adapter = new ArrayAdapter(getContext(), R.layout.support_simple_spinner_dropdown_item, varient);
                spinnerColorSelectionBottom.setAdapter(adapter);

                if (!items.get(pos).imgPath.equals("")) {
                    Picasso.with(getContext())
                            .load(items.get(pos).imgPath)
                            .placeholder(R.drawable.no_img)
                            .into(ivItemBottom);

                } else {
                    ivItemBottom.setImageResource(R.drawable.no_img);
                }



                ivAddToCartBottom.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (!etQtyBottom.getText().toString().isEmpty()) {

                            if (!etQtyBottom.getText().toString().equalsIgnoreCase("0")) {

                                if (selectedVariant != null) {


                                    if (NetworkUtils.isInternetAvailable(getContext())) {

                                        CartDTO cartDTO = new CartDTO();
                                        cartDTO.setMaterialMasterID(selectedVariant.materialID);
                                        cartDTO.setMCode(selectedVariant.mCode);
                                        cartDTO.setQuantity(etQtyBottom.getText().toString());
                                        cartDTO.setCustomerID(partnerId);
                                        cartDTO.setImagePath(selectedVariant.materialImgPath);
                                        cartDTO.setPrice(price);
                                        if(deliveryDatePicker.getText().toString().equals("")){
                                            cartDTO.setDeliveryDate(null);
                                        }else {
                                            cartDTO.setDeliveryDate(deliveryDatePicker.getText().toString());
                                        }

                                        cartList.add(cartDTO);

                                    } else {

                                        db.cartDetailsDAO().insert(new CartDetails(selectedVariant.materialID, selectedVariant.mCode,
                                                selectedVariant.mDescription, deliveryDatePicker.getText().toString(), "",
                                                etQtyBottom.getText().toString(), selectedVariantImage, price,"","", partnerId));
                                    }



                                    if(isCustomerMatched){

                                        if (NetworkUtils.isInternetAvailable(getContext())) {
                                            if(!price.equals("")) {
                                                addToCart();
                                            }else {
                                                SnackbarUtils.showSnackbarLengthShort(coordinatorLayout, getString(R.string.PriceNotAvailable), ContextCompat.getColor(getActivity(), R.color.colorAccent), Snackbar.LENGTH_SHORT);
                                                return;
                                            }
                                        }else {
                                            // Alert user that no internet is available and price may change after placing order

                                            Bundle bundle = new Bundle();
                                            bundle.putBoolean(KeyValues.IS_ITEM_ADDED_TO_CART, true);
                                            CartFragment cartFragment = new CartFragment();
                                            cartFragment.setArguments(bundle);
                                            FragmentUtils.replaceFragmentWithBackStack(getActivity(), R.id.container, cartFragment);
                                            return;
                                        }
                                    }else {
                                        SnackbarUtils.showSnackbarLengthShort(coordinatorLayout, getString(R.string.divisionIDnotmatched), ContextCompat.getColor(getActivity(), R.color.colorAccent), Snackbar.LENGTH_SHORT);
                                        return;
                                    }


                                }
                            } else {
                                SnackbarUtils.showSnackbarLengthShort(coordinatorLayout, getString(R.string.addTocartAlert), ContextCompat.getColor(getActivity(), R.color.colorAccent), Snackbar.LENGTH_SHORT);
                            }
                        } else {
                            SnackbarUtils.showSnackbarLengthShort(coordinatorLayout, getString(R.string.addTocartAlert), ContextCompat.getColor(getActivity(), R.color.colorAccent), Snackbar.LENGTH_SHORT);
                        }

                        behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                        frame.setVisibility(View.GONE);

                    }
                });
            }

            @Override
            public void onSchemeClick(int pos) {
                showAvailableOffers(pos);
            }
        }, isgrid,getActivity());


        mAdapter.addAll(items);
        recyclerView.setAdapter(mAdapter);

        recyclerView.scrollToPosition(pagecount * 50);

        ProgressDialogUtils.closeProgressDialog();

    }

    public void getPrice() {


        OMSCoreMessage message = new OMSCoreMessage();
        message = common.SetAuthentication(EndpointConstants.ProductCatalog_FPS_DTO, getContext());
        PriceDTO oDto = new PriceDTO();
        oDto.setMaterialMasterID(materialId);
        message.setEntityObject(oDto);


        Call<OMSCoreMessage> call = null;
        ApiInterface apiService =
                RestService.getClient().create(ApiInterface.class);


        call = apiService.GetPrice(message);
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

                                OMSExceptionMessage omsExceptionMessage = null;

                                for (OMSExceptionMessage oms : core.getOMSMessages()) {

                                    omsExceptionMessage = oms;
                                    ProgressDialogUtils.closeProgressDialog();
                                    common.showAlertType(omsExceptionMessage, getActivity(), getContext());
                                }


                            } else {

                                JSONObject jObject;
                                try {
                                    jObject = new JSONObject(new Gson().toJson(core.getEntityObject()));
                                    etPrice.setText("");
                                    price = jObject.getString("Amount");
                                    etPrice.setText("Rs." + price);

                                } catch (JSONException e) {
                                    e.printStackTrace();
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
                ExceptionLoggerUtils.createExceptionLog(ex.toString(), classCode, "001", getContext());

            } catch (IOException e) {
                e.printStackTrace();
            }
            ProgressDialogUtils.closeProgressDialog();
            DialogUtils.showAlertDialog(getActivity(), errorMessages.EMC_0003);
        }
    }

    public void addToCart() {


        OMSCoreMessage message = new OMSCoreMessage();
        message = common.SetAuthentication(EndpointConstants.HHTCartDTO, getContext());
        CartDTO oDto = new CartDTO();
        oDto.setUserID(userId);
        oDto.setCartHHT(cartList);
        message.setEntityObject(oDto);


        Call<OMSCoreMessage> call = null;
        ApiInterface apiService =
                RestService.getClient().create(ApiInterface.class);


        call = apiService.HHTCartDetails(message);



        try {
            //Getting response from the method
            call.enqueue(new Callback<OMSCoreMessage>() {

                @Override
                public void onResponse(Call<OMSCoreMessage> call, Response<OMSCoreMessage> response) {

                    if (response.body() != null) {

                        core = response.body();

                        if (core != null) {

                            if ((core.getType().toString().equals("Exception"))) {

                                OMSExceptionMessage omsExceptionMessage = null;

                                for (OMSExceptionMessage oms : core.getOMSMessages()) {

                                    omsExceptionMessage = oms;
                                    ProgressDialogUtils.closeProgressDialog();
                                    common.showAlertType(omsExceptionMessage, getActivity(), getContext());
                                }
                                ProgressDialogUtils.closeProgressDialog();

                            } else {

                                etQtyBottom.setText("1");
                                deliveryDatePicker.setText("");

                                ProgressDialogUtils.closeProgressDialog();


                                syncCart();



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
                ExceptionLoggerUtils.createExceptionLog(ex.toString(), classCode, "001", getContext());

            } catch (IOException e) {
                e.printStackTrace();
            }
            ProgressDialogUtils.closeProgressDialog();
            DialogUtils.showAlertDialog(getActivity(), errorMessages.EMC_0003);
        }
    }

    public void syncCart() {


        OMSCoreMessage message = new OMSCoreMessage();
        message = common.SetAuthentication(EndpointConstants.ProductCatalog_FPS_DTO, getContext());
        CartDTO cartDTO = new CartDTO();
        cartDTO.setHandheldRequest(true);
        cartDTO.setCartHeaderID(0);
        cartDTO.setUserID(userId);
        cartDTO.setCustomerID(partnerId);
        cartDTO.setSearchString(null);
        cartDTO.setPageIndex(1);
        cartDTO.setPageSize(100);
        message.setEntityObject(cartDTO);


        Call<OMSCoreMessage> call = null;
        ApiInterface apiService =
                RestService.getClient().create(ApiInterface.class);


        call = apiService.cartlist(message);
        setProgressDialog();

        ProgressDialogUtils.showProgressDialog("Please wait..");

        try {
            //Getting response from the method
            call.enqueue(new Callback<OMSCoreMessage>() {

                @Override
                public void onResponse(Call<OMSCoreMessage> call, Response<OMSCoreMessage> response) {

                    if (response.body() != null) {

                        core = response.body();

                        if (core != null) {

                            if ((core.getType().toString().equals("Exception"))) {

                                OMSExceptionMessage omsExceptionMessage = null;

                                for (OMSExceptionMessage oms : core.getOMSMessages()) {

                                    omsExceptionMessage = oms;
                                    ProgressDialogUtils.closeProgressDialog();
                                    common.showAlertType(omsExceptionMessage, getActivity(), getContext());
                                }
                                dialog.dismiss();
                                ProgressDialogUtils.closeProgressDialog();
                            } else {

                                ProgressDialogUtils.closeProgressDialog();



                                CartResponseListDTO cart = new CartResponseListDTO();
                                // get JSONObject from JSON file
                                JSONObject obj = null;
                                JSONArray cartListResp = null;

                                try {
                                    obj = new JSONObject((String) core.getEntityObject());


                                    JSONArray getCartHeader = new JSONArray(obj.getString("Table"));

                                    cartListResp = new JSONArray(obj.getString("Table1"));

                                    if (!getCartHeader.get(0).toString().contains("null")) {
                                        TypeToken<CartResponseListDTO> header = new TypeToken<CartResponseListDTO>() {
                                        };

                                        CartResponseListDTO headerInsert = new CartResponseListDTO();

                                        headerInsert = new Gson().fromJson(getCartHeader.get(0).toString(), header.getType());
                                        db.cartHeaderDAO().deleteAll();
                                        db.cartHeaderDAO().insert(new CartHeader(userId,Integer.parseInt(headerInsert.getCartHeaderID()),
                                                headerInsert.getInActive(),headerInsert.getCreditLimit(),headerInsert.getIsApproved()));

                                        TypeToken<CartResponseListDTO> token = new TypeToken<CartResponseListDTO>() {
                                        };

                                        for (int i = 0; i < cartListResp.length(); i++) {
                                            cart = new Gson().fromJson(cartListResp.get(0).toString(), token.getType());
                                            db.cartDetailsDAO().insert(new CartDetails(cart.getMaterialMasterID(), cart.getMCode(), cart.getMDescription(),
                                                    cart.getExpectedDeliveryDate(), cart.getActualDeliveryDate(),
                                                    cart.getQuantity(), cart.getFileNames(), cart.getPrice(),
                                                    cart.getCartHeaderID(),cart.getCartDetailsID(),cart.getCustomerID()));
                                        }

                                    }

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                dialog.dismiss();

                                Bundle bundle = new Bundle();
                                bundle.putBoolean(KeyValues.IS_ITEM_ADDED_TO_CART, true);
                                CartFragment cartFragment = new CartFragment();
                                cartFragment.setArguments(bundle);
                                FragmentUtils.replaceFragmentWithBackStack(getActivity(), R.id.container, cartFragment);


                            }

                        }

                    }

                }

                // response object fails
                @Override
                public void onFailure(Call<OMSCoreMessage> call, Throwable throwable) {
                    ProgressDialogUtils.closeProgressDialog();
                }
            });
        } catch (Exception ex) {

            try {
                ExceptionLoggerUtils.createExceptionLog(ex.toString(), classCode, "001", getContext());

            } catch (IOException e) {
                e.printStackTrace();
            }
            ProgressDialogUtils.closeProgressDialog();
            DialogUtils.showAlertDialog(getActivity(), errorMessages.EMC_0003);
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void showAvailableOffers(int pos) {
        final Dialog mDialog = new Dialog(getContext());
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialog.setCancelable(true);
        mDialog.setContentView(R.layout.dialog_offer);
        RecyclerView lstoffers = (RecyclerView) mDialog.findViewById(R.id.lstoffers);
        NestedScrollView nested = (NestedScrollView) mDialog.findViewById(R.id.nested);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        lstoffers.setLayoutManager(linearLayoutManager);
        lstoffers.setHasFixedSize(true);

        ArrayList<String> scAndD = new ArrayList<>();

        scAndD.add(items.get(pos).discountDesc);

        OffersAdapter adapter = new OffersAdapter(getContext(), scAndD, nested);
        lstoffers.setAdapter(adapter);
        lstoffers.setNestedScrollingEnabled(false);


        mDialog.show();


    }

    public void navigateToItemDetails(int pos) {

        Bundle bundle = new Bundle();
        bundle.putString(KeyValues.MODEL_ID, items.get(pos).modelID);
        ProductDetailsFragment productDetailsFragment = new ProductDetailsFragment();
        productDetailsFragment.setArguments(bundle);
        FragmentUtils.replaceFragmentWithBackStack(getActivity(), R.id.container, productDetailsFragment);

    }


    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        selected = spinnerColorSelectionBottom.getSelectedItem().toString();

        if (!selected.equals("")) {

            for (int k = 0; i <= variantTables.size(); k++) {

                if (variantTables.get(k).modelColor.equals(selected)) {
                    setDataListItems(variantTables.get(k));
                    selectedVariant = variantTables.get(k);
                    updateUI(variantTables.get(k));
                    return;
                }
            }

        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    private void setDataListItems(VariantTable variantTable) {


        if (variantTable.materialImgPath.contains("|")) {

            for (int i = 0; i < variantTable.materialImgPath.split("[|]").length; i++) {
                selectedVariantImage = variantTable.materialImgPath.split("[|]")[0];

            }
        } else {
            selectedVariantImage = variantTable.materialImgPath;

        }

    }

    public void updateUI(VariantTable variantTable) {
        Picasso.with(getContext())
                .load(selectedVariantImage)
                .placeholder(R.drawable.no_img)
                .into(ivItemBottom);

        UserDivisionCustTable division;
        userDivisionCustTable = db.userDivisionCustDAO().getPartner(variantTable.divisionID);
        if(userDivisionCustTable != null){
            partnerId = userDivisionCustTable.customerId;
            division = db.userDivisionCustDAO().getDivision(userId, variantTable.divisionID);
        }else {
            division = null;
        }



        if (division != null && !division.equals("")) {

            isCustomerMatched = true;

        } else {
            isCustomerMatched = false;
        }

        materialId = variantTable.materialID;

        getPrice();
    }

    @Override
    public void onResume() {
        super.onResume();

        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(getString(R.string.toolbar_ItemList));
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayUseLogoEnabled(false);

    }


    @Override
    public void onPause() {
        super.onPause();
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.llSort:
                if (isDesc) {
                    isDesc = false;
                    ivSort.setRotation(ivSort.getRotation() + 180);
                    updateToRecyclerView(db.itemDAO().getAllItems(pagecount));
                } else {
                    // changed here
                    isDesc = true;
                    ivSort.setRotation(ivSort.getRotation() + 180);
                    updateToRecyclerView(db.itemDAO().getDescendingList());
                }


                break;

            case R.id.txtViewType:
                pagecount = 0;

                if (isgrid) {
                    isgrid = false;

                    ivViewType.setImageResource(R.drawable.grid);
                    layoutManager = new LinearLayoutManager(getContext());
                    recyclerView.setLayoutManager(layoutManager);

                } else {

                    // changed here
                    isgrid = true;
                    ivViewType.setImageResource(R.drawable.listview);
                    layoutManager = new GridLayoutManager(getContext(), 2);
                    recyclerView.setLayoutManager(layoutManager);

                }

                if(isDesc){
                    updateToRecyclerView(db.itemDAO().getDescendingList());
                }else {
                    updateToRecyclerView(db.itemDAO().getAllItems(pagecount));
                }


                break;
        }
    }

    public void setProgressDialog() {

        int llPadding = 30;
        LinearLayout ll = new LinearLayout(getContext());
        ll.setOrientation(LinearLayout.HORIZONTAL);
        ll.setPadding(llPadding, llPadding, llPadding, llPadding);
        ll.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams llParam = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        llParam.gravity = Gravity.CENTER;
        ll.setLayoutParams(llParam);

        ProgressBar progressBar = new ProgressBar(getContext());
        progressBar.setIndeterminate(true);
        progressBar.setPadding(0, 0, llPadding, 0);
        progressBar.setLayoutParams(llParam);

        llParam = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        llParam.gravity = Gravity.CENTER;
        TextView tvText = new TextView(getContext());
        tvText.setText("Please wait..");
        tvText.setTextColor(Color.parseColor("#000000"));
        tvText.setTextSize(20);
        tvText.setLayoutParams(llParam);

        ll.addView(progressBar);
        ll.addView(tvText);

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setCancelable(true);
        builder.setView(ll);

        dialog = builder.create();
        dialog.show();
        Window window = dialog.getWindow();
        if (window != null) {
            WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
            layoutParams.copyFrom(dialog.getWindow().getAttributes());
            layoutParams.width = LinearLayout.LayoutParams.WRAP_CONTENT;
            layoutParams.height = LinearLayout.LayoutParams.WRAP_CONTENT;
            dialog.getWindow().setAttributes(layoutParams);
        }
    }
}
