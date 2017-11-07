package com.example.httpsrequest_master.http;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;

/**
 * Created by 亮亮 on 2017/11/7.
 *///不负责任的请求类

public class HttpUtilsUnSafe {
    private static HttpUtilsUnSafe httpUtils;

    public HttpUtilsUnSafe() {
    }

    public static  HttpUtilsUnSafe getInstance() {
        if(httpUtils==null){
            new HttpUtilsUnSafe();
        }

        return httpUtils;
    }
    public interface OnRequestCallBack{
        void onSuccess(String s);
        void onFail(Exception e);
    }
    public void get(final String path, final OnRequestCallBack callBack){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url=new URL(path);
                    //1.改成s
                    HttpsURLConnection conn= (HttpsURLConnection) url.openConnection();
                    //2.SSLContext初始化
                    //TLS是SSl的第二版加密方式
                    SSLContext tls = SSLContext.getInstance("TLS");
                    TrustManager[] trustManagers={};
                    tls.init(null,trustManagers,new SecureRandom());
                    //3.ssl工厂
                    SSLSocketFactory factory = tls.getSocketFactory();
                    conn.setSSLSocketFactory(factory);
                    conn.setRequestMethod("GET");
                    conn.setReadTimeout(5000);
                    conn.setConnectTimeout(5000);
                    conn.setDoInput(true);
                    conn.setDoOutput(true);
                    conn.connect();

                    InputStream inputStream = conn.getInputStream();
                    StringBuilder sb=new StringBuilder();
                    int flag;
                    byte[] by=new byte[1024];
                    while((flag=inputStream.read(by))!=-1){

                        sb.append(new String(by,0,flag));
                    }
                    String s=sb.toString();
                    //调用对方传入callback完成回调操作
                    callBack.onSuccess(s);



                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                } catch (KeyManagementException e) {
                    e.printStackTrace();
                    callBack.onFail(e);
                }
            }
        }).start();


    }
}
