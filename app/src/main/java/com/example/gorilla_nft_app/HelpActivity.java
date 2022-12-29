package com.example.gorilla_nft_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gorilla_nft_app.Model.Users;
import com.example.gorilla_nft_app.Prevalent.Prevalent;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import io.paperdb.Paper;

public class HelpActivity extends AppCompatActivity {

    private Button backBtn, submitBtn;
    private TextView emailText, usernameText;
    private EditText subjectInput, messageInput;
    private String subject,message, saveCurrentDate, saveCurrentTime, messageUniqueKey, username, emailData;
    DatabaseReference RootRef;
    Animation scaleDown, scaleUp;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        progressDialog = new ProgressDialog(this);

        //database reference
        Paper.init(this);
        username = Paper.book().read(Prevalent.UsernameKeyValue);
        RootRef = FirebaseDatabase.getInstance("https://gorillanft-d65f0-default-rtdb.europe-west1.firebasedatabase.app/").getReference();
        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child("Users").child(username).exists()){
                    Users usernameData = snapshot.child("Users").child(username).getValue(Users.class);
                    emailData = snapshot.child("Users").child(username).child("email").getValue().toString();
                    usernameText.setText(usernameData.getUsername());
                    emailText.setText(emailData);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        backBtn = (Button) findViewById(R.id.app__activity_help_back_btn);
        scaleDown = AnimationUtils.loadAnimation(this,R.anim.scale_down);
        scaleUp = AnimationUtils.loadAnimation(this,R.anim.scale_up);
        backBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    backBtn.startAnimation(scaleDown);
                }else if(event.getAction() == MotionEvent.ACTION_UP){
                    backBtn.startAnimation(scaleUp);

                    Intent intent = new Intent(HelpActivity.this,HomeActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }
                return true;
            }
        });


        submitBtn = (Button) findViewById(R.id.app__activity_help_submit_btn);
        emailText = (TextView) findViewById(R.id.app__activity_help_email_text);
        usernameText = (TextView) findViewById(R.id.app__activity_help_username_text);
        subjectInput = (EditText) findViewById(R.id.app__activity_help_subject_input);
        messageInput = (EditText) findViewById(R.id.app__activity_help_message_input);

        submitBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    submitBtn.startAnimation(scaleDown);
                }else if(event.getAction() == MotionEvent.ACTION_UP){
                    submitBtn.startAnimation(scaleUp);

                    submitForm();

                }
                return true;
            }
        });

    }

    private void submitForm() {
        subject = subjectInput.getText().toString().trim();
        message = messageInput.getText().toString();

        if(TextUtils.isEmpty(subject)){
            createToast("Uncompleted form");
        }

        if(TextUtils.isEmpty(message)){
            createToast("Uncompleted form");
        }

        if(!TextUtils.isEmpty(message) && !TextUtils.isEmpty(subject)){

            progressDialog.setTitle("Creating Account");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.setMessage("Please wait...");
            progressDialog.show();

            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat currentDate = new SimpleDateFormat("dd MM yyyy");
            saveCurrentDate = currentDate.format(calendar.getTime());
            SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
            saveCurrentTime = currentTime.format(calendar.getTime());

            messageUniqueKey = username + " " + saveCurrentDate + saveCurrentTime;

            RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.child("Users").child(username).exists()){
                        Map<String, Object> messageDataMap = new HashMap<>();
                        messageDataMap.put("email", emailData);
                        messageDataMap.put("username", username);
                        messageDataMap.put("data", saveCurrentDate);
                        messageDataMap.put("time", saveCurrentTime);
                        messageDataMap.put("subject", subject);
                        messageDataMap.put("message", message);
                        messageDataMap.put("id", messageUniqueKey);

                        RootRef.child("Inquiries").child(messageUniqueKey).updateChildren(messageDataMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    progressDialog.dismiss();
                                    Handler handler = new Handler();
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            startActivity(new Intent(HelpActivity.this, HomeActivity.class));
                                            finish();
                                        }
                                    },500);
                                    createToast("Message submitted");
                                }
                                else{
                                    progressDialog.dismiss();
                                    createToast("Error");
                                }
                            }
                        });
                    }
                    else{
                        progressDialog.dismiss();
                        createToast("Error");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    //front-end methods
    public void createToast(String someText){
        Toast toast = new Toast(getApplicationContext());
        toast.setGravity(Gravity.CENTER,0,700);

        Drawable d = ContextCompat.getDrawable(getApplicationContext(), R.drawable.toast_shape);

        TextView toastText = new TextView(HelpActivity.this);
        toastText.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.toast_color));
        toastText.setBackground(d);
        toastText.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.error_red2));
        toastText.setPadding(35,20,35,20);
        toastText.setText(someText);
        toastText.setTextSize(14);

        toast.setView(toastText);
        toast.show();
    }
}