package com.example.simplenav.View;

import static android.app.Activity.RESULT_OK;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager2.widget.ViewPager2;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.simplenav.Controller.BottomListenerForProfile;
import com.example.simplenav.Controller.ClickListener;
import com.example.simplenav.Controller.ClickListenerForTwok;
import com.example.simplenav.Controller.TwokListForProfileAdapter;
import com.example.simplenav.Model.PersonalProfileModel;
import com.example.simplenav.R;

import java.io.File;

public class PersonalProfile extends Fragment {
    public PersonalProfileModel personalProfileModel = PersonalProfileModel.getInstance();
    private static final String TAG = "PersonalProfilee";
    private static final int IMAGE_PICKER_REQUEST = 1;
    private ImageView image;

    public PersonalProfile() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d("PersonalProfile", "OnCreateView");
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.personal_profile, container, false);

        ViewPager2 viewPager = view.findViewById(R.id.personalViewPager);
        TextView nameTextView = view.findViewById(R.id.nome);
        AlertDialog dialog = new AlertDialog.Builder(this.getContext()).create();
        EditText editText = new EditText(this.getContext());

        SwipeRefreshLayout swipeRefreshLayout = view.findViewById(R.id.swipe_list_container);

        dialog.setTitle("Cambia username");
        dialog.setView(editText);
        dialog.setButton(DialogInterface.BUTTON_POSITIVE, "Salva", (dialogInterface, i) -> {
            if (!String.valueOf(editText.getText()).trim().equals("") && editText.getText().length() < 20) {
                nameTextView.setText(editText.getText());
                personalProfileModel.changeUsername(String.valueOf(editText.getText()), ucl -> {
                    AlertDialog newDialog = new AlertDialog.Builder(this.getContext()).create();
                    newDialog.setTitle(ucl);
                    newDialog.show();
                });
            }
        });

        nameTextView.setOnClickListener(l -> {
            editText.setText(nameTextView.getText());
            dialog.show();
        });
        image = view.findViewById(R.id.personalPic);

        TwokListForProfileAdapter adapter = new TwokListForProfileAdapter(personalProfileModel.getTwokListRepository(), view.getContext(), new ClickListenerForTwok() {
            @Override
            public void onListClickEvent(int index) {
            }

            @Override
            public void onListClickEventMap(int index) {
                Log.d(TAG, "Click on map with position: " + index);
                if (personalProfileModel.getTwokListRepository().getTwok(index).getLat() != null && personalProfileModel.getTwokListRepository().getTwok(index).getLon() != null) {
                    Bundle bundle = new Bundle();
                    bundle.putDouble("lat", personalProfileModel.getTwokListRepository().getTwok(index).getLat());
                    bundle.putDouble("lon", personalProfileModel.getTwokListRepository().getTwok(index).getLon());
                    bundle.putString("autore", personalProfileModel.getTwokListRepository().getTwok(index).getAuthor());
                    bundle.putString("twokText", personalProfileModel.getTwokListRepository().getTwok(index).getTwokText());
                    Log.d(TAG, "passo " + personalProfileModel.getTwokListRepository().getTwok(index).getLat() +" "+ personalProfileModel.getTwokListRepository().getTwok(index).getLon());
                    PersonalProfile.this.getFragmentManager().beginTransaction()
                            .setReorderingAllowed(true)
                            .replace(R.id.fragmentContainerView, MapsFragment.class, bundle)
                            .addToBackStack(null)
                            .commit();
                }else {
                    AlertDialog newDialog = new AlertDialog.Builder(PersonalProfile.this.getContext()).create();
                    newDialog.setTitle("Nessuna posizione per il twok selezionato");
                    newDialog.show();
                }
            }
        }, new BottomListenerForProfile() {
            @Override
            public void onBottomReached(TwokListForProfileAdapter adapter) {
                personalProfileModel.fiveMoreTwoks(adapter);
            }
        });
        personalProfileModel.initProfile(adapter, nameTextView, image);
        viewPager.setAdapter(adapter);
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openImagePicker();
            }
        });

        swipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        Log.d(TAG, "onRefresh called from SwipeRefreshLayout");
                        // This method performs the actual data-refresh operation.
                        // The method calls setRefreshing(false) when it's finished.
                        personalProfileModel.refresh(adapter);
                        swipeRefreshLayout.setRefreshing(false);
                    }
                });
        return view;
}

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");

        startActivityForResult(intent, IMAGE_PICKER_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == IMAGE_PICKER_REQUEST && resultCode == RESULT_OK) {
            Uri imageUri = data.getData();

            String filePath = "";
            Cursor cursor = getContext().getContentResolver().query(imageUri, null, null, null, null);
            if (cursor != null) {
                cursor.moveToFirst();
                int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                filePath = cursor.getString(idx);
                cursor.close();
            }
            File file = new File(filePath);
            long fileSizeInBytes = file.length();
            long fileSizeInKB = fileSizeInBytes / 1024;
            Log.d(TAG, String.valueOf(fileSizeInBytes));
            if (fileSizeInKB <= 100) {
                image.setImageURI(imageUri);
                personalProfileModel.changeImage(imageUri, this.getContext(), icl -> {
                    AlertDialog newDialog = new AlertDialog.Builder(this.getContext()).create();
                    newDialog.setTitle(icl);
                    newDialog.show();
                });
            } else {
                AlertDialog newDialog = new AlertDialog.Builder(this.getContext()).create();
                newDialog.setTitle("Immagine troppo grande\nselezionarne un'altra");
                newDialog.show();
            }
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        Log.d("PersonalProfile", "OnCreate");
        super.onCreate(savedInstanceState);
    }
}