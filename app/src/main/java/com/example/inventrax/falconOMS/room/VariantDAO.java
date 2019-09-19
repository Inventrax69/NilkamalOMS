package com.example.inventrax.falconOMS.room;


import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

@Dao
public interface VariantDAO {

    @Query("SELECT * FROM VariantTable")
    List<VariantTable> getVariants();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<VariantTable> variantTables);

    @Insert(onConflict = REPLACE)
    void insert(VariantTable variantTable);

    @Query("DELETE FROM VariantTable")
    void deleteAll();

    @Query("SELECT * FROM VariantTable WHERE modelID =:modelId")
    List<VariantTable> getVariants(String modelId);

}
