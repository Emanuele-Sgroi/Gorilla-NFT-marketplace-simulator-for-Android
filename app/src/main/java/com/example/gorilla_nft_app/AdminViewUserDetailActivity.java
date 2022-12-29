package com.example.gorilla_nft_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gorilla_nft_app.Model.Products;
import com.example.gorilla_nft_app.Model.SellerProducts;
import com.example.gorilla_nft_app.Model.Transactions;
import com.example.gorilla_nft_app.Model.UsersList;
import com.example.gorilla_nft_app.Prevalent.Prevalent;
import com.example.gorilla_nft_app.ViewHolder.TransactionsViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdminViewUserDetailActivity extends AppCompatActivity {

    private Button backBtn, removeUserBtn;
    private TextView usernameText, emailText, phoneText, passwordText;
    private CircleImageView userProfilePictureView;
    private String usernameID = "";
    private ProgressDialog progressDialog;
    private String  buyerString, buyerAddressString, nftQuantityString, paymentMethodString, transactionDateString,
            transactionTimeString, transactionIdString, worthDollarString, worthEthString;
    private RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_view_user_detail);

        backBtn = (Button) findViewById(R.id.back_btn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminViewUserDetailActivity.this, AdminUsersActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });

        //recycler view
        recyclerView = findViewById(R.id.user_detail_transactions);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        progressDialog = new ProgressDialog(this);

        removeUserBtn = (Button) findViewById(R.id.remove_user);
        usernameText = (TextView) findViewById(R.id.user_username);
        emailText = (TextView) findViewById(R.id.user_email);
        phoneText = (TextView) findViewById(R.id.user_phone);
        passwordText = (TextView) findViewById(R.id.user_password);
        userProfilePictureView = (CircleImageView) findViewById(R.id.user_detail_profile_picture);

        usernameID = getIntent().getStringExtra("usernameID");
        getUserDetails(usernameID);

        removeUserBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeUser(usernameID);
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

        DatabaseReference TransactionRef = FirebaseDatabase.getInstance("https://gorillanft-d65f0-default-rtdb.europe-west1.firebasedatabase.app/").getReference()
                .child("Transactions").child(usernameID);

        FirebaseRecyclerOptions<Transactions> options =
                new FirebaseRecyclerOptions.Builder<Transactions>()
                        .setQuery(TransactionRef,Transactions.class).build();

        FirebaseRecyclerAdapter<Transactions, TransactionsViewHolder> adapter =
                new FirebaseRecyclerAdapter<Transactions, TransactionsViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull TransactionsViewHolder holder, int position, @NonNull Transactions model) {
                        buyerString = model.getBuyer();
                        buyerAddressString = model.getBuyerAddress();
                        nftQuantityString = model.getNftQuantity();
                        paymentMethodString = model.getPaymentMethod();
                        transactionDateString = model.getTransactionDate();
                        transactionTimeString = model.getTransactionTime();
                        transactionIdString = model.getTransactionId();
                        worthDollarString = model.getWorthDollar();
                        worthEthString = model.getWorthEth();

                        holder.transactionId.setText(transactionIdString);
                        holder.transactionDate.setText(transactionDateString);
                        holder.transactionTime.setText(transactionTimeString);
                        holder.nftQuantity.setText(nftQuantityString);
                        holder.paymentMethod.setText(paymentMethodString);
                        holder.worthEth.setText(worthEthString);
                        holder.worthDollar.setText(worthDollarString);
                        holder.buyerAddress.setText(buyerAddressString);
                    }

                    @NonNull
                    @Override
                    public TransactionsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.transactions_layout, parent,false);
                        TransactionsViewHolder holder = new TransactionsViewHolder(view);
                        return holder;
                    }
                };


        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    private void removeUser(String usernameID) {
        progressDialog.setTitle("Removing User");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setMessage("Contacting the database");
        progressDialog.show();

        DatabaseReference UserRef = FirebaseDatabase.getInstance("https://gorillanft-d65f0-default-rtdb.europe-west1.firebasedatabase.app/").getReference();

        UserRef.child("Users").child(usernameID).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    createToast("User removed from the database");
                    progressDialog.dismiss();
                    Intent intent = new Intent(AdminViewUserDetailActivity.this, AdminUsersActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }
            }
        });

        UserRef.child("Cart List").child(usernameID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    UserRef.child("Cart List").child(usernameID).removeValue();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        UserRef.child("Wishlist").child(usernameID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    UserRef.child("Wishlist").child(usernameID).removeValue();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        UserRef.child("Transactions").child(usernameID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    UserRef.child("Transactions").child(usernameID).removeValue();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getUserDetails(String usernameID) {

        DatabaseReference UserRef = FirebaseDatabase.getInstance("https://gorillanft-d65f0-default-rtdb.europe-west1.firebasedatabase.app/").getReference()
                .child("Users");

        UserRef.child(usernameID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    UsersList user = snapshot.getValue(UsersList.class);

                    Picasso.get().load(user.getPicture()).into(userProfilePictureView);
                    usernameText.setText(user.getUsername());
                    emailText.setText(user.getEmail());
                    phoneText.setText(user.getPhone());
                    passwordText.setText(user.getPassword());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void createToast(String someText){
        Toast toast = new Toast(getApplicationContext());
        toast.setGravity(Gravity.CENTER,0,700);

        Drawable d = ContextCompat.getDrawable(getApplicationContext(), R.drawable.toast_shape);

        TextView toastText = new TextView(AdminViewUserDetailActivity.this);
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