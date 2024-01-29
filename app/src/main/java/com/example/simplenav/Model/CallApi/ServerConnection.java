package com.example.simplenav.Model.CallApi;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ServerConnection {
    public static ApiInterface newApiInterface() {
        String BASE_URL = "https://develop.ewlab.di.unimi.it/mc/twittok/";
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ApiInterface apiInterface = retrofit.create(ApiInterface.class);
        return apiInterface;
    }
}
