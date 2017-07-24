package com.skyline.kattaclientapp;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by MIHIR on 30-05-2016.
 */
public class OrderListHandler {

    private List<OrderObject> orderObjectList;

    public OrderListHandler() {
        orderObjectList = new ArrayList<>();
    }

    public int getListSize() {
        return orderObjectList.size();
    }

    public OrderObject getOrderObject(int position) {
        return orderObjectList.get(position);
    }

    public void setuserName(int position, String name) {
        orderObjectList.get(position).setUserName(name);
    }

    public void setStatus(int position, int status) {
        orderObjectList.get(position).setStatus(status);
    }

    public String getTimestamp(int position) {
        return orderObjectList.get(position).getTimestamp();
    }

    public void setTimestamp(int position, String timestamp) {
        orderObjectList.get(position).setTimestamp(timestamp);
    }

    public int getStatus(int position) {
        return orderObjectList.get(position).getStatus();
    }


    public String getuserName(int position) {
        return orderObjectList.get(position).getUserName();
    }

    public void setTotal(int position, int total) {
        orderObjectList.get(position).setTotal(total);
    }

    public int getTotal(int position) {
        return orderObjectList.get(position).getTotal();
    }

    public void setOrderId(int position, int id) {
        orderObjectList.get(position).setOrderId(id);
    }

    public int getOrderId(int position) {
        return orderObjectList.get(position).getOrderId();
    }

    public void setphoneNo(int position, String phoneNo) {
        orderObjectList.get(position).setTimestamp(phoneNo);
    }

    public String getphoneNo(int position) {
        return orderObjectList.get(position).getTimestamp();
    }

    public void setDelivery(int position, String delivery) {
        orderObjectList.get(position).setDelivery(delivery);
    }

    public String getDelivery(int position) {
        return orderObjectList.get(position).getDelivery();
    }

    public List<OrderItem> getItems(int position) {
        return orderObjectList.get(position).getItems();
    }

    public void setPayment(int position, String payment) {
        orderObjectList.get(position).setPayment(payment);
    }

    public String getPayment(int position) {
        return orderObjectList.get(position).getPayment();
    }

    public void setItemName(int position1, int position2, String name) {
        orderObjectList.get(position1).getItems().get(position2).setItemName(name);
    }

    public String getItemName(int position1, int position2) {
        return orderObjectList.get(position1).getItems().get(position2).getItemName();
    }

    public void setItemQuantity(int position1, int position2, int quantity) {
        orderObjectList.get(position1).getItems().get(position2).setItemQuantity(String.valueOf(quantity));
    }

    public String getItemQuantity(int position1, int position2) {
        return orderObjectList.get(position1).getItems().get(position2).getItemQuantity();
    }

    public void setItemTotal(int position1, int position2, int total) {
        orderObjectList.get(position1).getItems().get(position2).setItemTotal(total);
    }

    public int getItemTotal(int position1, int position2) {
        return orderObjectList.get(position1).getItems().get(position2).getItemTotal();
    }

    public void addOrder(OrderObject orderObject) {
        orderObjectList.add(orderObject);
    }

    public void remove(int position) {
        orderObjectList.remove(position);
    }

    public void additem(int position, OrderItem orderItem) {
        orderObjectList.get(position).getItems().add(orderItem);
    }

    public int getItemSize(int position) {
        return orderObjectList.get(position).getItems().size();
    }
}
