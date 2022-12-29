package com.example.gorilla_nft_app.Model;

public class Cards {

    private  String cardholder, cardNumber, endingNumber, expMonth, expYear, country;

    public Cards()
    {

    }

    public Cards(String cardholder, String cardNumber, String endingNumber, String expMonth, String expYear, String country) {
        this.cardholder = cardholder;
        this.cardNumber = cardNumber;
        this.endingNumber = endingNumber;
        this.expMonth = expMonth;
        this.expYear = expYear;
        this.country = country;
    }

    public String getCardholder() {
        return cardholder;
    }

    public void setCardholder(String cardholder) {
        this.cardholder = cardholder;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getEndingNumber() {
        return endingNumber;
    }

    public void setEndingNumber(String endingNumber) {
        this.endingNumber = endingNumber;
    }

    public String getExpMonth() {
        return expMonth;
    }

    public void setExpMonth(String expMonth) {
        this.expMonth = expMonth;
    }

    public String getExpYear() {
        return expYear;
    }

    public void setExpYear(String expYear) {
        this.expYear = expYear;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }
}
