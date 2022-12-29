package com.example.gorilla_nft_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.ColorStateList;
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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AdminRegistrationActivity extends AppCompatActivity {

    private Button goBackBtn, createAccount, usernameInfo, passwordInfo;;
    private EditText passwordInputRegister, passwordConfirmInputRegister, usernameRegister, phoneRegister, emailRegister, fullNameRegister;
    private TextView usernameText, emailText, phoneText, passwordText, confirmPasswordText, fullNameText;
    boolean passwordIsVisible;
    private ProgressDialog progressDialog;
    DatabaseReference RootRef;
    private Animation scaleDown, scaleUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_registration);

        goBackBtn = (Button) findViewById(R.id.back_btn);
        goBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminRegistrationActivity.this, AdminHomeActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });

        //set password visible toggle
        passwordInputRegister = (EditText) findViewById(R.id.admin__register_password_input);
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
        passwordConfirmInputRegister = (EditText) findViewById(R.id.admin__register_password_confirm_input);
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
        createAccount = (Button) findViewById(R.id.admin__register_btn);

        //info buttons
        usernameInfo = (Button) findViewById(R.id.admin__register_info_username_btn);
        passwordInfo = (Button) findViewById(R.id.admin__register_info_password_btn);

        //declare edit text (password is declared already)
        usernameRegister = (EditText) findViewById(R.id.admin__register_username_input);
        emailRegister = (EditText) findViewById(R.id.admin__register_email_input);
        phoneRegister = (EditText) findViewById(R.id.admin__register_phone_input);
        fullNameRegister = (EditText) findViewById(R.id.admin__register_fullname_input);

        //declare text view
        usernameText = (TextView) findViewById(R.id.admin__register_username_text);
        emailText = (TextView) findViewById(R.id.admin__register_email_text);
        phoneText = (TextView) findViewById(R.id.admin__register_phone_text);
        passwordText = (TextView) findViewById(R.id.admin__register_password_text);
        confirmPasswordText = (TextView) findViewById(R.id.admin__register_password_confirm_text);
        fullNameText = (TextView) findViewById(R.id.admin__register_fullname_text);

        //declare progress dialog
        progressDialog = new ProgressDialog(this);

        //buttons active effect
        scaleDown = AnimationUtils.loadAnimation(this,R.anim.scale_down);
        scaleUp = AnimationUtils.loadAnimation(this,R.anim.scale_up);

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
        String fullName = fullNameRegister.getText().toString().trim();

        //check if username and password have special characters and/or numbers
        Pattern pattern = Pattern.compile("[^a-z0-9 ]", Pattern.CASE_INSENSITIVE);
        boolean found_special_username = false;
        boolean username_start_with_number = false;
        boolean password_contains_number = false;
        boolean found_special_password = false;
        boolean password_contains_uppercase = false;
        char ch;

        if(!TextUtils.isEmpty(fullName) ){
            changeLineColorEditText(fullNameRegister, ContextCompat.getColor(getApplicationContext(), R.color.almost_black_text));
            fullNameText.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.almost_black_text));
        }
        else{
            changeLineColorEditText(fullNameRegister, ContextCompat.getColor(getApplicationContext(), R.color.error_red));
            fullNameText.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.error_red));
            createToast("Enter a valid full name");
        }

        if(!TextUtils.isEmpty(username)){
            Matcher matcher_username = pattern.matcher(username);
            found_special_username = matcher_username.find();
            if(username.substring(0, 1).matches("[0-9]")){
                username_start_with_number = true;
            }

            if(username.length() < 4 || username.length() > 15 || found_special_username || username_start_with_number){
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
            if(!email.contains("@") || email.length() < 6 || !email.contains(".")){
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
            if(phoneNumber.contains("+") || phoneNumber.length() < 8 || phoneNumber.length() > 12 || phoneNumber.contains(" ")){
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

            if(password.length() < 6 || password.length() > 15 || !found_special_password || !password_contains_number || !password_contains_uppercase){
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
            if(!passwordConfirm.equals(passwordInputRegister.getText().toString())){
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
                !TextUtils.isEmpty(username) && username.length() >= 4 && username.length() <=15 && !found_special_username && !username_start_with_number
                        && !TextUtils.isEmpty(email) && email.contains("@") && email.length() >= 6 && email.contains(".")
                        && !TextUtils.isEmpty(phoneNumber) && !phoneNumber.contains("+") && phoneNumber.length() >= 8 && phoneNumber.length() <= 12 && !phoneNumber.contains(" ")
                        && !TextUtils.isEmpty(password) && password.length() >= 6 && password.length() <= 15 && found_special_password && password_contains_number && password_contains_uppercase
                        && !TextUtils.isEmpty(passwordConfirm) && passwordConfirm.equals(password)
                        && !TextUtils.isEmpty(fullName)
        ){
            progressDialog.setTitle("Creating Account");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.setMessage("Please wait...");
            progressDialog.show();

            ValidateAccount(username, email, phoneNumber, password, fullName);
        }
    }

    private void ValidateAccount(String username, String email, String phoneNumber, String password, String fullName) {
        // Write to the database
        RootRef = FirebaseDatabase.getInstance("https://gorillanft-d65f0-default-rtdb.europe-west1.firebasedatabase.app/").getReference();

        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (!(snapshot.child("Admins").child(username).exists())){

                    RootRef.child("Admins").orderByChild("email").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
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
                                userDataMap.put("phone", phoneNumber);
                                userDataMap.put("password", password);
                                userDataMap.put("fullName", fullName);


                                RootRef.child("Admins").child(username).updateChildren(userDataMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            progressDialog.dismiss();
                                            createToast("Account created successfully");

                                            Handler handler = new Handler();
                                            handler.postDelayed(new Runnable() {
                                                @Override
                                                public void run() {
                                                    startActivity(new Intent(AdminRegistrationActivity.this, AdminHomeActivity.class));
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
                    createToast("Admin already exists");
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

        TextView toastText = new TextView(AdminRegistrationActivity.this);
        toastText.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.toast_color));
        toastText.setBackground(d);
        toastText.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.error_red2));
        toastText.setPadding(0,0,0,0);
        toastText.setText(someText);
        toastText.setTextSize(14);

        toast.setView(toastText);
        toast.show();
    }

    public void createToastInfoUsername(String someText){
        Toast toast = new Toast(getApplicationContext());
        toast.setGravity(Gravity.CENTER,0,-190);

        Drawable d = ContextCompat.getDrawable(getApplicationContext(), R.drawable.toast_shape_info);

        TextView toastText = new TextView(AdminRegistrationActivity.this);
        toastText.setBackground(d);
        toastText.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.info_grey_text));
        toastText.setPadding(0,0,0,0);
        toastText.setText(someText);
        toastText.setTextSize(14);
        toast.setView(toastText);
        toast.show();
    }

    public void createToastInfoPassword(String someText){
        Toast toast = new Toast(getApplicationContext());
        toast.setGravity(Gravity.CENTER,0,430);

        Drawable d = ContextCompat.getDrawable(getApplicationContext(), R.drawable.toast_shape_info);

        TextView toastText = new TextView(AdminRegistrationActivity.this);
        toastText.setBackground(d);
        toastText.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.info_grey_text));
        toastText.setPadding(35,20,35,20);
        toastText.setText(someText);
        toastText.setTextSize(14);
        toast.setView(toastText);
        toast.show();
    }
}