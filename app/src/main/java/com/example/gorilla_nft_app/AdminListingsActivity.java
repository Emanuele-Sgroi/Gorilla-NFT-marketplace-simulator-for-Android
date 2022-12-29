package com.example.gorilla_nft_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gorilla_nft_app.Model.AdminListing;
import com.example.gorilla_nft_app.Model.Products;
import com.example.gorilla_nft_app.ViewHolder.AdminListingViewHolder;
import com.example.gorilla_nft_app.ViewHolder.ProductViewHolder;
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

public class AdminListingsActivity extends AppCompatActivity {

    private Button goBackBtn;
    private EditText listingSearchInput;
    private ImageView listingResetText;
    private String listingInputText;
    private RecyclerView listingSearchList;
    RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_listings);

        goBackBtn = (Button) findViewById(R.id.listings_back_btn);
        goBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminListingsActivity.this, AdminHomeActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });

        listingSearchInput = (EditText) findViewById(R.id.listings_search_input);
        listingResetText = (ImageView) findViewById(R.id.listing_reset_text);

        //recycler view
        listingSearchList = findViewById(R.id.admin_listing_recycler_view);
        listingSearchList.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        listingSearchList.setLayoutManager(layoutManager);

        //reset text
        listingResetText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listingSearchInput.setText(null);
                listingInputText = null;
            }
        });

        //check admin input listener
        listingSearchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                listingInputText = listingSearchInput.getText().toString().trim();
                onStart();
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

        DatabaseReference nftRef = FirebaseDatabase.getInstance("https://gorillanft-d65f0-default-rtdb.europe-west1.firebasedatabase.app/").getReference()
                .child("NFTs");

        FirebaseRecyclerOptions<AdminListing> options =
                new FirebaseRecyclerOptions.Builder<AdminListing>()
                        .setQuery(nftRef.orderByChild("name").startAt(listingInputText), AdminListing.class)
                        .build();

        FirebaseRecyclerAdapter<AdminListing, AdminListingViewHolder> adapter =
                new FirebaseRecyclerAdapter<AdminListing, AdminListingViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull AdminListingViewHolder holder, int position, @NonNull AdminListing model) {
                        holder.nftNameText.setText(model.getName());
                        holder.nftCategoryText.setText(model.getCategory());
                        holder.nftPriceText.setText(model.getPrice());
                        holder.nftAddressText.setText(model.getAddress());
                        holder.nftDataText.setText(model.getDate());
                        holder.nftSellerText.setText(model.getSeller());
                        Picasso.get().load(model.getPreview()).into(holder.nftImageView);

                        holder.deleteNftBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                nftRef.child(model.getId()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            createToast("NFT deleted");
                                        }
                                    }
                                });
                            }
                        });
                    }

                    @NonNull
                    @Override
                    public AdminListingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.admin_listing_layout, parent,false);
                        AdminListingViewHolder holder = new AdminListingViewHolder(view);
                        return holder;
                    }
                };
        listingSearchList.setAdapter(adapter);
        adapter.startListening();
    }

    public void createToast(String someText){
        Toast toast = new Toast(getApplicationContext());
        toast.setGravity(Gravity.CENTER,0,700);

        Drawable d = ContextCompat.getDrawable(getApplicationContext(), R.drawable.toast_shape);

        TextView toastText = new TextView(AdminListingsActivity.this);
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