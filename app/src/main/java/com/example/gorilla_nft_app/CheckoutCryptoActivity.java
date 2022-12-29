package com.example.gorilla_nft_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
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
import com.rey.material.widget.CheckBox;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class CheckoutCryptoActivity extends AppCompatActivity {

    private TextView cancelBtn, ethTotalText, dollarTotalText, infoText;
    private EditText buyerAddressInput;
    private Button infoBtn, payWithBankBtn, payWithCryptoBtn;
    private CheckBox acceptCkBox;
    private String totalEthString, totalDollarString, buyerAddress, saveCurrentDate, saveCurrentTime, transactionUniqueKey
            ,cartChildrenCount, address, buyer, category, date, time, id, name, preview, priceDollar, priceETH, seller;
    private double ethTotalPrice = 0.0;
    private double dollarTotalPrice = 0.0;
    private double oneNftPriceEth = 0.0;
    private double oneNftPriceDollar = 0.0;
    private DecimalFormat decimalFormat, ethDecimalFormat;
    private String payWithBankAccount = "";
    private String payWithCrypto = "";
    private Animation scaleDown, scaleUp;
    private ProgressDialog progressDialog;
    private DatabaseReference RootRef;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout_crypto);

        //general database reference
        RootRef = FirebaseDatabase.getInstance("https://gorillanft-d65f0-default-rtdb.europe-west1.firebasedatabase.app/").getReference();

        //recycler view invisible
        recyclerView = (RecyclerView) findViewById(R.id.invisible_rw);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        //declare progress dialog
        progressDialog = new ProgressDialog(this);

        //decimal format for price
        decimalFormat = new DecimalFormat("#.##");
        ethDecimalFormat = new DecimalFormat("#.####");

        //Text Views
        cancelBtn = (TextView) findViewById(R.id.checkout_crypto_cancel_btn);
        cancelBtn.setPaintFlags(cancelBtn.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        ethTotalText = (TextView) findViewById(R.id.total_eth_checkout);
        dollarTotalText = (TextView) findViewById(R.id.total_dollar_checkout);
        infoText = (TextView) findViewById(R.id.info_text);

        //Edit text
        buyerAddressInput = (EditText) findViewById(R.id.buyer_address_input);

        //Buttons
        infoBtn = (Button) findViewById(R.id.info_btn);
        payWithBankBtn = (Button) findViewById(R.id.checkout_pay_bank_button);
        payWithCryptoBtn = (Button) findViewById(R.id.checkout_pay_crypto_button);

        //Check box
        acceptCkBox = (CheckBox) findViewById(R.id.checkout_checkbox_agree);

        //cancel and go back to cart
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DatabaseReference TempRef = FirebaseDatabase.getInstance("https://gorillanft-d65f0-default-rtdb.europe-west1.firebasedatabase.app/").getReference()
                        .child("Checkout").child(Prevalent.onlineUsers.getUsername());

                TempRef.removeValue();

                Intent intent = new Intent(CheckoutCryptoActivity.this, CartActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });

        //Buyer address info listener
        infoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createToastInfo("- An ETH token address is 16 characters long\n" +
                        "- Get you public address from your wallet\n" +
                        "\n" +
                        "Example\n" +
                        "cd2a3d9f938e13cd947ec05abc7fe734df8dd826");
            }
        });

        //payment listeners
        scaleDown = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.scale_down);
        scaleUp = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.scale_up);
        payWithCryptoBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    payWithCryptoBtn.startAnimation(scaleDown);
                }else if(event.getAction() == MotionEvent.ACTION_UP){
                    payWithCryptoBtn.startAnimation(scaleUp);
                    payWithCrypto = "clicked";
                    payWithBankAccount = "";
                    paymentValidations();
                }
                return true;
            }
        });

        payWithBankBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    payWithBankBtn.startAnimation(scaleDown);
                }else if(event.getAction() == MotionEvent.ACTION_UP){
                    payWithBankBtn.startAnimation(scaleUp);
                    payWithCrypto = "";
                    payWithBankAccount = "clicked";
                    paymentValidations();
                }
                return true;
            }
        });

    }

    private void paymentValidations() {
        buyerAddress = buyerAddressInput.getText().toString().trim();

        if(TextUtils.isEmpty(buyerAddress)){
            changeLineColorEditText(buyerAddressInput, ContextCompat.getColor(getApplicationContext(), R.color.error_red));
            infoText.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.error_red));
            createToast("Please insert your public address");
        }
        else{
            changeLineColorEditText(buyerAddressInput, ContextCompat.getColor(getApplicationContext(), R.color.almost_black_text));
            infoText.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.almost_black_text));
        }

        if(buyerAddress.length() != 16){
            changeLineColorEditText(buyerAddressInput, ContextCompat.getColor(getApplicationContext(), R.color.error_red));
            infoText.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.error_red));
            createToast("Your public address should be 16 characters long");
        }
        else{
            changeLineColorEditText(buyerAddressInput, ContextCompat.getColor(getApplicationContext(), R.color.almost_black_text));
            infoText.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.almost_black_text));
        }

        if(!TextUtils.isEmpty(buyerAddress) && buyerAddress.length() == 16){
            if(acceptCkBox.isChecked()){
                DatabaseReference TempRef = FirebaseDatabase.getInstance("https://gorillanft-d65f0-default-rtdb.europe-west1.firebasedatabase.app/").getReference()
                        .child("Checkout").child(Prevalent.onlineUsers.getUsername()).child("Temp");

                Map<String, Object> tempMap = new HashMap<>();
                tempMap.put("buyerAddress", buyerAddress);
                TempRef.updateChildren(tempMap);

                if(payWithCrypto.equals("clicked")){
                    createToast("You have chosen to pay in ETH");
                    ethPayment();
                }
                else if(payWithBankAccount.equals("clicked")){
                    createToast("You have chosen to pay with your bank account");
                    Intent intent = new Intent(CheckoutCryptoActivity.this, CheckoutCardActivity.class);
                    startActivity(intent);
                }
            }
            else{
                createToast("You need to accept the Terms and Conditions");
            }
        }

    }

    private void ethPayment() {
        progressDialog.setTitle("Processing the payment");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setMessage("Please wait while we are contacting your bank");
        progressDialog.show();

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("dd MM yyyy");
        saveCurrentDate = currentDate.format(calendar.getTime());
        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
        saveCurrentTime = currentTime.format(calendar.getTime());

        //generate random number for transaction key
        Random rand = new Random();
        int upperbound = 9999;
        int int_random = rand.nextInt(upperbound);

        transactionUniqueKey = String.valueOf(int_random);

        DatabaseReference TransactionRef = FirebaseDatabase.getInstance("https://gorillanft-d65f0-default-rtdb.europe-west1.firebasedatabase.app/").getReference()
                .child("Transactions");

        Map<String, Object> transactionMap = new HashMap<>();
        transactionMap.put("transactionId", transactionUniqueKey);
        transactionMap.put("nftQuantity", cartChildrenCount);
        transactionMap.put("buyer", Prevalent.onlineUsers.getUsername());
        transactionMap.put("buyerAddress", buyerAddressInput.getText().toString());
        transactionMap.put("transactionDate", saveCurrentDate);
        transactionMap.put("transactionTime", saveCurrentTime);
        transactionMap.put("worthDollar", totalDollarString);
        transactionMap.put("worthEth", totalEthString);
        transactionMap.put("paymentMethod", "Ethereum");

        TransactionRef.child(Prevalent.onlineUsers.getUsername()).child(transactionUniqueKey).updateChildren(transactionMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){

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

                                    DatabaseReference SellRef = FirebaseDatabase.getInstance("https://gorillanft-d65f0-default-rtdb.europe-west1.firebasedatabase.app/").getReference()
                                            .child("NFTs");

                                    SellRef.child(model.getId()).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            if(snapshot.exists()){
                                                SellRef.child(model.getId()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if(task.isSuccessful()){
                                                            DatabaseReference WishlistRef = FirebaseDatabase.getInstance("https://gorillanft-d65f0-default-rtdb.europe-west1.firebasedatabase.app/").getReference()
                                                                    .child("Wishlist").child(Prevalent.onlineUsers.getUsername())
                                                                    .child(model.getId());

                                                            WishlistRef.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                   if(task.isSuccessful()){
                                                                       DatabaseReference CartRef = FirebaseDatabase.getInstance("https://gorillanft-d65f0-default-rtdb.europe-west1.firebasedatabase.app/").getReference()
                                                                               .child("Cart List").child(Prevalent.onlineUsers.getUsername());
                                                                       CartRef.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                           @Override
                                                                           public void onComplete(@NonNull Task<Void> task) {
                                                                               if(task.isSuccessful()){
                                                                                   DatabaseReference TempRef = FirebaseDatabase.getInstance("https://gorillanft-d65f0-default-rtdb.europe-west1.firebasedatabase.app/").getReference()
                                                                                           .child("Checkout").child(Prevalent.onlineUsers.getUsername());
                                                                                   TempRef.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                       @Override
                                                                                       public void onComplete(@NonNull Task<Void> task) {
                                                                                           createToast("CONGRATULATION FOR YOUR PURCHASE!!!");
                                                                                           progressDialog.dismiss();
                                                                                           Intent intent = new Intent(CheckoutCryptoActivity.this, HomeActivity.class);
                                                                                           intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                                                           startActivity(intent);
                                                                                           finish();
                                                                                       }
                                                                                   });
                                                                               }
                                                                               else{
                                                                                   progressDialog.dismiss();
                                                                                   createToast("Something went wrong");
                                                                               }
                                                                           }
                                                                       });
                                                                   }
                                                                   else{
                                                                       progressDialog.dismiss();
                                                                       createToast("Something went wrong");
                                                                   }
                                                                }
                                                            });
                                                        }
                                                        else{
                                                            progressDialog.dismiss();
                                                            createToast("Something went wrong");
                                                        }
                                                    }
                                                });
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

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
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                totalEthString = snapshot.child("Checkout").child(Prevalent.onlineUsers.getUsername()).child("Temp").child("totalEth").getValue().toString();
                totalDollarString = snapshot.child("Checkout").child(Prevalent.onlineUsers.getUsername()).child("Temp").child("totalDollar").getValue().toString();

                ethTotalText.setText(totalEthString + " ETH");
                dollarTotalText.setText(totalDollarString + "$");


                cartChildrenCount = String.valueOf(snapshot.child("Cart List").child(Prevalent.onlineUsers.getUsername()).child("NFTs").getChildrenCount());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    public void createToastInfo(String someText){
        Toast toast = new Toast(getApplicationContext());
        toast.setGravity(Gravity.CENTER,0,0);

        Drawable d = ContextCompat.getDrawable(getApplicationContext(), R.drawable.toast_shape_info);

        TextView toastText = new TextView(CheckoutCryptoActivity.this);
        toastText.setBackground(d);
        toastText.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.info_grey_text));
        toastText.setPadding(35,20,35,20);
        toastText.setText(someText);
        toastText.setTextSize(14);
        toast.setView(toastText);
        toast.show();
        toast.setDuration(Toast.LENGTH_LONG + Toast.LENGTH_LONG);
    }

    public void createToast(String someText){
        Toast toast = new Toast(getApplicationContext());
        toast.setGravity(Gravity.CENTER,0,700);

        Drawable d = ContextCompat.getDrawable(getApplicationContext(), R.drawable.toast_shape);

        TextView toastText = new TextView(CheckoutCryptoActivity.this);
        toastText.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.toast_color));
        toastText.setBackground(d);
        toastText.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.error_red2));
        toastText.setPadding(35,20,35,20);
        toastText.setText(someText);
        toastText.setTextSize(14);

        toast.setView(toastText);
        toast.show();
    }

    public void changeLineColorEditText(EditText editText, int color){
        editText.setBackgroundTintList(ColorStateList.valueOf(color));
    }
}