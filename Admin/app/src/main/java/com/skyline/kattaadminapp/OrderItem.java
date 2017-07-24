package com.skyline.kattaadminapp;

/**
 * Created by MIHIR on 30-05-2016.
 */
public class OrderItem {
    private String itemName;
    private String itemQuantity;
    private Integer itemTotal;

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getItemQuantity() {
        return itemQuantity;
    }

    public void setItemQuantity(String itemQuantity) {
        this.itemQuantity = itemQuantity;
    }

    public Integer getItemTotal() {
        return itemTotal;
    }

    public void setItemTotal(Integer itemTotal) {
        this.itemTotal = itemTotal;
    }
}