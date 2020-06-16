package com.example.inventrax.falconOMS.room;


import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface CartDetailsDAO {

    @Ignore
    @Query("SELECT CartDetails.materialID,CartDetails.quantity," +
            "CartDetails.mCode,CartDetails.id,CartDetails.price,CartDetails.mDescription,CartDetails.deliveryDate,CartDetails.timeStamp,CartDetails.imgPath," +
            "CartDetails.cartDetailsId,CartDetails.cartHeaderId,CartDetails.BOMHeaderID,CartDetails.isPriority,CartDetails.discountedPrice,CartDetails.isInActive,CartDetails.customerId,CartDetails.isUpdated," +
            "CartDetails.totalPrice,CartDetails.offerValue,CartDetails.offerItemCartDetailsID," +
            "CartDetails.discountID,CartDetails.discountText,CartDetails.gst,CartDetails.tax,CartDetails.subtotal,CartDetails.HSNCode  FROM CartDetails JOIN CartHeader \n" +
            "ON CartDetails.cartHeaderId= CartHeader.cartHeaderID WHERE (CartDetails.offerItemCartDetailsID IS NULL OR CartDetails.offerItemCartDetailsID='-1') GROUP BY CartDetails.materialID")
    List<CartDetails> getAllCartItems();

    /*
    @Ignore
    @Query("SELECT CartDetails.materialID, quantity," +
            "CartDetails.mCode,CartDetails.id,CartDetails.price,CartDetails.mDescription,CartDetails.deliveryDate,CartDetails.timeStamp,CartDetails.imgPath," +
            "CartDetails.cartDetailsId,CartDetails.cartHeaderId,CartDetails.isInActive,CartDetails.customerId,CartDetails.isUpdated  FROM CartDetails JOIN CartHeader \n" +
            "ON CartDetails.cartHeaderId= CartHeader.cartHeaderID GROUP BY CartDetails.materialID ,CartDetails.isUpdated")
    List<CartDetails> getAllCartItems();
    */

    /*
    @Ignore
    @Query("SELECT CartDetails.materialID,sum(CartDetails.quantity) as quantity," +
            "CartDetails.mCode,CartDetails.id,CartDetails.price,CartDetails.mDescription,CartDetails.deliveryDate,CartDetails.timeStamp,CartDetails.imgPath," +
            "CartDetails.cartDetailsId,CartDetails.cartHeaderId,CartDetails.isInActive,CartDetails.customerId,CartDetails.isUpdated  FROM CartDetails JOIN CartHeader \n" +
            "ON CartDetails.cartHeaderId= CartHeader.cartHeaderID WHERE CartHeader.isApproved = 0 GROUP BY CartDetails.materialID ,CartDetails.isUpdated")
    List<CartDetails> getCartItemsWithOutApprovals();
    */

    @Ignore
    @Query("SELECT CartDetails.materialID,CartDetails.quantity," +
            "CartDetails.mCode,CartDetails.id,CartDetails.price,CartDetails.mDescription,CartDetails.deliveryDate,CartDetails.timeStamp,CartDetails.imgPath," +
            "CartDetails.cartDetailsId,CartDetails.cartHeaderId,CartDetails.isPriority,CartDetails.isInActive,CartDetails.customerId,CartDetails.isUpdated," +
            "CartDetails.totalPrice,CartDetails.offerValue,CartDetails.discountedPrice,CartDetails.BOMHeaderID,CartDetails.offerItemCartDetailsID ," +
            "CartDetails.discountID,CartDetails.discountText,CartDetails.gst,CartDetails.tax,CartDetails.subtotal,CartDetails.HSNCode  FROM CartDetails JOIN CartHeader \n" +
            "ON CartDetails.cartHeaderId = CartHeader.cartHeaderID WHERE CartHeader.isApproved = 0 AND (CartDetails.offerItemCartDetailsID IS NULL OR CartDetails.offerItemCartDetailsID='-1') GROUP BY CartDetails.materialID")
    List<CartDetails> getCartItemsWithOutApprovals();

    /*
    @Query("SELECT * FROM CartDetails")
    List<CartDetails> getAllCartItems();
    */

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<CartDetails> cartDetails);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(CartDetails cartDetails);

    @Query("DELETE FROM CartDetails")
    void deleteAll();

    @Query("DELETE FROM CartDetails WHERE isUpdated=0")
    void deleteAllIsUpdated();

    @Query("SELECT * FROM CartDetails WHERE materialID =:materialID")
    List<CartDetails> getCartItem(String materialID);

    @Query("DELETE FROM CartDetails WHERE materialID =:materialID")
    void deleteCartItem(String materialID);

    @Query("DELETE FROM CartDetails WHERE materialID =:materialID")
    void deleteItem(String materialID);

    @Query("DELETE FROM CartDetails WHERE materialID =:materialID")
    int getCountOfCartDetails(String materialID);

    @Ignore
    @Query("SELECT CartDetails.materialID,CartDetails.quantity," +
            "CartDetails.mCode,CartDetails.id,CartDetails.price,CartDetails.mDescription,CartDetails.timeStamp,CartDetails.imgPath," +
            "CartDetails.cartDetailsId,CartDetails.cartHeaderId,CartDetails.isPriority,CartDetails.isInActive,CartDetails.deliveryDate," +
            "CartDetails.customerId,CartDetails.isUpdated," +
            "CartDetails.totalPrice,CartDetails.offerValue,CartDetails.discountedPrice,CartDetails.offerItemCartDetailsID," +
            "CartDetails.discountID,CartDetails.discountText,CartDetails.gst,CartDetails.bomHeaderId,CartDetails.tax,CartDetails.subtotal,CartDetails.HSNCode FROM CartDetails JOIN CartHeader ON CartDetails.cartHeaderId= CartHeader.cartHeaderID " +
            "WHERE CartDetails.cartHeaderId =:cartHeaderId AND CartDetails.customerId=:customerId AND (CartDetails.offerItemCartDetailsID IS NULL OR CartDetails.offerItemCartDetailsID='-1') GROUP BY CartDetails.materialID")
    List<CartDetails> getCartItemsOfCustomer(int cartHeaderId,int customerId);

    /*
    @Ignore
    @Query("SELECT CartDetails.materialID,quantity," +
            "CartDetails.mCode,CartDetails.id,CartDetails.price,CartDetails.mDescription,CartDetails.timeStamp,CartDetails.imgPath," +
            "CartDetails.cartDetailsId,CartDetails.cartHeaderId,CartDetails.isInActive,CartDetails.deliveryDate," +
            "CartDetails.customerId,CartDetails.isUpdated FROM CartDetails JOIN CartHeader ON CartDetails.cartHeaderId= CartHeader.cartHeaderID " +
            "WHERE CartDetails.cartHeaderId =:cartHeaderId GROUP BY CartDetails.materialID")
    List<CartDetails> getCartItemsOfCustomer(int cartHeaderId);
    */

    /*
    @Query("SELECT * FROM CartDetails WHERE cartHeaderID =:cartHeaderId")
    List<CartDetails> getCartItemsOfCustomer(int cartHeaderId);
    */

    // delete compelete cart of particular cartHeaderID
    @Query("DELETE FROM CartDetails WHERE cartHeaderID =:cardHeaderId")
    void deleteCartDetailsOfCartDetails(int cardHeaderId);

    @Query("DELETE FROM CartDetails WHERE cartHeaderID IN (:cardHeaderId)")
    void deleteCartDetailsOfCartDetails(int[] cardHeaderId);

    @Query("UPDATE CartDetails SET deliveryDate='' WHERE cartHeaderID =:cardHeaderId")
    void updateDeliveryDate(String cardHeaderId);

    @Query("UPDATE CartDetails SET isUpdated=1 WHERE materialID =:materialID")
    void updateIsUpadated(String materialID);

    @Query("SELECT COUNT(*) FROM CartDetails WHERE cartHeaderID =:cardHeaderId")
    int getCartDetailsCountByCartHeaderID(int cardHeaderId);

    @Query("SELECT COUNT(*) FROM CartDetails WHERE cartHeaderId IN (SELECT cartHeaderID FROM CartHeader WHERE isApproved=0) AND (CartDetails.offerItemCartDetailsID IS NULL OR CartDetails.offerItemCartDetailsID='-1')")
    int getCartDetailsCountIsApproved();

    @Query("SELECT COUNT(*) FROM CartDetails WHERE customerId=:customerId AND cartHeaderId=:cartHeaderId AND materialID=:materialID AND (CartDetails.offerItemCartDetailsID IS NULL OR CartDetails.offerItemCartDetailsID='-1')")
    int getCartDetailsCountByMaterialId(int customerId, int cartHeaderId,int materialID);

    @Query("UPDATE CartDetails SET deliveryDate = '' WHERE cartHeaderId IN (SELECT cartHeaderID FROM CartHeader WHERE isApproved = 0) ")
    void resetDeliveryDates();

    @Query("UPDATE CartDetails SET quantity=:qty,isUpdated=1,price='0' WHERE materialID =:materialID AND customerId=:customerId AND cartHeaderId=:cartHeaderId AND (CartDetails.offerItemCartDetailsID IS NULL OR CartDetails.offerItemCartDetailsID='-1')")
    void updateQantity(String qty,String materialID,String customerId,String cartHeaderId );

    @Query("Select quantity FROM CartDetails WHERE materialID =:materialID AND customerId=:customerId AND cartHeaderId=:cartHeaderId AND (CartDetails.offerItemCartDetailsID IS NULL OR CartDetails.offerItemCartDetailsID='-1')")
    String getQantity(String materialID,String customerId,String cartHeaderId );

    @Query("DELETE FROM CartDetails WHERE cartDetailsId =:cartDetailsId")
    void deleteItemByCartDetailsId(String cartDetailsId);

    @Query("SELECT * FROM CartDetails WHERE offerItemCartDetailsID =:cartDetailsId AND (CartDetails.offerItemCartDetailsID IS NOT NULL OR CartDetails.offerItemCartDetailsID !='-1')")
    List<CartDetails> getCartDetailsList(String cartDetailsId);

    @Query("SELECT sum((quantity*price)) as total FROM CartDetails WHERE cartHeaderId IN (SELECT cartHeaderId FROM CartHeader WHERE isApproved=0 )")
    int getTotal();

    @Query("SELECT COUNT(*) FROM CartDetails WHERE cartHeaderId IN (SELECT cartHeaderId FROM CartHeader WHERE customerId=:customerId) AND materialID = :materialID")
    int getMaterialCount(String customerId,String materialID);

}
