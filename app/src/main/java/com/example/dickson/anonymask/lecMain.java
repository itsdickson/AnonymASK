package com.example.dickson.anonymask;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

public class lecMain extends AppCompatActivity {

    // reminder to add dropdown btn
    private final FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference ref = database.getReference();

    private int roomNum;
    private Button endBtn;
    private Button sortBtn;

    private ListView lv;
    private ArrayList<Message> messageList; // list of messages
    private ArrayList<Message> topList; // list of messages : to be used to sort by upvotes
    private ArrayList<String> keyList; // list of message keys from Firebase
    private CustomAdapter msgAdapter;
    private CustomAdapter topAdapter;
    private Toolbar lecTool;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lec_main);

        // Retrieving roomNum from lecStart
        Intent myIntent = getIntent();
        Bundle b = myIntent.getExtras();
        if (b != null) {
            roomNum = (int) b.get("roomNum");
        }
        lecTool = (Toolbar) findViewById(R.id.lecTool);
        setSupportActionBar(lecTool);

        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setTitle(Integer.toString(roomNum));

        TextView displayRoom = (TextView) findViewById(R.id.displayNum);
        displayRoom.setTextColor(Color.parseColor("#EAEDED"));
        displayRoom.setText(Integer.toString(roomNum));

        topList = new ArrayList<Message>();
        topAdapter = new CustomAdapter(topList, this);

//        sortBtn = (Button) findViewById(R.id.sortBtn);
//
//        // Sort Btn
//        sortBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                final BottomSheetDialog dialog = new BottomSheetDialog(lecMain.this);
//                View tempView = getLayoutInflater().inflate(R.layout.dialog, null);
//                dialog.setContentView(tempView);
//                ((View) tempView.getParent()).setBackgroundColor(getResources().getColor(R.color.cardview_shadow_start_color));
//
//                Button topBtn = (Button) tempView.findViewById(R.id.topBtn);
//                Button recBtn = (Button) tempView.findViewById(R.id.recBtn);
//
//                // if the Top Btn is clicked, the listview will be populated with toplist
//                topBtn.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        lv.setAdapter(topAdapter);
//                        dialog.dismiss();
//                    }
//                });
//
//                // if the Recent Btn is clicked, the listview will be populated with messageList
//                recBtn.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        lv.setAdapter(msgAdapter);
//                        dialog.dismiss();
//                    }
//                });
//
//                dialog.show();
//            }
//        });

        lv = (ListView) findViewById(R.id.list_of_message);
        displayMessage();

//        endBtn = (Button) findViewById(R.id.endBtn);
//
//        // End Session Btn
//        // Removes the entire room from Firebase and exit the page
//        endBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                final AlertDialog.Builder builder = new AlertDialog.Builder(lecMain.this);
//                builder.setMessage("End session?");
//
//                builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.dismiss();
//                    }
//                });
//                builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        ref.child("Room").child(Integer.toString(roomNum)).removeValue();
//                        finish();
//                    }
//                });
//
//
//                AlertDialog alert = builder.create();
//                alert.show();
//            }
//        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_lec, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()) {
            case R.id.sortBtn:
                final BottomSheetDialog dialog = new BottomSheetDialog(lecMain.this);
                View tempView = getLayoutInflater().inflate(R.layout.dialog, null);
                dialog.setContentView(tempView);
                ((View) tempView.getParent()).setBackgroundColor(getResources().getColor(R.color.cardview_shadow_start_color));

                Button topBtn = (Button) tempView.findViewById(R.id.topBtn);
                Button recBtn = (Button) tempView.findViewById(R.id.recBtn);

                // if the Top Btn is clicked, the listview will be populated with toplist
                topBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        lv.setAdapter(topAdapter);
                        dialog.dismiss();
                    }
                });

                // if the Recent Btn is clicked, the listview will be populated with messageList
                recBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        lv.setAdapter(msgAdapter);
                        dialog.dismiss();
                    }
                });

                dialog.show();

                return true;

            case R.id.endBtn:
                final AlertDialog.Builder builder = new AlertDialog.Builder(lecMain.this);
                builder.setMessage("End Session?");

                builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ref.child("Room").child(Integer.toString(roomNum)).removeValue();
                        finish();
                    }
                });


                AlertDialog alert = builder.create();
                alert.show();

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // Main method that displays the message on the ListView
    private void displayMessage() {
        messageList = new ArrayList<Message>();
        keyList = new ArrayList<String>();

        ref.child("Room").child(Integer.toString(roomNum)).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                // Takes the Message object created on Firebase and puts it into the ArrayLists
                Message temp = dataSnapshot.getValue(Message.class);
                messageList.add(0, temp);
                topList.add(0, temp);
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

                // When student upvotes any question, the topList will sort the list based on upvotes
                Collections.sort(topList, new Comparator<Message>(){public int compare(Message m1, Message m2){
                    return m1.getUpVote() > m2.getUpVote() ? -1 : m1.getUpVote() < m2.getUpVote() ? 1 : 0;}
                });

                topAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                // When moderator removes any question, it updates the arraylists accordingly
                int tempIdx = keyList.indexOf(dataSnapshot.getKey());
                keyList.remove(dataSnapshot.getKey());
                messageList.remove(tempIdx);

                topList.clear();
                for(Message m : messageList){
                    topList.add(m);
                }
                Collections.sort(topList, new Comparator<Message>(){public int compare(Message m1, Message m2){
                    return m1.getUpVote() > m2.getUpVote() ? -1 : m1.getUpVote() < m2.getUpVote() ? 1 : 0;}
                });

                topAdapter.notifyDataSetChanged();
                msgAdapter.notifyDataSetChanged();
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
    }
}