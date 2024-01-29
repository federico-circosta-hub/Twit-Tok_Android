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
import com.example.simplenav.View.TwokViewForProfileHolder;

import java.util.ArrayList;
import java.util.List;

public class TwokListForProfileAdapter extends RecyclerView.Adapter<TwokViewForProfileHolder> {
    private static final String TAG =  "TwokListForProfileAdapter";
    private final List<Integer> discoveredPos = new ArrayList<>();
    private TwokListRepository twokListRepository = null;
    private LayoutInflater mInflater = null;
    private ClickListenerForTwok clickListenerForTwok = null;
    private BottomListenerForProfile bottomListenerForProfile = null;

    public TwokListForProfileAdapter(TwokListRepository twokListRepository, Context context, ClickListenerForTwok clickListenerForTwok, BottomListenerForProfile bottomListenerForProfile) {
        this.twokListRepository = twokListRepository;
        this.mInflater = LayoutInflater.from(context);
        this.clickListenerForTwok = clickListenerForTwok;
        this.bottomListenerForProfile = bottomListenerForProfile;
    }

    @NonNull
    @Override
    public TwokViewForProfileHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d(MainActivity.TAG, "onCreateViewHolder");
        View view = mInflater.inflate(R.layout.twok_view_for_profile_holder, parent, false);
        return new TwokViewForProfileHolder(view, clickListenerForTwok);
    }

    @Override
    public void onBindViewHolder(@NonNull TwokViewForProfileHolder holder, int position) {
        Log.d(MainActivity.TAG, "onBindViewForProfileHolder with pos: " + position);
        TwokRepository twok = twokListRepository.getTwok(position);
        if ((position == getItemCount()-1) && !discoveredPos.contains(position))  {
            discoveredPos.add(position);
            Log.d(TAG, "pos: " + position + " e itemCount: " + (getItemCount()-1) + " quindi nuovi twok in arrivo!");
            bottomListenerForProfile.onBottomReached(this);
        }
        holder.updateContent(twok);
    }

    @Override
    public int getItemCount() {
        Log.d(MainActivity.TAG, "getItemCount");
        return twokListRepository.getSize();
    }
}
