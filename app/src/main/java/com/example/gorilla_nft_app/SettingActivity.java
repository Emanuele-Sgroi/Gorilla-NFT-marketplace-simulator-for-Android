package com.example.gorilla_nft_app;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gorilla_nft_app.Model.Cards;
import com.example.gorilla_nft_app.Model.Products;
import com.example.gorilla_nft_app.Model.Users;
import com.example.gorilla_nft_app.Prevalent.Prevalent;
import com.example.gorilla_nft_app.ViewHolder.CardViewHolder;
import com.example.gorilla_nft_app.ViewHolder.ProductViewHolder;
import com.example.gorilla_nft_app.databinding.ActivitySellerAddNewNftBinding;
import com.example.gorilla_nft_app.databinding.ActivitySettingBinding;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;
import io.paperdb.Paper;

public class SettingActivity extends AppCompatActivity {

    private Button goBackBtn;
    private ImageView editUsernameBtn, cancelUsernameBtn, saveUsernameBtn, editEmailBtn, cancelEmailBtn, saveEmailBtn,
            editPhoneBtn, cancelPhoneBtn, savePhoneBtn, editPasswordBtn, cancelPasswordBtn, savePasswordBtn, editImageBtn,
            saveImageBtn;
    private EditText newUsernameInput, newEmailInput, newPhoneInput, newPasswordInput;
    private LinearLayout usernameTextLl, usernameEditLl, emailTextLl, emailEditLl, phoneTextLl, phoneEditLl,
            passwordTextLl, passwordEditLl;
    private TextView usernameText, emailText, phoneText, addNewCardBtn, cardDeleteBtn;
    private String username, email, phone, password, newUsername, newEmail, newPhone, newPassword, downloadImageUrl;
    private ProgressDialog progressDialog;
    boolean passwordIsVisible;
    boolean userSelectedImage = true;
    private CircleImageView profilePictureView;
    String profilePictureEdited = "no";
    Uri imageUri;
    ActivityResultLauncher<String> takeImage;
    ActivitySettingBinding binding;
    StorageReference storageProfilePictureRef;
    DatabaseReference RootRef, CardRef;
    private RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    private int cardsChildrenCounter = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        binding = ActivitySettingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        progressDialog = new ProgressDialog(this);
        goBackBtn = (Button) findViewById(R.id.app__settings_back_btn);
        goBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingActivity.this, HomeActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });

        //code starts here

        //profile picture
        profilePictureView = (CircleImageView) findViewById(R.id.app__setting_user_profile_picture);

        //all linear layouts
        usernameTextLl = (LinearLayout) findViewById(R.id.ll1);
        usernameEditLl = (LinearLayout) findViewById(R.id.ll1_username);
        emailTextLl = (LinearLayout) findViewById(R.id.ll2);
        emailEditLl = (LinearLayout) findViewById(R.id.ll2_email);
        phoneTextLl = (LinearLayout) findViewById(R.id.ll3);
        phoneEditLl = (LinearLayout) findViewById(R.id.ll3_phone);
        passwordTextLl = (LinearLayout) findViewById(R.id.ll4);
        passwordEditLl = (LinearLayout) findViewById(R.id.ll4_password);

        //edit, save and cancel buttons (Could be ImageView)
        editUsernameBtn = (ImageView) findViewById(R.id.app__setting_edit_username_btn);
        cancelUsernameBtn = (ImageView) findViewById(R.id.app__setting_edit_username_cancel_btn);
        saveUsernameBtn = (ImageView) findViewById(R.id.app__setting_edit_username_save_btn);
        editEmailBtn = (ImageView) findViewById(R.id.app__setting_edit_email_btn);
        cancelEmailBtn = (ImageView) findViewById(R.id.app__setting_edit_email_cancel_btn);
        saveEmailBtn = (ImageView) findViewById(R.id.app__setting_edit_email_save_btn);
        editPhoneBtn = (ImageView) findViewById(R.id.app__setting_edit_phone_btn);
        cancelPhoneBtn = (ImageView) findViewById(R.id.app__setting_edit_phone_cancel_btn);
        savePhoneBtn = (ImageView) findViewById(R.id.app__setting_edit_phone_save_btn);
        editPasswordBtn = (ImageView) findViewById(R.id.app__setting_edit_password_btn);
        cancelPasswordBtn = (ImageView) findViewById(R.id.app__setting_edit_password_cancel_btn);
        savePasswordBtn = (ImageView) findViewById(R.id.app__setting_edit_password_save_btn);
        editImageBtn = (ImageView) findViewById(R.id.app__setting_edit_image_btn);
        saveImageBtn = (ImageView) findViewById(R.id.app__setting_save_image_btn);

        addNewCardBtn = (TextView) findViewById(R.id.app__setting_add_new_card_btn);

        //edit text
        newUsernameInput = (EditText) findViewById(R.id.app__setting_username_edit_input);
        newEmailInput = (EditText) findViewById(R.id.app__setting_email_edit_input);
        newPhoneInput = (EditText) findViewById(R.id.app__setting_phone_edit_input);
        newPasswordInput = (EditText) findViewById(R.id.app__setting_password_edit_input);

        usernameText = (TextView) findViewById(R.id.app__setting_username_text);
        emailText = (TextView) findViewById(R.id.app__setting_email_text);
        phoneText = (TextView) findViewById(R.id.app__setting_phone_text);

        Paper.init(this);
        username = Prevalent.onlineUsers.getUsername();
        storageProfilePictureRef = FirebaseStorage.getInstance().getReference().child("profile pictures");
        RootRef = FirebaseDatabase.getInstance("https://gorillanft-d65f0-default-rtdb.europe-west1.firebasedatabase.app/").getReference()
                .child("Users");
        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    email = snapshot.child(username).child("email").getValue().toString();
                    phone = snapshot.child(username).child("phone").getValue().toString();
                    password = snapshot.child(username).child("password").getValue().toString();

                    usernameText.setText(username);
                    emailText.setText(email);
                    phoneText.setText(phone);
                }

                if(snapshot.child(username).child("picture").exists()){
                    String picture = snapshot.child(username).child("picture").getValue().toString();
                    Picasso.get().load(picture).into(profilePictureView);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //edit username section
        editUsernameBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                usernameTextLl.setVisibility(View.INVISIBLE);
                usernameEditLl.setVisibility(View.VISIBLE);
            }
        });

        cancelUsernameBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                usernameTextLl.setVisibility(View.VISIBLE);
                usernameEditLl.setVisibility(View.INVISIBLE);
                newUsernameInput.setText(null);
            }
        });

        saveUsernameBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                newUsername = newUsernameInput.getText().toString().toLowerCase().trim();

                if(TextUtils.isEmpty(newUsername)){
                    createToast("Invalid username");
                }

                if(username.length() < 4 || username.length() > 15){
                    createToast("Invalid username");
                }

                if(!TextUtils.isEmpty(newUsername) && username.length() >= 4 && username.length() <= 15){
                    RootRef.child(username).removeValue();
                    updateNewUsername();
                }
            }
        });

        //edit email section
        editEmailBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                emailTextLl.setVisibility(View.INVISIBLE);
                emailEditLl.setVisibility(View.VISIBLE);
            }
        });

        cancelEmailBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                emailTextLl.setVisibility(View.VISIBLE);
                emailEditLl.setVisibility(View.INVISIBLE);
                newEmailInput.setText(null);
            }
        });
        saveEmailBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                newEmail = newEmailInput.getText().toString().toLowerCase().trim();

                if(TextUtils.isEmpty(newEmail)){
                    createToast("Invalid email address");
                }

                if(!newEmail.contains("@") || newEmail.length() < 6 || !newEmail.contains(".")){
                    createToast("Invalid username");
                }

                if(!TextUtils.isEmpty(newEmail) && newEmail.contains("@") && newEmail.length() >= 6 && newEmail.contains(".")){
                    updateNewEmail();
                }
            }
        });

        //edit phone number section
        editPhoneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                phoneTextLl.setVisibility(View.INVISIBLE);
                phoneEditLl.setVisibility(View.VISIBLE);
            }
        });

        cancelPhoneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                phoneTextLl.setVisibility(View.VISIBLE);
                phoneEditLl.setVisibility(View.INVISIBLE);
                newPhoneInput.setText(null);
            }
        });
        savePhoneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                newPhone = newPhoneInput.getText().toString().toLowerCase().trim();

                if(TextUtils.isEmpty(newPhone)){
                    createToast("Invalid Phone Number");
                }

                if(newPhone.length() < 8 || newPhone.length() > 12){
                    createToast("Invalid Phone Number");
                }

                if(!TextUtils.isEmpty(newPhone) && newPhone.length() >= 8 && newPhone.length() <= 12){
                    updateNewPhoneNumber();
                }
            }
        });

        //edit password section
        newPasswordInput.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DrawableRight=2;
                if(event.getAction() == MotionEvent.ACTION_UP){
                    if(event.getRawX() >= newPasswordInput.getRight() - newPasswordInput.getCompoundDrawables()[DrawableRight].getBounds().width()){
                        int elementSelected = newPasswordInput.getSelectionEnd();
                        if(passwordIsVisible){
                            newPasswordInput.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0, R.drawable.icon_visible_off, 0);
                            newPasswordInput.setTransformationMethod(PasswordTransformationMethod.getInstance());
                            passwordIsVisible = false;
                        }else{
                            newPasswordInput.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0, R.drawable.icon_visible, 0);
                            newPasswordInput.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                            passwordIsVisible = true;
                        }
                        newPasswordInput.setSelection(elementSelected);
                        return true;
                    }
                }
                return false;
            }
        });

        editPasswordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                passwordTextLl.setVisibility(View.INVISIBLE);
                passwordEditLl.setVisibility(View.VISIBLE);
            }
        });

        cancelPasswordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                passwordTextLl.setVisibility(View.VISIBLE);
                passwordEditLl.setVisibility(View.INVISIBLE);
                newPasswordInput.setText(null);
            }
        });
        savePasswordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                newPassword = newPasswordInput.getText().toString().trim();

                if(TextUtils.isEmpty(newPassword)){
                    createToast("Invalid Password");
                }

                if(newPassword.length() < 6 || newPassword.length() > 15){
                    createToast("Invalid Password");
                }

                if(!TextUtils.isEmpty(newPassword) && newPassword.length() >= 6 && newPassword.length() <= 15){
                    updateNewPassword();
                }
            }
        });

        //edit image section
        takeImage = registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
            @Override
            public void onActivityResult(Uri result) {
                binding.appSettingUserProfilePicture.setImageURI(result);
                if(result != null){
                    userSelectedImage = true;
                    imageUri = result;
                }
            }
        });

        editImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });



        saveImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                storeImage();
            }
        });


        //bank cards section
        addNewCardBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                checkNumberCards();

            }
        });

        //card reference
        CardRef = FirebaseDatabase.getInstance("https://gorillanft-d65f0-default-rtdb.europe-west1.firebasedatabase.app/").getReference()
                .child("Users").child(Prevalent.onlineUsers.getUsername()).child("Saved Cards");

        //recycler view
        recyclerView = findViewById(R.id.setting_card_recycleview);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

    }

    private void checkNumberCards() {

        CardRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                cardsChildrenCounter = Integer.parseInt(String.valueOf(snapshot.getChildrenCount()));

                if(!snapshot.exists() || cardsChildrenCounter == 0){
                    Intent intent = new Intent(SettingActivity.this, AddNewCardActivity.class);
                    startActivity(intent);
                }
                else if(snapshot.exists() && cardsChildrenCounter == 1){
                    createToast("You can save only one card at a time");
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
                        Log.d("cardholder", cardholder);
                        Log.d("country", country);
                        Log.d("expMonth", expMonth);
                        Log.d("expYear", expYear);
                        Log.d("card number", cardNumber);
                        holder.cardViewText.setText(cardViewTextString);
                        holder.cardDeleteBtn.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {

                                            DatabaseReference SellingRef = FirebaseDatabase.getInstance("https://gorillanft-d65f0-default-rtdb.europe-west1.firebasedatabase.app/").getReference()
                                                    .child("NFTs");

                                            SellingRef.orderByChild("seller").equalTo(Prevalent.onlineUsers.getUsername()).addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                    if(snapshot.exists()){
                                                        createToast("You can't delete this card if you are currently selling an NFT");
                                                    }
                                                    else{
                                                        CardRef.child(model.getCardNumber()).removeValue();
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {

                                                }
                                            });

                                        }
                                    });
                    }

                    @NonNull
                    @Override
                    public CardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_layout, parent,false);
                        CardViewHolder holder = new CardViewHolder(view);
                        return holder;
                    }
                };
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    private void selectImage() {

        takeImage.launch("image/*");

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                profilePictureEdited = "changed";
                if(profilePictureEdited.equals("changed")){
                    saveImageBtn.setVisibility(View.VISIBLE);
                }
            }
        },500);



    }

    private void storeImage() {
        progressDialog.setTitle("Updating your profile picture");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setMessage("Please wait..");
        progressDialog.show();

        StorageReference filePath = storageProfilePictureRef.child(imageUri.getLastPathSegment() + username);
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

                            downloadImageUrl = task.getResult().toString();

                            SaveImageToDatabase();
                        }

                    }
                });
            }
        });
    }

    private void SaveImageToDatabase() {
        Map<String, Object> pictureMap = new HashMap<>();
        pictureMap.put("picture", downloadImageUrl);

        RootRef.child(username).updateChildren(pictureMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if(task.isSuccessful()){
                    progressDialog.dismiss();
                    createToast("Profile picture updated");

                    RootRef.child(username).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.child("picture").exists()){
                                String picture = snapshot.child("picture").getValue().toString();
                                Picasso.get().load(picture).into(profilePictureView);
                                saveImageBtn.setVisibility(View.INVISIBLE);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                }
                else{
                    progressDialog.dismiss();
                    String message = "ERROR: " + task.getException().toString();
                    createToast(message);
                }

            }
        });
    }


    private void updateNewPassword() {
        Paper.book().destroy();

        progressDialog.setTitle("Updating Database");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setMessage("Please wait...");
        progressDialog.show();

        Map<String, Object> userDataMap = new HashMap<>();
        userDataMap.put("password", newPassword);

        RootRef.child(username).updateChildren(userDataMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    progressDialog.dismiss();
                    createToast("Password updated");

                    Intent intent = new Intent(SettingActivity.this,MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();

                    createToast("You need to login again");
                }
                else{
                    progressDialog.dismiss();
                    createToast("ERROR");
                }
            }
        });
    }

    private void updateNewPhoneNumber() {

        progressDialog.setTitle("Updating Database");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setMessage("Please wait...");
        progressDialog.show();

        Map<String, Object> userDataMap = new HashMap<>();
        userDataMap.put("phone", newPhone);

        RootRef.child(username).updateChildren(userDataMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    progressDialog.dismiss();
                    createToast("Phone number updated");
                    phoneTextLl.setVisibility(View.VISIBLE);
                    phoneEditLl.setVisibility(View.INVISIBLE);
                    phoneText.setText(newPhone);
                    newPhoneInput.setText(null);
                }
                else{
                    progressDialog.dismiss();
                    createToast("ERROR");
                }
            }
        });

    }

    private void updateNewEmail() {
        progressDialog.setTitle("Updating Database");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setMessage("Please wait...");
        progressDialog.show();

        Map<String, Object> userDataMap = new HashMap<>();
        userDataMap.put("email", newEmail);

        RootRef.child(username).updateChildren(userDataMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    progressDialog.dismiss();
                    createToast("Email updated");
                    emailTextLl.setVisibility(View.VISIBLE);
                    emailEditLl.setVisibility(View.INVISIBLE);
                    emailText.setText(newEmail);
                    newEmailInput.setText(null);
                }
                else{
                    progressDialog.dismiss();
                    createToast("ERROR");
                }
            }
        });


    }

    private void updateNewUsername() {
       Paper.book().destroy();

        progressDialog.setTitle("Updating Database");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setMessage("Please wait...");
        progressDialog.show();


        DatabaseReference RootRefNew = FirebaseDatabase.getInstance("https://gorillanft-d65f0-default-rtdb.europe-west1.firebasedatabase.app/").getReference().child("Users");

        RootRefNew.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                Map<String, Object> userDataMap = new HashMap<>();
                userDataMap.put("email", email);
                userDataMap.put("username", newUsername);
                userDataMap.put("phone", phone);
                userDataMap.put("password", password);

                RootRefNew.child(newUsername).updateChildren(userDataMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Users userData = snapshot.child(newUsername).getValue(Users.class);
                            Prevalent.onlineUsers = userData;
                            usernameText.setText(newUsername);
                            emailText.setText(email);
                            phoneText.setText(phone);
                            progressDialog.dismiss();
                            RootRef.child(username).removeValue();
                            createToast("Username Updated");
                            Intent intent = new Intent(SettingActivity.this,MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();

                            createToast("You need to login again");
                        }
                        else{
                            progressDialog.dismiss();
                            createToast("ERROR");
                        }
                    }
                });
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

        TextView toastText = new TextView(SettingActivity.this);
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