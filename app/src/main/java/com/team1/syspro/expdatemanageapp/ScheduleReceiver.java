package com.team1.syspro.expdatemanageapp;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;

// 端末が再起動された時のブロードキャストを受け取る．
// このときに，賞味期限のスケジュール情報を登録する．
public class ScheduleReceiver extends BroadcastReceiver {
    DatabaseOpenHelper m_helper;
    SQLiteDatabase m_db;
    // プッシュ通知を投げる時のリクエストコード
    private static int requestCode;
    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        Log.d("my-debug", "BOOST COMPLETED broadcast.");
        // request codeの初期化
        requestCode = 0;

        // 食品リストの情報を読み取り，アラームをセット．
        if(m_helper == null){
            m_helper = new DatabaseOpenHelper(context);
        }
        if(m_db == null){
            m_db = m_helper.getReadableDatabase();
        }
        ArrayList<ProductItem> items = readAllData();

        for (ProductItem item:items){
            // 賞味期限のプッシュ通知のインテントを選択
            Intent notify_intent = new Intent(context, ExpDateNotificationReceiver.class);
            // 明示的なブロードキャスト
            notify_intent.setAction("com.team1.syspro.expdatemanageapp.localpush");
            // 商品情報を付随
            notify_intent.putExtra("product", item.getProduct());
            notify_intent.putExtra("exp_date", item.getExp_dateString());
            notify_intent.putExtra("num", item.getNum());
            notify_intent.putExtra("requestCode", requestCode);
            Log.d("my-debug",notify_intent.toString());
            Log.d("my-debug",item.toString());
            // とりあえずテストとして10秒後にプッシュ通知を設定
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.SECOND, 10 + requestCode);
            long when = calendar.getTimeInMillis();
            // intent を指定してpendingIententを作成
            PendingIntent pIntent = PendingIntent.getBroadcast(context, requestCode, notify_intent, 0);
            // AlarmManager をコンテキストより取得
            AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            am.set(AlarmManager.RTC_WAKEUP, when, pIntent);
            requestCode ++;
        }

        // throw new UnsupportedOperationException("Not yet implemented");
    }

    private ArrayList<ProductItem> readAllData(){

        Log.d("my-debug","******Cursor open");

        ArrayList<ProductItem> list = new ArrayList<ProductItem>();
        // cursorを作成(iteratorのようなもの)
        Cursor cursor = m_db.query("listdb", new String[] {"product", "exp_date","num"},
                null,null,null,null,null);
        // なくなるまで読み取り，それをArrayListに格納
        // referenceを見るとmoveToFirstをしなくても自動で最初の行の一つ前にセットされているらしいので問題ない
        while(cursor.moveToNext()){
            String product = cursor.getString(0);
            String exp_date = cursor.getString(1);
            int num = cursor.getInt(2);
            try {
                ProductItem item = new ProductItem(product, exp_date,num);
                list.add(item);
                Log.d("my-debug","******read "+item.toString());
            } catch (ParseException e) {
                e.printStackTrace();
            }
        };
        cursor.close();

        Log.d("my-debug","******Cursor close");

        return list;
    }
}
