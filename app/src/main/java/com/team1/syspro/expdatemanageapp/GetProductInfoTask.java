package com.team1.syspro.expdatemanageapp;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class GetProductInfoTask extends AsyncTask<String, Void, String> {
    private Listener listener;

    @Override
    protected String doInBackground(String... params) {
        //googledriveのURL
        String urlSt = "https://script.google.com/macros/s/AKfycbwCLfqrz1-wdG1klm9kpVKwkxttRIFFixD3odklyKaZPm0PuUc/exec";
        // https://script.google.com/macros/s/AKfycbz5zQf6ZQn90AumRJDRkrE41r2EpeLm5yagPRQuh6RNj6KyA9BE/exec?UserName=testerA&PassWard=12345&BuyTime=2018%2F12%2F1+12%3A00&Production=%5B%7B%27id%27%3A+%271%27%2C+%27num%27%3A+%272%27%2C+%27time%27%3A+%272018%2F12%2F3+12%3A00%27%2C+%27price%27%3A+%27100%27%7D%2C+%7B%27id%27%3A+%272%27%2C+%27num%27%3A+%272%27%2C+%27time%27%3A+%272018%2F12%2F2+12%3A00%27%2C+%27price%27%3A+%27150%27%7D%5D
        HttpURLConnection httpConn = null;
        String result=null;
        // ここにデータを書く
        String sendData = "{\"UserName\": \"testerA\", \"PassWard\": \"12345\", \"BuyTime\": \"2018/12/1 12:00\", \"Production\": [{\"id\": \"1\", \"num\": \"2\", \"time\": \"2018/12/3 12:00\", \"price\": \"100\"}, {\"id\": \"2\", \"num\": \"2\", \"time\": \"2018/12/2 12:00\", \"price\": \"150\"}]}";

        try{
            URL url = new URL(urlSt);
            //HTTP connection
            httpConn = (HttpURLConnection)url.openConnection();
            // request POST
            httpConn.setRequestMethod("POST");
            // no Redirects
            httpConn.setInstanceFollowRedirects(false);
            // データを書き込む
            httpConn.setDoOutput(true);
            httpConn.setDoInput(true);
            httpConn.setRequestProperty("Content-Type", "application/json; charset=utf-8");
            // 時間制限
            httpConn.setReadTimeout(10000);
            httpConn.setConnectTimeout(20000);
            // 接続
            httpConn.connect();
            // POSTデータ送信処理
            OutputStream outStream = null;
            try {
                outStream = httpConn.getOutputStream();
                outStream.write( sendData.getBytes("UTF-8"));
                outStream.flush();
                Log.d("my-debug","flush");
            } catch (IOException e) {
                // POST送信エラー
                e.printStackTrace();
                result="POST送信エラー";
            } finally {
                if (outStream != null) {
                    outStream.close();
                }
            }
            // requestcode を表示
            final int status = httpConn.getResponseCode();
            Log.d("my-debug","request code = "+ String.valueOf(status));

            BufferedReader in = new BufferedReader(new InputStreamReader(httpConn.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result += "\n" + line;
            }




        }catch (IOException e){
            e.printStackTrace();
        }finally {
            if(httpConn != null){
                httpConn.disconnect();
            }
        }

        return result;
    }

    // 非同期処理が終了後、結果をメインスレッドに返す
    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);

        if (listener != null) {
            listener.onSuccess(result);
        }
    }

    void setListener(Listener listener) {
        this.listener = listener;
    }

    interface Listener {
        void onSuccess(String result);
    }
}
