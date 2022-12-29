package com.example.gorilla_nft_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.gorilla_nft_app.Model.SellerProducts;
import com.example.gorilla_nft_app.Model.Transactions;
import com.example.gorilla_nft_app.Prevalent.Prevalent;
import com.example.gorilla_nft_app.ViewHolder.SellerProductViewHolder;
import com.example.gorilla_nft_app.ViewHolder.TransactionsViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

public class TransactionsActivity extends AppCompatActivity {

    private Button backBtn;
    private TextView reportBtn;
    DatabaseReference RootRef;
    private RecyclerView recyclerView;
    private String  buyerString, buyerAddressString, nftQuantityString, paymentMethodString, transactionDateString,
            transactionTimeString, transactionIdString, worthDollarString, worthEthString;
    RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transactions);

        backBtn = (Button) findViewById(R.id.transactions_back_btn);
        reportBtn = (TextView) findViewById(R.id.report_btn);
        reportBtn.setPaintFlags(reportBtn.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        //recycler view
        recyclerView = findViewById(R.id.transactions_recycler_view);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(TransactionsActivity.this, HomeActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });

        reportBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TransactionsActivity.this, HelpActivity.class);
                startActivity(intent);
            }
        });

        RootRef = FirebaseDatabase.getInstance("https://gorillanft-d65f0-default-rtdb.europe-west1.firebasedatabase.app/").getReference()
                .child("Transactions").child(Prevalent.onlineUsers.getUsername());

    }

    @Override
    protected void onStart() {
        super.onStart();


        FirebaseRecyclerOptions<Transactions> options =
                new FirebaseRecyclerOptions.Builder<Transactions>()
                        .setQuery(RootRef,Transactions.class).build();

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
}