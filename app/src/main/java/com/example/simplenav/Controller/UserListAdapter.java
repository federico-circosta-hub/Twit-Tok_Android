package com.example.simplenav.Controller;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.simplenav.MainActivity;
import com.example.simplenav.Model.FollowedUsersModel;
import com.example.simplenav.Model.User;
import com.example.simplenav.Model.UserRepository;
import com.example.simplenav.R;
import com.example.simplenav.View.UserViewHolder;

public class UserListAdapter extends RecyclerView.Adapter<UserViewHolder> {

    private FollowedUsersModel followedUsersModel = null;
    private LayoutInflater mInflater = null;
    private ClickListener clickListener = null;
    public UserListAdapter(FollowedUsersModel followedUsersModel, Context context, ClickListener clickListener) {
        this.followedUsersModel = followedUsersModel;
        this.mInflater = LayoutInflater.from(context);
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d(MainActivity.TAG, "onCreateViewHolder");
        View view = mInflater.inflate(R.layout.user_view_holder, parent, false);
        return new UserViewHolder(view, clickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        Log.d(MainActivity.TAG, "onBindViewHolder with pos: " + position);
        UserRepository user = followedUsersModel.getUser(position);
        holder.updateContent(user);
    }

    @Override
    public int getItemCount() {
        Log.d(MainActivity.TAG, "getItemCount");
        return followedUsersModel.getSize();
    }
}
