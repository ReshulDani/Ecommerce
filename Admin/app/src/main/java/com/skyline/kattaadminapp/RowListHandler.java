package com.skyline.kattaadminapp;

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

    public int getListsize() {
        return rowObjectList.size();
    }

    public RowObject getRowListObject(int position) {
        return rowObjectList.get(position);
    }

    public String getName(int position) {
        return rowObjectList.get(position).getName();
    }

    public String getImageUrl(int position){
      return rowObjectList.get(position).getImage_url();
    }

    public void setImageUrl(int position,String url){
        rowObjectList.get(position).setImage_url(url);
    }
    public String getCategory(int position) {
        return rowObjectList.get(position).getCategory();
    }

    public void setCategory(int position, String category) {
        rowObjectList.get(position).setCategory(category);
    }


    public Boolean getCheckbox_flag(int position) {
        return rowObjectList.get(position).isCheckbox_flag();
    }

    public void setAvailability(String name, Boolean flag) {
        for (int i = 0; i < rowObjectList.size(); i++) {
            if (rowObjectList.get(i).getName().equals(name)) {
                rowObjectList.get(i).setCheckbox_flag(flag);
            }
        }
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
