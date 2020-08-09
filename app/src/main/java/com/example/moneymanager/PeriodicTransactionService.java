package com.example.moneymanager;

import android.app.IntentService;
import android.content.Intent;

import androidx.annotation.Nullable;

public class PeriodicTransactionService extends IntentService {
    public PeriodicTransactionService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

    }
}
