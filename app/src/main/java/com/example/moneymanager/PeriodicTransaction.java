package com.example.moneymanager;

import android.os.Parcel;
import android.os.Parcelable;

import java.sql.Timestamp;

public class PeriodicTransaction implements Parcelable {
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

    public PeriodicTransaction() {
        this.description = "";
        this.expenditureId = "";
        this.expenditureName = "";
        this.transactionAmount = 0;
        this.transactionId = "";
        this.moneySourceId = "";
        this.moneySourceName = "";
        this.transactionIsIncome = true;
        this.transactionTime = null;
        this.periodicType = "";
    }

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

    protected PeriodicTransaction(Parcel in) {
        description = in.readString();
        expenditureId = in.readString();
        expenditureName = in.readString();
        transactionAmount = in.readDouble();
        transactionId = in.readString();
        moneySourceId = in.readString();
        moneySourceName = in.readString();
        transactionIsIncome = in.readByte() != 0;
        periodicType = in.readString();
        transactionTime = new Timestamp(in.readLong());
    }

    public static final Creator<PeriodicTransaction> CREATOR = new Creator<PeriodicTransaction>() {
        @Override
        public PeriodicTransaction createFromParcel(Parcel in) {
            return new PeriodicTransaction(in);
        }

        @Override
        public PeriodicTransaction[] newArray(int size) {
            return new PeriodicTransaction[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(description);
        parcel.writeString(expenditureId);
        parcel.writeString(expenditureName);
        parcel.writeDouble(transactionAmount.doubleValue());
        parcel.writeString(transactionId);
        parcel.writeString(moneySourceId);
        parcel.writeString(moneySourceName);
        parcel.writeByte((byte) (transactionIsIncome ? 1 : 0));
        parcel.writeString(periodicType);
        parcel.writeLong(transactionTime.getTime());
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
