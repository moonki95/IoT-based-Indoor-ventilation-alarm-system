package com.example.arduino;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class ConnApiPmData {
    public static String request(String _url) throws UnsupportedEncodingException {
        HttpURLConnection conn = null;
        BufferedReader rd = null;
        StringBuilder sb = null;
        try {
            Log.v("air", "try...");  //이건뭐지
//            StringBuilder urlBuilder = new StringBuilder("http://openapi.airkorea.or.kr/openapi/services/rest/ArpltnInforInqireSvc/getCtprvnMesureSidoLIst");
//            urlBuilder.append("?" + URLEncoder.encode("ServiceKey", "UTF-8") + "=8%2B64ddNqAnHmK8Lx8tI%2Bngxj6dtexb%2FhgwcSwsDjWb%2FbWSgRHUmNk9%2BtkAlqPCFcVb1QiMx49DQfXdMPHnNkzg%3D%3D");
//            urlBuilder.append("&" + URLEncoder.encode("numOfRows", "UTF-8") + "=" + URLEncoder.encode("10", "UTF-8")); //한 페이지에 10개의 결과
//            urlBuilder.append("&" + URLEncoder.encode("pageNo", "UTF-8") + "=" + URLEncoder.encode("1", "UTF-8")); //페이지번호
//            urlBuilder.append("&" + URLEncoder.encode("sidoName", "UTF-8") + "=" + URLEncoder.encode("충북", "UTF-8")); //시도 이름
//            urlBuilder.append("&" + URLEncoder.encode("searchCondition", "UTF-8") + "=" + URLEncoder.encode("DAILY", "UTF-8")); //요청데이터 기간
//
//            URL url = new URL(urlBuilder.toString());
            URL url=new URL(_url);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Content-type", "application/json");
            Log.v("ApiPmData", "Response code:" + conn.getResponseCode());
            if (conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
                Log.v("air", "if>>>");
                rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            } else {
                rd = new BufferedReader((new InputStreamReader(conn.getErrorStream())));
            }
            sb = new StringBuilder();
            String line;
            while ((line = rd.readLine()) != null) {
                sb.append((line));
            }
        } catch (IOException e) {
            Log.v("air", "catch...");
            e.printStackTrace();
        } finally {
            Log.v("air", "finally...");
            try {
                rd.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            conn.disconnect();
        }
        return sb.toString();
    }

}
