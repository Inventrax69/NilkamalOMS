package com.example.inventrax.falconOMS.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.inventrax.falconOMS.R;
import com.example.inventrax.falconOMS.adapters.ImagesAdapter;
import com.squareup.picasso.Picasso;
import com.synnapps.carouselview.CarouselView;
import com.synnapps.carouselview.ImageListener;

import java.util.ArrayList;
import java.util.List;

public class ProductsFragment extends Fragment implements View.OnClickListener {

    private static final String classCode = "OMS_Android_ProductsFragment";
    private View rootView;
    CarouselView carouselView;
    RecyclerView rvReady,rvMattresses,rvPlastic;
    LinearLayoutManager linearLayoutManagerReady,linearLayoutManagerMat,linearLayoutManagerPlastic;
    ImagesAdapter imagesAdapter;
    private List<String> mPlasticImageList,mReadyFurImageList,mMatImageList;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.products_fragment, container, false);
        loadFormControls();
        return rootView;
    }

    private void loadFormControls() {


        carouselView = rootView.findViewById(R.id.carouselView);

        mPlasticImageList = new ArrayList<>();
        mReadyFurImageList = new ArrayList<>();
        mMatImageList = new ArrayList<>();

        rvReady =  rootView.findViewById(R.id.rvReady);
        rvMattresses =  rootView.findViewById(R.id.rvMattresses);
        rvPlastic =  rootView.findViewById(R.id.rvPlastic);

        linearLayoutManagerReady = new LinearLayoutManager(getActivity(),LinearLayoutManager.HORIZONTAL,false);
        linearLayoutManagerMat = new LinearLayoutManager(getActivity(),LinearLayoutManager.HORIZONTAL,false);
        linearLayoutManagerPlastic = new LinearLayoutManager(getActivity(),LinearLayoutManager.HORIZONTAL,false);
        rvReady.setLayoutManager(linearLayoutManagerReady);
        rvMattresses.setLayoutManager(linearLayoutManagerMat);
        rvPlastic.setLayoutManager(linearLayoutManagerPlastic);

        imagesAdapter = new ImagesAdapter();

        carouselView.setImageListener(imageListener);

        setReadyFurImgs();
        setPlasticImgs();
        setMattressesImgs();
        initMatRecyclerView();
        initPlasticFurRecyclerView();
        initReadyFurRecyclerView();

    }

    ImageListener imageListener = new ImageListener() {
        @Override
        public void setImageForPosition(int position, ImageView imageView) {
            //imageView.setImageResource(mImageList.);

            Picasso.with(getActivity())
                    .load(String.valueOf(mReadyFurImageList.get(position)))
                    .placeholder(R.drawable.no_img)
                    .into(imageView);
        }
    };


    @Override
    public void onClick(View v) {
        switch (v.getId()) {

        }
    }

    private void setReadyFurImgs() {

        mReadyFurImageList.add("https://cdn.shopify.com/s/files/1/0044/1208/0217/products/EDWINA_3DOOR_WARDROBE_WTH_2_DRAWER_BROWN_IEDW3DWRBW2DRWBRN_LS_300x.jpg?v=1567845403");
        mReadyFurImageList.add("https://cdn.shopify.com/s/files/1/0044/1208/0217/products/LUCAS_SINGLE_BED_BLACK_LLUCASSINBEDBLK_LS_300x.jpg?v=1563431542");
        mReadyFurImageList.add("https://cdn.shopify.com/s/files/1/0044/1208/0217/products/CRYSTA_4_DOOR_WARDROBE_WENGE_ICRYSTA4DWRBWEN_LS_300x.jpg?v=1567845394");
        mReadyFurImageList.add("https://cdn.shopify.com/s/files/1/0044/1208/0217/products/CRYSTA_QUEEN_BED_WENGE_ICRYSTAQUEBEDWEN_LS_300x.jpg?v=1567845396");
        mReadyFurImageList.add("https://cdn.shopify.com/s/files/1/0044/1208/0217/products/STERLING_KING_BED_CREAM_ISTERLINKINBEDCRM_07_300x.jpg?v=1565612207");
        mReadyFurImageList.add("https://cdn.shopify.com/s/files/1/0044/1208/0217/products/MOROCCO-NIGHT-STAND-W-ONE-DRAWER-WENGE_LMOROCCOSIDTBLWEN_01_300x.jpg?v=1565612123");
        mReadyFurImageList.add("https://cdn.shopify.com/s/files/1/0044/1208/0217/products/CRYSTA_SIDE_TABLE_WENGE_ICRYSTASIDTBLWEN_LS_300x.jpg?v=1567845399");

    }

    private void setPlasticImgs() {

        mPlasticImageList.add("https://cdn.shopify.com/s/files/1/0044/1208/0217/products/PLATINUM_RATTAN_DARK_BEIGE_PLATINUMRDB_02_300x.jpg?v=1567845631");
        mPlasticImageList.add("https://cdn.shopify.com/s/files/1/0044/1208/0217/products/VENTO_CHAIR_MILKY_WHITE_02_300x.jpg?v=1567845665");
        mPlasticImageList.add("https://cdn.shopify.com/s/files/1/0044/1208/0217/products/CHR2071_MARBLE_BEIGE_CHR2071MBG_2_300x.jpg?v=1567845635");
        mPlasticImageList.add("https://cdn.shopify.com/s/files/1/0044/1208/0217/products/EEEZY_CHAIR_PEACH__01_300x.jpg?v=1567845571");
        mPlasticImageList.add("https://cdn.shopify.com/s/files/1/0044/1208/0217/products/PBG_Pine_Beige__CLUBPNB_02_copy_300x.jpg?v=1567845672");
        mPlasticImageList.add("https://cdn.shopify.com/s/files/1/0044/1208/0217/products/NEXUSMWH_01_300x.jpg?v=1567844812");
        mPlasticImageList.add("https://cdn.shopify.com/s/files/1/0044/1208/0217/products/DESIRE_IRON_BLACK_DESIREIBK_03_300x.jpg?v=1567845626");

    }

    private void setMattressesImgs() {

        mMatImageList.add("https://cdn.shopify.com/s/files/1/0044/1208/0217/products/Travelite_LS_300x.jpg?v=1570085784");
        mMatImageList.add("https://cdn.shopify.com/s/files/1/0044/1208/0217/products/Coolbond_Double_LS_jpg_300x.jpg?v=1563874748");
        mMatImageList.add("https://cdn.shopify.com/s/files/1/0044/1208/0217/products/Wellbond_double_jpg_300x.jpg?v=1570085791");
        mMatImageList.add("https://cdn.shopify.com/s/files/1/0044/1208/0217/products/ORTHO_COMFORT_Double_LS_jpg_1b5b9c16-5011-4216-b805-0d9a6bfc9a35_300x.jpg?v=1570085803");
        mMatImageList.add("https://cdn.shopify.com/s/files/1/0044/1208/0217/products/MONARCH_2_double_LS_jpg_300x.jpg?v=1570085764");
        mMatImageList.add("https://cdn.shopify.com/s/files/1/0044/1208/0217/products/1_300x.JPG?v=1570086204");
        mMatImageList.add("https://cdn.shopify.com/s/files/1/0044/1208/0217/products/3_5503b5b8-0887-429c-bf51-1bd6b10b280a_300x.jpg?v=1570085757");

    }

    private void initReadyFurRecyclerView(){

        carouselView.setPageCount(mReadyFurImageList.size());

        /*DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(rvReady.getContext(), linearLayoutManagerReady.getOrientation());
        rvReady.addItemDecoration(dividerItemDecoration);*/

        imagesAdapter = new ImagesAdapter(getContext(), mReadyFurImageList, new ImagesAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int pos) {

            }
        });
        rvReady.setAdapter(imagesAdapter);

    }

    private void initMatRecyclerView(){

        carouselView.setPageCount(mMatImageList.size());

        /*DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(rvMattresses.getContext(), linearLayoutManagerMat.getOrientation());
        rvMattresses.addItemDecoration(dividerItemDecoration);*/

        imagesAdapter = new ImagesAdapter(getContext(), mMatImageList, new ImagesAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int pos) {

            }
        });
        rvMattresses.setAdapter(imagesAdapter);

    }

    private void initPlasticFurRecyclerView(){

        carouselView.setPageCount(mPlasticImageList.size());

        /*DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(rvPlastic.getContext(), linearLayoutManagerPlastic.getOrientation());
        rvPlastic.addItemDecoration(dividerItemDecoration);*/

        imagesAdapter = new ImagesAdapter(getContext(), mPlasticImageList, new ImagesAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int pos) {

            }
        });
        rvPlastic.setAdapter(imagesAdapter);

    }

    @Override
    public void onResume() {
        super.onResume();
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(getString(R.string.e_info));
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayUseLogoEnabled(false);
    }


}
