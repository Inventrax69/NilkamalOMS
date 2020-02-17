package com.example.inventrax.falconOMS.room;


import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.example.inventrax.falconOMS.pojos.TaxesResult;

import java.util.List;

@Dao
public interface CartHeaderDAO {

    @Query("SELECT * FROM CartHeader")
    List<CartHeader> getAll();

    @Query("SELECT * FROM CartHeader WHERE customerID IN (:cus)")
    List<CartHeader> example(int[] cus);

    @Query("SELECT * FROM CartHeader WHERE customerID IN (:customerID)")
    List<CartHeader> getAllCustomer(String customerID);

    @Query("SELECT * FROM CartHeader WHERE isApproved = 0 ORDER BY timeStamp DESC")
    List<CartHeader> getAllWithOutApprovals();

    @Query("SELECT * FROM CartHeader WHERE customerID IN (:customerID) AND isApproved = 0 ORDER BY timeStamp DESC")
    List<CartHeader> getAllCustomerWithOutApprovals(String customerID);

/*    @Query("SELECT * FROM CartHeader WHERE (isCreditLimit=1 OR isInActive=1) AND isApproved > 0 ORDER BY timeStamp DESC")
    List<CartHeader> getAllWithApprovals();   */

    @Query("SELECT * FROM CartHeader WHERE isApproved > 0 ORDER BY timeStamp DESC")
    List<CartHeader> getAllWithApprovals();

/*    @Query("SELECT * FROM CartHeader WHERE customerID IN (:customerID) AND (isCreditLimit=1 OR isInActive=1) AND isApproved > 0 ORDER BY timeStamp DESC")
    List<CartHeader> getAllCustomerWithApprovals(String customerID);*/

    @Query("SELECT * FROM CartHeader WHERE customerID IN (:customerID) AND isApproved > 0 ORDER BY timeStamp DESC")
    List<CartHeader> getAllCustomerWithApprovals(String customerID);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<CartHeader> cartHeaders);

/*    @Insert(onConflict = REPLACE)
    void insert(CartHeader cartHeader);    */

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(CartHeader cartHeader);

    @Query("DELETE FROM CartHeader")
    void deleteAll();

    @Query("DELETE FROM CartHeader WHERE isUpdated=0")
    void deleteAllIsUpdated();

    @Query("SELECT * FROM CartHeader WHERE cartHeaderID=:cartHeaderID")
    CartHeader getCartHeader(int cartHeaderID);

    @Query("DELETE FROM CartHeader WHERE cartHeaderID =:cartHeaderID")
    void deleteCartHeader(String cartHeaderID);

    @Query("SELECT * FROM CartHeader WHERE isFulfillmentCompleted = 1 AND isApproved=0 ORDER BY isInActive DESC,isCreditLimit DESC")
    List<CartHeader> getFullfilmentCompletedHeaders();

    @Query("SELECT * FROM CartHeader WHERE isApproved=0")
    List<CartHeader> getCartHeadersForSTP();

    @Query("SELECT cartHeaderID FROM CartHeader WHERE isFulfillmentCompleted = 1 AND isApproved=0 AND isInActive=0 AND isCreditLimit=0")
    int[] getFullfilmentCompletedHeadersList();

    @Query("UPDATE CartHeader SET isFulfillmentCompleted=1 WHERE cartHeaderID =:cartHeaderID AND customerID=:customerID")
    void updateisFulfillmentCompleted(int cartHeaderID,String customerID);

    @Query("SELECT cartHeaderID FROM CartHeader")
    List<Integer> getHeaders();

    @Query("SELECT cartHeaderID FROM CartHeader WHERE customerID IN (:customerID) AND isApproved=0 LIMIT 1")
    Integer getHeadersWithCustmor(String customerID);

    @Query("SELECT * FROM CartHeader WHERE isApproved=0 ")
    List<CartHeader> getHeadersWithCustmor();

    @Query("SELECT cartHeaderID FROM CartHeader WHERE isCreditLimit = 1")
    List<Integer> getHeadersForCreditLimitApproval();

    @Query("SELECT * FROM CartHeader WHERE isApproved=0 AND customerID=:customerID AND cartHeaderID=:cartHeaderID LIMIT 1")
    CartHeader getCartHeader(int customerID,int cartHeaderID);

    @Query("SELECT cartHeaderID FROM CartHeader WHERE isInActive = 1")
    List<Integer> getInActiveApprovalHeader();

    @Query("SELECT cartHeaderID FROM CartHeader WHERE isInActive = 0 AND isCreditLimit = 0 AND isFulfillmentCompleted = 1 AND isApproved = 0")
    List<Integer> getHeadersForOrderConfirmation();

    @Query("DELETE FROM CartHeader WHERE cartHeaderId NOT IN (SELECT cartHeaderId FROM CartDetails)")
    void deleteHeadersNotThereInCartDetails();

    @Query("SELECT customerName FROM CartHeader WHERE cartHeaderID =:cartHeaderId LIMIT 1")
    String getCustomerNameOfCartHeader(int cartHeaderId);

    @Query("SELECT COUNT(*) FROM CartHeader WHERE customerID=:customerID AND isApproved = 0 LIMIT 1")
    int getCountCustumerByHeader(int customerID);

    @Query("SELECT * FROM CartHeader WHERE customerID =:customerID AND isApproved = 0 LIMIT 1")
    CartHeader getCartHeaderByCustomerID(int customerID);

    @Query("SELECT * FROM CartHeader WHERE customerID =:customerID AND cartHeaderID=:cartHeaderID AND isApproved = 0 LIMIT 1")
    CartHeader getByCustomerIDCartHeaderID(int customerID,int cartHeaderID);

    @Query("UPDATE CartHeader SET isPriority=:priority WHERE customerID =:customerID AND cartHeaderID=:cartHeaderID AND isApproved = 0")
    void updateIsPriority(int customerID,int cartHeaderID,int priority);

    @Query("UPDATE CartHeader SET shipToPartyId=:shipToPartyId WHERE customerID =:customerID AND cartHeaderID=:cartHeaderID AND isApproved = 0")
    void updateShipToPatry(int customerID,int cartHeaderID,String shipToPartyId);

    @Query("UPDATE CartHeader SET shipToPartyId=:shipToPartyId,isPriority=:priority WHERE customerID =:customerID AND isApproved = 0")
    void updateShipToPatryAndIsPriority(int customerID,int shipToPartyId,int priority);

    @Query("UPDATE CartHeader SET isUpdated=:isUpdated WHERE customerID =:customerID AND isApproved = 0")
    void updateIsUpdated(int customerID,int isUpdated);

    @Query("SELECT printf('%.2f',CAST(SUM(totalPrice) AS FLOAT)) as totalPrice,printf('%.2f',CAST(SUM(totalPriceWithTax) AS FLOAT)) as totalPriceWithTax,printf('%.2f',CAST(SUM(totalPriceWithTax-totalPrice) AS FLOAT)) as taxes FROM CartHeader WHERE isApproved=0")
    List<TaxesResult> getTaxesList();

    /* @Query("SELECT ROUND(SUM(totalPrice),2) as totalPrice,ROUND(SUM(totalPriceWithTax),2) as totalPriceWithTax,ROUND(SUM(totalPriceWithTax-totalPrice),2) as taxes FROM CartHeader WHERE customerID=:customerID")
    List<TaxesResult> getTaxesListByCustomer(String customerID); */

    @Query("SELECT printf('%.2f',CAST(SUM(totalPrice) AS FLOAT)) as totalPrice,printf('%.2f',CAST(SUM(totalPriceWithTax) AS FLOAT)) as totalPriceWithTax,printf('%.2f',CAST(SUM(totalPriceWithTax-totalPrice) AS FLOAT)) as taxes FROM CartHeader WHERE customerID=:customerID AND isApproved=0")
    List<TaxesResult> getTaxesListByCustomer(String customerID);

    @Query("DELETE FROM CartHeader WHERE cartHeaderID IN (:cartHeaderID)")
    void deleteCartHeaderbyList(int[] cartHeaderID);

}
