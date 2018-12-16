package com.team1.syspro.expdatemanageapp;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

// 商品の名前，賞味期限を持つクラス
public class ProductItem {
    private String product;
    private Calendar exp_date;
    private int num;
    private int ID;
    private SimpleDateFormat sdf;

    ProductItem(int ID, String product, Calendar exp_date, int num){
        sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm");
        this.ID = ID;
        this.product = product;
        this.exp_date = exp_date;
        this.num = num;
    }
    ProductItem(int ID,String product, String exp_date, int num) throws ParseException {
        this.ID = ID;
        sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm");
        Date d = sdf.parse(exp_date);
        this.product = product;
        this.exp_date = Calendar.getInstance();
        this.exp_date.setTime(d);
        this.num = num;
    }

    // 商品名と賞味期限が一致していればequalsとする．(addの際)
    @Override
    public boolean equals(Object obj) {
        boolean equal_year = ((ProductItem)obj).getExp_date().get(Calendar.YEAR) == this.getExp_date().get(Calendar.YEAR);
        boolean equal_month = ((ProductItem)obj).getExp_date().get(Calendar.MONTH) == this.getExp_date().get(Calendar.MONTH);
        boolean equal_day = ((ProductItem)obj).getExp_date().get(Calendar.DATE) == this.getExp_date().get(Calendar.DATE);

        return ( equal_day && equal_month && equal_year) &&
                ( ((ProductItem)obj).getProduct().equals(this.getProduct()) );
    }

    public String getExp_dateString(){
        return sdf.format(this.exp_date.getTime());
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public Calendar getExp_date() {
        return exp_date;
    }

    public void setExp_date(Calendar exp_date) {
        this.exp_date = exp_date;
    }

    public int getNum() { return num; }

    public void setNum(int num) { this.num = num; }

    @Override
    public String toString() {
        String str = "[" + getID() + ":" + getProduct() + ", " + getExp_dateString() + ", " + getNum() + "]";
        return str;
    }

    public int getID() {
        return ID;
    }
}
