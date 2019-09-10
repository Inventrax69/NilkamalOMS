package com.example.inventrax.falconOMS.fragments;

/*
 * Created by Padmaja on 04/07/2019.
 */

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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
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
import com.example.inventrax.falconOMS.room.AppDatabase;
import com.example.inventrax.falconOMS.room.ItemTable;
import com.example.inventrax.falconOMS.services.RestService;
import com.example.inventrax.falconOMS.util.ExceptionLoggerUtils;
import com.example.inventrax.falconOMS.util.FragmentUtils;
import com.example.inventrax.falconOMS.util.ProgressDialogUtils;
import com.example.inventrax.falconOMS.util.searchableSpinner.SearchableSpinner;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

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
    private EditText etQtyBottom;

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
            Log.v("ABCDE",new Gson().toJson(db.itemDAO().getAllItems(pagecount)));
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
                    if (model.itemname.toLowerCase().contains(query.trim()) || model.itemdesc.toLowerCase().contains(query.trim())) {
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
                Toast.makeText(getContext(), String.valueOf(items.get(pos).itemname), Toast.LENGTH_SHORT).show();
                behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

                frame.setVisibility(View.GONE);
                //go to item details page
                navigateToItemDetails(pos);
            }

            @Override
            public void onCartClick(int pos) {

                behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                frame.setVisibility(View.VISIBLE);
                txtItemNameBottom.setText(String.valueOf(items.get(pos).itemname));

                ArrayList<String> varient = new ArrayList<>();
                varient.add("NA");
                varient.add("NA");
                varient.add("NA");
                varient.add("NA");
                varient.add("NA");
                varient.add("NA");

                ArrayAdapter adapter = new ArrayAdapter(getContext(), R.layout.support_simple_spinner_dropdown_item, varient);
                spinnerColorSelectionBottom.setAdapter(adapter);

                Picasso.with(getContext())
                        .load(items.get(pos).imageurl)
                        .placeholder(R.drawable.load)
                        .into(ivItemBottom);

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
        bundle.putString("itemName", String.valueOf(items.get(pos)));
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
