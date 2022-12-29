package com.example.gorilla_nft_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.gorilla_nft_app.Model.AdminListing;
import com.example.gorilla_nft_app.Model.Inquiries;
import com.example.gorilla_nft_app.ViewHolder.AdminListingViewHolder;
import com.example.gorilla_nft_app.ViewHolder.InquiriesViewHolder;
import com.example.gorilla_nft_app.ViewHolder.UsersListViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AdminInquiriesActivity extends AppCompatActivity {

    private Button goBackBtn;
    private String data, email, id, message, subject, time, username;
    int readMessage = 0;
    private RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_inquiries);

        goBackBtn = (Button) findViewById(R.id.back_btn);
        goBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminInquiriesActivity.this, AdminHomeActivity.class);
                startActivity(intent);
            }
        });

        //recycler view
        recyclerView = findViewById(R.id.inquiries_recycler_view);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
    }

    @Override
    protected void onStart() {
        super.onStart();

        DatabaseReference InquiriesRef = FirebaseDatabase.getInstance("https://gorillanft-d65f0-default-rtdb.europe-west1.firebasedatabase.app/").getReference()
                .child("Inquiries");

        FirebaseRecyclerOptions<Inquiries> options =
                new FirebaseRecyclerOptions.Builder<Inquiries>()
                        .setQuery(InquiriesRef, Inquiries.class)
                        .build();

        FirebaseRecyclerAdapter<Inquiries, InquiriesViewHolder> adapter =
                new FirebaseRecyclerAdapter<Inquiries, InquiriesViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull InquiriesViewHolder holder, int position, @NonNull Inquiries model) {
                        data = model.getData();
                        email = model.getEmail();
                        id = model.getId();
                        message = model.getMessage();
                        subject = model.getSubject();
                        time = model.getTime();
                        username = model.getUsername();

                        holder.subjectOutput.setText(subject);
                        holder.dateOutput.setText(data);
                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                readMessage++;
                                Intent intent = new Intent(AdminInquiriesActivity.this, AdminReadInquiryActivity.class);
                                intent.putExtra("id",model.getId());
                                startActivity(intent);
                            }
                        });
                        if(readMessage > 0){
                            holder.messageIcon.setImageResource(R.drawable.open_envelope);
                        }
                        else{
                            holder.messageIcon.setImageResource(R.drawable.close_envelop);
                        }
                    }

                    @NonNull
                    @Override
                    public InquiriesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.inquiries_list_layout, parent,false);
                        InquiriesViewHolder holder = new InquiriesViewHolder(view);
                        return holder;
                    }
                };
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }
}