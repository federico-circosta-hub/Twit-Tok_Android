package com.example.simplenav.View;

import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.simplenav.Controller.ClickListener;
import com.example.simplenav.MainActivity;
import com.example.simplenav.Model.UserRepository;
import com.example.simplenav.R;

public class UserViewHolder extends RecyclerView.ViewHolder {

    private TextView text;
    private ImageView pic;

    public UserViewHolder(@NonNull View itemView, ClickListener clickListener) {
        super(itemView);
        text = itemView.findViewById(R.id.userName);
        pic = itemView.findViewById(R.id.userPic);

        itemView.setOnClickListener(view -> {
            clickListener.onListClickEvent(getAdapterPosition());
        });
    }

    public void updateContent(UserRepository user) {
        Log.d(MainActivity.TAG, "sto aggiornando una cella che conteneva - " + text.getText() + " - con il valore " + user.getUsername());
        text.setText(user.getUsername());
        pic.setImageBitmap(user.getPic());
    }
}
