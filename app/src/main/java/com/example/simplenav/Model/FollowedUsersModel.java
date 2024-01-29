package com.example.simplenav.Model;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.simplenav.Controller.UserListAdapter;
import com.example.simplenav.Controller.UserPictureListener;
import com.example.simplenav.Model.CallApi.ApiInterface;
import com.example.simplenav.Model.CallApi.ServerConnection;
import com.example.simplenav.Model.CallApi.Sid;
import com.example.simplenav.Model.CallApi.UserForFollowed;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FollowedUsersModel {
    private List<UserRepository> userListRepository = new ArrayList<UserRepository>();
    private static final String TAG = "FollowedUsersModel";
    private static final Sid sid = SidRepository.getSid();

    public UserRepository getUser(int i) {
        return userListRepository.get(i);
    }

    public int getSize() {
        return userListRepository.size();
    }


    public void init(RecyclerView.Adapter adapter, Context context) {
        ApiInterface apiInterface = ServerConnection.newApiInterface();
        Call<List<UserForFollowed>> call = apiInterface.getFollowed(sid);
        call.enqueue(new Callback<List<UserForFollowed>>() {
            @Override
            public void onResponse(@NonNull Call<List<UserForFollowed>> call, @NonNull Response<List<UserForFollowed>> response) {
                Log.d(TAG, "lista utenti ottenuta");
                if (response.body() != null) {
                    for (UserForFollowed userForFollowed : response.body()) {
                        UserRepository u = new UserRepository();
                        u.setUsername(userForFollowed.getName());
                        u.setPversion(userForFollowed.getPversion());
                        u.setUid(userForFollowed.getUid());
                        userListRepository.add(u);
                        new PictureRepository().getPicture(context, sid, u.getUid(), u.getPversion(), new UserPictureListener() {
                            @Override
                            public void onPictureReady(Bitmap img) {
                                for (UserRepository user : userListRepository) {
                                    if (user.getUid() == u.getUid()) {
                                        u.setPic(img);
                                    }
                                }
                                adapter.notifyDataSetChanged();
                            }
                        });
                    }
                    adapter.notifyDataSetChanged();
                }
            }
            @Override
            public void onFailure(@NonNull Call<List<UserForFollowed>> call, @NonNull Throwable t) {
                Log.d(TAG, "ho fallito");
                t.printStackTrace();
            }
        });
    }

    public List<UserRepository> getUserListRepository() {
        return userListRepository;
    }

    public void refresh(UserListAdapter adapter, Context context) {
        userListRepository.clear();
        init(adapter, context);
    }
}
