package com.example.moneymanager;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.BitmapFactory;

import androidx.core.app.NotificationCompat;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.security.Provider;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

import static android.content.Context.MODE_PRIVATE;
import static android.content.Context.NOTIFICATION_SERVICE;

public class NewDayReceiver extends BroadcastReceiver {
    private Context context;
    private ArrayList<PeriodicTransaction> periodicTrasactionListFull;
    private DataHelper dataHelper;

    private NotificationManager notificationManager;
    private PendingIntent pendingIntent;
    private static int NOTIFICATION_ID = 1;
    public static String NOTIFICATION_CHANNEL_ID = "my_channel_id";
    Notification notification;

    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;
        dataHelper = new DataHelper();
        loadData();

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
                newPeriodicTime_day.set(Calendar.MINUTE, periodicTime_day.get(Calendar.MINUTE));
                peTrans.setTransactionTime(new Timestamp(newPeriodicTime_day.getTimeInMillis()));

                dataHelper.setTransactionFromPeriodicTransaction(peTrans);
                updateMoneySourceAndPushNotification(peTrans);
                break;
            case "month":
                Calendar periodicTime_month = Calendar.getInstance();
                periodicTime_month.setTimeInMillis(peTrans.getTransactionTime().getTime());

                if(periodicTime_month.get(Calendar.DAY_OF_MONTH) == now_day) {
                    Calendar newPeriodicTime_month = Calendar.getInstance();
                    newPeriodicTime_month.set(Calendar.HOUR_OF_DAY, periodicTime_month.get(Calendar.HOUR_OF_DAY));
                    newPeriodicTime_month.set(Calendar.MINUTE, periodicTime_month.get(Calendar.MINUTE));
                    peTrans.setTransactionTime(new Timestamp(newPeriodicTime_month.getTimeInMillis()));

                    dataHelper.setTransactionFromPeriodicTransaction(peTrans);
                    updateMoneySourceAndPushNotification(peTrans);
                }
                break;
            case "year":
                Calendar periodicTime_year = Calendar.getInstance();
                periodicTime_year.setTimeInMillis(peTrans.getTransactionTime().getTime());

                if(periodicTime_year.get(Calendar.DAY_OF_MONTH) == now_day && periodicTime_year.get(Calendar.MONTH) == now_month) {
                    Calendar newPeriodicTime_year = Calendar.getInstance();
                    newPeriodicTime_year.set(Calendar.HOUR_OF_DAY, periodicTime_year.get(Calendar.HOUR_OF_DAY));
                    newPeriodicTime_year.set(Calendar.MINUTE, periodicTime_year.get(Calendar.MINUTE));
                    peTrans.setTransactionTime(new Timestamp(newPeriodicTime_year.getTimeInMillis()));

                    dataHelper.setTransactionFromPeriodicTransaction(peTrans);
                    updateMoneySourceAndPushNotification(peTrans);
                }
                break;
        }
    }

    public void updateMoneySourceAndPushNotification(final PeriodicTransaction periodicTransaction) {
        dataHelper.getMoneySourceById(new SingleMoneySourceCallBack() {
            @Override
            public void onCallBack(MoneySource moneySource) {
                if(periodicTransaction.getTransactionIsIncome()) {
                    moneySource.setAmount(moneySource.getAmount().doubleValue() + periodicTransaction.getTransactionAmount().doubleValue());
                } else {
                    moneySource.setAmount(moneySource.getAmount().doubleValue() - periodicTransaction.getTransactionAmount().doubleValue());
                }

                dataHelper.updateMoneySource(moneySource);
                notificationBuil(periodicTransaction);
            }

            @Override
            public void onCallBackFailed(String msg) {

            }
        }, periodicTransaction.getMoneySourceId());
    }

    public void notificationBuil(PeriodicTransaction resPeriodicTransaction) {
        MoneyToStringConverter converter = new MoneyToStringConverter();
        String title = "";
        String subject = "";
        String content = "";
        if(resPeriodicTransaction.getPeriodicType().equals("day")) {
            title = "Giao dịch định kỳ ngày";
            subject = "Đã tự động thêm giao dịch định kỳ ngày với nội dung:";
            content = "Tên giao dịch: " + resPeriodicTransaction.getExpenditureName() + "\n\tSố tiền: " + converter.moneyToString(resPeriodicTransaction.getTransactionAmount().doubleValue());
        } else if(resPeriodicTransaction.getPeriodicType().equals("month")) {
            title = "Giao dịch định kỳ tháng";
            subject = "Đã tự động thêm giao dịch định kỳ tháng với nội dung:";
            content = "Tên giao dịch: " + resPeriodicTransaction.getExpenditureName() + "\n\tSố tiền: " + converter.moneyToString(resPeriodicTransaction.getTransactionAmount().doubleValue());
        } else if(resPeriodicTransaction.getPeriodicType().equals("year")) {
            title = "Giao dịch định kỳ năm";
            subject = "Đã tự động thêm giao dịch định kỳ năm với nội dung:";
            content = "Tên giao dịch: " + resPeriodicTransaction.getExpenditureName() + "\n\tSố tiền: " + converter.moneyToString(resPeriodicTransaction.getTransactionAmount().doubleValue());
        }

        notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);

        Intent myIntent = new Intent(context, SplashActivity.class);
        pendingIntent = PendingIntent.getActivity(context, 0, myIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        notification = new NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.ic_piggy_bank)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setContentTitle(title)
                .setContentText(subject)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(content))
                .build();

        notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        int m = (int) ((new Date().getTime() / 1000L) % Integer.MAX_VALUE);
        Random random = new Random();
        m += random.nextInt(100) + 1;
        notificationManager.notify(m, notification);
    }
}
