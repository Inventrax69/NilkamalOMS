package com.example.inventrax.falconOMS.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.example.inventrax.falconOMS.R;

import java.util.ArrayList;

public class SearchFragment extends Fragment implements View.OnClickListener, SearchView.OnQueryTextListener {

    private static final String classCode = "OMS_Android_SearchFragment";
    private View rootView;

    SearchView.SearchAutoComplete searchAutoComplete;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_search, container, false);

        loadFormControls();

        return rootView;
    }

    private void loadFormControls() {


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
            final SearchView searchView = (SearchView) item.getActionView();
            searchView.setMaxWidth(android.R.attr.width);
            searchView.setOnQueryTextListener(this);

            final ArrayList<String> sugg = new ArrayList<>();
            sugg.add("ABC");
            sugg.add("ABF");
            sugg.add("GAB");
            sugg.add("BFL");


            searchAutoComplete = (SearchView.SearchAutoComplete)  searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);

            searchAutoComplete.setTextColor(Color.WHITE);

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(),
                    R.layout.support_simple_spinner_dropdown_item, sugg);

            searchAutoComplete.setAdapter(adapter);


            searchAutoComplete.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position,
                                        long id) {
                    // TODO Auto-generated method stub

                    String searchString=(String)parent.getItemAtPosition(position);
                    searchAutoComplete.setText(""+searchString);
                    Toast.makeText(getContext(), "you clicked "+searchString, Toast.LENGTH_LONG).show();

                }
            });

        }

        super.onCreateOptionsMenu(menu, inflater);

    }



    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String searchText) {



        return true;
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
