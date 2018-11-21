package com.team1.syspro.expdatemanageapp;


import java.util.ArrayList;
import java.util.Calendar;

// ProductAdapterが内部に持っている． 同じproductNameのproductItemを持つ．
public class ProductCollector {
    private ArrayList<productItem> items;
    private String product;
    // getItemの関係で架空のアイテムを持っておく必要がある．
    private productItem img;

    ProductCollector(String product){
        this.product = product;
        this.items = new ArrayList<productItem>();
        this.img = new productItem(product, Calendar.getInstance(),-1);
    }

    public boolean addList(productItem item){
        if(!IsMatch(item)) return false;
        int pos = IsContain(item);
        if (pos != -1){
            items.get(pos).setNum( items.get(pos).getNum() + item.getNum() );
            return true;
        }
        return items.add(item);
    }
    // itemを含んでいるかどうか含んでいたら場所を返す
    public int IsContain(productItem item){
        for(int i=0;i < items.size(); i++){
            if (items.get(i).equals(item)) return i;
        }
        return -1;
    }
    // itemが適合するクラスか
    public boolean IsMatch(productItem item){
        return item.getProduct().equals(this.product);
    }
    // itemが空か？
    public boolean isEmpty(){
        return items.isEmpty();
    }

    public void erase(productItem item){
        if(!IsMatch(item)) return;
        int pos = IsContain(item);
        if (pos == -1) return;
        items.remove(pos);
    }
    public int getSize(){
        return items.size();
    }
    public productItem getItem(int pos){
        return items.get(pos);
    }
    public productItem getParent(){
        return img;
    }

    @Override
    public String toString() {
        return  items.toString();
    }
}
