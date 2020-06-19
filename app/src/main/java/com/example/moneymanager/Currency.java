package com.example.moneymanager;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Currency {
    //Currency sẽ cố định/

    //CurrencyID sẽ được khởi tạo tự động
    @PrimaryKey(autoGenerate = true)
    private int currencyID;

    @ColumnInfo(name = "col_CurrencyUnit")
    private String currencyUnit;

    @ColumnInfo(name = "col_Nation")
    private String nation;

    public Currency(String currencyUnit, String nation) {
        this.currencyUnit = currencyUnit;
        this.nation = nation;
    }

    public int getCurrencyID() {
        return currencyID;
    }

    public void setCurrencyID(int currencyID) {
        this.currencyID = currencyID;
    }

    public String getCurrencyUnit() {
        return currencyUnit;
    }

    public void setCurrencyUnit(String currencyUnit) {
        this.currencyUnit = currencyUnit;
    }

    public String getNation() {
        return nation;
    }

    public void setNation(String nation) {
        this.nation = nation;
    }
}
