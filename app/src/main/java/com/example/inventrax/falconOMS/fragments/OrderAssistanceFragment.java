package com.example.inventrax.falconOMS.fragments;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
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
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.inventrax.falconOMS.BuildConfig;
import com.example.inventrax.falconOMS.R;
import com.example.inventrax.falconOMS.activities.MainActivity;
import com.example.inventrax.falconOMS.adapters.FileListAdapter;
import com.example.inventrax.falconOMS.adapters.OrderAssistanceCustomerSelectionAdp;
import com.example.inventrax.falconOMS.common.Common;
import com.example.inventrax.falconOMS.common.constants.EndpointConstants;
import com.example.inventrax.falconOMS.common.constants.ErrorMessages;
import com.example.inventrax.falconOMS.interfaces.ApiInterface;
import com.example.inventrax.falconOMS.materialfilepicker.MaterialFilePicker;
import com.example.inventrax.falconOMS.materialfilepicker.ui.FilePickerActivity;
import com.example.inventrax.falconOMS.model.KeyValues;
import com.example.inventrax.falconOMS.pojos.FileUploadDTO;
import com.example.inventrax.falconOMS.pojos.OMSCoreMessage;
import com.example.inventrax.falconOMS.pojos.OMSExceptionMessage;
import com.example.inventrax.falconOMS.pojos.OrderAssistanceDTO;
import com.example.inventrax.falconOMS.room.AppDatabase;
import com.example.inventrax.falconOMS.room.RoomAppDatabase;
import com.example.inventrax.falconOMS.room.roomModel.CustomerData;
import com.example.inventrax.falconOMS.services.RestService;
import com.example.inventrax.falconOMS.util.DialogUtils;
import com.example.inventrax.falconOMS.util.ExceptionLoggerUtils;
import com.example.inventrax.falconOMS.util.FragmentUtils;
import com.example.inventrax.falconOMS.util.MaterialDialogUtils;
import com.example.inventrax.falconOMS.util.NetworkUtils;
import com.example.inventrax.falconOMS.util.ProgressDialogUtils;
import com.example.inventrax.falconOMS.util.SnackbarUtils;
import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class OrderAssistanceFragment extends Fragment implements View.OnClickListener {

    private static final String classCode = "OMS_Android_OrderAssistanceFragment";
    private View rootView;
    private TextView txtBack, txtUploadImg, tvSelectedCustomer;
    private CoordinatorLayout coordLayout;
    private FloatingActionButton btnOpenCamera;
    private ErrorMessages errorMessages;
    private Gson gson;
    private OMSCoreMessage core;
    private Common common;
    RestService restService;
    RecyclerView file_recyclerView;
    File[] files_list;
    GridLayoutManager layoutManager;
    FileListAdapter fileListAdapter;
    List<FileUploadDTO> fileUploadString;
    List<FileUploadDTO> pdfUpload;
    FileUploadDTO fileUploadDTO;
    private String userId, cusomerIDs;
    private MaterialDialogUtils materialDialogUtils;
    AppDatabase db;
    List customerNames;
    HashMap<String, String> custHashMap;
    String selectedCust = "", selectedCustId = "";
    public static final int CAMERA_REQUEST_CODE = 1;
    public static final int GALLERY_REQUEST_CODE = 2;
    public static final int FILE_PICKER_REQUEST_CODE = 3;
    public static final int PICK_IMAGES = 4;
    File oms_folder;
    File[] arrayFileList = new File[0];
    Dialog selectionDialog;
    ProgressDialog progress;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.order_assistande_fragment, container, false);

        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA}, 100);
        } else {
            loadFormControls();
        }

        return rootView;
    }

    private void loadFormControls() {

        try {

            // To disable Bottom navigation bar
            ((MainActivity) getActivity()).SetNavigationVisibility(false);

            db = new RoomAppDatabase(getActivity()).getAppDatabase();

            SharedPreferences sp = getContext().getSharedPreferences(KeyValues.MY_PREFS, Context.MODE_PRIVATE);
            userId = sp.getString(KeyValues.USER_ID, "");
            cusomerIDs = sp.getString(KeyValues.CUSTOMER_IDS, "");

            custHashMap = new HashMap<>();
            customerNames = new ArrayList();
            List<CustomerData> customerData = db.userDivisionCustDAO().getCustomerListByUser();
            for(int i=0;i< customerData.size();i++){

                if(customerData.get(i).getCustomerId() != null && customerData.get(i).getCustomerCode() != null && customerData.get(i).getCustomerName() != null){
                    String customerName=customerData.get(i).getCustomerName()==null || customerData.get(i).getCustomerName().isEmpty() ? " " : customerData.get(i).getCustomerName();
                    String customerCode=customerData.get(i).getCustomerCode()==null || customerData.get(i).getCustomerCode().isEmpty() ? " " : customerData.get(i).getCustomerCode();
                    custHashMap.put(customerName+"-"+customerCode,customerData.get(i).getCustomerId());
                }

            }

/*            JSONArray jsonArray = null;
            try {
                jsonArray = new JSONArray(cusomerIDs);
            } catch (JSONException e) {
                e.printStackTrace();
            }



            for (int i = 0; i < jsonArray.length(); i++) {

                try {
                    if (jsonArray.getString(i) != null) {

                        if (db.customerDAO().getCustomer(jsonArray.getString(i)).customerId != null && db.customerDAO().getCustomer(jsonArray.getString(i)).customerName != null) {
                            String customerName=db.customerDAO().getCustomer(jsonArray.getString(i)).customerName==null || db.customerDAO().getCustomer(jsonArray.getString(i)).customerName.isEmpty() ? " " : db.customerDAO().getCustomer(jsonArray.getString(i)).customerName;
                            String customerCode=db.customerDAO().getCustomer(jsonArray.getString(i)).customerCode==null || db.customerDAO().getCustomer(jsonArray.getString(i)).customerCode.isEmpty() ? " " : db.customerDAO().getCustomer(jsonArray.getString(i)).customerCode;
                            custHashMap.put(customerName+"-"+customerCode,db.customerDAO().getCustomer(jsonArray.getString(i)).customerId);
                        }

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }*/

            oms_folder = new File(getActivity().getBaseContext().getFilesDir(), "oms");
            if (!oms_folder.exists()) {
                boolean iscreated = oms_folder.mkdirs();
            }

            arrayFileList = oms_folder.listFiles();

            file_recyclerView = (RecyclerView) rootView.findViewById(R.id.file_recyclerView);
            layoutManager = new GridLayoutManager(getActivity(), 2);
            file_recyclerView.setLayoutManager(layoutManager);

            files_list = oms_folder.listFiles();
            fileListAdapter = new FileListAdapter(getActivity(), files_list);
            //file_recyclerView.setAdapter(fileListAdapter);

            coordLayout = (CoordinatorLayout) rootView.findViewById(R.id.coordinatorLayout);

            btnOpenCamera = (FloatingActionButton) rootView.findViewById(R.id.btnOpenCamera);
            btnOpenCamera.setOnClickListener(this);


            txtUploadImg = (TextView) rootView.findViewById(R.id.txtUploadImg);
            txtBack = (TextView) rootView.findViewById(R.id.txtBack);
            tvSelectedCustomer = (TextView) rootView.findViewById(R.id.tvSelectedCustomer);

            txtBack.setOnClickListener(this);
            txtUploadImg.setOnClickListener(this);
            tvSelectedCustomer.setOnClickListener(this);

            errorMessages = new ErrorMessages();
            common = new Common();
            restService = new RestService();
            core = new OMSCoreMessage();

            fileUploadDTO = new FileUploadDTO();
            materialDialogUtils = new MaterialDialogUtils();

            showCustomersDailog();

        }catch (Exception ex){

        }
    }

    AlertDialog alert;
    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btnOpenCamera:

                if(customerNames.size()==0){
                    SnackbarUtils.showSnackbar(coordLayout, "No customer mapped to the user", Snackbar.LENGTH_SHORT);
                    return;
                }

                if (!selectedCustId.isEmpty() || !selectedCust.equals("")) {

                    arrayFileList = oms_folder.listFiles();
                    if (arrayFileList.length < 10) {

                        selectionDialog = new Dialog(getActivity());
                        selectionDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        selectionDialog.setCancelable(true);
                        selectionDialog.setContentView(R.layout.selection_dialog);
                        selectionDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

                        TextView captureimage = selectionDialog.findViewById(R.id.captureimage);
                        TextView uploadimage = selectionDialog.findViewById(R.id.uploadimage);
                        TextView uploadpdf = selectionDialog.findViewById(R.id.uploadpdf);


                        captureimage.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                arrayFileList = oms_folder.listFiles();
                                if (arrayFileList.length > 0) {
                                    if (getFileExtension(arrayFileList[0]).equalsIgnoreCase("pdf")) {
                                        Toast.makeText(getActivity(), "Upload either Image or PDF file", Toast.LENGTH_SHORT).show();
                                    } else {
                                        captureFromCamera();
                                    }
                                } else {
                                    captureFromCamera();
                                }

                                selectionDialog.dismiss();
                            }
                        });

                        uploadimage.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                arrayFileList = oms_folder.listFiles();
                                if (arrayFileList.length > 0) {
                                    if (getFileExtension(arrayFileList[0]).equalsIgnoreCase("pdf")) {
                                        Toast.makeText(getActivity(), "Upload either Image or PDF file", Toast.LENGTH_SHORT).show();
                                    } else {
                                        pickFromGallery();
                                    }
                                } else {
                                    pickFromGallery();
                                }
                                selectionDialog.dismiss();
                            }
                        });

                        uploadpdf.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                arrayFileList = oms_folder.listFiles();
                                if (arrayFileList.length > 0) {
                                    if (getFileExtension(arrayFileList[0]).equals("jpg")) {
                                        Toast.makeText(getActivity(), "Upload either Image or PDF file", Toast.LENGTH_SHORT).show();
                                    } else {
                                        launchPicker();
                                    }
                                } else {
                                    launchPicker();
                                }
                                selectionDialog.dismiss();
                            }
                        });

                        selectionDialog.show();

                    } else {
                        Toast.makeText(getActivity(), "Already 10 items added", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    SnackbarUtils.showSnackbar(coordLayout, getString(R.string.selectCust), Snackbar.LENGTH_SHORT);
                    showCustomersDailog();
                }

                break;

            case R.id.txtUploadImg:

                if (!selectedCustId.isEmpty() || !selectedCust.equals("")) {
                    files_list = oms_folder.listFiles();
                    if (files_list.length > 0){

                        final AlertDialog.Builder builder;

                        builder = new AlertDialog.Builder(getActivity());

                        //Setting message manually and performing action on button click
                        builder.setMessage("Are you sure to upload")
                                .setCancelable(false)
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        prepareUploadData();
                                    }
                                })
                                .setNegativeButton("CLOSE", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        alert.dismiss();
                                    }
                                });

                        //Creating dialog box
                        alert = builder.create();

                        //Setting the title manually
                        alert.setTitle("Alert");
                        alert.show();
                    }
                    else{
                        SnackbarUtils.showSnackbar(coordLayout, "Please add atleast one file to proceed", Snackbar.LENGTH_SHORT);
                    }
                }else{
                    SnackbarUtils.showSnackbar(coordLayout, getString(R.string.selectCust), Snackbar.LENGTH_SHORT);
                    showCustomersDailog();
                }
                break;

            case R.id.txtBack:
                FragmentUtils.replaceFragmentWithBackStack(getActivity(), R.id.container, new HomeFragmentHints());
                break;

            case R.id.tvSelectedCustomer:
                showCustomersDailog();
                break;
        }

    }

    private String cameraFilePath;
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        //This is the directory in which the file will be created. This is the default location of Camera photos
        File storageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DCIM), "Camera");
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        // Save a file: path for using again
        cameraFilePath = "file://" + image.getAbsolutePath();
        return image;
    }

    private void captureFromCamera() {
        try {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, FileProvider.getUriForFile(getActivity(), BuildConfig.APPLICATION_ID + ".provider", createImageFile()));
            startActivityForResult(intent, CAMERA_REQUEST_CODE);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void pickFromGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGES);
    }


    private void launchPicker() {
        new MaterialFilePicker()
                .withSupportFragment(this)
                .withRequestCode(FILE_PICKER_REQUEST_CODE)
                .withHiddenFiles(false)
                .withFilter(Pattern.compile(".*\\.pdf$"))
                .withTitle("Select PDF file")
                .start();
    }

    public void prepareUploadData() {

        progress= new ProgressDialog(getActivity());
        progress.setMessage("Please wait..");

        progress.show();

        fileUploadString = new ArrayList<>();
        pdfUpload = new ArrayList<>();

        files_list = oms_folder.listFiles();

        boolean isPdf = false;

        if (!getFileExtension(files_list[0]).equalsIgnoreCase("jpg"))
            isPdf = true;

        for (int i = 0; i < files_list.length; i++) {
            fileUploadDTO = new FileUploadDTO();
            pdfUpload = new ArrayList<>();
            try {
                String encodedImage = "";
                if (!isPdf) {
                    encodedImage = Base64.encodeToString(fullyReadFileToBytes(files_list[i]), Base64.DEFAULT);

                    fileUploadDTO.setBase64(encodedImage);
                    fileUploadDTO.setName(files_list[i].getName());
                    fileUploadString.add(fileUploadDTO);
                } else {
                    encodedImage = Base64.encodeToString(fullyReadFileToBytes(files_list[i]), Base64.DEFAULT);

                    fileUploadDTO.setBase64(encodedImage);
                    fileUploadDTO.setName(files_list[i].getName());
                    pdfUpload.add(fileUploadDTO);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        orderAssistanceUpload();

    }

    private byte[] fullyReadFileToBytes(File f) throws IOException {
        int size = (int) f.length();
        byte[] bytes = new byte[size];
        byte[] tmpBuff = new byte[size];
        FileInputStream fis = new FileInputStream(f);

        try {

            int read = fis.read(bytes, 0, size);
            if (read < size) {
                int remain = size - read;
                while (remain > 0) {
                    read = fis.read(tmpBuff, 0, remain);
                    System.arraycopy(tmpBuff, 0, bytes, size - remain, read);
                    remain -= read;
                }
            }
        } catch (IOException e) {
            throw e;
        } finally {
            fis.close();
        }

        return bytes;
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
        oDto.setPdfUpload(pdfUpload);
        message.setEntityObject(oDto);

        Call<OMSCoreMessage> call = null;
        ApiInterface apiService = RestService.getClient().create(ApiInterface.class);

        call = apiService.OrderAssistanceUpload(message);

        try {
            //Getting response from the method
            call.enqueue(new Callback<OMSCoreMessage>() {

                @Override
                public void onResponse(Call<OMSCoreMessage> call, Response<OMSCoreMessage> response) {

                    if (response.body() != null) {

                        core = response.body();

                        if (core != null) {

                            if ((core.getType().toString().equals("Exception"))) {
                                progress.dismiss();
                                OMSExceptionMessage omsExceptionMessage = null;

                                for (OMSExceptionMessage oms : core.getOMSMessages()) {
                                    omsExceptionMessage = oms;
                                    ProgressDialogUtils.closeProgressDialog();
                                    common.showAlertType(omsExceptionMessage, getActivity(), getContext());
                                }

                            } else {
                                progress.dismiss();
                                try {


                                    if (!core.getEntityObject().equals("")) {

                                        MaterialDialogUtils.showUploadSuccessDialog(getContext(), "Uploaded");
                                        oms_folder = new File(getActivity().getBaseContext().getFilesDir(), "oms/" + selectedCustId + "/");
                                        if (oms_folder.exists())
                                            deleteRecursive(oms_folder);

                                        arrayFileList = oms_folder.listFiles();
                                        file_recyclerView.setAdapter(null);
                                        fileListAdapter.notifyDataSetChanged();

                                        showSuccessDialog(core.getEntityObject().toString());


                                    } else {
                                        Toast.makeText(getActivity(), "Upload failed please try again", Toast.LENGTH_SHORT).show();
                                    }
                                    progress.dismiss();
                                }catch (Exception ex){

                                }

                            }
                            progress.dismiss();
                        }
                        progress.dismiss();
                    }
                    else {
                        progress.dismiss();
                        Toast.makeText(getActivity(), "Upload failed please try again", Toast.LENGTH_SHORT).show();
                    }
                }

                // response object fails
                @Override
                public void onFailure(Call<OMSCoreMessage> call, Throwable throwable) {
                    if(NetworkUtils.isInternetAvailable(getActivity())){
                        DialogUtils.showAlertDialog(getActivity(), errorMessages.EMC_0001);
                    }else{
                        DialogUtils.showAlertDialog(getActivity(), errorMessages.EMC_0014);
                    }
                    progress.dismiss();
                }
            });
        } catch (Exception ex) {

            try {
                ExceptionLoggerUtils.createExceptionLog(ex.toString(), classCode, "001", getContext());
            } catch (IOException e) {
                e.printStackTrace();
            }
            progress.dismiss();
            DialogUtils.showAlertDialog(getActivity(), errorMessages.EMC_0003);
        }
    }


    public void deleteRecursive(File fileOrDirectory) {

        if (fileOrDirectory.isDirectory()) {
            for (File child : fileOrDirectory.listFiles()) {
                // deleteRecursive(child);
                if (child.exists()) {
                    boolean is = child.delete();
                }
            }
            //fileOrDirectory.delete();
        }
    }

    private void showCustomersDailog() {

        final Dialog mDialog = new Dialog(getContext());
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialog.setCancelable(true);
        mDialog.setContentView(R.layout.dialog_order_assist);

        RecyclerView lstCustomer = (RecyclerView) mDialog.findViewById(R.id.customerList);
        LinearLayoutManager lManager = new LinearLayoutManager(getActivity());
        lstCustomer.setLayoutManager(lManager);

        customerNames.clear();
        for (String list : custHashMap.keySet()) {
            // do something with tab
            customerNames.add(list);
        }

        if(customerNames.size()==0){
            SnackbarUtils.showSnackbar(coordLayout, "No customer mapped to the user", Snackbar.LENGTH_SHORT);
            return;
        }

       /* ArrayAdapter adapter = new ArrayAdapter<String>(getContext(), R.layout.row_customerlist_orderassist, customerNames);
        lstCustomer.setAdapter(adapter);*/

        OrderAssistanceCustomerSelectionAdp adp = new OrderAssistanceCustomerSelectionAdp(getActivity(), customerNames, new OrderAssistanceCustomerSelectionAdp.OnItemClickListener() {
            @Override
            public void onItemClick(int pos) {

                selectedCust = customerNames.get(pos).toString();
                selectedCustId = custHashMap.get(selectedCust);

                oms_folder = new File(getActivity().getBaseContext().getFilesDir(), "oms/" + selectedCustId + "/");
                if (!oms_folder.exists()) {
                    boolean iscreated = oms_folder.mkdirs();
                }

                files_list = oms_folder.listFiles();
                fileListAdapter = new FileListAdapter(getActivity(), files_list);
                file_recyclerView.setAdapter(fileListAdapter);

                tvSelectedCustomer.setText(selectedCust);

                mDialog.cancel();
            }
        });

        lstCustomer.setAdapter(adp);
        mDialog.show();

        /*lstCustomer.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                selectedCust = customerNames.get(position).toString();
                selectedCustId = custHashMap.get(selectedCust);

                oms_folder = new File(getActivity().getBaseContext().getFilesDir(), "oms/" + selectedCustId + "/");
                if (!oms_folder.exists()) {
                    boolean iscreated = oms_folder.mkdirs();
                }

                files_list = oms_folder.listFiles();
                fileListAdapter = new FileListAdapter(getActivity(), files_list);
                file_recyclerView.setAdapter(fileListAdapter);

                tvSelectedCustomer.setText(selectedCust);

                mDialog.cancel();

            }
        });*/
    }

/*    private void showCustomersDailog() {

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

        if(customerNames.size()==0){
            SnackbarUtils.showSnackbar(coordLayout, "No customer mapped to the user", Snackbar.LENGTH_SHORT);
            return;
        }

        ArrayAdapter adapter = new ArrayAdapter<String>(getContext(), R.layout.row_text, customerNames);
        lstCustomer.setAdapter(adapter);

        mDialog.show();

        lstCustomer.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                selectedCust = customerNames.get(position).toString();
                selectedCustId = custHashMap.get(selectedCust);

                oms_folder = new File(getActivity().getBaseContext().getFilesDir(), "oms/" + selectedCustId + "/");
                if (!oms_folder.exists()) {
                    boolean iscreated = oms_folder.mkdirs();
                }

                files_list = oms_folder.listFiles();
                fileListAdapter = new FileListAdapter(getActivity(), files_list);
                file_recyclerView.setAdapter(fileListAdapter);

                tvSelectedCustomer.setText(selectedCust);

                mDialog.cancel();

            }
        });
    }*/

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Result code is RESULT_OK only if the user captures an Image
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK){
            switch (requestCode) {
                case CAMERA_REQUEST_CODE:
                    try {
                        final InputStream imageStream = getActivity().getContentResolver().openInputStream(Uri.parse(cameraFilePath));
                        Bitmap bm = BitmapFactory.decodeStream(imageStream);
                        // resizing images when width greater than 720p
                        if (bm.getWidth() > 720) {
                            int height = (int) (((float) (720.0 / bm.getWidth())) * bm.getHeight());
                            bm = Bitmap.createScaledBitmap(bm, 720, height, true);
                        }
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        bm.compress(Bitmap.CompressFormat.JPEG, 100, baos); //bm is the bitmap object
                        byte[] b = baos.toByteArray();
                        // String encodedImage = Base64.encodeToString(b, Base64.DEFAULT);
                        Long tsLong = System.currentTimeMillis() / 1000;
                        File oms_file = new File(oms_folder, tsLong  + "C" + ".jpg");
                        if (!oms_file.exists()) {
                            try {
                                boolean iscreated = oms_file.createNewFile();
                                //write the bytes in file
                                if (iscreated) {
                                    FileOutputStream fos = new FileOutputStream(oms_file);
                                    fos.write(b);
                                    fos.flush();
                                    fos.close();
                                } else {
                                    Toast.makeText(getActivity(), "ERROR WHILE CREATION FILE.PLEASE TRY AGAIN", Toast.LENGTH_SHORT).show();
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }

                        arrayFileList = oms_folder.listFiles();
                        fileListAdapter = new FileListAdapter(getActivity(), arrayFileList);
                        file_recyclerView.setAdapter(fileListAdapter);

                    } catch (Exception e) {
                        Toast.makeText(getActivity(), "Failed to load the image", Toast.LENGTH_SHORT).show();
                    }

                    break;
                case GALLERY_REQUEST_CODE:
                    //data.getData returns the content URI for the selected Image
                    Uri selectedImage = data.getData();
                    // Set the Image in ImageView after decoding the String
                    try {

                        final InputStream imageStream = getActivity().getContentResolver().openInputStream(selectedImage);
                        Bitmap bm = BitmapFactory.decodeStream(imageStream);

                        // resizing images when width greater than 720p
                        if (bm.getWidth() > 720) {
                            int height = (int) (((float) (720.0 / bm.getWidth())) * bm.getHeight());
                            bm = Bitmap.createScaledBitmap(bm, 720, height, true);
                        }
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        bm.compress(Bitmap.CompressFormat.JPEG, 100, baos); //bm is the bitmap object
                        byte[] b = baos.toByteArray();

                        Long tsLong = System.currentTimeMillis() / 1000;
                        File oms_file = new File(oms_folder, tsLong + "G" + ".jpg");
                        if (!oms_file.exists()) {
                            try {
                                boolean iscreated = oms_file.createNewFile();
                                //write the bytes in file
                                if (iscreated) {
                                    FileOutputStream fos = new FileOutputStream(oms_file);
                                    fos.write(b);
                                    fos.flush();
                                    fos.close();
                                } else {
                                    Toast.makeText(getActivity(), "ERROR WHILE CREATION FILE.PLEASE TRY AGAIN", Toast.LENGTH_SHORT).show();
                                }

                            } catch (IOException ex) {

                            }
                        }

                        arrayFileList = oms_folder.listFiles();
                        fileListAdapter = new FileListAdapter(getActivity(), arrayFileList);
                        file_recyclerView.setAdapter(fileListAdapter);

                    } catch (Exception ex) {
                        Toast.makeText(getActivity(), "Failed to load the image", Toast.LENGTH_SHORT).show();
                    }

                    break;
                case FILE_PICKER_REQUEST_CODE:
                    //data.getData returns the content URI for the selected Image
                    String path = data.getStringExtra(FilePickerActivity.RESULT_FILE_PATH);
                    File file = new File(path);
                    if (path != null) {

                        if (((int) Math.round((double) file.length() / 1024)) <= 2048) {
                            byte[] b = new byte[(int) file.length()];
                            try {
                                FileInputStream fileInputStream = new FileInputStream(file);
                                fileInputStream.read(b);
/*                                for (int i = 0; i < b.length; i++) {
                                    System.out.print((char) b[i]);
                                }*/
                                Long tsLong = System.currentTimeMillis() / 1000;
                                File oms_file = new File(oms_folder, tsLong + "P"  + ".pdf");
                                if (!oms_file.exists()) {
                                    try {
                                        boolean iscreated = oms_file.createNewFile();
                                        //write the bytes in file
                                        if (iscreated) {
                                            FileOutputStream fos = new FileOutputStream(oms_file);
                                            fos.write(b);
                                            fos.flush();
                                            fos.close();
                                        } else {
                                            Toast.makeText(getActivity(), "ERROR WHILE CREATION FILE.PLEASE TRY AGAIN", Toast.LENGTH_SHORT).show();
                                        }

                                    } catch (IOException ex) {
                                        Toast.makeText(getActivity(),  ex.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }

                                arrayFileList = oms_folder.listFiles();
                                fileListAdapter = new FileListAdapter(getActivity(), arrayFileList);
                                file_recyclerView.setAdapter(fileListAdapter);

                            } catch (Exception e) {
                                Toast.makeText(getActivity(), "Failed to load the pdf", Toast.LENGTH_SHORT).show();
                            }

                        } else {
                            Toast.makeText(getActivity(), "File size more than 2 MB", Toast.LENGTH_SHORT).show();
                        }
                    }
                    break;

                case PICK_IMAGES:

                    //data.getParcelableArrayExtra(name);
                    //If Single image selected then it will fetch from Gallery

                    if (data.getData() != null) {

                        Uri mImageUri = data.getData();

                        storeAndShowImg(mImageUri, 999);

                        arrayFileList = oms_folder.listFiles();
                        fileListAdapter = new FileListAdapter(getActivity(), arrayFileList);
                        file_recyclerView.setAdapter(fileListAdapter);

                    } else {
                        if (data.getClipData() != null) {
                            ClipData mClipData = data.getClipData();
                            ArrayList<Uri> mArrayUri = new ArrayList<Uri>();
                            int size = oms_folder.listFiles().length + mClipData.getItemCount();
                            if (size <= 10) {
                                for (int i = 0; i < mClipData.getItemCount(); i++) {

                                    ClipData.Item item = mClipData.getItemAt(i);
                                    Uri uri = item.getUri();
                                    storeAndShowImg(uri, i);
                                    mArrayUri.add(uri);

                                }

                            } else {
                                Toast.makeText(getActivity(), "You cannot select more than 10 images", Toast.LENGTH_SHORT).show();
                            }


                            arrayFileList = oms_folder.listFiles();
                            fileListAdapter = new FileListAdapter(getActivity(), arrayFileList);
                            file_recyclerView.setAdapter(fileListAdapter);
                        }

                    }


                    break;
            }
        }
        if (resultCode == Activity.RESULT_CANCELED){
            Toast.makeText(getActivity(), "failed to retrieve", Toast.LENGTH_SHORT).show();
        }
    }


    public void storeAndShowImg(Uri img, int i) {
        // Set the Image in ImageView after decoding the String
        try {

            final InputStream imageStream = getActivity().getContentResolver().openInputStream(img);
            Bitmap bm = BitmapFactory.decodeStream(imageStream);

            // resizing images when width greater than 720p
            if (bm.getWidth() > 720) {
                int height = (int) (((float) (720.0 / bm.getWidth())) * bm.getHeight());
                bm = Bitmap.createScaledBitmap(bm, 720, height, true);
            }
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bm.compress(Bitmap.CompressFormat.JPEG, 100, baos); //bm is the bitmap object
            byte[] b = baos.toByteArray();
            Long tsLong = System.currentTimeMillis() / 1000;
            File oms_file = new File(oms_folder, tsLong + "M" + i + ".jpg");
            if (!oms_file.exists()) {
                try {
                    boolean iscreated = oms_file.createNewFile();
                    //write the bytes in file
                    if (iscreated) {
                        FileOutputStream fos = new FileOutputStream(oms_file);
                        fos.write(b);
                        fos.flush();
                        fos.close();
                    } else {
                        Toast.makeText(getActivity(), "ERROR WHILE CREATION FILE.PLEASE TRY AGAIN", Toast.LENGTH_SHORT).show();
                    }

                } catch (IOException ex) {
                    Toast.makeText(getActivity(),  ex.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(getString(R.string.toolbar_orderassist));
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayUseLogoEnabled(false);
    }

    public String getFileExtension(File file) {
        String fileName = file.getPath().substring(file.getPath().lastIndexOf("/") + 1);
        return fileName.substring(fileName.lastIndexOf(".") + 1);
    }

    private void showSuccessDialog(String ticketNum) {

        final Dialog mDialog = new Dialog(getActivity());
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialog.setCancelable(false);
        mDialog.setContentView(R.layout.dialog_order_created);

        TextView succesText = (TextView) mDialog.findViewById(R.id.textSONumber);
        Button buttonOk = (Button) mDialog.findViewById(R.id.buttonOk);
        TextView txtOrdersTitle = (TextView) mDialog.findViewById(R.id.txtOrdersTitle);
        txtOrdersTitle.setText("Ticket Created");
        succesText.setText("Ticket created successfully: " + ticketNum);

        buttonOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDialog.cancel();
                FragmentUtils.replaceFragmentWithBackStack(getActivity(), R.id.container, new HomeFragmentHints());
            }
        });

        mDialog.show();


    }

}
