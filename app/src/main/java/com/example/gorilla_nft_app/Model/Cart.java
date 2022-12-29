package com.example.gorilla_nft_app.Model;

public class Cart {

    private String address, buyer, category, date, time, id, name, preview, priceDollar, priceETH, seller;

    public Cart() {
    }

    public Cart(String address, String buyer, String category, String date, String time, String id, String name, String preview, String priceDollar, String priceETH, String seller) {
        this.address = address;
        this.buyer = buyer;
        this.category = category;
        this.date = date;
        this.time = time;
        this.id = id;
        this.name = name;
        this.preview = preview;
        this.priceDollar = priceDollar;
        this.priceETH = priceETH;
        this.seller = seller;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getBuyer() {
        return buyer;
    }

    public void setBuyer(String buyer) {
        this.buyer = buyer;
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

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPreview() {
        return preview;
    }

    public void setPreview(String preview) {
        this.preview = preview;
    }

    public String getPriceDollar() {
        return priceDollar;
    }

    public void setPriceDollar(String priceDollar) {
        this.priceDollar = priceDollar;
    }

    public String getPriceETH() {
        return priceETH;
    }

    public void setPriceETH(String priceETH) {
        this.priceETH = priceETH;
    }

    public String getSeller() {
        return seller;
    }

    public void setSeller(String seller) {
        this.seller = seller;
    }
}
