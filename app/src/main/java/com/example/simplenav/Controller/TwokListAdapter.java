package com.example.simplenav.Controller;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.simplenav.MainActivity;
import com.example.simplenav.Model.TwokListRepository;
import com.example.simplenav.Model.TwokRepository;
import com.example.simplenav.R;
import com.example.simplenav.View.TwokViewHolder;

import java.util.ArrayList;
import java.util.List;

public class TwokListAdapter extends RecyclerView.Adapter<TwokViewHolder> {
    private static final String TAG =  "TwokListAdapter";
    private final List<Integer> discoveredPos = new ArrayList<>();
    private TwokListRepository twokListRepository = null;
    private LayoutInflater mInflater = null;
    private ClickListenerForTwok clickListenerForTwok = null;
    private BottomListener bottomListener = null;

    public TwokListAdapter(TwokListRepository twokListRepository, Context context, ClickListenerForTwok clickListenerForTwok, BottomListener bottomListener) {
        this.twokListRepository = twokListRepository;
        this.mInflater = LayoutInflater.from(context);
        this.clickListenerForTwok = clickListenerForTwok;
        this.bottomListener = bottomListener;
    }

    @NonNull
    @Override
    public TwokViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d(MainActivity.TAG, "onCreateViewHolder");
        View view = mInflater.inflate(R.layout.twok_view_holder, parent, false);
        return new TwokViewHolder(view, clickListenerForTwok);
    }

    @Override
    public void onBindViewHolder(@NonNull TwokViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder with pos: " + position);
        TwokRepository twok = twokListRepository.getTwok(position);
        if ((position == getItemCount()-1) && !discoveredPos.contains(position))  {
            discoveredPos.add(position);
            Log.d(TAG, "pos: " + position + " e itemCount: " + (getItemCount()-1) + " quindi nuovi twok in arrivo!");
            bottomListener.onBottomReached(this);
        }
        holder.updateContent(twok);
    }

    @Override
    public int getItemCount() {
        Log.d(MainActivity.TAG, "getItemCount");
        return twokListRepository.getSize();
    }
}
