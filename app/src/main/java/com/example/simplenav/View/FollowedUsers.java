package com.example.simplenav.View;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.simplenav.Controller.ClickListener;
import com.example.simplenav.Controller.HomeErrorInitializeListener;
import com.example.simplenav.Controller.UserListAdapter;
import com.example.simplenav.Model.FollowedUsersModel;
import com.example.simplenav.R;

public class FollowedUsers extends Fragment {
    private final static String TAG = "FollowedUsers";

    public FollowedUsers() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.following, container, false);

        FollowedUsersModel followedUsersModel = new FollowedUsersModel();

        RecyclerView recyclerView = view.findViewById(R.id.followingRecycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        UserListAdapter adapter = new UserListAdapter(followedUsersModel, view.getContext(), new ClickListener() {
            @Override
            public void onListClickEvent(int index) {
                Log.d(TAG, "Click on element with position: " + index);
                Bundle bundle = new Bundle();
                bundle.putInt("uid", followedUsersModel.getUserListRepository().get(index).getUid());
                FollowedUsers.this.getFragmentManager().beginTransaction()
                        .setReorderingAllowed(true)
                        .replace(R.id.fragmentContainerView, UserProfile.class, bundle)
                        .addToBackStack(null)
                        .commit();
            }
        });

        recyclerView.setAdapter(adapter);

        followedUsersModel.init(adapter, this.getContext());

        SwipeRefreshLayout swipeRefreshLayout = view.findViewById(R.id.swipe_list_container_following);
        swipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        Log.d(TAG, "onRefresh called from SwipeRefreshLayout");
                        // This method performs the actual data-refresh operation.
                        // The method calls setRefreshing(false) when it's finished.
                        followedUsersModel.refresh(adapter, getContext());
                        swipeRefreshLayout.setRefreshing(false);
                    }
                });
        return view;
    }
}