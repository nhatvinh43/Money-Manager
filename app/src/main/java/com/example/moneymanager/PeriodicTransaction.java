package com.example.moneymanager;

import java.sql.Timestamp;

public class PeriodicTransaction {
    private String description;
    private String expenditureId;
    private String expenditureName;
    private Number transactionAmount;
    private String transactionId;
    private String moneySourceId;
    private String moneySourceName;
    private boolean transactionIsIncome;
    private Timestamp transactionTime;
    private String periodicType;

    public PeriodicTransaction(String description, String expenditureId, String expenditureName, Number transactionAmount, String transactionId, String moneySourceId, String moneySourceName, boolean transactionIsIncome, Timestamp transactionTime, String periodicType) {
        this.description = description;
        this.expenditureId = expenditureId;
        this.expenditureName = expenditureName;
        this.transactionAmount = transactionAmount;
        this.transactionId = transactionId;
        this.moneySourceId = moneySourceId;
        this.moneySourceName = moneySourceName;
        this.transactionIsIncome = transactionIsIncome;
        this.transactionTime = transactionTime;
        this.periodicType = periodicType;
    }

    public String getMoneySourceName() {
        return moneySourceName;
    }

    public void setMoneySourceName(String moneySourceName) {
        this.moneySourceName = moneySourceName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public Number getTransactionAmount() {
        return transactionAmount;
    }

    public void setTransactionAmount(Number transactionAmount) {
        this.transactionAmount = transactionAmount;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getMoneySourceId() {
        return moneySourceId;
    }

    public void setMoneySourceId(String moneySourceId) {
        this.moneySourceId = moneySourceId;
    }

    public boolean getTransactionIsIncome() {
        return transactionIsIncome;
    }

    public void setTransactionIsIncome(boolean transactionIsIncome) {
        this.transactionIsIncome = transactionIsIncome;
    }

    public Timestamp getTransactionTime() {
        return transactionTime;
    }

    public void setTransactionTime(Timestamp transactionTime) {
        this.transactionTime = transactionTime;
    }

    public String getPeriodicType() {
        return periodicType;
    }

    public void setPeriodicType(String periodicType) {
        this.periodicType = periodicType;
    }
}
