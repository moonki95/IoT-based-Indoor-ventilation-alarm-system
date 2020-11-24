package com.example.Android;


import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;


import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.lang.*;
import java.net.URL;


public class MainActivity extends AppCompatActivity {

  /*  private static final int GPS_ENABLE_REQUEST_CODE = 2001 ;
    private static final int PERMISSIONS_REQUEST_CODE = 100;
    String[] REQUIRED_PERMISSIONS  = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};*/

//    private GpsTracker gpsTracker;
//    private Spinner spinnerSido, spinnerGungu;
//    private ArrayAdapter<String> arrayAdapter;

    private String[] sigungu=new String[2];

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

        // 텍스트뷰
        textViewSido = (TextView) findViewById(R.id.textView_sido);
        textViewPm10 = (TextView) findViewById(R.id.textView_pm10);
        textViewPm25 = (TextView) findViewById(R.id.textView_pm25);

       /* // GPS 접근 권한 확인???
        if (!checkLocationServicesStatus()) {
            showDialogForLocationServiceSetting();
        }else {
            checkRunTimePermission();
        }

        gpsTracker = new GpsTracker(MainActivity.this);
        double latitude = gpsTracker.getLatitude(); // 위도
        double longitude = gpsTracker.getLongitude(); //경도
        String address = getCurrentAddress(latitude, longitude);
        textViewSido.setText(address);*/

//        //스피너
//        spinnerSido=(Spinner)findViewById(R.id.spinner_sido);
//        arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, (String[])getResources().getStringArray(R.array.spinner_region));
//        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        spinnerSido.setAdapter(arrayAdapter);
//
//        spinnerGungu=(Spinner)findViewById(R.id.spinner_gungu);
//        initAddressSpinner();


         //설정한 시군구가 있으면 계속 표시되게 하려고...
        if(outdoorAir.getSido()!=null){
            GetXMLTask getXMLTask = new GetXMLTask();
            getXMLTask.execute();
        }


        /*Button okBtn = findViewById(R.id.btn_ok);
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
        });*/


    }

    // 메뉴
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.menu_option,menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Intent intent;
        switch(item.getItemId()){
            case R.id.menu_outdoor:
                intent=new Intent(this, OutdoorPopupActivity.class);
                //intent.putExtra("data",sigungu);
                startActivityForResult(intent,1);
                break;
            case R.id.menu_indoor:
                intent=new Intent(this, IndoorPopupActivity.class);
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    // 팝업 spinner에서 시군구 데이터 받아오기
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                //데이터 받기
                String[] result = data.getStringArrayExtra("result");
                sigungu[0]=result[0];
                sigungu[1]=result[1];
                outdoorAir.setSido(sigungu[0]);
                outdoorAir.setGungu(sigungu[1]);

                GetXMLTask getXMLTask = new GetXMLTask();
                getXMLTask.execute();
                //getXMLTask.cancel(true);
            }
        }
    }

    /*// GPS
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

    }*/


    private class GetXMLTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... voids) {
            String url = "http://openapi.airkorea.or.kr/openapi/services/rest/ArpltnInforInqireSvc/getCtprvnMesureSidoLIst?serviceKey=8%2B64ddNqAnHmK8Lx8tI%2Bngxj6dtexb%2FhgwcSwsDjWb%2FbWSgRHUmNk9%2BtkAlqPCFcVb1QiMx49DQfXdMPHnNkzg%3D%3D&numOfRows=40&pageNo=1&sidoName="+sigungu[0]+"&searchCondition=DAILY";
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
                                if(parser.getText().equals(sigungu[1])){
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
                                textViewSido.setText(sigungu[0]+" "+sigungu[1]);
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