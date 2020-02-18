package com.example.inventrax.falconOMS.room;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.RoomWarnings;
import android.arch.persistence.room.Update;

import java.util.List;

@Dao
public interface CustomerDAO {

    @Query("SELECT * FROM CustomerTable")
    List<CustomerTable> getAll();

    @Query("SELECT COUNT(*) FROM CustomerTable")
    int getCustomerCount();

    @Query("SELECT * FROM CustomerTable WHERE customerId=:custId")
    CustomerTable getCustomer(String custId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<CustomerTable> customerTables);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(CustomerTable customerTable);

    @Delete
    void delete(CustomerTable customerTable);

    @Query("DELETE FROM CustomerTable")
    void deleteAll();

    @Update
    void update(CustomerTable customerTable);

    @Query("SELECT * FROM CustomerTable WHERE customerId=:custId")
    CustomerTable getCustomerNames(String custId);

    @Query("SELECT * FROM CustomerTable WHERE timeStamp = (SELECT MAX(timeStamp) FROM CustomerTable)")
    CustomerTable getLastRecord();

    @Query("SELECT * FROM CustomerTable LIMIT (:pagecount*50)+50  OFFSET (:pagecount*50)")
    List<CustomerTable> getAllCust(int pagecount);

    @Query("SELECT * FROM CustomerTable WHERE division=:division")
    CustomerTable getCustomerIdFromDivision(String division);

    @Query("SELECT customerCode FROM CustomerTable")
    List<String> getAllCustomerCodes();

     @Query("SELECT ((customerCode) || '-' || (customerName)) FROM CustomerTable WHERE customerTypeID = 3")
    List<String> getAllCustomerCodesName();

    @Query("SELECT customerId FROM CustomerTable WHERE customerTypeID = 3")
    List<String> getAllCustomerIds();

    @Query("SELECT ((customerCode) || '-' || (customerName)) FROM CustomerTable WHERE customerId=:customerId AND customerTypeID = 3")
    String getAllCustomerCodeById(int customerId);

    @Query("SELECT ((customerCode) || '-' || (customerName)) FROM CustomerTable WHERE customerId = :customerID")
    String getCustomerCode(String customerID);

    @Query("SELECT customerName FROM CustomerTable WHERE customerTypeID = 2 AND customerId = :customerId")
    String getAllCustomerName(String customerId);

    @SuppressWarnings(RoomWarnings.CURSOR_MISMATCH)
    @Query("SELECT DISTINCT customerId,id,((customerCode) || '-' || (customerName)) AS customerName,timeStamp FROM CustomerTable WHERE customerId IN (SELECT DISTINCT customerId FROM UserDivisionCustTable)")
    List<CustomerTable> getCustomerNames();

    @Query("SELECT DISTINCT ((customerCode) || '-' || (customerName)) AS customerName FROM CustomerTable WHERE customerId IN (SELECT DISTINCT customerId FROM UserDivisionCustTable)")
    List<String> getCustomerCodes();

    @Query("SELECT DISTINCT customerId AS customerName FROM CustomerTable WHERE customerId IN (SELECT DISTINCT customerId FROM UserDivisionCustTable)")
    List<String> getCustomerId();

    @Query("SELECT DISTINCT customerId FROM UserDivisionCustTable")
    List<String> getUserDivisionCustTableId();

    @Query("SELECT DISTINCT ((customerCode) || '-' || (customerName)) AS customerName FROM CustomerTable WHERE customerId=:customerId LIMIT 1")
    String getCustomerCodesByString(String customerId);

    @Query("SELECT DISTINCT customerId AS customerName FROM CustomerTable WHERE customerId=:customerId LIMIT 1")
    String getCustomerIdByString(String customerId);

    @Query("SELECT ((customerCode) || '-' || (customerName)) FROM CustomerTable WHERE customerId=:customerId AND customerTypeID = 2")
    String getAllCustomerCode(int customerId);

    @Query("SELECT ((customerCode) || '-' || (customerName)) FROM CustomerTable WHERE customerTypeID = 2 AND divisionId=:materialDivsionId")
    List<String> getCustomerNamesBasedOnMDivision(String materialDivsionId);

    @Query("SELECT customerId FROM CustomerTable WHERE customerTypeID = 2 AND divisionId=:materialDivsionId")
    List<String> getCustomerIdsBasedOnMDivision(String materialDivsionId);

}


