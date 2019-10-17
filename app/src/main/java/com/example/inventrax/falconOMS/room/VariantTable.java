package com.example.inventrax.falconOMS.room;


import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

import static android.arch.persistence.room.ForeignKey.CASCADE;

@Entity(foreignKeys = @ForeignKey(entity = ItemTable.class,
        parentColumns = "modelID",
        childColumns = "modelID",
        onDelete = CASCADE))
public class VariantTable {

    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "modelID", index = true)
    public String modelID;

    @ColumnInfo(name = "divisionID")
    public String divisionID;

    @ColumnInfo(name = "materialID")
    public String materialID;

    @ColumnInfo(name = "mDescription")
    public String mDescription;

    @ColumnInfo(name = "mDescriptionLong")
    public String mDescriptionLong;

    @ColumnInfo(name = "mCode")
    public String mCode;

    @ColumnInfo(name = "modelColor")
    public String modelColor;

    @ColumnInfo(name = "price")
    public String price;


    @ColumnInfo(name = "discountCount")
    public String discountCount;

    @ColumnInfo(name = "discountId")
    public String discountId;

    @ColumnInfo(name = "discountDesc")
    public String discountDesc;

    @ColumnInfo(name = "materialImgPath")
    public String materialImgPath;

    @ColumnInfo(name = "specsUrl")
    public String specsUrl;

    @ColumnInfo(name = "catalogUrl")
    public String catalogUrl;

    @ColumnInfo(name = "brochureUrl")
    public String brochureUrl;

    @ColumnInfo(name = "isOpenPrice")
    public Boolean isOpenPrice;



    public VariantTable(String modelID, String divisionID, String materialID, String mDescription,
                        String mDescriptionLong, String mCode, String modelColor, String materialImgPath,
                        String discountCount, String discountId, String discountDesc,
                        String specsUrl, String catalogUrl, String brochureUrl, Boolean isOpenPrice) {
        this.modelID = modelID;
        this.divisionID = divisionID;
        this.materialID = materialID;
        this.mDescription = mDescription;
        this.mDescriptionLong = mDescriptionLong;
        this.mCode = mCode;
        this.modelColor = modelColor;
        this.materialImgPath = materialImgPath;
        this.discountCount = discountCount;
        this.discountId = discountId;
        this.discountDesc = discountDesc;
        this.specsUrl = specsUrl;
        this.catalogUrl = catalogUrl;
        this.brochureUrl = brochureUrl;
        this.isOpenPrice = isOpenPrice;
    }

    @Ignore
    public VariantTable(){

    }

    @Ignore
    public VariantTable(int id, String modelID, String materialID, String mDescription, String mDescriptionLong, String mCode, String modelColor) {
        this.id = id;
        this.modelID = modelID;
        this.materialID = materialID;
        this.mDescription = mDescription;
        this.mDescriptionLong = mDescriptionLong;
        this.mCode = mCode;
        this.modelColor = modelColor;
    }
}
