package com.example.moneymanager;


import android.os.Parcel;
import android.os.Parcelable;

import java.sql.Timestamp;
import java.util.Date;

public class Transaction implements Parcelable {
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

    protected Transaction(Parcel in) {
        description = in.readString();
        expenditureId = in.readString();
        expenditureName = in.readString();
        transactionAmount = in.readDouble();
        transactionId = in.readString();
        moneySourceId = in.readString();
        transactionIsIncome = in.readByte() != 0;
        transactionTime = new Timestamp(in.readLong());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(description);
        dest.writeString(expenditureId);
        dest.writeString(expenditureName);
        dest.writeDouble(transactionAmount.doubleValue());
        dest.writeString(transactionId);
        dest.writeString(moneySourceId);
        dest.writeByte((byte) (transactionIsIncome ? 1 : 0));
        dest.writeLong(transactionTime.getTime());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Transaction> CREATOR = new Creator<Transaction>() {
        @Override
        public Transaction createFromParcel(Parcel in) {
            return new Transaction(in);
        }

        @Override
        public Transaction[] newArray(int size) {
            return new Transaction[size];
        }
    };

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
