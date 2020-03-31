package com.example.inventrax.falconOMS.room;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

@Entity/*(indices = {@Index(value = {"materialID"}, unique = true)})*/
public class CartDetails implements Serializable {

    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "materialID")
    public String materialID;

    @ColumnInfo(name = "mCode")
    public String mCode;

    @ColumnInfo(name = "mDescription")
    public String mDescription;

    @ColumnInfo(name = "deliveryDate")
    public String deliveryDate;

    @ColumnInfo(name = "quantity")
    public String quantity;

    @ColumnInfo(name = "timeStamp")
    public String timeStamp;

    @ColumnInfo(name = "imgPath")
    public String imgPath;

    @ColumnInfo(name = "price")
    public String price;

    @ColumnInfo(name = "totalPrice")
    public String totalPrice;

    @ColumnInfo(name = "offerValue")
    public String offerValue;

    @ColumnInfo(name = "offerItemCartDetailsID")
    public String offerItemCartDetailsID;

    @ColumnInfo(name = "cartHeaderId")
    public String cartHeaderId;

    @ColumnInfo(name = "cartDetailsId")
    public String cartDetailsId;

    @ColumnInfo(name = "customerId")
    public int customerId;

    @ColumnInfo(name = "isInActive")
    public boolean isInActive;

    @ColumnInfo(name = "isPriority")
    public int isPriority = 0;

    @ColumnInfo(name = "isUpdated")
    public int isUpdated = 0;

    @ColumnInfo(name = "discountID")
    public String discountID;

    @ColumnInfo(name = "discountText")
    public String discountText;

    @ColumnInfo(name = "gst")
    public String gst;

    @ColumnInfo(name = "tax")
    public String tax;

    @ColumnInfo(name = "subtotal")
    public String subtotal;

    @ColumnInfo(name = "HSNCode")
    public String HSNCode;

    @ColumnInfo(name = "discountedPrice")
    public String discountedPrice;



    public CartDetails(String cartHeaderId, String materialID, String mCode, String mDescription,
                       String deliveryDate, String quantity, String imgPath,
                       String price, boolean isInActive, String cartDetailsId, int customerId, int isUpdated, int isPriority,
                       String totalPrice, String offerValue, String offerItemCartDetailsID,
                       String discountID, String discountText, String gst, String tax, String subtotal, String HSNCode, String discountedPrice) {
        this.materialID = materialID;
        this.mCode = mCode;
        this.mDescription = mDescription;
        this.deliveryDate = deliveryDate;
        this.quantity = quantity;
        this.imgPath = imgPath;
        this.price = price;
        this.isInActive = isInActive;
        this.cartHeaderId = cartHeaderId;
        this.cartDetailsId = cartDetailsId;
        this.customerId = customerId;
        this.isUpdated = isUpdated;
        this.isPriority = isPriority;
        this.totalPrice = totalPrice;
        this.offerValue = offerValue;
        this.offerItemCartDetailsID = offerItemCartDetailsID;
        this.discountID = discountID;
        this.discountText = discountText;
        this.gst = gst;
        this.tax = tax;
        this.subtotal = subtotal;
        this.HSNCode = HSNCode;
        this.discountedPrice = discountedPrice;
    }

    @Ignore
    public CartDetails(String materialID, String mCode, String quantity, String imgPath, String price, String cartHeaderId, String cartDetailsId) {
        this.materialID = materialID;
        this.mCode = mCode;
        this.quantity = quantity;
        this.imgPath = imgPath;
        this.price = price;
        this.cartHeaderId = cartHeaderId;
        this.cartDetailsId = cartDetailsId;
    }

    @Ignore
    public CartDetails() {
    }

}
