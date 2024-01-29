package com.example.simplenav.Model.CallApi;


import com.example.simplenav.Model.TwokRepository;
import com.example.simplenav.Model.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiInterface {
    @POST("register")
    Call<Sid> getSid();

    @POST("getTwok")
    Call<TwokRepository> getTwok(@Body Sid sid);

    @POST("getTwok")
    Call<TwokRepository> getTwokWithTid(@Body SidTid sidTid);

    @POST("getTwok")
    Call<TwokRepository> getTwokWithUid(@Body SidUid sidUid);

    @POST("getProfile")
    Call<Profile> getProfile(@Body Sid sid);

    @POST("getPicture")
    Call<User> getUser(@Body SidUid sidUid);

    @POST("setProfile")
    Call<Void> setProfileUsername(@Body SidName sidName);

    @POST("setProfile")
    Call<Void> setProfilePicture(@Body SidPicture sidPicture);

    @POST("addTwok")
    Call<Void> addTwok(@Body PreparingTwok preparingTwok);

    @POST("isFollowed")
    Call<Followed> isFollowed(@Body SidUid sidUid);

    @POST("follow")
    Call<Void> follow(@Body SidUid sidUid);

    @POST("unfollow")
    Call<Void> unfollow(@Body SidUid sidUid);

    @POST("getFollowed")
    Call<List<UserForFollowed>> getFollowed(@Body Sid sid);
}
