package com.example.inventrax.falconOMS.room;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;

import java.io.Serializable;

@Entity(indices = {@Index(value = {"modelID"},
        unique = true)})
public class ItemTable implements Serializable {

    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "modelID")
    public String modelID;

    @ColumnInfo(name = "divisionID")
    public String divisionID;

    @ColumnInfo(name = "segmentID")
    public String segmentID;

    @ColumnInfo(name = "modelCode")
    public String modelCode;

    @ColumnInfo(name = "modelDescription")
    public String modelDescription;

    @ColumnInfo(name = "imgPath")
    public String imgPath;

    @ColumnInfo(name = "price")
    public String price;

    @ColumnInfo(name = "discountCount")
    public String discountCount;

    @ColumnInfo(name = "discountId")
    public String discountId;

    @ColumnInfo(name = "discountDesc")
    public String discountDesc;


    @ColumnInfo(name = "timeStamp")
    public long timestamp;


    public ItemTable(String modelID,String divisionID, String segmentID, String modelCode, String modelDescription,
                     String imgPath, String discountCount,String discountId,String discountDesc) {
        this.modelID = modelID;
        this.divisionID = divisionID;
        this.segmentID = segmentID;
        this.modelCode = modelCode;
        this.modelDescription = modelDescription;
        this.imgPath = imgPath;
        this.discountCount = discountCount;
        this.discountId = discountId;
        this.discountDesc = discountDesc;

    }


    @Ignore
    public ItemTable(){

    }

    @Ignore
    public ItemTable(int id, String modelID,String segmentID, String modelCode, String modelDescription, String imgPath, String price, String discount, long timestamp) {
        this.id = id;
        this.modelID = modelID;
        this.segmentID = segmentID;
        this.modelCode = modelCode;
        this.modelDescription = modelDescription;
        this.imgPath = imgPath;
        this.price = price;
        this.discountId = discount;
        this.timestamp = timestamp;

    }
}
