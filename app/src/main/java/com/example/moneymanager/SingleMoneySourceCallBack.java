package com.example.moneymanager;

import java.util.ArrayList;

public interface SingleMoneySourceCallBack {
    void onCallBack(MoneySource moneySource);
    void onCallBackFailed(String msg);
}
