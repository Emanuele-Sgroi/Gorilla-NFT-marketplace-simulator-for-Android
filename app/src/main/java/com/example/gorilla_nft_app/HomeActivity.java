package com.example.gorilla_nft_app;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;


import com.example.gorilla_nft_app.Model.Products;
import com.example.gorilla_nft_app.Prevalent.Prevalent;
import com.example.gorilla_nft_app.ViewHolder.ProductViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import io.paperdb.Paper;

public class HomeActivity extends AppCompatActivity {


    private RecyclerView recyclerView;
    private TextView cartQuantityText;
    private String childrenCount;
    private ImageView searchBtn;
    RecyclerView.LayoutManager layoutManager;
    DatabaseReference RootRef, ProductRef, SellerPictureRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        //go to search activity
        searchBtn = (ImageView) findViewById(R.id.search_button);
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, SearchActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.search_trasition_start,R.anim.search_trasition_end);
            }
        });

        Paper.init(this);

        //product reference
        ProductRef = FirebaseDatabase.getInstance("https://gorillanft-d65f0-default-rtdb.europe-west1.firebasedatabase.app/").getReference().child("NFTs");

        //recycler view
        recyclerView = findViewById(R.id.app__content_home_recycler_menu);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        //default java drawer layout
        Toolbar toolbar = (Toolbar)  findViewById(R.id.app__toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        FloatingActionButton floatingActionButton = (FloatingActionButton) findViewById(R.id.app__bar_home_floating_cart);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!cartQuantityText.getText().toString().equals("0")){
                    Intent intent = new Intent(HomeActivity.this, CartActivity.class);
                    startActivity(intent);
                }
                else{
                    createToast("The cart is empty");
                }
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.app__nav_view);
        navigationView.setNavigationItemSelectedListener(this::onNavigationItemSelected);

        View headerNavView = navigationView.getHeaderView(0);
        TextView usernameTextView = headerNavView.findViewById(R.id.app__nav_header_home_username_display);
        CircleImageView profilePictureView = headerNavView.findViewById(R.id.app__nav_header_home_user_profile_picture);

        usernameTextView.setText(Prevalent.onlineUsers.getUsername());
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

        //cart quantity
        cartQuantityText = (TextView) findViewById(R.id.home_cart_quantity);

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
                        String displayPrice = model.getPrice() + " ETH";

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
                                Intent intent = new Intent(HomeActivity.this, ProductDetailActivity.class);
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



    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if(drawer.isDrawerOpen(GravityCompat.START)){
            drawer.closeDrawer(GravityCompat.START);
        }
        else{
            super.onBackPressed();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")

    public boolean onNavigationItemSelected(MenuItem item){
        int id = item.getItemId();

        if (id == R.id.app__nav_cart)
        {
            if(!cartQuantityText.getText().toString().equals("0")){
                Intent intent = new Intent(HomeActivity.this, CartActivity.class);
                startActivity(intent);
            }
            else{
                createToast("The cart is empty");
            }

        }
        else if (id == R.id.app__nav_whishlist)
        {
            Intent intent = new Intent(HomeActivity.this, WishlistActivity.class);
            startActivity(intent);
        }
        else if (id == R.id.app_nav_transactions)
        {
            Intent intent = new Intent(HomeActivity.this, TransactionsActivity.class);
            startActivity(intent);
        }
        else if (id == R.id.app_nav_account)
        {
            Intent intent = new Intent(HomeActivity.this, SettingActivity.class);
            startActivity(intent);
        }
        else if (id == R.id.app__nav_sell)
        {
            Intent intent = new Intent(HomeActivity.this, sellMainActivity.class);
            startActivity(intent);
        }
        else if (id == R.id.app__nav_help)
        {
            Intent intent = new Intent(HomeActivity.this, HelpActivity.class);
            startActivity(intent);
        }
        else if (id == R.id.app__nav_terms)
        {
            Intent intent = new Intent(HomeActivity.this, TermCondActivity.class);
            startActivity(intent);
        }
        else if (id == R.id.app__nav_delete_account)
        {
            Intent intent = new Intent(HomeActivity.this, DeleteAccountActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }
        else if (id == R.id.app__nav_logout)
        {
            logOutUser();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void logOutUser() {
        Paper.book().destroy();

        ProgressDialog progressDialog = new ProgressDialog(HomeActivity.this);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setMessage("Logging you out...");
        progressDialog.show();

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(HomeActivity.this,MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        },1000);

    }

    public void createToast(String someText){
        Toast toast = new Toast(getApplicationContext());
        toast.setGravity(Gravity.CENTER,0,700);

        Drawable d = ContextCompat.getDrawable(getApplicationContext(), R.drawable.toast_shape);

        TextView toastText = new TextView(HomeActivity.this);
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