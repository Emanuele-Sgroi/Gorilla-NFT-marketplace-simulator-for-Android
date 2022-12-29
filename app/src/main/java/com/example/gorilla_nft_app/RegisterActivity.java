package com.example.gorilla_nft_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.drawable.DrawableCompat;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.PorterDuff;
import android.graphics.drawable.AdaptiveIconDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.models.SlideModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.intellij.lang.annotations.JdkConstants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {

    Button createAccount, usernameInfo, passwordInfo;
    EditText passwordInputRegister, passwordConfirmInputRegister, usernameRegister, phoneRegister, emailRegister;
    TextView usernameText, emailText, phoneText, passwordText, confirmPasswordText;
    boolean passwordIsVisible;
    private ProgressDialog progressDialog;
    DatabaseReference RootRef;
    Animation scaleDown, scaleUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //image slider set up
        ImageSlider imageSlider = findViewById(R.id.app__register_image_slider);

        List<SlideModel> slideModels = new ArrayList<>();
        slideModels.add(new SlideModel(R.drawable.register1));
        slideModels.add(new SlideModel(R.drawable.register2));
        slideModels.add(new SlideModel(R.drawable.register3));

        imageSlider.setImageList(slideModels,true);

        //set password visible toggle
        passwordInputRegister = (EditText) findViewById(R.id.app__register_password_input);
        passwordInputRegister.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
        passwordInputRegister.setTransformationMethod(PasswordTransformationMethod.getInstance());
        passwordInputRegister.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DrawableRight=2;
                if(event.getAction() == MotionEvent.ACTION_UP){
                    if(event.getRawX() >= passwordInputRegister.getRight() - passwordInputRegister.getCompoundDrawables()[DrawableRight].getBounds().width()){
                        int elementSelected = passwordInputRegister.getSelectionEnd();
                        if(passwordIsVisible){
                            passwordInputRegister.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0, R.drawable.icon_visible_off, 0);
                            passwordInputRegister.setTransformationMethod(PasswordTransformationMethod.getInstance());
                            passwordIsVisible = false;
                        }else{
                            passwordInputRegister.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0, R.drawable.icon_visible, 0);
                            passwordInputRegister.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                            passwordIsVisible = true;
                        }
                        passwordInputRegister.setSelection(elementSelected);
                        return true;
                    }
                }
                return false;
            }
        });

        //set password confirm visible toggle
        passwordConfirmInputRegister = (EditText) findViewById(R.id.app__register_password_confirm_input);
        passwordConfirmInputRegister.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
        passwordConfirmInputRegister.setTransformationMethod(PasswordTransformationMethod.getInstance());
        passwordConfirmInputRegister.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DrawableRight=2;
                if(event.getAction() == MotionEvent.ACTION_UP){
                    if(event.getRawX() >= passwordConfirmInputRegister.getRight() - passwordConfirmInputRegister.getCompoundDrawables()[DrawableRight].getBounds().width()){
                        int elementSelected = passwordConfirmInputRegister.getSelectionEnd();
                        if(passwordIsVisible){
                            passwordConfirmInputRegister.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0, R.drawable.icon_visible_off, 0);
                            passwordConfirmInputRegister.setTransformationMethod(PasswordTransformationMethod.getInstance());
                            passwordIsVisible = false;
                        }else{
                            passwordConfirmInputRegister.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0, R.drawable.icon_visible, 0);
                            passwordConfirmInputRegister.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                            passwordIsVisible = true;
                        }
                        passwordConfirmInputRegister.setSelection(elementSelected);
                        return true;
                    }
                }
                return false;
            }
        });

        //links buttons
        createAccount = (Button) findViewById(R.id.app__register_btn);

        //info buttons
        usernameInfo = (Button) findViewById(R.id.app__register_info_username_btn);
        passwordInfo = (Button) findViewById(R.id.app__register_info_password_btn);

        //declare edit text (password is declared already)
        usernameRegister = (EditText) findViewById(R.id.app__register_username_input);
        emailRegister = (EditText) findViewById(R.id.app__register_email_input);
        phoneRegister = (EditText) findViewById(R.id.app__register_phone_input);

        //declare text view
        usernameText = (TextView) findViewById(R.id.app__register_username_text);
        emailText = (TextView) findViewById(R.id.app__register_email_text);
        phoneText = (TextView) findViewById(R.id.app__register_phone_text);
        passwordText = (TextView) findViewById(R.id.app__register_password_text);
        confirmPasswordText = (TextView) findViewById(R.id.app__register_password_confirm_text);

        //declare progress dialog
        progressDialog = new ProgressDialog(this);

        //buttons active effect
        scaleDown = AnimationUtils.loadAnimation(this,R.anim.scale_down);
        scaleUp = AnimationUtils.loadAnimation(this,R.anim.scale_up);

        //animation and intent
        createAccount.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    createAccount.startAnimation(scaleDown);
                }else if(event.getAction() == MotionEvent.ACTION_UP){
                    createAccount.startAnimation(scaleUp);

                    CreateAccount();
                }
                return true;
            }
        });

        //set info click listener
        usernameInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createToastInfoUsername("- Must be minimum 6 and maximum 15 characters\n" +
                        "- Cannot start with numbers\n" +
                        "- Cannot contain special character (< , = _ ? ! &...)");
            }
        });

        passwordInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createToastInfoPassword("- Must be minimum 4 and maximum 15 characters\n" +
                        "- Must contain a capital letter\n" +
                        "- Must contain a number\n" +
                        "- Must contain a special symbol (< , = _ ? ! &...)");
            }
        });
    }

    private void CreateAccount() {
        String username = usernameRegister.getText().toString().toLowerCase().trim();
        String email = emailRegister.getText().toString().toLowerCase().trim();
        String phoneNumber = phoneRegister.getText().toString();
        String password = passwordInputRegister.getText().toString().trim();
        String passwordConfirm = passwordConfirmInputRegister.getText().toString().trim();

        //check if username and password have special characters and/or numbers
        Pattern pattern = Pattern.compile("[^a-z0-9 ]", Pattern.CASE_INSENSITIVE);
        boolean found_special_username = false;
        boolean username_start_with_number = false;
        boolean password_contains_number = false;
        boolean found_special_password = false;
        boolean password_contains_uppercase = false;
        char ch;

        if(!TextUtils.isEmpty(username)){
            Matcher matcher_username = pattern.matcher(username);
            found_special_username = matcher_username.find();
            if(username.substring(0, 1).matches("[0-9]")){
                username_start_with_number = true;
            }

            if(usernameRegister.getText().toString().length() < 4 || usernameRegister.getText().toString().length() > 15 || found_special_username || username_start_with_number){
                changeLineColorEditText(usernameRegister, ContextCompat.getColor(getApplicationContext(), R.color.error_red));
                usernameText.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.error_red));
                createToast("Invalid username");
            }
            else{
                changeLineColorEditText(usernameRegister, ContextCompat.getColor(getApplicationContext(), R.color.almost_black_text));
                usernameText.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.almost_black_text));
            }
        }
        else {
            changeLineColorEditText(usernameRegister, ContextCompat.getColor(getApplicationContext(), R.color.error_red));
            usernameText.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.error_red));
            createToast("Invalid username");
        }

        if(!TextUtils.isEmpty(email)){
            if(!emailRegister.getText().toString().contains("@") || emailRegister.getText().length() < 6 || !emailRegister.getText().toString().contains(".")){
                emailRegister.setText(null);
                changeLineColorEditText(emailRegister, ContextCompat.getColor(getApplicationContext(), R.color.error_red));
                emailText.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.error_red));
                createToast("Invalid email");
            }else {
                changeLineColorEditText(emailRegister, ContextCompat.getColor(getApplicationContext(), R.color.almost_black_text));
                emailText.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.almost_black_text));
            }
        }
        else{
            changeLineColorEditText(emailRegister, ContextCompat.getColor(getApplicationContext(), R.color.error_red));
            emailText.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.error_red));
            createToast("Invalid email");
        }

        if(!TextUtils.isEmpty(phoneNumber) ){
            if(phoneRegister.getText().toString().contains("+") || phoneRegister.getText().length() < 8 || phoneRegister.getText().length() > 12 || phoneRegister.getText().toString().contains(" ")){
                phoneRegister.setText(null);
                changeLineColorEditText(phoneRegister, ContextCompat.getColor(getApplicationContext(), R.color.error_red));
                phoneText.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.error_red));

                createToast("Invalid phone number");
            }else{
                changeLineColorEditText(phoneRegister, ContextCompat.getColor(getApplicationContext(), R.color.almost_black_text));
                phoneText.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.almost_black_text));
            }
        }
        else{
            changeLineColorEditText(phoneRegister, ContextCompat.getColor(getApplicationContext(), R.color.error_red));
            phoneText.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.error_red));
        }

        if(!TextUtils.isEmpty(password)){
            Matcher matcher_password = pattern.matcher(password);
            found_special_password = matcher_password.find();
            for(int i=0; i < password.length(); i++) {
                ch = password.charAt(i);
                if(Character.isDigit(ch)){
                    password_contains_number = true;
                }
                else if (Character.isUpperCase(ch)){
                    password_contains_uppercase = true;
                }
            }
            if(password.length() < 6 || password.length() > 15
                    || !found_special_password || !password_contains_number || !password_contains_uppercase){
                changeLineColorEditText(passwordInputRegister, ContextCompat.getColor(getApplicationContext(), R.color.error_red));
                passwordText.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.error_red));
                createToast("Invalid password");
            }else{
                changeLineColorEditText(passwordInputRegister, ContextCompat.getColor(getApplicationContext(), R.color.almost_black_text));
                passwordText.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.almost_black_text));
            }
        }else{
            changeLineColorEditText(passwordInputRegister, ContextCompat.getColor(getApplicationContext(), R.color.error_red));
            passwordText.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.error_red));
            createToast("Invalid password");
        }

        if(!TextUtils.isEmpty(passwordConfirm)){
            if(!passwordConfirmInputRegister.getText().toString().equals(passwordInputRegister.getText().toString())){
                passwordConfirmInputRegister.setText(null);
                changeLineColorEditText(passwordConfirmInputRegister, ContextCompat.getColor(getApplicationContext(), R.color.error_red));
                confirmPasswordText.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.error_red));
                createToast("Passwords must match");
            }else{
                changeLineColorEditText(passwordConfirmInputRegister, ContextCompat.getColor(getApplicationContext(), R.color.almost_black_text));
                confirmPasswordText.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.almost_black_text));
            }
        }else{
            changeLineColorEditText(passwordConfirmInputRegister, ContextCompat.getColor(getApplicationContext(), R.color.error_red));
            confirmPasswordText.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.error_red));
            createToast("Passwords must match");
        }

        if(
                !TextUtils.isEmpty(username) && usernameRegister.getText().toString().length() >= 4 && usernameRegister.getText().toString().length() <=15 && !found_special_username && !username_start_with_number
                && !TextUtils.isEmpty(email) && emailRegister.getText().toString().contains("@") && emailRegister.getText().length() >= 6 && emailRegister.getText().toString().contains(".")
                && !TextUtils.isEmpty(phoneNumber) && !phoneRegister.getText().toString().contains("+") && phoneRegister.getText().length() >= 8 && phoneRegister.getText().length() <= 12 && !phoneRegister.getText().toString().contains(" ")
                && !TextUtils.isEmpty(password) && password.length() >= 6 && password.length() <= 15 && found_special_password && password_contains_number && password_contains_uppercase
                && !TextUtils.isEmpty(passwordConfirm) && passwordConfirmInputRegister.getText().toString().equals(passwordInputRegister.getText().toString())
        ){
            progressDialog.setTitle("Creating Account");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.setMessage("Please wait...");
            progressDialog.show();

            ValidateAccount(username, email, phoneNumber, password);
        }


    }

    //this method validate whenever a user exists already or not
    private void ValidateAccount( final String username, final String email, final String phone, final String password) {
        // Write to the database
        RootRef = FirebaseDatabase.getInstance("https://gorillanft-d65f0-default-rtdb.europe-west1.firebasedatabase.app/").getReference();

        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (!(snapshot.child("Users").child(username).exists())){

                    RootRef.child("Users").orderByChild("email").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                           if(snapshot.exists()){
                               createToast(email + " already in use");
                               progressDialog.dismiss();
                           }
                           else{
                               Map<String, Object> userDataMap = new HashMap<>();
                               userDataMap.put("email", email);
                               userDataMap.put("username", username);
                               userDataMap.put("phone", phone);
                               userDataMap.put("password", password);
                               userDataMap.put("picture", "https://firebasestorage.googleapis.com/v0/b/gorillanft-d65f0.appspot.com/o/profile%20pictures%2Fuser_profile_temp.jpg?alt=media&token=051c2517-17df-47fe-91ce-1d1d6f3e4e6d");

                               RootRef.child("Users").child(username).updateChildren(userDataMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                   @Override
                                   public void onComplete(@NonNull Task<Void> task) {
                                       if(task.isSuccessful()){
                                           progressDialog.dismiss();
                                           createToast("Account created successfully");

                                           Handler handler = new Handler();
                                           handler.postDelayed(new Runnable() {
                                               @Override
                                               public void run() {
                                                   startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                                                   finish();
                                               }
                                           },1000);
                                       }
                                   }
                               });
                           }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
                else {
                    createToast(username + " already exists");
                    progressDialog.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    // Front-end methods
    public void changeLineColorEditText(EditText editText, int color){
        editText.setBackgroundTintList(ColorStateList.valueOf(color));
    }

    public void createToast(String someText){
        Toast toast = new Toast(getApplicationContext());
        toast.setGravity(Gravity.CENTER,0,700);

        Drawable d = ContextCompat.getDrawable(getApplicationContext(), R.drawable.toast_shape);

        TextView toastText = new TextView(RegisterActivity.this);
        toastText.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.toast_color));
        toastText.setBackground(d);
        toastText.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.error_red2));
        toastText.setPadding(35,20,35,20);
        toastText.setText(someText);
        toastText.setTextSize(14);

        toast.setView(toastText);
        toast.show();
    }

    public void createToastInfoUsername(String someText){
        Toast toast = new Toast(getApplicationContext());
        toast.setGravity(Gravity.CENTER,0,-190);

        Drawable d = ContextCompat.getDrawable(getApplicationContext(), R.drawable.toast_shape_info);

        TextView toastText = new TextView(RegisterActivity.this);
        toastText.setBackground(d);
        toastText.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.info_grey_text));
        toastText.setPadding(35,20,35,20);
        toastText.setText(someText);
        toastText.setTextSize(14);
        toast.setView(toastText);
        toast.show();
    }

    public void createToastInfoPassword(String someText){
        Toast toast = new Toast(getApplicationContext());
        toast.setGravity(Gravity.CENTER,0,430);

        Drawable d = ContextCompat.getDrawable(getApplicationContext(), R.drawable.toast_shape_info);

        TextView toastText = new TextView(RegisterActivity.this);
        toastText.setBackground(d);
        toastText.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.info_grey_text));
        toastText.setPadding(35,20,35,20);
        toastText.setText(someText);
        toastText.setTextSize(14);
        toast.setView(toastText);
        toast.show();
    }

    public void createProgressDialog(String someMessage){
        ProgressDialog progressDialog = new ProgressDialog(this, R.drawable.progress_dialog);
        progressDialog.setIndeterminateDrawable(null);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.getWindow().setGravity(Gravity.CENTER);
        progressDialog.getWindow().setLayout(600, 180);
        progressDialog.setIcon(R.drawable.logo_notext);
        progressDialog.setTitle(someMessage);
        progressDialog.show();
    }

}