package com.example.inventrax.falconOMS.room;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

@Dao
public interface CustomerDAO {

    @Query("SELECT * FROM CustomerTable")
    List<CustomerTable> getAll();

    @Query("SELECT * FROM CustomerTable WHERE custId=:custId")
    CustomerTable getCustomer(String custId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<CustomerTable> customerTables);

    @Insert(onConflict = REPLACE)
    void insert(CustomerTable customerTable);

    @Delete
    void delete(CustomerTable customerTable);

    @Query("DELETE FROM CustomerTable")
    void delete();

    @Update
    void update(CustomerTable customerTable);

}
