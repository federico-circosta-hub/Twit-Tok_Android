package com.example.simplenav.Model;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.simplenav.Controller.HomeErrorInitializeListener;
import com.example.simplenav.Controller.TwokListForProfileAdapter;
import com.example.simplenav.Controller.UserPictureListener;
import com.example.simplenav.Model.CallApi.ApiInterface;
import com.example.simplenav.Model.CallApi.ServerConnection;
import com.example.simplenav.Model.CallApi.Sid;
import com.example.simplenav.Model.CallApi.SidTid;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeModel {
    private static final String TAG = "HomeModel";
    private static HomeModel instance = null;
    private TwokListRepository twokListRepository = new TwokListRepository();
    private int tidSequence = -1;
    private boolean firstRun = true;
    //private TwokRepository twokReceived = new TwokRepository();


    //Il costruttore private impedisce l'istanza di oggetti da parte di classi esterne
    private HomeModel() {}

    // Metodo della classe impiegato per accedere al singleton
    public static synchronized HomeModel getInstance() {
        if (instance == null) {
            instance = new HomeModel();
        }
        return instance;
    }

    public TwokListRepository getTwokListRepository() {
        return twokListRepository;
    }

    public void setTwokListRepository(TwokListRepository twokListRepository) {
        this.twokListRepository = twokListRepository;
    }

    public void fiveMoreTwoks(RecyclerView.Adapter adapter, Context context, HomeErrorInitializeListener homeErrorInitializeListener) {
        for (int i = 0; i < 5; i++) {
            get1TwokFromServer(adapter, context, homeErrorInitializeListener);
        }
    }

    public void init(RecyclerView.Adapter adapter, Context context, HomeErrorInitializeListener homeErrorInitializeListener) {
        if (firstRun) {
            for (int i = 0; i < 5; i++) {
                get1TwokFromServer(adapter, context, homeErrorInitializeListener);
            }
            firstRun = false;
        }
    }

    public void get1TwokFromServer(RecyclerView.Adapter adapter, Context context, HomeErrorInitializeListener homeErrorInitializeListener) {
        if (tidSequence == -1) {
            withoutTidSequence(adapter, context, homeErrorInitializeListener);
        } else {
            withTidSequence(adapter, context, homeErrorInitializeListener);
        }
    }

    private void withTidSequence(RecyclerView.Adapter adapter, Context context, HomeErrorInitializeListener homeErrorInitializeListener) {
        Sid sid = SidRepository.getSid();
        ApiInterface apiInterface = ServerConnection.newApiInterface();
        SidTid sidTid = new SidTid();
        sidTid.setSid(sid.getSid());
        sidTid.setTid(tidSequence);
        Call<TwokRepository> call = apiInterface.getTwokWithTid(sidTid);
        Log.d(TAG, "chiamo twok con tid " + tidSequence);
        call.enqueue(new Callback<TwokRepository>() {
            @Override
            public void onResponse(@NonNull Call<TwokRepository> call, @NonNull Response<TwokRepository> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        Log.d(TAG, "ricevuto twok con body " + response.body());
                        Log.d(TAG, "ricevuto twok con msg " + response.message());
                        Log.d(TAG, "ricevuto twok con errBody " + response.errorBody());
                        Log.d(TAG, "ricevuto twok con testo " + response.body().getTwokText());
                        twokListRepository.add(response.body());
                        Log.d(TAG, "ora ottengo foto per testo " + response.body().getTwokText());
                        new PictureRepository().getPicture(context, sid, response.body().getUid(), response.body().getPversion(), new UserPictureListener() {
                            @Override
                            public void onPictureReady(Bitmap img) {
                                //twokReceived.setImg(img);
                                twokListRepository.sort();
                                adapter.notifyDataSetChanged();
                                Log.d(TAG, "aggiunta img " + img.toString() + " a " + response.body().getTwokText());
                                //tidSequence++;
                            }
                        });
                    }
                } else {
                    Log.d(TAG, "risposta non 200");
                    homeErrorInitializeListener.onHomeNotInitialized();
                }
            }
            @Override
            public void onFailure(@NonNull Call<TwokRepository> call, @NonNull Throwable t) {
                Log.d(TAG, "ho fallito");
                t.printStackTrace();
                homeErrorInitializeListener.onHomeNotInitialized();
                //tidSequence++;
            }
        });
        tidSequence++;
    }

    private void withoutTidSequence(RecyclerView.Adapter adapter, Context context, HomeErrorInitializeListener homeErrorInitializeListener) {
        Sid sid = SidRepository.getSid();
        ApiInterface apiInterface = ServerConnection.newApiInterface();
        Call<TwokRepository> call = apiInterface.getTwok(sid);
        call.enqueue(new Callback<TwokRepository>() {
            @Override
            public void onResponse(@NonNull Call<TwokRepository> call, @NonNull Response<TwokRepository> response) {
                Log.d(TAG, "" + sid.getSid());
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        Log.d(TAG, "ricevuto twok dal server: " + response.body().getTwokText());
                        twokListRepository.add(response.body());
                        adapter.notifyDataSetChanged();
                        Log.d(TAG, "chiedo img per " + response.body().getAuthor() + "attualmente ha pVersion" + response.body().getPversion());
                        new PictureRepository().getPicture(context, sid, response.body().getUid(), response.body().getPversion(), new UserPictureListener() {
                            @Override
                            public void onPictureReady(Bitmap img) {
                                for (TwokRepository twokRepository : twokListRepository) {
                                    if (twokRepository.getUid() == response.body().getUid()) {
                                        twokRepository.setImg(img);
                                        Log.d(TAG, "aggiunta img " + img.toString() + " a " + response.body().getAuthor());
                                    }
                                }
                                adapter.notifyDataSetChanged();
                            }
                        });
                    }
                } else {
                    Log.d(TAG, "risposta non 200");
                    homeErrorInitializeListener.onHomeNotInitialized();
                }
            }
            @Override
            public void onFailure(@NonNull Call<TwokRepository> call, @NonNull Throwable t) {
                Log.d(TAG, "ho fallito");
                t.printStackTrace();
                homeErrorInitializeListener.onHomeNotInitialized();
            }
        });
    }

    public void refresh(RecyclerView.Adapter adapter, Context context, HomeErrorInitializeListener homeErrorInitializeListener) {
        twokListRepository.clear();
        firstRun = true;
        init(adapter, context, homeErrorInitializeListener);
    }
}
