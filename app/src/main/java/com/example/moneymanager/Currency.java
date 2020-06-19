package com.example.moneymanager;



public class Currency {
    //Currency sẽ cố định/
    private String currencyId;
    private String getCurrencyName;
    private String nation;

    public Currency(String currencyId, String getCurrencyName, String nation) {
        this.currencyId = currencyId;
        this.getCurrencyName = getCurrencyName;
        this.nation = nation;
    }

    public Currency(String currencyId, String getCurrencyName) {
        this.currencyId = currencyId;
        this.getCurrencyName = getCurrencyName;
    }

    public String getCurrencyId() {
        return currencyId;
    }

    public void setCurrencyId(String currencyId) {
        this.currencyId = currencyId;
    }

    public String getCurrencyName() {
        return getCurrencyName;
    }

    public void setCurrencyName(String getCurrencyName) {
        this.getCurrencyName = getCurrencyName;
    }

    public String getNation() {
        return nation;
    }

    public void setNation(String nation) {
        this.nation = nation;
    }
}
