package com.example.user.mynavigation;

import android.app.Activity;
import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by user on 2017-11-07.
 */

public class Tab_Write_Form extends Activity{

    Button b1;
    Button b2;
    EditText e1;
    EditText e2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tab1_write_form);

        b1 =  (Button)findViewById(R.id.b1);
        b2 =  (Button)findViewById(R.id.b2);
        e1 =  (EditText)findViewById(R.id.e1);
        e2 =  (EditText)findViewById(R.id.e2);

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(Tab_Write_Form.this, "여기서 스프링통신", Toast.LENGTH_SHORT).show();
            }
        });
        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
