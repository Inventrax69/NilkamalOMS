package com.example.inventrax.falconOMS.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.inventrax.falconOMS.R;
import com.example.inventrax.falconOMS.util.FragmentUtils;

public class OrderAssistanceList extends Fragment implements View.OnClickListener {

    private static final String classCode = "OMS_Android_OrderAsstListFragment";
    private View rootView;
    private RecyclerView rvTickets;
    private FloatingActionButton btnAdd;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.order_history_fragment, container, false);

        loadFormControls();

        return rootView;
    }

    private void loadFormControls() {

        try {
            rvTickets = (RecyclerView) rootView.findViewById(R.id.rvTickets);
            btnAdd = (FloatingActionButton) rootView.findViewById(R.id.btnAdd);

        } catch (Exception e) {

        }

    }


    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.btnAdd:
                FragmentUtils.replaceFragmentWithBackStack(getActivity(), R.id.container, new OrderAssistanceFragment());
                break;
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
