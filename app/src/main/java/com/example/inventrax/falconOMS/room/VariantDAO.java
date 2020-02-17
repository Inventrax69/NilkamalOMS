package com.example.inventrax.falconOMS.room;


import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface VariantDAO {

    @Query("SELECT * FROM VariantTable")
    List<VariantTable> getVariants();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<VariantTable> variantTables);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(VariantTable variantTable);

    @Query("DELETE FROM VariantTable")
    void deleteAll();

    @Query("SELECT * FROM VariantTable WHERE modelID =:modelId")
    List<VariantTable> getVariants(String modelId);

    @Query("SELECT * FROM VariantTable WHERE materialID = :materialID")
    VariantTable getMaterial(int materialID);

    @Query("SELECT modelID FROM VariantTable WHERE materialID = :materialID")
    int getModelId(int materialID);

    @Query("SELECT discountDesc FROM VariantTable WHERE modelID=:modelID AND discountId > 0")
    List<String> getDiscountDesc(int modelID);

    @Query("SELECT mCode FROM VariantTable WHERE modelID=:modelID AND discountId > 0")
    List<String> getMCode(int modelID);

}
