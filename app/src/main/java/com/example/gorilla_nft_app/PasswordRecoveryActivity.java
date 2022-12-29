package com.example.gorilla_nft_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.SmsManager;
import android.text.Editable;
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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rey.material.widget.CheckBox;

import java.util.Locale;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class PasswordRecoveryActivity extends AppCompatActivity {

    private Button goBackBtn, recoveryBtn;
    private EditText usernameInput;
    private CheckBox emailCheck, smsCheck;
    private DatabaseReference RootRef;
    private ProgressDialog progressDialog;
    private String username;
    Boolean passwordRecovered;
    Animation scaleDown, scaleUp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_recovery);

        goBackBtn = (Button) findViewById(R.id.app__password_recovery_back_btn);
        goBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PasswordRecoveryActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });

        usernameInput = (EditText) findViewById(R.id.app__recovery_password_username_input);
        emailCheck = (CheckBox) findViewById(R.id.app__password_recovery_email_checkbox);
        smsCheck = (CheckBox) findViewById(R.id.app__password_recovery_sms_checkbox);
        recoveryBtn = (Button) findViewById(R.id.app__recovery_password_btn);
        progressDialog = new ProgressDialog(this);

        //buttons active effect
        scaleDown = AnimationUtils.loadAnimation(this,R.anim.scale_down);
        scaleUp = AnimationUtils.loadAnimation(this,R.anim.scale_up);

        //animation and intent
        recoveryBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    recoveryBtn.startAnimation(scaleDown);
                }else if(event.getAction() == MotionEvent.ACTION_UP){
                    recoveryBtn.startAnimation(scaleUp);

                    recoverPassword();

                }
                return true;
            }
        });
    }

    private void recoverPassword() {
        username = usernameInput.getText().toString().toLowerCase(Locale.ROOT).trim();
        RootRef = FirebaseDatabase.getInstance("https://gorillanft-d65f0-default-rtdb.europe-west1.firebasedatabase.app/").getReference();

        if(TextUtils.isEmpty(username)){
            createToast("Please type your username");
            changeLineColorEditText(usernameInput, ContextCompat.getColor(getApplicationContext(), R.color.error_red));
        }
        else{
            changeLineColorEditText(usernameInput, ContextCompat.getColor(getApplicationContext(), R.color.almost_black_text));

            RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.child("Users").child(username).exists()){
                        changeLineColorEditText(usernameInput, ContextCompat.getColor(getApplicationContext(), R.color.almost_black_text));

                        //send email and sms here
                        if(smsCheck.isChecked()){
                            ActivityCompat.requestPermissions(PasswordRecoveryActivity.this, new String[]{Manifest.permission.SEND_SMS,Manifest.permission.READ_SMS}, PackageManager.PERMISSION_GRANTED);

                            String phoneNumber = snapshot.child("Users").child(username).child("phone").getValue().toString();
                            SmsManager smsManager = SmsManager.getDefault();
                            String smsPassword = snapshot.child("Users").child(username).child("password").getValue().toString();

                            String messageToSend = "Your password is: " + smsPassword;

                            smsManager.sendTextMessage(phoneNumber,null,messageToSend, null, null);

                            createToast("We send you an SMS");

                            progressDialog.setMessage("Recovering your password...");
                            progressDialog.setCanceledOnTouchOutside(false);
                            progressDialog.show();
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    startActivity(new Intent(PasswordRecoveryActivity.this, MainActivity.class));
                                    finish();
                                }
                            },700);
                        }

                        if(emailCheck.isChecked()){

                            String receiverEmail = snapshot.child("Users").child(username).child("email").getValue().toString();
                            String senderEmail = "gorillanft.service@gmail.com";
                            String passwordUser =  snapshot.child("Users").child(username).child("password").getValue().toString();
                            String p_e = getResources().getString(R.string.gorilla_nft_service);
                            String subjectEmail = "Gorilla NFT - Password recovery Service";
                            String messageToSend = "Hello " + username + "\n" +
                                    "\n" +
                                    "Your password is: " + passwordUser;

                            try {
                                String mailHost = "smtp.gmail.com";
                                Properties properties = System.getProperties();
                                properties.put("mail.smtp.host", mailHost);
                                properties.put("mail.smtp.port", "465");
                                properties.put("mail.smtp.ssl.enable", "true");
                                properties.put("mail.smtp.auth", "true");

                                javax.mail.Session session = Session.getInstance(properties, new Authenticator() {
                                    @Override
                                    protected PasswordAuthentication getPasswordAuthentication() {
                                        return new PasswordAuthentication(senderEmail, p_e);
                                    }
                                });

                                MimeMessage mimeMessage = new MimeMessage(session);
                                mimeMessage.addRecipient(Message.RecipientType.TO, new InternetAddress(receiverEmail));
                                mimeMessage.setSubject(subjectEmail);
                                mimeMessage.setText(messageToSend);

                                Thread thread = new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            Transport.send(mimeMessage);

                                        } catch (MessagingException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                });
                                thread.start();

                                progressDialog.setMessage("Recovering your password...");
                                progressDialog.setCanceledOnTouchOutside(false);
                                progressDialog.show();
                                Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        startActivity(new Intent(PasswordRecoveryActivity.this, MainActivity.class));
                                        finish();
                                    }
                                },700);

                                createToast("We send you an email");
                            } catch (AddressException e) {
                                e.printStackTrace();
                            } catch (MessagingException e) {
                                e.printStackTrace();
                            }


                        }

                        if(!smsCheck.isChecked() && !emailCheck.isChecked()){
                            createToast("choose a recovery method");
                        }

                    }
                    else{
                        createToast("This username doesn't exist");
                        changeLineColorEditText(usernameInput, ContextCompat.getColor(getApplicationContext(), R.color.error_red));
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

    }

    public void createToast(String someText){
        Toast toast = new Toast(getApplicationContext());
        toast.setGravity(Gravity.CENTER,0,700);

        Drawable d = ContextCompat.getDrawable(getApplicationContext(), R.drawable.toast_shape);

        TextView toastText = new TextView(PasswordRecoveryActivity.this);
        toastText.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.toast_color));
        toastText.setBackground(d);
        toastText.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.error_red2));
        toastText.setPadding(35,20,35,20);
        toastText.setText(someText);
        toastText.setTextSize(14);

        toast.setView(toastText);
        toast.show();
    }

    public void changeLineColorEditText(EditText editText, int color){
        editText.setBackgroundTintList(ColorStateList.valueOf(color));
    }
}