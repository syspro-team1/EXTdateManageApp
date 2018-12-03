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


// BaseAdapter を継承したクラス
// :TODO そのうちcomparatorクラスを定義して，sortの実装
public class ProductAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    // layout id
    private int layoutID;
    // productCollector
    private ArrayList<ProductCollector> collectors;
    // productItem のArrayList
    private ArrayList<ProductItem> items;
    // 日付表示のフォーマット
    private SimpleDateFormat format;

    // View holder
    static class ViewHolder{
        TextView product_name;
        TextView exp_date;
        TextView num;
    }


    public ProductAdapter(Context context, int layoutID,
                          ArrayList<ProductItem> productList){
        inflater = LayoutInflater.from(context);
        // ここに置けるレイアウトは一つのアイテム内でのレイアウトを指している
        this.layoutID = layoutID;
        // deep copy
        this.items = (ArrayList<ProductItem>) productList.clone();
        collectors = new ArrayList<ProductCollector>();
        ArrayList<String> products = new ArrayList<String>();
        for (ProductItem item:items){
            if (!products.contains(item.getProduct())) products.add(item.getProduct());
        }
        // 名前ごとに整列してcollectorsに追加
        for (String str:products){
            ProductCollector collector = new ProductCollector(str);
            for(ProductItem item:items){
                collector.addList(item);
            }
            collectors.add(collector);
        }
        for (ProductCollector collector: collectors) Log.d("my-debug", collector.toString());
        // 日付表示のフォーマットを設定
        format = new SimpleDateFormat("yyyy.MM.dd");
    }
    //adapterへのアイテムの追加処理
    public int add(ProductItem item) {
        for (ProductCollector collector:collectors){
            if(collector.isMatch(item)) {
                int res= collector.addList(item);
                notifyDataSetChanged();
                return res;
            }
        }
        // コレクターに一致するものがないので新しい商品
        ProductCollector collector = new ProductCollector(item.getProduct());
        int res = collector.addList(item);
        collectors.add(collector);
        notifyDataSetChanged();
        return res;
    }
    //adapterへのアイテムの削除
    public void remove(int position){
        ProductItem target = (ProductItem) getItem(position);
            for(ProductCollector collector:collectors){
                if (collector.isMatch(target)){
                    // 親である場合
                    if(target.getNum() == -1) {
                        collectors.remove(collector);
                        break;
                    }else {
                        collector.erase(target);
                        //　アイテムを消した結果商品が空になったらコレクタを削除
                        if (collector.isEmpty()) collectors.remove(collector);
                        break;
                    }
                }
            }
        notifyDataSetChanged();
    }
    //adapterの賞味期限順にソート
    public void sort_byExp_date(){
        //comparator classを匿名関数で定義，Collectionsでソート
        /*
        Collections.sort(items, new Comparator<ProductItem>() {
            @Override
            public int compare(ProductItem o1, ProductItem o2) {
                return o1.getExp_date().compareTo(o2.getExp_date());
            }
        });
        notifyDataSetChanged();
        */
    }

    @Override
    public int getCount(){
        int sum=0;
        for (ProductCollector collector:collectors){
            sum += collector.getSize() + 1; //親の分
        }
        return sum;
    }
    @Override
    public Object getItem(int position){
        int i=0;
        while(position > collectors.get(i).getSize()){
            position -= collectors.get(i).getSize()+1;
            i++;
        }
        return (position == 0) ? collectors.get(i).getParent() : collectors.get(i).getItem(position-1);
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
            holder.num = convertView.findViewById(R.id.num);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder)convertView.getTag();
        }
        //実際にフォルダーにテキストを設定
        ProductItem target = (ProductItem) getItem(position);
        //現在時刻を取得
        Calendar now = Calendar.getInstance();
        // 親である場合
        if (target.getNum() == -1){
            holder.product_name.setText(target.getProduct());
            holder.exp_date.setText("");
            holder.num.setText("");
        }else {
            holder.product_name.setText(" ");
            holder.exp_date.setText(format.format(target.getExp_date().getTime()));
            long diff = target.getExp_date().getTimeInMillis() + 1000 - now.getTimeInMillis();
            long hour = diff/1000/60/60;
            // 1日前
            if ( hour <= 24){
                holder.exp_date.setTextColor(convertView.getResources().getColor(R.color.red));
            }else if(hour <= 24*3){
                holder.exp_date.setTextColor(convertView.getResources().getColor(R.color.coral));
            }else if(hour <= 24*5){
                holder.exp_date.setTextColor(convertView.getResources().getColor(R.color.yellow));
            }else{
                holder.exp_date.setTextColor(convertView.getResources().getColor(R.color.dimgray));
            }
            holder.num.setText(String.valueOf(target.getNum()) );
        }
        return convertView;
    }

}
