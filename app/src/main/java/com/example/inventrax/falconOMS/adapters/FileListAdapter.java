package com.example.inventrax.falconOMS.adapters;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.example.inventrax.falconOMS.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FileListAdapter extends RecyclerView.Adapter<FileListAdapter.ViewHolder> {

    private Context context;
    private File[] files;
    private Dialog popupDialog;

    public FileListAdapter(Context context, File[] files) {
        this.context = context;
        this.files = files;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.file_list_view, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {

        String fileExtension = getFileExtension(files[position]);

        if (fileExtension.equals("jpg")) {
            holder.imageRelative.setVisibility(View.VISIBLE);
            holder.pdfRelative.setVisibility(View.GONE);
            Bitmap imageBitmap = BitmapFactory.decodeFile(files[position].getAbsolutePath());
            holder.imageimageView.setImageBitmap(imageBitmap);
            int color = Color.parseColor("#FFFFFF"); // The color u want
            holder.imageEye.setColorFilter(color);
        } else {
            holder.imageRelative.setVisibility(View.GONE);
            holder.pdfRelative.setVisibility(View.VISIBLE);

            /*
            holder.pdfView.fromFile(files[position])
                    .pages(0)
                    .enableDoubletap(false)
                    .enableSwipe(true)
                    .defaultPage(0)
                    .load();
            */
        }

        holder.imageimageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                popupDialog = new Dialog(context);
                popupDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                popupDialog.setCancelable(true);
                popupDialog.setContentView(R.layout.image_view);

                ImageView imageimageView = (ImageView) popupDialog.findViewById(R.id.imageimageView);
                Bitmap imageBitmap = BitmapFactory.decodeFile(files[position].getAbsolutePath());
                imageimageView.setImageBitmap(imageBitmap);

                popupDialog.show();

            }
        });

        holder.imageEye.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (getFileExtension(files[position]).equalsIgnoreCase("pdf")) {
                    popupDialog = new Dialog(context);
                    popupDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    popupDialog.setCancelable(true);
                    popupDialog.setContentView(R.layout.pdf_view);
                } else {

                    popupDialog = new Dialog(context);
                    popupDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    popupDialog.setCancelable(true);
                    popupDialog.setContentView(R.layout.image_view);

                    ImageView imageimageView = (ImageView) popupDialog.findViewById(R.id.imageimageView);
                    Bitmap imageBitmap = BitmapFactory.decodeFile(files[position].getAbsolutePath());
                    imageimageView.setImageBitmap(imageBitmap);

                    popupDialog.show();
                }

              /*  popupDialog = new Dialog(context);
                popupDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                popupDialog.setCancelable(true);
                popupDialog.setContentView(R.layout.pdf_view);

*//*                PDFView pdfView=(PDFView)popupDialog.findViewById(R.id.pdfView);
                pdfView.fromFile(files[position])
                        .enableDoubletap(false)
                        .defaultPage(0)
                        .load();*//*

                popupDialog.show();*/

            }
        });

        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<File> list = new ArrayList<File>(Arrays.asList(files));
                list.get(position).delete();
                list.remove(position);
                files = list.toArray(new File[0]);
                notifyItemChanged(position);
                notifyDataSetChanged();
            }
        });

    }


    public String getFileExtension(File file) {
        String fileName = file.getPath().substring(file.getPath().lastIndexOf("/") + 1);
        return fileName.substring(fileName.lastIndexOf(".") + 1);
    }

    private byte[] fullyReadFileToBytes(File f) throws IOException {
        int size = (int) f.length();
        byte[] bytes = new byte[size];
        byte[] tmpBuff = new byte[size];
        FileInputStream fis = new FileInputStream(f);
        ;
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


    @Override
    public int getItemCount() {
        return files.length;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        // TextView filename;
        RelativeLayout imageRelative, pdfRelative;
        ImageView imageimageView, imageEye, delete;
        //PDFView pdfView;
        ImageView pdfView;
        View view;

        public ViewHolder(View view) {
            super(view);
            // filename=view.findViewById(R.id.filename);
            this.view = view;
            imageRelative = view.findViewById(R.id.imageRelative);
            pdfRelative = view.findViewById(R.id.pdfRelative);
            imageimageView = view.findViewById(R.id.imageimageView);
            pdfView = view.findViewById(R.id.pdfView);
            imageEye = view.findViewById(R.id.imageEye);
            delete = view.findViewById(R.id.delete);

        }
    }
}
