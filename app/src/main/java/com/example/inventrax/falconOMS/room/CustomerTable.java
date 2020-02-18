package com.example.inventrax.falconOMS.room;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import java.io.Serializable;

@Entity(indices = {@Index(value = {"customerId"},
        unique = true)})
public class CustomerTable implements Serializable {

    @PrimaryKey(autoGenerate = true)
    public int id;

    @NonNull
    @ColumnInfo(name = "customerId")
    public String customerId;

    @ColumnInfo(name = "customerName")
    public String customerName;

    @ColumnInfo(name = "customerCode")
    public String customerCode;

    @ColumnInfo(name = "customerType")
    public String customerType;

    @ColumnInfo(name = "customerTypeID")
    public String customerTypeID;

    @ColumnInfo(name = "division")
    public String division;

    @ColumnInfo(name = "divisionId")
    public String divisionId;

    @ColumnInfo(name = "connectedDepo")
    public String connectedDepo;

    @ColumnInfo(name = "mobile")
    public String mobile;

    @ColumnInfo(name = "primaryID")
    public String primaryID;

    @ColumnInfo(name = "salesDistrict")
    public String salesDistrict;

    @ColumnInfo(name = "zone")
    public String zone;

    @ColumnInfo(name = "city")
    public String city;



    @ColumnInfo(name = "timeStamp")
    public long timestamp;


    @Ignore
    public CustomerTable(String customerId, String customerName, String customerCode, String customerType,String customerTypeID, String division, String divisionId, String connectedDepo, String mobile, String primaryID, String salesDistrict, String zone,String city) {
        this.customerId = customerId;
        this.customerName = customerName;
        this.customerCode = customerCode;
        this.customerType = customerType;
        this.customerTypeID = customerTypeID;
        this.division = division;
        this.divisionId = divisionId;
        this.connectedDepo = connectedDepo;
        this.mobile = mobile;
        this.primaryID = primaryID;
        this.salesDistrict = salesDistrict;
        this.zone = zone;
        this.city = city;
    }


    public CustomerTable(@NonNull String customerId, String customerName, String customerCode, String customerType,String customerTypeID, String division, String connectedDepo, String mobile, String primaryID, String salesDistrict, String zone, long timestamp,String city) {
        this.customerId = customerId;
        this.customerId = customerId;
        this.customerName = customerName;
        this.customerCode = customerCode;
        this.customerType = customerType;
        this.customerTypeID = customerTypeID;
        this.division = division;
        this.connectedDepo = connectedDepo;
        this.mobile = mobile;
        this.primaryID = primaryID;
        this.salesDistrict = salesDistrict;
        this.zone = zone;
        this.timestamp = timestamp;
        this.city = city;
    }
}
