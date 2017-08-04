package com.example.dickson.anonymask;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class stuStart extends AppCompatActivity {

    private TextView codeTxt;
    private EditText codeET;
    private Button enterBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stu_start);

        Typeface custom_font = Typeface.createFromAsset(getAssets(),  "impact.ttf");
        codeTxt = (TextView) findViewById(R.id.codeTxt);
        codeTxt.setTypeface(custom_font);

        codeET = (EditText) findViewById(R.id.codeET);
        enterBtn = (Button) findViewById(R.id.enterBtn);

        // clicking the enter btn will bring you to the next page, stuMain based on the room number
        enterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = codeET.getText().toString();
                int roomNum = Integer.parseInt(message);

                // This brings over the room number to stuMain
                Intent myIntent = new Intent(stuStart.this, stuMain.class);
                myIntent.putExtra("roomNum", roomNum);
                startActivity(myIntent);
            }
        });
    }
}
