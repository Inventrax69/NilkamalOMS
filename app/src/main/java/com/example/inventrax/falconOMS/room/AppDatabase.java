package com.example.inventrax.falconOMS.room;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

@Database(entities = {CustomerTable.class, ItemTable.class}, version = 2)
public abstract  class AppDatabase extends RoomDatabase {
    public abstract CustomerDAO customerDAO();
    public abstract ItemDAO itemDAO();
}
