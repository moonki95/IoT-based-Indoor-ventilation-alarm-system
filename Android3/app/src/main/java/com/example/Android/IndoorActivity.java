package com.example.Android;

import android.content.ContentValues;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class IndoorActivity extends AppCompatActivity {
    private static String IP_ADDRESS = "54.144.159.63";
    private static String TAG = "phptest";

    private TextView textViewTemp;
    private TextView textViewHumid;
    private TextView textViewDust;

    private IndoorAir indoorAir =new IndoorAir();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_indoor);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_indoor);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        textViewTemp = (TextView) findViewById(R.id.textView_temp);
        textViewHumid = (TextView) findViewById(R.id.textView_humid);
        textViewDust = (TextView) findViewById(R.id.textView_dust);

        String url="http://" + IP_ADDRESS + "/getjson.php";
        SelectDatabaseTask task = new SelectDatabaseTask(url, null);
        task.execute();


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

    /*private class GetIndoorAirData extends AsyncTask<String, Void, String> {

        ProgressDialog progressDialog;
        String errorString = null;

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

                textViewTemp.setText(errorString);
                textViewHumid.setText(errorString);
                textViewDust.setText(errorString);
            }
            else {

                mJsonString = result;
                ShowIndoorAirData();

            }
        }

    }*/
}
