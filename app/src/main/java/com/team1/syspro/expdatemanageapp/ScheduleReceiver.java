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
    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        Log.d("my-debug", "BOOST COMPLETED broadcast.");

        // 食品リストの情報を読み取り，アラームをセット．
        if(m_helper == null){
            m_helper = new DatabaseOpenHelper(context);
        }
        if(m_db == null){
            m_db = m_helper.getReadableDatabase();
        }
        ArrayList<ProductItem> items = readAllData();

        //商品名，賞味期限ごとに固有のIDが割り振られているのでこれをリクエストコードにすればよい．

        for (ProductItem item:items){
            int requestCode = item.getID();
            // 賞味期限のプッシュ通知のインテントを選択
            Intent notify_intent = new Intent(context, ExpDateNotificationReceiver.class);
            // 明示的なブロードキャスト
            notify_intent.setAction("com.team1.syspro.expdatemanageapp.localpush");
            // 商品情報を付随
            notify_intent.putExtra("ID",item.getID());
            notify_intent.putExtra("product", item.getProduct());
            notify_intent.putExtra("exp_date", item.getExp_dateString());
            notify_intent.putExtra("num", item.getNum());
            Log.d("my-debug",notify_intent.toString());
            // 賞味期限の1日前と3日前に通知を発信
            Calendar calendar = item.getExp_date();
            Log.d("my-debug",calendar.toString());
            calendar.add(Calendar.DAY_OF_MONTH, -1);
            long when1day = calendar.getTimeInMillis();
            calendar.add(Calendar.DAY_OF_MONTH, -2);
            long when3day = calendar.getTimeInMillis();
            // とりあえずテストとして10秒後にプッシュ通知を設定
            //Calendar calendar = Calendar.getInstance();
            //calendar.add(Calendar.SECOND, 10 + requestCode);
            //long when = calendar.getTimeInMillis();
            // intent を指定してpendingIententを作成
            // requestCodeは　item.getID()*10 + 1 or item.getID()*10 + 3 ということで
            notify_intent.putExtra("before_day",1);
            notify_intent.putExtra("requestCode", requestCode*10+1);
            PendingIntent pIntent1day = PendingIntent.getBroadcast(context, requestCode*10 + 1, notify_intent, 0);
            notify_intent.putExtra("before_day",3);
            notify_intent.putExtra("requestCode", requestCode*10+3);
            PendingIntent pIntent3day = PendingIntent.getBroadcast(context, requestCode*10 + 3, notify_intent, 0);
            // AlarmManager をコンテキストより取得
            AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            am.set(AlarmManager.RTC_WAKEUP, when1day, pIntent1day);
            am.set(AlarmManager.RTC_WAKEUP, when3day, pIntent3day);
        }

        // throw new UnsupportedOperationException("Not yet implemented");
    }

    private ArrayList<ProductItem> readAllData(){

        Log.d("my-debug","******Cursor open");

        ArrayList<ProductItem> list = new ArrayList<ProductItem>();
        // cursorを作成(iteratorのようなもの)
        Cursor cursor = m_db.query("listdb", new String[] {"_id","product", "exp_date","num"},
                null,null,null,null,null);
        // なくなるまで読み取り，それをArrayListに格納
        // referenceを見るとmoveToFirstをしなくても自動で最初の行の一つ前にセットされているらしいので問題ない
        while(cursor.moveToNext()){
            int _id = cursor.getInt(0);
            String product = cursor.getString(1);
            String exp_date = cursor.getString(2);
            int num = cursor.getInt(3);
            try {
                ProductItem item = new ProductItem(_id,product, exp_date,num);
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
