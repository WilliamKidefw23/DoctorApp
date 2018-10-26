package com.doctor.app.doctorapp.Utilitario;

import android.content.Context;
import android.content.SharedPreferences;

public class Preferences {

    static Context ctx;

    public static void SavedPreferences(Context context,String valor){
        ctx = context;
        SharedPreferences prefs = ctx.getSharedPreferences(Constantes.PREFERENCE_UID, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(Constantes.UID_USER, valor);
        editor.commit();
    }

    public static String GetPreferencesUid(Context context){
        ctx = context;
        SharedPreferences prefs = ctx.getSharedPreferences(Constantes.PREFERENCE_UID,Context.MODE_PRIVATE);
        String valor = prefs.getString(Constantes.UID_USER, "");
        return valor;
    }
}
