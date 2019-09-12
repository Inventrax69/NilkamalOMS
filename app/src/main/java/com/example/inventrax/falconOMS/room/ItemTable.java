package com.example.inventrax.falconOMS.room;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

import java.io.Serializable;

@Entity
public class ItemTable implements Serializable {

    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "modelID")
    public String modelID;

    @ColumnInfo(name = "modelCode")
    public String modelCode;

    @ColumnInfo(name = "modelDescription")
    public String modelDescription;

     @ColumnInfo(name = "imgPath")
    public String imgPath;

    @ColumnInfo(name = "varientList")
    public String varientList;

    @ColumnInfo(name="price")
    public long price;

    @ColumnInfo(name="discount")
    public long discount;

    @ColumnInfo(name="timeStamp")
    public long timestamp;


    public ItemTable(String modelID, String modelCode, String modelDescription, String imgPath, String varientList) {
        this.modelID = modelID;
        this.modelCode = modelCode;
        this.modelDescription = modelDescription;
        this.imgPath = imgPath;
        this.varientList = varientList;
    }

    @Ignore
    public ItemTable(String modelID, String modelCode, String modelDescription, String imgPath, String varientList, long price, long discount, long timestamp) {
        this.modelID = modelID;
        this.modelCode = modelCode;
        this.modelDescription = modelDescription;
        this.imgPath = imgPath;
        this.varientList = varientList;
        this.price = price;
        this.discount = discount;
        this.timestamp = timestamp;
    }



    @Ignore
    public ItemTable(){

    }
}
