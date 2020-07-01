package com.example.moneymanager;

import java.util.ArrayList;

public interface TransactionCallBack {
    void onCallBack(ArrayList<Transaction> list);
    void onCallBackFail(String message);
}
