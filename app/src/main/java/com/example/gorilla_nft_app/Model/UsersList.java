package com.example.gorilla_nft_app.Model;

public class UsersList {

    private String username, email, phone, password, picture;

    public UsersList() {
    }

    public UsersList(String username, String email, String phone, String password, String picture) {
        this.username = username;
        this.email = email;
        this.phone = phone;
        this.password = password;
        this.picture = picture;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }
}
