package com.example.dickson.anonymask;

import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    private Button stuBtn;
    private Button lecBtn;
    private Button modBtn;
    private TextView startTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Typeface custom_font = Typeface.createFromAsset(getAssets(),  "verdana.ttf");

        // Initialising of all the buttons, edittexts, textviews and etc
        stuBtn = (Button) findViewById(R.id.stuBtn);
        modBtn = (Button) findViewById(R.id.modBtn);
        lecBtn = (Button) findViewById(R.id.lecBtn);
        startTxt = (TextView) findViewById(R.id.startTxt);

        // Setting a custom font on all the buttons
        stuBtn.setTypeface(custom_font);
        modBtn.setTypeface(custom_font);
        lecBtn.setTypeface(custom_font);
        startTxt.setTypeface(custom_font);

        // This moves from the main page to stuStart
        stuBtn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view){
                Intent myIntent = new Intent(MainActivity.this, stuStart.class);
                startActivity(myIntent);
            }
        });

        // This moves from the main page to lecStart
        lecBtn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view){
                Intent myIntent = new Intent(MainActivity.this, lecStart.class);
                startActivity(myIntent);
            }
        });

        // This moves from the main page to modStart
        modBtn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view){
                Intent myIntent = new Intent(MainActivity.this, modStart.class);
                startActivity(myIntent);
            }
        });
    }
}


