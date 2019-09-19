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

    @ColumnInfo(name = "modelID")
    public String modelID;

    @ColumnInfo(name = "MaterialID")
    public String materialID;

    @ColumnInfo(name = "MDescription")
    public String mDescription;

    @ColumnInfo(name = "MDescriptionLong")
    public String mDescriptionLong;

    @ColumnInfo(name = "Mcode")
    public String mCode;

    @ColumnInfo(name = "ModelColor")
    public String modelColor;


    public VariantTable(String modelID, String materialID, String mDescription, String mDescriptionLong, String mCode, String modelColor) {
        this.modelID = modelID;
        this.materialID = materialID;
        this.mDescription = mDescription;
        this.mDescriptionLong = mDescriptionLong;
        this.mCode = mCode;
        this.modelColor = modelColor;
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
