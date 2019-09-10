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
public interface ItemDAO {

    @Query("SELECT * FROM ItemTable")
    List<ItemTable> getAll();

    @Query("SELECT * FROM ItemTable LIMIT (:pagecount*50)+50  OFFSET (:pagecount*50)")
    List<ItemTable> getAllItems(int pagecount);

    @Query("SELECT * FROM ItemTable WHERE itemid=:itemid")
    ItemTable getItem(String itemid);

    @Insert (onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<ItemTable> itemTables);

    @Insert(onConflict = REPLACE)
    void insert(ItemTable itemTable);

    @Delete
    void delete(ItemTable itemTable);

    @Query("DELETE FROM ItemTable")
    void deleteAll();

    @Update
    void update(ItemTable itemTable);

    @Query("SELECT * FROM ItemTable WHERE timeStamp = (SELECT MAX(timeStamp) FROM ItemTable)")
    ItemTable getLastRecord ();

}
