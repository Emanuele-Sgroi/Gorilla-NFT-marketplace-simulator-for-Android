package com.example.gorilla_nft_app.ViewHolder;

import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gorilla_nft_app.Interface.ItemClickListener;
import com.example.gorilla_nft_app.R;
import com.example.gorilla_nft_app.RegisterActivity;
import com.example.gorilla_nft_app.databinding.ActivitySellMainBinding;

public class SellerProductViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
{

    public TextView nftNameText, nftCategoryText, nftPriceText, nftAddressText, nftDataText;
    public ImageView nftImageView, deleteNftBtn;
    public ItemClickListener listener;

    public SellerProductViewHolder(@NonNull View itemView) {
        super(itemView);

        nftNameText = (TextView) itemView.findViewById(R.id.seller_nft_name);
        nftCategoryText = (TextView) itemView.findViewById(R.id.seller_nft_category);
        nftPriceText = (TextView) itemView.findViewById(R.id.seller_nft_price);
        nftAddressText = (TextView) itemView.findViewById(R.id.seller_nft_address);
        nftDataText = (TextView) itemView.findViewById(R.id.seller_nft_date);

        nftImageView = (ImageView) itemView.findViewById(R.id.seller_nft_preview);
        deleteNftBtn = (ImageView) itemView.findViewById(R.id.seller_nft_delete);
    }

    public void setItemClickListener(ItemClickListener listener){
        this.listener = listener;
    }

    @Override
    public void onClick(View v) {
        listener.onClick(v, getBindingAdapterPosition(),false);
    }
}
