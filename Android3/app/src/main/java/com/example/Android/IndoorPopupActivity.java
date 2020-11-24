package com.example.Android;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class IndoorPopupActivity extends Activity {

    private static String IP_ADDRESS = "54.144.159.63";
    private static String TAG = "phptest";

    private TextView textViewTemp;
    private TextView textViewHumid;
    private TextView textViewDust;

    private IndoorAir indoorAir =new IndoorAir();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //타이틀바 없애기
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_indoor_popup);


        textViewTemp = (TextView) findViewById(R.id.textView_temp);
        textViewHumid = (TextView) findViewById(R.id.textView_humid);
        textViewDust = (TextView) findViewById(R.id.textView_dust);

        String url="http://" + IP_ADDRESS + "/getjson.php";
        SelectDatabaseTask task = new SelectDatabaseTask(url, null);
        task.execute();


    }

    //확인 버튼 클릭
    public void mOnClose(View v){

        //액티비티(팝업) 닫기
        finish();
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
            //textViewTemp.setText(s);  // 파서 없이 전체 출력
            ShowIndoorAirData(s); // 파서로 전체 출력
        }
    }


    private void ShowIndoorAirData(String mJsonString){

        String TAG_JSON="result";
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


            indoorAir.setTemp(_temp);
            indoorAir.setHumid(_humid);
            indoorAir.setDust(_dust);

            textViewTemp.setText(_temp + " °C");
            textViewHumid.setText(_humid + " %");
            textViewDust.setText(_dust + " ㎍/㎥    ");

        } catch (JSONException e) {

            Log.d(TAG, "showResult : ", e);
        }
    }


}
