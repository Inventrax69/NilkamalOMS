package com.example.inventrax.falconOMS.room;


import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.example.inventrax.falconOMS.room.roomModel.CustomerData;

import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

@Dao
public interface UserDivisionCustDAO {

    @Query("SELECT * FROM UserDivisionCustTable")
    List<UserDivisionCustTable> getAll();

    /*@Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<UserDivisionCustDAO> userDivisionCustTables);*/

    @Insert(onConflict = REPLACE)
    void insert(UserDivisionCustTable userDivisionCustTable);

    @Query("DELETE FROM UserDivisionCustTable")
    void deleteAll();

    @Query("SELECT * FROM UserDivisionCustTable WHERE divisionId =:divisionId")
    UserDivisionCustTable getPartner(String divisionId);

    @Query("SELECT * FROM UserDivisionCustTable WHERE divisionId=:divisionId")
    UserDivisionCustTable getDivision(String divisionId);

    @Query("SELECT * FROM UserDivisionCustTable")
    int  getUserDivisionCustCount();

    @Query("SELECT DISTINCT customerId,customerName,customerCode FROM CustomerTable WHERE  customerId IN (SELECT customerId FROM UserDivisionCustTable)")
    List<CustomerData> getCustomerListByUser();

    /*@Query("DELETE FROM CartDetails WHERE materialID =:materialID AND quantity=:quantity AND id=:id")
    void deleteItem(String materialID, String quantity, int id);*/




}
