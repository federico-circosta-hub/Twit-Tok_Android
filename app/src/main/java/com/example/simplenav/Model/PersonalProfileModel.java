package com.example.simplenav.Model;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Base64;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.simplenav.Controller.ImageChangeListener;
import com.example.simplenav.Controller.PictureConverter;
import com.example.simplenav.Controller.RefreshListener;
import com.example.simplenav.Controller.TwokListForProfileAdapter;
import com.example.simplenav.Controller.UsernameChangeListener;
import com.example.simplenav.Model.CallApi.ApiInterface;
import com.example.simplenav.Model.CallApi.Profile;
import com.example.simplenav.Model.CallApi.ServerConnection;
import com.example.simplenav.Model.CallApi.Sid;
import com.example.simplenav.Model.CallApi.SidName;
import com.example.simplenav.Model.CallApi.SidPicture;
import com.example.simplenav.Model.CallApi.SidUid;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PersonalProfileModel {
    private static final String TAG = "PersonalProfileModel";
    private static final String SUCCESSFUL_USERNAME_CHANGED = "Username cambiata con successo!";
    private static final String UNSUCCESSFUL_USERNAME_CHANGED = "Errore di rete\nriprovare più tardi";
    private static final String SUCCESSFUL_IMAGE_CHANGED = "Immagine di profilo cambiata con successo!";
    private static final String UNSUCCESSFUL_IMAGE_CHANGED = "Errore di rete\nriprovare più tardi";
    private static PersonalProfileModel instance = null;
    private TwokListRepository twokListRepository = new TwokListRepository();
    private Profile profile = new Profile();
    private Bitmap decodedImage;
    private boolean firstRun = true;
    private final Sid sid = SidRepository.getSid();
    private int attemps = 0;
    private List<String> buffer = new ArrayList<>();

    //Il costruttore private impedisce l'istanza di oggetti da parte di classi esterne
    private PersonalProfileModel() {}

    // Metodo della classe impiegato per accedere al singleton
    public static synchronized PersonalProfileModel getInstance() {
        if (instance == null) {
            instance = new PersonalProfileModel();
        }
        return instance;
    }

    public TwokListRepository getTwokListRepository() {
        return twokListRepository;
    }

    public void setTwokListRepository(TwokListRepository twokListRepository) {
        this.twokListRepository = twokListRepository;
    }

    public void fiveMoreTwoks(RecyclerView.Adapter adapter) {
        for (int i = 0; i < 5; i++) {
            get1TwokFromServer(adapter);
        }
    }

    public void initProfile(RecyclerView.Adapter adapter, TextView textView, ImageView image) {
        textView.setText(profile.getName());
        image.setImageBitmap(decodedImage);
        if (firstRun) {
            getProfileInfo(adapter, textView, image);
        }
    }

    private void getProfileInfo(RecyclerView.Adapter adapter, TextView textView, ImageView image) {
        ApiInterface apiInterface = ServerConnection.newApiInterface();
        Call<Profile> call = apiInterface.getProfile(sid);
        call.enqueue(new Callback<Profile>() {
            @Override
            public void onResponse(@NonNull Call<Profile> call, @NonNull Response<Profile> response) {
                if (response.isSuccessful()) {
                    assert response.body() != null;
                    Log.d(TAG, "ottengo info per " + response.body().getName());
                    profile = response.body();
                    textView.setText(profile.getName());
                    decodedImage = PictureConverter.createImage(profile.getPicture());
                    image.setImageBitmap(decodedImage);
                    Log.d(TAG, "" + profile.getPicture());
                    init(adapter);
                } else {
                    //TODO: gestire l'errore
                }
            }

            @Override
            public void onFailure(@NonNull Call<Profile> call, @NonNull Throwable t) {
                Log.d(TAG, "ho fallito");
                t.printStackTrace();
            }
        });
    }

    public void init(RecyclerView.Adapter adapter) {
        Log.d(TAG,"vado di init");
        for (attemps = 0; attemps < 20 && buffer.size() < 5; attemps++) {
            get1TwokFromServer(adapter);
        }
        firstRun = false;
        buffer.clear();
    }

    public void get1TwokFromServer(RecyclerView.Adapter adapter) {
        SidUid sidUid = new SidUid();
        sidUid.setSid(sid.getSid());
        sidUid.setUid(profile.getUid());
        ApiInterface apiInterface = ServerConnection.newApiInterface();
        Call<TwokRepository> call = apiInterface.getTwokWithUid(sidUid);
        call.enqueue(new Callback<TwokRepository>() {
            @Override
            public void onResponse(@NonNull Call<TwokRepository> call, @NonNull Response<TwokRepository> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "riveuto twok " + response.body().toString());
                    boolean added = twokListRepository.addIfNotPresent(response.body());
                    if (added) {
                        buffer.add("added");
                        adapter.notifyDataSetChanged();
                    }
                } else {
                    Log.d(TAG, "nessun twok per questo account " + response.message() + response.errorBody().toString());
                }
            }
            @Override
            public void onFailure(@NonNull Call<TwokRepository> call, @NonNull Throwable t) {
                Log.d(TAG, "ho fallito");
                t.printStackTrace();
            }
        });
    }

    public void changeUsername(String newName, UsernameChangeListener ucl) {
        profile.setName(newName);
        SidName sidName = new SidName();
        sidName.setSid(sid.getSid());
        sidName.setName(profile.getName());
        ApiInterface apiInterface = ServerConnection.newApiInterface();
        Call<Void> call = apiInterface.setProfileUsername(sidName);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "cambio nome " + response.message());
                    ucl.onUsernameChanged(SUCCESSFUL_USERNAME_CHANGED);
                } else {
                    Log.d(TAG, "cambio nome " + response.message());
                    ucl.onUsernameChanged(UNSUCCESSFUL_USERNAME_CHANGED);
                }
            }
            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                Log.d(TAG, "ho fallito");
                t.printStackTrace();
                ucl.onUsernameChanged(UNSUCCESSFUL_USERNAME_CHANGED);
            }
        });
    }

    public void changeImage(Uri imageUri, Context context, ImageChangeListener icl) {
        String imageString = null;
        try {
            InputStream inputStream = context.getContentResolver().openInputStream(imageUri);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            decodedImage = bitmap;
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            byte[] imageBytes = outputStream.toByteArray();
            imageString = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        if (imageString != null) {
            Log.d(TAG, "base64 da mandare a Mascetti "+imageString);
            SidPicture sidPicture = new SidPicture();
            sidPicture.setSid(sid.getSid());
            sidPicture.setPicture(imageString);
            ApiInterface apiInterface = ServerConnection.newApiInterface();
            Call<Void> call = apiInterface.setProfilePicture(sidPicture);
            call.enqueue(new Callback<Void>() {
                @Override
                public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                    if (response.isSuccessful()) {
                        Log.d(TAG, "cambio nome " + response.message());
                        icl.onChangedImage(SUCCESSFUL_IMAGE_CHANGED);
                    } else {
                        Log.d(TAG, "cambio nome " + response.message());
                        icl.onChangedImage(UNSUCCESSFUL_IMAGE_CHANGED);
                    }
                }
                @Override
                public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                    Log.d(TAG, "ho fallito");
                    t.printStackTrace();
                    icl.onChangedImage(UNSUCCESSFUL_IMAGE_CHANGED);
                }
            });
        }

    }

    public void refresh(TwokListForProfileAdapter adapter) {
        twokListRepository.clear();
        attemps = 0;
        buffer.clear();
        init(adapter);
    }
}
