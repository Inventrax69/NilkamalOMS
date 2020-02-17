package com.example.inventrax.falconOMS.room;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

import java.io.Serializable;

@Entity
public class UserDivisionCustTable implements Serializable {

    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "customerId")
    public String customerId;

    @ColumnInfo(name = "divisionId")
    public String divisionId;



    public UserDivisionCustTable(String customerId, String divisionId) {
        this.customerId = customerId;
        this.divisionId = divisionId;
    }

    @Ignore
    public UserDivisionCustTable(){

    }





}


