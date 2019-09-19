package com.example.inventrax.falconOMS.room;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;

import com.example.inventrax.falconOMS.util.Converters;

@Database(entities = {CustomerTable.class, ItemTable.class, VariantTable.class}, version = 2)
@TypeConverters({Converters.class})
public abstract  class AppDatabase extends RoomDatabase {
    public abstract CustomerDAO customerDAO();
    public abstract ItemDAO itemDAO();
    public abstract VariantDAO variantDAO();
}
