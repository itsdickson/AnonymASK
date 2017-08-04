package com.example.dickson.anonymask;

import android.app.ActionBar;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CheckedTextView;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.facebook.internal.Mutable;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;

public class stuMain extends AppCompatActivity implements android.widget.CompoundButton.OnCheckedChangeListener {

    private int roomNum;
    private TextView askTxt;
    private Button viewBtn;
    private EditText qnET;
    private FloatingActionButton fab;

    private ImageView camPhoto;
    private ImageButton activateCam;
    private ImageButton getImage;
    private static final int GALLERY_INTENT = 2;
    private static final int CAMERA_REQUEST_CODE = 1;
    private StorageReference mStorage;
    private ProgressDialog mProgressDialog;
    private String mCurrentPhotoPath;
    private static String photoURL = "";

    private final FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference ref = database.getReference();

    private final ArrayList<String> profList = new ArrayList<String>();

    private ListView lv;
    private ArrayList<Message> messageList; // list of messages
    private ArrayList<String> keyList; // list of message keys from Firebase
    private CustomAdapter msgAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stu_main);

        // Retrieving roomNum from lecStart
        Intent myIntent = getIntent();
        Bundle b = myIntent.getExtras();
        if (b != null) {
            roomNum = (int) b.get("roomNum");
        }

        // Initialising of all the buttons, edittexts, textviews and etc
        fab = (FloatingActionButton) findViewById(R.id.fab);
        askTxt = (TextView) findViewById(R.id.askTxt);
        activateCam = (ImageButton) findViewById(R.id.cameraBtn);
        getImage = (ImageButton) findViewById(R.id.galleryBtn);
        qnET = (EditText) findViewById(R.id.qnET);
        viewBtn = (Button) findViewById(R.id.viewBtn);

        mStorage = FirebaseStorage.getInstance().getReference();
        mProgressDialog = new ProgressDialog(this);

        lv = (ListView) findViewById(R.id.list_of_message);

        displayMessage();

        // Initializing profanities
        initializeProf(profList);

        // Action when clicking the "Camera" Button
        activateCam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }
        });

        // Action when clicking the "Gallery" Button
        getImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, GALLERY_INTENT);
            }
        });

        // Action of "Send" Button
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Message text;

                // Creates a Message object without a url when url is not created
                if (photoURL.isEmpty()) {
                    text = new Message(qnET.getText().toString(), false, 0, "");
//                    System.out.println("text = " + text.getURL() + ", " + text.getMessageText());
                }

                // Creates a Message object with the created url
                else{
                    // if time permits, remind user to write a text if they send a image without text
                    text = new Message(qnET.getText().toString(), false, 0, photoURL);
//                    System.out.println("text = " + text.getURL() + ", " + text.getMessageText());
                }

                // Splits the question based on their spaces so as to check if profanities exist
                // for each word
                String temp = qnET.getText().toString();
                String [] separated = temp.split(" ");
//                String[] words = temp.replaceAll("[^a-zA-Z ]", "").toLowerCase().split("\\s+");

                // This checks if there's profanities in the sent message, and removes it if it does,
                // and pushes the Message object to Firebase if it doesn't.
                for (int i=0; i<separated.length; i++){
                    if (profList.contains(separated[i])){
//                        Log.i("Log: ", "Profanities Detected!");
                        break;
                    }

                    else if (i == separated.length-1){
                        ref.child("Room").child(Integer.toString(roomNum)).push().setValue(text);
                    }
                }

                qnET.setText("");
                viewBtn.setText("");
                photoURL = "";
            }
        });
    }
//    public String removePunctuations(String s) {
//        String res = "";
//        for (Character c : s.toCharArray()) {
//            if(Character.isLetter(c))
//                res += c;
//        }
//        return res;
//    }

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
                // Every time there's a change in the childs (upVotes, etc), update the adapter
                // so that it updates the listview
                msgAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                // When moderator removes any question, it updates the arraylists accordingly
                int tempIdx = keyList.indexOf(dataSnapshot.getKey());
                keyList.remove(dataSnapshot.getKey());
                messageList.remove(tempIdx);
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
    }

    // This method overrides the checkbox's behaviours
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        final int pos = lv.getPositionForView(buttonView);
        final Message temp = messageList.get(pos);

        if (pos != ListView.INVALID_POSITION){
            // When the checkbox is checked/unchecked, the corresponding Message object
            // will update the upvote count at Firebase
            if (isChecked){
                final AlertDialog.Builder builder = new AlertDialog.Builder(stuMain.this);
                builder.setMessage("Upvote?");
                builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        displayMessage();
                        dialog.dismiss();
                    }
                });
                builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        DatabaseReference postRef = ref.child("Room").child(Integer.toString(roomNum)).child(keyList.get(pos));

                        postRef.runTransaction(new Transaction.Handler() {
                            @Override
                            public Transaction.Result doTransaction(MutableData mutableData) {
                                Message m = mutableData.getValue(Message.class);

                                if (m == null) {
                                    return Transaction.success(mutableData);
                                } else {
                                    System.out.println("UPVOTED!");
                                    m.thumbsUp();
                                    temp.thumbsUp();
                                }
                                mutableData.setValue(m);

                                return Transaction.success(mutableData);
                            }

                            @Override
                            public void onComplete(DatabaseError databaseError, boolean b,
                                                   DataSnapshot dataSnapshot) {
                                // Transaction completed
//                                Log.d("TAG", "postTransaction:onComplete:" + databaseError);

                                msgAdapter.notifyDataSetChanged();
                            }
                        });
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();
            }
            else {
                DatabaseReference postRef = ref.child("Room").child(Integer.toString(roomNum)).child(keyList.get(pos));

                postRef.runTransaction(new Transaction.Handler(){
                    @Override
                    public Transaction.Result doTransaction(MutableData mutableData){
                        Message m = mutableData.getValue(Message.class);
                        System.out.println(mutableData);
                        if (m == null) {
                            return Transaction.success(mutableData);
                        } else {
                            System.out.println("DOWNVOTED!");
                            m.thumbsDown();
                            temp.thumbsDown();
                        }
                        mutableData.setValue(m);
                        return Transaction.success(mutableData);
                    }

                    @Override
                    public void onComplete(DatabaseError databaseError, boolean b,
                                           DataSnapshot dataSnapshot) {
                        // Transaction completed
//                        Log.d("TAG", "postTransaction:onComplete:" + databaseError);
                        msgAdapter.notifyDataSetChanged();
                    }
                });
            }
        }
    }

    // this method creates an image 'file' for it to be stored
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    // main method when clicking "camera" button
    private void dispatchTakePictureIntent(){
        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);

        if (intent.resolveActivity(getPackageManager()) != null){
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex){
//                Log.i("Error Found: ", "Error creating file");
            }

            if (photoFile != null){
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.android.fileprovider",
                        photoFile);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(intent, CAMERA_REQUEST_CODE);
            }
        }
    }

    // checks if gallery or the camera function is triggered, and do the according activities
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_INTENT && resultCode == RESULT_OK) {

            mProgressDialog.setMessage("Uploading...");
            mProgressDialog.show();

            Uri uri = data.getData();

            StorageReference filepath = mStorage.child("Photos").child(uri.getLastPathSegment());

            // Inputs the image file into Firebase Storage
            filepath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Toast.makeText(stuMain.this, "Upload Done!", Toast.LENGTH_LONG).show();
                    mProgressDialog.dismiss();

                    photoURL = taskSnapshot.getDownloadUrl().toString();

                    viewBtn.setText("View Image");

                    viewBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (!photoURL.isEmpty()){
                                Dialog settingsDialog = new Dialog(stuMain.this);
                                settingsDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
                                settingsDialog.setContentView(getLayoutInflater().inflate(R.layout.image_view, null), new RelativeLayout.LayoutParams(900, 1100));
                                camPhoto = (ImageView) settingsDialog.findViewById(R.id.camView);
                                camPhoto.setRotation(camPhoto.getRotation() + 90);
                                Glide.with(stuMain.this).load(photoURL).into(camPhoto);
                                settingsDialog.show();
                            }
                        }

                    });
                }
            });
        }

        else if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK) {

            Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            File f = new File(mCurrentPhotoPath);
            Uri uri = Uri.fromFile(f);
            mediaScanIntent.setData(uri);
            this.sendBroadcast(mediaScanIntent);

            StorageReference filepath = mStorage.child("Photos/").child(uri.getLastPathSegment());

            // Inputs the image file into Firebase Storage
            filepath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Toast.makeText(stuMain.this, "Upload Done!", Toast.LENGTH_LONG).show();

                    photoURL = taskSnapshot.getDownloadUrl().toString();

                    viewBtn.setText("View Image");

                    viewBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (!photoURL.isEmpty()){
                                Dialog settingsDialog = new Dialog(stuMain.this);
                                settingsDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
                                settingsDialog.setContentView(getLayoutInflater().inflate(R.layout.image_view, null), new RelativeLayout.LayoutParams(900, 1100));
                                camPhoto = (ImageView) settingsDialog.findViewById(R.id.camView);
                                camPhoto.setRotation(camPhoto.getRotation() + 90);
                                Glide.with(stuMain.this).load(photoURL).into(camPhoto);
                                settingsDialog.show();
                            }
                        }

                    });
                }
            });
        }
    }

    // Initialising the lists of profanities
    private void initializeProf(ArrayList<String> array){
        array.add("fuck");
        array.add("fucking");
        array.add("fking");
        array.add("fucker");
        array.add("fker");
        array.add("cb");
        array.add("ccb");
        array.add("dick");
        array.add("cock");
        array.add("fk");
        array.add("pussy");
        array.add("cunt");
        array.add("bitch");
        array.add("fag");
        array.add("asshole");
        array.add("slut");
        array.add("ass");
        array.add("bastard");
        array.add("douche");
        array.add("faggot");
        array.add("fck");
        array.add("kanina");
        array.add("knn");
        array.add("knnbcb");
        array.add("knncb");
        array.add("shit");
        array.add("fuckie");
        array.add("");
    }
}

