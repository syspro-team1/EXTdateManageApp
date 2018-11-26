package com.team1.syspro.expdatemanageapp;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
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
        String title = context.getString(R.string.app_name);
        // intent に付いてきたデータを読む
        String product = intent.getStringExtra("product");
        String exp_date_str = intent.getStringExtra("exp_date");
        int num = intent.getIntExtra("num",-1);
        int requestCode = intent.getIntExtra("requestCode",-1);
        // 通知に表示されるメッセージ
        String message = "date format miss.";
        long when = System.currentTimeMillis();
        try {
            ProductItem item = new ProductItem(product,exp_date_str,num);
            message = item.toString();
        } catch (ParseException e) {
            e.printStackTrace();
        }


        // local push だった時の処理，
        if(intent.getAction().equals("com.team1.syspro.expdatemanageapp.localpush")){
            // notification manager
            NotificationManager manager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
            // notification channel
            NotificationChannel channel = new NotificationChannel(channelId, title, NotificationManager.IMPORTANCE_DEFAULT);
            channel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
            channel.enableVibration(true);
            channel.enableLights(true);
            channel.setLightColor(Color.BLUE);
            // notification
            if(manager != null){
                manager.createNotificationChannel(channel);
                Notification notification = new Notification.Builder(context, channelId)
                        .setContentTitle(title)
                        .setSmallIcon(android.R.drawable.ic_dialog_alert)
                        .setContentText(message)
                        .setAutoCancel(true)
                        .setWhen(when)
                        .build();
                manager.notify(R.string.app_name + requestCode, notification);
            }
        }

        // throw new UnsupportedOperationException("Not yet implemented");
    }
}
