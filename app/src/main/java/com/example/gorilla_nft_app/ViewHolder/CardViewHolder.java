package com.example.gorilla_nft_app.ViewHolder;

import android.graphics.Paint;
import android.view.View;

import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gorilla_nft_app.Interface.ItemClickListener;
import com.example.gorilla_nft_app.Model.Cards;
import com.example.gorilla_nft_app.Model.Users;
import com.example.gorilla_nft_app.Prevalent.Prevalent;
import com.example.gorilla_nft_app.R;
import com.example.gorilla_nft_app.SettingActivity;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class CardViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
{

    public TextView cardViewText, cardDeleteBtn, checkoutCardViewText;
    public ItemClickListener listener;

    public CardViewHolder(@NonNull View itemView) {
        super(itemView);

        cardViewText = (TextView) itemView.findViewById(R.id.card_show_text);
        cardDeleteBtn = (TextView) itemView.findViewById(R.id.card_delete_btn);
        checkoutCardViewText = (TextView) itemView.findViewById(R.id.checkout_card_show_text);

    }

    public void setItemClickListener(ItemClickListener listener){

        this.listener = listener;

    }

    @Override
    public void onClick(View v) {
        listener.onClick(v, getBindingAdapterPosition(),false);
    }
}
