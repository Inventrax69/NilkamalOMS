package com.example.inventrax.falconOMS.room;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity
public class CustomerTable {

    @PrimaryKey(autoGenerate = true)
    public int id;

    @NonNull
    @ColumnInfo(name = "customerId")
    public String customerId;

    @ColumnInfo(name = "custName")
    public String custName;

    @ColumnInfo(name = "custCode")
    public String custCode;

     @ColumnInfo(name = "custType")
    public String custType;

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

    @ColumnInfo(name="timeStamp")
    public long timestamp;

    @Ignore
    public CustomerTable(String customerId, String custName, String custCode, String custType, String division, String divisionId, String connectedDepo, String mobile, String primaryID, String salesDistrict, String zone) {
        this.customerId = customerId;
        this.custName = custName;
        this.custCode = custCode;
        this.custType = custType;
        this.division = division;
        this.divisionId = divisionId;
        this.connectedDepo = connectedDepo;
        this.mobile = mobile;
        this.primaryID = primaryID;
        this.salesDistrict = salesDistrict;
        this.zone = zone;
    }


    public CustomerTable(@NonNull String customerId, String custName, String custCode, String custType, String division, String connectedDepo, String mobile, String primaryID, String salesDistrict, String zone, long timestamp) {
        this.customerId = customerId;
        this.custName = custName;
        this.custCode = custCode;
        this.custType = custType;
        this.division = division;
        this.connectedDepo = connectedDepo;
        this.mobile = mobile;
        this.primaryID = primaryID;
        this.salesDistrict = salesDistrict;
        this.zone = zone;
        this.timestamp = timestamp;
    }
}
