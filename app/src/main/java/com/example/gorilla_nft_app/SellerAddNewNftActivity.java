package com.example.gorilla_nft_app;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import java.text.DecimalFormat;

import com.example.gorilla_nft_app.Prevalent.Prevalent;
import com.example.gorilla_nft_app.databinding.ActivitySellerAddNewNftBinding;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.core.utilities.Utilities;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.rey.material.widget.CheckBox;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

import cz.msebera.android.httpclient.Header;


public class SellerAddNewNftActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private Spinner spinnerCategory;
    TextView cancelBtn, priceCurrency, earningEth, earningDollars, payingEth, payingDollars,imageText,tokenText;
    String categorySelected, showPrice, nftName, tokenAddress, saveCurrentDate,
            saveCurrentTime, nftUniqueKey,currentUser, downloadImageUrl;
    Button tokenAddressInfo, priceInfo, feesInfo, finishBtn;
    DecimalFormat decimalFormat, ethDecimalFormat;
    Double price_of_1ETH, dollarPrice, priceCounter, dollarPriceFormatted, totalPaymentEth,
            gorillaFees, creatorFees, ethEarningFormatter,extractEth, ethEarning, dollarEarning,
            gorillaFeesDollar, creatorFeesDollar, totalPaymentDollars;
    EditText priceInput, tokenInput, nftNameInput;
    ImageView inputPreviewImage;
    private static final int galleryPick = 1;
    ActivityResultLauncher<String> takeImage;
    ActivitySellerAddNewNftBinding binding;
    Boolean userSelectedImage = false;
    StorageReference nftImageRef;
    CheckBox acceptConditions;
    DatabaseReference nftRef;
    ProgressDialog progressDialog;

    Uri imageUri;

    Animation scaleDown, scaleUp;

   // private final String BASE_URL = "http://api.coinlayer.com/api/live?access_key=ead1fc257461eb0e9bf6d129d871a787&symbols=BTC,ETH";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_add_new_nft);
        binding = ActivitySellerAddNewNftBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //getting reference from the database
        currentUser = Prevalent.onlineUsers.getUsername();
        nftImageRef = FirebaseStorage.getInstance().getReference().child("nft preview images");
        nftRef = FirebaseDatabase.getInstance("https://gorillanft-d65f0-default-rtdb.europe-west1.firebasedatabase.app/").getReference()
                .child("NFTs");


//        //test
//        test = (TextView) findViewById(R.id.test10);
//        test.setText(currentUser);

        //declare activity result launcher
        takeImage = registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
            @Override
            public void onActivityResult(Uri result) {
                binding.appSellerAddNftPreviewImage.setImageURI(result);
                if(result != null){
                    userSelectedImage = true;
                    imageUri = result;
                }
            }
        });

        //decimal format for price
        decimalFormat = new DecimalFormat("#.##");
        ethDecimalFormat = new DecimalFormat("#.####");

        //declare edit text
        priceInput = (EditText) findViewById(R.id.app__seller_add_nft_eth_price_input);
        nftNameInput = (EditText) findViewById(R.id.app__seller_add_nft_name_input);
        tokenInput = (EditText) findViewById(R.id.app__seller_add_nft_token_address_input);

        //declare text view
        earningEth = (TextView) findViewById(R.id.app__seller_add_nft_earning_eth);
        earningDollars = (TextView) findViewById(R.id.app__seller_add_nft_earning_dollars);
        payingEth = (TextView) findViewById(R.id.app__seller_add_nft_payment_eth);
        payingDollars = (TextView) findViewById(R.id.app__seller_add_nft_payment_dollars);
        imageText = (TextView) findViewById(R.id.app__seller_add_nft_preview_text);
        tokenText = (TextView) findViewById(R.id.token_text);

        //declare image input
        inputPreviewImage = (ImageView) findViewById(R.id.app__seller_add_nft_preview_image);


        /////////////////   THIS PART OF CODE IS FOR THE CRYPTO REAL TIME PRICE HTTP REQUEST /////////////////////////////////
        priceCurrency = (TextView) findViewById(R.id.app__seller_add_nft_price_currency_instance);

        priceCounter = 0.0;
        dollarPrice = 0.0;
        dollarPriceFormatted = 0.0;
        extractEth = 0.0;
        ethEarning = 0.0;
        dollarEarning = 0.0;
        gorillaFeesDollar = 0.0;
        creatorFeesDollar = 0.0;
        totalPaymentEth = 0.0;
        totalPaymentDollars = 0.0;

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


        //declare buttons
        cancelBtn = (TextView) findViewById(R.id.app__seller_add_nft_cancel_btn);
        tokenAddressInfo = (Button) findViewById(R.id.app__seller_add_nft_address_detail_btn);
        priceInfo = (Button) findViewById(R.id.app__seller_add_nft_price_detail_btn);
        feesInfo = (Button) findViewById(R.id.app__seller_add_nft_fees_detail_btn);
        finishBtn = (Button) findViewById(R.id.app__seller_add_nft_add_button);

        //declare spinner
        spinnerCategory = (Spinner) findViewById(R.id.app__seller_add_nft_spinner_container);
        spinnerCategory.setOnItemSelectedListener(this);
        String[] categoriesList = getResources().getStringArray(R.array.category_list);
        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, categoriesList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(adapter);

        //check box
        acceptConditions = (CheckBox) findViewById(R.id.app__seller_add_nft_checkbox_agree);

        //underlines
        cancelBtn.setPaintFlags(cancelBtn.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        //send user back if he cancel
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SellerAddNewNftActivity.this, sellMainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });

        //declare progress dialog
        progressDialog = new ProgressDialog(this);

        //set info click listener
        tokenAddressInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createToastInfo("- An ETH token address is 16 characters long\n" +
                        "- Copy the address from your wallet\n" +
                        "\n" +
                        "Example\n" +
                        "cd2a3d9f938e13cd947ec05abc7fe734df8dd826");
            }
        });

        priceInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createToastInfo("- The price must be in ETH\n" +
                        "- You can add decimal format numbers\n" +
                        "\n" +
                        "Example\n" +
                        "0.00455 ETH");
            }
        });

        feesInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createToastInfo("- You need to pay fees to Gorilla NFT\n" +
                        "- Creator royalty is a way of paying the artist for his/her job\n" +
                        "- You need to pay in ETH\n");
            }
        });

        priceInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                String s_eth;
                String s_dollar;
                String s_eth_pay;
                String s_dollar_pay;

                if(!TextUtils.isEmpty(s)){

                    //TextView below EditText
                    String cs_s = String.valueOf(s);
                    extractEth = Double.parseDouble(cs_s);
                    dollarPrice =  extractEth * price_of_1ETH;
                    dollarPriceFormatted = Double.parseDouble(decimalFormat.format(dollarPrice));
                    showPrice = s + " ETH = " + dollarPriceFormatted + "$";
                    priceCurrency.setText(showPrice);

                    //Fees sections
                    gorillaFees = (extractEth / 100) * 2.50;
                    creatorFees = (extractEth / 100) * 10;
                    gorillaFeesDollar = (dollarPriceFormatted / 100) * 10;
                    creatorFeesDollar = (dollarPriceFormatted / 100) * 10;

                    ethEarning = extractEth - gorillaFees - creatorFees;
                    ethEarningFormatter = Double.parseDouble(ethDecimalFormat.format(ethEarning));
                    s_eth = ethEarningFormatter + " ETH";
                    earningEth.setText(s_eth);

                    dollarEarning = dollarPriceFormatted - gorillaFeesDollar - creatorFeesDollar;
                    s_dollar = decimalFormat.format(dollarEarning) + "$";
                    earningDollars.setText(s_dollar);

                    totalPaymentEth = Double.parseDouble(ethDecimalFormat.format(gorillaFees)) + Double.parseDouble(ethDecimalFormat.format(gorillaFees));
                    s_eth_pay = totalPaymentEth + " ETH";
                    payingEth.setText(s_eth_pay);

                    totalPaymentDollars = Double.parseDouble(decimalFormat.format(gorillaFeesDollar)) + Double.parseDouble(decimalFormat.format(creatorFeesDollar));
                    s_dollar_pay = totalPaymentDollars + "$";
                    payingDollars.setText(s_dollar_pay);
                }
                else{
                    showPrice ="0 ETH = 0$";
                    priceCurrency.setText(showPrice);

                    s_eth = "0 ETH";
                    earningEth.setText(s_eth);

                    s_dollar = "0$";
                    earningDollars.setText(s_dollar);

                    s_eth_pay = "0 ETH";
                    payingEth.setText(s_eth_pay);

                    s_dollar_pay = "0 $";
                    payingDollars.setText(s_dollar_pay);
                }


            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        //image preview btn
        inputPreviewImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });

        //buttons active effect
        scaleDown = AnimationUtils.loadAnimation(this,R.anim.scale_down);
        scaleUp = AnimationUtils.loadAnimation(this,R.anim.scale_up);

        //finish button
        finishBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    finishBtn.startAnimation(scaleDown);
                }else if(event.getAction() == MotionEvent.ACTION_UP){
                    finishBtn.startAnimation(scaleUp);

                    payAndFinish();
                }
                return true;
            }
        });
    }

    private void payAndFinish() {
        nftName = nftNameInput.getText().toString().trim();
        tokenAddress = tokenInput.getText().toString().trim();

        if(!userSelectedImage){
            imageText.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.error_red));
            createToast("Image not selected");
        }
        else{
            imageText.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.almost_black_text));
        }

        if(TextUtils.isEmpty(nftName)){
            changeLineColorEditText(nftNameInput, ContextCompat.getColor(getApplicationContext(), R.color.error_red));
            createToast("name can't be empty");
        }
        else{
            changeLineColorEditText(nftNameInput, ContextCompat.getColor(getApplicationContext(), R.color.almost_black_text));
        }

        if(!TextUtils.isEmpty(tokenAddress) && tokenAddress.length() != 16){
            changeLineColorEditText(tokenInput, ContextCompat.getColor(getApplicationContext(), R.color.error_red));
            tokenText.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.error_red));
            createToast("Wrong token address");
        }
        else{
            changeLineColorEditText(tokenInput, ContextCompat.getColor(getApplicationContext(), R.color.almost_black_text));
            tokenText.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.almost_black_text));
        }

        if(priceInput.getText().toString().isEmpty()){
            changeLineColorEditText(priceInput, ContextCompat.getColor(getApplicationContext(), R.color.error_red));
            priceCurrency.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.error_red));
            createToast("You need to set a price");
        }
        else{
            changeLineColorEditText(priceInput, ContextCompat.getColor(getApplicationContext(), R.color.almost_black_text));
            priceCurrency.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.almost_black_text));
        }

        if(!acceptConditions.isChecked()){
            createToast("You need to accept the agreement");
        }

        if(userSelectedImage && !TextUtils.isEmpty(nftName) && !TextUtils.isEmpty(tokenAddress) && tokenAddress.length() == 16
        && !priceInput.getText().toString().isEmpty() && acceptConditions.isChecked()){

            storeNftInformation();

        }

    }

    private void storeNftInformation() {

        progressDialog.setTitle("Processing the payment");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setMessage("Please wait while we connect to your wallet");
        progressDialog.show();

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("dd MM yyyy");
        saveCurrentDate = currentDate.format(calendar.getTime());
        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
        saveCurrentTime = currentTime.format(calendar.getTime());

        nftUniqueKey = nftName + saveCurrentDate + saveCurrentTime;

        StorageReference filePath = nftImageRef.child(imageUri.getLastPathSegment() + nftUniqueKey);

        final UploadTask uploadTask = filePath.putFile(imageUri);

        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                createToast("ERROR");
                progressDialog.dismiss();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                createToast("The preview of your NFT is uploaded successfully");

                Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {

                        if(!task.isSuccessful()){
                            throw task.getException();

                        }

                        downloadImageUrl = filePath.getDownloadUrl().toString();
                        return filePath.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {

                        if(task.isSuccessful()){

                            createToast("NFT saved to the database");

                          downloadImageUrl = task.getResult().toString();

                            SaveNftInfoToDatabase();
                        }

                    }
                });
            }
        });

    }

    private void SaveNftInfoToDatabase() {
        Map<String, Object> sellNftMap = new HashMap<>();
        sellNftMap.put("id", nftUniqueKey);
        sellNftMap.put("date", saveCurrentDate);
        sellNftMap.put("time", saveCurrentTime);
        sellNftMap.put("preview", downloadImageUrl);
        sellNftMap.put("name", nftName);
        sellNftMap.put("category", categorySelected);
        sellNftMap.put("price", priceInput.getText().toString());
        sellNftMap.put("address", tokenAddress);
        sellNftMap.put("seller", Prevalent.onlineUsers.getUsername());

        nftRef.child(nftUniqueKey).updateChildren(sellNftMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if(task.isSuccessful()){
                    progressDialog.dismiss();
                    createToast("NFT added to your listing");
                    Intent intent = new Intent(SellerAddNewNftActivity.this, sellMainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }
                else{
                    progressDialog.dismiss();
                    String message = "ERROR: " + task.getException().toString();
                    createToast(message);
                }

            }
        });
    }

    private void selectImage() {
        takeImage.launch("image/*");

    }


    //drop menu spinner methods
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (parent.getId() == R.id.app__seller_add_nft_spinner_container){
            categorySelected = parent.getItemAtPosition(position).toString();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }


    //CRYPTO API
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

    //front end
    public void createToastInfo(String someText){
        Toast toast = new Toast(getApplicationContext());
        toast.setGravity(Gravity.CENTER,0,0);

        Drawable d = ContextCompat.getDrawable(getApplicationContext(), R.drawable.toast_shape_info);

        TextView toastText = new TextView(SellerAddNewNftActivity.this);
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

        TextView toastText = new TextView(SellerAddNewNftActivity.this);
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
