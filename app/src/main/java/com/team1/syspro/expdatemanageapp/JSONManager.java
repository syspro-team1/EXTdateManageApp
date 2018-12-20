package com.team1.syspro.expdatemanageapp;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;

// HTTP通信に投げるJSON形式をよしなに変換する．
// HTTPに投げる場合に関しては，QRから読み込んだのをそのまま個人情報とマージするので処理がちょっと異なる．
public class JSONManager {
    private JSONObject main;

    public JSONManager(){
        main = new JSONObject();
    }
    public JSONManager(String user,String pass){
        main = new JSONObject();
        try{
            main.put("UserName",user);
            main.put("PassWord",pass);
        }catch (JSONException e){
            e.printStackTrace();
        }
    }
    public void setUser(String user){
        try {
            main.put("UserName",user);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public void setPassword(String pass){
        try {
            main.put("PassWord",pass);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public void setBuyTime(String buy){
        try {
            main.put("BuyTime",buy);
        } catch (JSONException e) {
            e.printStackTrace();
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
            e.printStackTrace();
        }
    }
    public void setProductList(String QRstr){
        //{'id': '1', 'num': '2', 'time': '2018/12/3 12:00', 'price': '100'};
        try {
            JSONObject obj = new JSONObject(QRstr);
            JSONArray productlist = obj.getJSONArray("Production");
            setBuyTime(obj.getString("BuyTime") );

            main.put("Production", productlist);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String toString(){
        return main.toString();
    }
    public String toString(int indentSpaces){
        try {
            return main.toString(indentSpaces);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
}
