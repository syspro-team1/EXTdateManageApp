package com.team1.syspro.expdatemanageapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.BaseAdapter;
import android.widget.ListView;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

// :TODO どうやら日付の管理はJava8 ではLocalDateTimeだがAPI 26が要求される
// :TODO Stringとかはstring xmlとかに分離するのが綺麗らしい
public class ProductManageActivity extends AppCompatActivity {
    private static final ArrayList<String> names = new ArrayList<String>(Arrays.asList(
            "大根",
            "ごぼう",
            "にんじん",
            "ねぎ",
            "こんにゃく",
            "じゃがいも",
            "りんご",
            "豆腐",
            "味噌",
            "醤油"
    ));
    private static final ArrayList<Calendar> ext_dates = new ArrayList<Calendar>(Arrays.asList(
            (Calendar)(new GregorianCalendar(2018,12-1,1,12,0)),
            (Calendar)(new GregorianCalendar(2018,12-1,1,12,0)),
            (Calendar)(new GregorianCalendar(2018,12-1,1,12,0)),
            (Calendar)(new GregorianCalendar(2018,12-1,1,12,0)),
            (Calendar)(new GregorianCalendar(2018,12-1,1,12,0)),
            (Calendar)(new GregorianCalendar(2018,12-1,1,12,0)),
            (Calendar)(new GregorianCalendar(2018,12-1,1,12,0)),
            (Calendar)(new GregorianCalendar(2018,12-1,1,12,0)),
            (Calendar)(new GregorianCalendar(2018,12-1,1,12,0)),
            (Calendar)(new GregorianCalendar(2018,12-1,1,12,0))
    ));
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("my-debug","product manager activity onCreate;");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_manager);

        /* ListViewのインスタンスを取得し，BaseAdapterをextendしたProductAdapterを設定 */
        ListView listView = findViewById(R.id.listView);
        BaseAdapter adapter = new ProductAdapter(this.getApplicationContext(), R.layout.list_items,
                names, ext_dates);
        listView.setAdapter(adapter);
    }
}
