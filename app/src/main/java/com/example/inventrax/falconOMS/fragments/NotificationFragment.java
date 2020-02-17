package com.example.inventrax.falconOMS.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.inventrax.falconOMS.R;
import com.example.inventrax.falconOMS.adapters.NotificationListAdapter;
import com.example.inventrax.falconOMS.common.Common;
import com.example.inventrax.falconOMS.common.constants.EndpointConstants;
import com.example.inventrax.falconOMS.common.constants.ErrorMessages;
import com.example.inventrax.falconOMS.interfaces.ApiInterface;
import com.example.inventrax.falconOMS.model.KeyValues;
import com.example.inventrax.falconOMS.pojos.NotificationDTO;
import com.example.inventrax.falconOMS.pojos.OMSCoreMessage;
import com.example.inventrax.falconOMS.pojos.OMSExceptionMessage;
import com.example.inventrax.falconOMS.services.RestService;
import com.example.inventrax.falconOMS.util.DialogUtils;
import com.example.inventrax.falconOMS.util.FragmentUtils;
import com.example.inventrax.falconOMS.util.NetworkUtils;
import com.example.inventrax.falconOMS.util.ProgressDialogUtils;
import com.google.gson.Gson;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;

public class NotificationFragment extends Fragment implements View.OnClickListener {

    private static final String classCode = "OMS_Android_NotificationsFragment";
    private View rootView;

    private ListView notificationListView;

    private Common common;
    private OMSCoreMessage core;
    private List<NotificationDTO> notificationDTOList;
    private ErrorMessages errorMessages;

    String ID="";


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.notifications_fragment, container, false);

        loadFormControls();

        return rootView;
    }

    private void loadFormControls() {

        notificationListView = (ListView) rootView.findViewById(R.id.notificationListView);


        common = new Common();
        core = new OMSCoreMessage();
        errorMessages=new ErrorMessages();

        getNotifications();

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        try{

            if (getArguments().getString("ID") != null) {
                ID = getArguments().getString("ID");
            }

        }catch (Exception e){
            //
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        if (menu != null) {

            final MenuItem item = menu.findItem(R.id.action_home);
            item.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
            item.setVisible(true);

            final MenuItem item1 = menu.findItem(R.id.action_notification);
            item1.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
            item1.setVisible(false);

        }

        super.onCreateOptionsMenu(menu, inflater);

    }

    public void getNotifications() {

        OMSCoreMessage message = new OMSCoreMessage();
        message = common.SetAuthentication(EndpointConstants.Scalar, getActivity());
        message.setEntityObject(null);

        Call<OMSCoreMessage> call = null;
        ApiInterface apiService = RestService.getClient().create(ApiInterface.class);
        call = apiService.GetNotifications(message);
        ProgressDialogUtils.showProgressDialog("Please Wait");

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

                            if (core.getEntityObject() != null) {

                                JSONArray getCartHeader = new JSONArray((ArrayList) core.getEntityObject());
                                notificationDTOList = new ArrayList<>();
                                if (getCartHeader.length() > 0) {

                                    for (int i = 0; i < getCartHeader.length(); i++) {

                                        NotificationDTO notificationDTO = (NotificationDTO) new Gson().fromJson(String.valueOf(getCartHeader.getJSONObject(i)), NotificationDTO.class);
                                        notificationDTOList.add(notificationDTO);
                                    }

                                    NotificationListAdapter customAdapter = new NotificationListAdapter(getActivity(), notificationDTOList);
                                    notificationListView.setAdapter(customAdapter);


                                    SharedPreferences prefs = getActivity().getSharedPreferences("myPrefs", MODE_PRIVATE);
                                    SharedPreferences.Editor editor = prefs.edit();
                                    editor.putBoolean(KeyValues.IS_NOTIFICATION_AVAILABLE, false);
                                    editor.commit();

                                    notificationListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                        @Override
                                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                                            if(notificationDTOList.get(i).getNotifTriggerName().equals("OpenPrice Approval")){
                                                if(notificationDTOList.get(i).getLink().split("[/]").length==4){
                                                    Bundle bundle=new Bundle();
                                                    bundle.putString("type",notificationDTOList.get(i).getLink().split("[/]")[2]);
                                                    bundle.putString("cartHeaderId",notificationDTOList.get(i).getLink().split("[/]")[3]);
                                                    FragmentUtils.replaceFragmentWithBackStackWithBundle(getActivity(), R.id.container, new ApprovalsDetailsFragment(),bundle);
                                                }else{
                                                    Bundle bundle=new Bundle();
                                                    bundle.putString("type","4");
                                                    FragmentUtils.replaceFragmentWithBackStackWithBundle(getActivity(), R.id.container, new ApprovalsListFragment(),bundle);
                                                }
                                            }
                                            else if(notificationDTOList.get(i).getNotifTriggerName().equals("Credit Limit Approval")){
                                                Bundle bundle2=new Bundle();
                                                bundle2.putString("type","5");
                                                FragmentUtils.replaceFragmentWithBackStackWithBundle(getActivity(), R.id.container, new ApprovalsListFragment(),bundle2);
                                            }
                                            else if(notificationDTOList.get(i).getNotifTriggerName().equals("InActive Approval")){
                                                if(notificationDTOList.get(i).getLink().split("[/]").length==4){
                                                    Bundle bundle1=new Bundle();
                                                    bundle1.putString("type",notificationDTOList.get(i).getLink().split("[/]")[2]);
                                                    bundle1.putString("cartHeaderId",notificationDTOList.get(i).getLink().split("[/]")[3]);
                                                    FragmentUtils.replaceFragmentWithBackStackWithBundle(getActivity(), R.id.container, new ApprovalsDetailsFragment(),bundle1);
                                                }else{
                                                    Bundle bundle1=new Bundle();
                                                    bundle1.putString("type","6");
                                                    FragmentUtils.replaceFragmentWithBackStackWithBundle(getActivity(), R.id.container, new ApprovalsListFragment(),bundle1);
                                                }

                                            }
                                            else if(notificationDTOList.get(i).getNotifTriggerName().equals("Discount Approval")){
                                                FragmentUtils.replaceFragmentWithBackStack(getActivity(), R.id.container, new SADListFragment());
                                            }
                                        }
                                    });

                                    if(!ID.isEmpty()){
                                        for(int i=0;i<notificationDTOList.size();i++){
                                            if(ID.equals(String.valueOf(notificationDTOList.get(i).getNotifRequestID()))){
                                                notificationListView.performItemClick(
                                                        notificationListView.getAdapter().getView(i, null, null),
                                                        i, notificationListView.getAdapter().getItemId(i));
                                                ID="";
                                            }
                                        }
                                    }


                                }
                            }

                        } catch (Exception e) {

                        }

                        ProgressDialogUtils.closeProgressDialog();

                    }
                }
            }

            @Override
            public void onFailure(Call<OMSCoreMessage> call, Throwable t) {
                if(NetworkUtils.isInternetAvailable(getActivity())){
                    DialogUtils.showAlertDialog(getActivity(), errorMessages.EMC_0001);
                }else{
                    DialogUtils.showAlertDialog(getActivity(), errorMessages.EMC_0014);
                }
            }
        });


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
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(getString(R.string.toolbar_notifications));
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayUseLogoEnabled(false);
    }


}
