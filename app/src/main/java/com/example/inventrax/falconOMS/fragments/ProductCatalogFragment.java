package com.example.inventrax.falconOMS.fragments;

/*
 * Created by Padmaja on 04/07/2019.
 */

import android.app.DatePickerDialog;
import android.arch.persistence.room.Room;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.inventrax.falconOMS.R;
import com.example.inventrax.falconOMS.activities.MainActivity;
import com.example.inventrax.falconOMS.adapters.PaginationAdapter;
import com.example.inventrax.falconOMS.common.Common;
import com.example.inventrax.falconOMS.common.Log.Logger;
import com.example.inventrax.falconOMS.common.constants.ErrorMessages;
import com.example.inventrax.falconOMS.pojos.OMSCoreMessage;
import com.example.inventrax.falconOMS.pojos.VariantDTO;
import com.example.inventrax.falconOMS.room.AppDatabase;
import com.example.inventrax.falconOMS.room.ItemTable;
import com.example.inventrax.falconOMS.services.RestService;
import com.example.inventrax.falconOMS.util.Converters;
import com.example.inventrax.falconOMS.util.DateUtils;
import com.example.inventrax.falconOMS.util.ExceptionLoggerUtils;
import com.example.inventrax.falconOMS.util.FragmentUtils;
import com.example.inventrax.falconOMS.util.ProgressDialogUtils;
import com.example.inventrax.falconOMS.util.searchableSpinner.SearchableSpinner;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ProductCatalogFragment extends Fragment implements SearchView.OnQueryTextListener {

    private View rootView;
    private static final String classCode = "OMS_Android_ProductCatalog";

    private FrameLayout frame;
    private RecyclerView recyclerView;

    //private SwipeRefreshLayout swipeContainer;
    private CardView txtViewType;
    private ImageView ivViewType;

    private ImageView ivAddToCartBottom, ivItemBottom;
    BottomSheetBehavior behavior;
    CoordinatorLayout coordinatorLayout;
    private SearchableSpinner spinnerColorSelectionBottom;
    private TextView txtItemNameBottom;
    private EditText etQtyBottom,deliveryDatePicker;

    private PaginationAdapter mAdapter;

    private Common common;
    private ExceptionLoggerUtils exceptionLoggerUtils;
    private ErrorMessages errorMessages;
    private Gson gson;
    RestService restService;
    private OMSCoreMessage core;
    List<ItemTable> items;

    private boolean isgrid = false;


    boolean loading = true;
    int pastVisiblesItems, visibleItemCount, totalItemCount;

    LinearLayoutManager layoutManager;
    int pagecount = 0;

    AppDatabase db;
    String myFormat = "dd/MMM/yyyy";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.productcatalog_fragment, container, false);

        loadFormControl();


        return rootView;
    }

    private void loadFormControl() {


        // To enable Bottom navigation bar
        ((MainActivity) getActivity()).SetNavigationVisibiltity(true);

        frame = (FrameLayout) rootView.findViewById(R.id.frame);

        recyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.smoothScrollToPosition(0);

        mAdapter = new PaginationAdapter(isgrid);

        db = Room.databaseBuilder(getActivity(),
                AppDatabase.class, "room_oms").allowMainThreadQueries().build();


        coordinatorLayout = (CoordinatorLayout) rootView.findViewById(R.id.coordinatorLayout);
        ivAddToCartBottom = (ImageView) rootView.findViewById(R.id.ivAddToCartBottom);
        ivItemBottom = (ImageView) rootView.findViewById(R.id.ivItemBottom);
        txtItemNameBottom = (TextView) rootView.findViewById(R.id.txtItemNameBottom);
        etQtyBottom = (EditText) rootView.findViewById(R.id.etQtyBottom);
        deliveryDatePicker = (EditText) rootView.findViewById(R.id.deliveryDatePicker);

        spinnerColorSelectionBottom = (SearchableSpinner) rootView.findViewById(R.id.spinnerColorSelectionBottom);

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
        ivViewType = (ImageView) rootView.findViewById(R.id.ivViewType);


        if (!db.itemDAO().getAllItems(pagecount).isEmpty()) {
            layoutManager = new LinearLayoutManager(getContext());
            recyclerView.setLayoutManager(layoutManager);
            // shows the items which are there in sqlite item table
            updateToRecyclerView(db.itemDAO().getAllItems(pagecount));
        } else {
            layoutManager = new LinearLayoutManager(getContext());
            recyclerView.setLayoutManager(layoutManager);

        }

        // when user changes view from linear layout list to grid layout
        isViewChanged();


        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
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
                            updateToRecyclerView(db.itemDAO().getAllItems(pagecount));
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
        });


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
                    Toast.makeText(getContext(),"Date specified [" + selectedDate + "] is before today [" + today + "]",Toast.LENGTH_SHORT).show();
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
                DatePickerDialog datePickerDialog =  new DatePickerDialog(getContext(), datePickerListener, myCalendar
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

                List<VariantDTO> varients = Converters.fromString(items.get(pos).varientList);

                for(VariantDTO var: varients){

                   varient.add(var.getMcode());

                }

                ArrayAdapter adapter = new ArrayAdapter(getContext(), R.layout.support_simple_spinner_dropdown_item, varient);
                spinnerColorSelectionBottom.setAdapter(adapter);

                if(!items.get(pos).imgPath.equals("")){
                    Picasso.with(getContext())
                            .load(items.get(pos).imgPath)
                            .placeholder(R.drawable.no_img)
                            .into(ivItemBottom);
                }else {
                    ivItemBottom.setImageResource(R.drawable.no_img);
                }



                ivAddToCartBottom.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        FragmentUtils.replaceFragmentWithBackStack(getActivity(), R.id.container, new CartFragment());

                        behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                        frame.setVisibility(View.GONE);

                    }
                });
            }
        }, isgrid);


        mAdapter.addAll(items);
        recyclerView.setAdapter(mAdapter);
        recyclerView.scrollToPosition(pagecount * 50);

        ProgressDialogUtils.closeProgressDialog();

    }

    public void isViewChanged() {
        txtViewType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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

                updateToRecyclerView(db.itemDAO().getAllItems(pagecount));
            }
        });
    }

    public void navigateToItemDetails(int pos) {

        Bundle bundle = new Bundle();
        bundle.putSerializable("modelItems", items.get(pos));
        ProductDetailsFragment productDetailsFragment = new ProductDetailsFragment();
        productDetailsFragment.setArguments(bundle);
        FragmentUtils.replaceFragmentWithBackStack(getActivity(), R.id.container, productDetailsFragment);

    }


    @Override
    public void onResume() {
        super.onResume();

        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(getString(R.string.toolbar_ItemList));
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayUseLogoEnabled(false);

    }


}
