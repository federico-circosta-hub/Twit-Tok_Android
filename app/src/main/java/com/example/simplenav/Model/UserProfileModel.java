package com.example.simplenav.Model;

import static android.graphics.Color.BLACK;
import static android.graphics.Color.BLUE;
import static android.graphics.Color.GREEN;
import static android.graphics.Color.RED;
import static android.graphics.Color.WHITE;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Base64;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.simplenav.Controller.FollowListener;
import com.example.simplenav.Controller.ImageChangeListener;
import com.example.simplenav.Controller.UsernameChangeListener;
import com.example.simplenav.Model.CallApi.ApiInterface;
import com.example.simplenav.Model.CallApi.Followed;
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

public class UserProfileModel {
    private static final String TAG = "UserProfileModel";
    private static UserProfileModel instance = null;
    private TwokListRepository twokListRepository = new TwokListRepository();
    private User user = new User();
    private Bitmap decodedImage;
    private boolean firstRun = true;
    private final Sid sid = SidRepository.getSid();
    private int attemps = 0;
    private List<String> buffer = new ArrayList<>();
    private Boolean userFollowed;

    public UserProfileModel() {}


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

    public void initProfile(RecyclerView.Adapter adapter, TextView textView, ImageView image, int uid, Button follow) {
        textView.setText(user.getName());
        image.setImageBitmap(decodedImage);
        if (userFollowed != null) {
            resolveButton(userFollowed, follow);
        }
        if (firstRun) {
            getProfileInfo(adapter, textView, image, uid, follow);
        }
    }

    private void resolveButton(Boolean userFollowed, Button follow) {
        if (userFollowed) {
            follow.setText("Unfollow");
            follow.setBackgroundColor(RED);
        } else {
            follow.setText("Follow");
            follow.setBackgroundColor(GREEN);
        }
    }

    private void getProfileInfo(RecyclerView.Adapter adapter, TextView textView, ImageView image, int uid, Button follow) {
        SidUid sidUid = new SidUid();
        sidUid.setSid(sid.getSid());
        sidUid.setUid(uid);
        ApiInterface apiInterface = ServerConnection.newApiInterface();
        Call<User> call = apiInterface.getUser(sidUid);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                assert response.body() != null;
                Log.d(TAG, "ottengo info per " + response.body().getName());
                user = response.body();
                textView.setText(user.getName());
                decodedImage = createImage(user.getPicture());
                image.setImageBitmap(decodedImage);
                init(adapter);
                amIFollow(follow);
            }

            @Override
            public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {
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
        sidUid.setUid(user.getUid());
        ApiInterface apiInterface = ServerConnection.newApiInterface();
        Call<TwokRepository> call = apiInterface.getTwokWithUid(sidUid);
        call.enqueue(new Callback<TwokRepository>() {
            @Override
            public void onResponse(@NonNull Call<TwokRepository> call, @NonNull Response<TwokRepository> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "riveuto twok " + response.body().getTwokText());
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

    private Bitmap createImage(String pic) {
        byte[] imageBytes;
        if (pic != null && pic.length() > 1000 && pic.length() < 137000) {
            imageBytes = Base64.decode(pic, Base64.DEFAULT);
        } else {
            imageBytes = Base64.decode(PictureRepository.NONE, Base64.DEFAULT);
        }
        Bitmap bp = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
        if (bp == null) {
            imageBytes = Base64.decode(PictureRepository.FAILED, Base64.DEFAULT);
            bp = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
        }
        return bp;
    }

    private void amIFollow(Button follow) {
        SidUid sidUid = new SidUid();
        sidUid.setSid(sid.getSid());
        sidUid.setUid(user.getUid());
        ApiInterface apiInterface = ServerConnection.newApiInterface();
        Call<Followed> call = apiInterface.isFollowed(sidUid);
        call.enqueue(new Callback<Followed>() {
            @Override
            public void onResponse(@NonNull Call<Followed> call, @NonNull Response<Followed> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, user.getName()+" è seguito? : " + response.body().isFollowed());
                    if (response.body().isFollowed()) {
                        userFollowed = true;
                        resolveButton(userFollowed, follow);
                    } else {
                        userFollowed = false;
                        resolveButton(userFollowed, follow);
                    }
                } else {
                    follow.setText("NO DATA");
                    follow.setBackgroundColor(BLUE);
                }
            }
            @Override
            public void onFailure(@NonNull Call<Followed> call, @NonNull Throwable t) {
                Log.d(TAG, "ho fallito");
                follow.setText("NO DATA");
                follow.setBackgroundColor(WHITE);
                t.printStackTrace();
            }
        });
    }

    public void followOrUnfollow(Button button, FollowListener followListener) {
        if (userFollowed) {
            button.setText("Follow");
            button.setBackgroundColor(GREEN);
            userFollowed = false;
            SidUid sidUid = new SidUid();
            sidUid.setSid(sid.getSid());
            sidUid.setUid(user.getUid());
            ApiInterface apiInterface = ServerConnection.newApiInterface();
            Call<Void> call = apiInterface.unfollow(sidUid);
            call.enqueue(new Callback<Void>() {
                @Override
                public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                    if (response.isSuccessful()) {
                        followListener.onFollowOrUnfollow("Hai smesso di seguire " + user.getName() + "\ncorrettamente");
                    } else {
                        followListener.onFollowOrUnfollow("Errore di rete\nriprovare più tardi");
                    }
                }
                @Override
                public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                    Log.d(TAG, "ho fallito");
                    followListener.onFollowOrUnfollow("Errore di rete\nriprovare più tardi");
                    t.printStackTrace();
                }
            });
        } else {
            button.setText("Unfollow");
            button.setBackgroundColor(RED);
            userFollowed = true;
            SidUid sidUid = new SidUid();
            sidUid.setSid(sid.getSid());
            sidUid.setUid(user.getUid());
            ApiInterface apiInterface = ServerConnection.newApiInterface();
            Call<Void> call = apiInterface.follow(sidUid);
            call.enqueue(new Callback<Void>() {
                @Override
                public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                    if (response.isSuccessful()) {
                        followListener.onFollowOrUnfollow("Hai iniziato a seguire " + user.getName() + "\ncorrettamente");
                    } else {
                        followListener.onFollowOrUnfollow("Errore di rete\nriprovare più tardi");
                    }
                }
                @Override
                public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                    Log.d(TAG, "ho fallito");
                    followListener.onFollowOrUnfollow("Errore di rete\nriprovare più tardi");
                    t.printStackTrace();
                }
            });
        }

    }
}
