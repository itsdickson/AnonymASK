package com.example.dickson.anonymask;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
/**
 * Created by Junyang on 13/6/2017.
 */
class Message {
    private String messageText;
    private boolean checked;
    private int upVote;
    private String url;

    // Default Constructor
    public Message(){

    }

    public Message(String messageText, boolean checked, int upVote, String url) {
        this.messageText = messageText;
        this.checked = checked;
        this.upVote = upVote;
        this.url = url;
    }

    public String getMessageText(){
        return messageText;
    }

    public void setMessage(String messageText) {
        this.messageText = messageText;
    }

    public boolean isChecked(){
        return checked;
    }

    public void setChecked(boolean check){
        this.checked=check;
    }

    public void thumbsUp(){
        this.upVote++;
    }

    public void thumbsDown(){
        this.upVote--;
    }

    public int getUpVote(){
        return this.upVote;
    }

    public void setUpVote(int num){
        this.upVote = num;
    }

    public String getURL(){
        return this.url;
    }
}

public class CustomAdapter extends ArrayAdapter<Message> {

    private List<Message> messageList;
    private Context context;
    private boolean isVoted;

    public CustomAdapter(List<Message> messageList, Context context){
        super(context, R.layout.test1, messageList);
        this.messageList = messageList;
        this.context = context;
    }

    // ViewHolder for Student
    private static class stuHolder {
        public ImageButton stuImg;
        public TextView stuView1;
        public TextView stuView2;
        public CheckBox stuBox;
    }

    // ViewHolder for Lecturer
    private static class lecHolder {
        public ImageButton lecImg;
        public TextView lecView1;
        public TextView lecView2;
        public CheckBox lecBox;
    }

    // ViewHolder for Moderator
    private static class modHolder {
        public ImageButton modImg;
        public TextView modView1;
        public TextView modView2;
        public CheckBox modBox;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){

        View v = convertView;

        stuHolder stuHolder = new stuHolder();
        lecHolder lecHolder = new lecHolder();
        modHolder modHolder = new modHolder();

        if (convertView == null) {
//            Log.i("Log ", "convertView == null");

            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.test1, null);

            // Initialisation of all the different items for all the pages
            stuHolder.stuView1 = (TextView) v.findViewById(R.id.text1);
            stuHolder.stuView2 = (TextView) v.findViewById(R.id.text2);
            stuHolder.stuBox = (CheckBox) v.findViewById(R.id.check1);
            stuHolder.stuImg = (ImageButton) v.findViewById(R.id.uploadImg);

            lecHolder.lecView1 = (TextView) v.findViewById(R.id.text1);
            lecHolder.lecView2 = (TextView) v.findViewById(R.id.text2);
            lecHolder.lecBox = (CheckBox) v.findViewById(R.id.check1);
            lecHolder.lecImg = (ImageButton) v.findViewById(R.id.uploadImg);

            modHolder.modView1 = (TextView) v.findViewById(R.id.text1);
            modHolder.modView2 = (TextView) v.findViewById(R.id.text2);
            modHolder.modBox = (CheckBox) v.findViewById(R.id.check1);
            modHolder.modImg = (ImageButton) v.findViewById(R.id.uploadImg);

            if (context.toString().contains("stuMain")){
                stuHolder.stuBox.setOnCheckedChangeListener((stuMain) context);
            }

            else if (context.toString().contains("modMain")){
                stuHolder.stuBox.setOnCheckedChangeListener((modMain) context);
            }
        }

        else {
//            Log.i("Log ", "convertView != null");
            if (context.toString().contains("stuMain")){
                stuHolder = (stuHolder) v.getTag();
            }

            else if (context.toString().contains("lecMain")){
                lecHolder = (lecHolder) v.getTag();
            }

            else if (context.toString().contains("modMain")){
                modHolder = (modHolder) v.getTag();
            }
        }

        final Message m = messageList.get(position);

        if (context.toString().contains("stuMain")){
            v.setTag(stuHolder);
            stuHolder.stuView1.setText(m.getMessageText());
            stuHolder.stuView2.setText("" + m.getUpVote());
            System.out.println("upvotes = " + m.getUpVote());
            stuHolder.stuBox.setButtonDrawable(R.drawable.customthumbsup);
//            stuHolder.stuBox.setChecked(isVoted);
            System.out.println(stuHolder.stuBox.isChecked());

            if (stuHolder.stuBox.isChecked()){
                stuHolder.stuView2.setText("" + m.getUpVote());
                System.out.println("checked = " + m.getUpVote());
            }
            else {
                stuHolder.stuView2.setText("" + m.getUpVote());
                System.out.println("unchecked = " + m.getUpVote());
            }

            if (m.getURL().isEmpty()){
                // stops showing the image button when there's no image url in the Message object
                stuHolder.stuImg.setVisibility(View.INVISIBLE);
            }

            else {
                stuHolder.stuImg.setVisibility(View.VISIBLE);

                // enlarging of the image if you click on it
                stuHolder.stuImg.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Dialog settingsDialog = new Dialog(context);
                        settingsDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
                        LayoutInflater li = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        settingsDialog.setContentView(li.inflate(R.layout.image_view, null), new RelativeLayout.LayoutParams(900, 1100));
                        ImageView camPhoto = (ImageView) settingsDialog.findViewById(R.id.camView);
//                        camPhoto.setRotation(camPhoto.getRotation() + 90);
                        Glide.with(context).load(m.getURL()).into(camPhoto);
                        settingsDialog.show();
                    }
                });
            }

            stuHolder.stuBox.setTag(m);
        }

        else if (context.toString().contains("lecMain")){
            v.setTag(lecHolder);

            // Removal of the checkbox
            lecHolder.lecBox.setButtonDrawable(null);
            lecHolder.lecView2.setPadding(50, 0, 0, 0);

            lecHolder.lecView1.setText(m.getMessageText());
            lecHolder.lecView2.setText("" + m.getUpVote());

            if (m.getURL().isEmpty()){
                // stops showing the image button when there's no image url in the Message object
                lecHolder.lecImg.setVisibility(View.INVISIBLE);
            }

            else {
                lecHolder.lecImg.setVisibility(View.VISIBLE);

                // enlarging of the image if you click on it
                lecHolder.lecImg.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Dialog settingsDialog = new Dialog(context);
                        settingsDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
                        LayoutInflater li = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        settingsDialog.setContentView(li.inflate(R.layout.image_view, null), new RelativeLayout.LayoutParams(900, 1100));
                        ImageView camPhoto = (ImageView) settingsDialog.findViewById(R.id.camView);
//                        camPhoto.setRotation(camPhoto.getRotation() + 90);
                        Glide.with(context).load(m.getURL()).into(camPhoto);
                        settingsDialog.show();
                    }
                });
            }

        }

        else if (context.toString().contains("modMain")){
            v.setTag(modHolder);
            modHolder.modView1.setText(m.getMessageText());
            modHolder.modView2.setText("" + m.getUpVote());
            modHolder.modBox.setChecked(m.isChecked());
            System.out.println(modHolder.modBox.isChecked());
            modHolder.modBox.setTag(m);

            if (m.getURL().isEmpty()){
                // stops showing the image button when there's no image url in the Message object
                modHolder.modImg.setVisibility(View.INVISIBLE);
            }

            else {
                modHolder.modImg.setVisibility(View.VISIBLE);

                // enlarging of the image if you click on it
                modHolder.modImg.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Dialog settingsDialog = new Dialog(context);
                        settingsDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
                        LayoutInflater li = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        settingsDialog.setContentView(li.inflate(R.layout.image_view, null), new RelativeLayout.LayoutParams(900, 1100));
                        ImageView camPhoto = (ImageView) settingsDialog.findViewById(R.id.camView);
//                        camPhoto.setRotation(camPhoto.getRotation() + 90);
                        Glide.with(context).load(m.getURL()).into(camPhoto);
                        settingsDialog.show();
                    }
                });
            }
        }

        return v;
    }
}



