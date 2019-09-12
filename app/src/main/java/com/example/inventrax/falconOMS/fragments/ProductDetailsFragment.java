package com.example.inventrax.falconOMS.fragments;

/*
 * Created by Padmaja on 04/07/2019.
 */

import android.annotation.TargetApi;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.inventrax.falconOMS.R;
import com.example.inventrax.falconOMS.activities.MainActivity;
import com.example.inventrax.falconOMS.adapters.ImagesAdapter;
import com.example.inventrax.falconOMS.adapters.OffersAdapter;
import com.example.inventrax.falconOMS.adapters.SlideImageAdapter;
import com.example.inventrax.falconOMS.model.ImageModel;
import com.example.inventrax.falconOMS.pojos.VariantDTO;
import com.example.inventrax.falconOMS.room.ItemTable;
import com.example.inventrax.falconOMS.util.Converters;
import com.example.inventrax.falconOMS.util.DateUtils;
import com.example.inventrax.falconOMS.util.FragmentUtils;
import com.example.inventrax.falconOMS.util.searchableSpinner.SearchableSpinner;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class ProductDetailsFragment extends Fragment implements View.OnClickListener {
    private static final String classCode = "OMS_Android_ItemDetailsFragment";
    private View rootView;

    private EditText deliveryDatePicker,etQtyBottom;
    private TextView txtItemName,txtDiscount,txtPrice,txtPriceCut,
            txtAvailableCreditLimit,txtDescreption,txtwidth,txtheight,txtdepth,txtPackageContents,txtAddtoCart;
    private ImageView availOffer,ivItem;
    private SearchableSpinner spinnerVariant;

    private ArrayList<ImageModel> mImageList;
    ImagesAdapter imagesAdapter;
    private RecyclerView rvImages;
    private LinearLayoutManager linearLayoutManager;

    private static ViewPager mPager;
    ArrayList<String> varient;

    private String selectedVar = "";
    ArrayList<VariantDTO> varientDtoLst;
    String myFormat = "dd/MMM/yyyy";


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.product_details_fragment,container,false);

        loadFormControls();

        return rootView;
    }

    private void loadFormControls() {

        ItemTable itemTable = new ItemTable();

        itemTable = (ItemTable) getArguments().getSerializable("modelItems");

        varient = new ArrayList<>();

        varientDtoLst = new ArrayList<>();
        varientDtoLst = Converters.fromString(itemTable.varientList);

        for(VariantDTO v: varientDtoLst){

            varient.add(v.getMcode());

        }



        // To disable Bottom navigation bar
        ((MainActivity)getActivity()).SetNavigationVisibiltity(false);

        deliveryDatePicker = (EditText) rootView.findViewById(R.id.deliveryDatePicker);
        etQtyBottom = (EditText) rootView.findViewById(R.id.etQtyBottom);

        ivItem = (ImageView) rootView.findViewById(R.id.ivItem);
        availOffer = (ImageView) rootView.findViewById(R.id.availOffer);

        txtItemName = (TextView) rootView.findViewById(R.id.txtItemName);
        txtDiscount = (TextView) rootView.findViewById(R.id.txtDiscount);
        txtPrice = (TextView) rootView.findViewById(R.id.txtPrice);
        txtPriceCut = (TextView) rootView.findViewById(R.id.txtPriceCut);
        txtAvailableCreditLimit = (TextView) rootView.findViewById(R.id.txtAvailableCreditLimit);
        txtDescreption = (TextView) rootView.findViewById(R.id.txtDescreption);
        txtwidth = (TextView) rootView.findViewById(R.id.txtwidth);
        txtheight = (TextView) rootView.findViewById(R.id.txtheight);
        txtdepth = (TextView) rootView.findViewById(R.id.txtdepth);
        txtPackageContents = (TextView) rootView.findViewById(R.id.txtPackageContents);
        txtAddtoCart = (TextView) rootView.findViewById(R.id.txtAddtoCart);

        spinnerVariant = (SearchableSpinner) rootView.findViewById(R.id.spinnerVariant);

        txtAddtoCart.setOnClickListener(this);
        ivItem.setOnClickListener(this);
        availOffer.setOnClickListener(this);

        // Date Picker Dailog integration and validation
        final Calendar myCalendar = Calendar.getInstance();
        final DatePickerDialog.OnDateSetListener datePickerListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                 //In which you need put here
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.UK);

                String date = sdf.format(myCalendar.getTime());

                Date today = null;
                Date selectedDate = null;

                try {
                    today = sdf.parse(DateUtils.getDate(myFormat));       // gets the current date
                    selectedDate = sdf.parse(date);                       // gets the date from date picker
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                // shows err when selected date before the current date
                if (selectedDate.before(today)) {
                    deliveryDatePicker.setText("");
                    deliveryDatePicker.setError("Please select valid date");
                    Toast.makeText(getContext(),"Date specified [" + selectedDate + "] is before today [" + today + "]",Toast.LENGTH_SHORT).show();
                } else {
                    deliveryDatePicker.setError(null);
                    deliveryDatePicker.setText(sdf.format(myCalendar.getTime()));
                }

            }

        };

        // onClick for edit text to popup the date picker
        deliveryDatePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog datePickerDialog =  new DatePickerDialog(getContext(), datePickerListener, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH));
                String myDate = "2019/08/12";
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MMM/yyyy");
                Date date = null;
                try {
                    //date = sdf.parse(myDate);   // If a it requires selected date is mentioned
                    date = sdf.parse(DateUtils.getDate(myFormat));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                long millis = date.getTime();
                datePickerDialog.getDatePicker().setMinDate(millis);
                datePickerDialog.show();

            }
        });


        Picasso.with(getContext())
                .load("https://avatars2.githubusercontent.com/u/8110201?v=4")
                .placeholder(R.drawable.no_img)
                .into(ivItem);



        mImageList = new ArrayList<ImageModel>();

        // To disable Bottom navigation bar
        //((MainActivity) getActivity()).SetNavigationVisibiltity(false);

        rvImages = (RecyclerView) rootView.findViewById(R.id.rvImages);
        linearLayoutManager = new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false);
        rvImages.setLayoutManager(linearLayoutManager);

        imagesAdapter = new ImagesAdapter();

        setDataListItems();
        initRecyclerView();

        ArrayAdapter adapter = new ArrayAdapter(getContext(), R.layout.support_simple_spinner_dropdown_item, varient);
        spinnerVariant.setAdapter(adapter);

        spinnerVariant.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedVar = spinnerVariant.getSelectedItem().toString();

                if(!selectedVar.equals("")){

                    for(int k= 0;i <= varientDtoLst.size();k++){

                        if(varientDtoLst.get(k).getMcode().equals(selectedVar)){
                            updateUI(varientDtoLst.get(k));
                            return;
                        }
                    }

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });




    }

    public void updateUI(VariantDTO varientDtoLst){
        txtItemName.setText(varientDtoLst.getMcode());
        txtDescreption.setText(varientDtoLst.getMDescriptionLong());
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){

            case R.id.txtAddtoCart:
                // sending params to next screen through bundle
                Bundle bundle = new Bundle();
                bundle.putString("itemName","myItem");
                CartFragment cartFragment = new CartFragment();
                cartFragment.setArguments(bundle);
                FragmentUtils.replaceFragmentWithBackStack(getActivity(),R.id.container,cartFragment);
                break;

            case R.id.ivItem:
                showImage("https://avatars2.githubusercontent.com/u/8110201?v=4");
                break;

            case R.id.availOffer:
                showAvailableOffers();
                break;


        }
    }



    private void setDataListItems() {

        mImageList.add(new ImageModel("https://avatars2.githubusercontent.com/u/8110201?v=4"));
        mImageList.add(new ImageModel("https://avatars0.githubusercontent.com/u/276286?v=4"));
        mImageList.add(new ImageModel("https://avatars3.githubusercontent.com/u/13552664?v=4"));
        mImageList.add(new ImageModel("https://avatars1.githubusercontent.com/u/4929406?v=4"));
        mImageList.add(new ImageModel("https://avatars2.githubusercontent.com/u/15170090?v=4"));
        mImageList.add(new ImageModel("https://avatars3.githubusercontent.com/u/678974?v=4"));
        mImageList.add(new ImageModel("https://avatars1.githubusercontent.com/u/6963510?v=4"));

    }

    private void initRecyclerView(){

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(rvImages.getContext(), linearLayoutManager.getOrientation());
        rvImages.addItemDecoration(dividerItemDecoration);

        imagesAdapter = new ImagesAdapter(getContext(), mImageList, new ImagesAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int pos) {
                showImage(mImageList.get(pos).getImage());
            }
        });
        rvImages.setAdapter(imagesAdapter);

    }

    private void showImage(String img){
        final Dialog mDialog = new Dialog(getContext());
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialog.setCancelable(true);
        mDialog.setContentView(R.layout.image_viewpager);
        CardView btnimgClose = (CardView) mDialog.findViewById(R.id.closedialog);
        TabLayout tabIndicator = (TabLayout) mDialog.findViewById(R.id.tab_indicator);
        
        Integer[] IMAGES= {R.drawable.one,R.drawable.two,R.drawable.three};
        ArrayList<Integer> ImagesArray = new ArrayList<Integer>();

        for(int i=0;i<IMAGES.length;i++)
            ImagesArray.add(IMAGES[i]);

        mPager = (ViewPager) mDialog.findViewById(R.id.pager);


        mPager.setAdapter(new SlideImageAdapter(getContext(),mImageList));

        // setup tablayout with viewpager

        tabIndicator.setupWithViewPager(mPager);


        btnimgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mDialog.dismiss();
            }
        });
        mDialog.show();



    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void showAvailableOffers(){
        final Dialog mDialog = new Dialog(getContext());
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialog.setCancelable(true);
        mDialog.setContentView(R.layout.dialog_offer);
        RecyclerView lstoffers = (RecyclerView) mDialog.findViewById(R.id.lstoffers);
        NestedScrollView nested = (NestedScrollView) mDialog.findViewById(R.id.nested);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        lstoffers.setLayoutManager(linearLayoutManager);
        lstoffers.setHasFixedSize(true);

        ArrayList<String> items = new ArrayList<>();
        items.add("NA");
        items.add("NA");
        items.add("NA");
        items.add("NA");
        items.add("NA");
        items.add("NA");
        items.add("NA");
        items.add("NA");
        items.add("NA");
        items.add("NA");
        items.add("NA");
        items.add("NA");
        items.add("NA");
        items.add("NA");
        items.add("NA");


        OffersAdapter adapter = new OffersAdapter(getContext(), items, nested);
        lstoffers.setAdapter(adapter);
        lstoffers.setNestedScrollingEnabled(false);


        mDialog.show();



    }

    @Override
    public void onResume() {
        super.onResume();
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(getString(R.string.toolbar_product));
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayUseLogoEnabled(false);

    }
}
