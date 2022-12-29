package com.example.gorilla_nft_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gorilla_nft_app.Model.Products;
import com.example.gorilla_nft_app.Model.SellerProducts;
import com.example.gorilla_nft_app.Prevalent.Prevalent;
import com.example.gorilla_nft_app.ViewHolder.ProductViewHolder;
import com.example.gorilla_nft_app.ViewHolder.SellerProductViewHolder;
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

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class sellMainActivity extends AppCompatActivity {

    Button goBackBtn, sellNftBtn;
    TextView usernameSeller;
    private CircleImageView profilePictureView;
    DatabaseReference RootRef, SellingRef;
    private RecyclerView recyclerViewSeller;
    RecyclerView.LayoutManager layoutManager;
    boolean childExist = false;
    String idRef;


    Animation scaleDown, scaleUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sell_main);

        goBackBtn = (Button) findViewById(R.id.app__activity_sell_main_back_btn);
        sellNftBtn = (Button) findViewById(R.id.app__activity_sell_main_sell_btn);

        usernameSeller = (TextView) findViewById(R.id.app__activity_sell_main_username_text);
        usernameSeller.setText(Prevalent.onlineUsers.getUsername());

        scaleDown = AnimationUtils.loadAnimation(this,R.anim.scale_down);
        scaleUp = AnimationUtils.loadAnimation(this,R.anim.scale_up);

        //animation and intent
       goBackBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    goBackBtn.startAnimation(scaleDown);
                }else if(event.getAction() == MotionEvent.ACTION_UP){
                    goBackBtn.startAnimation(scaleUp);

                    Intent intent = new Intent(sellMainActivity.this,HomeActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }
                return true;
            }
        });

        sellNftBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    sellNftBtn.startAnimation(scaleDown);
                }else if(event.getAction() == MotionEvent.ACTION_UP){
                    sellNftBtn.startAnimation(scaleUp);

                    checkBankAccount();

                }
                return true;
            }
        });

        profilePictureView = (CircleImageView) findViewById(R.id.app__activity_sell_main_user_profile_picture);
        RootRef = FirebaseDatabase.getInstance("https://gorillanft-d65f0-default-rtdb.europe-west1.firebasedatabase.app/").getReference().child("Users");
        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child(Prevalent.onlineUsers.getUsername()).child("picture").exists()){
                    String picture = snapshot.child(Prevalent.onlineUsers.getUsername()).child("picture").getValue().toString();
                    Picasso.get().load(picture).into(profilePictureView);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //Database seller ref
        SellingRef = FirebaseDatabase.getInstance("https://gorillanft-d65f0-default-rtdb.europe-west1.firebasedatabase.app/").getReference();


        //recycler view
        recyclerViewSeller = findViewById(R.id.app__seller_main_recycler_menu);
        recyclerViewSeller.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerViewSeller.setLayoutManager(layoutManager);

    }

    private void checkBankAccount() {

       DatabaseReference CardRef = FirebaseDatabase.getInstance("https://gorillanft-d65f0-default-rtdb.europe-west1.firebasedatabase.app/").getReference()
                .child("Users").child(Prevalent.onlineUsers.getUsername()).child("Saved Cards");

       CardRef.addListenerForSingleValueEvent(new ValueEventListener() {
           @Override
           public void onDataChange(@NonNull DataSnapshot snapshot) {
               if(snapshot.exists()){
                   Intent intent = new Intent(sellMainActivity.this, SellerAddNewNftActivity.class);
                   startActivity(intent);
               }
               else{
                   createToast("You need to save a credit/debit card first");
               }
           }

           @Override
           public void onCancelled(@NonNull DatabaseError error) {

           }
       });

    }


    @Override
    protected void onStart() {
        super.onStart();

        SellingRef.child("NFTs").orderByChild("seller").equalTo(Prevalent.onlineUsers.getUsername()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.exists()){

                    FirebaseRecyclerOptions<SellerProducts> options =
                            new FirebaseRecyclerOptions.Builder<SellerProducts>()
                                    .setQuery(SellingRef.child("NFTs").orderByChild("seller").equalTo(Prevalent.onlineUsers.getUsername()),SellerProducts.class).build();

                    FirebaseRecyclerAdapter<SellerProducts, SellerProductViewHolder> adapter =
                            new FirebaseRecyclerAdapter<SellerProducts, SellerProductViewHolder>(options) {
                                @Override
                                protected void onBindViewHolder(@NonNull SellerProductViewHolder holder, int position, @NonNull SellerProducts model) {

                                    holder.nftNameText.setText(model.getName());
                                    holder.nftCategoryText.setText(model.getCategory());
                                    holder.nftPriceText.setText(model.getPrice() + " ETH");
                                    holder.nftDataText.setText(model.getDate());
                                    holder.nftAddressText.setText((model.getAddress()));
                                    Picasso.get().load(model.getPreview()).into(holder.nftImageView);
                                    idRef = model.getId();
                                    holder.deleteNftBtn.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            SellingRef.child("NFTs").child(model.getId()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if(task.isSuccessful()){
                                                        createToast("NFT deleted");
                                                    }
                                                    else{
                                                        createToast("Something went wrong, try again later");
                                                    }
                                                }
                                            });
                                        }
                                    });

                                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Intent intent = new Intent(sellMainActivity.this, ProductDetailActivity.class);
                                            intent.putExtra("id",model.getId());
                                            startActivity(intent);
                                        }
                                    });
                                }

                                @NonNull
                                @Override
                                public SellerProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                                    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.seller_products_layout, parent,false);
                                    SellerProductViewHolder holder = new SellerProductViewHolder(view);
                                    return holder;
                                }
                            };

                    recyclerViewSeller.setAdapter(adapter);
                    adapter.startListening();


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

        TextView toastText = new TextView(getApplicationContext());
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