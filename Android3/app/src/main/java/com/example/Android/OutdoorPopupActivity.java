package com.example.Android;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

public class OutdoorPopupActivity extends Activity {

    private Spinner spinnerSido, spinnerGungu;
    private ArrayAdapter<String> arrayAdapter;
    
    private String[] data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //타이틀바 없애기
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_outdoor_popup);

        //스피너
        spinnerSido=(Spinner)findViewById(R.id.spinner_sido);
        arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, (String[])getResources().getStringArray(R.array.spinner_region));
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSido.setAdapter(arrayAdapter);

        spinnerGungu=(Spinner)findViewById(R.id.spinner_gungu);
        initAddressSpinner();



        //데이터 가져오기
        Intent intent = getIntent();
        data = intent.getStringArrayExtra("data");

    }

    //확인 버튼 클릭
    public void mOnClose(View v){
        String[] sigungu = new String[2];
        if(spinnerSido.getSelectedItem().equals("")){
            sigungu[0]=data[0];
            sigungu[1]=data[1];
        }
        else {
            sigungu[0] = spinnerSido.getSelectedItem().toString();
            sigungu[1] = spinnerGungu.getSelectedItem().toString();
        }

        //데이터 전달하기
        Intent intent = new Intent();
        intent.putExtra("result", sigungu);
        setResult(RESULT_OK, intent);

        //액티비티(팝업) 닫기
        finish();
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
}
