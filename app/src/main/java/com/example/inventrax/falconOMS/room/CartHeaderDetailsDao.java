package com.example.inventrax.falconOMS.room;


import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Transaction;

import java.util.List;

@Dao
public abstract class CartHeaderDetailsDao {

    @Query("SELECT CAST(creditLimit AS INT) as value from CartHeader where cartHeaderId=:cardHeaderId")
    public abstract int getCreditLimit(int cardHeaderId);

    @Query("SELECT SUM(quantity * price) FROM CartDetails where cartHeaderID=:cardHeaderId")
    public abstract int getSumCreditLimit(int cardHeaderId);

    @Query("UPDATE CartHeader SET isCreditLimit=:isCreditLimit WHERE cartHeaderId=:cardHeaderId")
    public abstract void setIsCreditLimit(int cardHeaderId,int isCreditLimit);

    @Query("UPDATE CartHeader SET isInActive=:isInActive WHERE cartHeaderId=:cardHeaderId")
    public abstract void setIsisInActive(int cardHeaderId,int isInActive);

    @Query("SELECT COUNT(*) FROM CartDetails WHERE cartHeaderId=:cardHeaderId AND isInActive=1")
    public abstract int setIsActiveCount(int cardHeaderId);

    @Query("SELECT * FROM CartHeader WHERE cartHeaderId=:cardHeaderId")
    public abstract CartHeader setCartHeader(int cardHeaderId);

    @Query("SELECT COUNT(*) FROM CartHeader WHERE isUpdated = 1")
    public abstract int getUpdateCountCartHeader();

    @Query("SELECT COUNT(*) FROM CartDetails WHERE isUpdated = 1")
    public abstract int getUpdateCountCartDetails();

    @Query("SELECT COUNT(*) FROM CartDetails WHERE materialID = :materialID")
    public abstract int getMaterialIDCount(String materialID);

    @Query("SELECT * FROM CartHeader WHERE isApproved=0")
    public abstract List<CartHeader> getCartList();

    @Query("SELECT COUNT(*) FROM CartDetails WHERE materialID = :materialID AND customerId=:customerId AND cartHeaderId=:cartHeaderId")
    public abstract int getCartDetailsCount(String materialID,int customerId ,int cartHeaderId);

    @Query("SELECT * FROM CartDetails WHERE materialID = :materialID AND customerId=:customerId AND cartHeaderId=:cartHeaderId")
    public abstract CartDetails getCartDetails(String materialID,int customerId ,int cartHeaderId);


    @Transaction
    public CartHeader updateCartHeaderStatus(int cartHeaderId){

        int getSumCreditLimit = getSumCreditLimit(cartHeaderId) > 0 ? getSumCreditLimit(cartHeaderId) : 0;
        int getCreditLimit = getCreditLimit(cartHeaderId) > 0 ? getCreditLimit(cartHeaderId) : 0;
        int setIsActiveCount = setIsActiveCount(cartHeaderId) > 0 ? setIsActiveCount(cartHeaderId) : 0;

        if(setIsActiveCount>0){
            setIsisInActive(cartHeaderId,1);
        }else{
            setIsisInActive(cartHeaderId,0);
        }

        if(getSumCreditLimit>getCreditLimit){
            setIsCreditLimit(cartHeaderId,1);
        }else{
            setIsCreditLimit(cartHeaderId,0);
        }

        return setCartHeader(cartHeaderId);
    }

    @Transaction
    public boolean getUpdateCount(){
        int count = getUpdateCountCartHeader() + getUpdateCountCartDetails();
        if(count==0)
            return true;
        else
            return false;
    }


    @Transaction
    public String getIsPriority(String materialID){
        for(int i=0;i<getCartList().size();i++){
            if((getCartDetailsCount(materialID,getCartList().get(i).customerID,getCartList().get(i).cartHeaderID))>0){
                return ""+getCartDetails(materialID,getCartList().get(i).customerID,getCartList().get(i).cartHeaderID).isPriority;
            }
        }
        return "0";
    }
}
