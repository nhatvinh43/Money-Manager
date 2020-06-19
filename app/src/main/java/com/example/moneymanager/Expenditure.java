package com.example.moneymanager;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Expenditure {
    //expenditure sẽ cố định và được khởi tạo từ khi cài đặt ứng dụng

    //expenditureID sẽ được tạo tự động
    @PrimaryKey(autoGenerate = true)
    private int expenditureID;

    @ColumnInfo(name = "col_Name")
    private String name;

    //type lưu giá trị thu hoặc chi có thể dùng boolean
    @ColumnInfo(name = "col_Type")
    private boolean type;

    @ColumnInfo(name = "col_Icon")
    private String icon;

    public Expenditure(String name, boolean type, String icon) {
        this.name = name;
        this.type = type;
        this.icon = icon;
    }

    public int getExpenditureID() {
        return expenditureID;
    }

    public void setExpenditureID(int expenditureID) {
        this.expenditureID = expenditureID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean getType() {
        return type;
    }

    public void setType(boolean type) {
        this.type = type;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }
}

