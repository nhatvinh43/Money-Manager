package com.example.moneymanager;


import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class MoneySource implements Parcelable {
    private Number amount;
    private String currencyId;
    private String currencyName;
    private Number limit;
    private String moneySourceId;
    private String moneySourceName;
    private String userId;
    private ArrayList<Transaction> transactionsList;

    public MoneySource(){
        this.amount = 0.0;
        this.currencyId = "";
        this.currencyName = "";
        this.limit = 0.0;
        this.moneySourceId = "";
        this.moneySourceName = "";
        this.userId = "";
        this.transactionsList = null;
    }

    public MoneySource(Number amount, String currencyId, String currencyName,
                       Number limit, String moneySourceId, String moneySourceName, String userId) {
        this.amount = amount;
        this.currencyId = currencyId;
        this.currencyName = currencyName;
        this.limit = limit;
        this.moneySourceId = moneySourceId;
        this.moneySourceName = moneySourceName;
        this.userId = userId;
        this.transactionsList = null;
    }

    protected MoneySource(Parcel in) {
        amount = in.readDouble();
        currencyId = in.readString();
        currencyName = in.readString();
        moneySourceId = in.readString();
        moneySourceName = in.readString();
        limit = in.readDouble();
        userId = in.readString();
    }

    public static final Creator<MoneySource> CREATOR = new Creator<MoneySource>() {
        @Override
        public MoneySource createFromParcel(Parcel in) {
            return new MoneySource(in);
        }

        @Override
        public MoneySource[] newArray(int size) {
            return new MoneySource[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeDouble(amount.doubleValue());
        parcel.writeString(currencyId);
        parcel.writeString(currencyName);
        parcel.writeString(moneySourceId);
        parcel.writeString(moneySourceName);
        parcel.writeDouble(limit.doubleValue());
        parcel.writeString(userId);
    }

    public Number getAmount() {
        return amount;
    }

    public void setAmount(Number amount) {
        this.amount = amount;
    }

    public String getCurrencyId() {
        return currencyId;
    }

    public void setCurrencyId(String currencyId) {
        this.currencyId = currencyId;
    }

    public String getCurrencyName() {
        return currencyName;
    }

    public void setCurrencyName(String currencyName) {
        this.currencyName = currencyName;
    }

    public Number getLimit() {
        return limit;
    }

    public void setLimit(Number limit) {
        this.limit = limit;
    }

    public String getMoneySourceId() {
        return moneySourceId;
    }

    public void setMoneySourceId(String moneySourceId) {
        this.moneySourceId = moneySourceId;
    }

    public String getMoneySourceName() {
        return moneySourceName;
    }

    public void setMoneySourceName(String moneySourceName) {
        this.moneySourceName = moneySourceName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public ArrayList<Transaction> getTransactionsList() {
        return transactionsList;
    }

    public void setTransactionsList(ArrayList<Transaction> transactionsList) {
        this.transactionsList = transactionsList;
    }
}
