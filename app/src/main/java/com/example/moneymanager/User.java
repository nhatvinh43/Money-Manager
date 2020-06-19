package com.example.moneymanager;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class User {

    //UserID sẽ được tạo tự động
    @PrimaryKey(autoGenerate = true)
    private int userID;

    @ColumnInfo(name = "col_Name")
    private String name;

    @ColumnInfo(name = "col_Username")
    private String userName;

    @ColumnInfo(name = "col_Password")
    private String password;

    @ColumnInfo(name = "col_Avatar")
    private String avatar;

    public User(String name, String userName, String password, String avatar) {
        this.name = name;
        this.userName = userName;
        this.password = password;
        this.avatar = avatar;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
}
