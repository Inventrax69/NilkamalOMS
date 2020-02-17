package com.example.inventrax.falconOMS.fragments;

import android.annotation.SuppressLint;
import android.app.SearchManager;
import android.app.Service;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.InputType;
import android.text.method.DigitsKeyListener;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.inventrax.falconOMS.R;
import com.example.inventrax.falconOMS.activities.MainActivity;
import com.example.inventrax.falconOMS.adapters.ItemAdapter;
import com.example.inventrax.falconOMS.common.Common;
import com.example.inventrax.falconOMS.common.constants.EndpointConstants;
import com.example.inventrax.falconOMS.common.constants.ErrorMessages;
import com.example.inventrax.falconOMS.interfaces.ApiInterface;
import com.example.inventrax.falconOMS.model.KeyValues;
import com.example.inventrax.falconOMS.pojos.OMSCoreMessage;
import com.example.inventrax.falconOMS.pojos.OMSExceptionMessage;
import com.example.inventrax.falconOMS.room.AppDatabase;
import com.example.inventrax.falconOMS.room.RoomAppDatabase;
import com.example.inventrax.falconOMS.room.VariantTable;
import com.example.inventrax.falconOMS.services.RestService;
import com.example.inventrax.falconOMS.util.DialogUtils;
import com.example.inventrax.falconOMS.util.ExceptionLoggerUtils;
import com.example.inventrax.falconOMS.util.FragmentUtils;
import com.example.inventrax.falconOMS.util.ProgressDialogUtils;

import org.json.JSONArray;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchFragment extends Fragment implements View.OnClickListener, SearchView.OnQueryTextListener {

    private static final String classCode = "OMS_Android_SearchFragment";
    private View rootView;
    SearchView.SearchAutoComplete searchAutoComplete;
    private RecyclerView recyclerView;
    LinearLayoutManager layoutManager;
    SearchView searchView;
    HashMap<String, Integer> hashMap;
    Common common;
    private OMSCoreMessage core;
    RestService restService;
    ErrorMessages errorMessages;
    AppDatabase db;
    int mmId;
    List<VariantTable> listVariants;
    MenuItem item;
    List<String> sugg;

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(getActivity() != null){
            ((MainActivity)getActivity()).isSearchOpened=false;
        }

        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Service.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(searchAutoComplete.getWindowToken(), 0);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_search, container, false);

        try{
            loadFormControls();
        }catch(Exception e){

        }

        return rootView;
    }

    private void loadFormControls() {
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        common = new Common();
        restService = new RestService();
        core = new OMSCoreMessage();
        errorMessages = new ErrorMessages();

        recyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        db = new RoomAppDatabase(getActivity()).getAppDatabase();


    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    SearchManager searchManager;


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
            searchView.setIconified(false);
            searchView.setFocusable(true);

            // Get SearchView autocomplete object.
            searchAutoComplete = (SearchView.SearchAutoComplete) searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
            searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
            searchAutoComplete.setKeyListener(DigitsKeyListener.getInstance("abcdefghijklmnopqrstuvwxyz 0123456789"));
            searchAutoComplete.setInputType(InputType.TYPE_CLASS_TEXT);
            searchAutoComplete.setTextColor(Color.WHITE);


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
                  //  Toast.makeText(getContext(), "Your search result is " + " " + searchString, Toast.LENGTH_LONG).show();
                    mmId = hashMap.get(searchString);
                    loadView();

                }
            });

        }

        super.onCreateOptionsMenu(menu, inflater);

    }


    private void loadView() {

        VariantTable variantTable = db.variantDAO().getMaterial(mmId);
        if(variantTable!=null){
            listVariants = new ArrayList<>();
            listVariants.add(variantTable);
            ItemAdapter itemAdapter = new ItemAdapter(getActivity(), listVariants, new ItemAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(int pos) {

                    int modelId = db.variantDAO().getModelId(mmId);
                    Bundle bundle = new Bundle();
                    bundle.putString(KeyValues.MODEL_ID, String.valueOf(modelId));
                    bundle.putInt(KeyValues.MID, mmId);
                    ProductDetailsFragment productDetailsFragment = new ProductDetailsFragment();
                    productDetailsFragment.setArguments(bundle);
                    FragmentUtils.replaceFragmentWithBackStack(getActivity(), R.id.container, productDetailsFragment);
                }
            });
            recyclerView.setAdapter(itemAdapter);
        }

    }


    // When enter pressed
    @Override
    public boolean onQueryTextSubmit(String searchString) {
        searchItems(searchString);
        searchView.setFocusable(true);
        searchAutoComplete.setFocusable(true);
        return false;
    }

    @Override
    public boolean onQueryTextChange(String searchText) {

        if (searchText != null && !searchText.equals("") && searchText.length()>=2) {

            if (!Character.isWhitespace(searchText.charAt(0))) {

                String lastChar = searchText.substring(searchText.length() - 1);
                if (lastChar.equals(" ")) {

                    searchText.trim();
                    searchItems(searchText);
                  /*  Toast.makeText(getContext(), "space bar pressed" + "" + searchText,
                            Toast.LENGTH_SHORT).show();*/
                }else {
                    searchItems(searchText);
                }
            } else {
                Toast.makeText(getContext(), "space at starting is not valid", Toast.LENGTH_SHORT).show();
                searchAutoComplete.setText("");
            }
        }

        return true;
    }

    ArrayAdapter<String> adapter;
    public void searchItems(String searchString) {

        OMSCoreMessage message = new OMSCoreMessage();
        message = common.SetAuthentication(EndpointConstants.LoginDTO, getActivity());
        message.setEntityObject(searchString);


        Call<OMSCoreMessage> call = null;
        ApiInterface apiService =
                RestService.getClient().create(ApiInterface.class);

        call = apiService.MMIntelliSearch(message);

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

                            JSONArray getCartHeader = null;
                            try {

                                searchAutoComplete.setAdapter(null);

                                sugg = new ArrayList<>();
                                hashMap = new HashMap<String, Integer>();

                                sugg.clear();
                                hashMap.clear();
                                if(adapter!=null) adapter.clear();

                                getCartHeader = new JSONArray((String) core.getEntityObject());

                                for (int i = 0; i < getCartHeader.length(); i++) {

                                    hashMap.put(getCartHeader.getJSONObject(i).getString("Label"), getCartHeader.getJSONObject(i).getInt("Value"));

                                }

                                sugg.addAll(hashMap.keySet());


                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        adapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_dropdown_item_1line, sugg);
                                        searchAutoComplete.setAdapter(adapter);
                                        adapter.notifyDataSetChanged();
                                    }
                                });

                            } catch (Exception e) {

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


    @Override
    public void onClick(View v) {
        switch (v.getId()) {

        }
    }

    @Override
    public void onResume() {
        super.onResume();
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(getString(R.string.toolbar_search));
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayUseLogoEnabled(false);
    }

}