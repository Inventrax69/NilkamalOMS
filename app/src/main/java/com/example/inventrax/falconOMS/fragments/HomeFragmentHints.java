package com.example.inventrax.falconOMS.fragments;

/*
 * Created by Padmaja on 04/07/2019.
 */

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.example.inventrax.falconOMS.R;
import com.example.inventrax.falconOMS.activities.MainActivity;
import com.example.inventrax.falconOMS.activities.SyncMasterDataActivity;
import com.example.inventrax.falconOMS.adapters.MainMenuViewAdapter;
import com.example.inventrax.falconOMS.common.Common;
import com.example.inventrax.falconOMS.common.constants.EndpointConstants;
import com.example.inventrax.falconOMS.common.constants.ErrorMessages;
import com.example.inventrax.falconOMS.interfaces.ApiInterface;
import com.example.inventrax.falconOMS.model.KeyValues;
import com.example.inventrax.falconOMS.model.MainMenu;
import com.example.inventrax.falconOMS.pojos.CartDetailsListDTO;
import com.example.inventrax.falconOMS.pojos.CartHeaderListDTO;
import com.example.inventrax.falconOMS.pojos.CustomerListDTO;
import com.example.inventrax.falconOMS.pojos.ItemListDTO;
import com.example.inventrax.falconOMS.pojos.ModelDTO;
import com.example.inventrax.falconOMS.pojos.OMSCoreMessage;
import com.example.inventrax.falconOMS.pojos.OMSExceptionMessage;
import com.example.inventrax.falconOMS.pojos.VariantDTO;
import com.example.inventrax.falconOMS.pojos.productCatalogs;
import com.example.inventrax.falconOMS.room.AppDatabase;
import com.example.inventrax.falconOMS.room.CartDetails;
import com.example.inventrax.falconOMS.room.CartHeader;
import com.example.inventrax.falconOMS.room.CustomerTable;
import com.example.inventrax.falconOMS.room.ItemTable;
import com.example.inventrax.falconOMS.room.RoomAppDatabase;
import com.example.inventrax.falconOMS.room.UserDivisionCustTable;
import com.example.inventrax.falconOMS.room.VariantTable;
import com.example.inventrax.falconOMS.services.RestService;
import com.example.inventrax.falconOMS.util.DialogUtils;
import com.example.inventrax.falconOMS.util.ExceptionLoggerUtils;
import com.example.inventrax.falconOMS.util.FragmentUtils;
import com.example.inventrax.falconOMS.util.MainMenuList;
import com.example.inventrax.falconOMS.util.NetworkUtils;
import com.example.inventrax.falconOMS.util.ProgressDialogUtils;
import com.example.inventrax.falconOMS.util.SharedPreferencesUtils;
import com.example.inventrax.falconOMS.util.SnackbarUtils;
import com.example.inventrax.falconOMS.util.searchableSpinner.SearchableSpinner;
import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import smartdevelop.ir.eram.showcaseviewlib.GuideView;

import static android.content.Context.MODE_PRIVATE;

public class HomeFragmentHints extends Fragment {

    private static final String classCode = "OMS_Android_HomeFragment";
    private View rootView;
    private CoordinatorLayout coordLayout;
    private GuideView mGuideView;
    private GuideView.Builder builder;
    SharedPreferencesUtils sharedPreferencesUtils;
    String userName = "", userId = "", customerIDs = "", userRoleName = "";
    AppDatabase db;
    String customerId;
    private Gson gson;
    private OMSCoreMessage core;
    private Common common;
    private RestService restService;
    private ErrorMessages errorMessages;
    List<CustomerTable> customerTables;
    private List<ModelDTO> lstItem;
    private List<CustomerListDTO> customerList;
    List<ItemTable> itemTables;
    List<productCatalogs> cartList = null;
    private boolean isCustSynced = false, isItemSynced = false, isCartSynced = false;
    ProgressDialog progress;
    List<MainMenu> mainMenus;
    RecyclerView recyclerView;
    MainMenuViewAdapter mainMenuViewAdapter;
    SharedPreferences sp;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.aa_test_home_layout, container, false);
        loadFormControls();
        return rootView;

    }

    private void loadFormControls() {

        // To enable Bottom navigation bar
        ((MainActivity) getActivity()).SetNavigationVisibility(true);
        sp = getContext().getSharedPreferences(KeyValues.MY_PREFS, MODE_PRIVATE);
        userName = sp.getString(KeyValues.USER_NAME, "");
        userId = sp.getString(KeyValues.USER_ID, "");
        userRoleName = sp.getString(KeyValues.USER_ROLE_NAME, "");

        coordLayout = (CoordinatorLayout) rootView.findViewById(R.id.coordLayout);

        recyclerView=rootView.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        GridLayoutManager layoutManager = new GridLayoutManager(getActivity(),2);
        recyclerView.setLayoutManager(layoutManager);

        gson = new Gson();
        core = new OMSCoreMessage();
        common = new Common();
        restService = new RestService();
        errorMessages = new ErrorMessages();

        customerTables = new ArrayList<>();
        lstItem = new ArrayList<>();
        customerList = new ArrayList<>();
        itemTables = new ArrayList<>();
        cartList = new ArrayList<>();

        db = new RoomAppDatabase(getActivity()).getAppDatabase();

        sharedPreferencesUtils = new SharedPreferencesUtils(KeyValues.MY_PREFS, getActivity());

        //sharedPreferencesUtils.savePreference(KeyValues.SELECTED_CUSTOMER_ID_GLOBAL,"");

        mainMenus = new MainMenuList(getActivity()).getMainMenu(userRoleName);
        mainMenuViewAdapter = new MainMenuViewAdapter(getActivity(), mainMenus, new MainMenuViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int pos) {
                onClickView(mainMenus.get(pos).getTileName());
            }
        });

        recyclerView.setAdapter(mainMenuViewAdapter);


    }

    public void onClickView(String tileName) {
        switch (tileName) {
            case KeyValues.CUSTOMER_TITLE:
                FragmentUtils.replaceFragmentWithBackStack(getActivity(), R.id.container, new CustomerListFragment());
                break;
            case KeyValues.ORDER_BOOKING_TITLE:
                if (userRoleName.equals("DTD")) {
                    FragmentUtils.replaceFragmentWithBackStack(getActivity(), R.id.container, new ProductCatalogFragment());
                } else {
                    if(sp.getString(KeyValues.SELECTED_CUSTOMER_ID_GLOBAL,"").isEmpty()) {
                        showShipToPartyDialog();
                    }else {
                        Bundle bundle = new Bundle();
                        bundle.putString("customerId", sp.getString(KeyValues.SELECTED_CUSTOMER_ID_GLOBAL,""));
                        sharedPreferencesUtils.savePreference(KeyValues.SELECTED_CUSTOMER_ID_GLOBAL,customerId);
                        FragmentUtils.replaceFragmentWithBackStackWithBundle(getActivity(), R.id.container, new ProductCatalogFragment(), bundle);
                    }
                }
                break;
            case KeyValues.ORDER_TRACKING_TITLE:
                // FragmentUtils.replaceFragmentWithBackStack(getActivity(), R.id.container, new DeliveryTrackingFragment());
                break;
            case KeyValues.ORDER_ASSISTANCE_TITLE:
                if (sharedPreferencesUtils.loadPreferenceAsBoolean(KeyValues.IS_CUSTOMER_LOADED)) {
                    FragmentUtils.replaceFragmentWithBackStack(getActivity(), R.id.container, new OrderAssistanceFragment());
                } else {
                    SnackbarUtils.showSnackbar(coordLayout, getString(R.string.syncingData), Snackbar.LENGTH_SHORT);
                }
                break;
            case KeyValues.ORDER_LIST_TITLE:
                 FragmentUtils.replaceFragmentWithBackStack(getActivity(), R.id.container, new OrderHistoryFragment());

                break;
            case KeyValues.E_INFORMATION_TITLE:
                // FragmentUtils.replaceFragmentWithBackStack(getActivity(), R.id.container, new ProductsFragment());
                break;
            case KeyValues.DASHBOARD_TITLE:
                break;
            case KeyValues.INVENTORY_TITLE:
                break;
            case KeyValues.COMPLAINTS_TITLE:
                String url = KeyValues.COMPLAINTS_URL_EXTERNAL;
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
                break;
            case KeyValues.APPROVALS_TITLE:
                FragmentUtils.replaceFragmentWithBackStack(getActivity(), R.id.container, new ApprovalsFragment());
                break;
            default:
                break;
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

            final MenuItem item = menu.findItem(R.id.action_sync_info);
            item.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
            item.setVisible(true);

            final MenuItem item1 = menu.findItem(R.id.action_home);
            item1.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
            item1.setVisible(false);

            final MenuItem item2 = menu.findItem(R.id.action_notification);
            item2.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
            item2.setVisible(true);

        }

        super.onCreateOptionsMenu(menu, inflater);

        final View itemMessages = menu.findItem(R.id.action_notification).getActionView();
        TextView itemMessagesBadgeTextView = (TextView) itemMessages.findViewById(R.id.badge_textView);
        SharedPreferences prefs = getActivity().getSharedPreferences("myPrefs", MODE_PRIVATE);
        boolean value = prefs.getBoolean(KeyValues.IS_NOTIFICATION_AVAILABLE, false);
        Animation anim = new AlphaAnimation(0.0f, 1.0f);
        anim.setDuration(500); //You can manage the blinking time with this parameter
        anim.setStartOffset(20);
        anim.setRepeatMode(Animation.REVERSE);
        anim.setRepeatCount(Animation.INFINITE);

        if(value){
            itemMessagesBadgeTextView.startAnimation(anim);
            itemMessagesBadgeTextView.setVisibility(View.VISIBLE);
        }else{
            itemMessagesBadgeTextView.clearAnimation();
            itemMessagesBadgeTextView.setVisibility(View.GONE);
        }

        itemMessages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentUtils.replaceFragmentWithBackStack(getActivity(), R.id.container, new NotificationFragment());
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (id) {
            case R.id.action_sync_info: {
                /*progress = new ProgressDialog(getActivity());
                progress.setMessage(getActivity().getString(R.string.data_sync));
                progress.setCancelable(false);
                progress.show();*/

                /*if (!isCartSynced && !isCustSynced && !isItemSynced) {
                    getCustomerList();
                }*/

                Intent syncActivity = new Intent(getActivity(), SyncMasterDataActivity.class);
                startActivity(syncActivity);

            }
            break;
        }
        return super.onOptionsItemSelected(item);
    }

    protected void showShipToPartyDialog() {

        LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
        View promptView = layoutInflater.inflate(R.layout.shiptoparty_dialog, null);

        List<CustomerTable> getCustomerNames;
        List<String> customerCodes;
        final List<String> customerIds;


        /*
        customerCodes = db.customerDAO().getAllCustomerCodesName();
        ArrayAdapter adapter = new ArrayAdapter<String>(getActivity(), R.layout.row_text, customerCodes);
        final AutoCompleteTextView shipToPartyAutoComplete = (AutoCompleteTextView) promptView.findViewById(R.id.shipToPartyAutoComplete);*/

        // getCustomerNames = db.customerDAO().getCustomerNames();
        customerCodes = db.customerDAO().getCustomerCodes();
        customerIds = db.customerDAO().getCustomerId();
/*        for (int i = 0; i < customerCodes.size(); i++) {
            customerCodes.add(getCustomerNames.get(i).customerName);
            customerIds.add(getCustomerNames.get(i).customerId);
        }*/


        if (customerCodes.size() == 0) {
            SnackbarUtils.showSnackbarLengthShort(coordLayout, getActivity().getString(R.string.no_custmer), ContextCompat.getColor(getActivity(), R.color.dark_red), Snackbar.LENGTH_SHORT);
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
                .setPositiveButton(getString(R.string.proceed), null) //Set to null. We override the onclick
                //  .setNegativeButton("CLEAR", null)
                .create();

        d.setOnShowListener(new DialogInterface.OnShowListener() {

            @Override
            public void onShow(final DialogInterface dialog) {

                Button b = d.getButton(AlertDialog.BUTTON_POSITIVE);
                b.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {

                        if (customerId != null || !customerId.isEmpty()) {
                            Bundle bundle = new Bundle();
                            bundle.putString("customerId", customerId);
                            sharedPreferencesUtils.savePreference(KeyValues.SELECTED_CUSTOMER_ID_GLOBAL,customerId);
                            FragmentUtils.replaceFragmentWithBackStackWithBundle(getActivity(), R.id.container, new ProductCatalogFragment(), bundle);
                        } else {
                            SnackbarUtils.showSnackbarLengthShort(coordLayout, getActivity().getString(R.string.no_custmer), ContextCompat.getColor(getActivity(), R.color.dark_red), Snackbar.LENGTH_SHORT);
                        }
                        dialog.dismiss();

                    }
                });

            }
        });

        d.show();

    }

    public void getCustomerList() {

        if (NetworkUtils.isInternetAvailable(getActivity())) { }
        else {
            DialogUtils.showAlertDialog(getActivity(), errorMessages.EMC_0007);
            // soundUtils.alertSuccess(getActivity(),getBaseContext());
            return;
        }

        OMSCoreMessage message = new OMSCoreMessage();
        message = common.SetAuthentication(EndpointConstants.Customer_FPS_DTO, getActivity());
        ItemListDTO itemListDTO = new ItemListDTO();
        itemListDTO.setPageIndex(0);
        itemListDTO.setPageSize(0);
        itemListDTO.setHandheldRequest(true);
        message.setEntityObject(itemListDTO);

        Call<OMSCoreMessage> call = null;
        ApiInterface apiService = RestService.getClient().create(ApiInterface.class);

        call = apiService.GetCustomerListMobile(message);


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


                        } else {

                            LinkedTreeMap<String, String> _lstItem = new LinkedTreeMap<String, String>();
                            _lstItem = (LinkedTreeMap<String, String>) core.getEntityObject();

                            CustomerListDTO itemList;
                            final List<CustomerListDTO> lstDto = new ArrayList<CustomerListDTO>();

                            itemList = new CustomerListDTO(_lstItem.entrySet());

                            if (itemList.getResults() != null) {
                                customerTables = new ArrayList<>();

                                SharedPreferences sp = getActivity().getSharedPreferences(KeyValues.MY_PREFS, MODE_PRIVATE);
                                customerIDs = sp.getString(KeyValues.CUSTOMER_IDS, "");
                                userId = sp.getString(KeyValues.USER_ID, "");

                                JSONArray jsonArray = null;
                                try {
                                    jsonArray = new JSONArray(customerIDs);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                db.userDivisionCustDAO().deleteAll();
                                db.customerDAO().deleteAll();

                                for (CustomerListDTO dd : itemList.getResults()) {

                                    customerTables.add(new CustomerTable(dd.getCustomerID(), dd.getCustomerName(), dd.getCustomerCode(),
                                            dd.getCustomerType(), dd.getCustomerTypeID(), dd.getDivision(), dd.getDivisionID().split("[.]")[0], dd.getConnectedDepot(), dd.getMobile(),
                                            dd.getPrimaryID(), dd.getSalesDistrict(), dd.getZone(),dd.getCity()));

                                }

                                if (customerTables != null && customerTables.size() > 0) {
                                    db.customerDAO().insertAll(customerTables);
                                }

                                for (int i = 0; i < jsonArray.length(); i++) {

                                    try {
                                        CustomerTable custTable;
                                        custTable = db.customerDAO().getCustomer(jsonArray.getString(i));

                                        if (jsonArray.getString(i) != null) {
                                            db.userDivisionCustDAO().insert(new UserDivisionCustTable(custTable.customerId, custTable.divisionId));
                                        }

                                    } catch (Exception e) {

                                    }
                                }

  /*                              customerList = lstDto;
                                if (customerList != null && customerList.size() > 0) {

                                    db.customerDAO().insertAll(customerTables);

                                }*/

                                isCustSynced = true;
                                getProductCatalog();
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
                    if(NetworkUtils.isInternetAvailable(getActivity())){
                        DialogUtils.showAlertDialog(getActivity(), errorMessages.EMC_0001);
                    }else{
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

    public void getProductCatalog() {

        if (NetworkUtils.isInternetAvailable(getActivity())) {
        } else {
            DialogUtils.showAlertDialog(getActivity(), errorMessages.EMC_0007);
            // soundUtils.alertSuccess(getActivity(),getBaseContext());
            return;
        }

        OMSCoreMessage message = new OMSCoreMessage();
        message = common.SetAuthentication(EndpointConstants.ProductCatalog_FPS_DTO, getActivity());
        ItemListDTO itemListDTO = new ItemListDTO();
        itemListDTO.setPageIndex(1);
        itemListDTO.setPageSize(10);
        itemListDTO.setHandheldRequest(true);
        itemListDTO.setSearchString(null);
        itemListDTO.setFilter("0");
        message.setEntityObject(itemListDTO);

        Call<OMSCoreMessage> call = null;
        ApiInterface apiService = RestService.getClient().create(ApiInterface.class);

        call = apiService.ProductCatalog(message);

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


                        } else {

                            LinkedTreeMap<?, ?> _lstItem = new LinkedTreeMap<String, String>();
                            _lstItem = (LinkedTreeMap<String, String>) core.getEntityObject();

                            itemTables = new ArrayList<>();
                            ItemListDTO itemList;

                            try {

                                itemList = new ItemListDTO(_lstItem.entrySet());
                                lstItem = itemList.getResults();


                                if (lstItem != null && lstItem.size() > 0) {

                                    db.itemDAO().deleteAll();
                                    db.variantDAO().deleteAll();

                                    for (ModelDTO md : lstItem) {

                                        db.itemDAO().insert(new ItemTable(md.getModelID(), md.getDivisionID(), md.getSegmentID(), md.getModel(),
                                                md.getModelDescription(), md.getImgPath(), md.getDiscountCount(), md.getDiscountId(), md.getDiscountDesc()));

                                        for (VariantDTO variantDTO : md.getVarientList()) {

                                            db.variantDAO().insert(new VariantTable(md.getModelID(), md.getDivisionID(),
                                                    variantDTO.getMaterialID(), variantDTO.getMDescription(), variantDTO.getMDescriptionLong(),
                                                    variantDTO.getMcode(), variantDTO.getModelColor(), variantDTO.getMaterialImgPath(),
                                                    variantDTO.getDiscountCount(), variantDTO.getDiscountId(), variantDTO.getDiscountDesc(),
                                                    variantDTO.getProductSpecification(), variantDTO.getProductCatalog(), variantDTO.getEBrochure(), variantDTO.getOpenPrice(), (int) Double.parseDouble(variantDTO.getStackSize())));

                                        }

                                    }

                                    isItemSynced = true;
                                    syncCart();
                                }

                                ProgressDialogUtils.closeProgressDialog();

                            } catch (Exception e) {
                                common.showUserDefinedAlertType(errorMessages.EMC_0024, getActivity(), getActivity(), "Warning");
                                // logException();
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
                    if(NetworkUtils.isInternetAvailable(getActivity())){
                        DialogUtils.showAlertDialog(getActivity(), errorMessages.EMC_0001);
                    }else{
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

    public void syncCart() {

        OMSCoreMessage message = new OMSCoreMessage();
        message = common.SetAuthentication(EndpointConstants.ProductCatalog_FPS_DTO, getActivity());
        productCatalogs productCatalogs = new productCatalogs();
        productCatalogs.setHandheldRequest(true);
        productCatalogs.setUserID(userId);
        message.setEntityObject(productCatalogs);

        Call<OMSCoreMessage> call = null;
        ApiInterface apiService = RestService.getClient().create(ApiInterface.class);

        call = apiService.cartlist(message);

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

                            ProgressDialogUtils.closeProgressDialog();

                            try {
                                JSONArray getCartHeader = new JSONArray((String) core.getEntityObject());

/*                                  db.cartHeaderDAO().deleteAllIsUpdated();
                                db.cartDetailsDAO().deleteAllIsUpdated();
                                db.cartHeaderDAO().deleteHeadersNotThereInCartDetails();*/

                                db.cartHeaderDAO().deleteAll();
                                db.cartDetailsDAO().deleteAll();

                                for (int i = 0; i < getCartHeader.length(); i++) {

                                    TypeToken<CartHeaderListDTO> header = new TypeToken<CartHeaderListDTO>() {};

                                    for (int j = 0; j < getCartHeader.getJSONObject(i).getJSONArray("CartHeader").length(); j++) {

                                        CartHeaderListDTO cartHeaderListDTO = new Gson().fromJson(getCartHeader.getJSONObject(i).getJSONArray("CartHeader").getJSONObject(j).toString(), CartHeaderListDTO.class);

                                        db.cartHeaderDAO().insert(new CartHeader(cartHeaderListDTO.getCustomerID(), cartHeaderListDTO.getCustomerName(), cartHeaderListDTO.getCreditLimit(), cartHeaderListDTO.getCartHeaderID(),
                                                cartHeaderListDTO.getIsInActive(), cartHeaderListDTO.getIsCreditLimit(), cartHeaderListDTO.getIsApproved(), 0, cartHeaderListDTO.getCreatedOn(),
                                                cartHeaderListDTO.getTotalPrice(),cartHeaderListDTO.getTotalPriceWithTax()));

                                        for (int k = 0; k < cartHeaderListDTO.getListCartDetailsList().size(); k++) {

                                            CartDetailsListDTO cart = cartHeaderListDTO.getListCartDetailsList().get(k);

                                            db.cartDetailsDAO().insert(new CartDetails(String.valueOf(cartHeaderListDTO.getCartHeaderID()), cart.getMaterialMasterID(),
                                                    cart.getMCode(), cart.getMDescription(), cart.getActualDeliveryDate(),
                                                    cart.getQuantity(), cart.getFileNames(), cart.getPrice(), cart.getIsInActive(),
                                                    cart.getCartDetailsID(), cartHeaderListDTO.getCustomerID(), 0, cart.getMaterialPriorityID(),
                                                    cart.getTotalPrice(),cart.getOfferValue(),cart.getOfferItemCartDetailsID(),
                                                    cart.getDiscountID(),cart.getDiscountText(),cart.getGST(),cart.getTax(),cart.getSubTotal(),cart.getHSNCode(),cart.getDiscountedPrice()));
                                        }
                                    }
                                }

                                isCartSynced = true;
                                if (isCustSynced && isItemSynced)
                                    progress.dismiss();

                            } catch (Exception e) {
                                ProgressDialogUtils.closeProgressDialog();
                            }
                        }
                    }
                }

                // response object fails
                @Override
                public void onFailure(Call<OMSCoreMessage> call, Throwable throwable) {
                    if(NetworkUtils.isInternetAvailable(getActivity())){
                        DialogUtils.showAlertDialog(getActivity(), errorMessages.EMC_0001);
                    }else{
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


    @Override
    public void onResume() {
        super.onResume();
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayUseLogoEnabled(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setLogo(R.drawable.nilkamal);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(userName);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
    }
}
