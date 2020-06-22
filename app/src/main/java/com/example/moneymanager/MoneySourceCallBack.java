package com.example.moneymanager;

import java.util.ArrayList;

public interface MoneySourceCallBack {
    void onCallBack(ArrayList<MoneySource> list);
    void onCallBackFail(String message);
}
