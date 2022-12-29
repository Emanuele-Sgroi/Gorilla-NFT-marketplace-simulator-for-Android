package com.example.gorilla_nft_app.Model;

public class Inquiries {

    private String data, email, id, message, subject, time, username;

    public Inquiries() {
    }

    public Inquiries(String data, String email, String id, String message, String subject, String time, String username) {
        this.data = data;
        this.email = email;
        this.id = id;
        this.message = message;
        this.subject = subject;
        this.time = time;
        this.username = username;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
