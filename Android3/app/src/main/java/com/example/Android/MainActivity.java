package com.example.Android;


import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.lang.*;
import java.net.URL;
import java.util.List;
import java.util.Locale;


public class MainActivity extends AppCompatActivity {

    private static final int GPS_ENABLE_REQUEST_CODE = 2001 ;
    private static final int PERMISSIONS_REQUEST_CODE = 100;
    String[] REQUIRED_PERMISSIONS  = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};

    private GpsTracker gpsTracker;
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
        setContentView(R.layout.activity_main);

        //툴바
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        textViewSido = (TextView) findViewById(R.id.textView_sido);
        textViewPm10 = (TextView) findViewById(R.id.textView_pm10);
        textViewPm25 = (TextView) findViewById(R.id.textView_pm25);

        if (!checkLocationServicesStatus()) {

            showDialogForLocationServiceSetting();
        }else {

            checkRunTimePermission();
        }

        gpsTracker = new GpsTracker(MainActivity.this);
        double latitude = gpsTracker.getLatitude(); // 위도
        double longitude = gpsTracker.getLongitude(); //경도
        String address = getCurrentAddress(latitude, longitude);
        textViewSido.setText(address);

//        //스피너
//        spinnerSido=(Spinner)findViewById(R.id.spinner_sido);
//        arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, (String[])getResources().getStringArray(R.array.spinner_region));
//        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        spinnerSido.setAdapter(arrayAdapter);
//
//        spinnerGungu=(Spinner)findViewById(R.id.spinner_gungu);
//        initAddressSpinner();


        if(outdoorAir.getSido()!=null){
            textViewSido.setText(outdoorAir.getSido()+" "+outdoorAir.getGungu());
            textViewPm10.setText(outdoorAir.getPM10()+" ㎍/㎥        "+ outdoorAir.getPM10Grade());
            textViewPm25.setText(outdoorAir.getPM25()+" ㎍/㎥        "+ outdoorAir.getPM25Grade());
        }
        Button okBtn = findViewById(R.id.btn_ok);
        okBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

//                sido=spinnerSido.getSelectedItem().toString();
//                gungu=spinnerGungu.getSelectedItem().toString();
                outdoorAir.setSido(sido);
                outdoorAir.setGungu(gungu);

                GetXMLTask task = new GetXMLTask();
                task.execute();
            }
        });


    }

    public String getCurrentAddress( double latitude, double longitude) {

        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> addresses;
        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 100);
        } catch (IOException ioException) {
            //네트워크 문제
            Toast.makeText(this, "지오코더 서비스 사용불가", Toast.LENGTH_LONG).show();
            showDialogForLocationServiceSetting();
            return "지오코더 서비스 사용불가";
        } catch (IllegalArgumentException illegalArgumentException) {
            Toast.makeText(this, "잘못된 GPS 좌표", Toast.LENGTH_LONG).show();
            showDialogForLocationServiceSetting();
            return "잘못된 GPS 좌표";
        }

        if (addresses == null || addresses.size() == 0) {
            Toast.makeText(this, "주소 미발견", Toast.LENGTH_LONG).show();
            showDialogForLocationServiceSetting();
            return "주소 미발견";
        }
        Address address = addresses.get(0);
        return address.getAddressLine(0).toString() + "\n";
    }
    //여기부터는 GPS 활성화를 위한 메소드들
    private void showDialogForLocationServiceSetting() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("위치 서비스 비활성화");
        builder.setMessage("앱을 사용하기 위해서는 위치 서비스가 필요합니다.\n" + "위치 설정을 수정하실래요?");
        builder.setCancelable(true);
        builder.setPositiveButton("설정", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                Intent callGPSSettingIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivityForResult(callGPSSettingIntent, GPS_ENABLE_REQUEST_CODE);
            }
        });
        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        builder.create().show();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case GPS_ENABLE_REQUEST_CODE: //사용자가 GPS 활성 시켰는지 검사
                if (checkLocationServicesStatus()) {
                    if (checkLocationServicesStatus()) {
                        Log.d("@@@", "onActivityResult : GPS 활성화 되있음");
                        checkRunTimePermission();
                        return;
                    }
                }
                break;
        }
    }

    public boolean checkLocationServicesStatus() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }
    void checkRunTimePermission(){

        //런타임 퍼미션 처리
        // 1. 위치 퍼미션을 가지고 있는지 체크합니다.
        int hasFineLocationPermission = ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        int hasCoarseLocationPermission = ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.ACCESS_COARSE_LOCATION);


        if (hasFineLocationPermission == PackageManager.PERMISSION_GRANTED &&
                hasCoarseLocationPermission == PackageManager.PERMISSION_GRANTED) {

            // 2. 이미 퍼미션을 가지고 있다면
            // ( 안드로이드 6.0 이하 버전은 런타임 퍼미션이 필요없기 때문에 이미 허용된 걸로 인식합니다.)


            // 3.  위치 값을 가져올 수 있음



        } else {  //2. 퍼미션 요청을 허용한 적이 없다면 퍼미션 요청이 필요합니다. 2가지 경우(3-1, 4-1)가 있습니다.

            // 3-1. 사용자가 퍼미션 거부를 한 적이 있는 경우에는
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, REQUIRED_PERMISSIONS[0])) {

                // 3-2. 요청을 진행하기 전에 사용자가에게 퍼미션이 필요한 이유를 설명해줄 필요가 있습니다.
                Toast.makeText(MainActivity.this, "이 앱을 실행하려면 위치 접근 권한이 필요합니다.", Toast.LENGTH_LONG).show();
                // 3-3. 사용자게에 퍼미션 요청을 합니다. 요청 결과는 onRequestPermissionResult에서 수신됩니다.
                ActivityCompat.requestPermissions(MainActivity.this, REQUIRED_PERMISSIONS,
                        PERMISSIONS_REQUEST_CODE);


            } else {
                // 4-1. 사용자가 퍼미션 거부를 한 적이 없는 경우에는 퍼미션 요청을 바로 합니다.
                // 요청 결과는 onRequestPermissionResult에서 수신됩니다.
                ActivityCompat.requestPermissions(MainActivity.this, REQUIRED_PERMISSIONS,
                        PERMISSIONS_REQUEST_CODE);
            }

        }

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.menu_option,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Intent intent=null;
        switch(item.getItemId()){
            case R.id.menu_outdoor:

                break;
            case R.id.menu_indoor:
                intent=new Intent(this, IndoorActivity.class);
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void clickIndoorBtn(View view){
        Intent intent=new Intent(this, IndoorActivity.class);
        startActivity(intent);
    }

    public void clickOutsideBtn(View view){
        Intent intent=new Intent(this, OutdoorActivity.class);
        startActivity(intent);
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