package com.team1.syspro.expdatemanageapp;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

// 商品の名前，賞味期限を持つクラス
public class productItem {
    private String product;
    private Calendar exp_date;
    private SimpleDateFormat sdf;

    productItem(String product,Calendar exp_date){
        sdf = new SimpleDateFormat("yyyy.MM.dd");
        this.product = product;
        this.exp_date = exp_date;
    }
    productItem(String product,String exp_date) throws ParseException {
        sdf = new SimpleDateFormat("yyyy.MM.dd");
        Date d = sdf.parse(exp_date);
        this.product = product;
        this.exp_date = Calendar.getInstance();
        this.exp_date.setTime(d);
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
}
