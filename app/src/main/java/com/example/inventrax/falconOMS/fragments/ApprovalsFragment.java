package com.example.inventrax.falconOMS.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.inventrax.falconOMS.R;
import com.example.inventrax.falconOMS.model.KeyValues;
import com.example.inventrax.falconOMS.util.FragmentUtils;

public class ApprovalsFragment extends Fragment implements View.OnClickListener {

    private static final String classCode = "OMS_Android_ApprovalsFragment";
    private View rootView;
    CardView cvOpenPrice, cvInActive, cvCreditLimit, cvSchemesAndDiscounts, cvSCMApproval;
  /*  String[] strings = {KeyValues.OPEN_PRICE_APPROVAL,KeyValues.IN_ACTIVE_APPROVAL,
            KeyValues.CREDIT_LIMIT_APPROVAL,KeyValues.S_AND_D_APPROVAL,KeyValues.SCM_APPROVAL};*/
  String[] strings = {KeyValues.OPEN_PRICE_APPROVAL,KeyValues.IN_ACTIVE_APPROVAL,
            KeyValues.CREDIT_LIMIT_APPROVAL,KeyValues.S_AND_D_APPROVAL};

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.approvals_fragment, container, false);
        try {
            loadFormControls();
        } catch (Exception e) {
            //
        }
        return rootView;
    }

    private void loadFormControls() {


        cvOpenPrice = rootView.findViewById(R.id.cvOpenPrice);
        cvInActive = rootView.findViewById(R.id.cvInActive);
        cvCreditLimit = rootView.findViewById(R.id.cvCreditLimit);
        cvSchemesAndDiscounts = rootView.findViewById(R.id.cvSchemesAndDiscounts);
        cvSCMApproval = rootView.findViewById(R.id.cvSCMApproval);

        cvOpenPrice.setVisibility(View.GONE);
        cvInActive.setVisibility(View.GONE);
        cvCreditLimit.setVisibility(View.GONE);
        cvSchemesAndDiscounts.setVisibility(View.GONE);
        cvSCMApproval.setVisibility(View.GONE);

        for (String s : strings) {
            if (s.equals(KeyValues.OPEN_PRICE_APPROVAL))
                cvOpenPrice.setVisibility(View.VISIBLE);
            if (s.equals(KeyValues.IN_ACTIVE_APPROVAL))
                cvInActive.setVisibility(View.VISIBLE);
            if (s.equals(KeyValues.CREDIT_LIMIT_APPROVAL))
                cvCreditLimit.setVisibility(View.VISIBLE);
            if (s.equals(KeyValues.S_AND_D_APPROVAL))
                cvSchemesAndDiscounts.setVisibility(View.VISIBLE);
            if (s.equals(KeyValues.SCM_APPROVAL))
                cvSCMApproval.setVisibility(View.VISIBLE);
        }


        cvOpenPrice.setOnClickListener(this);
        cvInActive.setOnClickListener(this);
        cvCreditLimit.setOnClickListener(this);
        cvSchemesAndDiscounts.setOnClickListener(this);
        cvSCMApproval.setOnClickListener(this);

    }


    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.cvOpenPrice:
                Bundle bundle = new Bundle();
                bundle.putString("type", "4");
                FragmentUtils.replaceFragmentWithBackStackWithBundle(getActivity(), R.id.container, new ApprovalsListFragment(), bundle);
                break;

            case R.id.cvInActive:
                Bundle bundle1 = new Bundle();
                bundle1.putString("type", "6");
                FragmentUtils.replaceFragmentWithBackStackWithBundle(getActivity(), R.id.container, new ApprovalsListFragment(), bundle1);
                break;

            case R.id.cvSCMApproval:
                Bundle bundle5 = new Bundle();
                bundle5.putString("type", "23");
                FragmentUtils.replaceFragmentWithBackStackWithBundle(getActivity(), R.id.container, new ApprovalsListFragment(), bundle5);
                break;

            case R.id.cvCreditLimit:
                Bundle bundle2 = new Bundle();
                bundle2.putString("type", "5");
                FragmentUtils.replaceFragmentWithBackStackWithBundle(getActivity(), R.id.container, new ApprovalsListFragment(), bundle2);
                break;

            case R.id.cvSchemesAndDiscounts:
                FragmentUtils.replaceFragmentWithBackStack(getActivity(), R.id.container, new SADListFragment());
                break;

        }
    }

    @Override
    public void onResume() {
        super.onResume();
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(getString(R.string.toolbar_approvals));
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayUseLogoEnabled(false);
    }

}
