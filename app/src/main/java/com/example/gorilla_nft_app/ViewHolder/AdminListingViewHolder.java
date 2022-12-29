package com.example.gorilla_nft_app.ViewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gorilla_nft_app.Interface.ItemClickListener;
import com.example.gorilla_nft_app.R;

public class AdminListingViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView nftNameText, nftCategoryText, nftPriceText, nftAddressText, nftDataText , nftSellerText;
    public ImageView nftImageView, deleteNftBtn;
    public ItemClickListener listener;

    public AdminListingViewHolder(@NonNull View itemView) {
        super(itemView);

        nftNameText = (TextView) itemView.findViewById(R.id.admin_nft_name);
        nftCategoryText = (TextView) itemView.findViewById(R.id.admin_nft_category);
        nftPriceText = (TextView) itemView.findViewById(R.id.admin_nft_price);
        nftAddressText = (TextView) itemView.findViewById(R.id.admin_nft_address);
        nftDataText = (TextView) itemView.findViewById(R.id.admin_nft_date);
        nftSellerText = (TextView) itemView.findViewById(R.id.admin_nft_seller);

        nftImageView = (ImageView) itemView.findViewById(R.id.admin_nft_preview);
        deleteNftBtn = (ImageView) itemView.findViewById(R.id.admin_nft_delete);
    }

    public void setItemClickListener(ItemClickListener listener){
        this.listener = listener;
    }

    @Override
    public void onClick(View v) {
        listener.onClick(v, getBindingAdapterPosition(),false);
    }
}
