package com.team1.syspro.expdatemanageapp;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;


/* 商品アイテムの管理アクティビティ */
// :TODO Stringとかはstring xmlとかに分離するのが綺麗らしい
// :TODO listdbとかマジックナンバになっているのでよくない気はする
public class ProductManageActivity extends AppCompatActivity {
    private DatabaseOpenHelper m_helper;
    private SQLiteDatabase m_db;
    private static int dammy=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("my-debug","product manager activity onCreate;");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_manager);

        /* helperを作成．dbを作成．*/
        if(m_helper == null){
            m_helper = new DatabaseOpenHelper(getApplicationContext());
        }
        if(m_db == null){
            m_db = m_helper.getWritableDatabase();
        }
        /* database から商品と賞味期限を取得する */
        ArrayList<productItem> productList = readAllData();


        /* ListViewのインスタンスを取得し，BaseAdapterをextendしたProductAdapterを設定 */
        ListView listView = findViewById(R.id.listView);
        final BaseAdapter adapter = new ProductAdapter(this.getApplicationContext(), R.layout.list_items,
                productList);
        listView.setAdapter(adapter);

        dammy=0;
        Button addButton = findViewById(R.id.addButton);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //:TODO とりあえずダミーのproduct Itemを追加する．
                String name = "nantoka" + String.valueOf(dammy);
                Calendar exp_date = Calendar.getInstance();
                exp_date.add(Calendar.DATE,dammy);
                //database への追加
                insertData(m_db,name,exp_date);
                // productAdapterへの追加
                ((ProductAdapter) adapter).add(new productItem(name,exp_date));
                dammy++;

            }
        });
    }

    private ArrayList<productItem> readAllData(){
        if(m_helper == null){
            m_helper = new DatabaseOpenHelper(getApplicationContext());
        }
        if(m_db == null){
            m_db = m_helper.getReadableDatabase();
        }
        Log.d("my-debug","******Cursor");

        ArrayList<productItem> list = new ArrayList<productItem>();
        // cursorを作成(iteratorのようなもの)
        Cursor cursor = m_db.query("listdb", new String[] {"product", "exp_date"},
                            null,null,null,null,null);
        // なくなるまで読み取り，それをArrayListに格納
        // referenceを見るとmoveToFirstをしなくても自動で最初の行の一つ前にセットされているらしいので問題ない
        while(cursor.moveToNext()){
            String product = cursor.getString(0);
            String exp_date = cursor.getString(1);
            try {
                productItem item = new productItem(product, exp_date);
                list.add(item);
                Log.d("my-debug","******"+product+" "+exp_date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        };
        cursor.close();

        return list;
    }

    // databaseへのinsert
    private void insertData(SQLiteDatabase db, String product, Calendar exp_date){
        ContentValues values = new ContentValues();
        values.put("product",product);
        // calender -> stringへ変更
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd");
        values.put("exp_date",sdf.format(exp_date.getTime()) );
        Log.d("my-debug","******"+product+" "+values+" insert");
        db.insert("listdb",null,values);
    }
}
