package com.example.moneymanager;

import android.app.Activity;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;


public class PeriodicTransactionService extends IntentService {
    private NotificationManager notificationManager;
    private PendingIntent pendingIntent;
    private static int NOTIFICATION_ID = 1;
    public static String NOTIFICATION_CHANNEL_ID = "my_channel_id";
    Notification notification;

    public PeriodicTransactionService() {
        super("DisplayNotification");
    }

    public PeriodicTransactionService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        PeriodicTransaction resPeriodicTransaction = (PeriodicTransaction) intent.getParcelableExtra("periodicTransaction");
        MoneyToStringConverter converter = new MoneyToStringConverter();
        String title = "";
        String subject = "";
        String content = "";
        if(resPeriodicTransaction.getPeriodicType().equals("day")) {
            title = "Giao dịch định kỳ ngày";
            subject = "Đã tự động thêm giao dịch định kỳ ngày với nội dung:";
            content = "Tên nguồn tiền: " + resPeriodicTransaction.getMoneySourceName() + "\n\t" +
                    "Tên giao dịch: " + resPeriodicTransaction.getExpenditureName() + "\n\tSố tiền: " + converter.moneyToString(resPeriodicTransaction.getTransactionAmount().doubleValue());
        } else if(resPeriodicTransaction.getPeriodicType().equals("month")) {
            title = "Giao dịch định kỳ tháng";
            subject = "Đã tự động thêm giao dịch định kỳ tháng với nội dung:";
            content = "Tên nguồn tiền: " + resPeriodicTransaction.getMoneySourceName() + "\n\t" +
                    "Tên giao dịch: " + resPeriodicTransaction.getExpenditureName() + "\n\tSố tiền: " + converter.moneyToString(resPeriodicTransaction.getTransactionAmount().doubleValue());
        } else if(resPeriodicTransaction.getPeriodicType().equals("year")) {
            title = "Giao dịch định kỳ năm";
            subject = "Đã tự động thêm giao dịch định kỳ năm với nội dung:";
            content = "Tên nguồn tiền: " + resPeriodicTransaction.getMoneySourceName() + "\n\t" +
                    "Tên giao dịch: " + resPeriodicTransaction.getExpenditureName() + "\n\tSố tiền: " + converter.moneyToString(resPeriodicTransaction.getTransactionAmount().doubleValue());
        }

        Context context = this.getApplicationContext();
        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        Intent myIntent = new Intent(this, SplashActivity.class);
        pendingIntent = PendingIntent.getActivity(context, 0, myIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Resources res = this.getResources();
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
        notification= new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.ic_piggy_bank)
                .setLargeIcon(BitmapFactory.decodeResource(res, R.drawable.ic_piggy_bank))
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setContentTitle(title)
                .setContentText(subject)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(content))
                .build();

        notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFICATION_ID, notification);
    }
}
