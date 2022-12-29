package com.example.gorilla_nft_app.ViewHolder;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.gorilla_nft_app.Interface.ItemClickListener;
import com.example.gorilla_nft_app.R;
import de.hdodenhof.circleimageview.CircleImageView;

public class ProductViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
{
    public TextView nftNameText, nftSellerText, nftPriceText;
    public ImageView nftImageView;
    public CircleImageView nftSellerPicture;
    public ItemClickListener listener;

    public ProductViewHolder(@NonNull View itemView) {
        super(itemView);

        nftNameText = (TextView) itemView.findViewById(R.id.cardview_nft_name);
        nftSellerText = (TextView) itemView.findViewById(R.id.cardview_seller_name);
        nftPriceText = (TextView) itemView.findViewById(R.id.cardview_nft_price);
        nftImageView = (ImageView) itemView.findViewById(R.id.cardview_nft_preview);
        nftSellerPicture = (CircleImageView) itemView.findViewById(R.id.cardview_seller_picture);
    }

    public void setItemClickListener(ItemClickListener listener){

        this.listener = listener;

    }

    @Override
    public void onClick(View v) {
        listener.onClick(v, getBindingAdapterPosition(),false);
    }
}

//getBindingAdapterPosition(),    getAdapterPosition()
