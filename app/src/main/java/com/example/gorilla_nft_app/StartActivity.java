package com.example.gorilla_nft_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.Gravity;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gorilla_nft_app.Model.Users;
import com.example.gorilla_nft_app.Prevalent.Prevalent;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import io.paperdb.Paper;

public class StartActivity extends AppCompatActivity {

    DatabaseReference RootRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        Paper.init(this);
        String usernameKey = Paper.book().read(Prevalent.UsernameKeyValue);
        String passwordKey = Paper.book().read(Prevalent.PasswordKeyValue);

        if(usernameKey != null && passwordKey != null){
            if(!TextUtils.isEmpty(usernameKey) && !TextUtils.isEmpty(passwordKey)){
               AccessToAccount(usernameKey, passwordKey);
            }
        }
        else{
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {

                    startActivity(new Intent(StartActivity.this, MainActivity.class));
                    finish();
                }
            },1500);
        }

    }

    private void AccessToAccount(final String username, final String password) {


        RootRef = FirebaseDatabase.getInstance("https://gorillanft-d65f0-default-rtdb.europe-west1.firebasedatabase.app/").getReference();

        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child("Users").child(username).exists()){
                    Users userData = snapshot.child("Users").child(username).getValue(Users.class);

                    if(userData.getUsername().equals(username)){
                        if(userData.getPassword().equals(password)){

                            //declaring who the user is so we can use his date later on and the app does not crash
                            Prevalent.onlineUsers = userData;

                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    startActivity(new Intent(StartActivity.this, HomeActivity.class));
                                    finish();
                                    createToast("Hello " + username);
                                }
                            },1500);
                        }
                    }
                }


                if(snapshot.child("Admins").child(username).exists()){
                    Users userData = snapshot.child("Admins").child(username).getValue(Users.class);

                    if(userData.getUsername().equals(username)){
                        if(userData.getPassword().equals(password)){

                            Prevalent.onlineUsers = userData;

                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    startActivity(new Intent(StartActivity.this, AdminHomeActivity.class));
                                    finish();
                                    createToast("Hello " + "Admin");
                                }
                            },1500);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    //front-end methods
    public void createToast(String someText){
        Toast toast = new Toast(getApplicationContext());
        toast.setGravity(Gravity.CENTER,0,700);

        Drawable d = ContextCompat.getDrawable(getApplicationContext(), R.drawable.toast_shape);

        TextView toastText = new TextView(StartActivity.this);
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