package com.example.gorilla_nft_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gorilla_nft_app.Prevalent.Prevalent;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class AddNewCardActivity extends AppCompatActivity {

    private TextView cancelBtn, cardholderText, cardNumberText, expText, cvvText, countryText;
    private EditText cardholderInput, cardNumberInput, expMonthInput, expYearInput, cvvInput, countryInput;
    private Button saveCardBtn;
    private String cardholder, cardNumber, expMonth, expYear, cvv, country;
    DatabaseReference RootRef;
    Animation scaleDown, scaleUp;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_card);

        cancelBtn = (TextView) findViewById(R.id.app__add_card_cancel_btn);
        cancelBtn.setPaintFlags(cancelBtn.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddNewCardActivity.this, SettingActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });

        cardholderInput = (EditText) findViewById(R.id.cardholder_input);
        cardNumberInput = (EditText) findViewById(R.id.card_number_input);
        expMonthInput = (EditText) findViewById(R.id.card_exp_month_input);
        expYearInput = (EditText) findViewById(R.id.card_exp_year_input);
        cvvInput = (EditText) findViewById(R.id.card_cvv_input);
        countryInput = (EditText) findViewById(R.id.card_country_input);
        saveCardBtn = (Button) findViewById(R.id.save_card_btn);
        cardholderText = (TextView) findViewById(R.id.t1);
        cardNumberText = (TextView) findViewById(R.id.t2);
        expText = (TextView) findViewById(R.id.t3);
        cvvText = (TextView) findViewById(R.id.t4);
        countryText = (TextView) findViewById(R.id.t5);

        progressDialog = new ProgressDialog(this);

        RootRef = FirebaseDatabase.getInstance("https://gorillanft-d65f0-default-rtdb.europe-west1.firebasedatabase.app/").getReference()
                .child("Users").child(Prevalent.onlineUsers.getUsername()).child("Saved Cards");


        scaleDown = AnimationUtils.loadAnimation(this,R.anim.scale_down);
        scaleUp = AnimationUtils.loadAnimation(this,R.anim.scale_up);

        //animation and intent
        saveCardBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    saveCardBtn.startAnimation(scaleDown);
                }else if(event.getAction() == MotionEvent.ACTION_UP){
                    saveCardBtn.startAnimation(scaleUp);

                    checkInput();
                }
                return true;
            }
        });
    }

    private void checkInput() {

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
            addCardToDatabase();
        }
        else{
            createToast("Something is wrong");
        }
    }

    private void addCardToDatabase() {

        progressDialog.setTitle("Contacting your bank");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setMessage("Please wait...");
        progressDialog.show();

        String last4digits = cardNumber.substring(cardNumber.length() - 4);

        Map<String, Object> userDataMap = new HashMap<>();
        userDataMap.put("cardholder", cardholder);
        userDataMap.put("cardNumber", cardNumber);
        userDataMap.put("endingNumber", last4digits);
        userDataMap.put("expMonth", expMonth);
        userDataMap.put("expYear", expYear);
        userDataMap.put("country", country);

        RootRef.child(cardNumber).updateChildren(userDataMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){

                    createToast("Card saved successfully");
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            progressDialog.dismiss();
                            startActivity(new Intent(AddNewCardActivity.this, SettingActivity.class));
                            finish();
                        }
                    },600);
                }
            }
        });

    }

    //front end
    public void createToast(String someText){
        Toast toast = new Toast(getApplicationContext());
        toast.setGravity(Gravity.CENTER,0,700);

        Drawable d = ContextCompat.getDrawable(getApplicationContext(), R.drawable.toast_shape);

        TextView toastText = new TextView(AddNewCardActivity.this);
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