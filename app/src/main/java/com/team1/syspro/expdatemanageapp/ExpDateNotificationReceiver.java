package com.team1.syspro.expdatemanageapp;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;


// 賞味期限の情報を通知するためのIntent
public class ExpDateNotificationReceiver extends BroadcastReceiver {
    private String channelId = "default";
    private SimpleDateFormat sdf;

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        Log.d("my-debug","expdate notification recieved.");
        // intent に付いてきたデータを読む
        int ID = intent.getIntExtra("ID", -1);
        String product = intent.getStringExtra("product");
        String exp_date_str = intent.getStringExtra("exp_date");
        int num = intent.getIntExtra("num",-1);
        int requestCode = intent.getIntExtra("requestCode",-1);
        int day = intent.getIntExtra("before_day",-1);
        // 通知に表示されるメッセージ
        String title = context.getString(R.string.app_name);
        String message = "date format miss.";
        long when = System.currentTimeMillis();
        try {
            // IDには存在しない値を突っ込んでいる
            ProductItem item = new ProductItem(ID,product,exp_date_str,num);
            title = "賞味期限" + String.valueOf(day) +"日前です";
            message =  item.getProduct() + ":" + String.valueOf(item.getNum()) + "個";
        } catch (ParseException e) {
            e.printStackTrace();
        }


        // local push だった時の処理，
        if(intent.getAction().equals("com.team1.syspro.expdatemanageapp.localpush")){
            // API >=26 の時はnotification channelを設定
            if (Build.VERSION.SDK_INT >= 26) {
                // notification manager
                NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                // notification channel
                NotificationChannel channel = new NotificationChannel(channelId, title, NotificationManager.IMPORTANCE_DEFAULT);
                channel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
                channel.enableVibration(true);
                channel.enableLights(true);
                channel.setLightColor(Color.BLUE);
                // notification
                if (manager != null) {
                    manager.createNotificationChannel(channel);
                    Notification notification = new NotificationCompat.Builder(context, channelId)
                            .setContentTitle(title)
                            .setSmallIcon(android.R.drawable.ic_dialog_alert)
                            .setContentText(message)
                            .setAutoCancel(true)
                            .setWhen(when)
                            .build();
                    manager.notify(R.string.app_name + requestCode, notification);
                }
            }else{
                NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                // notification
                if (manager != null) {
                    Notification notification = new NotificationCompat.Builder(context, channelId)
                            .setContentTitle(title)
                            .setSmallIcon(android.R.drawable.ic_dialog_alert)
                            .setContentText(message)
                            .setAutoCancel(true)
                            .setWhen(when)
                            .build();
                    manager.notify(R.string.app_name + requestCode, notification);
                }
            }
        }

        // throw new UnsupportedOperationException("Not yet implemented");
    }
}
