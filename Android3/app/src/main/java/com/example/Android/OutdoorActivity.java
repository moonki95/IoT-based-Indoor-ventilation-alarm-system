package com.example.Android;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.net.URL;

public class OutdoorActivity extends AppCompatActivity {

    private Spinner spinnerSido, spinnerGungu;
    private ArrayAdapter<String> arrayAdapter;
    private String sido, gungu;

    private TextView textViewSido;
    private TextView textViewPm10;
    private TextView textViewPm25;

    private OutdoorAir outdoorAir =new OutdoorAir();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_outdoor);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_outdoor);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        textViewSido = (TextView) findViewById(R.id.textView_sido);
        textViewPm10 = (TextView) findViewById(R.id.textView_pm10);
        textViewPm25 = (TextView) findViewById(R.id.textView_pm25);

        spinnerSido=(Spinner)findViewById(R.id.spinner_sido);
        arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, (String[])getResources().getStringArray(R.array.spinner_region));
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSido.setAdapter(arrayAdapter);

        spinnerGungu=(Spinner)findViewById(R.id.spinner_gungu);
        initAddressSpinner();


        Button okBtn = findViewById(R.id.btn_ok);
        okBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {


                sido=spinnerSido.getSelectedItem().toString();
                gungu=spinnerGungu.getSelectedItem().toString();
                outdoorAir.setSido(sido);
                outdoorAir.setGungu(gungu);

                GetXMLTask task = new GetXMLTask();
                task.execute();
            }
        });


    }

    private void initAddressSpinner() {
        spinnerSido.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // 시군구, 동의 스피너를 초기화한다.
                switch (position) {
                    case 0:
                        spinnerGungu.setAdapter(null);
                        break;
                    case 1:
                        setGunguSpinnerAdapterItem(R.array.spinner_region_seoul);
                        break;
                    case 2:
                        setGunguSpinnerAdapterItem(R.array.spinner_region_busan);
                        break;
                    case 3:
                        setGunguSpinnerAdapterItem(R.array.spinner_region_daegu);
                        break;
                    case 4:
                        setGunguSpinnerAdapterItem(R.array.spinner_region_incheon);
                        break;
                    case 5:
                        setGunguSpinnerAdapterItem(R.array.spinner_region_gwangju);
                        break;
                    case 6:
                        setGunguSpinnerAdapterItem(R.array.spinner_region_daejeon);
                        break;
                    case 7:
                        setGunguSpinnerAdapterItem(R.array.spinner_region_ulsan);
                        break;
                    case 8:
                        setGunguSpinnerAdapterItem(R.array.spinner_region_gyeonggi);
                        break;
                    case 9:
                        setGunguSpinnerAdapterItem(R.array.spinner_region_gangwon);
                        break;
                    case 10:
                        setGunguSpinnerAdapterItem(R.array.spinner_region_chung_buk);
                        break;
                    case 11:
                        setGunguSpinnerAdapterItem(R.array.spinner_region_chung_nam);
                        break;
                    case 12:
                        setGunguSpinnerAdapterItem(R.array.spinner_region_jeon_buk);
                        break;
                    case 13:
                        setGunguSpinnerAdapterItem(R.array.spinner_region_jeon_nam);
                        break;
                    case 14:
                        setGunguSpinnerAdapterItem(R.array.spinner_region_gyeong_buk);
                        break;
                    case 15:
                        setGunguSpinnerAdapterItem(R.array.spinner_region_gyeong_nam);
                        break;
                    case 16:
                        setGunguSpinnerAdapterItem(R.array.spinner_region_jeju);
                        break;
                    case 17:
                        setGunguSpinnerAdapterItem(R.array.spinner_region_sejong);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }
    private void setGunguSpinnerAdapterItem(int array_resource) {
        if (arrayAdapter != null) {
            spinnerGungu.setAdapter(null);
            arrayAdapter = null;
        }

        arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, (String[])getResources().getStringArray(array_resource));
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerGungu.setAdapter(arrayAdapter);
    }


    private class GetXMLTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... voids) {
            String url = "http://openapi.airkorea.or.kr/openapi/services/rest/ArpltnInforInqireSvc/getCtprvnMesureSidoLIst?serviceKey=8%2B64ddNqAnHmK8Lx8tI%2Bngxj6dtexb%2FhgwcSwsDjWb%2FbWSgRHUmNk9%2BtkAlqPCFcVb1QiMx49DQfXdMPHnNkzg%3D%3D&numOfRows=40&pageNo=1&sidoName="+sido+"&searchCondition=DAILY";
            XmlPullParserFactory factory;
            XmlPullParser parser;
            URL xmlUrl;
            String returnResult = "";

            boolean isdataTime = false, isCityName = false, ispm10 = false, ispm25 = false;

            try {
                int flag = 0;
                int check=0;

                xmlUrl = new URL(url);
                xmlUrl.openConnection().getInputStream();
                factory = XmlPullParserFactory.newInstance();
                parser = factory.newPullParser();
                parser.setInput(xmlUrl.openStream(), "utf-8");

                int eventType = parser.getEventType();
                while ((eventType != XmlPullParser.END_DOCUMENT) && check == 0) {
                    //<tag>text</tag>이므로 startTag뒤에 있는 text를 찾기위한 switch절
                    switch (eventType) {
                        case XmlPullParser.START_TAG:  //start tag가 다음 중 하나일 때 flag=true
                            if (parser.getName().equals("cityName")) {
                                isCityName = true;
                            } else if (parser.getName().equals("pm10Value")) {
                                ispm10 = true;
                            } else if (parser.getName().equals("pm25Value")) {
                                ispm25 = true;
                            }
                            break;

                        case XmlPullParser.TEXT:
                            if(isCityName) {
                                if(parser.getText().equals(gungu)){
                                    flag=1;
                                }
                                isCityName=false;
                            }else if (ispm10&&flag==1) {
                                outdoorAir.setPM10(parser.getText());
                                ispm10 = false;
                            } else if (ispm25&&flag==1) {
                                outdoorAir.setPM25(parser.getText());
                                ispm25 = false;
                            }
                            break;

                        case XmlPullParser.END_TAG:
                            if (parser.getName().equals("item")&&flag==1) {
                                textViewSido.setText(sido+" "+gungu);
                                textViewPm10.setText(outdoorAir.getPM10()+" ㎍/㎥        "+ outdoorAir.getPM10Grade());
                                textViewPm25.setText(outdoorAir.getPM25()+" ㎍/㎥        "+ outdoorAir.getPM25Grade());

                                check = 1;
                                flag=0;

                            }
                            break;
                    }
                    eventType = parser.next();
                }
            } catch (Exception e) {
                textViewSido.setText("에러가 났습니다...");
                textViewPm10.setText("에러가 났습니다...");
                textViewPm25.setText("에러가 났습니다...");
            }
            return returnResult;
        }



        @Override
        protected void onPostExecute(String result) {

        }

    }

}
