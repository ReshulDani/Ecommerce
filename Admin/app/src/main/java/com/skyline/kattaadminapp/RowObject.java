package com.skyline.kattaadminapp;

/**
 * Created by ameyaapte1 on 25/5/16.
 */
class RowObject {
    private String name;
    private String price;
    private String category;
    private String image_url;
    private boolean checkbox_flag;

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }



    public RowObject(String name, String price, String category ,boolean checckbox_flag) {
        this.name = name;
        this.price = price;
        this.category=category;
        this.checkbox_flag = checckbox_flag;

    }


    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isCheckbox_flag() {
        return checkbox_flag;
    }

    public void setCheckbox_flag(boolean checkbox_flag) {
        this.checkbox_flag = checkbox_flag;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

}
