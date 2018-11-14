package com.team1.syspro.expdatemanageapp;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.lang.Object;

import static android.content.ContentValues.TAG;


// BaseAdapter を継承したクラス
// :TODO そのうちcomparatorクラスを定義して，sortの実装
public class ProductAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    // layout id
    private int layoutID;
    // productItem のArrayList
    private ArrayList<productItem> items;
    // 日付表示のフォーマット
    private SimpleDateFormat format;

    // View holder
    static class ViewHolder{
        TextView product_name;
        TextView exp_date;
    }


    public ProductAdapter(Context context, int layoutID,
                          ArrayList<productItem> productList){
        inflater = LayoutInflater.from(context);
        // ここに置けるレイアウトは一つのアイテム内でのレイアウトを指している
        this.layoutID = layoutID;
        // deep copy
        this.items = (ArrayList<productItem>) productList.clone();
        // 日付表示のフォーマットを設定
        format = new SimpleDateFormat("yyyy.MM.dd");
    }
    //adapterへのアイテムの追加処理
    public boolean add(productItem item){
        boolean ress = this.items.add(item);
        if (ress){
            //アイテムが正常に追加されればnotifyする．s
            notifyDataSetChanged();
        }
        return ress;
    }

    @Override
    public int getCount(){
        return items.size();
    }
    @Override
    public Object getItem(int position){
        return position;
    }
    @Override
    public long getItemId(int position){
        return position;
    }

    //getView メソッドでOverride
    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        ViewHolder holder;
        // staticなクラスを持っておき，最初以外は再利用
        if(convertView == null){
            convertView = inflater.inflate(layoutID,null);
            holder = new ViewHolder();
            holder.product_name = convertView.findViewById(R.id.product);
            holder.exp_date = convertView.findViewById(R.id.exp_date);
            convertView.setTag(holder);
            Log.d("[my-debug]","position: " + position + ")  " + convertView.hashCode());
        }else{
            holder = (ViewHolder)convertView.getTag();
        }
        //実際にフォルダーにテキストを設定
        holder.product_name.setText(items.get(position).getProduct());
        holder.exp_date.setText(format.format(items.get(position).getExp_date().getTime()));
        return convertView;
    }

}
