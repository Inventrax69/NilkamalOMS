package com.example.inventrax.falconOMS.fragments;

/*
 * Created by Padmaja on 04/07/2019.
 */

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabaseLockedException;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.internal.BottomNavigationItemView;
import android.support.design.internal.BottomNavigationMenuView;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
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
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.inventrax.falconOMS.R;
import com.example.inventrax.falconOMS.activities.LoginActivity;
import com.example.inventrax.falconOMS.activities.MainActivity;
import com.example.inventrax.falconOMS.adapters.OffersAdapter;
import com.example.inventrax.falconOMS.common.Common;
import com.example.inventrax.falconOMS.common.Log.Logger;
import com.example.inventrax.falconOMS.common.constants.EndpointConstants;
import com.example.inventrax.falconOMS.common.constants.ErrorMessages;
import com.example.inventrax.falconOMS.interfaces.ApiInterface;
import com.example.inventrax.falconOMS.model.KeyValues;
import com.example.inventrax.falconOMS.pojos.CartDetailsListDTO;
import com.example.inventrax.falconOMS.pojos.CartHeaderListDTO;
import com.example.inventrax.falconOMS.pojos.ItemListDTO;
import com.example.inventrax.falconOMS.pojos.ModelDTO;
import com.example.inventrax.falconOMS.pojos.OMSCoreMessage;
import com.example.inventrax.falconOMS.pojos.OMSExceptionMessage;
import com.example.inventrax.falconOMS.pojos.PriceDTO;
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
import com.example.inventrax.falconOMS.util.NetworkUtils;
import com.example.inventrax.falconOMS.util.ProgressDialogUtils;
import com.example.inventrax.falconOMS.util.SharedPreferencesUtils;
import com.example.inventrax.falconOMS.util.SnackbarUtils;
import com.example.inventrax.falconOMS.util.ViewDialog;
import com.example.inventrax.falconOMS.util.searchableSpinner.SearchableSpinner;
import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
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

import static java.sql.Types.NULL;

public class ProductCatalogFragment extends Fragment implements SearchView.OnQueryTextListener, AdapterView.OnItemSelectedListener, View.OnClickListener {

    private static final String classCode = "OMS_Android_ProductCatalog";
    static int pagecount = 0;
    static int postion_value = 0;
    static List<ItemTable> itemsList;
    BottomSheetBehavior behavior;
    CoordinatorLayout coordinatorLayout;
    RestService restService;
    List<VariantTable> variantTables = null;
    boolean loading = true;
    int pastVisiblesItems, visibleItemCount, totalItemCount;
    LinearLayoutManager layoutManager;
    AppDatabase db;
    String myFormat = "dd/MMM/yyyy";
    UserDivisionCustTable userDivisionCustTable;
    List<productCatalogs> cartList = null;
    AlertDialog dialog;
    String customerId = "";
    // flag=0 => getList() ; flag=1 => getListDESC() ; flag=2 => getListASC() ; flag=3 => filter()
    static int flag = 0;
    AlertDialog b;
    int stackSize;
    SharedPreferencesUtils sharedPreferencesUtils;
    private View rootView;
    private FrameLayout frame;
    private RecyclerView recyclerView;
    private CardView txtViewType;
    private ImageView ivViewType, ivSort;
    private LinearLayout llSort;
    private AppCompatButton ivAddToCartBottom;
    private ImageView ivItemBottom;
    private SearchableSpinner spinnerColorSelectionBottom;
    private TextView txtItemNameBottom, txtTimer, txtCustomerName;
    private EditText etQtyBottom, etPrice;
    private PaginationAdapter mAdapter;
    private Common common;
    private ExceptionLoggerUtils exceptionLoggerUtils;
    private ErrorMessages errorMessages;
    private Gson gson;
    private OMSCoreMessage core;
    private boolean isgrid = false;
    private boolean isDesc = false;
    private VariantTable selectedVariant;
    private String selectedVariantImage;
    private String selected;
    private String materialId = "", price = "";
    private CustomerTable custTable;
    private String userId = "", cusomerIDs = "", partnerId = "", shipToParty = "", userRoleName = "";
    private boolean isCustomerMatched = false;
    CheckBox cbPriority;
    int count = 0;
    ViewDialog viewDialog;
    SharedPreferences sp;
    ProgressDialog dialog1;
    String sMaterailId="",sPartnerId="";

    @Override
    public void onStart() {
        recyclerView.scrollToPosition(postion_value);
        super.onStart();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.productcatalog_fragment, container, false);
        isgrid = false;
        try {
            loadFormControl();
        } catch (Exception e) { }

        return rootView;
    }

    @SuppressLint("StaticFieldLeak")
    private void loadFormControl() {
        // To enable Bottom navigation bar
        ((MainActivity) getActivity()).SetNavigationVisibility(true);
        sp = getActivity().getSharedPreferences(KeyValues.MY_PREFS, Context.MODE_PRIVATE);
        userId = sp.getString(KeyValues.USER_ID, "");
        cusomerIDs = sp.getString(KeyValues.CUSTOMER_IDS, "");
        userRoleName = sp.getString(KeyValues.USER_ROLE_NAME, "");
        db = new RoomAppDatabase(getActivity()).getAppDatabase();
        sharedPreferencesUtils = new SharedPreferencesUtils(KeyValues.MY_PREFS, getActivity());
        try {
            if (getArguments().getString("customerId") != null) {
                if (!getArguments().getString("customerId").isEmpty() || getArguments().getString("customerId") != null)
                    customerId = getArguments().getString("customerId");
            } else {
                if (customerId == null || customerId.isEmpty())
                    if (userRoleName.equals("DTD")) { } else {
                        showShipToPartyDialog();
                    }
            }
        } catch (NullPointerException e) {
            if (customerId == null || customerId.isEmpty())
                if (userRoleName.equals("DTD")) { } else {
                    showShipToPartyDialog();
                }
        }

        frame = (FrameLayout) rootView.findViewById(R.id.frame);
        // itemsList=new ArrayList<>();
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);

        llSort = (LinearLayout) rootView.findViewById(R.id.llSort);
        llSort.setOnClickListener(this);

        mAdapter = new PaginationAdapter(isgrid);

        coordinatorLayout = (CoordinatorLayout) rootView.findViewById(R.id.coordinatorLayout);
        ivAddToCartBottom = (AppCompatButton) rootView.findViewById(R.id.ivAddToCartBottom);
        ivItemBottom = (ImageView) rootView.findViewById(R.id.ivItemBottom);
        txtItemNameBottom = (TextView) rootView.findViewById(R.id.txtItemNameBottom);
        etQtyBottom = (EditText) rootView.findViewById(R.id.etQtyBottom);
        etPrice = (EditText) rootView.findViewById(R.id.etPrice);
        cbPriority = (CheckBox) rootView.findViewById(R.id.cbPriority);
        txtTimer = (TextView) getActivity().findViewById(R.id.txtTimer);
        txtCustomerName = (TextView) rootView.findViewById(R.id.txtCustomerName);

        if (userRoleName.equals("DTD")) {
            txtCustomerName.setVisibility(View.GONE);
        } else {
            txtCustomerName.setVisibility(View.VISIBLE);
            if (db.customerDAO().getAllCustomerName(customerId) == null)
                txtCustomerName.setText("");
            else
                txtCustomerName.setText(db.customerDAO().getCustomerCode(customerId));
        }

        spinnerColorSelectionBottom = (SearchableSpinner) rootView.findViewById(R.id.spinnerColorSelectionBottom);
        spinnerColorSelectionBottom.setOnItemSelectedListener(this);

        common = new Common();
        errorMessages = new ErrorMessages();
        exceptionLoggerUtils = new ExceptionLoggerUtils();
        restService = new RestService();
        core = new OMSCoreMessage();

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

        viewDialog = new ViewDialog(getActivity());

        if (NetworkUtils.isInternetAvailable(getActivity())) {

            new AsyncTask<Void, Integer, String>() {

                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                    dialog1 = new ProgressDialog(getActivity());
                    dialog1.setMessage("Fetching items...");
                    dialog1.show();
                }

                @Override
                protected String doInBackground(Void... voids) {
                    loadProductCatlog();
                    return null;
                }

                @Override
                protected void onPostExecute(String s) {
                    super.onPostExecute(s);
/*                    if (dialog1.isShowing()) {
                        dialog1.dismiss();
                    }*/
                }

            }.execute();

        } else
            loadCatlog();

    }

    void loadCatlog() {

        try {
            if (customerId.equals("") || customerId.isEmpty()) {

                if (!db.itemDAO().getAllItemsAll(pagecount).isEmpty()) {
                    layoutManager = new LinearLayoutManager(getActivity());
                    recyclerView.setLayoutManager(layoutManager);
                    // shows the items which are there in sqlite item table
                    count = db.itemDAO().getAllItemsAllCount();
                    if (flag == 0)
                        updateToRecyclerView(db.itemDAO().getAllItemsAll(pagecount), 0);
                    else if (flag == 1)
                        updateToRecyclerView(db.itemDAO().getAllItemsDESCAll(pagecount), 0);
                    else
                        updateToRecyclerView(db.itemDAO().getAllItemsASCAll(pagecount), 0);
                } else {
                    layoutManager = new LinearLayoutManager(getActivity());
                    recyclerView.setLayoutManager(layoutManager);
                }

            } else {
                if (!db.itemDAO().getAllItems(pagecount, customerId).isEmpty()) {
                    layoutManager = new LinearLayoutManager(getActivity());
                    recyclerView.setLayoutManager(layoutManager);
                    // shows the items which are there in sqlite item table
                    count = db.itemDAO().getAllItemsCount(customerId);
                    if (flag == 0)
                        updateToRecyclerView(db.itemDAO().getAllItems(pagecount, customerId), 0);
                    else if (flag == 1)
                        updateToRecyclerView(db.itemDAO().getAllItemsDESC(pagecount, customerId), 0);
                    else
                        updateToRecyclerView(db.itemDAO().getAllItemsASC(pagecount, customerId), 0);

                } else {
                    layoutManager = new LinearLayoutManager(getActivity());
                    recyclerView.setLayoutManager(layoutManager);
                }
            }


        } catch (SQLiteDatabaseLockedException e) {

        }


        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) {
                    // ((MainActivity) getActivity()).SetNavigationVisibility(false);
                    visibleItemCount = layoutManager.getChildCount();
                    totalItemCount = layoutManager.getItemCount();
                    pastVisiblesItems = layoutManager.findFirstVisibleItemPosition();

                    if (loading) {
                        if ((visibleItemCount + pastVisiblesItems) >= totalItemCount) {
                            //  loading = true;

                            //Do pagination.. i.e. fetch new data
                            // mAdapter.addLoadingFooter();

                            // Log.v("pagination","Value "+(count) +" Size" + itemsList.size());
                            if (count > itemsList.size()) {
                                pagecount++;
                                if (customerId.equals("") || customerId.isEmpty()) {
                                    if (flag == 0)
                                        updateToRecyclerView(db.itemDAO().getAllItemsAll(pagecount), 1);
                                    else if (flag == 1)
                                        updateToRecyclerView(db.itemDAO().getAllItemsDESCAll(pagecount), 1);
                                    else
                                        updateToRecyclerView(db.itemDAO().getAllItemsASCAll(pagecount), 1);
                                } else {
                                    if (flag == 0)
                                        updateToRecyclerView(db.itemDAO().getAllItems(pagecount, customerId), 1);
                                    else if (flag == 1)
                                        updateToRecyclerView(db.itemDAO().getAllItemsDESC(pagecount, customerId), 1);
                                    else
                                        updateToRecyclerView(db.itemDAO().getAllItemsASC(pagecount, customerId), 1);
                                }
                            }
                        }
                    }
                } else if (dy < 0) {
                    //((MainActivity) getActivity()).SetNavigationVisibility(true);
                }
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }
        });


        recyclerView.smoothScrollToPosition(0);
    }


    void loadProductCatlog() {

        if (NetworkUtils.isInternetAvailable(getActivity())) {

        } else {
            DialogUtils.showAlertDialog(getActivity(), errorMessages.EMC_0007);
            // soundUtils.alertSuccess(getActivity(),getBaseContext());
            return;
        }

        OMSCoreMessage message = new OMSCoreMessage();
        message = common.SetAuthentication(EndpointConstants.ProductCatalog_FPS_DTO, getActivity());
        ItemListDTO itemListDTO = new ItemListDTO();
        itemListDTO.setSearchString(null);
        itemListDTO.setFilter("0");
        itemListDTO.setPageIndex(1);
        itemListDTO.setPageSize(10);
        itemListDTO.setHandheldRequest(true);
        if (customerId.isEmpty() || customerId.equals(""))
            itemListDTO.setCustomerID(null);
        else
            itemListDTO.setCustomerID(customerId);
        message.setEntityObject(itemListDTO);

        Call<OMSCoreMessage> call = null;
        ApiInterface apiService = RestService.getClient().create(ApiInterface.class);

        // ProgressDialogUtils.showProgressDialog("Please wait..");
        call = apiService.ProductCatalog2(message);

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

                            dialog.dismiss();

                        } else {

                            try {

                                LinkedTreeMap<?, ?> _lstItem = new LinkedTreeMap<String, String>();
                                _lstItem = (LinkedTreeMap<String, String>) core.getEntityObject();

                                List<ItemTable> itemTables = new ArrayList<>();
                                ItemListDTO itemList;

                                try {

                                    itemList = new ItemListDTO(_lstItem.entrySet());
                                    final List<ModelDTO> lstItem = itemList.getResults();

                                    if (lstItem != null && lstItem.size() > 0) {

/*                                        new AsyncTask<Void, String, Integer>() {
                                            @Override
                                            protected Integer doInBackground(Void... voids) {*/
                                                db.itemDAO().updateDiscount();
                                                db.variantDAO().updateDiscount();

                                                for (ModelDTO md : lstItem) {

                                                    if (db.itemDAO().getCountByModelID(md.getModelID()) == 0) {
                                                        db.itemDAO().insert(new ItemTable(md.getModelID(), md.getDivisionID(), md.getSegmentID(), md.getModel(),
                                                                md.getModelDescription(), md.getImgPath(), md.getDiscountCount(), md.getDiscountId(), md.getDiscountDesc()));
                                                    } else {
                                                        db.itemDAO().updateByModelID(md.getModelID(), md.getDivisionID(), md.getSegmentID(), md.getModel(),
                                                                md.getModelDescription(), md.getImgPath(), "", md.getDiscountCount(), md.getDiscountId(), md.getDiscountDesc(), "");
                                                    }

                                                    for (VariantDTO variantDTO : md.getVarientList()) {

                                                        if (db.variantDAO().getCountByMaterialID(variantDTO.getMaterialID()) == 0) {
                                                            db.variantDAO().insert(new VariantTable(md.getModelID(), md.getDivisionID(),
                                                                    variantDTO.getMaterialID(), variantDTO.getMDescription(), variantDTO.getMDescriptionLong(),
                                                                    variantDTO.getMcode(), variantDTO.getModelColor(), variantDTO.getMaterialImgPath(),
                                                                    variantDTO.getDiscountCount(), variantDTO.getDiscountId(), variantDTO.getDiscountDesc(),
                                                                    variantDTO.getProductSpecification(), variantDTO.getProductCatalog(), variantDTO.getEBrochure(), variantDTO.getOpenPrice(), (int) Double.parseDouble(variantDTO.getStackSize())));
                                                        } else {
                                                            db.variantDAO().updateByMaterialID(md.getModelID(), md.getDivisionID(),
                                                                    variantDTO.getMaterialID(), variantDTO.getMDescription(), variantDTO.getMDescriptionLong(),
                                                                    variantDTO.getMcode(), variantDTO.getModelColor(), "",
                                                                    variantDTO.getDiscountCount(), variantDTO.getDiscountId(), variantDTO.getDiscountDesc(), variantDTO.getMaterialImgPath(),
                                                                    variantDTO.getProductSpecification(), variantDTO.getProductCatalog(), variantDTO.getEBrochure(),
                                                                    String.valueOf(variantDTO.getOpenPrice()), String.valueOf((int) Double.parseDouble(variantDTO.getStackSize())), "");
                                                        }
                                                    }
                                                }



/*                                                return null;
                                            }
                                        }.execute();*/

                                        loadCatlog();
                                    } else {
                                        db.itemDAO().updateDiscount();
                                        db.variantDAO().updateDiscount();
                                        loadCatlog();

                                    }

                                    ProgressDialogUtils.closeProgressDialog();

                                } catch (Exception e) {
                                    common.showUserDefinedAlertType(errorMessages.EMC_0018, getActivity(), getActivity(), "Warning");
                                }


                                if (dialog1.isShowing()) {
                                    dialog1.dismiss();
                                }


                            } catch (Exception ex) {

                                if (dialog1.isShowing()) {
                                    dialog1.dismiss();
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

                    if (dialog1.isShowing()) {
                        dialog1.dismiss();
                    }

                    if (NetworkUtils.isInternetAvailable(getActivity())) {
                        DialogUtils.showAlertDialog(getActivity(), errorMessages.EMC_0001);
                    } else {
                        DialogUtils.showAlertDialog(getActivity(), errorMessages.EMC_0014);
                    }
                    ProgressDialogUtils.closeProgressDialog();
                }
            });
        } catch (Exception ex) {

            if (dialog1.isShowing()) {
                dialog1.dismiss();
            }

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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        postion_value = 0;
        pagecount = 0;
        ((MainActivity) getActivity()).SetNavigationVisibility(true);
        try {
            itemsList = new ArrayList<>();
            recyclerView.setAdapter(null);
        } catch (NullPointerException ex) {
            //
        }

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        if (menu != null) {

            final MenuItem item = menu.findItem(R.id.cust_action_search);
            item.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
            item.setVisible(true);

            final MenuItem itemc = menu.findItem(R.id.action_change);
            itemc.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
            if (userRoleName.equals("DTD")) {
                itemc.setVisible(false);
            } else {
                itemc.setVisible(true);
            }


            final MenuItem item1 = menu.findItem(R.id.action_home);
            item1.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
            item1.setVisible(false);

            final MenuItem item2 = menu.findItem(R.id.action_cart);
            item2.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
            item2.setVisible(false);

            final SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);
            searchView.setMaxWidth(android.R.attr.width);
            searchView.setOnQueryTextListener(this);
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

            case R.id.action_change: {
                showShipToPartyDialog();
            }
            break;

        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String searchText) {

        List<ItemTable> filteredModelList = null;
        if (customerId.isEmpty() && customerId.equals(""))
            filteredModelList = filter(db.itemDAO().getAll(), searchText);
        else
            filteredModelList = filter(db.itemDAO().getAllByCustomer(customerId), searchText);

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));


        // List<ItemTable> sample= db.itemDAO().getFilterAll(searchText);

        try {
            // data set changed
            if (filteredModelList != null)
                updateToRecyclerView(db.itemDAO().getFilterAll(searchText), 2);

            if (customerId.isEmpty() && customerId.equals(""))
                updateToRecyclerView(db.itemDAO().getFilterAll(searchText), 2);
            else
                updateToRecyclerView(db.itemDAO().getFilterAllByCustomer(searchText, customerId), 2);

            if (TextUtils.isEmpty(searchText)) {
                updateToRecyclerView(itemsList, 2);
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

    public void updateToRecyclerView(final List<ItemTable> itemList1, int i) {

        ProgressDialogUtils.showProgressDialog("Please wait..");

        mAdapter = new PaginationAdapter(getActivity(), new PaginationAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int pos) {
                behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                frame.setVisibility(View.GONE);
                //go to item details page
                navigateToItemDetails(pos);
            }

            @Override
            public void onCartClick(int pos) {

                behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                frame.setVisibility(View.VISIBLE);

                ArrayList<String> varient = new ArrayList<>();

                variantTables = db.variantDAO().getVariants(itemsList.get(pos).modelID);

                for (VariantTable var : variantTables) {
                    varient.add(var.modelColor);
                }

                ArrayAdapter adapter = new ArrayAdapter(getActivity(), R.layout.support_simple_spinner_dropdown_item, varient);
                spinnerColorSelectionBottom.setAdapter(adapter);

                if (!itemsList.get(pos).imgPath.equals("")) {
                    Picasso.with(getActivity())
                            .load(itemsList.get(pos).imgPath)
                            .placeholder(R.drawable.no_img)
                            .into(ivItemBottom);
                } else {
                    ivItemBottom.setImageResource(R.drawable.no_img);
                }

                ivAddToCartBottom.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        sMaterailId="";
                        sPartnerId="";

                        if (db.userDivisionCustDAO().getAll().size() == 0) {

                            SnackbarUtils.showSnackbarLengthShort(coordinatorLayout, "No customer is mapped. Please contact support team", ContextCompat.getColor(getActivity(), R.color.colorAccent), Snackbar.LENGTH_SHORT);

                        } else {

                            behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                            frame.setVisibility(View.GONE);

                            if (!sp.getString(KeyValues.USER_ROLE_NAME, "").equalsIgnoreCase(KeyValues.USER_ROLE_NAME_DTD)) {
                                partnerId = customerId;
                            }

                            cartList = new ArrayList<>();
                            if (!partnerId.isEmpty() || !partnerId.equalsIgnoreCase("")) {

                                if (!etQtyBottom.getText().toString().isEmpty()) {

                                    if (Integer.parseInt(etQtyBottom.getText().toString()) != 0) {

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
                                                productCatalogs.setQuantity(etQtyBottom.getText().toString());
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
                                                sMaterailId=selectedVariant.materialID;
                                                sPartnerId=partnerId;
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
                                                                selectedVariant.mDescription, "", etQtyBottom.getText().toString(), selectedVariantImage,
                                                                "0", false, "0", Integer.valueOf(partnerId), 1, prioity, "0", "0", null,
                                                                "0", "", "0", "0", "0", "0"));
                                                    } else {
                                                        String qty = db.cartDetailsDAO().getQantity(selectedVariant.materialID, partnerId, "0");
                                                        int total_qty = Integer.parseInt(qty) + Integer.parseInt(etQtyBottom.getText().toString());
                                                        db.cartDetailsDAO().updateQantity(String.valueOf(total_qty), selectedVariant.materialID, partnerId, "0");
                                                    }
                                                    // cart.getDiscountID(),cart.getDiscountText(),cart.getGST(),cart.getTax(),cart.getSubTotal(),cart.getHSNCode()

                                                } else {
                                                    CartHeader cartHeader = db.cartHeaderDAO().getCartHeaderByCustomerID(Integer.valueOf(partnerId));
                                                    if (db.cartDetailsDAO().getCartDetailsCountByMaterialId(Integer.parseInt(partnerId), cartHeader.cartHeaderID, Integer.parseInt(selectedVariant.materialID)) == 0) {
                                                        db.cartDetailsDAO().insert(new CartDetails(String.valueOf(cartHeader.cartHeaderID), selectedVariant.materialID, selectedVariant.mCode,
                                                                selectedVariant.mDescription, "", etQtyBottom.getText().toString(), selectedVariantImage,
                                                                "0", false, "0", Integer.valueOf(partnerId), 1, prioity, "0", "0", null,
                                                                "0", "", "0", "0", "0", "0"));
                                                    } else {
                                                        String qty = db.cartDetailsDAO().getQantity(selectedVariant.materialID, partnerId, String.valueOf(cartHeader.cartHeaderID));
                                                        int total_qty = Integer.parseInt(qty) + Integer.parseInt(etQtyBottom.getText().toString());
                                                        db.cartDetailsDAO().updateQantity(String.valueOf(total_qty), selectedVariant.materialID, partnerId, String.valueOf(cartHeader.cartHeaderID));
                                                    }
                                                }
                                                db.cartHeaderDAO().updateIsUpdated(Integer.parseInt(partnerId), 1);
                                            }

                                            if (isCustomerMatched) {
                                                // showShipToPartyDialog();
                                                if (NetworkUtils.isInternetAvailable(getActivity())) {

                                                    if (!price.equals("")) {
                                                        if ((Integer.parseInt(etQtyBottom.getText().toString()) % (selectedVariant.stackSize)) == 0) {
                                                            behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                                                            frame.setVisibility(View.GONE);
                                                            addToCart();
                                                        } else {
                                                            SnackbarUtils.showSnackbarLengthShort(coordinatorLayout, "Stack size is not correct please enter the mutiples of " + selectedVariant.stackSize + " Eg : " + stackSizeQty(Integer.parseInt((!etQtyBottom.getText().toString().isEmpty()) ? etQtyBottom.getText().toString() : "1"), selectedVariant.stackSize), ContextCompat.getColor(getActivity(), R.color.colorAccent), Snackbar.LENGTH_SHORT);
                                                        }
                                                    } else {
                                                        SnackbarUtils.showSnackbarLengthShort(coordinatorLayout, getString(R.string.PriceNotAvailable), ContextCompat.getColor(getActivity(), R.color.colorAccent), Snackbar.LENGTH_SHORT);
                                                    }
                                                } else {
                                                    /*Intent i = new Intent(getActivity(), CartActivity.class);
                                                    Bundle extras = new Bundle();
                                                    extras.putBoolean(KeyValues.IS_ITEM_ADDED_TO_CART, true);
                                                    startActivity(i);*/

                                                    updateCartItemsCount();
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
                        }

                    }
                });
            }

            @Override
            public void onSchemeClick(int pos) {
                showAvailableOffers(pos);
            }
        }, isgrid, getActivity());

        if (count > itemsList.size()) {
            if (i == 1) {
                if (customerId.isEmpty() && customerId.equals(""))
                    if (flag == 0)
                        itemsList = db.itemDAO().setAllItemsAll(pagecount);
                    else if (flag == 1)
                        itemsList = db.itemDAO().setAllItemsAllDESC(pagecount);
                    else
                        itemsList = db.itemDAO().setAllItemsAllASC(pagecount);
                else {
                    if (flag == 0)
                        itemsList = db.itemDAO().setAllItems(pagecount, customerId);
                    else if (flag == 1)
                        itemsList = db.itemDAO().setAllItemsDESC(pagecount, customerId);
                    else
                        itemsList = db.itemDAO().setAllItemsASC(pagecount, customerId);
                }
                mAdapter.addAll(itemList1);
            } else if (i == 2) {
                itemsList = itemList1;
            } else {
                if (customerId.isEmpty() && customerId.equals(""))
                    if (flag == 0)
                        itemsList = db.itemDAO().setAllItemsAll(pagecount + 1);
                    else if (flag == 1)
                        itemsList = db.itemDAO().setAllItemsAllDESC(pagecount + 1);
                    else
                        itemsList = db.itemDAO().setAllItemsAllASC(pagecount + 1);
                else if (flag == 0)
                    itemsList = db.itemDAO().setAllItems(pagecount + 1, customerId);
                else if (flag == 1)
                    itemsList = db.itemDAO().setAllItemsDESC(pagecount + 1, customerId);
                else
                    itemsList = db.itemDAO().setAllItemsASC(pagecount + 1, customerId);
            }
        } else {
            if (i == 2) {
                itemsList = itemList1;
            }
        }

        recyclerView.setAdapter(mAdapter);

        if (count > (pagecount * 15))
            recyclerView.scrollToPosition(pagecount * 15);
        else
            recyclerView.scrollToPosition(count);

        ProgressDialogUtils.closeProgressDialog();

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
                                etPrice.setText("");
                                price = jObject.getString("Amount");
                                etPrice.setText("Rs." + price);

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
        ApiInterface apiService = RestService.getClient().create(ApiInterface.class);

        call = apiService.HHTCartDetails(message);

        //ProgressDialogUtils.showProgressDialog("Please Wait");

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


                                etQtyBottom.setText("1");

                                List<CartHeader> cartHeadersList = db.cartHeaderDAO().getCartHeadersForSTP();

                                db.cartDetailsDAO().deleteAll();
                                db.cartHeaderDAO().deleteAll();

                                ProgressDialogUtils.closeProgressDialog();
                                try {
                                    if(core.getEntityObject()!= null) {

                                        JSONArray getCartHeader = new JSONArray((String) core.getEntityObject());

                                        CartHeaderListDTO cartHeaderListDTO;
                                        CartDetailsListDTO cart;

                                        for (int i = 0; i < getCartHeader.length(); i++) {

                                            for (int j = 0; j < getCartHeader.getJSONObject(i).getJSONArray("CartHeader").length(); j++) {
                                                cartHeaderListDTO = new Gson().fromJson(getCartHeader.getJSONObject(i).getJSONArray("CartHeader").getJSONObject(j).toString(), CartHeaderListDTO.class);

                                                if (cartHeaderListDTO.getListCartDetailsList().size() > 0) {
                                                    db.cartHeaderDAO().insert(new CartHeader(cartHeaderListDTO.getCustomerID(), cartHeaderListDTO.getCustomerName(), cartHeaderListDTO.getCreditLimit(), cartHeaderListDTO.getCartHeaderID(),
                                                            cartHeaderListDTO.getIsInActive(), cartHeaderListDTO.getIsCreditLimit(), cartHeaderListDTO.getIsApproved(), 0, cartHeaderListDTO.getCreatedOn(),
                                                            cartHeaderListDTO.getTotalPrice(), cartHeaderListDTO.getTotalPriceWithTax()));
                                                    db.cartHeaderDAO().updateIsUpdated(cartHeaderListDTO.getCustomerID(), 0);
                                                }


                                                for (int k = 0; k < cartHeaderListDTO.getListCartDetailsList().size(); k++) {
                                                    cart = cartHeaderListDTO.getListCartDetailsList().get(k);
                                                    db.cartDetailsDAO().insert(new CartDetails(String.valueOf(cartHeaderListDTO.getCartHeaderID()), cart.getMaterialMasterID(),
                                                            cart.getMCode(), cart.getMDescription(), cart.getActualDeliveryDate(),
                                                            cart.getQuantity(), cart.getFileNames(), cart.getPrice(), cart.getIsInActive(),
                                                            cart.getCartDetailsID(), cartHeaderListDTO.getCustomerID(), 0, cart.getMaterialPriorityID(),
                                                            cart.getTotalPrice(), cart.getOfferValue(), cart.getOfferItemCartDetailsID(),
                                                            cart.getDiscountID(), cart.getDiscountText(), cart.getGST(), cart.getTax(), cart.getSubTotal(), cart.getHSNCode()));
                                                }

                                            }

                                        }

                                        for (int i = 0; i < cartHeadersList.size(); i++) {
                                            db.cartHeaderDAO().updateShipToPatryAndIsPriority(cartHeadersList.get(i).customerID, cartHeadersList.get(i).shipToPartyId, cartHeadersList.get(i).isPriority);
                                        }

                                        Calendar calendar = Calendar.getInstance();
                                        long mills = calendar.getTimeInMillis();

                                        sharedPreferencesUtils.savePreference("timer", mills);
                                        long timer = sharedPreferencesUtils.loadPreferenceAsLong("timer");
                                        ((MainActivity) getActivity()).startTime(300000 - (mills - timer));

                                        viewDialog.hideDialog();
                                        if(!sMaterailId.isEmpty() && !sPartnerId.isEmpty()){
                                            if(db.cartDetailsDAO().getMaterialCount(sPartnerId,sMaterailId)==0){
                                                SnackbarUtils.showSnackbarLengthShort(coordinatorLayout, "Item is not available in your supply chain network.", ContextCompat.getColor(getActivity(), R.color.dark_red), Snackbar.LENGTH_SHORT);
                                            }else{
                                                updateCartItemsCount();
                                                SnackbarUtils.showSnackbarLengthShort(coordinatorLayout, "Item added to cart", ContextCompat.getColor(getActivity(), R.color.dark_green), Snackbar.LENGTH_SHORT);
                                            }
                                        }

                                    }else {
                                        viewDialog.hideDialog();
                                        SnackbarUtils.showSnackbarLengthShort(coordinatorLayout, "Item is not available in your supply chain network.", ContextCompat.getColor(getActivity(), R.color.dark_red), Snackbar.LENGTH_SHORT);
                                    }
                                } catch (JSONException e) {
                                    viewDialog.hideDialog();
                                }



                                /*BottomNavigationMenuView menuView = (BottomNavigationMenuView) ((BottomNavigationView) getActivity().findViewById(R.id.navigation)).getChildAt(0);
                                BottomNavigationItemView itemView = (BottomNavigationItemView) menuView.getChildAt(1);

                                View notificationBadge = LayoutInflater.from(getActivity()).inflate(R.layout.notification_badge, menuView, false);
                                TextView textView = notificationBadge.findViewById(R.id.counter_badge);
                                textView.setText(String.valueOf(db.cartDetailsDAO().getCartDetailsCountIsApproved()));
                                itemView.addView(notificationBadge);*/





/*                                Intent i = new Intent(getActivity(), CartActivity.class);
                                i.putExtra(KeyValues.IS_ITEM_ADDED_TO_CART, true);
                                startActivity(i);*/

                            } catch (Exception e) {
                                viewDialog.hideDialog();
                                Toast.makeText(getActivity(), "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }

                        ProgressDialogUtils.closeProgressDialog();
                    }
                    ProgressDialogUtils.closeProgressDialog();
                }

                // response object fails
                @Override
                public void onFailure(Call<OMSCoreMessage> call, Throwable throwable) {
                    viewDialog.hideDialog();
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


    private void updateCartItemsCount() {

        SnackbarUtils.showSnackbarLengthShort(coordinatorLayout, "Item added to cart", ContextCompat.getColor(getActivity(), R.color.dark_green), Snackbar.LENGTH_SHORT);

        BottomNavigationMenuView menuView = (BottomNavigationMenuView) ((BottomNavigationView) getActivity().findViewById(R.id.navigation)).getChildAt(0);
        BottomNavigationItemView itemView = (BottomNavigationItemView) menuView.getChildAt(1);

        View notificationBadge = LayoutInflater.from(getActivity()).inflate(R.layout.notification_badge, menuView, false);
        TextView textView = notificationBadge.findViewById(R.id.counter_badge);
        textView.setText(String.valueOf(db.cartDetailsDAO().getCartDetailsCountIsApproved()));
        itemView.addView(notificationBadge);

    }


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void showAvailableOffers(int pos) {

        final Dialog mDialog = new Dialog(getActivity());
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialog.setCancelable(true);
        mDialog.setContentView(R.layout.dialog_offer);
        RecyclerView lstoffers = (RecyclerView) mDialog.findViewById(R.id.lstoffers);
        NestedScrollView nested = (NestedScrollView) mDialog.findViewById(R.id.nested);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        lstoffers.setLayoutManager(linearLayoutManager);
        lstoffers.setHasFixedSize(true);


        List<String> mCodeName = db.variantDAO().getMCode(Integer.parseInt(itemsList.get(pos).modelID));
        List<String> mDiscountDesc = db.variantDAO().getDiscountDesc(Integer.parseInt(itemsList.get(pos).modelID));

        OffersAdapter adapter = new OffersAdapter(getActivity(), mDiscountDesc, mCodeName, nested);
        lstoffers.setAdapter(adapter);
        lstoffers.setNestedScrollingEnabled(false);

        mDialog.show();

    }

    public void navigateToItemDetails(int pos) {
        postion_value = pos;
        Bundle bundle = new Bundle();
        bundle.putString(KeyValues.MODEL_ID, itemsList.get(pos).modelID);
        bundle.putString(KeyValues.MATERIAL_DIVISION_ID, itemsList.get(pos).divisionID);
        bundle.putString(KeyValues.CUSTOMER_ID, customerId);
        ProductDetailsFragment productDetailsFragment = new ProductDetailsFragment();
        productDetailsFragment.setArguments(bundle);
        FragmentUtils.addFragmentWithBackStack(getActivity(), R.id.container, productDetailsFragment);
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

        Picasso.with(getActivity())
                .load(selectedVariantImage)
                .placeholder(R.drawable.no_img)
                .into(ivItemBottom);

        txtItemNameBottom.setText(String.valueOf(variantTable.mCode));

        UserDivisionCustTable division;
        userDivisionCustTable = db.userDivisionCustDAO().getPartner(variantTable.divisionID);
        if (userDivisionCustTable != null) {
            partnerId = userDivisionCustTable.customerId;
            division = db.userDivisionCustDAO().getDivision(variantTable.divisionID);
        } else {
            division = null;
        }

        if (division != null && !division.equals("")) {
            isCustomerMatched = true;
        } else {
            isCustomerMatched = false;
        }

        materialId = variantTable.materialID;
        stackSize = (variantTable.stackSize != NULL) ? variantTable.stackSize : 1;
        etQtyBottom.setText("" + stackSize);

        cbPriority.setChecked(false);
        String isPriority = db.cartHeaderDetailsDao().getIsPriority(variantTable.materialID);
        if (isPriority.equals("1")) {
            cbPriority.setEnabled(false);
            cbPriority.setChecked(true);
        } else {
            cbPriority.setEnabled(true);
            cbPriority.setChecked(false);
        }

        etPrice.setText("");
        if (NetworkUtils.isInternetAvailable(getActivity())) {
            getPrice();
        }


    }

    protected void showShipToPartyDialog() {

        LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
        View promptView = layoutInflater.inflate(R.layout.shiptoparty_dialog, null);

        List<CustomerTable> getCustomerNames;
        List<String> customerCodes;
        final List<String> customerIds;


/*        customerCodes = db.customerDAO().getAllCustomerCodesName();

        ArrayAdapter adapter = new ArrayAdapter<String>(getActivity(), R.layout.row_text, customerCodes);
        final AutoCompleteTextView shipToPartyAutoComplete = (AutoCompleteTextView) promptView.findViewById(R.id.shipToPartyAutoComplete);*/

        //getCustomerNames = db.customerDAO().getCustomerNames();
        customerCodes = db.customerDAO().getCustomerCodes();
        customerIds = db.customerDAO().getCustomerId();
/*        for (int i = 0; i < getCustomerNames.size(); i++) {
            customerCodes.add(getCustomerNames.get(i).customerName);
            customerIds.add(getCustomerNames.get(i).customerId);
        }*/

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
                .setPositiveButton("OK", null)
                //Set to null. We override the onclick
                //.setNegativeButton("CLEAR", null)
                .create();

        d.setOnShowListener(new DialogInterface.OnShowListener() {

            @Override
            public void onShow(final DialogInterface dialog) {

                Button b = d.getButton(AlertDialog.BUTTON_POSITIVE);
                b.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {

                        if (!customerId.isEmpty() || customerId != null) {
                            sharedPreferencesUtils.savePreference(KeyValues.SELECTED_CUSTOMER_ID_GLOBAL, customerId);
/*                            Bundle bundle = new Bundle();
                            bundle.putString("customerId", customerId);
                            FragmentUtils.replaceFragmentWithBackStackWithBundle(getActivity(), R.id.container, new ProductCatalogFragment(), bundle);*/
                            postion_value = 0;
                            pagecount = 0;
                            try {
                                itemsList = new ArrayList<>();
                                recyclerView.setAdapter(null);
                                getArguments().remove("customerId");
                            } catch (NullPointerException ex) {
                                //
                            }

                            loadFormControl();

                        } else {
                            Toast.makeText(getActivity(), "No customer are there", Toast.LENGTH_SHORT).show();
                        }
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
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(getString(R.string.toolbar_ItemList));
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayUseLogoEnabled(false);
        ((MainActivity) getActivity()).SetNavigationVisibility(true);
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.llSort:
                if (isDesc) {
                    isDesc = false;
                    ivSort.setRotation(ivSort.getRotation() + 180);
                    pagecount = 0;
                    itemsList = new ArrayList<>();
                    ProgressDialogUtils.showProgressDialog("Please wait..");
                    recyclerView.setAdapter(null);
                    flag = 2;
                    if (customerId.equals("") || customerId.isEmpty())
                        updateToRecyclerView(db.itemDAO().getAllItemsASCAll(pagecount), 0);
                    else
                        updateToRecyclerView(db.itemDAO().getAllItemsASC(pagecount, customerId), 0);
                } else {
                    isDesc = true;
                    ivSort.setRotation(ivSort.getRotation() + 180);
                    pagecount = 0;
                    itemsList = new ArrayList<>();
                    ProgressDialogUtils.showProgressDialog("Please wait..");
                    recyclerView.setAdapter(null);
                    flag = 1;
                    if (customerId.equals("") || customerId.isEmpty())
                        updateToRecyclerView(db.itemDAO().getAllItemsDESCAll(pagecount), 0);
                    else
                        updateToRecyclerView(db.itemDAO().getAllItemsDESC(pagecount, customerId), 0);
                }

                break;

            case R.id.txtViewType:
                pagecount = 0;
                if (isgrid) {
                    isgrid = false;
                    ivViewType.setImageResource(R.drawable.grid);
                    layoutManager = new LinearLayoutManager(getActivity());
                    recyclerView.setLayoutManager(layoutManager);
                } else {
                    isgrid = true;
                    ivViewType.setImageResource(R.drawable.listview);
                    layoutManager = new GridLayoutManager(getActivity(), 2);
                    recyclerView.setLayoutManager(layoutManager);
                }
                itemsList = new ArrayList<>();
                if (flag == 0) {
                    if (customerId.equals("") || customerId.isEmpty())
                        updateToRecyclerView(db.itemDAO().getAllItemsAll(pagecount), 0);
                    else
                        updateToRecyclerView(db.itemDAO().getAllItems(pagecount, customerId), 0);
                } else if (flag == 1) {
                    if (customerId.equals("") || customerId.isEmpty())
                        updateToRecyclerView(db.itemDAO().getAllItemsDESCAll(pagecount), 0);
                    else
                        updateToRecyclerView(db.itemDAO().getAllItemsDESC(pagecount, customerId), 0);
                } else {
                    if (customerId.equals("") || customerId.isEmpty())
                        updateToRecyclerView(db.itemDAO().getAllItemsASCAll(pagecount), 0);
                    else
                        updateToRecyclerView(db.itemDAO().getAllItemsASC(pagecount, customerId), 0);
                }

//                if(isDesc){
//                    updateToRecyclerView(db.itemDAO().getDescendingList());
//                }else {
//                    updateToRecyclerView(db.itemDAO().getAllItems(pagecount));
//                }


                break;
        }
    }

    public void setProgressDialog() {

        int llPadding = 30;
        LinearLayout ll = new LinearLayout(getActivity());
        ll.setOrientation(LinearLayout.HORIZONTAL);
        ll.setPadding(llPadding, llPadding, llPadding, llPadding);
        ll.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams llParam = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        llParam.gravity = Gravity.CENTER;
        ll.setLayoutParams(llParam);

        ProgressBar progressBar = new ProgressBar(getActivity());
        progressBar.setIndeterminate(true);
        progressBar.setPadding(0, 0, llPadding, 0);
        progressBar.setLayoutParams(llParam);

        llParam = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        llParam.gravity = Gravity.CENTER;
        TextView tvText = new TextView(getActivity());
        tvText.setText("Please wait..");
        tvText.setTextColor(Color.parseColor("#000000"));
        tvText.setTextSize(20);
        tvText.setLayoutParams(llParam);

        ll.addView(progressBar);
        ll.addView(tvText);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
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

    public int stackSizeQty(int qty, int size) {
        return (qty != 0) ? ((qty % size) != 0) ? ((qty / size) * size) + size : ((qty / size) * size) : size;
    }

    public static class PaginationAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private static final int ITEM = 0;
        private static final int LOADING = 1;
        OnItemClickListener listener;
        private Context context;
        private boolean isgrid;
        private FragmentActivity activity;
        private boolean isLoadingAdded = false;

        public PaginationAdapter(Context applicationContext, OnItemClickListener mlistener, boolean isgrid, FragmentActivity activity) {
            this.context = applicationContext;
            listener = mlistener;
            this.isgrid = isgrid;
            this.activity = activity;
        }

        public PaginationAdapter(boolean isgrid) {
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            RecyclerView.ViewHolder viewHolder = null;
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());

            switch (viewType) {
                case ITEM:
                    viewHolder = getViewHolder(parent, inflater);
                    break;
                case LOADING:
                    View v2 = inflater.inflate(R.layout.progress, parent, false);
                    viewHolder = new LoadingVH(v2);
                    break;
            }
            return viewHolder;
        }


        @NonNull
        private RecyclerView.ViewHolder getViewHolder(ViewGroup parent, LayoutInflater inflater) {
            RecyclerView.ViewHolder viewHolder;

            View view;
            if (isgrid)
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_product_catalog_grid, parent, false);
            else
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_product_catalog, parent, false);

            viewHolder = new ItemListView(view);
            return viewHolder;
        }


        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

            ItemTable result = itemsList.get(position); // Item

            switch (getItemViewType(position)) {
                case ITEM:
                    final ItemListView itemListView = (ItemListView) holder;

                    itemListView.txtItemName.setText(result.modelCode);
                    itemListView.txtItemDesc.setText(result.modelDescription);
                    //itemListView.txtPrice.setText("Rs" + " " + result.price + "/-");
                    if (result.imgPath.equals("")) {
                        itemListView.ivItem.setImageResource(R.drawable.no_img);
                    } else {
                        Picasso.with(context)
                                .load(result.imgPath)
                                .placeholder(R.drawable.no_img)
                                .into(itemListView.ivItem);
                    }

                    if (NetworkUtils.isInternetAvailable(context)) {

                        if (!result.discountCount.isEmpty() && !result.discountCount.equalsIgnoreCase("0")) {
                            itemListView.tvScheme.setClickable(true);
                            itemListView.tvScheme.setVisibility(View.VISIBLE);
                        } else {
                            itemListView.tvScheme.setVisibility(View.GONE);
                        }

                    } else {
                        itemListView.tvScheme.setVisibility(View.GONE);
                    }

                    break;
                case LOADING:
                    break;
            }

        }

        @Override
        public int getItemCount() {
            return itemsList == null ? 0 : itemsList.size();
        }

        @Override
        public int getItemViewType(int position) {
            return (position == itemsList.size() - 1 && isLoadingAdded) ? LOADING : ITEM;
        }

    /*
   Helpers
   _________________________________________________________________________________________________
    */

        public void add(final ItemTable r) {
            itemsList.add(r);
            notifyItemInserted(itemsList.size() - 1);
        }

        public void addAll(List<ItemTable> itemTables) {
            for (ItemTable result : itemTables) {
                add(result);
            }
        }

        public void remove(ItemTable r) {
            int position = itemsList.indexOf(r);
            if (position > -1) {
                itemsList.remove(position);
                notifyItemRemoved(position);
            }
        }

        public void clear() {
            isLoadingAdded = false;
            while (getItemCount() > 0) {
                remove(getItem(0));
            }
        }

        public boolean isEmpty() {
            return getItemCount() == 0;
        }


        public void addLoadingFooter() {
            isLoadingAdded = true;
            add(new ItemTable());
        }

        public void removeLoadingFooter() {
            isLoadingAdded = false;

            int position = itemsList.size() - 1;
            ItemTable result = getItem(position);

            if (result != null) {
                itemsList.remove(position);
                notifyItemRemoved(position);
            }
        }

        public ItemTable getItem(int position) {
            return itemsList.get(position);
        }


   /*
   View Holders
   _________________________________________________________________________________________________
    */

        // Item click listener interface
        public interface OnItemClickListener {

            void onItemClick(int pos);

            void onCartClick(int pos);

            void onSchemeClick(int pos);

        }

        /**
         * Main list's content ViewHolder
         */
        public class ItemListView extends RecyclerView.ViewHolder {
            private TextView txtItemName, txtItemDesc, txtPrice, txtDiscount, tvScheme;
            private ImageView ivItem;
            private Button ivAddToCart;


            public ItemListView(View view) {
                super(view);
                // Initializing Views
                txtItemName = (TextView) view.findViewById(R.id.txtItemName);
                txtItemDesc = (TextView) view.findViewById(R.id.txtItemDesc);
                txtPrice = (TextView) view.findViewById(R.id.txtPrice);
                txtDiscount = (TextView) view.findViewById(R.id.txtDiscount);
                ivItem = (ImageView) view.findViewById(R.id.ivItem);
                ivAddToCart = (Button) view.findViewById(R.id.ivAddToCart);
                tvScheme = (TextView) view.findViewById(R.id.tvScheme);

                //on item click
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (listener != null) {
                            int pos = getAdapterPosition();
                            if (pos != RecyclerView.NO_POSITION) {
                                listener.onItemClick(pos);
                            }
                        }
                    }

                });

                ivAddToCart.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (listener != null) {
                            int pos = getAdapterPosition();
                            if (pos != RecyclerView.NO_POSITION) {
                                listener.onCartClick(pos);
                            }
                        }

                    }
                });

                tvScheme.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (listener != null) {
                            int pos = getAdapterPosition();
                            if (pos != RecyclerView.NO_POSITION) {
                                listener.onSchemeClick(pos);
                            }
                        }

                    }
                });


            }
        }

        protected class LoadingVH extends RecyclerView.ViewHolder {
            public LoadingVH(View itemView) {
                super(itemView);
            }
        }
    }

}

