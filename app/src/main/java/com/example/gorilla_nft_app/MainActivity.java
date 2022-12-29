package com.example.gorilla_nft_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gorilla_nft_app.Model.Users;
import com.example.gorilla_nft_app.Prevalent.Prevalent;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.core.utilities.Utilities;
import com.rey.material.widget.CheckBox;

import io.paperdb.Paper;

public class MainActivity extends AppCompatActivity {
    private Button loginButton, signUpButton;
    private EditText passwordInput, usernameInput;
    private TextView forgotPassword;
    boolean passwordIsVisible;
    private ProgressDialog progressDialog;
    private CheckBox checkBoxRememberMe;
    private LinearLayout landingPageLink;

    DatabaseReference RootRef;
    private String parentDatabaseName = "Users";

    Animation scaleDown, scaleUp;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //declare progress dialog
        progressDialog = new ProgressDialog(this);

        //links other activities and more
        loginButton = (Button) findViewById(R.id.app__main_login_btn);
        signUpButton = (Button) findViewById(R.id.app__main_signup_btn);
        forgotPassword = (TextView) findViewById(R.id.app__main_login_forgot_password_link);
        landingPageLink = (LinearLayout) findViewById(R.id.app__main_landing_page_link);

        //underlines
        forgotPassword.setPaintFlags(forgotPassword.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        //declare inputs fields
        passwordInput = (EditText) findViewById(R.id.app__main_login_password_input);
        usernameInput = (EditText) findViewById(R.id.app__main_login_user_input);

        //declare check box remember me including API from github
        checkBoxRememberMe = (CheckBox) findViewById(R.id.app__main_checkbox_remember_me);
        Paper.init(this);

        //set password visible toggle
        passwordInput.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
        passwordInput.setTransformationMethod(PasswordTransformationMethod.getInstance());
        passwordInput.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DrawableRight=2;
                if(event.getAction() == MotionEvent.ACTION_UP){
                    if(event.getRawX() >= passwordInput.getRight() - passwordInput.getCompoundDrawables()[DrawableRight].getBounds().width()){
                        int elementSelected = passwordInput.getSelectionEnd();
                        if(passwordIsVisible){
                            passwordInput.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0, R.drawable.icon_visible_off, 0);
                            passwordInput.setTransformationMethod(PasswordTransformationMethod.getInstance());
                            passwordIsVisible = false;
                        }else{
                            passwordInput.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0, R.drawable.icon_visible, 0);
                            passwordInput.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                            passwordIsVisible = true;
                        }
                        passwordInput.setSelection(elementSelected);
                        return true;
                    }
                }
                return false;
            }
        });

        //buttons active effect
        scaleDown = AnimationUtils.loadAnimation(this,R.anim.scale_down);
        scaleUp = AnimationUtils.loadAnimation(this,R.anim.scale_up);

        //animation and intent
        loginButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    loginButton.startAnimation(scaleDown);
                }else if(event.getAction() == MotionEvent.ACTION_UP){
                    loginButton.startAnimation(scaleUp);

                    //call login method
                    LoginUser();
                }
                return true;
            }
        });

        signUpButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    signUpButton.startAnimation(scaleDown);
                }else if(event.getAction() == MotionEvent.ACTION_UP){
                    signUpButton.startAnimation(scaleUp);

                    //intent register activity
                    Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
                    startActivity(intent);
                }
                return true;
            }
        });

        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, PasswordRecoveryActivity.class);
                startActivity(intent);
            }
        });
    }

    //method for let user logging in
    private void LoginUser(){
        String username = usernameInput.getText().toString().toLowerCase().trim();
        String password = passwordInput.getText().toString().trim();

        if(TextUtils.isEmpty(username)){
            changeLineColorEditText(usernameInput, ContextCompat.getColor(getApplicationContext(), R.color.error_red));
            usernameInput.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.almost_black_text));
        }
        else{
            changeLineColorEditText(usernameInput, ContextCompat.getColor(getApplicationContext(), R.color.almost_black_text));
        }

        if(TextUtils.isEmpty(password)){
            changeLineColorEditText(passwordInput, ContextCompat.getColor(getApplicationContext(), R.color.error_red));
            passwordInput.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.almost_black_text));
        }
        else{
            changeLineColorEditText(passwordInput, ContextCompat.getColor(getApplicationContext(), R.color.almost_black_text));
        }

       if(!TextUtils.isEmpty(username) && !TextUtils.isEmpty(password)){
           progressDialog.setTitle("Login Account");
           progressDialog.setMessage("Please wait...");
           progressDialog.setCanceledOnTouchOutside(false);
           progressDialog.show();

           AccessToAccount(username, password);
        }

    }

    private void AccessToAccount(final String username, final String password) {

        if(checkBoxRememberMe.isChecked()){
            Paper.book().write(Prevalent.UsernameKeyValue, username);
            Paper.book().write(Prevalent.PasswordKeyValue, password);
        }

        RootRef = FirebaseDatabase.getInstance("https://gorillanft-d65f0-default-rtdb.europe-west1.firebasedatabase.app/").getReference();

        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child("Users").child(username).exists()){
                    Users userData = snapshot.child("Users").child(username).getValue(Users.class);

                    if(userData.getUsername().equals(username)){
                        changeLineColorEditText(usernameInput, ContextCompat.getColor(getApplicationContext(), R.color.almost_black_text));
                        if(userData.getPassword().equals(password)){
                            changeLineColorEditText(passwordInput, ContextCompat.getColor(getApplicationContext(), R.color.almost_black_text));

                            //Store the user data so you can use it later
                            Prevalent.onlineUsers = userData;

                            progressDialog.dismiss();
                            createToast("Hello " + username);
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    startActivity(new Intent(MainActivity.this, HomeActivity.class));
                                    finish();
                                }
                            },400);


                        }
                        else{
                            createToast("Wrong password");
                            progressDialog.dismiss();
                            changeLineColorEditText(passwordInput, ContextCompat.getColor(getApplicationContext(), R.color.error_red));
                            passwordInput.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.almost_black_text));
                        }
                    }

                }
                else if (snapshot.child("Admins").child(username).exists()){
                    Users userData = snapshot.child("Admins").child(username).getValue(Users.class);

                    if(userData.getUsername().equals(username)){
                        changeLineColorEditText(usernameInput, ContextCompat.getColor(getApplicationContext(), R.color.almost_black_text));
                        if(userData.getPassword().equals(password)){
                            changeLineColorEditText(passwordInput, ContextCompat.getColor(getApplicationContext(), R.color.almost_black_text));
                            progressDialog.dismiss();
                            createToast("Hello Admin");
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    startActivity(new Intent(MainActivity.this, AdminHomeActivity.class));
                                    finish();
                                }
                            },400);

                        }
                        else{
                            createToast("Wrong password");
                            progressDialog.dismiss();
                            changeLineColorEditText(passwordInput, ContextCompat.getColor(getApplicationContext(), R.color.error_red));
                            passwordInput.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.almost_black_text));
                        }
                    }
                }
                else{
                    createToast("Wrong username");
                    progressDialog.dismiss();
                    changeLineColorEditText(usernameInput, ContextCompat.getColor(getApplicationContext(), R.color.error_red));
                    usernameInput.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.almost_black_text));
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

        TextView toastText = new TextView(MainActivity.this);
        toastText.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.toast_color));
        toastText.setBackground(d);
        toastText.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.error_red2));
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