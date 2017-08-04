package com.example.dickson.anonymask;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class modStart extends AppCompatActivity {

    private TextView codeTxt;
    private EditText codeET;
    private Button enterBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mod_start);

        codeTxt = (TextView) findViewById(R.id.codeTxt);
        codeET = (EditText) findViewById(R.id.codeET);
        enterBtn = (Button) findViewById(R.id.enterBtn);

        // clicking the enter btn will bring you to the next page, modMain based on the room number
        enterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = codeET.getText().toString();
                int roomNum = Integer.parseInt(message);

                // This brings over the room number to modMain
                Intent myIntent = new Intent(modStart.this, modMain.class);
                myIntent.putExtra("roomNum", roomNum);
                startActivity(myIntent);
            }
        });
    }
}
