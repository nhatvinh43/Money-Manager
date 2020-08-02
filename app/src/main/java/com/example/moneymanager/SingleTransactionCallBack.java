package com.example.moneymanager;

public interface SingleTransactionCallBack {
    void onCallBack(Transaction ts);
    void onCallBackFailed(String msg);
}
