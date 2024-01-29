package com.example.simplenav.ViewModel;

import android.graphics.Bitmap;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.simplenav.Controller.SendingTwokListener;
import com.example.simplenav.Controller.UserPictureListener;
import com.example.simplenav.Model.CallApi.ApiInterface;
import com.example.simplenav.Model.CallApi.PreparingTwok;
import com.example.simplenav.Model.CallApi.ServerConnection;
import com.example.simplenav.Model.CallApi.Sid;
import com.example.simplenav.Model.PictureRepository;
import com.example.simplenav.Model.SidRepository;
import com.example.simplenav.Model.TwokRepository;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddTwokViewModel extends ViewModel {
    private final static String TAG = "AddTwokViewModel";
    private MutableLiveData<String> text = null;
    private MutableLiveData<String> bgcol = null;
    private MutableLiveData<String> fontcol = null;
    private MutableLiveData<Integer> fontsize = null;
    private MutableLiveData<Integer> fonttype = null;
    private MutableLiveData<Integer> halign = null;
    private MutableLiveData<Integer> valign = null;
    private MutableLiveData<Double> lat = null;
    private MutableLiveData<Double> lon = null;
    private static final String[] fontcolors = {"Fontcol", "Nero", "Rosso", "Verde", "Blu", "Bianco"};
    private static final String[] bgcolors = {"Bgcol","Bianco", "Nero", "Rosso", "Verde", "Blu"};
    private static final String[] fontsizes = {"Fontsize", "Piccolo", "Medio", "Grande"};
    private static final String[] fonttypes = {"Fonttype", "Android", "Monospace", "Serif"};
    private static final String[] halignes = {"Halign", "Sinistra", "Centro", "Destra"};
    private static final String[] valignes = {"Valign", "Alto", "Centro", "Basso"};

    public MutableLiveData<String> getText() {
        if (text == null) {
            text = new MutableLiveData<String>();
            text.setValue("A cosa stai pensando?");
        }
        return text;
    }

    public MutableLiveData<String> getFontcol() {
        if (fontcol == null) {
            fontcol = new MutableLiveData<String>();
            fontcol.setValue("000000");
        }
        return fontcol;
    }

    public MutableLiveData<String> getBgcol() {
        if (bgcol == null) {
            bgcol = new MutableLiveData<String>();
            bgcol.setValue("ffffff");
        }
        return bgcol;
    }

    public MutableLiveData<Integer> getFontsize() {
        if (fontsize == null) {
            fontsize = new MutableLiveData<Integer>();
            fontsize.setValue(1);
        }
        return fontsize;
    }

    public MutableLiveData<Integer> getFonttype() {
        if (fonttype == null) {
            fonttype = new MutableLiveData<Integer>();
            fonttype.setValue(0);
        }
        return fonttype;
    }

    public MutableLiveData<Integer> getHalign() {
        if (halign == null) {
            halign = new MutableLiveData<Integer>();
            halign.setValue(1);
        }
        return halign;
    }

    public MutableLiveData<Integer> getValign() {
        if (valign == null) {
            valign = new MutableLiveData<Integer>();
            valign.setValue(1);
        }
        return valign;
    }

    public MutableLiveData<Double> getLat() {
        if (lat == null) {
            lat = new MutableLiveData<Double>();
        }
        return lat;
    }

    public MutableLiveData<Double> getLon() {
        if (lon == null) {
            lon = new MutableLiveData<Double>();
        }
        return lon;
    }

    public String[] getFontcolors() {
        return fontcolors;
    }

    public String[] getBgcolors() {
        return bgcolors;
    }

    public String[] getFontsizes() {
        return fontsizes;
    }

    public String[] getFonttypes() {
        return fonttypes;
    }

    public String[] getHalignes() {
        return halignes;
    }
    public String[] getValignes() {
        return valignes;
    }

    public void wrapTwok(SendingTwokListener l) {
        Sid sid = SidRepository.getSid();
        if (sid != null) {
            PreparingTwok preparingTwok = new PreparingTwok();
            preparingTwok.setSid(sid.getSid());
            preparingTwok.setText(getText().getValue());
            preparingTwok.setFontcol(getFontcol().getValue());
            preparingTwok.setBgcol(getBgcol().getValue());
            preparingTwok.setFontsize(getFontsize().getValue());
            preparingTwok.setFonttype(getFonttype().getValue());
            preparingTwok.setHalign(getHalign().getValue());
            preparingTwok.setValign(getValign().getValue());
            preparingTwok.setLat(getLat().getValue());
            preparingTwok.setLon(getLon().getValue());

            Log.d(TAG, "inviando twok " + preparingTwok.toString());

            ApiInterface apiInterface = ServerConnection.newApiInterface();
            Call<Void> call = apiInterface.addTwok(preparingTwok);
            call.enqueue(new Callback<Void>() {
                @Override
                public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                    if (response.isSuccessful()) {
                        l.onSendedTwok("Twok inviato correttamente!");
                    } else {
                        Log.d(TAG, response.code()+ " "+response.message());
                        l.onSendedTwok("Errore di rete\nriprovare più tardi");
                    }
                }

                @Override
                public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                    Log.d(TAG, "ho fallito");
                    l.onSendedTwok("Errore di rete\nriprovare più tardi");
                    t.printStackTrace();
                }
            });
        }
    }
}
