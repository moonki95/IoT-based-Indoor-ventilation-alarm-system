package com.example.arduino;


import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.lang.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

import java.net.HttpURLConnection;
import java.net.URL;



public class MainActivity extends AppCompatActivity {

    private static String IP_ADDRESS = "54.237.101.189";
    private static String TAG = "phptest";


    private TextView textViewStemp;
    private TextView textViewShumid;
    private TextView textViewSdust;
    private TextView textViewAsido;
    private TextView textViewApm10;
    private TextView textViewApm25;
    private TextView textViewComp;
    private String mJsonString;

    private PmData pmData=new PmData();
    private SensData sensData=new SensData();

    private int isSensdata=0;
    private int isApidata=0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        StrictMode.enableDefaults();

        textViewComp = (TextView) findViewById(R.id.textView_comp);

        textViewStemp = (TextView) findViewById(R.id.textView_sens_temp);
        textViewShumid = (TextView) findViewById(R.id.textView_sens_humid);
        textViewSdust = (TextView) findViewById(R.id.textView_sens_dust);

        textViewAsido = (TextView) findViewById(R.id.textView_api_sido);
        textViewApm10 = (TextView) findViewById(R.id.textView_api_pm10);
        textViewApm25 = (TextView) findViewById(R.id.textView_api_pm25);

        //pmData.setSido("청주시");

        Button button_s = (Button) findViewById(R.id.button_sens);
        button_s.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                GetSensData task = new GetSensData();
                task.execute("http://" + IP_ADDRESS + "/sensingData.php", "");
            }
        });

        Button button_a = (Button) findViewById(R.id.button_api);
        button_a.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                GetApiData();
            }
        });

    }

    private void ShowSensResult(){

        String TAG_JSON="sensData";
        String TAG_TEMP = "temp";
        String TAG_HUMID ="humid";
        String TAG_DUST="dust";
        String _temp = null;
        String _humid=null;
        String _dust=null;


        try {
            JSONObject jsonObject = new JSONObject(mJsonString);
            JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);


            JSONObject item = jsonArray.getJSONObject(0);

            _temp = item.getString(TAG_TEMP);
            _humid = item.getString(TAG_HUMID);
            _dust = item.getString(TAG_DUST);


            sensData.setTemp(_temp);
            sensData.setHumid(_humid);
            sensData.setDust(_dust);

            textViewStemp.setText(_temp + " °C");
            textViewShumid.setText(_humid + " %");
            textViewSdust.setText(_dust + " ㎍/㎥    ");

        } catch (JSONException e) {

            Log.d(TAG, "showResult : ", e);
        }
    }


    private class GetSensData extends AsyncTask<String, Void, String>{

        ProgressDialog progressDialog;
        String errorString = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(MainActivity.this,
                    "Please Wait", null, true, true);
            isSensdata=1;
        }

        @Override
        protected String doInBackground(String... params) {

            String serverURL = params[0];
            String postParameters = params[1];

            try {
                URL url = new URL(serverURL);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

                httpURLConnection.setReadTimeout(5000);
                httpURLConnection.setConnectTimeout(5000);
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoInput(true);
                httpURLConnection.connect();

                OutputStream outputStream = httpURLConnection.getOutputStream();
                outputStream.write(postParameters.getBytes("UTF-8"));
                outputStream.flush();
                outputStream.close();

                int responseStatusCode = httpURLConnection.getResponseCode();
                Log.d(TAG, "response code - " + responseStatusCode);

                InputStream inputStream;
                if (responseStatusCode == HttpURLConnection.HTTP_OK) {
                    inputStream = httpURLConnection.getInputStream();
                } else {
                    inputStream = httpURLConnection.getErrorStream();
                }

                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                StringBuilder sb = new StringBuilder();
                String line;

                while ((line = bufferedReader.readLine()) != null) {
                    sb.append(line);
                }

                bufferedReader.close();

                return sb.toString().trim();

            } catch (Exception e) {

                Log.d(TAG, "GetData : Error ", e);
                errorString = e.toString();

                return null;
            }
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            progressDialog.dismiss();

            Log.d(TAG, "response - " + result);

            if (result == null){

                textViewStemp.setText(errorString);
                textViewShumid.setText(errorString);
                textViewSdust.setText(errorString);
            }
            else {

                mJsonString = result;
                ShowSensResult();
               if(isApidata==1&&isSensdata==1) {
                  // WindowCtrl();
                   textViewComp.setText("실내외 미세먼지농도가 비슷해요.");
                }
            }
        }

    }


    private void GetApiData() {

        //TextView textView = (TextView) findViewById(R.id.textView_api);
        isApidata=1;
        String _url = "http://openapi.airkorea.or.kr/openapi/services/rest/ArpltnInforInqireSvc/getCtprvnMesureSidoLIst?serviceKey=8%2B64ddNqAnHmK8Lx8tI%2Bngxj6dtexb%2FhgwcSwsDjWb%2FbWSgRHUmNk9%2BtkAlqPCFcVb1QiMx49DQfXdMPHnNkzg%3D%3D&numOfRows=10&pageNo=1&sidoName=충북&searchCondition=DAILY";

        boolean isdataTime = false, isCityName = false, ispm10 = false, ispm25 = false;


        try {//XML파싱

            URL url = new URL(_url);
            InputStream in = url.openStream();

            //factory 생성
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            //factory에서 pullparser생성
            XmlPullParser parser = factory.newPullParser();

            //parser에서 사용할 데이터를 입력.
            parser.setInput(in, "utf-8");

            //현재 팟의 이벤트 타입을 받는다.
            int eventType = parser.getEventType();


            int check = 0;
            int flag=0;
            //Loop구문: parser의 마지막 </tag>가 될 때까지
            while ((eventType != XmlPullParser.END_DOCUMENT) && check == 0) {
                //<tag>text</tag>이므로 startTag뒤에 있는 text를 찾기위한 switch절
                switch (eventType) {
                    case XmlPullParser.START_TAG:  //start tag가 다음 중 하나일 때 flag=true
                        if (parser.getName().equals("dataTime")) {
                            isdataTime = true;
                        } else if (parser.getName().equals("cityName")) {
                            isCityName = true;
                        } else if (parser.getName().equals("pm10Value")) {
                            ispm10 = true;
                        } else if (parser.getName().equals("pm25Value")) {
                            ispm25 = true;
                        }
                        break;

                    case XmlPullParser.TEXT:
                        if (isdataTime) {
                            pmData.setDataTime(parser.getText());
                            isdataTime = false;
                        }
                        else if(isCityName) {
                            pmData.setSido(parser.getText());
                            if(pmData.getSido().equals("청주시")){
                                flag=1;
                            }
                            isCityName=false;
                        }else if (ispm10) {
                            pmData.setPM10(parser.getText());
                            ispm10 = false;
                        } else if (ispm25) {
                            pmData.setPM25(parser.getText());
                            ispm25 = false;
                        }
                        break;

                    case XmlPullParser.END_TAG:
                        if (parser.getName().equals("item")&&flag==1) {
                            textViewAsido.setText(pmData.getSido());
                            textViewApm10.setText(pmData.getPM10()+" ㎍/㎥        "+pmData.getPM10Grade());
                            textViewApm25.setText(pmData.getPM25()+" ㎍/㎥        "+pmData.getPM25Grade());

                            if(isApidata==1&&isSensdata==1) {
                                 //WindowCtrl();
                                textViewComp.setText("실내 미세먼지가 더 좋지 않아요. 창문을 열어 환기하세요.");
                            }
                            check = 1;
                            flag=0;

                        }
                        break;
                }
                eventType = parser.next();
            }

        } catch (Exception e) {
            textViewAsido.setText("에러가 났습니다...");
            textViewApm10.setText("에러가 났습니다...");
            textViewApm25.setText("에러가 났습니다...");

        }

    }
//    private void WindowCtrl(String _pm10,String _dust){
//        int pm10=Integer.parseInt(_pm10);
//        int dust=Integer.parseInt(_dust);
//        if(pm10>dust){
//            textViewComp.setText("실외 미세먼지가 더 좋지 않아요. 창문을 꼭 닫으세요.");
//        }
//        else if(pm10==dust){
//
//            textViewComp.setText("실내외 미세먼지농도가 비슷해요.");
//        }
//        else{
//            textViewComp.setText("실내 미세먼지가 더 좋지 않아요. 창문을 열어 환기하세요.");
//        }
//    }
}