package com.example.moneymanager;

public class Expenditure {
    //expenditure sẽ cố định và được khởi tạo từ khi cài đặt ứng dụng
    private String expenditureId;
    private String expenditureName;
    private boolean isIncome;

    public Expenditure(String expenditureId, String expenditureName, boolean isIncome) {
        this.expenditureId = expenditureId;
        this.expenditureName = expenditureName;
        this.isIncome = isIncome;
    }

    public String getExpenditureId() {
        return expenditureId;
    }

    public void setExpenditureId(String expenditureId) {
        this.expenditureId = expenditureId;
    }

    public String getExpenditureName() {
        return expenditureName;
    }

    public void setExpenditureName(String expenditureName) {
        this.expenditureName = expenditureName;
    }

    public boolean isExpenditureType() {
        return isIncome;
    }

    public void setExpenditureType(boolean expenditureType) {
        this.isIncome = expenditureType;
    }

    public boolean isIncome() {
        return isIncome;
    }

    public void setIncome(boolean income) {
        isIncome = income;
    }
}

