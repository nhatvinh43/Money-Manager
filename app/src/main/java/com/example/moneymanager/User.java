package com.example.moneymanager;

public class User {
    private String avatarURL;
    private String email;
    private String fullName;

    public User(String avatarURL, String email, String fullName) {
        this.avatarURL = avatarURL;
        this.email = email;
        this.fullName = fullName;
    }

    public String getAvatarURL() {
        return avatarURL;
    }

    public void setAvatarURL(String avatarURL) {
        this.avatarURL = avatarURL;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
}
