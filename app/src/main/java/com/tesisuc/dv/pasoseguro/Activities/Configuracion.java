package com.tesisuc.dv.pasoseguro.Activities;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.ListPreference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;
import android.preference.SwitchPreference;
import android.support.v4.content.LocalBroadcastManager;

import com.tesisuc.dv.pasoseguro.MainActivity;
import com.tesisuc.dv.pasoseguro.R;

public class Configuracion extends PreferenceActivity implements OnSharedPreferenceChangeListener{

    private static final String tag = Configuracion.class
            .getSimpleName();
    private SharedPreferences sp;
    private SharedPreferences.Editor editor;
    private ListPreference tiempo_preference;
    private ListPreference seguridad_preference;
    private ListPreference algoritmo_preference;
    private static final int OK_RESULT_CODE = 1;
    public String currValue1;
    public String currValue2;
    public String currValue3;
    public Configuracion() {
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.configuracion);
        sp = getSharedPreferences("Usuarios", Context.MODE_PRIVATE);
        editor = sp.edit();
        seguridad_preference = (ListPreference) findPreference("seguridad_preference");
        tiempo_preference = (ListPreference) findPreference("tiempo_preference");
        algoritmo_preference = (ListPreference) findPreference("algoritmo_preference");
        currValue1 = seguridad_preference.getValue();
        currValue2 = algoritmo_preference.getValue();
        currValue3 = tiempo_preference.getValue();
        seguridad_preference.getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
        tiempo_preference.getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
        algoritmo_preference.getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
        editor.putString("seguridad_preference", currValue1);
        editor.putString("tiempo_preference", currValue3);
        editor.putString("algoritmo_preference", currValue2);
        editor.commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        seguridad_preference.getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
        tiempo_preference.getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
        algoritmo_preference.getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        seguridad_preference.getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
        tiempo_preference.getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
        algoritmo_preference.getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }



    public void actualizarInten() {
        Intent intent = new Intent();
        intent.putExtra("ReinicioActividad", true);
        setResult(OK_RESULT_CODE, intent);
        finish();
    }


    public void LLamado() {

        currValue1 = seguridad_preference.getValue();
        currValue2 = algoritmo_preference.getValue();
        currValue3 = tiempo_preference.getValue();
        editor.putString("seguridad_preference", currValue1);
        editor.putString("tiempo_preference", currValue3);
        editor.putString("algoritmo_preference", currValue2);
        editor.commit();


    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        LLamado();
        actualizarInten();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}