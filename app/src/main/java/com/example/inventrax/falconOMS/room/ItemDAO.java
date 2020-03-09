package com.example.inventrax.falconOMS.room;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

@Dao
public interface ItemDAO {

    @Query("SELECT * FROM ItemTable")
    List<ItemTable> getAll();

    @Query("SELECT * FROM ItemTable WHERE divisionID IN (SELECT divisionID FROM CustomerTable WHERE customerId=:customerId)")
    List<ItemTable> getAllByCustomer(String customerId);

    /*@Query("SELECT * FROM ItemTable LIMIT (:pagecount*50)+50  OFFSET (:pagecount*50)")
    List<ItemTable> getAllItems(int pagecount);*/

    @Query("SELECT * FROM ItemTable WHERE modelID=:mID")
    ItemTable getItem(String mID);

    @Insert (onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<ItemTable> itemTables);

    @Delete
    void delete(ItemTable itemTable);

    @Query("DELETE FROM ItemTable")
    void deleteAll();

    @Update
    void update(ItemTable itemTable);

    @Query("SELECT * FROM ItemTable WHERE timeStamp = (SELECT MAX(timeStamp) FROM ItemTable)")
    ItemTable getLastRecord();

    @Query("SELECT * FROM ItemTable  ORDER BY  modelCode  DESC")
    List<ItemTable> getDescendingList();

    @Query("SELECT * FROM ItemTable WHERE divisionID IN (SELECT divisionID FROM CustomerTable WHERE customerId=:customerId) LIMIT  15  OFFSET (:pagecount*15)")
    List<ItemTable> getAllItems(int pagecount, String customerId);

    @Query("SELECT * FROM ItemTable WHERE divisionID IN (SELECT divisionID FROM CustomerTable WHERE customerId=:customerId) LIMIT (:pagecount*15)")
    List<ItemTable> setAllItems(int pagecount, String customerId);

    @Query("SELECT * FROM ItemTable WHERE divisionID IN (SELECT divisionID FROM CustomerTable WHERE customerId=:customerId) ORDER BY modelCode DESC LIMIT (:pagecount*15)")
    List<ItemTable> setAllItemsDESC(int pagecount, String customerId);

    @Query("SELECT * FROM ItemTable WHERE divisionID IN (SELECT divisionID FROM CustomerTable WHERE customerId=:customerId) ORDER BY modelCode ASC LIMIT (:pagecount*15)")
    List<ItemTable> setAllItemsASC(int pagecount, String customerId);

    @Query("SELECT COUNT(*) FROM ItemTable WHERE divisionID IN (SELECT divisionID FROM CustomerTable WHERE customerId=:customerId)")
    int getAllItemsCount(String customerId);

    @Query("SELECT * FROM ItemTable WHERE divisionID IN (SELECT divisionID FROM CustomerTable WHERE customerId=:customerId) ORDER BY modelCode DESC LIMIT  15  OFFSET (:pagecount*15)")
    List<ItemTable> getAllItemsDESC(int pagecount, String customerId);

    @Query("SELECT * FROM ItemTable WHERE divisionID IN (SELECT divisionID FROM CustomerTable WHERE customerId=:customerId) ORDER BY modelCode ASC LIMIT  15  OFFSET (:pagecount*15)")
    List<ItemTable> getAllItemsASC(int pagecount, String customerId);

    @Query("SELECT * FROM ItemTable LIMIT  15  OFFSET (:pagecount*15)")
    List<ItemTable> getAllItemsAll(int pagecount);

    @Query("SELECT * FROM ItemTable LIMIT (:pagecount*15)")
    List<ItemTable> setAllItemsAll(int pagecount);

    @Query("SELECT * FROM ItemTable ORDER BY modelCode DESC LIMIT (:pagecount*15)")
    List<ItemTable> setAllItemsAllDESC(int pagecount);

    @Query("SELECT * FROM ItemTable ORDER BY modelCode ASC LIMIT (:pagecount*15)")
    List<ItemTable> setAllItemsAllASC(int pagecount);

    @Query("SELECT COUNT(*) FROM ItemTable")
    int getAllItemsAllCount();

    @Query("SELECT * FROM ItemTable ORDER BY modelCode DESC LIMIT  15  OFFSET (:pagecount*15)")
    List<ItemTable> getAllItemsDESCAll(int pagecount);

    @Query("SELECT * FROM ItemTable  ORDER BY modelCode ASC LIMIT  15  OFFSET (:pagecount*15)")
    List<ItemTable> getAllItemsASCAll(int pagecount);

    @Query("SELECT count(*) FROM ItemTable")
    int getCount();

    @Query("SELECT (printf('%s', ItemTable.modelCode) ||  '-' || printf('%s', ItemTable.modelDescription)|| '-'" +
            " || printf('%s', VariantTable.mDescription)  || '-' || printf('%s', VariantTable.mDescriptionLong) || '-' " +
            "|| printf('%s', VariantTable.modelColor)) AS qSearch FROM ItemTable " +
            "INNER JOIN VariantTable On ItemTable.modelID=VariantTable.modelID")
    List<String> getFilterList();

    @Query("SELECT * FROM ItemTable WHERE modelID IN (SELECT modelID FROM VariantTable WHERE (mDescription LIKE '%'||:searchText||'%') " +
            "OR (mCode LIKE '%'||:searchText||'%') OR (modelColor LIKE '%'||:searchText||'%')) " +
            "OR (modelCode like '%'||:searchText||'%') OR (modelDescription like '%'||:searchText||'%')")
    List<ItemTable> getFilterAll(String searchText);

    @Query("SELECT * FROM ItemTable WHERE (divisionID IN (SELECT divisionID FROM CustomerTable WHERE customerId=:customerId)) AND modelID IN (SELECT modelID FROM VariantTable WHERE (mDescription LIKE '%'||:searchText||'%') " +
            "OR (mCode LIKE '%'||:searchText||'%') OR (modelColor LIKE '%'||:searchText||'%')) " +
            "OR (modelCode like '%'||:searchText||'%') OR (modelDescription like '%'||:searchText||'%')")
    List<ItemTable> getFilterAllByCustomer(String searchText,String customerId);


    // Master data updates
    @Insert (onConflict = OnConflictStrategy.REPLACE)
    void insert(ItemTable itemTable);

    @Query("SELECT COUNT(*) FROM ItemTable WHERE modelID=:modelID")
    int getCountByModelID(String modelID);

  //  modelID,divisionID,segmentID,modelCode,modelDescription,imgPath,price,discountCount,discountId,discountDesc,timeStamp

    @Query("UPDATE itemtable SET modelID=:modelID,divisionID=:divisionID,segmentID=:segmentID," +
            "modelCode=:modelCode,modelDescription=:modelDescription,imgPath=:imgPath," +
            "price=:price,discountCount=:discountCount,discountId=:discountId,discountDesc=:discountDesc," +
            "timeStamp=:timeStamp WHERE modelID=:modelID")
    void updateByModelID(String modelID,String divisionID,String segmentID,String modelCode,String modelDescription,
                         String imgPath,String price,String discountCount,String discountId,String discountDesc,String timeStamp);

    @Query("DELETE FROM ItemTable WHERE modelID=:modelID")
    void deleteByModelID(String modelID);


    @Query("UPDATE itemtable SET discountId=0,discountDesc='',discountDesc=0")
    void updateDiscount();



}
