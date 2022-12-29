package com.example.gorilla_nft_app.Model;

public class Products {

    private String name, price, seller, preview, address, category, date, id;

    public Products()
    {
    }

    public Products(String name, String price, String seller, String preview, String address, String category, String date, String id) {
        this.name = name;
        this.price = price;
        this.seller = seller;
        this.preview = preview;
        this.address = address;
        this.category = category;
        this.date = date;
        this.id = id;
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

    public String getSeller() {
        return seller;
    }

    public void setSeller(String seller) {
        this.seller = seller;
    }

    public String getPreview() {
        return preview;
    }

    public void setPreview(String preview) {
        this.preview = preview;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
