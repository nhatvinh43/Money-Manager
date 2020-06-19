package com.example.moneymanager;


import java.util.ArrayList;

public class MoneySource {
    private Number amount;
    private String currencyId;
    private String currencyName;
    private Number limit;
    private String moneySourceId;
    private String moneySourceName;
    private String userId;

    public MoneySource(Number amount, String currencyId, String currencyName,
                       Number limit, String moneySourceId, String moneySourceName, String userId) {
        this.amount = amount;
        this.currencyId = currencyId;
        this.currencyName = currencyName;
        this.limit = limit;
        this.moneySourceId = moneySourceId;
        this.moneySourceName = moneySourceName;
        this.userId = userId;
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

    public String getCurrencyName(ArrayList<Currency> currencyList) {
        String name = "";

        for(Currency i : currencyList) {
            if(i.getCurrencyId().compareTo(this.currencyId) == 0) {
                name = i.getCurrencyName();
                break;
            }
        }

        return name;
    }
}
