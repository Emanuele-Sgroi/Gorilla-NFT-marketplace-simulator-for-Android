package com.example.gorilla_nft_app.ViewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gorilla_nft_app.Interface.ItemClickListener;
import com.example.gorilla_nft_app.R;

public class TransactionsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

    public ItemClickListener listener;
    public TextView buyerAddress, nftQuantity, paymentMethod, transactionDate, transactionTime, worthDollar, worthEth, transactionId;

    public TransactionsViewHolder(@NonNull View itemView) {
        super(itemView);

        buyerAddress = (TextView) itemView.findViewById(R.id.transaction_buyer_address);
        nftQuantity = (TextView) itemView.findViewById(R.id.transaction_nft_quantity);
        paymentMethod = (TextView) itemView.findViewById(R.id.transaction_payment);
        transactionDate = (TextView) itemView.findViewById(R.id.transaction_date);
        transactionTime = (TextView) itemView.findViewById(R.id.transaction_time);
        worthDollar = (TextView) itemView.findViewById(R.id.transaction_dollar);
        worthEth = (TextView) itemView.findViewById(R.id.transaction_eth);
        transactionId = (TextView) itemView.findViewById(R.id.transaction_id);
    }

    public void setItemClickListener(ItemClickListener listener){
        this.listener = listener;
    }

    @Override
    public void onClick(View v) {
        listener.onClick(v, getBindingAdapterPosition(),false);
    }
}
