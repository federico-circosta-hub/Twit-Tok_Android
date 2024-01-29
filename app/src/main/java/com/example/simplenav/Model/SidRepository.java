package com.example.simplenav.Model;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.simplenav.Controller.SidInitializeError;
import com.example.simplenav.Controller.SidInitializeListener;
import com.example.simplenav.Model.CallApi.ApiInterface;
import com.example.simplenav.Model.CallApi.ServerConnection;
import com.example.simplenav.Model.CallApi.Sid;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SidRepository {

    private static final String TAG = "SidRepository";
    private static final String ERROR = "Errore di rete,\nriprovare più tardi";
    public static Sid sid = new Sid();

    public static Sid getSid() {
         return sid;
    }

    static public void initSid(Context context, SidInitializeListener sil, SidInitializeError sie) {
        SharedPreferences availableSid = context.getSharedPreferences("SID", 0);
        String sidcode = availableSid.getString("sidCode", "NONE");
        System.out.println(sidcode);
        if (!sidcode.equals("NONE")) {
            sid.setSid(sidcode);
            sil.onSidInizialized(sid);
        } else {
            Log.d(TAG, "ottengo sid...");
            //String hardCodedSid = "1DcCMg91xjfy8gL0ExaZ";
            //sid.setSid(hardCodedSid);
                ApiInterface apiInterface = ServerConnection.newApiInterface();
                Call<Sid> call = apiInterface.getSid();
                call.enqueue(new Callback<Sid>() {
                    @Override
                    public void onResponse(@NonNull Call<Sid> call, @NonNull Response<Sid> response) {
                        //DONE Controllare che la risposta sia 200.
                        if (response.isSuccessful()) {
                            assert response.body() != null;
                            Log.d(TAG, "" + response.body().getSid());
                            sid = response.body();
                            Log.d(TAG, "ottenuto sid nuovo di zecca");
                            SharedPreferences.Editor editor = availableSid.edit();
                            editor.putString("sidCode", sid.getSid());
                            editor.commit();
                            sil.onSidInizialized(sid);
                        } else {
                            sie.onSidNotInizalized(ERROR);
                            Log.d(TAG, ""+response.code() + " "+ response.message());
                        }
                    }
                    @Override
                    public void onFailure(Call<Sid> call, Throwable t) {
                        //DONE: gestire errore
                        Log.d(TAG, "ho fallito");
                        sie.onSidNotInizalized(ERROR);
                        Log.d(TAG, ""+t.getMessage());
                        t.printStackTrace();
                    }
                });
        }
    }

}

