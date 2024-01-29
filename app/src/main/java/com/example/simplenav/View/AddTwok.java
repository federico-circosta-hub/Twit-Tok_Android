package com.example.simplenav.View;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.renderscript.RenderScript;
import android.text.InputType;
import android.text.Layout;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.simplenav.Controller.SendingTwokListener;
import com.example.simplenav.R;
import com.example.simplenav.ViewModel.AddTwokViewModel;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.CancellationToken;
import com.google.android.gms.tasks.CancellationTokenSource;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.OnTokenCanceledListener;
import com.google.android.gms.tasks.Task;

public class AddTwok extends Fragment {
    private final static String TAG = "AddTwok";
    private AddTwokViewModel model;
    private TextView twokText;
    private View backgroud;
    private Switch locationSwitch;
    private FusedLocationProviderClient fusedLocationClient;

    public AddTwok() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.add_twok, container, false);

         model = new ViewModelProvider(this).get(AddTwokViewModel.class);

        twokText = view.findViewById(R.id.twokText);
        backgroud = view.findViewById(R.id.constraintLayout);
        locationSwitch = view.findViewById(R.id.detectPosition);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this.getActivity());

        locationSwitch.setOnCheckedChangeListener((button, checked) -> {
            //se attivo switch
            if (checked) {
                // controllo se l'utente aveva dato i permessi di calcolo posizione
                if (ContextCompat.checkSelfPermission(this.getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    //I permessi non sono stati (ancora) concessi
                    ActivityCompat.requestPermissions(this.getActivity(),
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0
                    );
                    locationSwitch.setChecked(false);
                } else {
                //I permessi sono stati concessi }
                    calculatePosition();
                }
            } else {
                model.getLat().setValue(null);
                model.getLon().setValue(null);
            }
        });


        AlertDialog dialog = new AlertDialog.Builder(this.getContext()).create();
        EditText editText = new EditText(this.getContext());

        Spinner fontcolSpinner = view.findViewById(R.id.fontcol);
        fontcolSpinner.setPrompt("Font color");
        Spinner bgcolSpinner = view.findViewById(R.id.bgcol);
        Spinner fontsizeSpinner = view.findViewById(R.id.fontsize);
        Spinner fontfamilySpinner = view.findViewById(R.id.fontfamily);
        Spinner halignSpinner = view.findViewById(R.id.halign);
        Spinner valignSpinner = view.findViewById(R.id.valign);

        model.getText().observe(this.getViewLifecycleOwner(), s -> {
            twokText.setText(s);
        });

        model.getFontcol().observe(this.getViewLifecycleOwner(), s -> {
            twokText.setTextColor(Color.parseColor("#" + s));
        });

        model.getBgcol().observe(this.getViewLifecycleOwner(), s -> {
            backgroud.setBackgroundColor(Color.parseColor("#" + s));
        });

        model.getFontsize().observe(this.getViewLifecycleOwner(), s -> {
            twokText.setTextSize(sizeResolver(s));
        });

        model.getFonttype().observe(this.getViewLifecycleOwner(), s -> {
            twokText.setTypeface(familyResolver(s));
        });

        model.getHalign().observe(this.getViewLifecycleOwner(), s -> {
            twokText.setGravity(hAlignResolver(s) + vAlignResolver(model.getValign().getValue()));
        });

        model.getValign().observe(this.getViewLifecycleOwner(), s -> {
            twokText.setGravity(vAlignResolver(s) + hAlignResolver(model.getHalign().getValue()));
        });

        dialog.setTitle("Testo del twok");
        dialog.setView(editText);
        dialog.setButton(DialogInterface.BUTTON_POSITIVE, "Salva", (dialogInterface, i) -> {
            if (!String.valueOf(editText.getText()).trim().equals("") && editText.getText().length() < 100) {
                twokText.setText(editText.getText());
                model.getText().setValue(String.valueOf(editText.getText()));
            }
        });

        twokText.setOnClickListener(l -> {
            editText.setText(twokText.getText());
            dialog.show();
        });


        ArrayAdapter<String> adapterFontcol = new ArrayAdapter<>(this.getActivity(), android.R.layout.simple_spinner_item, model.getFontcolors());
        adapterFontcol.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        fontcolSpinner.setAdapter(adapterFontcol);

        // aggiunge un listener al spinner per modificare il colore del testo in base alla selezione dell'utente
        fontcolSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0) {
                    String selectedColor = (String) parent.getItemAtPosition(position);
                    switch (selectedColor) {
                        case "Nero":
                            model.getFontcol().setValue("000000");
                            break;
                        case "Rosso":
                            model.getFontcol().setValue("ff0000");
                            break;
                        case "Verde":
                            model.getFontcol().setValue("00ff7f");
                            break;
                        case "Blu":
                            model.getFontcol().setValue("4169e1");
                            break;
                        case "Bianco":
                            model.getFontcol().setValue("ffffff");
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // non fare nulla
            }
        });

        ArrayAdapter<String> adapterBg = new ArrayAdapter<>(this.getActivity(), android.R.layout.simple_spinner_item, model.getBgcolors());
        adapterBg.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        bgcolSpinner.setAdapter(adapterBg);
        bgcolSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i != 0) {
                    String selectedColor = (String) adapterView.getItemAtPosition(i);
                    switch (selectedColor) {
                        case "Bianco":
                            model.getBgcol().setValue("ffffff");
                            break;
                        case "Rosso":
                            model.getBgcol().setValue("ff0000");
                            break;
                        case "Verde":
                            model.getBgcol().setValue("00ff7f");
                            break;
                        case "Blu":
                            model.getBgcol().setValue("4169e1");
                            break;
                        case "Nero":
                            model.getBgcol().setValue("000000");
                            break;
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        ArrayAdapter<String> adapterFontsize = new ArrayAdapter<>(this.getActivity(), android.R.layout.simple_spinner_item, model.getFontsizes());
        adapterFontsize.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        fontsizeSpinner.setAdapter(adapterFontsize);
        fontsizeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i != 0) {
                    String selectedColor = (String) adapterView.getItemAtPosition(i);
                    switch (selectedColor) {
                        case "Medio":
                            model.getFontsize().setValue(1);
                            break;
                        case "Piccolo":
                            model.getFontsize().setValue(0);
                            break;
                        case "Grande":
                            model.getFontsize().setValue(2);
                            break;
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        ArrayAdapter<String> adapterFontfamily = new ArrayAdapter<>(this.getActivity(), android.R.layout.simple_spinner_item, model.getFonttypes());
        adapterFontfamily.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        fontfamilySpinner.setAdapter(adapterFontfamily);
        fontfamilySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i != 0) {
                    String selectedColor = (String) adapterView.getItemAtPosition(i);
                    switch (selectedColor) {
                        case "Android":
                            model.getFonttype().setValue(0);
                            break;
                        case "Monospace":
                            model.getFonttype().setValue(1);
                            break;
                        case "Serif":
                            model.getFonttype().setValue(2);
                            break;
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        ArrayAdapter<String> adapterHAlign = new ArrayAdapter<>(this.getActivity(), android.R.layout.simple_spinner_item, model.getHalignes());
        adapterHAlign.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        halignSpinner.setAdapter(adapterHAlign);
        halignSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i != 0) {
                    String selectedColor = (String) adapterView.getItemAtPosition(i);
                    switch (selectedColor) {
                        case "Centro":
                            model.getHalign().setValue(1);
                            break;
                        case "Sinistra":
                            model.getHalign().setValue(0);
                            break;
                        case "Destra":
                            model.getHalign().setValue(2);
                            break;
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        ArrayAdapter<String> adapterVAlign = new ArrayAdapter<>(this.getActivity(), android.R.layout.simple_spinner_item, model.getValignes());
        adapterVAlign.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        valignSpinner.setAdapter(adapterVAlign);
        valignSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i != 0) {
                    String selectedColor = (String) adapterView.getItemAtPosition(i);
                    switch (selectedColor) {
                        case "Centro":
                            model.getValign().setValue(1);
                            break;
                        case "Alto":
                            model.getValign().setValue(0);
                            break;
                        case "Basso":
                            model.getValign().setValue(2);
                            break;
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        Button send = view.findViewById(R.id.sendTwok);
        send.setOnClickListener(l -> {
            model.wrapTwok(new SendingTwokListener() {
                @Override
                public void onSendedTwok(String msg) {
                    AlertDialog newDialog = new AlertDialog.Builder(AddTwok.this.getContext()).create();
                    newDialog.setTitle(msg);
                    newDialog.show();
                }
            });
        });

        return view;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);

    }

    private float sizeResolver(int fontsize) {
        switch (fontsize) {
            case 2:
                return 54;
            case 1:
                return 26;
            default:
                return 16;
        }
    }

    private Typeface familyResolver(int fontfamily) {
        switch (fontfamily) {
            case 2:
                return Typeface.SERIF;
            case 1:
                return Typeface.MONOSPACE;
            default:
                return Typeface.DEFAULT;
        }
    }

    private int hAlignResolver(int halign) {
        switch (halign) {
            case 2:
                return Gravity.RIGHT;
            case 1:
                return Gravity.CENTER_HORIZONTAL;
            default:
                return Gravity.LEFT;
        }
    }

    private int vAlignResolver(int valign) {
        switch (valign) {
            case 2:
                return Gravity.BOTTOM;
            case 1:
                return Gravity.CENTER_VERTICAL;
            default:
                return Gravity.TOP;
        }
    }

    @Override
        public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 0: {
        // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
        // permission was granted
                    Log.d("Location", "Now the permission is granted");
                    calculatePosition();
                } else {
                    Log.d("Location", "Permission still not granted");
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    private void calculatePosition() {
        model = new ViewModelProvider(this).get(AddTwokViewModel.class);
        CancellationTokenSource cancellationTokenSource = new CancellationTokenSource();
        fusedLocationClient.getCurrentLocation(100, cancellationTokenSource.getToken())
                .addOnSuccessListener(this.getActivity(), new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        Log.d(TAG, "Last known location:" + location.toString());
                        model.getLat().setValue(location.getLatitude());
                        model.getLon().setValue(location.getLongitude());
                    }
                });
    }
}
