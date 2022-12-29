package com.example.gorilla_nft_app.ViewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gorilla_nft_app.Interface.ItemClickListener;
import com.example.gorilla_nft_app.R;

public class InquiriesViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

    public TextView subjectOutput, dateOutput;
    public ImageView messageIcon;
    public ItemClickListener listener;

    public InquiriesViewHolder(@NonNull View itemView) {
        super(itemView);
        subjectOutput = (TextView) itemView.findViewById(R.id.subject_output);
        dateOutput = (TextView) itemView.findViewById(R.id.date_output);
        messageIcon = (ImageView) itemView.findViewById(R.id.message_icon);

    }

    public void setItemClickListener(ItemClickListener listener){
        this.listener = listener;
    }

    @Override
    public void onClick(View v) {
        listener.onClick(v, getBindingAdapterPosition(),false);
    }
}


