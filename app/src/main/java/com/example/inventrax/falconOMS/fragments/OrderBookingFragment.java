package com.example.inventrax.falconOMS.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.inventrax.falconOMS.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class OrderBookingFragment extends Fragment implements View.OnClickListener {

    private static final String classCode = "OMS_Android_ComplaintsFragment";
    private View rootView;


    String json = "{\"NotificationTypes\":{\"4\":{\"Item\":{\"A\":\"1\"}}}}";
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.order_booking_fragment, container, false);

        loadFormControls();

        return rootView;
    }

    private void loadFormControls() {
        try {

            if(json!=null) {

                List<JSONObject> objects = new ArrayList<>();

                // get JSONObject from JSON file
                //JSONObject obj = new JSONObject(json);
                JSONObject obj;
                for (int i = 0;i<=json.length();i++){

                    obj = new JSONObject(json);

                    objects.add(obj);
                }

                /*// fetch JSONObject named employee
                String s = obj.getString("NotificationTypes");

                JSONObject obj1 = new JSONObject(s);
                // fetch JSONObject named employee
                String nType = s.substring(1,3).replaceAll("\"", "");

                if(nType.equalsIgnoreCase("4")){
                    Toast.makeText(getActivity(), "4", Toast.LENGTH_SHORT).show();
                    JSONObject obj2 = new JSONObject()

                }else if(nType.equalsIgnoreCase("3")){
                    Toast.makeText(getActivity(), "3", Toast.LENGTH_SHORT).show();
                }*/

            }

        } catch (JSONException e) {
            e.printStackTrace();
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
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(getString(R.string.toolbar_orderbooking));
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayUseLogoEnabled(false);
    }


}
