package com.example.moneymanager;


import java.sql.Timestamp;
import java.util.Date;

public class Transaction {
    private String description;
    private String expenditureId;
    private String expenditureName;
    private Number transactionAmout;
    private String transactionId;
    private String transactionIsIncome;
    private Date transactionTime;

    public Transaction(String description, String expenditureId, String expenditureName,
                       Number transactionAmout, String transactionId, String transactionIsIncome, Date transactionTime) {
        this.description = description;
        this.expenditureId = expenditureId;
        this.expenditureName = expenditureName;
        this.transactionAmout = transactionAmout;
        this.transactionId = transactionId;
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

    public Number getTransactionAmout() {
        return transactionAmout;
    }

    public void setTransactionAmout(Number transactionAmout) {
        this.transactionAmout = transactionAmout;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getTransactionIsIncome() {
        return transactionIsIncome;
    }

    public void setTransactionIsIncome(String transactionIsIncome) {
        this.transactionIsIncome = transactionIsIncome;
    }

    public Date getTransactionTime() {
        return transactionTime;
    }

    public void setTransactionTime(Date transactionTime) {
        this.transactionTime = transactionTime;
    }
}
