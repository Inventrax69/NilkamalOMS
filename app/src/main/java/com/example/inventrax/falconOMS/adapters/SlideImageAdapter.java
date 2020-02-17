package com.example.inventrax.falconOMS.adapters;

import android.content.Context;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.inventrax.falconOMS.R;
import com.example.inventrax.falconOMS.model.ImageModel;
import com.example.inventrax.falconOMS.util.photoview.PhotoView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class SlideImageAdapter extends PagerAdapter {


    private ArrayList<ImageModel> IMAGES;
    private LayoutInflater inflater;
    private Context context;


    public SlideImageAdapter(Context context,ArrayList<ImageModel> IMAGES) {
        this.context = context;
        this.IMAGES=IMAGES;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getCount() {
        return IMAGES.size();
    }

    @Override
    public Object instantiateItem(ViewGroup view, int position) {
        View imageLayout = inflater.inflate(R.layout.sliding_images, view, false);

        assert imageLayout != null;
        final PhotoView imageView = (PhotoView) imageLayout
                .findViewById(R.id.image);

        ImageModel imageModel = IMAGES.get(position);

        Picasso.with(context)
                .load(imageModel.getImage())
                .placeholder(R.drawable.load)
                .into(imageView);

        view.addView(imageLayout, 0);

        return imageLayout;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view.equals(object);
    }

    @Override
    public void restoreState(Parcelable state, ClassLoader loader) {
    }

    @Override
    public Parcelable saveState() {
        return null;
    }


}