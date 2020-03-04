package com.example.inventrax.falconOMS.model;

import java.util.Comparator;

public class MainMenu {

    private int orderNumber;
    private String tileName;
    private int displayName;
    private boolean isAvailable;


    public MainMenu(int orderNumber, String tileName, boolean isAvailable,int displayName) {
        this.orderNumber = orderNumber;
        this.tileName = tileName;
        this.isAvailable = isAvailable;
        this.displayName=displayName;
    }


    public int getDisplayName() {
        return displayName;
    }

    public void setDisplayName(int displayName) {
        this.displayName = displayName;
    }

    public int getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(int orderNumber) {
        this.orderNumber = orderNumber;
    }

    public String getTileName() {
        return tileName;
    }

    public void setTileName(String tileName) {
        this.tileName = tileName;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean available) {
        isAvailable = available;
    }

    public static Comparator<MainMenu> GetOrderNumber = new Comparator<MainMenu>() {

        public int compare(MainMenu s1, MainMenu s2) {

            int OrderNumber1 = s1.getOrderNumber();
            int OrderNumber2 = s2.getOrderNumber();

            /*For ascending order*/
            return OrderNumber1-OrderNumber2;

            /*For descending order*/
            //OrderNumber2-OrderNumber1;
        }

    };


}
