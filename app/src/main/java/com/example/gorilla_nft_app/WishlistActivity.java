package com.example.gorilla_nft_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.gorilla_nft_app.Model.Products;
import com.example.gorilla_nft_app.Prevalent.Prevalent;
import com.example.gorilla_nft_app.ViewHolder.ProductViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class WishlistActivity extends AppCompatActivity {

    private Button backBtn;
    private FloatingActionButton cartBtn;
    private TextView quantityCart;
    private RecyclerView recyclerView;
    private String childrenCount;
    RecyclerView.LayoutManager layoutManager;

    DatabaseReference RootRef, ProductRef, WishlistRef, SellerPictureRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wishlist);

        backBtn = (Button) findViewById(R.id.wishlist_back_btn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WishlistActivity.this, HomeActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });

        cartBtn = (FloatingActionButton) findViewById(R.id.wishlist_cart_floating_button);

        //recycler view
        recyclerView = findViewById(R.id.app__content_home_recycler_menu);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        quantityCart = (TextView) findViewById(R.id.wishlist_cart_quantity);

        final Handler handler = new Handler();
        Runnable refresh = new Runnable() {
            @Override
            public void run() {

                DatabaseReference QuantityCartRef = FirebaseDatabase.getInstance("https://gorillanft-d65f0-default-rtdb.europe-west1.firebasedatabase.app/").getReference()
                        .child("Cart List").child("User Section").child(Prevalent.onlineUsers.getUsername()).child("NFTs");

                QuantityCartRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        childrenCount = String.valueOf(snapshot.getChildrenCount());
                        quantityCart.setText(childrenCount);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                handler.postDelayed(this, 0);
            }
        };
        handler.postDelayed(refresh, 0);


        ProductRef = FirebaseDatabase.getInstance("https://gorillanft-d65f0-default-rtdb.europe-west1.firebasedatabase.app/").getReference()
                .child("Wishlist").child(Prevalent.onlineUsers.getUsername());
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Products> options =
                new FirebaseRecyclerOptions.Builder<Products>()
                        .setQuery(ProductRef, Products.class).build();

        FirebaseRecyclerAdapter<Products, ProductViewHolder> adapter =
                new FirebaseRecyclerAdapter<Products, ProductViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull ProductViewHolder holder, int position, @NonNull Products model) {
                        String displaySeller = "By " + model.getSeller();
                        String displayPrice = model.getPrice();

                        SellerPictureRef = FirebaseDatabase.getInstance("https://gorillanft-d65f0-default-rtdb.europe-west1.firebasedatabase.app/").getReference().child("Users").child(model.getSeller());
                        SellerPictureRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if(snapshot.exists()){
                                    Picasso.get().load(snapshot.child("picture").getValue().toString()).into(holder.nftSellerPicture);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                        holder.nftNameText.setText(model.getName());
                        holder.nftSellerText.setText(displaySeller);
                        holder.nftPriceText.setText(displayPrice);
                        Picasso.get().load(model.getPreview()).into(holder.nftImageView);

                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(WishlistActivity.this, ProductDetailActivity.class);
                                intent.putExtra("id",model.getId());
                                startActivity(intent);
                            }
                        });

                    }

                    @NonNull
                    @Override
                    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_products_layout, parent,false);
                        ProductViewHolder holder = new ProductViewHolder(view);
                        return holder;
                    }
                };
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }
}