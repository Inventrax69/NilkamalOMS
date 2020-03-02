package com.example.inventrax.falconOMS.room;


import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

import retrofit2.http.DELETE;

@Dao
public interface VariantDAO {

    @Query("SELECT * FROM VariantTable")
    List<VariantTable> getVariants();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<VariantTable> variantTables);



    @Query("DELETE FROM VariantTable")
    void deleteAll();

    @Query("SELECT * FROM VariantTable WHERE modelID =:modelId")
    List<VariantTable> getVariants(String modelId);

    @Query("SELECT * FROM VariantTable WHERE materialID = :materialID")
    VariantTable getMaterial(int materialID);

    @Query("SELECT modelID FROM VariantTable WHERE materialID = :materialID")
    int getModelId(int materialID);

    @Query("SELECT divisionID FROM VariantTable WHERE materialID = :materialID")
    int getMaterialDivisionID(int materialID);

    @Query("SELECT discountDesc FROM VariantTable WHERE modelID=:modelID AND discountId > 0")
    List<String> getDiscountDesc(int modelID);

    @Query("SELECT mCode FROM VariantTable WHERE modelID=:modelID AND discountId > 0")
    List<String> getMCode(int modelID);


    // Queries for Master Data Update, Add,  Delete
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(VariantTable variantTable);

    @Delete
    void delete(VariantTable variantTable);

    @Query("DELETE FROM variantTable WHERE materialID=:materialID")
    void deleteByMaterialID(String materialID);


    @Query("SELECT COUNT(*) FROM variantTable WHERE materialID=:materialID")
    int getCountByMaterialID(String materialID);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void update(VariantTable variantTable);

    @Query("UPDATE varianttable SET modelID=:modelID,divisionID=:divisionID,materialID=:materialID," +
            "mDescription=:mDescription,mDescriptionLong=:mDescriptionLong,mCode=:mCode," +
            "modelColor=:modelColor,price=:price,discountCount=:discountCount,discountId=:discountId," +
            "discountDesc=:discountDesc,materialImgPath=:materialImgPath,specsUrl=:specsUrl," +
            "catalogUrl=:catalogUrl,brochureUrl=:brochureUrl,isOpenPrice=:isOpenPrice,stackSize=:stackSize," +
            "packSize=:packSize WHERE materialID=:materialID")
    void updateByMaterialID(String modelID,String divisionID,String materialID,String mDescription,String mDescriptionLong,
                            String mCode,String modelColor,String price,String discountCount,String discountId,
                            String discountDesc,String materialImgPath, String specsUrl,
                            String catalogUrl,String brochureUrl,String isOpenPrice,String stackSize,String packSize);


    @Query("UPDATE varianttable SET discountCount=0,discountId=0,discountDesc=''")
    void updateDiscount();


}
