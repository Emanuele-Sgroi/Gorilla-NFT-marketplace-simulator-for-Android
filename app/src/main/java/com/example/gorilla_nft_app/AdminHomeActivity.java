package com.example.gorilla_nft_app;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;

import io.paperdb.Paper;

public class AdminHomeActivity extends AppCompatActivity {

    private Button logOutBtn, listingsBtn, usersBtn, inquiriesBtn, addNewAdminBtn;
    private Animation scaleDown, scaleUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_home);

        logOutBtn = (Button) findViewById(R.id.admin_logout_btn);
        listingsBtn = (Button) findViewById(R.id.admin_home_listing_btn);
        usersBtn = (Button) findViewById(R.id.admin_home_users_btn);
        inquiriesBtn = (Button) findViewById(R.id.admin_home_inquiries_btn);
        addNewAdminBtn = (Button) findViewById(R.id.admin_home_add_new_admin_btn);

        scaleDown = AnimationUtils.loadAnimation(this,R.anim.scale_down);
        scaleUp = AnimationUtils.loadAnimation(this,R.anim.scale_up);

        logOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Paper.book().destroy();

                ProgressDialog progressDialog = new ProgressDialog(AdminHomeActivity.this);
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.setMessage("Logging you out...");
                progressDialog.show();

                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(AdminHomeActivity.this,MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    }
                },1000);
            }
        });

        listingsBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    listingsBtn.startAnimation(scaleDown);
                }else if(event.getAction() == MotionEvent.ACTION_UP){
                    listingsBtn.startAnimation(scaleUp);
                    Intent intent = new Intent(AdminHomeActivity.this,AdminListingsActivity.class);
                    startActivity(intent);
                }
                return true;
            }
        });

        usersBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    usersBtn.startAnimation(scaleDown);
                }else if(event.getAction() == MotionEvent.ACTION_UP){
                    usersBtn.startAnimation(scaleUp);
                    Intent intent = new Intent(AdminHomeActivity.this,AdminUsersActivity.class);
                    startActivity(intent);
                }
                return true;
            }
        });

        inquiriesBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    inquiriesBtn.startAnimation(scaleDown);
                }else if(event.getAction() == MotionEvent.ACTION_UP){
                    inquiriesBtn.startAnimation(scaleUp);
                    Intent intent = new Intent(AdminHomeActivity.this,AdminInquiriesActivity.class);
                    startActivity(intent);
                }
                return true;
            }
        });

        addNewAdminBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    addNewAdminBtn.startAnimation(scaleDown);
                }else if(event.getAction() == MotionEvent.ACTION_UP){
                    addNewAdminBtn.startAnimation(scaleUp);
                    Intent intent = new Intent(AdminHomeActivity.this, AdminRegistrationActivity.class);
                    startActivity(intent);
                }
                return true;
            }
        });

    }
}