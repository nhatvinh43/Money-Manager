package com.example.moneymanager;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class MoneySource {

    @PrimaryKey(autoGenerate = true)
    private int moneySourceID;

    @ColumnInfo(name = "col_UserID")
    private int userID;

    @ColumnInfo(name = "col_Name")
    private int name;

    @ColumnInfo(name = "col_CurrentMoney")
    private float currentMoney;

    @ColumnInfo(name = "col_Limit")
    private float limit;

    @ColumnInfo(name = "col_CurrencyID")
    private int currencyID;

    public MoneySource(int userID, int name, float currentMoney, float limit, int currencyID) {
        this.userID = userID;
        this.name = name;
        this.currentMoney = currentMoney;
        this.limit = limit;
        this.currencyID = currencyID;
    }

    public int getMoneySourceID() {
        return moneySourceID;
    }

    public void setMoneySourceID(int moneySourceID) {
        this.moneySourceID = moneySourceID;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public int getName() {
        return name;
    }

    public void setName(int name) {
        this.name = name;
    }

    public float getCurrentMoney() {
        return currentMoney;
    }

    public void setCurrentMoney(float currentMoney) {
        this.currentMoney = currentMoney;
    }

    public float getLimit() {
        return limit;
    }

    public void setLimit(float limit) {
        this.limit = limit;
    }

    public int getCurrencyID() {
        return currencyID;
    }

    public void setCurrencyID(int currency) {
        this.currencyID = currencyID;
    }
}
