package com.qhylc.android.bms;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by qhylc on {2016/11/21.}
 */

public class HttpUtil {
    public static void sendHttpRequest(final String address,final String jsonStr,
                                       final HttpCallbackListener listener) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection = null;
                try{
                    URL url = new URL(address);
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("POST");
                    connection.setConnectTimeout(8000);
                    connection.setReadTimeout(8000);
                    connection.setDoInput(true);
                    connection.setDoOutput(true);
                    connection.setRequestProperty("Content-Type","application/json;charset=UTF-8");
                    OutputStream os = connection.getOutputStream();
                    os.write(jsonStr.getBytes());
                    os.flush();
                    if (connection.getResponseCode() == 200){
                        InputStream in = connection.getInputStream();
                        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                        StringBuilder response = new StringBuilder();
                        String line;
                        while ((line = reader.readLine()) != null){
                            response.append(line);
                        }
                        if (listener != null) {
                            //回调onFinish()方法
                            listener.onFinish(response.toString());
                        }
                    }
                } catch (Exception e) {
                    if(listener != null) {
                        //回调onError()方法
                        listener.onError(e);
                    }
                } finally {
                    if (connection != null)
                        connection.disconnect();
                }
            }
        }).start();
    }

    interface HttpCallbackListener {
        void onFinish(String response);
        void onError(Exception e);
    }
}
