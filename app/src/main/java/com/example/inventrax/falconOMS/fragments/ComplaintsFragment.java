package com.example.inventrax.falconOMS.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.inventrax.falconOMS.R;

public class ComplaintsFragment extends Fragment implements View.OnClickListener {

    private static final String classCode = "OMS_Android_ComplaintsFragment";
    private View rootView;




    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.complaints_fragment, container, false);

        loadFormControls();

        return rootView;
    }

    private void loadFormControls() {


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
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(getString(R.string.toolbar_complaints));
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayUseLogoEnabled(false);
    }


}
