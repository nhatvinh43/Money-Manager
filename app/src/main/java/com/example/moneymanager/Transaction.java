package com.example.moneymanager;


import java.sql.Timestamp;
import java.util.Date;

public class Transaction {
    private String description;
    private String expenditureId;
    private String expenditureName;
    private Number transactionAmount;
    private String transactionId;
    private String moneySourceId;
    private boolean transactionIsIncome;
    private Timestamp transactionTime;

    public Transaction(){
        this.description = "";
        this.expenditureId = "";
        this.expenditureName = "";
        this.transactionAmount = 0;
        this.transactionId = "";
        this.moneySourceId = "";
        this.transactionIsIncome = true;
        this.transactionTime = null;
    }

    public Transaction(String description, String expenditureId, String expenditureName,
                       Number transactionAmount, String transactionId, String moneySourceId, boolean transactionIsIncome, Timestamp transactionTime) {
        this.description = description;
        this.expenditureId = expenditureId;
        this.expenditureName = expenditureName;
        this.transactionAmount = transactionAmount;
        this.transactionId = transactionId;
        this.moneySourceId = moneySourceId;
        this.transactionIsIncome = transactionIsIncome;
        this.transactionTime = transactionTime;
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

    public String getMoneySourceId() {
        return moneySourceId;
    }

    public void setMoneySourceId(String moneySourceId) {
        this.moneySourceId = moneySourceId;
    }

    public boolean isTransactionIsIncome() {
        return transactionIsIncome;
    }
}
