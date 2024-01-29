package com.example.simplenav.View;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.example.simplenav.Controller.BottomListenerForProfile;
import com.example.simplenav.Controller.ClickListener;
import com.example.simplenav.Controller.ClickListenerForTwok;
import com.example.simplenav.Controller.FollowListener;
import com.example.simplenav.Controller.TwokListForProfileAdapter;
import com.example.simplenav.Model.UserProfileModel;
import com.example.simplenav.R;

public class UserProfile extends Fragment {
    public UserProfileModel userProfileModel = new UserProfileModel();
    private String TAG = "UserProfile";
    TextView tv;
    ImageView image;
    Button follow;

    public UserProfile() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.user_profile, container, false);

        ViewPager2 viewPager = view.findViewById(R.id.userViewPager);
        TwokListForProfileAdapter adapter = new TwokListForProfileAdapter(userProfileModel.getTwokListRepository(), view.getContext(), new ClickListenerForTwok() {
            @Override
            public void onListClickEvent(int index) {
            }

            @Override
            public void onListClickEventMap(int index) {
                Log.d(TAG, "Click on map with position: " + index);
                if (userProfileModel.getTwokListRepository().getTwok(index).getLat() != null && userProfileModel.getTwokListRepository().getTwok(index).getLon() != null) {
                    Bundle bundle = new Bundle();
                    bundle.putDouble("lat", userProfileModel.getTwokListRepository().getTwok(index).getLat());
                    bundle.putDouble("lon", userProfileModel.getTwokListRepository().getTwok(index).getLon());
                    bundle.putString("autore", userProfileModel.getTwokListRepository().getTwok(index).getAuthor());
                    bundle.putString("twokText", userProfileModel.getTwokListRepository().getTwok(index).getTwokText());
                    Log.d(TAG, "passo " + userProfileModel.getTwokListRepository().getTwok(index).getLat() +" "+ userProfileModel.getTwokListRepository().getTwok(index).getLon());
                    UserProfile.this.getFragmentManager().beginTransaction()
                            .setReorderingAllowed(true)
                            .replace(R.id.fragmentContainerView, MapsFragment.class, bundle)
                            .addToBackStack(null)
                            .commit();
                } else {
                    AlertDialog newDialog = new AlertDialog.Builder(UserProfile.this.getContext()).create();
                    newDialog.setTitle("Nessuna posizione per il twok selezionato");
                    newDialog.show();
                }
            }
        }, new BottomListenerForProfile() {
            @Override
            public void onBottomReached(TwokListForProfileAdapter adapter) {
                userProfileModel.fiveMoreTwoks(adapter);
            }
        });
        viewPager.setAdapter(adapter);
        tv = view.findViewById(R.id.userID);
        image = view.findViewById(R.id.userProfilePic);
        follow = view.findViewById(R.id.follow);
        userProfileModel.initProfile(adapter,tv,image,getUid(),follow);
        //userProfileModel.amIFollow(follow);

        follow.setOnClickListener(l -> {
            userProfileModel.followOrUnfollow(follow, new FollowListener() {
                @Override
                public void onFollowOrUnfollow(String msg) {
                    AlertDialog newDialog = new AlertDialog.Builder(UserProfile.this.getContext()).create();
                    newDialog.setTitle(msg);
                    newDialog.show();
                }
            });
        });
        return view;
    }

    private int getUid() {
        Bundle b = getArguments();
        return b.getInt("uid");
    }
}