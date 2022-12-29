package com.example.gorilla_nft_app.ViewHolder;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gorilla_nft_app.Interface.ItemClickListener;
import com.example.gorilla_nft_app.R;

public class UsersListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

    public TextView usernameDisplay, viewUserBtn;
    public ItemClickListener listener;

    public UsersListViewHolder(@NonNull View itemView) {
        super(itemView);

        usernameDisplay = (TextView) itemView.findViewById(R.id.username_of_list);
        viewUserBtn = (TextView) itemView.findViewById(R.id.view_username_btn);

    }

    public void setItemClickListener(ItemClickListener listener){

        this.listener = listener;

    }

    @Override
    public void onClick(View v) {
        listener.onClick(v, getBindingAdapterPosition(),false);
    }
}
