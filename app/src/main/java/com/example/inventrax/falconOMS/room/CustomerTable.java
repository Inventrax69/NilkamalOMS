package com.example.inventrax.falconOMS.room;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity
public class CustomerTable {

    @PrimaryKey @NonNull
    @ColumnInfo(name = "custId")
    public String custId;

    @ColumnInfo(name = "custName")
    public String custName;

    @ColumnInfo(name = "custCode")
    public String custCode;

     @ColumnInfo(name = "custType")
    public String custType;

    @ColumnInfo(name = "division")
    public String division;

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

    public CustomerTable(String custId, String custName, String custCode, String custType, String division, String connectedDepo, String mobile, String primaryID, String salesDistrict, String zone) {
        this.custId = custId;
        this.custName = custName;
        this.custCode = custCode;
        this.custType = custType;
        this.division = division;
        this.connectedDepo = connectedDepo;
        this.mobile = mobile;
        this.primaryID = primaryID;
        this.salesDistrict = salesDistrict;
        this.zone = zone;
    }




}
