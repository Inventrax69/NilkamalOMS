package com.example.inventrax.falconOMS.fragments;

/*
 * Created by Padmaja on 04/07/2019.
 */

import android.annotation.TargetApi;
import android.app.Dialog;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.inventrax.falconOMS.R;
import com.example.inventrax.falconOMS.activities.CartActivity;
import com.example.inventrax.falconOMS.activities.LoginActivity;
import com.example.inventrax.falconOMS.activities.MainActivity;
import com.example.inventrax.falconOMS.adapters.OffersAdapter;
import com.example.inventrax.falconOMS.adapters.SlideImageAdapter;
import com.example.inventrax.falconOMS.common.Common;
import com.example.inventrax.falconOMS.common.constants.EndpointConstants;
import com.example.inventrax.falconOMS.common.constants.ErrorMessages;
import com.example.inventrax.falconOMS.interfaces.ApiInterface;
import com.example.inventrax.falconOMS.model.ImageModel;
import com.example.inventrax.falconOMS.model.KeyValues;
import com.example.inventrax.falconOMS.pojos.CartDetailsListDTO;
import com.example.inventrax.falconOMS.pojos.CartHeaderListDTO;
import com.example.inventrax.falconOMS.pojos.OMSCoreMessage;
import com.example.inventrax.falconOMS.pojos.OMSExceptionMessage;
import com.example.inventrax.falconOMS.pojos.PriceDTO;
import com.example.inventrax.falconOMS.pojos.ProductDiscountDTO;
import com.example.inventrax.falconOMS.pojos.VariantDTO;
import com.example.inventrax.falconOMS.pojos.productCatalogs;
import com.example.inventrax.falconOMS.room.AppDatabase;
import com.example.inventrax.falconOMS.room.CartDetails;
import com.example.inventrax.falconOMS.room.CartHeader;
import com.example.inventrax.falconOMS.room.CustomerTable;
import com.example.inventrax.falconOMS.room.RoomAppDatabase;
import com.example.inventrax.falconOMS.room.UserDivisionCustTable;
import com.example.inventrax.falconOMS.room.VariantTable;
import com.example.inventrax.falconOMS.services.RestService;
import com.example.inventrax.falconOMS.util.DialogUtils;
import com.example.inventrax.falconOMS.util.ExceptionLoggerUtils;
import com.example.inventrax.falconOMS.util.FragmentUtils;
import com.example.inventrax.falconOMS.util.NetworkUtils;
import com.example.inventrax.falconOMS.util.ProgressDialogUtils;
import com.example.inventrax.falconOMS.util.SharedPreferencesUtils;
import com.example.inventrax.falconOMS.util.SnackbarUtils;
import com.example.inventrax.falconOMS.util.ViewDialog;
import com.example.inventrax.falconOMS.util.searchableSpinner.SearchableSpinner;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ProductDetailsFragment extends Fragment implements View.OnClickListener {

    private static final String classCode = "OMS_Android_ItemDetailsFragment";
    private View rootView;
    private EditText etQty;
    private TextView txtItemName, txtDiscount, txtPrice, txtShortDesc,
            txtAvailableCreditLimit, txtDescreption, txtDownloadSepcs, txtDownloadCatalog, txtDownloadBrochure, txtPackageContents, txtAddtoCart;
    private ImageView availOffer, ivItem;
    private Spinner spinnerVariant;
    private ArrayList<ImageModel> mImageList;
    private CoordinatorLayout coordinatorLayout;
    private static ViewPager mPager;
    ArrayList<String> varient;
    private String selectedVar = "";
    String myFormat = "dd/MMM/yyyy";
    private String modelId = "";
    AppDatabase db;
    List<VariantTable> variantTables = null;
    List<productCatalogs> cartList = null;

    VariantTable selectedVariant = null;
    String selectedVariantImage = "", partnerId = "", userId = "", customerIDs = "", materialId = "";
    UserDivisionCustTable userDivisionCustTable;
    private String specsUrl = "", catalogUrl = "", brochureUrl = "", price = "", conditionType = "", shipToParty = "", customerId = "";
    private boolean isCustomerMatched = false, isFromSearchResult = false;
    private int materialDivisionId;

    AlertDialog dialog;

    private ErrorMessages errorMessages;
    private Gson gson;
    private OMSCoreMessage core;
    private Common common;
    RestService restService;
    private CustomerTable custTable;
    SharedPreferencesUtils sharedPreferencesUtils;
    TextView txtTimer;
    CheckBox cbPriority;

    IntentFilter intentFilter = null;
    DownloadManager dm = null;
    long downloadID;

    SharedPreferences sp;
    ViewDialog viewDialog;
    String sMaterailId = "", sPartnerId = "";

    List<String> mCodeName;
    List<String> discountDesc;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.product_details_fragment, container, false);

        try {
            loadFormControls();
        } catch (Exception e) {

        }


        return rootView;
    }

    private void loadFormControls() {

        // To disable Bottom navigation bar
        ((MainActivity) getActivity()).SetNavigationVisibility(false);
        sp = getActivity().getSharedPreferences(KeyValues.MY_PREFS, Context.MODE_PRIVATE);
        if (getArguments() == null) {
            return;
        }
        coordinatorLayout = (CoordinatorLayout) rootView.findViewById(R.id.coordinatorLayout);
        modelId = getArguments().getString(KeyValues.MODEL_ID);

        // result from Intelli-Search
        materialDivisionId = getArguments().getInt(KeyValues.MATERIAL_DIVISION_ID);


        varient = new ArrayList<>();

        db = new RoomAppDatabase(getActivity()).getAppDatabase();

        // Handling Product Catalog result when non DTD/ChannelPartner case
        if (!sp.getString(KeyValues.USER_ROLE_NAME, "").equalsIgnoreCase(KeyValues.USER_ROLE_NAME_DTD)) {
            if (getArguments().getString(KeyValues.CUSTOMER_ID) != null) {
                if (!getArguments().getString(KeyValues.CUSTOMER_ID).isEmpty() || getArguments().getString(KeyValues.CUSTOMER_ID) != null)
                    customerId = getArguments().getString(KeyValues.CUSTOMER_ID);
            }
        }

        SharedPreferences sp = getContext().getSharedPreferences(KeyValues.MY_PREFS, Context.MODE_PRIVATE);
        userId = sp.getString(KeyValues.USER_ID, "");
        customerIDs = sp.getString(KeyValues.CUSTOMER_IDS, "");

        etQty = (EditText) rootView.findViewById(R.id.etQty);

        ivItem = (ImageView) rootView.findViewById(R.id.ivItem);
        availOffer = (ImageView) rootView.findViewById(R.id.availOffer);

        txtItemName = (TextView) rootView.findViewById(R.id.txtItemName);
        txtDiscount = (TextView) rootView.findViewById(R.id.txtDiscount);
        txtPrice = (TextView) rootView.findViewById(R.id.txtPrice);
        txtShortDesc = (TextView) rootView.findViewById(R.id.txtShortDesc);
        txtAvailableCreditLimit = (TextView) rootView.findViewById(R.id.txtAvailableCreditLimit);
        txtDescreption = (TextView) rootView.findViewById(R.id.txtDescreption);
        txtDownloadBrochure = (TextView) rootView.findViewById(R.id.txtDownloadBrochure);
        txtDownloadCatalog = (TextView) rootView.findViewById(R.id.txtDownloadCatalog);
        txtDownloadSepcs = (TextView) rootView.findViewById(R.id.txtDownloadSepcs);
        txtPackageContents = (TextView) rootView.findViewById(R.id.txtPackageContents);
        txtAddtoCart = (TextView) rootView.findViewById(R.id.txtAddtoCart);
        txtTimer = (TextView) getActivity().findViewById(R.id.txtTimer);

        spinnerVariant = (Spinner) rootView.findViewById(R.id.spinnerVariant);

        cbPriority = (CheckBox) rootView.findViewById(R.id.cbPriority);

        txtAddtoCart.setOnClickListener(this);
        ivItem.setOnClickListener(this);
        availOffer.setOnClickListener(this);
        txtDownloadBrochure.setOnClickListener(this);
        txtDownloadCatalog.setOnClickListener(this);
        txtDownloadSepcs.setOnClickListener(this);


        // To disable Bottom navigation bar
        // ((MainActivity) getActivity()).SetNavigationVisibility(false);

        variantTables = db.variantDAO().getVariants(modelId);

        for (VariantTable var : variantTables) {
            varient.add(var.modelColor);
        }

        ArrayAdapter adapter = new ArrayAdapter(getContext(), R.layout.support_simple_spinner_dropdown_item, varient);
        spinnerVariant.setAdapter(adapter);

        viewDialog = new ViewDialog(getActivity());

        spinnerVariant.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                if (isFromSearchResult) {

                    // selected item from intelli-search

                    ArrayAdapter myAdap = (ArrayAdapter) spinnerVariant.getAdapter(); //cast to an ArrayAdapter

                    int spinnerPosition = myAdap.getPosition(selectedVar);

                    //set the default according to value
                    spinnerVariant.setSelection(spinnerPosition);


                } else {
                    // change result of variant spinner
                    selectedVar = spinnerVariant.getSelectedItem().toString();
                }

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

        mCodeName = new ArrayList<>();
        discountDesc = new ArrayList<>();


        // To handle intelli - search result material
        if ((getArguments().getInt(KeyValues.MID)) != 0) {

            int materialId = getArguments().getInt(KeyValues.MID);

            VariantTable variantTable = db.variantDAO().getMaterial(materialId);

            selectedVar = variantTable.modelColor;

            isFromSearchResult = true;

        }

        // Handling Intelli-Search result when non DTD/ChannelPartner case
        if (!sp.getString(KeyValues.USER_ROLE_NAME, "").equalsIgnoreCase(KeyValues.USER_ROLE_NAME_DTD)) {
            if (materialDivisionId != 0) {
                showShipToPartyDialog();
            }
        }

    }

    public void updateUI(VariantTable variantTable) {

        // offers tag visible only when internet is available or discount id not equals to 0
        if (NetworkUtils.isInternetAvailable(getActivity())) {

            if (!variantTable.discountId.equalsIgnoreCase("0")) {
                availOffer.setVisibility(View.VISIBLE);
            } else {
                availOffer.setVisibility(View.GONE);
            }

        } else {
            availOffer.setVisibility(View.GONE);
        }

        txtItemName.setText(variantTable.mCode);
        txtShortDesc.setText(variantTable.mDescription);
        txtDescreption.setText(variantTable.mDescriptionLong);
        etQty.setText(String.valueOf(variantTable.stackSize));
        txtPrice.setText(variantTable.price);
        materialId = variantTable.materialID;
        UserDivisionCustTable division;

        // getting customer id based on selected variant divison
        userDivisionCustTable = db.userDivisionCustDAO().getPartner(variantTable.divisionID);
        if (userDivisionCustTable != null) {
            partnerId = userDivisionCustTable.customerId;
            division = db.userDivisionCustDAO().getDivision(variantTable.divisionID);
        } else {
            division = null;
        }

       /* customer division and material division should match to proceed to add to cart in case of DTD/ChannelPartner
        in case of non DTD/ChannelPartner case, selected customer division id and material division should match*/

        if (division != null && !division.equals("")) {
            isCustomerMatched = true;
        } else {
            isCustomerMatched = false;
        }

        specsUrl = variantTable.specsUrl;
        catalogUrl = variantTable.catalogUrl;
        brochureUrl = variantTable.brochureUrl;

        if (NetworkUtils.isInternetAvailable(getActivity())) {


            if (!isFromSearchResult) {
                productDiscount();
                getPrice();
            }

        }

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

        if (isFromSearchResult) {
            isFromSearchResult = false;
        }

        cbPriority.setChecked(false);
        String isPriority = db.cartHeaderDetailsDao().getIsPriority(variantTable.materialID);
        if (isPriority.equals("1")) {
            cbPriority.setEnabled(false);
            cbPriority.setChecked(true);
        } else {
            cbPriority.setEnabled(true);
            cbPriority.setChecked(false);
        }


    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        if (menu != null) {

            final MenuItem item = menu.findItem(R.id.action_home);
            item.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
            item.setVisible(true);

            final MenuItem item1 = menu.findItem(R.id.cust_action_search);
            item1.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
            item1.setVisible(false);

            final MenuItem customerSelection = menu.findItem(R.id.action_change);
            customerSelection.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
            customerSelection.setVisible(false);

            final MenuItem settings = menu.findItem(R.id.action_setting);
            settings.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
            settings.setVisible(false);

        }

        super.onCreateOptionsMenu(menu, inflater);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case R.id.action_logout: {
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);
            }
            break;

            case R.id.action_profile: {
                FragmentUtils.replaceFragmentWithBackStack(getActivity(), getActivity().findViewById(R.id.container).getId(), new ProfileFragment());
            }
            break;

            case R.id.action_home: {
                FragmentUtils.replaceFragmentWithBackStack(getActivity(), getActivity().findViewById(R.id.container).getId(), new HomeFragmentHints());
            }
            break;

        }

        return super.onOptionsItemSelected(item);
    }


    public void getPrice() {

        OMSCoreMessage message = new OMSCoreMessage();
        message = common.SetAuthentication(EndpointConstants.ProductCatalog_FPS_DTO, getActivity());
        PriceDTO oDto = new PriceDTO();
        oDto.setMaterialMasterID(materialId);
        if (customerId.isEmpty() || customerId.equals(""))
            oDto.setPartnerID("0");
        else
            oDto.setPartnerID(customerId);

        message.setEntityObject(oDto);

        Call<OMSCoreMessage> call = null;
        ApiInterface apiService = RestService.getClient().create(ApiInterface.class);

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

                        if ((core.getType().toString().equals("Exception"))) {

                            OMSExceptionMessage omsExceptionMessage = null;

                            for (OMSExceptionMessage oms : core.getOMSMessages()) {

                                omsExceptionMessage = oms;
                                ProgressDialogUtils.closeProgressDialog();
                                common.showAlertType(omsExceptionMessage, getActivity(), getActivity());
                            }


                        } else {

                            JSONObject jObject;
                            try {

                                jObject = new JSONObject(new Gson().toJson(core.getEntityObject()));
                                txtPrice.setText("");
                                price = jObject.getString("Amount");
                                txtPrice.setText("Rs." + price);

                            } catch (Exception e) {
                                SnackbarUtils.showSnackbarLengthShort(coordinatorLayout, "Error while getting price", ContextCompat.getColor(getActivity(), R.color.dark_red), Snackbar.LENGTH_SHORT);
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
                    DialogUtils.showAlertDialog(getActivity(), errorMessages.EMC_0002);
                    ProgressDialogUtils.closeProgressDialog();
                }
            });
        } catch (Exception ex) {

            try {
                ExceptionLoggerUtils.createExceptionLog(ex.toString(), classCode, "001", getActivity());

            } catch (IOException e) {
                e.printStackTrace();
            }
            ProgressDialogUtils.closeProgressDialog();
            DialogUtils.showAlertDialog(getActivity(), errorMessages.EMC_0003);
        }
    }

    public void getVariants() {

        OMSCoreMessage message = new OMSCoreMessage();
        message = common.SetAuthentication(EndpointConstants.ProductCatalog_FPS_DTO, getActivity());
        PriceDTO oDto = new PriceDTO();
        oDto.setMaterialMasterID(materialId);
        oDto.setModelID(modelId);
        oDto.setCustomerID(customerId);
        oDto.setResults("");

        message.setEntityObject(oDto);

        Call<OMSCoreMessage> call = null;
        ApiInterface apiService = RestService.getClient().create(ApiInterface.class);

        call = apiService.Varient(message);
        ProgressDialogUtils.showProgressDialog("Please Wait");

        try {
            //Getting response from the method
            call.enqueue(new Callback<OMSCoreMessage>() {

                @Override
                public void onResponse(Call<OMSCoreMessage> call, Response<OMSCoreMessage> response) {
                    ProgressDialogUtils.closeProgressDialog();
                    if (response.body() != null) {

                        core = response.body();

                        if ((core.getType().toString().equals("Exception"))) {

                            OMSExceptionMessage omsExceptionMessage = null;

                            for (OMSExceptionMessage oms : core.getOMSMessages()) {

                                omsExceptionMessage = oms;
                                ProgressDialogUtils.closeProgressDialog();
                                common.showAlertType(omsExceptionMessage, getActivity(), getActivity());
                            }


                        } else {

                            List<VariantDTO> variants = new ArrayList<>();
                            List<VariantTable> variantsTable = new ArrayList<>();


                            if (core.getEntityObject() != null) {
                                try {

                                    JSONArray getApprovalListDTO = new JSONArray((ArrayList) core.getEntityObject());
                                    if (getApprovalListDTO.length() > 0) {
                                        VariantDTO variantDTO = new VariantDTO();
                                        VariantTable variantTable = new VariantTable();
                                        for (int i = 0; i < getApprovalListDTO.length(); i++) {
                                            variantDTO = new Gson().fromJson(getApprovalListDTO.get(i).toString(), VariantDTO.class);

                                            db.variantDAO().updateDiscountbyID(variantDTO.getDiscountCount(), variantDTO.getDiscountId(), variantDTO.getDiscountDesc(), variantDTO.getMaterialID());

                                            selectedVariant.discountDesc = variantDTO.getDiscountDesc();
                                            selectedVariant.discountId = variantDTO.getDiscountId();
                                            selectedVariant.discountCount = variantDTO.getDiscountCount();

                                            //productDiscount();

                                        }
                                    }

                                } catch (Exception e) {

                                }

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
                    DialogUtils.showAlertDialog(getActivity(), errorMessages.EMC_0002);
                    ProgressDialogUtils.closeProgressDialog();
                }
            });
        } catch (Exception ex) {

            try {
                ExceptionLoggerUtils.createExceptionLog(ex.toString(), classCode, "001", getActivity());

            } catch (IOException e) {
                e.printStackTrace();
            }
            ProgressDialogUtils.closeProgressDialog();
            DialogUtils.showAlertDialog(getActivity(), errorMessages.EMC_0003);
        }
    }

    public void productDiscount() {

        OMSCoreMessage message = new OMSCoreMessage();
        message = common.SetAuthentication(EndpointConstants.ProductCatalog_FPS_DTO, getActivity());

        ProductDiscountDTO dto = new ProductDiscountDTO();

        if (!customerId.equals("")) {
            dto.setCustomerID(customerId);
        } else {
            dto.setCustomerID("0");
        }

        dto.setMaterialMasterID(materialId);
        dto.setModelID("0");

        message.setEntityObject(dto);

        Call<OMSCoreMessage> call = null;
        ApiInterface apiService =
                RestService.getClient().create(ApiInterface.class);

        ProgressDialogUtils.showProgressDialog("Please wait..");

        call = apiService.ProductDiscount(message);

        try {
            //Getting response from the method
            call.enqueue(new Callback<OMSCoreMessage>() {

                @Override
                public void onResponse(Call<OMSCoreMessage> call, Response<OMSCoreMessage> response) {

                    if (response.body() != null) {

                        core = response.body();

                        if ((core.getType().toString().equals("Exception"))) {

                            OMSExceptionMessage omsExceptionMessage = null;

                            for (OMSExceptionMessage oms : core.getOMSMessages()) {
                                omsExceptionMessage = oms;
                                ProgressDialogUtils.closeProgressDialog();
                                common.showAlertType(omsExceptionMessage, getActivity(), getActivity());
                            }
                            availOffer.setVisibility(View.GONE);
                            ProgressDialogUtils.closeProgressDialog();


                        } else {

                            try {

                                mCodeName = new ArrayList<>();
                                discountDesc = new ArrayList<>();

                                JSONArray getCartHeader = new JSONArray((String) core.getEntityObject());
                                for (int i = 0; i < getCartHeader.length(); i++) {
                                    mCodeName.add(getCartHeader.getJSONObject(i).getString("MCode"));
                                    discountDesc.add(getCartHeader.getJSONObject(i).getString("Remarks"));

                                }
                                ProgressDialogUtils.closeProgressDialog();

                                if (mCodeName.size() > 0) {
                                    availOffer.setVisibility(View.VISIBLE);
                                } else {
                                    availOffer.setVisibility(View.GONE);
                                }


                            } catch (Exception e) {
                                ProgressDialogUtils.closeProgressDialog();

                            }
                        }
                    }
                }

                // response object fails
                @Override
                public void onFailure(Call<OMSCoreMessage> call, Throwable throwable) {
                    if (NetworkUtils.isInternetAvailable(getActivity())) {
                        DialogUtils.showAlertDialog(getActivity(), errorMessages.EMC_0001);
                    } else {
                        DialogUtils.showAlertDialog(getActivity(), errorMessages.EMC_0014);
                    }
                    ProgressDialogUtils.closeProgressDialog();
                }
            });
        } catch (Exception ex) {
            try {
                ExceptionLoggerUtils.createExceptionLog(ex.toString(), classCode, "001", getActivity());
            } catch (IOException e) {
                e.printStackTrace();
            }
            ProgressDialogUtils.closeProgressDialog();
            DialogUtils.showAlertDialog(getActivity(), errorMessages.EMC_0003);
        }
    }

    public void addToCart() {

        viewDialog.showDialog();

        OMSCoreMessage message = new OMSCoreMessage();
        message = common.SetAuthentication(EndpointConstants.HHTCartDTO, getActivity());
        productCatalogs oDto = new productCatalogs();
        oDto.setUserID(userId);
        oDto.setProductCatalogs(cartList);
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

                        if ((core.getType().toString().equals("Exception"))) {

                            OMSExceptionMessage omsExceptionMessage = null;

                            for (OMSExceptionMessage oms : core.getOMSMessages()) {

                                omsExceptionMessage = oms;
                                ProgressDialogUtils.closeProgressDialog();
                                common.showAlertType(omsExceptionMessage, getActivity(), getActivity());

                            }
                            ProgressDialogUtils.closeProgressDialog();

                        } else {

                            try {

                                List<CartHeader> cartHeadersList = db.cartHeaderDAO().getCartHeadersForSTP();

                                etQty.setText(String.valueOf(selectedVariant.stackSize));
                                db.cartDetailsDAO().deleteAll();
                                db.cartHeaderDAO().deleteAll();

                                ProgressDialogUtils.closeProgressDialog();

                                try {

                                    // JSONObject getObjectCartHeader = new JSONObject((String) core.getEntityObject());
                                    // JSONObject getObjectCartHeader = new JSONObject(new Gson().toJson(core.getEntityObject()));
                                    if (core.getEntityObject() != null) {
                                        JSONArray getCartHeader = new JSONArray((String) core.getEntityObject());

                                        CartHeaderListDTO cartHeaderListDTO;
                                        CartDetailsListDTO cart;

                                        for (int i = 0; i < getCartHeader.length(); i++) {

                                            for (int j = 0; j < getCartHeader.getJSONObject(i).getJSONArray("CartHeader").length(); j++) {
                                                cartHeaderListDTO = new Gson().fromJson(getCartHeader.getJSONObject(i).getJSONArray("CartHeader").getJSONObject(j).toString(), CartHeaderListDTO.class);

                                                if (cartHeaderListDTO.getListCartDetailsList().size() > 0)
                                                    db.cartHeaderDAO().insert(new CartHeader(cartHeaderListDTO.getCustomerID(), cartHeaderListDTO.getCustomerName(), cartHeaderListDTO.getCreditLimit(), cartHeaderListDTO.getCartHeaderID(),
                                                            cartHeaderListDTO.getIsInActive(), cartHeaderListDTO.getIsCreditLimit(), cartHeaderListDTO.getIsApproved(), 0, cartHeaderListDTO.getCreatedOn(),
                                                            cartHeaderListDTO.getTotalPrice(), cartHeaderListDTO.getTotalPriceWithTax()));

                                                db.cartHeaderDAO().updateIsUpdated(cartHeaderListDTO.getCustomerID(), 0);
                                                for (int k = 0; k < cartHeaderListDTO.getListCartDetailsList().size(); k++) {
                                                    cart = cartHeaderListDTO.getListCartDetailsList().get(k);

                                                    db.cartDetailsDAO().insert(new CartDetails(String.valueOf(cartHeaderListDTO.getCartHeaderID()), cart.getMaterialMasterID(),
                                                            cart.getMCode(), cart.getMDescription(), cart.getActualDeliveryDate(),
                                                            cart.getQuantity(), cart.getFileNames(), cart.getPrice(), cart.getIsInActive(),
                                                            cart.getCartDetailsID(), cartHeaderListDTO.getCustomerID(), 0, cart.getMaterialPriorityID(),
                                                            cart.getTotalPrice(), cart.getOfferValue(), cart.getOfferItemCartDetailsID(),
                                                            cart.getDiscountID(), cart.getDiscountText(), cart.getGST(), cart.getTax(), cart.getSubTotal(), cart.getHSNCode(),""));
                                                }
                                            }

                                        }

                                        for (int i = 0; i < cartHeadersList.size(); i++) {
                                            db.cartHeaderDAO().updateShipToPatryAndIsPriority(cartHeadersList.get(i).customerID, cartHeadersList.get(i).shipToPartyId, cartHeadersList.get(i).isPriority);
                                        }


                                        Calendar calendar = Calendar.getInstance();
                                        long mills = calendar.getTimeInMillis();
                                        sharedPreferencesUtils = new SharedPreferencesUtils(KeyValues.MY_PREFS, getActivity());
                                        sharedPreferencesUtils.savePreference("timer", mills);
                                        long timer = sharedPreferencesUtils.loadPreferenceAsLong("timer");
                                        ((MainActivity) getActivity()).startTime(300000 - (mills - timer));

                                        viewDialog.hideDialog();

                                        if (!sMaterailId.isEmpty() && !sPartnerId.isEmpty()) {
                                            if (db.cartDetailsDAO().getMaterialCount(sPartnerId, sMaterailId) == 0) {
                                                SnackbarUtils.showSnackbarLengthShort(coordinatorLayout, "Item is not available in your supply chain network.", ContextCompat.getColor(getActivity(), R.color.dark_red), Snackbar.LENGTH_SHORT);
                                            } else {
                                                Intent i = new Intent(getActivity(), CartActivity.class);
                                                i.putExtra(KeyValues.IS_ITEM_ADDED_TO_CART, true);
                                                startActivity(i);
                                                // SnackbarUtils.showSnackbarLengthShort(coordinatorLayout, "Item added to cart", ContextCompat.getColor(getActivity(), R.color.dark_green), Snackbar.LENGTH_SHORT);
                                            }
                                        }


                                    } else {
                                        viewDialog.hideDialog();
                                        SnackbarUtils.showSnackbarLengthShort(coordinatorLayout, "Item is not available in your supply chain network.", ContextCompat.getColor(getActivity(), R.color.dark_red), Snackbar.LENGTH_SHORT);
                                    }

                                } catch (JSONException e) {
                                    viewDialog.hideDialog();
                                }


                            } catch (Exception e) {
                                viewDialog.hideDialog();
                            }

                        }
                        ProgressDialogUtils.closeProgressDialog();
                    }
                    ProgressDialogUtils.closeProgressDialog();
                }

                // response object fails
                @Override
                public void onFailure(Call<OMSCoreMessage> call, Throwable throwable) {
                    DialogUtils.showAlertDialog(getActivity(), errorMessages.EMC_0002);
                    viewDialog.hideDialog();
                    ProgressDialogUtils.closeProgressDialog();
                }
            });
        } catch (Exception ex) {
            viewDialog.hideDialog();
            try {
                ExceptionLoggerUtils.createExceptionLog(ex.toString(), classCode, "001", getActivity());
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
                sMaterailId = "";
                sPartnerId = "";

                if (!sp.getString(KeyValues.USER_ROLE_NAME, "").equalsIgnoreCase(KeyValues.USER_ROLE_NAME_DTD)) {
                    partnerId = customerId;
                }

                if (!partnerId.isEmpty() || !partnerId.equalsIgnoreCase("")) {

                    if (!etQty.getText().toString().isEmpty()) {

                        if (Integer.parseInt(etQty.getText().toString()) != 0) {

                            /*if (!etQty.getText().toString().equalsIgnoreCase("0")) {*/

                            if (selectedVariant != null) {

                                if (db.cartDetailsDAO().getCartItemsWithOutApprovals() != null) {
                                    List<CartDetails> cartDetailsList = new ArrayList<>();
                                    cartDetailsList = db.cartDetailsDAO().getCartItemsWithOutApprovals();

                                    productCatalogs cDto;

                                    for (int i = 0; i < cartDetailsList.size(); i++) {

                                        cDto = new productCatalogs();
                                        cDto.setMaterialMasterID(cartDetailsList.get(i).materialID);
                                        cDto.setMCode(cartDetailsList.get(i).mCode);
                                        cDto.setQuantity(cartDetailsList.get(i).quantity);
                                        cDto.setCustomerID(String.valueOf(cartDetailsList.get(i).customerId));
                                        cDto.setImagePath(cartDetailsList.get(i).imgPath);
                                        cDto.setPrice(cartDetailsList.get(i).price);
                                        cDto.setShipToPartyCustomerID(String.valueOf(cartDetailsList.get(i).customerId));
                                        cDto.setCartDetailsID("0");
                                        cDto.setMaterialPriorityID(String.valueOf(cartDetailsList.get(i).isPriority));
                                        cDto.setDeliveryDate(cartDetailsList.get(i).deliveryDate);
                                        cDto.setCartHeaderID(Integer.parseInt(cartDetailsList.get(i).cartHeaderId));
                                        cartList.add(cDto);
                                    }
                                }

                                if (NetworkUtils.isInternetAvailable(getActivity())) {

                                    productCatalogs productCatalogs = new productCatalogs();
                                    productCatalogs.setMaterialMasterID(selectedVariant.materialID);
                                    productCatalogs.setMCode(selectedVariant.mCode);
                                    productCatalogs.setQuantity(etQty.getText().toString());
                                    productCatalogs.setCustomerID(partnerId);
                                    productCatalogs.setImagePath(selectedVariant.materialImgPath);
                                    productCatalogs.setPrice(price);
                                    productCatalogs.setShipToPartyCustomerID(partnerId);
                                    productCatalogs.setCartDetailsID("0");
                                    if (cbPriority.isChecked())
                                        productCatalogs.setMaterialPriorityID("1");
                                    else
                                        productCatalogs.setMaterialPriorityID("0");
                                    int header_count = db.cartHeaderDAO().getCountCustumerByHeader(Integer.parseInt(partnerId));
                                    if (header_count == 0)
                                        productCatalogs.setCartHeaderID(0);
                                    else {
                                        CartHeader cartHeader = db.cartHeaderDAO().getCartHeaderByCustomerID(Integer.valueOf(partnerId));
                                        productCatalogs.setCartHeaderID(cartHeader.cartHeaderID);
                                    }

                                    sMaterailId = selectedVariant.materialID;
                                    sPartnerId = partnerId;
                                    cartList.add(productCatalogs);

                                } else {
                                    int header_count = db.cartHeaderDAO().getCountCustumerByHeader(Integer.parseInt(partnerId));
                                    int prioity = 0;
                                    if (cbPriority.isChecked()) {
                                        prioity = 1;
                                    }
                                    if (header_count == 0) {
                                        db.cartHeaderDAO().insert(new CartHeader(Integer.parseInt(partnerId), db.customerDAO().getAllCustomerName(partnerId), 0.0, 0, 0, 0, 0, 0, "", "", ""));
                                        if (db.cartDetailsDAO().getCartDetailsCountByMaterialId(Integer.parseInt(partnerId), 0, Integer.parseInt(selectedVariant.materialID)) == 0) {
                                            db.cartDetailsDAO().insert(new CartDetails("0", selectedVariant.materialID, selectedVariant.mCode,
                                                    selectedVariant.mDescription, "", etQty.getText().toString(), selectedVariantImage,
                                                    "0", false, "0", Integer.valueOf(partnerId), 1, prioity, "0", "0", null,
                                                    "0", "", "0", "0", "0", "0",""));
                                        } else {
                                            String qty = db.cartDetailsDAO().getQantity(selectedVariant.materialID, partnerId, "0");
                                            int total_qty = Integer.parseInt(qty) + Integer.parseInt(etQty.getText().toString());
                                            db.cartDetailsDAO().updateQantity(String.valueOf(total_qty), selectedVariant.materialID, partnerId, "0");
                                        }
                                    } else {
                                        CartHeader cartHeader = db.cartHeaderDAO().getCartHeaderByCustomerID(Integer.valueOf(partnerId));
                                        if (db.cartDetailsDAO().getCartDetailsCountByMaterialId(Integer.parseInt(partnerId), cartHeader.cartHeaderID, Integer.parseInt(selectedVariant.materialID)) == 0) {
                                            db.cartDetailsDAO().insert(new CartDetails(String.valueOf(cartHeader.cartHeaderID), selectedVariant.materialID, selectedVariant.mCode,
                                                    selectedVariant.mDescription, "", etQty.getText().toString(), selectedVariantImage,
                                                    "0", false, "0", Integer.valueOf(partnerId), 1, prioity, "0", "0", null,
                                                    "0", "", "0", "0", "0", "0",""));
                                        } else {
                                            String qty = db.cartDetailsDAO().getQantity(selectedVariant.materialID, partnerId, String.valueOf(cartHeader.cartHeaderID));
                                            int total_qty = Integer.parseInt(qty) + Integer.parseInt(etQty.getText().toString());
                                            db.cartDetailsDAO().updateQantity(String.valueOf(total_qty), selectedVariant.materialID, partnerId, String.valueOf(cartHeader.cartHeaderID));
                                        }
                                    }
                                    db.cartHeaderDAO().updateIsUpdated(Integer.parseInt(partnerId), 1);
                                }

                                if (isCustomerMatched) {

                                    if (NetworkUtils.isInternetAvailable(getActivity())) {
                                        if (!price.equals("")) {
                                            if ((Integer.parseInt(etQty.getText().toString()) % (selectedVariant.stackSize)) == 0) {
                                                addToCart();
                                            } else {
                                                SnackbarUtils.showSnackbarLengthShort(coordinatorLayout, "Stack size is not correct please enter the mutiples of " + selectedVariant.stackSize + " Eg : " + stackSizeQty(Integer.parseInt((!etQty.getText().toString().isEmpty()) ? etQty.getText().toString() : "1"), selectedVariant.stackSize), ContextCompat.getColor(getActivity(), R.color.colorAccent), Snackbar.LENGTH_SHORT);
                                            }
                                        } else {
                                            SnackbarUtils.showSnackbarLengthShort(coordinatorLayout, getString(R.string.PriceNotAvailable), ContextCompat.getColor(getActivity(), R.color.colorAccent), Snackbar.LENGTH_SHORT);
                                        }
                                    } else {
                                        Intent i = new Intent(getActivity(), CartActivity.class);
                                        Bundle extras = new Bundle();
                                        extras.putBoolean(KeyValues.IS_ITEM_ADDED_TO_CART, true);
                                        startActivity(i);
                                    }

                                } else {
                                    SnackbarUtils.showSnackbarLengthShort(coordinatorLayout, getString(R.string.divisionIDnotmatched), ContextCompat.getColor(getActivity(), R.color.dark_red), Snackbar.LENGTH_SHORT);
                                    return;
                                }
                            }
                        } else {
                            SnackbarUtils.showSnackbarLengthShort(coordinatorLayout, getString(R.string.addTocartAlert), ContextCompat.getColor(getActivity(), R.color.dark_red), Snackbar.LENGTH_SHORT);
                        }
                    } else {
                        SnackbarUtils.showSnackbarLengthShort(coordinatorLayout, getString(R.string.addTocartAlert), ContextCompat.getColor(getActivity(), R.color.dark_red), Snackbar.LENGTH_SHORT);
                    }
                } else {
                    SnackbarUtils.showSnackbarLengthShort(coordinatorLayout, "No customer is mapped to this material.", ContextCompat.getColor(getActivity(), R.color.dark_red), Snackbar.LENGTH_SHORT);
                }

                break;

            case R.id.ivItem:
                showImage();
                break;

            case R.id.txtDownloadBrochure:
                if (NetworkUtils.isInternetAvailable(getActivity())) {
                    downLoadPDF(brochureUrl, txtItemName.getText().toString(), "Brochure");
                } else {
                    SnackbarUtils.showSnackbarLengthShort(coordinatorLayout, "Please enable internet or connectivity issue is there. ", ContextCompat.getColor(getActivity(), R.color.dark_red), Snackbar.LENGTH_SHORT);
                }
                break;

            case R.id.txtDownloadCatalog:
                if (NetworkUtils.isInternetAvailable(getActivity())) {
                    downLoadPDF(catalogUrl, txtItemName.getText().toString(), "Catalog");
                } else {
                    SnackbarUtils.showSnackbarLengthShort(coordinatorLayout, "Please enable internet or connectivity issue is there. ", ContextCompat.getColor(getActivity(), R.color.dark_red), Snackbar.LENGTH_SHORT);
                }
                break;

            case R.id.txtDownloadSepcs:
                if (NetworkUtils.isInternetAvailable(getActivity())) {
                    downLoadPDF(specsUrl, txtItemName.getText().toString(), "Specifications");
                } else {
                    SnackbarUtils.showSnackbarLengthShort(coordinatorLayout, "Please enable internet or connectivity issue is there. ", ContextCompat.getColor(getActivity(), R.color.dark_red), Snackbar.LENGTH_SHORT);
                }
                break;

            case R.id.availOffer:
                if (NetworkUtils.isInternetAvailable(getActivity())) {
                    if (mCodeName.size() > 0 && discountDesc.size() > 0)
                        showAvailableOffers(mCodeName, discountDesc);
                }
                break;


        }
    }


    public void downLoadPDF(String downloadUrl, String itemName, String type) {

        if (isDownloadManagerAvailable(getContext())) {

            BroadcastReceiver receiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    String action = intent.getAction();
                    long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
                    //Checking if the received broadcast is for our enqueued download by matching download id
                    if (downloadID == id) {
                        DownloadManager.Query query = new DownloadManager.Query();
                        query.setFilterById(downloadID);
                        Cursor cursor = dm.query(query);
                        if (cursor.moveToFirst()) {

                            switch (cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))) {
                                case DownloadManager.STATUS_SUCCESSFUL: {
                                    //common.showUserDefinedAlertType("SUCCESS",getActivity(),getContext(),"Success");
                                    break;
                                }
                                case DownloadManager.STATUS_FAILED: {
                                    common.showUserDefinedAlertType("Download failed..!", getActivity(), getContext(), "Error");
                                    break;
                                }
                                default:
                                    break;
                            }
                        }
                    }
/*                    if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(action)) {

                        try {
                            Toast.makeText(context, "ACTION_DOWNLOAD_COMPLETE", Toast.LENGTH_SHORT).show();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    else{
                        try {
                            Toast.makeText(context, "ACTION_DOWNLOAD_FAILED", Toast.LENGTH_SHORT).show();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }*/
                }
            };

            if (intentFilter == null) {
                intentFilter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
                getActivity().registerReceiver(receiver, intentFilter);
            }
            if (dm == null) {
                dm = (DownloadManager) getActivity().getSystemService(Context.DOWNLOAD_SERVICE);
            }

            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(downloadUrl));
            request.setDescription("Downloading");
            request.setTitle(itemName);
            // in order for this if to run, you must use the android 3.2 to compile your app
            request.allowScanningByMediaScanner();
            request.setMimeType("application/pdf");
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, itemName + " " + type + ".pdf");
            //dm.enqueue(request);
            downloadID = dm.enqueue(request);
            // get download service and enqueue file
            /* DownloadManager manager = (DownloadManager) getActivity().getSystemService(Context.DOWNLOAD_SERVICE);
            manager.enqueue(request); */
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
    private void showAvailableOffers(List<String> mCode, List<String> discountDesc) {

        final Dialog mDialog = new Dialog(getContext());
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialog.setCancelable(true);
        mDialog.setContentView(R.layout.dialog_offer);
        RecyclerView lstoffers = (RecyclerView) mDialog.findViewById(R.id.lstoffers);
        NestedScrollView nested = (NestedScrollView) mDialog.findViewById(R.id.nested);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        lstoffers.setLayoutManager(linearLayoutManager);
        lstoffers.setHasFixedSize(true);

        //ArrayList<String> scAndD = new ArrayList<>();

        //scAndD.add(selectedVariant.discountDesc);

        OffersAdapter adapter = new OffersAdapter(getActivity(), discountDesc, mCode, nested);
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

    protected void showShipToPartyDialog() {

        LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
        View promptView = layoutInflater.inflate(R.layout.shiptoparty_dialog, null);

        List<String> customerCodes;
        final List<String> customerIds;

        customerCodes = db.customerDAO().getCustomerNamesBasedOnMDivision(String.valueOf(materialDivisionId));
        customerIds = db.customerDAO().getCustomerIdsBasedOnMDivision(String.valueOf(materialDivisionId));


        if (customerCodes.size() == 0) {
            SnackbarUtils.showSnackbarLengthShort(coordinatorLayout, "No customer is mapped", ContextCompat.getColor(getActivity(), R.color.dark_red), Snackbar.LENGTH_SHORT);
            return;
        }

        ArrayAdapter adapter1 = new ArrayAdapter<String>(getActivity(), R.layout.row_text, customerCodes);
        final SearchableSpinner customerListDropDown = (SearchableSpinner) promptView.findViewById(R.id.customerListDropDown);
        customerListDropDown.setAdapter(adapter1);
        customerListDropDown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                // customerListDropDown.getSelectedItem().toString();
                customerId = customerIds.get(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        final AlertDialog d = new AlertDialog.Builder(getActivity())
                .setView(promptView)
                .setCancelable(false)
                .setPositiveButton("OK", null) //Set to null. We override the onclick
                //  .setNegativeButton("CLEAR", null)
                .create();

        d.setOnShowListener(new DialogInterface.OnShowListener() {

            @Override
            public void onShow(final DialogInterface dialog) {

                Button b = d.getButton(AlertDialog.BUTTON_POSITIVE);
                b.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {

                        /*if (!customerId.isEmpty() || customerId != null) {
                         *//*                            Bundle bundle = new Bundle();
                            bundle.putString("customerId", customerId);
                            FragmentUtils.replaceFragmentWithBackStackWithBundle(getActivity(), R.id.container, new ProductCatalogFragment(), bundle);*//*

                            try {
                                getArguments().remove("customerId");
                            } catch (NullPointerException ex) {
                                //
                            }

                        } else {
                            Toast.makeText(getActivity(), "No customer are there", Toast.LENGTH_SHORT).show();
                        }*/

                        getPrice();
                        productDiscount();

                        dialog.dismiss();
                    }
                });

            }
        });

        d.show();


    }

    @Override
    public void onResume() {
        super.onResume();
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(getString(R.string.toolbar_product));
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayUseLogoEnabled(false);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(getString(R.string.toolbar_ItemList));
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayUseLogoEnabled(false);
    }

    public int stackSizeQty(int qty, int size) {
        return (qty != 0) ? ((qty % size) != 0) ? ((qty / size) * size) + size : ((qty / size) * size) : size;
    }

}
