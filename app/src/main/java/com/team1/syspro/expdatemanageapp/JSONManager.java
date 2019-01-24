package com.team1.syspro.expdatemanageapp;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// HTTP通信に投げるJSON形式をよしなに変換する．
// HTTPに投げる場合に関しては，QRから読み込んだのをそのまま個人情報とマージするので処理がちょっと異なる．
public class JSONManager {
    private JSONObject main;
    private static final int BARCODE_SIZE = 27;
    private static final int COMPANY_SIZE = 7;
    private static final int PRODUCT_SIZE = 5;
    private static final int CHECK_SIZE = 1;
    private static final int PRICE_SIZE = 4;
    private static final int DATE_SIZE = 10;



    public JSONManager(){
        main = new JSONObject();
    }
    public JSONManager(String user,String pass){
        main = new JSONObject();
        try{
            main.put("UserName",user);
            main.put("PassWord",pass);
        }catch (JSONException e){
            Log.d("my-debug","in JSONManager Constractor: ",e);
        }
    }
    public void setUser(String user){
        try {
            main.put("UserName",user);
        } catch (JSONException e) {
            Log.d("my-debug","in JSONManager setUser: ",e);
        }
    }
    public void setPassword(String pass){
        try {
            main.put("PassWord",pass);
        } catch (JSONException e) {

            Log.d("my-debug","in JSONManager setPassword: ",e);
        }
    }
    public void setBuyTime(String buy){
        try {
            main.put("BuyTime",buy);
        } catch (JSONException e) {

            Log.d("my-debug","in JSONManager setBuyTime: ",e);
        }
    }
    //2018/12/1 12:00
    public void setBuyTime(Calendar day) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm");
        String str = sdf.format(day.getTime());
        Log.d("my-debug", str);
        try {
            main.put("BuyTime", str);
        } catch (JSONException e) {
            Log.d("my-debug","in JSONManager setBuyTime: ",e);
        }
    }
    public void setProductList(String QRstr){
        // yyyy-MM-dd HH:mm:ss xxxxxxxxxxxxxxxxxxx...
        // QR情報の文字列をパースしてよしなにする．
        String[] QRstrs = QRstr.split(" ",0);
        //:DEBUG
        for (String str:QRstrs){
            Log.d("my-debug", "parse:" + str);
        }

        String buyTime = QRstrs[0]+" "+QRstrs[1];
        // 送る際の日付フォーマットに変更する．(: -> /, 後ろ3桁を削除)
        buyTime = buyTime.replace('-','/').substring(0,16);
        Log.d("my-debug", "buyTime:" + buyTime);
        setBuyTime(buyTime);

        // 商品バーコードをパースしてなんとかする．(バーコードは27桁)
        JSONArray productlist = new JSONArray();
        Matcher m = Pattern.compile("[\\s\\S]{1,27}").matcher(QRstrs[QRstrs.length-1]);
        while(m.find()){
            String barcode = m.group();
            // バーコードをパースする
            ArrayList<Integer> idx = new ArrayList<>(Arrays.asList(COMPANY_SIZE,PRODUCT_SIZE,CHECK_SIZE,PRICE_SIZE,DATE_SIZE) );
            int tmp=0;
            for (int i=0;i<idx.size();i++){
                tmp+=idx.get(i);
                idx.set(i,tmp);
                Log.d("my-debug","idx :"+idx.get(i));
            }
            idx.add(0,0);
            String[] parse = new String[5];
            for (int i=0;i<idx.size()-1;i++) {
                parse[i] = barcode.substring(idx.get(i), idx.get(i + 1));
                Log.d("my-debug","barcode parse :"+parse[i]);
            }
            // 日付のフォ〜マットに10桁の数字を直す．
            String date = getDayString(parse[4]);
            // JSONObjectの作成
            JSONObject product = new JSONObject();
            try {
                product.put("id",parse[1]);
                product.put("num","1");
                product.put("time",date);
                product.put("price",parse[3]);

            } catch (JSONException e) {
                e.printStackTrace();
            }

            //{'id': '1', 'num': '2', 'time': '2018/12/03 12:00', 'price': '100'};
            try {
                Log.d("my-debug",product.toString(4));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            productlist.put(product);
        }
        try {
            main.put("Production",productlist);
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    private String getDayString(String s) {
        //10文字から日付フォーマットに
        //くそ
        return s.substring(0,4) + '/' + s.substring(4,6) + '/' + s.substring(6,8) + ' ' + s.substring(8,10) + ":00";
    }

    public String toString(){
        return main.toString();
    }
    public String toString(int indentSpaces){
        try {
            return main.toString(indentSpaces);
        } catch (JSONException e) {
            Log.d("my-debug","in JSONManager toString(indentSpace): ",e);
            return null;
        }
    }
}
