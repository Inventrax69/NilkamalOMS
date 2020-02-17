package com.example.inventrax.falconOMS.util;

import android.content.Context;

import com.example.inventrax.falconOMS.R;
import com.example.inventrax.falconOMS.model.KeyValues;
import com.example.inventrax.falconOMS.model.MainMenu;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainMenuList {

    Context context;

    public MainMenuList(Context context){
        this.context=context;
    }

    public List<MainMenu> getMainMenu(String userRoleName){
        final List<MainMenu> arraylist = new ArrayList<>();
        if(userRoleName.equals(KeyValues.USER_ROLE_NAME_BRANCH_MANAGER)){
            arraylist.add(new MainMenu(10, KeyValues.APPROVALS_TITLE, true,R.string.approvals));
            arraylist.add(new MainMenu(11, KeyValues.COMPLAINTS_TITLE, true,R.string.complaints));
        }else if(userRoleName.equals(KeyValues.USER_ROLE_NAME_SUPPLY_CHAIN_MANAGER)){
            arraylist.add(new MainMenu(10, KeyValues.APPROVALS_TITLE, true,R.string.approvals));
            arraylist.add(new MainMenu(11, KeyValues.COMPLAINTS_TITLE, true,R.string.complaints));
        }else if(userRoleName.equals(KeyValues.USER_ROLE_NAME_BCO_RCO)){
            arraylist.add(new MainMenu(1, KeyValues.CUSTOMER_TITLE, true,R.string.customer));
            arraylist.add(new MainMenu(2, KeyValues.ORDER_BOOKING_TITLE, true,R.string.order_booking));
            arraylist.add(new MainMenu(4, KeyValues.ORDER_ASSISTANCE_TITLE, true,R.string.order_assistance));
            arraylist.add(new MainMenu(9, KeyValues.COMPLAINTS_TITLE, true,R.string.complaints));
        }else{
            arraylist.add(new MainMenu(1, KeyValues.CUSTOMER_TITLE, true,R.string.customer));
            arraylist.add(new MainMenu(2, KeyValues.ORDER_BOOKING_TITLE, true,R.string.order_booking));
            arraylist.add(new MainMenu(3, KeyValues.ORDER_TRACKING_TITLE, true,R.string.order_tracking));
            arraylist.add(new MainMenu(4, KeyValues.ORDER_ASSISTANCE_TITLE, true,R.string.order_assistance));
            arraylist.add(new MainMenu(5, KeyValues.ORDER_LIST_TITLE, true,R.string.orders_list));
            arraylist.add(new MainMenu(6, KeyValues.E_INFORMATION_TITLE, true,R.string.e_info));
            arraylist.add(new MainMenu(7, KeyValues.DASHBOARD_TITLE, true,R.string.dashboard));
            arraylist.add(new MainMenu(8, KeyValues.INVENTORY_TITLE, true,R.string.inventory));
            arraylist.add(new MainMenu(9, KeyValues.COMPLAINTS_TITLE, true,R.string.complaints));
            arraylist.add(new MainMenu(10, KeyValues.APPROVALS_TITLE, true,R.string.approvals));
        }

        Collections.sort(arraylist, MainMenu.GetOrderNumber);
        return arraylist;
    }

}
