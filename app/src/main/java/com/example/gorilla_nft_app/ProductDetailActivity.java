package com.example.gorilla_nft_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gorilla_nft_app.Model.Products;
import com.example.gorilla_nft_app.Prevalent.Prevalent;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import cz.msebera.android.httpclient.Header;

public class ProductDetailActivity extends AppCompatActivity {

private Button backBtn, wishlistBtn, addToCartBtn;
private TextView nftNameText, nftSellerText, nftCategoryText, nftDateText, nftAddressText, ethPriceText,
        dollarPriceText, cartQuantityText;
private FloatingActionButton openCartBtn;
private ImageView nftPreviewImage;
private String nftID = "";
private Double price_of_1ETH, calculation_eth_to_dollar, formatDollarPrice, tempEth;
private DecimalFormat decimalFormat, ethDecimalFormat;
private String convertPriceEth, formatDollarPriceString, childrenCount, previewLink, addEthPriceToCart;
boolean cartUpdated = false;
private LinearLayout buttonCartLayout, checkTextLayout;
boolean toggleWishlist;
Animation scaleDown, scaleUp;
DatabaseReference WishlistRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        backBtn = (Button) findViewById(R.id.detail_back_btn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProductDetailActivity.this, HomeActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });

        //declare buttons
        wishlistBtn = (Button) findViewById(R.id.detail_wishlist_btn);
        addToCartBtn = (Button) findViewById(R.id.detail_add_to_cart_btn);
        openCartBtn = (FloatingActionButton) findViewById(R.id.detail_cart_floating_button);

        //declare text view
        nftNameText = (TextView) findViewById(R.id.detail_nft_name);
        nftSellerText = (TextView) findViewById(R.id.detail_nft_seller);
        nftCategoryText = (TextView) findViewById(R.id.detail_nft_category);
        nftDateText = (TextView) findViewById(R.id.detail_nft_date);
        nftAddressText = (TextView) findViewById(R.id.detail_nft_address);
        ethPriceText = (TextView) findViewById(R.id.detail_nft_price_eth);
        dollarPriceText = (TextView) findViewById(R.id.detail_nft_price_dollar);
        cartQuantityText = (TextView) findViewById(R.id.detail_cart_quantity);

        //declare image view
        nftPreviewImage = (ImageView) findViewById(R.id.detail_nft_preview);

        //declare linear layouts
        buttonCartLayout = (LinearLayout) findViewById(R.id.lld8);
        checkTextLayout = (LinearLayout) findViewById(R.id.lld9);

        /////////////////   THIS PART OF CODE IS FOR THE CRYPTO REAL TIME PRICE HTTP REQUEST /////////////////////////////////

        //decimal format for price
        decimalFormat = new DecimalFormat("#.##");
        ethDecimalFormat = new DecimalFormat("#.####");

        calculation_eth_to_dollar = 0.0;
        formatDollarPrice = 0.0;
        tempEth = 0.0;

        String BASE_URL = "http://api.coinlayer.com/api/live?access_key=ead1fc257461eb0e9bf6d129d871a787&symbols=BTC,ETH";

        String publicKey = "ead1fc257461eb0e9bf6d129d871a787";
        String finalUrl = BASE_URL + publicKey + "&TARGET=" +  "&symbols=ETH";
        Log.d("Clima", "Request fail! Status code: " + finalUrl);
        try {
            letsDoSomeNetworking(finalUrl);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //insert nft id for page view
        nftID = getIntent().getStringExtra("id");
        getProductDetails(nftID);

        scaleDown = AnimationUtils.loadAnimation(this,R.anim.scale_down);
        scaleUp = AnimationUtils.loadAnimation(this,R.anim.scale_up);

        addToCartBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    addToCartBtn.startAnimation(scaleDown);
                }else if(event.getAction() == MotionEvent.ACTION_UP){
                    addToCartBtn.startAnimation(scaleUp);

                    DatabaseReference UserRef = FirebaseDatabase.getInstance("https://gorillanft-d65f0-default-rtdb.europe-west1.firebasedatabase.app/").getReference()
                                    .child("NFTs").child(nftID);
                    UserRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(!snapshot.child("seller").getValue().equals(Prevalent.onlineUsers.getUsername())){
                                addNftToCart();
                                checkQuantityCart();
                            }
                            else{
                                createToast("You own this NFT");
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                }
                return true;
            }
        });

        // wish list section
        WishlistRef = FirebaseDatabase.getInstance("https://gorillanft-d65f0-default-rtdb.europe-west1.firebasedatabase.app/").getReference()
                        .child("Wishlist").child(Prevalent.onlineUsers.getUsername());

        wishlistCommunication();

        wishlistBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference UserRef = FirebaseDatabase.getInstance("https://gorillanft-d65f0-default-rtdb.europe-west1.firebasedatabase.app/").getReference()
                        .child("NFTs").child(nftID);
                UserRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(!snapshot.child("seller").getValue().equals(Prevalent.onlineUsers.getUsername())){
                            addOrRemoveFromWishList();
                        }
                        else{
                            createToast("You own this NFT");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });

        //open cart
        openCartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!cartQuantityText.getText().toString().equals("0")){
                    Intent intent = new Intent(ProductDetailActivity.this, CartActivity.class);
                    startActivity(intent);
                }
                else{
                    createToast("The cart is empty");
                }
            }
        });
    }

    private void addOrRemoveFromWishList() {
        if(toggleWishlist){
            WishlistRef.child(nftID).removeValue();
            toggleWishlist = false;
            wishlistBtn.setBackgroundResource(R.drawable.unwished);
            createToast("NFT removed to wishlist");
        }
        else{
            Map<String, Object> wishlistMap = new HashMap<>();
            wishlistMap.put("id", nftID);
            wishlistMap.put("name", nftNameText.getText().toString());
            wishlistMap.put("seller", nftSellerText.getText().toString());
            wishlistMap.put("price", ethPriceText.getText().toString());
            wishlistMap.put("address", nftAddressText.getText().toString());
            wishlistMap.put("category", nftCategoryText.getText().toString());
            wishlistMap.put("buyer", Prevalent.onlineUsers.getUsername());
            wishlistMap.put("preview", previewLink);

            WishlistRef.child(nftID).updateChildren(wishlistMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        toggleWishlist = true;
                        wishlistBtn.setBackgroundResource(R.drawable.wished);
                        createToast("NFT added to wishlist");
                    }
                }
            });
        }
    }

    private void wishlistCommunication() {

        WishlistRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child(nftID).exists()){
                    toggleWishlist = true;
                    wishlistBtn.setBackgroundResource(R.drawable.wished);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void addNftToCart() {

        String saveCurrentDate, saveCurrentTime;

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("dd MM yyyy");
        saveCurrentDate = currentDate.format(calendar.getTime());
        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
        saveCurrentTime = currentTime.format(calendar.getTime());

       final DatabaseReference CartListRef = FirebaseDatabase.getInstance("https://gorillanft-d65f0-default-rtdb.europe-west1.firebasedatabase.app/").getReference()
                .child("Cart List");

        Map<String, Object> cartMap = new HashMap<>();
        cartMap.put("id", nftID);
        cartMap.put("name", nftNameText.getText().toString());
        cartMap.put("seller", nftSellerText.getText().toString());
        cartMap.put("priceETH", addEthPriceToCart);
        cartMap.put("priceDollar", formatDollarPrice.toString());
        cartMap.put("address", nftAddressText.getText().toString());
        cartMap.put("date", saveCurrentDate);
        cartMap.put("time", saveCurrentTime);
        cartMap.put("category", nftCategoryText.getText().toString());
        cartMap.put("buyer", Prevalent.onlineUsers.getUsername());
        cartMap.put("preview", previewLink);

        CartListRef.child(Prevalent.onlineUsers.getUsername()).child("NFTs").child(nftID)
                .updateChildren(cartMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            createToast("NFT added to the cart");
                            cartUpdated = true;
                            Log.d("The cart is updated", String.valueOf(cartUpdated));
                        }
                    }
                });
    }

    private void getProductDetails(String id) {

        DatabaseReference NftRef = FirebaseDatabase.getInstance("https://gorillanft-d65f0-default-rtdb.europe-west1.firebasedatabase.app/").getReference()
                .child("NFTs");

        NftRef.child(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    Products products = snapshot.getValue(Products.class);

                    Picasso.get().load(products.getPreview()).into(nftPreviewImage);
                    nftNameText.setText(products.getName());
                    nftSellerText.setText(products.getSeller());
                    nftCategoryText.setText(products.getCategory());
                    nftDateText.setText(products.getDate());
                    nftAddressText.setText(products.getAddress());
                    ethPriceText.setText(products.getPrice() + " ETH");
                    previewLink = products.getPreview();

                    addEthPriceToCart = products.getPrice();

                    final Handler handler = new Handler();
                    Runnable refresh = new Runnable() {
                        @Override
                        public void run() {
                            calculation_eth_to_dollar = tempEth * Double.parseDouble(products.getPrice());
                            formatDollarPrice = Double.valueOf(decimalFormat.format(calculation_eth_to_dollar));
                            formatDollarPriceString = formatDollarPrice.toString() + "$";


                            dollarPriceText.setText(formatDollarPriceString);
                            handler.postDelayed(this, 250);
                        }
                    };
                    handler.postDelayed(refresh, 250);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        checkQuantityCart();
    }

    //crypto method
    private void letsDoSomeNetworking(String url) throws IOException, JSONException {
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(url, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                // called when response HTTP status is "200 OK"
                Log.d("Clima", "JSON: " + response.toString());
                try {
                    JSONObject price = response.getJSONObject("rates");
                    String object = price.getString("ETH");
                    price_of_1ETH = Double.parseDouble(object);
                    convertPriceEth = price_of_1ETH.toString();

                    final Handler handler = new Handler();
                    Runnable refresh = new Runnable() {
                        @Override
                        public void run() {
                            tempEth = price_of_1ETH;
                            handler.postDelayed(this, 5);
                        }
                    };
                    handler.postDelayed(refresh, 5);


                } catch (JSONException E) {
                    E.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable e, JSONObject response) {
                // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                Log.d("Clima", "Request fail! Status code: " + statusCode);
                Log.d("Clima", "Fail response: " + response);
                Log.e("ERROR", e.toString());
            }
        });
    }

    private void checkQuantityCart() {

        DatabaseReference QuantityCartRef = FirebaseDatabase.getInstance("https://gorillanft-d65f0-default-rtdb.europe-west1.firebasedatabase.app/").getReference()
                .child("Cart List").child(Prevalent.onlineUsers.getUsername()).child("NFTs");

        QuantityCartRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                childrenCount = String.valueOf(snapshot.getChildrenCount());
                cartQuantityText.setText(childrenCount);

                if(snapshot.child(nftID).exists()){
                    buttonCartLayout.setVisibility(View.INVISIBLE);
                    checkTextLayout.setVisibility(View.VISIBLE);
                }
                else{
                    buttonCartLayout.setVisibility(View.VISIBLE);
                    checkTextLayout.setVisibility(View.INVISIBLE);
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

        TextView toastText = new TextView(ProductDetailActivity.this);
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