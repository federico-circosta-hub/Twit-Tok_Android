package com.example.simplenav.View;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.simplenav.Controller.ClickListenerForTwok;
import com.example.simplenav.Controller.TwokVisualizer;
import com.example.simplenav.Model.TwokRepository;
import com.example.simplenav.R;

public class TwokViewHolder extends RecyclerView.ViewHolder {
    private static final String TAG = "TwokViewHolder";

    private TextView author = null;
    private TextView text = null;
    private ImageView pic = null;
    private View cell = null;

    public TwokViewHolder(@NonNull View itemView, ClickListenerForTwok clickListenerForTwok) {
        super(itemView);
        author = itemView.findViewById(R.id.author);
        text = itemView.findViewById(R.id.text);
        Button mapButton = itemView.findViewById(R.id.mapButton);
        cell = itemView.findViewById(R.id.twokCell);
        pic = itemView.findViewById(R.id.personalPicInHome);

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickListenerForTwok.onListClickEvent(TwokViewHolder.this.getAdapterPosition());
            }
        });

        mapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickListenerForTwok.onListClickEventMap(TwokViewHolder.this.getAdapterPosition());
                Log.d(TAG, "mappa di elemento " + getAdapterPosition());
            }
        });
    }

    public void updateContent(TwokRepository twok) {
        Log.d(TAG, "sto aggiornando una cella che conteneva - " + author.getText() + " - con il valore " + twok.getAuthor());
        Log.d(TAG, "twok color " + twok.getFontcol() + " twok size " + twok.getFontsize());
        author.setText(twok.getAuthor());
        text.setText(twok.getTwokText().substring(1,twok.getTwokText().length()-1));
        pic.setImageBitmap(twok.getImg());
        new TwokVisualizer().formatTwok(twok, text, cell);
    }
}
