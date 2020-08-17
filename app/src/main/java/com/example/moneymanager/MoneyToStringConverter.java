package com.example.moneymanager;

import android.util.Log;

import java.math.RoundingMode;
import java.text.DecimalFormat;

public class MoneyToStringConverter {
    public String moneyToString(double amount) {
        if(amount < 0) return "-" + moneyToString(-amount);
        if(amount == 0) return "0";
        StringBuilder mString = new StringBuilder();
        long mAmount = (long) amount;
        double remainder = amount - mAmount;
        int count = 0;
        while (mAmount > 0) {
            mString.insert(0, Long.toString(Math.floorMod(mAmount, 10)));
            mAmount /= 10;
            count++;

            if (count == 3 && mAmount != 0) {
                mString.insert(0, ",");
                count = 0;
            }
        }
        if(mString.length() == 0) mString.insert(0, "0");

        String decimal = "";
        if (remainder > 0) {
            decimal = String.valueOf(remainder).substring(String.valueOf(remainder).indexOf("."), String.valueOf(remainder).indexOf(".") + 3);
        }

        return mString.toString() + decimal;
    }

    public double stringToMoney(String amountStr) {
        if(amountStr.charAt(0) == '-') return -stringToMoney(amountStr.substring(1, amountStr.length()));
        if(amountStr.equals("0")) return 0.0;
        StringBuilder mString = new StringBuilder(amountStr);
        int index = mString.indexOf(",");
        while(index != -1) {
            mString.deleteCharAt(index);
            index = mString.indexOf(",");
        }
        Log.d("------Test-----", mString.toString());
        return Double.valueOf(mString.toString());
    }
}
