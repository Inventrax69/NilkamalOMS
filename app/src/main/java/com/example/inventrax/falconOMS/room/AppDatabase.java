package com.example.inventrax.falconOMS.room;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;

import com.example.inventrax.falconOMS.util.Converters;

@Database(entities = {CustomerTable.class, ItemTable.class, VariantTable.class, CartDetails.class, UserDivisionCustTable.class, CartHeader.class}, version = 1)
@TypeConverters({Converters.class})
public abstract  class AppDatabase extends RoomDatabase {
    public abstract CustomerDAO customerDAO();
    public abstract ItemDAO itemDAO();
    public abstract VariantDAO variantDAO();
    public abstract CartDetailsDAO cartDetailsDAO();
    public abstract CartHeaderDAO cartHeaderDAO();
    public abstract UserDivisionCustDAO userDivisionCustDAO();

}
