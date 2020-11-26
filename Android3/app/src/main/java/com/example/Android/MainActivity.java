package com.example.Android;


import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.lang.*;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;


public class MainActivity extends AppCompatActivity {

    private GpsTracker gpsTracker;
    private static final int GPS_ENABLE_REQUEST_CODE = 2001 ;
    private static final int PERMISSIONS_REQUEST_CODE = 100;
    String[] REQUIRED_PERMISSIONS  = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};

    private String[] sigungu=new String[2];

    private TextView textViewSido;
    private TextView textViewTime;
    private TextView textViewPm10;
    private TextView textViewGrade;
    private TextView textViewPm25;
    private TextView textViewBelowpm10;
    private TextView textViewBelowpm25;
    private TextView textViewo3;
    private TextView textViewco;
    private TextView textViewno2;
    private TextView textViewso2;

    private OutdoorAir outdoorAir =new OutdoorAir();

    private Timer timer_indoor=new Timer();
    private Timer timer_outdoor=new Timer();
    private static String IP_ADDRESS = "54.144.159.63";
    private static String TAG = "phptest";
    private String url = "http://" + IP_ADDRESS + "/getjson.php";

    long mNow;
    Date mDate;
    SimpleDateFormat mFormat=new SimpleDateFormat("mm");
    String currTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // GPS 접근권한 확인
        if (!checkLocationServicesStatus()) {
            showDialogForLocationServiceSetting();
        }
        else {
            checkRunTimePermission();
        }
        gpsTracker = new GpsTracker(MainActivity.this);
        double latitude = gpsTracker.getLatitude(); // 위도
        double longitude = gpsTracker.getLongitude(); //경도
        String address = getCurrentAddress(latitude, longitude);

        //툴바
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // 텍스트뷰
        textViewSido = (TextView) findViewById(R.id.textView_sido);
        textViewTime= (TextView) findViewById(R.id.textView_time);
        textViewPm10 = (TextView) findViewById(R.id.textView_pm10);
        textViewGrade=(TextView) findViewById(R.id.textView_grade);
        textViewPm25 = (TextView) findViewById(R.id.textView_pm25);
        textViewBelowpm10=(TextView) findViewById(R.id.dust4);
        textViewBelowpm25=(TextView) findViewById(R.id.nano4);
        textViewo3=(TextView) findViewById(R.id.ozone4);
        textViewco=(TextView) findViewById(R.id.co4);
        textViewno2=(TextView) findViewById(R.id.no4);
        textViewso2=(TextView) findViewById(R.id.so4);

        textViewSido.setGravity(Gravity.CENTER);
        textViewTime.setGravity(Gravity.CENTER);
        textViewPm10.setGravity(Gravity.CENTER);
        textViewGrade.setGravity(Gravity.CENTER);
        textViewPm25.setGravity(Gravity.CENTER);

        mNow=System.currentTimeMillis();
        mDate=new Date(mNow);
        currTime=mFormat.format(mDate);

        //타이머
        TimerTask tt_indoor= new TimerTask(){
            @Override
            public void run() {
                //실내공기데이터 가져와서 나쁨이면 알람
                SelectDatabaseTask task = new SelectDatabaseTask(url, null);
                task.execute();

                //시간이 바뀌면 실외공기데이터 다시 뿌리기
                if(currTime.equals("00")){
                    GetOutdoorDataTask getOutdoorDataTask = new GetOutdoorDataTask();
                    getOutdoorDataTask.execute();
                }
            }
        };
        timer_indoor.schedule(tt_indoor,0,5000);  //5초마다 실행


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
            case R.id.menu_outdoor_current_position:
                gpsTracker = new GpsTracker(MainActivity.this);
                double latitude = gpsTracker.getLatitude(); // 위도
                double longitude = gpsTracker.getLongitude(); //경도
                String address = getCurrentAddress(latitude, longitude);
                Log.e("address",address);
                Log.e("si",sigungu[0]);
                Log.e("gungu",sigungu[1]);
                break;
            case R.id.menu_outdoor_diff_region:
                intent=new Intent(this, OutdoorPopupActivity.class);
                startActivityForResult(intent,1);
                break;
            case R.id.menu_indoor:
                intent=new Intent(this, IndoorPopupActivity.class);
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    // 다른 지역 실외공기 데이터 받아오기
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
             case 1:    // 다른 지역 선택
                if (resultCode == RESULT_OK) {
                    //데이터 받기
                    String[] result = data.getStringArrayExtra("result");
                    sigungu[0] = result[0];
                    sigungu[1] = result[1];
                    outdoorAir.setSido(sigungu[0]);
                    outdoorAir.setGungu(sigungu[1]);

                    GetOutdoorDataTask getOutdoorDataTask = new GetOutdoorDataTask();
                    getOutdoorDataTask.execute();
                }
                break;
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

    private void createNotification(String text) {

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "default");

        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setContentTitle("실내 미세먼지");
        builder.setContentText(text);

        builder.setColor(Color.RED);
        // 사용자가 탭을 클릭하면 자동 제거
        builder.setAutoCancel(true);

        // 알림 표시
        NotificationManager notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.createNotificationChannel(new NotificationChannel("default", "기본 채널", NotificationManager.IMPORTANCE_DEFAULT));
        }

        // id값은
        // 정의해야하는 각 알림의 고유한 int값
        notificationManager.notify(1, builder.build());
    }

    class SelectDatabaseTask extends AsyncTask<Void, Void, String> {

        private String url1;
        private ContentValues values1;
        String result1; // 요청 결과를 저장할 변수.

        public SelectDatabaseTask(String url, ContentValues contentValues) {
            this.url1 = url;
            this.values1 = contentValues;
        }

        @Override
        protected String doInBackground(Void... params) {
            RequestHttpURLConnection requestHttpURLConnection = new RequestHttpURLConnection();
            result1 = requestHttpURLConnection.request(url1, values1); // 해당 URL로 부터 결과물을 얻어온다.
            return result1; // 여기서 당장 실행 X, onPostExcute에서 실행
        }
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            GetAirData(s); // 파서로 전체 출력
        }
    }


    private void GetAirData(String mJsonString){

        String TAG_JSON="result";
        String TAG_DUST="dust";
        String _dust=null;
        String text;

        try {
            JSONObject jsonObject = new JSONObject(mJsonString);
            JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);
            JSONObject item = jsonArray.getJSONObject(0);

            _dust = item.getString(TAG_DUST);

            int dust = Integer.parseInt(_dust);
            if (80 < dust && dust <= 150) {
                text = "실내 공기가 나쁨이에요. 환기를 시켜보세요~";
                //Log.e("타이머",_dust);
                createNotification(text);
            }
            else if(dust > 150){
                text = "실내 공기가 매우나쁨이에요. 환기가 필요해요!!";
                //Log.e("타이머",_dust);
                createNotification(text);
            }
        } catch (JSONException e) {
            Log.d(TAG, "showResult : ", e);
        }
    }

    private class GetOutdoorDataTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... voids) {
            String url = "http://openapi.airkorea.or.kr/openapi/services/rest/ArpltnInforInqireSvc/getCtprvnMesureSidoLIst?serviceKey=8%2B64ddNqAnHmK8Lx8tI%2Bngxj6dtexb%2FhgwcSwsDjWb%2FbWSgRHUmNk9%2BtkAlqPCFcVb1QiMx49DQfXdMPHnNkzg%3D%3D&numOfRows=40&pageNo=1&sidoName="+sigungu[0]+"&searchCondition=DAILY";
            XmlPullParserFactory factory;
            XmlPullParser parser;
            URL xmlUrl;
            String returnResult = "";

            boolean isdataTime = false, isCityName = false, ispm10 = false, ispm25 = false
                    , iso3 = false, isco = false, isno2 = false, isso2 = false;

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
                            if (parser.getName().equals("dataTime")){
                                isdataTime=true;
                            }
                            else if (parser.getName().equals("cityName")) {
                                isCityName = true;
                            } else if (parser.getName().equals("pm10Value")) {
                                ispm10 = true;
                            } else if (parser.getName().equals("pm25Value")) {
                                ispm25 = true;
                            }
                            else if (parser.getName().equals("o3Value")) {
                                iso3 = true;
                            }
                            else if (parser.getName().equals("coValue")) {
                                isco = true;
                            }
                            else if (parser.getName().equals("no2Value")) {
                                isno2 = true;
                            }
                            else if (parser.getName().equals("so2Value")) {
                                isso2 = true;
                            }

                            break;

                        case XmlPullParser.TEXT:
                            if(isdataTime){
                                outdoorAir.setDataTime(parser.getText());
                                isdataTime=false;
                            }
                            else if(isCityName) {
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
                            }else if (iso3&&flag==1) {
                                outdoorAir.setO3(parser.getText());
                                iso3 = false;
                            } else if (isco&&flag==1) {
                                outdoorAir.setCo(parser.getText());
                                isco = false;
                            }else if (isno2&&flag==1) {
                                outdoorAir.setNo2(parser.getText());
                                isno2 = false;
                            } else if (isso2&&flag==1) {
                                outdoorAir.setSo2(parser.getText());
                                isso2 = false;
                            }
                            break;

                        case XmlPullParser.END_TAG:
                            if (parser.getName().equals("item")&&flag==1) {
                                textViewSido.setText(sigungu[0]+" "+sigungu[1]);
                                textViewTime.setText(outdoorAir.getDataTime());
                                textViewGrade.setText(outdoorAir.getPM10Grade());
                                textViewPm10.setText(outdoorAir.getPM10());
                                textViewBelowpm10.setText(outdoorAir.getPM10());
                                textViewBelowpm25.setText(outdoorAir.getPM25());
                                textViewo3.setText(outdoorAir.getO3());
                                textViewco.setText(outdoorAir.getCo());
                                textViewno2.setText(outdoorAir.getNo2());
                                textViewso2.setText(outdoorAir.getSo2());

                                //textViewPm25.setText(outdoorAir.getPM25()+" ㎍/㎥        "+ outdoorAir.getPM25Grade());

                                check = 1;
                                flag=0;

                            }
                            break;
                    }
                    eventType = parser.next();
                }
            } catch (Exception e) {
                textViewSido.setText("에러가 났습니다...");
                textViewTime.setText("에러가 났습니다...");
                textViewPm10.setText("에러가 났습니다...");
                //textViewPm25.setText("에러가 났습니다...");
            }
            return returnResult;
        }

        @Override
        protected void onPostExecute(String result) {
        }
    }

    // GPS
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

        String sido=address.getAdminArea().toString();
        sigungu[0]=changeAddress(sido);
        sigungu[1]=address.getLocality().toString();
        outdoorAir.setSido(sigungu[0]);
        outdoorAir.setGungu(sigungu[1]);

        GetOutdoorDataTask getOutdoorDataTask = new GetOutdoorDataTask();
        getOutdoorDataTask.execute();

        return address.toString()+"\n";
//        return address.getAddressLine(0).toString() + "\n";
    }
    public String changeAddress(String s){
        String sido;
        switch(s){
            case "서울특별시":
                sido= "서울";
                break;
            case "부산광역시":
                sido= "부산";
                break;
            case "대구광역시":
                sido= "대구";
                break;
            case "인천광역시":
                sido= "인천";
                break;
            case "광주광역시":
                sido= "광주";
                break;
            case "대전광역시":
                sido= "대전";
                break;
            case "울산광역시":
                sido= "울산";
                break;
            case "경기도":
                sido= "경기";
                break;
            case "강원도":
                sido= "강원";
                break;
            case "충청북도":
                sido= "충북";
                break;
            case "충청남도":
                sido= "충남";
                break;
            case "전라북도":
                sido= "전북";
                break;
            case "전라남도":
                sido= "전남";
                break;
            case "경상북도":
                sido= "경북";
                break;
            case "경상남도":
                sido= "경남";
                break;
            case "제주도":
                sido= "제주";
                break;
            case "세종특별시":
                sido= "세종";
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + s);
        }
        return sido;
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

    public boolean checkLocationServicesStatus() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }
    @Override
    public void onRequestPermissionsResult(int permsRequestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grandResults) {

        if ( permsRequestCode == PERMISSIONS_REQUEST_CODE && grandResults.length == REQUIRED_PERMISSIONS.length) {

            // 요청 코드가 PERMISSIONS_REQUEST_CODE 이고, 요청한 퍼미션 개수만큼 수신되었다면

            boolean check_result = true;


            // 모든 퍼미션을 허용했는지 체크합니다.

            for (int result : grandResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    check_result = false;
                    break;
                }
            }


            if ( check_result ) {

                //위치 값을 가져올 수 있음
                ;
            }
            else {
                // 거부한 퍼미션이 있다면 앱을 사용할 수 없는 이유를 설명해주고 앱을 종료합니다.2 가지 경우가 있습니다.

                if (ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[0])
                        || ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[1])) {

                    Toast.makeText(MainActivity.this, "퍼미션이 거부되었습니다. 앱을 다시 실행하여 퍼미션을 허용해주세요.", Toast.LENGTH_LONG).show();
                    finish();


                }else {

                    Toast.makeText(MainActivity.this, "퍼미션이 거부되었습니다. 설정(앱 정보)에서 퍼미션을 허용해야 합니다. ", Toast.LENGTH_LONG).show();

                }
            }

        }
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