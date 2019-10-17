package com.example.inventrax.falconOMS.fragments;

/*
 * Created by Padmaja on 04/07/2019.
 */

import android.annotation.TargetApi;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DownloadManager;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.inventrax.falconOMS.R;
import com.example.inventrax.falconOMS.activities.MainActivity;
import com.example.inventrax.falconOMS.adapters.OffersAdapter;
import com.example.inventrax.falconOMS.adapters.SlideImageAdapter;
import com.example.inventrax.falconOMS.common.Common;
import com.example.inventrax.falconOMS.common.constants.EndpointConstants;
import com.example.inventrax.falconOMS.common.constants.ErrorMessages;
import com.example.inventrax.falconOMS.interfaces.ApiInterface;
import com.example.inventrax.falconOMS.model.ImageModel;
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

public class ProductDetailsFragment extends Fragment implements View.OnClickListener {
    private static final String classCode = "OMS_Android_ItemDetailsFragment";
    private View rootView;

    private EditText deliveryDatePicker, etQty;
    private TextView txtItemName, txtDiscount, txtPrice, txtPriceCut, txtShortDesc,
            txtAvailableCreditLimit, txtDescreption, txtDownloadSepcs, txtDownloadCatalog, txtDownloadBrochure, txtPackageContents, txtAddtoCart;
    private ImageView availOffer, ivItem;
    private SearchableSpinner spinnerVariant;

    private ArrayList<ImageModel> mImageList;
    private CoordinatorLayout coordinatorLayout;

    private static ViewPager mPager;
    ArrayList<String> varient;

    private String selectedVar = "";
    String myFormat = "dd/MMM/yyyy";

    private String modelId = "";
    AppDatabase db;
    List<VariantTable> variantTables = null;
    List<CartDTO> cartList = null;

    VariantTable selectedVariant = null;
    String selectedVariantImage = "", partnerId = "", userId = "", cusomerIDs = "", materialId = "";
    UserDivisionCustTable userDivisionCustTable;
    private String specsUrl = "", catalogUrl = "", brochureUrl = "", price = "",conditionType = "";
    private boolean isCustomerMatched = false;

    AlertDialog dialog;

    private ErrorMessages errorMessages;
    private Gson gson;
    private OMSCoreMessage core;
    private Common common;
    RestService restService;
    private CustomerTable custTable;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.product_details_fragment, container, false);

        loadFormControls();

        return rootView;
    }

    private void loadFormControls() {

        // To disable Bottom navigation bar
        ((MainActivity) getActivity()).SetNavigationVisibiltity(false);

        if (getArguments() == null) {
            return;
        }
        coordinatorLayout = (CoordinatorLayout) rootView.findViewById(R.id.coordinatorLayout);
        modelId = getArguments().getString(KeyValues.MODEL_ID);
        varient = new ArrayList<>();

        db = Room.databaseBuilder(getActivity(),
                AppDatabase.class, "room_oms").allowMainThreadQueries().build();

        SharedPreferences sp = getContext().getSharedPreferences(KeyValues.MY_PREFS, Context.MODE_PRIVATE);
        userId = sp.getString(KeyValues.USER_ID, "");
        cusomerIDs = sp.getString(KeyValues.CUSTOMER_IDS, "");

        deliveryDatePicker = (EditText) rootView.findViewById(R.id.deliveryDatePicker);
        etQty = (EditText) rootView.findViewById(R.id.etQty);

        ivItem = (ImageView) rootView.findViewById(R.id.ivItem);
        availOffer = (ImageView) rootView.findViewById(R.id.availOffer);

        txtItemName = (TextView) rootView.findViewById(R.id.txtItemName);
        txtDiscount = (TextView) rootView.findViewById(R.id.txtDiscount);
        txtPrice = (TextView) rootView.findViewById(R.id.txtPrice);
        txtShortDesc = (TextView) rootView.findViewById(R.id.txtShortDesc);
        txtPriceCut = (TextView) rootView.findViewById(R.id.txtPriceCut);
        txtAvailableCreditLimit = (TextView) rootView.findViewById(R.id.txtAvailableCreditLimit);
        txtDescreption = (TextView) rootView.findViewById(R.id.txtDescreption);
        txtDownloadBrochure = (TextView) rootView.findViewById(R.id.txtDownloadBrochure);
        txtDownloadCatalog = (TextView) rootView.findViewById(R.id.txtDownloadCatalog);
        txtDownloadSepcs = (TextView) rootView.findViewById(R.id.txtDownloadSepcs);
        txtPackageContents = (TextView) rootView.findViewById(R.id.txtPackageContents);
        txtAddtoCart = (TextView) rootView.findViewById(R.id.txtAddtoCart);

        spinnerVariant = (SearchableSpinner) rootView.findViewById(R.id.spinnerVariant);

        txtAddtoCart.setOnClickListener(this);
        ivItem.setOnClickListener(this);
        availOffer.setOnClickListener(this);
        txtDownloadBrochure.setOnClickListener(this);
        txtDownloadCatalog.setOnClickListener(this);
        txtDownloadSepcs.setOnClickListener(this);


        // Date Picker Dailog integration and validation
        final Calendar myCalendar = Calendar.getInstance();
        final DatePickerDialog.OnDateSetListener datePickerListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                //In which you need put here
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


        // To disable Bottom navigation bar
        //((MainActivity) getActivity()).SetNavigationVisibiltity(false);


        variantTables = db.variantDAO().getVariants(modelId);

        for (VariantTable var : variantTables) {

            varient.add(var.modelColor);

        }

        ArrayAdapter adapter = new ArrayAdapter(getContext(), R.layout.support_simple_spinner_dropdown_item, varient);
        spinnerVariant.setAdapter(adapter);


        spinnerVariant.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedVar = spinnerVariant.getSelectedItem().toString();

                if (!selectedVar.equals("")) {

                    for (int k = 0; i <= variantTables.size(); k++) {

                        if (variantTables.get(k).modelColor.equals(selectedVar)) {
                            updateUI(variantTables.get(k));
                            setDataListItems(variantTables.get(k));
                            selectedVariant = variantTables.get(k);
                            Picasso.with(getContext())
                                    .load(selectedVariantImage)
                                    .placeholder(R.drawable.no_img)
                                    .into(ivItem);


                            return;
                        }
                    }

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        errorMessages = new ErrorMessages();
        common = new Common();
        restService = new RestService();
        core = new OMSCoreMessage();
        cartList = new ArrayList<>();

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

    public void updateUI(VariantTable variantTable) {

        txtItemName.setText(variantTable.mCode);
        txtShortDesc.setText(variantTable.mDescription);
        txtDescreption.setText(variantTable.mDescriptionLong);
        txtPrice.setText(variantTable.price);
        materialId = variantTable.materialID;
        UserDivisionCustTable division;
        userDivisionCustTable = db.userDivisionCustDAO().getPartner(variantTable.divisionID);
        if (userDivisionCustTable != null) {
            partnerId = userDivisionCustTable.customerId;
            division = db.userDivisionCustDAO().getDivision(userId, variantTable.divisionID);
        } else {
            division = null;
        }


        if (division != null && !division.equals("")) {

            isCustomerMatched = true;

        } else {
            isCustomerMatched = false;
        }

        specsUrl = variantTable.specsUrl;
        catalogUrl = variantTable.catalogUrl;
        brochureUrl = variantTable.brochureUrl;

        if (specsUrl.equals("")) {
            txtDownloadSepcs.setVisibility(View.GONE);
        } else {
            txtDownloadSepcs.setVisibility(View.VISIBLE);
        }

        if (catalogUrl.equals("")) {
            txtDownloadCatalog.setVisibility(View.GONE);
        } else {
            txtDownloadCatalog.setVisibility(View.VISIBLE);
        }

        if (brochureUrl.equals("")) {
            txtDownloadBrochure.setVisibility(View.GONE);
        } else {
            txtDownloadBrochure.setVisibility(View.VISIBLE);
        }

        getPrice();
    }


    public void getPrice() {

        if (!NetworkUtils.isInternetAvailable(getContext())) {
            return;
        }

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
                                    price = jObject.getString("Amount");
                                    conditionType = jObject.getString("Conditiontype");
                                    txtPrice.setText("Rs." + price);

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

    public void addToCart() {

        if (!NetworkUtils.isInternetAvailable(getContext())) {
            return;
        }

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
        ProgressDialogUtils.showProgressDialog("Please Wait");


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

                                syncCart();

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
                                dialog.dismiss();
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



    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.txtAddtoCart:

                if (!etQty.getText().toString().isEmpty()) {

                    if (!etQty.getText().toString().equalsIgnoreCase("0")) {

                        if (selectedVariant != null) {


                            if (NetworkUtils.isInternetAvailable(getContext())) {

                                CartDTO cartDTO = new CartDTO();
                                cartDTO.setMaterialMasterID(selectedVariant.materialID);
                                cartDTO.setMCode(selectedVariant.mCode);
                                cartDTO.setQuantity(etQty.getText().toString());
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
                                        etQty.getText().toString(), selectedVariantImage, price,"","",partnerId));
                            }


                            if (isCustomerMatched) {

                                if (NetworkUtils.isInternetAvailable(getContext())) {
                                    if (!price.equals("")) {
                                        addToCart();
                                    } else {
                                        SnackbarUtils.showSnackbarLengthShort(coordinatorLayout, getString(R.string.PriceNotAvailable), ContextCompat.getColor(getActivity(), R.color.colorAccent), Snackbar.LENGTH_SHORT);
                                        return;
                                    }
                                } else {
                                    // Alert user that no internet is available and price may change after placing order

                                    Bundle bundle = new Bundle();
                                    bundle.putBoolean(KeyValues.IS_ITEM_ADDED_TO_CART, true);
                                    CartFragment cartFragment = new CartFragment();
                                    cartFragment.setArguments(bundle);
                                    FragmentUtils.replaceFragmentWithBackStack(getActivity(), R.id.container, cartFragment);
                                    return;
                                }
                            } else {
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

                break;

            case R.id.ivItem:
                showImage();
                break;

            case R.id.txtDownloadBrochure:
                downLoadPDF(brochureUrl, txtItemName.getText().toString(), "Brochure");
                break;

            case R.id.txtDownloadCatalog:
                downLoadPDF(catalogUrl, txtItemName.getText().toString(), "Catalog");
                break;

            case R.id.txtDownloadSepcs:
                downLoadPDF(specsUrl, txtItemName.getText().toString(), "Specifications");
                break;

            case R.id.availOffer:

                showAvailableOffers();

                break;


        }
    }

    public void downLoadPDF(String downloadUrl, String itemName, String type) {

        if (isDownloadManagerAvailable(getContext())) {


            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(downloadUrl));
            request.setDescription("Downloading");
            request.setTitle(itemName);
            // in order for this if to run, you must use the android 3.2 to compile your app
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                request.allowScanningByMediaScanner();
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            }
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, itemName + " " + type + ".pdf");

            // get download service and enqueue file
            DownloadManager manager = (DownloadManager) getActivity().getSystemService(Context.DOWNLOAD_SERVICE);
            manager.enqueue(request);
        }
    }

    public static boolean isDownloadManagerAvailable(Context context) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            return true;
        }
        return false;
    }

    private void setDataListItems(VariantTable variantTable) {

        mImageList = new ArrayList<ImageModel>();

        if (variantTable.materialImgPath.contains("|")) {

            for (int i = 0; i < variantTable.materialImgPath.split("[|]").length; i++) {
                String res = variantTable.materialImgPath.split("[|]")[i];
                selectedVariantImage = variantTable.materialImgPath.split("[|]")[0];
                mImageList.add(new ImageModel(res));
            }
        } else {
            selectedVariantImage = variantTable.materialImgPath;
            mImageList.add(new ImageModel(variantTable.materialImgPath));
        }

    }


    private void showImage() {
        final Dialog mDialog = new Dialog(getContext());
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialog.setCancelable(true);
        mDialog.setContentView(R.layout.image_viewpager);
        CardView btnimgClose = (CardView) mDialog.findViewById(R.id.closedialog);
        TabLayout tabIndicator = (TabLayout) mDialog.findViewById(R.id.tab_indicator);

        mPager = (ViewPager) mDialog.findViewById(R.id.pager);

        mPager.setAdapter(new SlideImageAdapter(getContext(), mImageList));

        // setup tablayout with viewpager
        tabIndicator.setupWithViewPager(mPager);


        btnimgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mDialog.dismiss();
            }
        });
        mDialog.show();


    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void showAvailableOffers() {

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

        scAndD.add(selectedVariant.discountDesc);

        OffersAdapter adapter = new OffersAdapter(getContext(), scAndD, nested);
        lstoffers.setAdapter(adapter);
        lstoffers.setNestedScrollingEnabled(false);


        mDialog.show();


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

    @Override
    public void onResume() {
        super.onResume();
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(getString(R.string.toolbar_product));
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayUseLogoEnabled(false);

    }
}
