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
import android.os.Handler;
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

import com.example.gorilla_nft_app.Model.Cart;
import com.example.gorilla_nft_app.Prevalent.Prevalent;
import com.example.gorilla_nft_app.ViewHolder.CartViewHolder;
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

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class CartActivity extends AppCompatActivity {

    private Button goBackBtn, checkoutBtn;
    private TextView cartQuantityText, ethTotalText, dollarTotalText;
    private String childrenCount,address, buyer, category, date, time, id, name, preview, priceDollar, priceETH, seller
            ,saveCurrentDate, saveCurrentTime, transactionUniqueKey;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private double ethTotalPrice = 0.0;
    private double dollarTotalPrice = 0.0;
    private double oneNftPriceEth = 0.0;
    private double oneNftPriceDollar = 0.0;
    private DecimalFormat decimalFormat, ethDecimalFormat;
    private Animation scaleDown, scaleUp;
    private ProgressDialog progressDialog;
    private String goingToCheckout = "no";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        //decimal format for price
        decimalFormat = new DecimalFormat("#.##");
        ethDecimalFormat = new DecimalFormat("#.####");

        //progress dialog
        progressDialog = new ProgressDialog(this);

        //Buttons
        goBackBtn = (Button) findViewById(R.id.cart_back_btn);
        checkoutBtn = (Button) findViewById(R.id.cart_checkout_btn);

        //Text Views
        cartQuantityText = (TextView) findViewById(R.id.cart_quantity);
        ethTotalText = (TextView) findViewById(R.id.cart_eth_total_price);
        dollarTotalText = (TextView) findViewById(R.id.cart_dollar_total_price);


        //recycler view
        recyclerView = (RecyclerView) findViewById(R.id.cart_list);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        //back to shopping
        goBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CartActivity.this, HomeActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });

        //cart quantity
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

        //checkout click listener
        scaleDown = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.scale_down);
        scaleUp = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.scale_up);
        checkoutBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    checkoutBtn.startAnimation(scaleDown);
                }else if(event.getAction() == MotionEvent.ACTION_UP){
                    checkoutBtn.startAnimation(scaleUp);
                    checkingOut();
                }
                return true;
            }
        });
    }

    private void checkingOut() {

        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setMessage("Please wait...");
        progressDialog.show();

        DatabaseReference TempRef = FirebaseDatabase.getInstance("https://gorillanft-d65f0-default-rtdb.europe-west1.firebasedatabase.app/").getReference()
                .child("Checkout").child(Prevalent.onlineUsers.getUsername()).child("Temp");

        Map<String, Object> tempMap = new HashMap<>();
        tempMap.put("totalEth", String.valueOf(ethDecimalFormat.format(ethTotalPrice)));
        tempMap.put("totalDollar", String.valueOf(decimalFormat.format(dollarTotalPrice)));

        TempRef.updateChildren(tempMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    progressDialog.dismiss();
                    Intent intent = new Intent(CartActivity.this, CheckoutCryptoActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

        final DatabaseReference CartListRef = FirebaseDatabase.getInstance("https://gorillanft-d65f0-default-rtdb.europe-west1.firebasedatabase.app/").getReference()
                .child("Cart List").child(Prevalent.onlineUsers.getUsername()).child("NFTs");

        FirebaseRecyclerOptions<Cart> options =
                new FirebaseRecyclerOptions.Builder<Cart>()
                        .setQuery(CartListRef, Cart.class)
                        .build();

        FirebaseRecyclerAdapter<Cart, CartViewHolder> adapter =
                new FirebaseRecyclerAdapter<Cart, CartViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull CartViewHolder holder, int position, @NonNull Cart model) {

                        address = model.getAddress();
                        buyer = model.getBuyer();
                        category = model.getCategory();
                        date = model.getDate();
                        time = model.getTime();
                        id = model.getId();
                        name = model.getName();
                        preview = model.getPreview();
                        priceDollar = model.getPriceDollar();
                        priceETH = model.getPriceETH();
                        seller = model.getSeller();

                        holder.cartNameText.setText(name);
                        holder.cartSellerText.setText("By " + seller);
                        holder.cartAddressText.setText(address);
                        holder.cartEthPriceText.setText(priceETH + " ETH");
                        holder.cartDollarPriceText.setText(priceDollar + "$");
                        Picasso.get().load(preview).into(holder.cartImgItem);

                        oneNftPriceEth = Double.valueOf(model.getPriceETH());
                        ethTotalPrice = ethTotalPrice + oneNftPriceEth;
                        ethTotalText.setText(String.valueOf(ethDecimalFormat.format(ethTotalPrice)) + " ETH");

                        oneNftPriceDollar = Double.valueOf(model.getPriceDollar());
                        dollarTotalPrice = dollarTotalPrice + oneNftPriceDollar;
                        dollarTotalText.setText(String.valueOf(decimalFormat.format(dollarTotalPrice)) + "$");

                        holder.cartImgItem.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(CartActivity.this, ProductDetailActivity.class);
                                intent.putExtra("id",model.getId());
                                startActivity(intent);
                            }
                        });

                        holder.removeItemBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                CartListRef.child(model.getId()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            createToast("NFT removed from the cart");

                                            ethTotalPrice = ethTotalPrice - oneNftPriceEth;
                                            ethTotalText.setText(String.valueOf(ethDecimalFormat.format(ethTotalPrice)) + " ETH");

                                            dollarTotalPrice = dollarTotalPrice - oneNftPriceDollar;
                                            dollarTotalText.setText(String.valueOf(decimalFormat.format(dollarTotalPrice)) + "$");
                                        }

                                        if(task.isSuccessful() && childrenCount.equals("0")){
                                            Intent intent = new Intent(CartActivity.this, HomeActivity.class);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                            startActivity(intent);
                                            finish();
                                        }
                                    }
                                });
                            }
                        });

                    }

                    @NonNull
                    @Override
                    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_items_layout, parent, false);
                        CartViewHolder holder = new CartViewHolder(view);
                        return holder;
                    }
                };
        recyclerView.setAdapter(adapter);
        adapter.startListening();

    }

    public void createToast(String someText){
        Toast toast = new Toast(getApplicationContext());
        toast.setGravity(Gravity.CENTER,0,700);

        Drawable d = ContextCompat.getDrawable(getApplicationContext(), R.drawable.toast_shape);

        TextView toastText = new TextView(CartActivity.this);
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