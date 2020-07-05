package com.example.moneymanager;



public class Currency {
    //Currency sẽ cố định/
    private String currencyId;
    private String currencyName;
    private String nation;

    public Currency(String currencyId, String currencyName, String nation) {
        this.currencyId = currencyId;
        this.currencyName = currencyName;
        this.nation = nation;
    }

    public Currency(String currencyId, String currencyName) {
        this.currencyId = currencyId;
        this.currencyName = currencyName;
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

    public String getNation() {
        return nation;
    }

    public void setNation(String nation) {
        this.nation = nation;
    }
}
