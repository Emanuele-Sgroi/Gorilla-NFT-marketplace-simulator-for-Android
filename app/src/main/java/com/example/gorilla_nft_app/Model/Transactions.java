package com.example.gorilla_nft_app.Model;

public class Transactions {

    private String buyer, buyerAddress, nftQuantity, paymentMethod, transactionDate, transactionTime, transactionId, worthDollar, worthEth;

    public Transactions() {
    }

    public Transactions(String buyer, String buyerAddress, String nftQuantity, String paymentMethod, String transactionDate, String transactionTime, String transactionId, String worthDollar, String worthEth) {
        this.buyer = buyer;
        this.buyerAddress = buyerAddress;
        this.nftQuantity = nftQuantity;
        this.paymentMethod = paymentMethod;
        this.transactionDate = transactionDate;
        this.transactionTime = transactionTime;
        this.transactionId = transactionId;
        this.worthDollar = worthDollar;
        this.worthEth = worthEth;
    }

    public String getBuyer() {
        return buyer;
    }

    public void setBuyer(String buyer) {
        this.buyer = buyer;
    }

    public String getBuyerAddress() {
        return buyerAddress;
    }

    public void setBuyerAddress(String buyerAddress) {
        this.buyerAddress = buyerAddress;
    }

    public String getNftQuantity() {
        return nftQuantity;
    }

    public void setNftQuantity(String nftQuantity) {
        this.nftQuantity = nftQuantity;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(String transactionDate) {
        this.transactionDate = transactionDate;
    }

    public String getTransactionTime() {
        return transactionTime;
    }

    public void setTransactionTime(String transactionTime) {
        this.transactionTime = transactionTime;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getWorthDollar() {
        return worthDollar;
    }

    public void setWorthDollar(String worthDollar) {
        this.worthDollar = worthDollar;
    }

    public String getWorthEth() {
        return worthEth;
    }

    public void setWorthEth(String worthEth) {
        this.worthEth = worthEth;
    }
}
