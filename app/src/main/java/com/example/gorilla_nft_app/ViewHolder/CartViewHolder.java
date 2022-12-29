package com.example.gorilla_nft_app.ViewHolder;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gorilla_nft_app.Interface.ItemClickListener;
import com.example.gorilla_nft_app.R;

public class CartViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

    public TextView cartNameText, cartSellerText, cartAddressText, cartEthPriceText, cartDollarPriceText;
    public ImageView cartImgItem, removeItemBtn;
    public ItemClickListener itemClickListener;

    public CartViewHolder(@NonNull View itemView) {
        super(itemView);

        //Text Views
        cartNameText = (TextView) itemView.findViewById(R.id.nft_name);
        cartSellerText = (TextView) itemView.findViewById(R.id.nft_seller_name);
        cartAddressText = (TextView) itemView.findViewById(R.id.nft_address_cart);
        cartEthPriceText = (TextView) itemView.findViewById(R.id.nft_eth_price);
        cartDollarPriceText = (TextView) itemView.findViewById(R.id.nft_dollar_price);

        //Buttons and Image Views
        removeItemBtn = (ImageView) itemView.findViewById(R.id.delete_item_btn);
        cartImgItem = (ImageView) itemView.findViewById(R.id.cart_nft_preview);
    }

    @Override
    public void onClick(View v) {
        itemClickListener.onClick(v, getBindingAdapterPosition(), false);
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }
}
