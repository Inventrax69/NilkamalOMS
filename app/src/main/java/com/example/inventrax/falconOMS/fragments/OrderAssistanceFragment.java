package com.example.inventrax.falconOMS.fragments;

import android.Manifest;
import android.app.Dialog;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.inventrax.falconOMS.R;
import com.example.inventrax.falconOMS.activities.MainActivity;
import com.example.inventrax.falconOMS.common.Common;
import com.example.inventrax.falconOMS.common.constants.EndpointConstants;
import com.example.inventrax.falconOMS.common.constants.ErrorMessages;
import com.example.inventrax.falconOMS.interfaces.ApiInterface;
import com.example.inventrax.falconOMS.model.KeyValues;
import com.example.inventrax.falconOMS.pojos.FileUploadDTO;
import com.example.inventrax.falconOMS.pojos.OMSCoreMessage;
import com.example.inventrax.falconOMS.pojos.OMSExceptionMessage;
import com.example.inventrax.falconOMS.pojos.OrderAssistanceDTO;
import com.example.inventrax.falconOMS.room.AppDatabase;
import com.example.inventrax.falconOMS.services.RestService;
import com.example.inventrax.falconOMS.util.DialogUtils;
import com.example.inventrax.falconOMS.util.ExceptionLoggerUtils;
import com.example.inventrax.falconOMS.util.FragmentUtils;
import com.example.inventrax.falconOMS.util.MaterialDialogUtils;
import com.example.inventrax.falconOMS.util.NetworkUtils;
import com.example.inventrax.falconOMS.util.ProgressDialogUtils;
import com.example.inventrax.falconOMS.util.SnackbarUtils;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_OK;

public class OrderAssistanceFragment extends Fragment implements View.OnClickListener {

    private static final String classCode = "OMS_Android_OrderAssistanceFragment";
    private View rootView;
    private ImageView imgOne, imgTwo, imgThree;
    private CheckBox chkBoxOne, chkBoxTwo, chkBoxThree;
    private TextView txtBack, txtUploadImg;
    private CoordinatorLayout coordLayout;

    private FloatingActionButton btnOpenCamera;

    private int camCount = 4;
    private String imgStringOne = "", imgStringTwo = "", imgStringThree = "";

    private ErrorMessages errorMessages;
    private Gson gson;
    private OMSCoreMessage core;
    private Common common;
    RestService restService;

    List<FileUploadDTO> fileUploadString;
    FileUploadDTO fileUploadDTO;
    private String userId, cusomerIDs;
    private MaterialDialogUtils materialDialogUtils;

    AppDatabase db;
    List customerNames;
    HashMap<String, String> custHashMap;
    String selectedCust = "", selectedCustId="";


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.order_assistande_fragment, container, false);

        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA}, 100);

        } else {
            loadFormControls();
        }


        return rootView;
    }

    private void loadFormControls() {

        // To disable Bottom navigation bar
        ((MainActivity) getActivity()).SetNavigationVisibiltity(false);

        db = Room.databaseBuilder(getActivity(),
                AppDatabase.class, "room_oms").allowMainThreadQueries().build();

        SharedPreferences sp = getContext().getSharedPreferences(KeyValues.MY_PREFS, Context.MODE_PRIVATE);
        userId = sp.getString(KeyValues.USER_ID, "");
        cusomerIDs = sp.getString(KeyValues.CUSTOMER_IDS, "");

        custHashMap = new HashMap<>();

        JSONArray jsonArray = null;
        try {
            jsonArray = new JSONArray(cusomerIDs);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        customerNames = new ArrayList();


        for (int i = 0; i < jsonArray.length(); i++) {

            try {
                if(jsonArray.getString(i) != null) {

                    if(db.customerDAO().getCustomer(jsonArray.getString(i)).customerId != null && db.customerDAO().getCustomer(jsonArray.getString(i)).custName != null){
                        custHashMap.put(db.customerDAO().getCustomer(jsonArray.getString(i)).custName, db.customerDAO().getCustomer(jsonArray.getString(i)).customerId);
                    }

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


        coordLayout = (CoordinatorLayout) rootView.findViewById(R.id.coordinatorLayout);

        btnOpenCamera = (FloatingActionButton) rootView.findViewById(R.id.btnOpenCamera);
        btnOpenCamera.setOnClickListener(this);

        imgOne = (ImageView) rootView.findViewById(R.id.imgOne);
        imgTwo = (ImageView) rootView.findViewById(R.id.imgTwo);
        imgThree = (ImageView) rootView.findViewById(R.id.imgThree);

        chkBoxOne = (CheckBox) rootView.findViewById(R.id.chkBoxOne);
        chkBoxTwo = (CheckBox) rootView.findViewById(R.id.chkBoxTwo);
        chkBoxThree = (CheckBox) rootView.findViewById(R.id.chkBoxThree);

        txtUploadImg = (TextView) rootView.findViewById(R.id.txtUploadImg);
        txtBack = (TextView) rootView.findViewById(R.id.txtBack);

        txtBack.setOnClickListener(this);
        txtUploadImg.setOnClickListener(this);

        chkBoxOne.setEnabled(false);
        chkBoxTwo.setEnabled(false);
        chkBoxThree.setEnabled(false);

        errorMessages = new ErrorMessages();
        common = new Common();
        restService = new RestService();
        core = new OMSCoreMessage();
        fileUploadString = new ArrayList<>();
        fileUploadDTO = new FileUploadDTO();
        materialDialogUtils = new MaterialDialogUtils();

        showCustomersDailog();
    }


    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btnOpenCamera:

                if(!selectedCustId.isEmpty() || !selectedCust.equals("")) {

                    camCount--;

                    if (camCount == 0) {

                        return;
                    }

                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    File f = new File(android.os.Environment.getExternalStorageDirectory(), "temp.jpg");
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
                    startActivityForResult(intent, 1);

                }else {
                    SnackbarUtils.showSnackbar(coordLayout, getString(R.string.selectCust), Snackbar.LENGTH_SHORT);
                    showCustomersDailog();
                }

                break;

            case R.id.txtUploadImg:

                if (chkBoxOne.isChecked() || chkBoxTwo.isChecked() || chkBoxThree.isChecked()) {


                    if (chkBoxOne.isChecked()) {
                        prepareUploadData(imgStringOne);
                    }
                    if (chkBoxTwo.isChecked()) {
                        prepareUploadData(imgStringTwo);
                    }
                    if (chkBoxThree.isChecked()) {
                        prepareUploadData(imgStringThree);
                    }

                    orderAssistanceUpload();

                } else {
                    SnackbarUtils.showSnackbar(coordLayout, getString(R.string.imgUploadAlert), Snackbar.LENGTH_SHORT);
                }

                break;

            case R.id.txtBack:

                FragmentUtils.replaceFragmentWithBackStack(getActivity(), R.id.container, new HomeFragmentHints());

                break;
        }

    }

    public void prepareUploadData(String imgString) {
        fileUploadDTO = new FileUploadDTO();
        Long tsLong = System.currentTimeMillis() / 1000;
        fileUploadDTO.setName(tsLong.toString() + ".jpg");
        fileUploadDTO.setBase64(imgString);
        fileUploadString.add(fileUploadDTO);
    }

    public void orderAssistanceUpload() {

        if (NetworkUtils.isInternetAvailable(getContext())) {
        } else {
            DialogUtils.showAlertDialog(getActivity(), errorMessages.EMC_0007);
            // soundUtils.alertSuccess(LoginActivity.this,getBaseContext());
            return;
        }


        OMSCoreMessage message = new OMSCoreMessage();
        message = common.SetAuthentication(EndpointConstants.OrderAssistance_DTO, getContext());
        OrderAssistanceDTO oDto = new OrderAssistanceDTO();
        oDto.setPartnerID(selectedCustId);
        oDto.setFileUpload(fileUploadString);
        message.setEntityObject(oDto);


        Call<OMSCoreMessage> call = null;
        ApiInterface apiService =
                RestService.getClient().create(ApiInterface.class);


        call = apiService.OrderAssistanceUpload(message);
        ProgressDialogUtils.showProgressDialog("Please Wait");


        try {
            //Getting response from the method
            call.enqueue(new Callback<OMSCoreMessage>() {

                @Override
                public void onResponse(Call<OMSCoreMessage> call, Response<OMSCoreMessage> response) {
                    ProgressDialogUtils.closeProgressDialog();
                    if (response.body() != null) {

                        core = response.body();

                        if (core != null) {

                            if ((core.getType().toString().equals("Exception"))) {

                                OMSExceptionMessage omsExceptionMessage = null;

                                for (OMSExceptionMessage oms : core.getOMSMessages()) {

                                    omsExceptionMessage = oms;
                                    ProgressDialogUtils.closeProgressDialog();
                                    common.showAlertType(omsExceptionMessage, getActivity(), getContext());
                                }


                            } else {

                                MaterialDialogUtils.showUploadSuccessDialog(getContext(), "Done");
                                ProgressDialogUtils.closeProgressDialog();
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
                    Toast.makeText(getContext(), throwable.toString(), Toast.LENGTH_LONG).show();
                    ProgressDialogUtils.closeProgressDialog();
                }
            });
        } catch (Exception ex) {

            try {
                ExceptionLoggerUtils.createExceptionLog(ex.toString(), classCode, "001", getContext());

            } catch (IOException e) {
                e.printStackTrace();
            }
            ProgressDialogUtils.closeProgressDialog();
            DialogUtils.showAlertDialog(getActivity(), errorMessages.EMC_0003);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == 1) {
                File f = new File(Environment.getExternalStorageDirectory().toString());
                for (File temp : f.listFiles()) {
                    if (temp.getName().equals("temp.jpg")) {
                        f = temp;
                        break;
                    }
                }
                try {
                    Bitmap bitmap;
                    BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
                    bitmap = BitmapFactory.decodeFile(f.getAbsolutePath(), bitmapOptions);
                    bitmap = getResizedBitmap(bitmap, 400);

                    if (camCount == 3) {
                        imgOne.setImageBitmap(bitmap);
                        imgStringOne = BitMapToString(bitmap);
                        chkBoxOne.setEnabled(true);
                    } else if (camCount == 2) {
                        imgTwo.setImageBitmap(bitmap);
                        imgStringTwo = BitMapToString(bitmap);
                        chkBoxTwo.setEnabled(true);
                    } else if (camCount == 1) {
                        imgThree.setImageBitmap(bitmap);
                        imgStringThree = BitMapToString(bitmap);
                        chkBoxThree.setEnabled(true);
                    }


                    String path = android.os.Environment
                            .getExternalStorageDirectory()
                            + File.separator
                            + "Inventrax" + File.separator + "default";
                    f.delete();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (requestCode == 2) {
                Uri selectedImage = data.getData();
                String[] filePath = {MediaStore.Images.Media.DATA};
                Cursor c = getActivity().getContentResolver().query(selectedImage, filePath, null, null, null);
                c.moveToFirst();
                int columnIndex = c.getColumnIndex(filePath[0]);
                String picturePath = c.getString(columnIndex);
                c.close();
                Bitmap thumbnail = (BitmapFactory.decodeFile(picturePath));
                thumbnail = getResizedBitmap(thumbnail, 400);
                //Log.w("path of image from gallery......******************.........", picturePath+"");
                imgOne.setImageBitmap(thumbnail);
                BitMapToString(thumbnail);
            }
        }
    }

    public Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float) width / (float) height;
        if (bitmapRatio > 1) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image, width, height, true);
    }

    public String BitMapToString(Bitmap userImage1) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        userImage1.compress(Bitmap.CompressFormat.PNG, 60, baos);
        byte[] b = baos.toByteArray();
        return Base64.encodeToString(b, Base64.NO_WRAP);
    }


    private void showCustomersDailog() {

        final Dialog mDialog = new Dialog(getContext());
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialog.setCancelable(true);
        mDialog.setContentView(R.layout.dialog_order_assist);

        ListView lstCustomer = (ListView) mDialog.findViewById(R.id.customerList);

        customerNames.clear();
        for (String list : custHashMap.keySet()) {
            // do something with tab

            customerNames.add(list);
        }


        ArrayAdapter adapter = new ArrayAdapter<String>(getContext(), R.layout.row_offer, customerNames);
        lstCustomer.setAdapter(adapter);

        mDialog.show();

        lstCustomer.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedCust = customerNames.get(position).toString();
                selectedCustId = custHashMap.get(selectedCust);
                mDialog.cancel();

            }
        });
    }


    @Override
    public void onResume() {
        super.onResume();
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(getString(R.string.toolbar_orderassist));
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayUseLogoEnabled(false);
    }


}
