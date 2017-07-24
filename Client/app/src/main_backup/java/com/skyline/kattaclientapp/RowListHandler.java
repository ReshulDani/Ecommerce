package com.skyline.kattaclientapp;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ameyaapte1 on 25/5/16.
 */
class RowListHandler {
    private List<RowObject> rowObjectList;


    public RowListHandler() {
        rowObjectList = new ArrayList<>();
    }

    public String getId(int position) {
        return rowObjectList.get(position).getId();
    }

    public int getIs_veg(int position) {
        return rowObjectList.get(position).getIs_veg();
    }

    public void setIs_veg(int position, int is_veg) {
        rowObjectList.get(position).setIs_veg(is_veg);
    }

    public String getImageUrl(int position) {
        return rowObjectList.get(position).getImage_url();
    }

    public void setImageUrl(int position, String image_url) {
        rowObjectList.get(position).setImage_url(image_url);
    }

    public int getListsize() {
        return rowObjectList.size();
    }

    public RowObject getRowListObject(int position) {
        return rowObjectList.get(position);
    }

    public String getName(int position) {
        return rowObjectList.get(position).getName();
    }


    public void setName(int position, String name) {
        rowObjectList.get(position).setName(name);
    }

    public void setPrice(int position, String price) {
        rowObjectList.get(position).setPrice(price);
    }

    public String getPrice(int position) {
        return rowObjectList.get(position).getPrice();
    }

    public void setQuantity(int position, int quantity) {
        rowObjectList.get(position).setQuantity(quantity);
    }

    public int getQuantity(int postion) {
        return rowObjectList.get(postion).getQuantity();
    }

    public void clearAll() {
        rowObjectList.clear();
    }

    public void remove(int position) {
        rowObjectList.remove(position);
    }

    public void addRow(RowObject rowObject) {
        rowObjectList.add(rowObject);
    }
}
