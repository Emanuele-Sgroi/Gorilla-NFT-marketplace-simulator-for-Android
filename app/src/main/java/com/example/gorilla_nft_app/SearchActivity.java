package com.example.gorilla_nft_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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

import java.util.Locale;

public class SearchActivity extends AppCompatActivity {

    private Button goBack;
    private EditText searchInput;
    private RecyclerView searchList;
    private String inputText, childrenCount;
    private ImageView resetText;
    private TextView cartQuantityText;
    private FloatingActionButton openCart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        goBack = (Button) findViewById(R.id.search_back_btn);
        goBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SearchActivity.this, HomeActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });

        // main code

        searchInput = (EditText) findViewById(R.id.search_input);
        searchList = (RecyclerView) findViewById(R.id.search_list);
        searchList.setLayoutManager(new LinearLayoutManager(SearchActivity.this));
        resetText = (ImageView) findViewById(R.id.reset_text);

        //reset text
        resetText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchInput.setText(null);
                inputText = null;
            }
        });

        //check user input listener
        searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                inputText = searchInput.getText().toString().trim();
                onStart();
            }
        });

        //cart quantity
        cartQuantityText = (TextView) findViewById(R.id.search_cart_quantity);
        final Handler handler = new Handler();
        Runnable refresh = new Runnable() {
            @Override
            public void run() {

                DatabaseReference QuantityCartRef = FirebaseDatabase.getInstance("https://gorillanft-d65f0-default-rtdb.europe-west1.firebasedatabase.app/").getReference()
                        .child("Cart List").child(Prevalent.onlineUsers.getUsername()).child("NFTs");

                QuantityCartRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        childrenCount = String.valueOf(snapshot.getChildrenCount());
                        cartQuantityText.setText(childrenCount);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                handler.postDelayed(this, 0);
            }
        };
        handler.postDelayed(refresh, 0);

        //open cart
        openCart = (FloatingActionButton) findViewById(R.id.search_cart_floating_button);
        openCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!cartQuantityText.getText().toString().equals("0")){
                    Intent intent = new Intent(SearchActivity.this, CartActivity.class);
                    startActivity(intent);
                }
                else{
                    createToast("The cart is empty");
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        DatabaseReference nftRef = FirebaseDatabase.getInstance("https://gorillanft-d65f0-default-rtdb.europe-west1.firebasedatabase.app/").getReference()
                .child("NFTs");

        FirebaseRecyclerOptions<Products> options =
                new FirebaseRecyclerOptions.Builder<Products>()
                        .setQuery(nftRef.orderByChild("name").startAt(inputText), Products.class)
                        .build();

        FirebaseRecyclerAdapter<Products, ProductViewHolder> adapter =
                new FirebaseRecyclerAdapter<Products, ProductViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull ProductViewHolder holder, int position, @NonNull Products model) {

                        String displaySeller = "By " + model.getSeller();
                        String displayPrice = model.getPrice() + " ETH";

                       DatabaseReference SellerPictureRef = FirebaseDatabase.getInstance("https://gorillanft-d65f0-default-rtdb.europe-west1.firebasedatabase.app/").getReference().child("Users").child(model.getSeller());
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
                                Intent intent = new Intent(SearchActivity.this, ProductDetailActivity.class);
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
        searchList.setAdapter(adapter);
        adapter.startListening();
    }

    public void createToast(String someText){
        Toast toast = new Toast(getApplicationContext());
        toast.setGravity(Gravity.CENTER,0,700);

        Drawable d = ContextCompat.getDrawable(getApplicationContext(), R.drawable.toast_shape);

        TextView toastText = new TextView(SearchActivity.this);
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