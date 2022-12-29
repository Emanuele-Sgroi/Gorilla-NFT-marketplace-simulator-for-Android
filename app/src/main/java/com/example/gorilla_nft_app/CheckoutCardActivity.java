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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gorilla_nft_app.Model.Cards;
import com.example.gorilla_nft_app.Model.Cart;
import com.example.gorilla_nft_app.Prevalent.Prevalent;
import com.example.gorilla_nft_app.ViewHolder.CardViewHolder;
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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class CheckoutCardActivity extends AppCompatActivity {

    private TextView cancelBtn, cardholderText, cardNumberText, expText, cvvText, countryText, dollarTotalText, savedCardText;
    private EditText cardholderInput, cardNumberInput, expMonthInput, expYearInput, cvvInput, countryInput;
    private Button payNowBtn;
    private CheckBox useNewCardCkBox;
    private String cardholder, cardNumber, expMonth, expYear, cvv, country, totalDollarString, totalEthString, buyerAddress
            ,cartChildrenCount, address, buyer, category, date, time, id, name, preview, priceDollar, priceETH, seller
            ,transactionUniqueKey, saveCurrentTime, saveCurrentDate;
    private RelativeLayout newCardContainer;
    Animation scaleDown, scaleUp;
    private ProgressDialog progressDialog;
    private RecyclerView recyclerView, recyclerViewInvisible;
    RecyclerView.LayoutManager layoutManager, layoutManagerInvisible;
    boolean checkBoxChecked = false;
    private DatabaseReference RootRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout_card);

        cancelBtn = (TextView) findViewById(R.id.checkout_card_cancel_btn);
        cancelBtn.setPaintFlags(cancelBtn.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DatabaseReference TempRef = FirebaseDatabase.getInstance("https://gorillanft-d65f0-default-rtdb.europe-west1.firebasedatabase.app/").getReference()
                        .child("Checkout").child(Prevalent.onlineUsers.getUsername());

                TempRef.removeValue();

                Intent intent = new Intent(CheckoutCardActivity.this, CartActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });

        //declare progress dialog
        progressDialog = new ProgressDialog(this);

        //general database reference
        RootRef = FirebaseDatabase.getInstance("https://gorillanft-d65f0-default-rtdb.europe-west1.firebasedatabase.app/").getReference();

        //Edit Text
        cardholderInput = (EditText) findViewById(R.id.checkout_cardholder_input);
        cardNumberInput = (EditText) findViewById(R.id.checkout_card_number_input);
        expMonthInput = (EditText) findViewById(R.id.checkout_card_exp_month_input);
        expYearInput = (EditText) findViewById(R.id.checkout_card_exp_year_input);
        cvvInput = (EditText) findViewById(R.id.checkout_card_cvv_input);
        countryInput = (EditText) findViewById(R.id.checkout_card_country_input);

        //Text Views
        cardholderText = (TextView) findViewById(R.id.checkout_t1);
        cardNumberText = (TextView) findViewById(R.id.checkout_t2);
        expText = (TextView) findViewById(R.id.checkout_t3);
        cvvText = (TextView) findViewById(R.id.checkout_t4);
        countryText = (TextView) findViewById(R.id.checkout_t5);
        dollarTotalText = (TextView) findViewById(R.id.checkout_dollar_total_text);

        //Buttons
        payNowBtn = (Button) findViewById(R.id.checkout_pay_now_button);

        //checkbox
        useNewCardCkBox = (CheckBox) findViewById(R.id.checkout_new_card);

        //Relative layouts
        newCardContainer = (RelativeLayout)findViewById(R.id.new_card_container);

        //recycler view
        recyclerView = findViewById(R.id.checkout_saved_card);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        //recycler view invisible
        recyclerViewInvisible = (RecyclerView) findViewById(R.id.invisible_rw_card);
        recyclerViewInvisible.setHasFixedSize(true);
        layoutManagerInvisible = new LinearLayoutManager(this);
        recyclerViewInvisible.setLayoutManager(layoutManagerInvisible);

        //new card layout
        useNewCardCkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkBoxChecked = !checkBoxChecked;
                if(checkBoxChecked){
                    newCardContainer.setVisibility(View.VISIBLE);
                    useNewCardCkBox.setChecked(true);
                }
                else{
                    newCardContainer.setVisibility(View.INVISIBLE);
                    useNewCardCkBox.setChecked(false);
                }
            }
        });


        //pay now listener
        scaleDown = AnimationUtils.loadAnimation(this,R.anim.scale_down);
        scaleUp = AnimationUtils.loadAnimation(this,R.anim.scale_up);
        payNowBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    payNowBtn.startAnimation(scaleDown);
                }else if(event.getAction() == MotionEvent.ACTION_UP){
                    payNowBtn.startAnimation(scaleUp);
                    checkPaymentMethod();
                }
                return true;
            }
        });
    }

    private void checkPaymentMethod() {
        if(checkBoxChecked){
            checkCardInfo();
        }
        else{
            finalPayment();
        }
    }

    private void finalPayment() {

        progressDialog.setTitle("Processing the payment");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setMessage("Please wait while we connect to your wallet");
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
        transactionMap.put("buyerAddress", buyerAddress);
        transactionMap.put("transactionDate", saveCurrentDate);
        transactionMap.put("transactionTime", saveCurrentTime);
        transactionMap.put("worthDollar", totalDollarString);
        transactionMap.put("worthEth", totalEthString);
        transactionMap.put("paymentMethod", "Bank Account");

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
                                                                                            Intent intent = new Intent(CheckoutCardActivity.this, HomeActivity.class);
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

    private void checkCardInfo() {
        cardholder = cardholderInput.getText().toString();
        cardNumber = cardNumberInput.getText().toString();
        expMonth = expMonthInput.getText().toString();
        expYear = expYearInput.getText().toString();
        cvv = cvvInput.getText().toString();
        country = countryInput.getText().toString();

        if(TextUtils.isEmpty(cardholder)){
            changeLineColorEditText(cardholderInput, ContextCompat.getColor(getApplicationContext(), R.color.error_red));
            cardholderText.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.error_red));
        }
        else{
            changeLineColorEditText(cardholderInput, ContextCompat.getColor(getApplicationContext(), R.color.almost_black_text));
            cardholderText.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.almost_black_text));
        }

        if(TextUtils.isEmpty(cardNumber)){
            changeLineColorEditText(cardNumberInput, ContextCompat.getColor(getApplicationContext(), R.color.error_red));
            cardNumberText.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.error_red));
        }
        else{
            changeLineColorEditText(cardholderInput, ContextCompat.getColor(getApplicationContext(), R.color.almost_black_text));
            cardholderText.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.almost_black_text));
        }

        if(cardNumber.length() != 16){
            changeLineColorEditText(cardNumberInput, ContextCompat.getColor(getApplicationContext(), R.color.error_red));
            cardNumberText.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.error_red));
        }
        else{
            changeLineColorEditText(cardNumberInput, ContextCompat.getColor(getApplicationContext(), R.color.almost_black_text));
            cardNumberText.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.almost_black_text));
        }

        if(TextUtils.isEmpty(expMonth)){
            changeLineColorEditText(expMonthInput, ContextCompat.getColor(getApplicationContext(), R.color.error_red));
            expText.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.error_red));
        }
        else{
            changeLineColorEditText(expMonthInput, ContextCompat.getColor(getApplicationContext(), R.color.almost_black_text));
            expText.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.almost_black_text));
        }

        if(expMonth.length() != 2){
            changeLineColorEditText(expMonthInput, ContextCompat.getColor(getApplicationContext(), R.color.error_red));
            expText.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.error_red));
        }
        else{
            changeLineColorEditText(expMonthInput, ContextCompat.getColor(getApplicationContext(), R.color.almost_black_text));
            expText.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.almost_black_text));
        }

        if(TextUtils.isEmpty(expYear)){
            changeLineColorEditText(expYearInput, ContextCompat.getColor(getApplicationContext(), R.color.error_red));
            expText.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.error_red));
        }
        else{
            changeLineColorEditText(expYearInput, ContextCompat.getColor(getApplicationContext(), R.color.almost_black_text));
            expText.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.almost_black_text));
        }

        if(expYear.length() != 4){
            changeLineColorEditText(expYearInput, ContextCompat.getColor(getApplicationContext(), R.color.error_red));
            expText.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.error_red));
        }
        else{
            changeLineColorEditText(expYearInput, ContextCompat.getColor(getApplicationContext(), R.color.almost_black_text));
            expText.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.almost_black_text));
        }

        if(TextUtils.isEmpty(cvv)){
            changeLineColorEditText(cvvInput, ContextCompat.getColor(getApplicationContext(), R.color.error_red));
            cvvText.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.error_red));
        }
        else{
            changeLineColorEditText(cvvInput, ContextCompat.getColor(getApplicationContext(), R.color.almost_black_text));
            cvvText.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.almost_black_text));
        }

        if(cvv.length() != 3){
            changeLineColorEditText(cvvInput, ContextCompat.getColor(getApplicationContext(), R.color.error_red));
            cvvText.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.error_red));
        }
        else{
            changeLineColorEditText(cvvInput, ContextCompat.getColor(getApplicationContext(), R.color.almost_black_text));
            cvvText.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.almost_black_text));
        }

        if(TextUtils.isEmpty(country)){
            changeLineColorEditText(countryInput, ContextCompat.getColor(getApplicationContext(), R.color.error_red));
            countryText.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.error_red));
        }
        else{
            changeLineColorEditText(countryInput, ContextCompat.getColor(getApplicationContext(), R.color.almost_black_text));
            countryText.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.almost_black_text));
        }

        if(!TextUtils.isEmpty(cardholder) && !TextUtils.isEmpty(cardNumber) && cardNumber.length() == 16 && !TextUtils.isEmpty(expMonth)
                && expMonth.length() == 2 && !TextUtils.isEmpty(expYear) && expYear.length() == 4 && !TextUtils.isEmpty(cvv) && cvv.length() == 3
                && !TextUtils.isEmpty(country)){
            finalPayment();
        }
        else{
            createToast("Something is wrong");
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        DatabaseReference DollarPriceRef = FirebaseDatabase.getInstance("https://gorillanft-d65f0-default-rtdb.europe-west1.firebasedatabase.app/").getReference();

        DollarPriceRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                totalDollarString = snapshot.child("Checkout").child(Prevalent.onlineUsers.getUsername()).child("Temp").child("totalDollar").getValue().toString();
                dollarTotalText.setText(totalDollarString + "$");
                totalEthString = snapshot.child("Checkout").child(Prevalent.onlineUsers.getUsername()).child("Temp").child("totalEth").getValue().toString();
                buyerAddress = snapshot.child("Checkout").child(Prevalent.onlineUsers.getUsername()).child("Temp").child("buyerAddress").getValue().toString();

                cartChildrenCount = String.valueOf(snapshot.child("Cart List").child(Prevalent.onlineUsers.getUsername()).child("NFTs").getChildrenCount());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        DatabaseReference CardRef = FirebaseDatabase.getInstance("https://gorillanft-d65f0-default-rtdb.europe-west1.firebasedatabase.app/").getReference()
                .child("Users").child(Prevalent.onlineUsers.getUsername()).child("Saved Cards");

        FirebaseRecyclerOptions<Cards> options =
                new FirebaseRecyclerOptions.Builder<Cards>()
                        .setQuery(CardRef, Cards.class).build();

        FirebaseRecyclerAdapter<Cards, CardViewHolder> adapter =
                new FirebaseRecyclerAdapter<Cards, CardViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull CardViewHolder holder, int position, @NonNull Cards model) {

                        String cardNumber = model.getCardNumber();
                        String cardViewTextString = "************"+ model.getEndingNumber();
                        String cardholder = model.getCardholder();
                        String country = model.getCountry();
                        String expMonth = model.getExpMonth();
                        String expYear = model.getExpYear();
                        holder.checkoutCardViewText.setText(cardViewTextString);
                    }

                    @NonNull
                    @Override
                    public CardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.checkout_card_layout, parent,false);
                        CardViewHolder holder = new CardViewHolder(view);
                        return holder;
                    }
                };
        recyclerView.setAdapter(adapter);
        adapter.startListening();


    }

    public void changeLineColorEditText(EditText editText, int color){
        editText.setBackgroundTintList(ColorStateList.valueOf(color));
    }
    public void createToast(String someText){
        Toast toast = new Toast(getApplicationContext());
        toast.setGravity(Gravity.CENTER,0,700);

        Drawable d = ContextCompat.getDrawable(getApplicationContext(), R.drawable.toast_shape);

        TextView toastText = new TextView(CheckoutCardActivity.this);
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