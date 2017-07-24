package com.skyline.kattaclientapp;

/**
 * Created by ameyaapte1 on 25/5/16.
 */
class RowObject {
    private String id;
    private String name;
    private String price;
    private String image_url;
    private int quantity;
    private int is_veg;

    public RowObject(String id, String name, String price, int quantity) {
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.id = id;
    }

    public int getIs_veg() {
        return is_veg;
    }

    public void setIs_veg(int is_veg) {
        this.is_veg = is_veg;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
