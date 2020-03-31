package com.example.inventrax.falconOMS.adapters;

import android.app.Activity;
import android.app.Dialog;
import android.app.Service;
import android.content.Context;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.inventrax.falconOMS.R;
import com.example.inventrax.falconOMS.common.Common;
import com.example.inventrax.falconOMS.common.constants.EndpointConstants;
import com.example.inventrax.falconOMS.common.constants.ErrorMessages;
import com.example.inventrax.falconOMS.interfaces.ApiInterface;
import com.example.inventrax.falconOMS.pojos.AvailableStockDTO;
import com.example.inventrax.falconOMS.pojos.CartDetailsListDTO;
import com.example.inventrax.falconOMS.pojos.CartHeaderListDTO;
import com.example.inventrax.falconOMS.pojos.OMSCoreMessage;
import com.example.inventrax.falconOMS.pojos.OMSExceptionMessage;
import com.example.inventrax.falconOMS.pojos.ProductDiscountDTO;
import com.example.inventrax.falconOMS.room.AppDatabase;
import com.example.inventrax.falconOMS.room.CartDetails;
import com.example.inventrax.falconOMS.room.RoomAppDatabase;
import com.example.inventrax.falconOMS.room.VariantTable;
import com.example.inventrax.falconOMS.services.RestService;
import com.example.inventrax.falconOMS.util.DialogUtils;
import com.example.inventrax.falconOMS.util.ExceptionLoggerUtils;
import com.example.inventrax.falconOMS.util.NetworkUtils;
import com.example.inventrax.falconOMS.util.ProgressDialogUtils;
import com.example.inventrax.falconOMS.util.SnackbarUtils;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CartDetailsAdapter extends RecyclerView.Adapter<CartDetailsAdapter.SubItemViewHolder> {

    private List<CartDetailsListDTO> cartItemList;
    private CartHeaderListDTO cartHeaderListDTO;
    private List<Boolean> isUpdatedBoolean;
    private OnItemClickListener mlistener;
    private Context context;
    private AppDatabase db;
    private Drawable editDrawable, updateDrawable, updatePriority;
    private TextView txtOrderFulfilment;
    private TextView txtApplyOffers;
    private Common common;
    private RestService restService;
    private OMSCoreMessage core;
    private ErrorMessages errorMessages;

    public CartDetailsAdapter(Context applicationContext, CartHeaderListDTO cartHeaderListDTO, List<CartDetailsListDTO> cartItemList, OnItemClickListener mlistener) {
        this.context = applicationContext;
        this.cartItemList = cartItemList;
        this.cartHeaderListDTO = cartHeaderListDTO;
        common = new Common();
        restService = new RestService();
        core = new OMSCoreMessage();
        errorMessages = new ErrorMessages();
        db = new RoomAppDatabase(this.context).getAppDatabase();
        editDrawable = context.getResources().getDrawable(R.drawable.ic_edit_black_24dp);
        updateDrawable = context.getResources().getDrawable(R.drawable.ic_refresh_black_24dp);
        updatePriority = context.getResources().getDrawable(R.drawable.ic_priority);
        txtOrderFulfilment = (TextView) ((Activity) context).findViewById(R.id.txtOrderFulfilment);
        txtApplyOffers = (TextView) ((Activity) context).findViewById(R.id.txtApplyOffers);
        isUpdatedBoolean = new ArrayList<>();
        for (int i = 0; i < cartItemList.size(); i++) {
            isUpdatedBoolean.add(true);
        }
        this.mlistener = mlistener;
    }

    @NonNull
    @Override
    public SubItemViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_cart_item, viewGroup, false);
        return new SubItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final SubItemViewHolder viewHolder, final int i) {

        final CartDetailsListDTO item = cartItemList.get(i);

        List<CartDetails> cartDetails = db.cartDetailsDAO().getCartDetailsList(cartItemList.get(i).getCartDetailsID());

        viewHolder.txtItemName.setText(item.getMCode());
        viewHolder.txtItemDesc.setText(item.getMDescription());

        viewHolder.etQtyCart.setText(item.getQuantity());
        final boolean getUpdateCount = db.cartHeaderDetailsDao().getUpdateCount();
        if (!getUpdateCount) {
            viewHolder.txtPrice.setText("");
            viewHolder.txtOriginalPrice.setText("");
            txtOrderFulfilment.setText(context.getString(R.string.sync_cart));
            txtApplyOffers.setVisibility(View.INVISIBLE);
            viewHolder.txtOfferAvaiable.setVisibility(View.INVISIBLE);
            viewHolder.txtAvailableItem.setVisibility(View.INVISIBLE);
        } else {
            txtOrderFulfilment.setText(context.getString(R.string.order_fulfilment));
            txtApplyOffers.setVisibility(View.VISIBLE);
            viewHolder.txtOfferAvaiable.setVisibility(View.VISIBLE);
            viewHolder.txtAvailableItem.setVisibility(View.VISIBLE);
            Animation anim = new AlphaAnimation(0.0f, 1.0f);
            anim.setDuration(500); //You can manage the blinking time with this parameter
            anim.setStartOffset(20);
            anim.setRepeatMode(Animation.REVERSE);
            anim.setRepeatCount(Animation.INFINITE);
            viewHolder.txtAvailableItem.startAnimation(anim);
            viewHolder.txtPrice.setText(Html.fromHtml("Rs. :" + item.getOfferValue()));
            if (item.getTotalPrice().equals(item.getOfferValue()))
                viewHolder.txtOriginalPrice.setText(Html.fromHtml(""));
            else
                viewHolder.txtOriginalPrice.setText(Html.fromHtml(item.getTotalPrice()));
            viewHolder.txtOriginalPrice.setPaintFlags(viewHolder.txtOriginalPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
          /*  if (item.getTotalPrice().equals(item.getOfferValue()))
                viewHolder.txtPrice.setText(Html.fromHtml("Rs. :" + item.getTotalPrice()));
            else
                viewHolder.txtPrice.setText(Html.fromHtml("Rs. : <strike>" + item.getTotalPrice() + "</strike> " + item.getOfferValue()));*/
        }

        // viewHolder.isItemInactive.setVisibility(View.GONE);

        if (item.getIsInActive() != null) {
            if (item.getIsInActive()) {
                viewHolder.isItemInactive.setVisibility(View.VISIBLE);
            } else {
                viewHolder.isItemInactive.setVisibility(View.GONE);
            }
        } else {
            viewHolder.isItemInactive.setVisibility(View.GONE);
        }
        //viewHolder.txtPriority.setText(""+item.getMaterialPriorityID());

        if (cartDetails.size() > 0) {
            viewHolder.txtOfferAvaiable.setText("Offer item (s)");
            viewHolder.txtOfferAvaiable.setVisibility(View.VISIBLE);
        } else {
            viewHolder.txtOfferAvaiable.setVisibility(View.INVISIBLE);
        }

        if (!item.getDiscountID().equals("") && !item.getDiscountID().equals("0")) {
            viewHolder.ivAppliedOffer.setVisibility(View.VISIBLE);
        } else {
            viewHolder.ivAppliedOffer.setVisibility(View.GONE);
        }

        if (item.getMaterialPriorityID() == 1) {
            viewHolder.txtPriority.setImageDrawable(updatePriority);
        } else {
            viewHolder.txtPriority.setImageDrawable(null);
        }

        Picasso.with(context)
                .load(cartItemList.get(i).getFileNames())
                .placeholder(R.drawable.no_img)
                .into(viewHolder.ivItem);

        if (cartHeaderListDTO.getIsApproved() == 0 || cartHeaderListDTO.getIsApproved() == 6 || cartHeaderListDTO.getIsApproved() == 4)
            viewHolder.imageEdit.setVisibility(View.VISIBLE);
        else
            viewHolder.imageEdit.setVisibility(View.INVISIBLE);

        if (isUpdatedBoolean.get(i)) {
            viewHolder.imageEdit.setImageDrawable(editDrawable);
            viewHolder.etQtyCart.setEnabled(false);
        } else {
            viewHolder.imageEdit.setImageDrawable(updateDrawable);
            viewHolder.etQtyCart.setEnabled(true);
        }

        viewHolder.imageEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (viewHolder.etQtyCart.isEnabled()) {
                    if (viewHolder.etQtyCart.getText().toString().isEmpty()) {
                        InputMethodManager imm = (InputMethodManager) context.getSystemService(Service.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(viewHolder.etQtyCart.getWindowToken(), 0);
                        SnackbarUtils.showSnackbarLengthShort((CoordinatorLayout) ((Activity) context).findViewById(R.id.coordinatorLayout), "Qty must not be empty", ContextCompat.getColor(context, R.color.dark_red), Snackbar.LENGTH_SHORT);
                    } else {
                        int QtyCount = Integer.parseInt(viewHolder.etQtyCart.getText().toString());
                        if (QtyCount != 0) {
                            VariantTable selectedVariant = db.variantDAO().getMaterial(Integer.parseInt(item.getMaterialMasterID()));
                            if ((Integer.parseInt(viewHolder.etQtyCart.getText().toString()) % (selectedVariant.stackSize)) == 0) {
                                viewHolder.imageEdit.setImageDrawable(editDrawable);
                                viewHolder.etQtyCart.setEnabled(false);
                                isUpdatedBoolean.set(i, true);
                                db.cartDetailsDAO().updateQantity(viewHolder.etQtyCart.getText().toString(), item.getMaterialMasterID(), String.valueOf(item.getCustomerID()), item.getCartHeaderID());
                                cartItemList.get(i).setQuantity(viewHolder.etQtyCart.getText().toString());
                                notifyItemChanged(i);
                                notifyDataSetChanged();
                            } else {
                                InputMethodManager imm = (InputMethodManager) context.getSystemService(Service.INPUT_METHOD_SERVICE);
                                imm.hideSoftInputFromWindow(viewHolder.etQtyCart.getWindowToken(), 0);
                                SnackbarUtils.showSnackbarLengthShort((CoordinatorLayout) ((Activity) context).findViewById(R.id.coordinatorLayout), "Stack size is not correct please enter the mutiples of " + selectedVariant.stackSize + " Eg : " + stackSizeQty(Integer.parseInt((!viewHolder.etQtyCart.getText().toString().isEmpty()) ? viewHolder.etQtyCart.getText().toString() : "1"), selectedVariant.stackSize), ContextCompat.getColor(context, R.color.dark_red), Snackbar.LENGTH_SHORT);
                            }
                        } else {
                            InputMethodManager imm = (InputMethodManager) context.getSystemService(Service.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(viewHolder.etQtyCart.getWindowToken(), 0);
                            SnackbarUtils.showSnackbarLengthShort((CoordinatorLayout) ((Activity) context).findViewById(R.id.coordinatorLayout), "Qty must not be zero", ContextCompat.getColor(context, R.color.dark_red), Snackbar.LENGTH_SHORT);
                        }
                    }

                } else {
                    viewHolder.imageEdit.setImageDrawable(updateDrawable);
                    viewHolder.etQtyCart.setEnabled(true);
                    isUpdatedBoolean.set(i, true);
                }
            }
        });


        viewHolder.txtAvailableItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!getUpdateCount) {
                    SnackbarUtils.showSnackbarLengthShort((CoordinatorLayout) ((Activity) context).findViewById(R.id.coordinatorLayout), "Sync cart to online", ContextCompat.getColor(context, R.color.dark_red), Snackbar.LENGTH_SHORT);
                } else {
                    GetStock(cartItemList.get(i).getCartDetailsID());
                }
            }
        });

        viewHolder.txtOfferAvaiable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!getUpdateCount) {
                    SnackbarUtils.showSnackbarLengthShort((CoordinatorLayout) ((Activity) context).findViewById(R.id.coordinatorLayout), "Sync cart to online", ContextCompat.getColor(context, R.color.dark_red), Snackbar.LENGTH_SHORT);
                } else {
                    final Dialog dialog = new Dialog(context);
                    dialog.setContentView(R.layout.offer_item_recycler);
                    dialog.setCancelable(false);
                    Window window = dialog.getWindow();
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                    window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    final RecyclerView recyclerView;
                    recyclerView = dialog.findViewById(R.id.recyclerView);
                    recyclerView.setHasFixedSize(true);

                    ImageView btnClOSE = dialog.findViewById(R.id.btnClOSE);

                    LinearLayoutManager layoutManager = new LinearLayoutManager(context);
                    recyclerView.setLayoutManager(layoutManager);

                    // Log.v("ABCDE",cartItemList.get(i).getCartDetailsID());

                    final OfferItemsAdapter offerItemsAdapter = new OfferItemsAdapter(context, db.cartDetailsDAO().getCartDetailsList(cartItemList.get(i).getCartDetailsID()), new OfferItemsAdapter.OnItemClickListener() {
                        @Override
                        public void OnItemClick(int pos) {

                        }
                    });

                    recyclerView.setAdapter(offerItemsAdapter);

                    btnClOSE.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialog.dismiss();
                        }
                    });

                    dialog.show();
                }
            }
        });

        viewHolder.ivAppliedOffer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, cartItemList.get(i).getDiscountText(), Toast.LENGTH_LONG).show();
            }
        });

    }


    public void GetStock(final String cartDetailsID) {

        OMSCoreMessage message = new OMSCoreMessage();
        message = common.SetAuthentication(EndpointConstants.OrderFulfilment_DTO, context);
        CartDetailsListDTO cartDetailsListDTO = new CartDetailsListDTO();
        cartDetailsListDTO.setCartDetailsID(cartDetailsID);
        message.setEntityObject(cartDetailsListDTO);

        Call<OMSCoreMessage> call = null;
        ApiInterface apiService = RestService.getClient().create(ApiInterface.class);

        ProgressDialogUtils.showProgressDialog("Please wait..");

        call = apiService.GetStock(message);

        try {
            //Getting response from the method
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
                                common.showAlertType(omsExceptionMessage, (Activity) context, context);
                            }

                            ProgressDialogUtils.closeProgressDialog();

                        } else {

                            ProgressDialogUtils.closeProgressDialog();

                            try {

                                JSONArray getAvailableStockDTO = new JSONArray((String) core.getEntityObject());
                                AvailableStockDTO availableStockDTO = new AvailableStockDTO();

                                final List<AvailableStockDTO> availableStockDTOS = new ArrayList<>();

                                if (getAvailableStockDTO != null && getAvailableStockDTO.length() > 0) {
                                    for (int i = 0; i < getAvailableStockDTO.length(); i++) {
                                        availableStockDTO = new Gson().fromJson(getAvailableStockDTO.getJSONObject(i).toString(), AvailableStockDTO.class);
                                        availableStockDTOS.add(availableStockDTO);
                                    }

                                    if (availableStockDTOS.size() > 0) {

                                        final Dialog dialog = new Dialog(context);
                                        dialog.setContentView(R.layout.avaible_stock_recycler);
                                        dialog.setCancelable(false);
                                        Window window = dialog.getWindow();
                                        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                                        window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                                        final RecyclerView credit_recycler;
                                        credit_recycler = dialog.findViewById(R.id.recyclerView);
                                        credit_recycler.setHasFixedSize(true);

                                        ImageView btnClOSE = dialog.findViewById(R.id.btnClOSE);

                                        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
                                        credit_recycler.setLayoutManager(layoutManager);

                                        final AvailableStockAdapter availableStockDTO1 = new AvailableStockAdapter(context, availableStockDTOS, new AvailableStockAdapter.OnItemClickListener() {
                                            @Override
                                            public void OnItemClick(int pos) {

                                            }
                                        });

                                        credit_recycler.setAdapter(availableStockDTO1);

                                        btnClOSE.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                dialog.dismiss();
                                            }
                                        });

                                        dialog.show();

                                    } else {
                                        SnackbarUtils.showSnackbarLengthShort((CoordinatorLayout) ((Activity) context).findViewById(R.id.coordinatorLayout), "No Stock Available", ContextCompat.getColor(context, R.color.dark_red), Snackbar.LENGTH_SHORT);
                                    }

                                } else {
                                    SnackbarUtils.showSnackbarLengthShort((CoordinatorLayout) ((Activity) context).findViewById(R.id.coordinatorLayout), "No Stock Available", ContextCompat.getColor(context, R.color.dark_red), Snackbar.LENGTH_SHORT);
                                }

                                notifyDataSetChanged();

                            } catch (Exception ex) {
                                ProgressDialogUtils.closeProgressDialog();
                                SnackbarUtils.showSnackbarLengthShort((CoordinatorLayout) ((Activity) context).findViewById(R.id.coordinatorLayout), "Error While Stock Available", ContextCompat.getColor(context, R.color.dark_red), Snackbar.LENGTH_SHORT);
                            }
                        }
                    }
                }

                // response object fails
                @Override
                public void onFailure(Call<OMSCoreMessage> call, Throwable throwable) {
                    if (NetworkUtils.isInternetAvailable(context)) {
                        DialogUtils.showAlertDialog((Activity) context, errorMessages.EMC_0001);
                    } else {
                        DialogUtils.showAlertDialog((Activity) context, errorMessages.EMC_0014);
                    }
                    ProgressDialogUtils.closeProgressDialog();
                }
            });
        } catch (Exception ex) {
            try {
                ExceptionLoggerUtils.createExceptionLog(ex.toString(), "SADListAdapter", "001", context);
            } catch (IOException e) {
                e.printStackTrace();
            }
            ProgressDialogUtils.closeProgressDialog();
            DialogUtils.showAlertDialog((Activity) context, errorMessages.EMC_0003);
        }
    }


    @Override
    public int getItemCount() {
        return cartItemList.size();
    }

    public class SubItemViewHolder extends RecyclerView.ViewHolder {

        private TextView txtItemName, txtItemDesc, txtPrice, txtOriginalPrice, isItemInactive, txtAvailableItem, txtOfferAvaiable;
        private EditText etQtyCart;
        private ImageView ivItem, ivDeleteItem, imageEdit, txtPriority, ivAppliedOffer;

        public SubItemViewHolder(View view) {
            super(view);
            // Initializing views
            txtItemName = (TextView) view.findViewById(R.id.txtItemName);
            txtItemDesc = (TextView) view.findViewById(R.id.txtItemDesc);
            txtPrice = (TextView) view.findViewById(R.id.txtPrice);
            txtOriginalPrice = (TextView) view.findViewById(R.id.txtOriginalPrice);
            txtAvailableItem = (TextView) view.findViewById(R.id.txtAvailableItem);
            txtOfferAvaiable = (TextView) view.findViewById(R.id.txtOfferAvaiable);
            txtPriority = (ImageView) view.findViewById(R.id.txtPriority);
            isItemInactive = (TextView) view.findViewById(R.id.isItemInactive);
            ivItem = (ImageView) view.findViewById(R.id.ivItem);
            imageEdit = (ImageView) view.findViewById(R.id.imageEdit);
            ivDeleteItem = (ImageView) view.findViewById(R.id.ivDeleteItem);
            ivAppliedOffer = (ImageView) view.findViewById(R.id.ivAppliedOffer);
            etQtyCart = (EditText) view.findViewById(R.id.etQtyCart);


            ivDeleteItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (mlistener != null) {
                        int pos = getAdapterPosition();
                        if (pos != RecyclerView.NO_POSITION) {
                            mlistener.onDeletClick(pos);
                        }
                    }

                }
            });
        }
    }

    // Item Click listener interface
    public interface OnItemClickListener {
        void onDeletClick(int pos);
    }

    public int stackSizeQty(int qty, int size) {
        return (qty != 0) ? ((qty % size) != 0) ? ((qty / size) * size) + size : ((qty / size) * size) : size;
    }
}
