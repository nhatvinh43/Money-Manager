package com.example.moneymanager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.security.Provider;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import static android.content.Context.MODE_PRIVATE;

public class NewDayReceiver extends BroadcastReceiver {
    private Context context;
    private ArrayList<PeriodicTransaction> periodicTrasactionListFull;
    private DataHelper dataHelper;
    private Intent service;

    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;
        dataHelper = new DataHelper();
        loadData();

        service = new Intent(context, PeriodicTransactionService.class);

        for(PeriodicTransaction peTrans : periodicTrasactionListFull) {
           addTransaction(peTrans);
        }
    }

    private void loadData() {
        SharedPreferences sharedPreferences = context.getSharedPreferences("periodicTransactionList", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("list", null);
        Type type = new TypeToken<ArrayList<PeriodicTransaction>>() {}.getType();

        periodicTrasactionListFull = gson.fromJson(json, type);
        if(periodicTrasactionListFull == null) {
            periodicTrasactionListFull = new ArrayList<>();
        }
    }

    private void addTransaction(PeriodicTransaction peTrans) {
        int now_day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
        int now_month = Calendar.getInstance().get(Calendar.MONTH);

        switch (peTrans.getPeriodicType()) {
            case "day":
                Calendar periodicTime_day = Calendar.getInstance();
                periodicTime_day.setTimeInMillis(peTrans.getTransactionTime().getTime());

                Calendar newPeriodicTime_day = Calendar.getInstance();
                newPeriodicTime_day.set(Calendar.HOUR_OF_DAY, periodicTime_day.get(Calendar.HOUR_OF_DAY));
                peTrans.setTransactionTime(new Timestamp(newPeriodicTime_day.getTimeInMillis()));

                dataHelper.setTransactionFromPeriodicTransaction(peTrans);
                service.putExtra("periodicTransaction", peTrans);
                context.startService(service);
                break;
            case "month":
                Calendar periodicTime_month = Calendar.getInstance();
                periodicTime_month.setTimeInMillis(peTrans.getTransactionTime().getTime());

                if(periodicTime_month.get(Calendar.DAY_OF_MONTH) == now_day) {
                    Calendar newPeriodicTime_month = Calendar.getInstance();
                    newPeriodicTime_month.set(Calendar.HOUR_OF_DAY, periodicTime_month.get(Calendar.HOUR_OF_DAY));
                    peTrans.setTransactionTime(new Timestamp(newPeriodicTime_month.getTimeInMillis()));

                    dataHelper.setTransactionFromPeriodicTransaction(peTrans);
                    service.putExtra("periodicTransaction", peTrans);
                    context.startService(service);
                }
                break;
            case "year":
                Calendar periodicTime_year = Calendar.getInstance();
                periodicTime_year.setTimeInMillis(peTrans.getTransactionTime().getTime());

                if(periodicTime_year.get(Calendar.DAY_OF_MONTH) == now_day && periodicTime_year.get(Calendar.MONTH) == now_month) {
                    Calendar newPeriodicTime_year = Calendar.getInstance();
                    newPeriodicTime_year.set(Calendar.HOUR_OF_DAY, periodicTime_year.get(Calendar.HOUR_OF_DAY));
                    peTrans.setTransactionTime(new Timestamp(newPeriodicTime_year.getTimeInMillis()));

                    dataHelper.setTransactionFromPeriodicTransaction(peTrans);
                    service.putExtra("periodicTransaction", peTrans);
                    context.startService(service);
                }
                break;
        }
    }
}
