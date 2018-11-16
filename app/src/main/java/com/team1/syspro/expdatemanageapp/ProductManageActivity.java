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
        Log.d("my-tag",findViewById(R.id.toolbar).toString());
        Log.d("my-debug",toolbar.toString()+" ");
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
        ArrayList<productItem> productList = readAllData();


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
                insertData(m_db,name,exp_date);
                // productAdapterへの追加
                ((ProductAdapter) adapter).add(new productItem(name,exp_date));
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
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
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

    //:TODO そもそもDatabaseOpenHelperがあるのにinsertとかでdbをいじってるのがおかしい
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

    /* product item がクリックされた時の処理　*/
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //対象のproductItem classを取得
        Log.d("my-debug","onItemClick");
        alertCheck((productItem)parent.getAdapter().getItem(position), position);
    }
    /* ダイアログを表示する */
    private void alertCheck(productItem item ,int position){
        final int pos=position;
        final productItem ite = item;
        String[] alert_menu = {"削除", "cancel"};
        AlertDialog.Builder alert = new AlertDialog.Builder(ProductManageActivity.this);
        alert.setTitle(item.getProduct());
        alert.setItems(alert_menu, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int idx) {
                // リストアイテムを選択したときの処理
                // 上に移動
                if (idx == 0) {
                    deleteItem(m_db,ite, pos);
                }
                // cancel
                else {
                    Log.d("my-debug", "ProductItem Dialog cancel");
                }
            }
        });
        alert.show();
    }
    /* itemを削除する */
    private void deleteItem(SQLiteDatabase db, productItem item, int position){
        ((ProductAdapter) adapter).remove(position);

        int a = db.delete("listdb", "product = ? AND exp_date = ?",new String[]{item.getProduct(),item.getExp_dateString()});

        Log.d("my-debug",String.valueOf(a) + " " + item.getProduct()+" "+item.getExp_dateString());
    }
}
