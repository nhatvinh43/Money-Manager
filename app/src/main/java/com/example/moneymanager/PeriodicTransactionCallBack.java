package com.example.moneymanager;

import java.util.ArrayList;

public interface PeriodicTransactionCallBack {
    void onCallBack(ArrayList<PeriodicTransaction> list);
    void onCallBackFail(String message);
}
