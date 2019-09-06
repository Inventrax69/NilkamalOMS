package com.example.inventrax.falconOMS.fragments;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.inventrax.falconOMS.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import static android.app.Activity.RESULT_OK;

public class OrderAssistanceFragment extends Fragment implements View.OnClickListener {

    private static final String classCode = "OMS_Android_OrderAssistanceFragment";
    private View rootView;
    private ImageView imgOne, imgTwo, imgThree;
    private CheckBox chkBoxOne, chkBoxTwo, chkBoxThree;

    private FloatingActionButton btnOpenCamera;

    private int camCount = 4;
    private String Document_img1="";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.order_assistande_fragment, container, false);

        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_DENIED){
            ActivityCompat.requestPermissions(getActivity(), new String[] {Manifest.permission.CAMERA}, 100);

        }else {
            loadFormControls();
        }





        return rootView;
    }

    private void loadFormControls() {

        btnOpenCamera = (FloatingActionButton) rootView.findViewById(R.id.btnOpenCamera);
        btnOpenCamera.setOnClickListener(this);

        imgOne = (ImageView) rootView.findViewById(R.id.imgOne);
        imgTwo = (ImageView) rootView.findViewById(R.id.imgTwo);
        imgThree = (ImageView) rootView.findViewById(R.id.imgThree);

        chkBoxOne = (CheckBox) rootView.findViewById(R.id.chkBoxOne);
        chkBoxTwo = (CheckBox) rootView.findViewById(R.id.chkBoxTwo);
        chkBoxThree = (CheckBox) rootView.findViewById(R.id.chkBoxThree);

    }


    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btnOpenCamera:

                camCount--;

                if (camCount == 0) {

                    Toast.makeText(getContext(), "full", Toast.LENGTH_LONG).show();
                    return;
                }

                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                File f = new File(android.os.Environment.getExternalStorageDirectory(), "temp.jpg");
                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
                startActivityForResult(intent, 1);

                break;
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
                    } else if (camCount == 2){
                        imgTwo.setImageBitmap(bitmap);
                    }else if (camCount == 1){
                        imgThree.setImageBitmap(bitmap);
                    }

                        BitMapToString(bitmap);
                        String path = android.os.Environment
                                .getExternalStorageDirectory()
                                + File.separator
                                + "Inventrax" + File.separator + "default";
                    f.delete();
                    OutputStream outFile = null;
                    File file = new File(path, String.valueOf(System.currentTimeMillis()) + ".jpg");
                    try {
                        outFile = new FileOutputStream(file);
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 85, outFile);
                        outFile.flush();
                        outFile.close();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
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
        Document_img1 = Base64.encodeToString(b,Base64.NO_WRAP );
        return Document_img1;
    }


    @Override
    public void onResume() {
        super.onResume();
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(getString(R.string.toolbar_orderassist));
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayUseLogoEnabled(false);
    }


}
