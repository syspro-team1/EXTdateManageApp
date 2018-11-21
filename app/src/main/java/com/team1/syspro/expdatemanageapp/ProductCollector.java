package com.team1.syspro.expdatemanageapp;


import java.util.ArrayList;
import java.util.Calendar;

// ProductAdapterが内部に持っている． 同じproductNameのproductItemを持つ．
public class ProductCollector {
    private ArrayList<ProductItem> items;
    private String product;
    // getItemの関係で架空のアイテムを持っておく必要がある．
    private ProductItem img;

    ProductCollector(String product){
        this.product = product;
        this.items = new ArrayList<ProductItem>();
        this.img = new ProductItem(product, Calendar.getInstance(),-1);
    }

    public boolean addList(ProductItem item){
        if(!isMatch(item)) return false;
        int pos = isContain(item);
        if (pos != -1){
            items.get(pos).setNum( items.get(pos).getNum() + item.getNum() );
            return true;
        }
        return items.add(item);
    }
    // itemを含んでいるかどうか含んでいたら場所を返す
    public int isContain(ProductItem item){
        for(int i=0;i < items.size(); i++){
            if (items.get(i).equals(item)) return i;
        }
        return -1;
    }
    // itemが適合するクラスか
    public boolean isMatch(ProductItem item){
        return item.getProduct().equals(this.product);
    }
    // itemが空か？
    public boolean isEmpty(){
        return items.isEmpty();
    }

    public void erase(ProductItem item){
        if(!isMatch(item)) return;
        int pos = isContain(item);
        if (pos == -1) return;
        items.remove(pos);
    }
    public int getSize(){
        return items.size();
    }
    public ProductItem getItem(int pos){
        return items.get(pos);
    }
    public ProductItem getParent(){
        return img;
    }

    @Override
    public String toString() {
        return  items.toString();
    }
}
