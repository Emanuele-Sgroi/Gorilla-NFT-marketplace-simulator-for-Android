package com.example.gorilla_nft_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.gorilla_nft_app.Model.Inquiries;
import com.example.gorilla_nft_app.Model.UsersList;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class AdminReadInquiryActivity extends AppCompatActivity {

    private Button backBtn;
    private TextView readUsernameOutput, readEmailOutput, readSubjectOutput, readMessageOutput;
    private String inquiryID = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_read_inquiry);

        backBtn = (Button) findViewById(R.id.message_back_btn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminReadInquiryActivity.this, AdminInquiriesActivity.class);
                startActivity(intent);
            }
        });

        readUsernameOutput = (TextView) findViewById(R.id.read_username);
        readEmailOutput = (TextView) findViewById(R.id.read_email);
        readSubjectOutput = (TextView) findViewById(R.id.read_subject_output);
        readMessageOutput = (TextView) findViewById(R.id.read_message_output);

        inquiryID = getIntent().getStringExtra("id");
        readMessage(inquiryID);
    }

    private void readMessage(String inquiryID) {
        DatabaseReference InquiryRef = FirebaseDatabase.getInstance("https://gorillanft-d65f0-default-rtdb.europe-west1.firebasedatabase.app/").getReference()
                .child("Inquiries");

        InquiryRef.child(inquiryID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    Inquiries inq = snapshot.getValue(Inquiries.class);

                    readUsernameOutput.setText(inq.getUsername());
                    readEmailOutput.setText(inq.getEmail());
                    readSubjectOutput.setText(inq.getSubject());
                    readMessageOutput.setText(inq.getMessage());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}