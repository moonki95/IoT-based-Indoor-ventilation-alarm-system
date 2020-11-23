package com.example.Android;


import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;


import java.lang.*;


public class MainActivity extends AppCompatActivity {




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

//        StrictMode.enableDefaults();

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