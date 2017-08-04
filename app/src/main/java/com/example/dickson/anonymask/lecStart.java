package com.example.dickson.anonymask;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Random;

public class lecStart extends AppCompatActivity {
    private int rand;
    private Button genBtn;
    private TextView numText;
    private ArrayList<Integer> roomArr = new ArrayList<Integer>(); // array list of rooms when room is created

    private final FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference ref = database.getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lec_start);

        genBtn = (Button) findViewById(R.id.genBtn);
        numText = (TextView) findViewById(R.id.numText);
        numText.setText("");

        ref.child("Room").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                // When room is "created" in Firebase, add it inside the roomArr
                roomArr.add(Integer.parseInt(dataSnapshot.getKey()));
//                for (int i : roomArr){
//                    System.out.println("i = " + i);
//                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                // When lecturer ends the session, remove the room number from roomArr
                roomArr.remove((Integer) Integer.parseInt(dataSnapshot.getKey()));
//                for (int i : roomArr){
//                    System.out.println("i = " + i);
//                }
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        // this button generates a unique random room number and enter to the next page, lecMain
        genBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                int roomNum = generateRoomNum();
                System.out.println("current room num = " + roomNum);
                while (roomArr.contains(roomNum)){
                    System.out.println("roomNum " + roomNum + " exists!");
                    roomNum = generateRoomNum();
                }

                numText.setText(Integer.toString(roomNum));

                // This brings over the room number to lecMain
                Intent myIntent = new Intent(lecStart.this, lecMain.class);
                myIntent.putExtra("roomNum", roomNum);
                startActivity(myIntent);
            }
        });
    }

    // this method generates a random room number
    public int generateRoomNum() {
        rand = (int) (Math.random() * 9000) + 1000;
        return rand;
    }
}
