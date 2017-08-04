package com.example.dickson.anonymask;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CheckedTextView;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

public class modMain extends AppCompatActivity implements android.widget.CompoundButton.OnCheckedChangeListener{
    private final FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference ref = database.getReference();

    private int roomNum;
    private Button selectBtn;
    private Button removeBtn;

    private ListView lv;
    private ArrayList<Message> messageList; // list of messages
    private ArrayList<String> keyList; // list of message keys from Firebase
    private CustomAdapter msgAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mod_main);

        // Retrieving roomNum from modStart
        Intent myIntent = getIntent();
        Bundle b = myIntent.getExtras();
        if (b != null) {
            roomNum = (int) b.get("roomNum");
        }

        lv = (ListView) findViewById(R.id.list_of_message);
        displayMessage();
    }

    // Main method that displays the message on the ListView
    private void displayMessage(){
        messageList = new ArrayList<Message>();
        keyList = new ArrayList<String>();

        ref.child("Room").child(Integer.toString(roomNum)).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                // Takes the Message object created on Firebase and puts it into the ArrayLists
                Message temp = dataSnapshot.getValue(Message.class);
                messageList.add(0, temp);
                keyList.add(0, dataSnapshot.getKey());
                msgAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                // When student upvotes any question, have to update the messageList, adapter,
                // and listview accordingly
                int tempIdx = keyList.indexOf(dataSnapshot.getKey());
                Integer value = (int) (long) dataSnapshot.child("upVote").getValue();
                messageList.get(tempIdx).setUpVote(value);
                msgAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                // When moderator removes the question, it updates the adapter and listview
                msgAdapter.notifyDataSetChanged();

                // This checks when the lecturer ends the session (removes all questions)
                // Once detected, the user will automatically move out of the room.
                // It may also be triggered when moderator removes the only question in the room
                // or removes every question.
                ref.child("Room").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (!dataSnapshot.hasChild(Integer.toString(roomNum))){
                            System.out.println("Session Ended!");
                            finish();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        msgAdapter = new CustomAdapter(messageList, this);

        lv.setAdapter(msgAdapter);

        selectBtn = (Button) findViewById(R.id.selectBtn);
        removeBtn = (Button) findViewById(R.id.removeBtn);

        // Select All Button (To Be Removed)
        // Sets all messages to checked
        selectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for (int i=0; i<keyList.size(); i++){
                    ref.child("Room").child(Integer.toString(roomNum)).child(keyList.get(i)).child("checked").setValue(true);
                    Message m = messageList.get(i);
                    m.setChecked(true);
                }
                msgAdapter.notifyDataSetChanged();
            }
        });

        // Remove Button
        // Checks through the Firebase for checked Message objects and remove them from both
        // Firebase Database and both ArrayLists
        ref.child("Room").child(Integer.toString(roomNum)).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {
                removeBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        for(DataSnapshot player: dataSnapshot.getChildren()){
                            if ((player.child("checked").getValue()).equals(true)){
                                String tempKey = player.getKey();
                                int tempIndex = keyList.indexOf(tempKey);
                                System.out.println(tempIndex);
                                keyList.remove(tempIndex);
                                msgAdapter.remove(messageList.get(tempIndex));
                                msgAdapter.notifyDataSetChanged();

//                                Log.i("testing: ", "PASS!");
                                player.getRef().removeValue();

                                for (int i=0; i<msgAdapter.getCount(); i++){
                                    msgAdapter.getItem(i).setChecked(false);
                                }
                            }
                        }
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    // This method overrides the checkbox's behaviours
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        final int pos = lv.getPositionForView(buttonView);

        if (pos != ListView.INVALID_POSITION){
            Message m = messageList.get(pos);

            // When the checkbox is checked/unchecked, the corresponding Message object
            // will update the boolean "checked" to the proper boolean value
            m.setChecked(isChecked);
            if (m.isChecked()){
                ref.child("Room").child(Integer.toString(roomNum)).child(keyList.get(pos)).child("checked").setValue(true);
            }

            else {
                ref.child("Room").child(Integer.toString(roomNum)).child(keyList.get(pos)).child("checked").setValue(false);
            }

            msgAdapter.notifyDataSetChanged();
        }
    }
}

