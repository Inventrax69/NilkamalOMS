package com.example.inventrax.falconOMS.room;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

@Entity
public class ItemTable {

    @PrimaryKey(autoGenerate = true)
    public int iid;

    @ColumnInfo(name = "itemid")
    public String itemid;

    @ColumnInfo(name = "itemname")
    public String itemname;

    @ColumnInfo(name = "itemdesc")
    public String itemdesc;

     @ColumnInfo(name = "imageurl")
    public String imageurl;

    @ColumnInfo(name = "price")
    public String price;

    public ItemTable(String itemid, String itemname, String itemdesc, String imageurl, String price) {
        this.itemid = itemid;
        this.itemname = itemname;
        this.itemdesc = itemdesc;
        this.imageurl = imageurl;
        this.price = price;
    }

    @Ignore
    public ItemTable(){

    }
}
