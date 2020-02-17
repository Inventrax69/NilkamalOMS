package com.example.inventrax.falconOMS.room;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

import java.io.Serializable;

/*@Entity(indices = {@Index(value = {"cuchid"},
        unique = true)})*/
@Entity
public class CartHeader implements Serializable {

    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "customerID")
    public int customerID;

    @ColumnInfo(name = "customerName")
    public String customerName;

    @ColumnInfo(name = "cartHeaderID")
    public int cartHeaderID;

    @ColumnInfo(name = "creditLimit")
    public double creditLimit;

    @ColumnInfo(name = "isInActive")
    public int isInActive;

    @ColumnInfo(name = "isCreditLimit")
    public int isCreditLimit;

    @ColumnInfo(name = "isApproved")
    public int isApproved;

    @ColumnInfo(name = "shipToPartyId")
    public int shipToPartyId=0;

    @ColumnInfo(name = "isPriority")
    public int isPriority=0;

    @ColumnInfo(name = "isFulfillmentCompleted")
    public int isFulfillmentCompleted;

    @ColumnInfo(name = "timeStamp")
    public String timeStamp;

    @ColumnInfo(name = "isUpdated")
    public int isUpdated=0;

    @ColumnInfo(name = "totalPrice")
    public String totalPrice;

    @ColumnInfo(name = "totalPriceWithTax")
    public String totalPriceWithTax;

    public CartHeader(int customerID,String customerName,double creditLimit,  int cartHeaderID,
                      int isInActive, int isCreditLimit, int isApproved,int isFulfillmentCompleted,String timeStamp,
                      String totalPrice,String totalPriceWithTax) {
        this.customerID = customerID;
        this.customerName = customerName;
        this.creditLimit = creditLimit;
        this.cartHeaderID = cartHeaderID;
        this.isInActive = isInActive;
        this.isCreditLimit = isCreditLimit;
        this.isApproved = isApproved;
        this.isFulfillmentCompleted = isFulfillmentCompleted;
        this.timeStamp=timeStamp;
        this.totalPrice=totalPrice;
        this.totalPriceWithTax=totalPriceWithTax;
    }

    @Ignore
    public CartHeader(){

    }

}
