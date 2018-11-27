package com.team1.syspro.expdatemanageapp;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;


/* 商品アイテムの管理アクティビティ */
// :TODO Stringとかはstring xmlとかに分離するのが綺麗らしい
// :TODO listdbとかマジックナンバになっているのでよくない気はする
public class ProductManageActivity extends AppCompatActivity
    implements AdapterView.OnItemClickListener {
    private DatabaseOpenHelper m_helper;
    private SQLiteDatabase m_db;
    private static int dammy=0;
    private BaseAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("my-debug","product manager activity onCreate;");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_manager);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        /* helperを作成．dbを作成．*/
        if(m_helper == null){
            m_helper = new DatabaseOpenHelper(getApplicationContext());
        }
        if(m_db == null){
            m_db = m_helper.getWritableDatabase();
        }
        /* database から商品と賞味期限を取得する */
        ArrayList<ProductItem> productList = readAllData();


        /* ListViewのインスタンスを取得し，BaseAdapterをextendしたProductAdapterを設定 */
        ListView listView = findViewById(R.id.listView);
        if (adapter == null) {
            adapter = new ProductAdapter(ProductManageActivity.this, R.layout.list_items,
                    productList);
        }
        listView.setAdapter(adapter);
        // Listenerを設定
        listView.setOnItemClickListener(this);

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
                int ID = insertData(m_db,name,exp_date,1);
                // productAdapterへの追加
                ((ProductAdapter) adapter).add(new ProductItem(ID, name,exp_date,1));
                dammy++;

            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_product_manager, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_sortByExpdate) {
            ((ProductAdapter)adapter).sort_byExp_date();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private ArrayList<ProductItem> readAllData(){
        if(m_helper == null){
            m_helper = new DatabaseOpenHelper(getApplicationContext());
        }
        if(m_db == null){
            m_db = m_helper.getReadableDatabase();
        }
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
                ProductItem item = new ProductItem(_id, product, exp_date,num);
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

    //:TODO そもそもDatabaseOpenHelperがあるのにinsertとかでdbをいじってるのがおかしい
    // databaseへのinsert
    private int insertData(SQLiteDatabase db, String product, Calendar exp_date, int num){
        // calender -> stringへ変更
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd");
        //ContentValues の設定
        ContentValues values = new ContentValues();
        values.put("product",product);
        values.put("exp_date",sdf.format(exp_date.getTime()) );

        // databaseに対応するデータがあるときは，アップデートする
        Cursor cursor = m_db.query("listdb", new String[] {"_id","product", "exp_date","num"},
                "product = ? AND exp_date = ?",
                new String[]{product, sdf.format(exp_date.getTime())},null,null,null);
        while(cursor.moveToNext()){
            values.put("num", num + cursor.getInt(3));
            m_db.update("listdb", values, "product = ? AND exp_date = ?",
                    new String[]{product, sdf.format(exp_date.getTime())});
            Log.d("my-debug","******update "+product+" "+values);
            int _id = cursor.getInt(0);
            cursor.close();
            return _id;
        }
        cursor.close();

        values.put("num",num);
        Log.d("my-debug","******insert "+product+" "+values);
        db.insert("listdb",null,values);
        // なんとなく冗長な気がするけど idを調べるためにまた捜索
        cursor = m_db.query("listdb", new String[] {"_id"},
                "product = ? AND exp_date = ?",
                new String[]{product, sdf.format(exp_date.getTime())},null,null,null);
        int _id = -1;
        while(cursor.moveToNext()){
            _id = cursor.getInt(0);
        }
        cursor.close();
        return _id;

    }

    /* product item がクリックされた時の処理　*/
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //対象のproductItem classを取得
        Log.d("my-debug","ProductAdapter onItemClick()");
        alertCheck((ProductItem)parent.getAdapter().getItem(position), position);
    }
    /* ダイアログを表示する */
    private void alertCheck(ProductItem target , int position){
        final int pos=position;
        // 名前を表示している部分か，賞味期限をだしている部分かで処理を変える
        if (target.getNum() == -1){


        }else {
            final ProductItem item = target;
            String[] alert_menu = {"削除", "cancel"};
            AlertDialog.Builder alert = new AlertDialog.Builder(ProductManageActivity.this);
            alert.setTitle(target.toString());
            alert.setItems(alert_menu, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int idx) {
                    // リストアイテムを選択したときの処理
                    // 削除
                    if (idx == 0) {
                        deleteItem(m_db, item, pos);
                    }
                    // cancel
                    else {
                        Log.d("my-debug", "ProductItem Dialog cancel");
                    }
                }
            });
            alert.show();
        }
    }
    /* itemを削除する */
    private void deleteItem(SQLiteDatabase db, ProductItem item, int position){
        ((ProductAdapter) adapter).remove(position);

        int a = db.delete("listdb", "product = ? AND exp_date = ?",new String[]{item.getProduct(),item.getExp_dateString()});

        Log.d("my-debug","******delete "+item.toString());
    }
}
