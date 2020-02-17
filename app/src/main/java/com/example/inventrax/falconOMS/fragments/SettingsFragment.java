package com.example.inventrax.falconOMS.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import com.example.inventrax.falconOMS.R;
import com.example.inventrax.falconOMS.activities.MainActivity;
import com.example.inventrax.falconOMS.util.FragmentUtils;

public class SettingsFragment extends Fragment implements View.OnClickListener {

    private static final String classCode = "OMS_Android_SettingsFragment";
    private View rootView;
    private Switch notificationSwitch;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.settings_fragment, container, false);

        loadFormControls();

        return rootView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(getActivity() != null){
            ((MainActivity)getActivity()).isSettingOpened=false;
        }
    }

    private void loadFormControls() {

        try {
            notificationSwitch = (Switch) rootView.findViewById(R.id.notificationSwitch);
            notificationSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                    if (isChecked) {
                        Intent intent = new Intent();
                        intent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");

                        //for Android 5-7

                        intent.putExtra("app_package", getActivity().getPackageName());
                        intent.putExtra("app_uid", getActivity().getApplicationInfo().uid);

                        // for Android 8 and above
                        intent.putExtra("android.provider.extra.APP_PACKAGE", getActivity().getPackageName());

                        startActivity(intent);
                    } else {
                        // The toggle is disabled
                    }
                }
            });


        } catch (Exception e) {
            Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
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
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(getString(R.string.toolbar_settings));
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayUseLogoEnabled(false);
    }


}
