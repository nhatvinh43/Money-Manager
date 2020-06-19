package com.example.moneymanager;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Transaction {

    @PrimaryKey(autoGenerate = true)
    private int transactionID;

    @ColumnInfo(name = "col_ExpenditureID")
    private int expenditureID;

    @ColumnInfo(name = "col_MoneySourceID")
    private int moneySourceID;

    @ColumnInfo(name = "col_Description")
    private String description;

    @ColumnInfo(name = "col_Money")
    private float money;

    @ColumnInfo(name = "col_Time")
    private long time;

    public Transaction(int expenditureID, int moneySourceID, String description, float money, long time) {
        this.expenditureID = expenditureID;
        this.moneySourceID = moneySourceID;
        this.description = description;
        this.money = money;
        this.time = time;
    }

    public int getTransactionID() {
        return transactionID;
    }

    public void setTransactionID(int transactionID) {
        this.transactionID = transactionID;
    }

    public int getExpenditureID() {
        return expenditureID;
    }

    public void setExpenditureID(int expenditureID) {
        this.expenditureID = expenditureID;
    }

    public int getMoneySourceID() {
        return moneySourceID;
    }

    public void setMoneySourceID(int moneySourceID) {
        this.moneySourceID = moneySourceID;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public float getMoney() {
        return money;
    }

    public void setMoney(float money) {
        this.money = money;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
