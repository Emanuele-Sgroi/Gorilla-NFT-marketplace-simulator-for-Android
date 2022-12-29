package com.example.gorilla_nft_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.gorilla_nft_app.Model.AdminListing;
import com.example.gorilla_nft_app.Model.Users;
import com.example.gorilla_nft_app.Model.UsersList;
import com.example.gorilla_nft_app.Prevalent.Prevalent;
import com.example.gorilla_nft_app.ViewHolder.AdminListingViewHolder;
import com.example.gorilla_nft_app.ViewHolder.UsersListViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AdminUsersActivity extends AppCompatActivity {

    private Button goBackBtn;
    private EditText usersSearchInput;
    private ImageView usersResetText;
    private String usersInputText;
    private RecyclerView usersList;
    RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_users);


        goBackBtn = (Button) findViewById(R.id.back_btn);
        goBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminUsersActivity.this, AdminHomeActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });

        usersSearchInput = (EditText) findViewById(R.id.admin_users_search_input);
        usersResetText = (ImageView) findViewById(R.id.admin_users_reset_text);

        //recycler view
        usersList = findViewById(R.id.users_list);
        usersList.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        usersList.setLayoutManager(layoutManager);

        //reset text
        usersResetText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                usersSearchInput.setText(null);
                usersInputText = null;
            }
        });

        //check admin input listener
        usersSearchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                usersInputText = usersSearchInput.getText().toString().trim();
                onStart();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        DatabaseReference UsersListRef = FirebaseDatabase.getInstance("https://gorillanft-d65f0-default-rtdb.europe-west1.firebasedatabase.app/").getReference()
                .child("Users");

        FirebaseRecyclerOptions<UsersList> options =
                new FirebaseRecyclerOptions.Builder<UsersList>()
                        .setQuery(UsersListRef.orderByChild("username").startAt(usersInputText), UsersList.class)
                        .build();

        FirebaseRecyclerAdapter<UsersList, UsersListViewHolder> adapter =
                new FirebaseRecyclerAdapter<UsersList, UsersListViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull UsersListViewHolder holder, int position, @NonNull UsersList model) {
                        holder.usernameDisplay.setText(model.getUsername());
                        holder.viewUserBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(AdminUsersActivity.this, AdminViewUserDetailActivity.class);
                                intent.putExtra("usernameID",model.getUsername());
                                startActivity(intent);
                            }
                        });
                    }

                    @NonNull
                    @Override
                    public UsersListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.admin_users_list_layout, parent,false);
                        UsersListViewHolder holder = new UsersListViewHolder(view);
                        return holder;
                    }
                };
        usersList.setAdapter(adapter);
        adapter.startListening();
    }
}