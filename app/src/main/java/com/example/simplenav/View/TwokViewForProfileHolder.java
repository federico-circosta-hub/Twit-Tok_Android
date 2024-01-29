package com.example.simplenav.View;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.simplenav.Controller.ClickListener;
import com.example.simplenav.Controller.ClickListenerForTwok;
import com.example.simplenav.Controller.TwokVisualizer;
import com.example.simplenav.Model.TwokRepository;
import com.example.simplenav.R;

public class TwokViewForProfileHolder extends RecyclerView.ViewHolder {
    private static final String TAG = "TwokViewForProfileViewHolder";

    private TextView text;
    private View cell;

    public TwokViewForProfileHolder(@NonNull View itemView, ClickListenerForTwok clickListenerForTwok) {
        super(itemView);
        text = itemView.findViewById(R.id.text);
        cell = itemView.findViewById(R.id.twokCellInProfile);
        Button mapButton = itemView.findViewById(R.id.mapButtonForProfile);
        itemView.setOnClickListener(view -> {
            clickListenerForTwok.onListClickEvent(getAdapterPosition());
        });
        mapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickListenerForTwok.onListClickEventMap(TwokViewForProfileHolder.this.getAdapterPosition());
                Log.d(TAG, "mappa di elemento " + getAdapterPosition());
            }
        });
    }

    public void updateContent(TwokRepository twok) {
        Log.d(TAG, "sto aggiornando una cella che conteneva - " + text.getText() + " - con il valore " + twok.getTwokText());
        Log.d(TAG, "dati twok da visualizzare " + twok.toString());
        text.setText(twok.getTwokText().substring(1,twok.getTwokText().length()-1));
        new TwokVisualizer().formatTwok(twok, text, cell);
    }
}
