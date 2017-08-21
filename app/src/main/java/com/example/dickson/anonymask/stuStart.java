package com.example.dickson.anonymask;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class stuStart extends AppCompatActivity {

    private TextView codeTxt;
    private EditText codeET;
    private Button enterBtn;
    private Toolbar stuTool;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stu_start);

        stuTool = (Toolbar) findViewById(R.id.stuTool);
        setSupportActionBar(stuTool);

        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);


        codeTxt = (TextView) findViewById(R.id.codeTxt);
//        Typeface custom_font = Typeface.createFromAsset(getAssets(),  "impact.ttf");
//        codeTxt.setTypeface(custom_font);

        codeET = (EditText) findViewById(R.id.codeET);
        enterBtn = (Button) findViewById(R.id.enterBtn);

        codeET.setRawInputType(InputType.TYPE_CLASS_NUMBER);

        codeET.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN){
                    switch (keyCode){
                        case KeyEvent.KEYCODE_ENTER:
                            String message = codeET.getText().toString();

                            if (message.isEmpty()){
//                                AlertDialog.Builder builder = new AlertDialog.Builder(stuStart.this);
//                                builder.setMessage("Please input a valid room number!");
//                                builder.setPositiveButton("ok", new DialogInterface.OnClickListener(){
//                                    @Override
//                                    public void onClick(DialogInterface dialog, int which){
//                                        dialog.dismiss();
//                                    }
//                                });
//                                builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
//                                    @Override
//                                    public void onClick(DialogInterface dialog, int which) {
//                                    }
//                                });
                                Toast.makeText(stuStart.this, "Please input a valid room number!", Toast.LENGTH_SHORT).show();
                            }

                            else {
                                // This brings over the room number to stuMain
                                int roomNum = Integer.parseInt(message);
                                Intent myIntent = new Intent(stuStart.this, stuMain.class);
                                myIntent.putExtra("roomNum", roomNum);
                                startActivity(myIntent);

                                codeET.setText("");
                            }
                            return true;

                        default:
                            break;
                    }
                }
                return false;
            }
        });

        // clicking the enter btn will bring you to the next page, stuMain based on the room number
        enterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = codeET.getText().toString();

                if (message.isEmpty()){
//                                AlertDialog.Builder builder = new AlertDialog.Builder(stuStart.this);
//                                builder.setMessage("Please input a valid room number!");
//                                builder.setPositiveButton("ok", new DialogInterface.OnClickListener(){
//                                    @Override
//                                    public void onClick(DialogInterface dialog, int which){
//                                        dialog.dismiss();
//                                    }
//                                });
//                                builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
//                                    @Override
//                                    public void onClick(DialogInterface dialog, int which) {
//                                    }
//                                });
                    Toast.makeText(stuStart.this, "Please input a valid room number!", Toast.LENGTH_SHORT).show();
                }

                else {
                    // This brings over the room number to stuMain
                    int roomNum = Integer.parseInt(message);
                    Intent myIntent = new Intent(stuStart.this, stuMain.class);
                    myIntent.putExtra("roomNum", roomNum);
                    startActivity(myIntent);

                    codeET.setText("");
                }
            }
        });
    }
}
