package com.tesisuc.dv.pasoseguro.Services;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.StrictMode;
import android.support.v4.app.ActivityCompat;
import android.telephony.SmsManager;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.Location;

import java.text.DecimalFormat;

import android.util.Log;

import com.tesisuc.dv.pasoseguro.Activities.Registro;
import com.tesisuc.dv.pasoseguro.MainActivity;
import com.tesisuc.dv.pasoseguro.Procesos.Correo;
import com.tesisuc.dv.pasoseguro.Procesos.MyLocation;

import javax.mail.MessagingException;


public class Respuesta extends Service {
    private Correo servidorcorreo;
    private SharedPreferences sp;
    private SharedPreferences.Editor editor;
    private LocationManager locationManager;
    private double longitud, latitud;
    private boolean bandera;
    MainActivity mainActivity;

    @Override
    public void onCreate() {
        super.onCreate();
        //REsolver setMainActivity();
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        locationStart();
        sp = getSharedPreferences("Usuarios", Context.MODE_PRIVATE);
        editor = sp.edit();
        bandera = false;
    }

    public MainActivity getMainActivity() {
        return mainActivity;
    }

    public void setMainActivity() {
        this.mainActivity = mainActivity;
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int cont =0;

        super.onStartCommand(intent, flags, startId);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                if (isLocationEnabled() ) {
                    DecimalFormat df = new DecimalFormat("0.0000000");
                    if( latitud > 0.01){
                        String phone;
						if(sp.getString("usuarioActual", "").equals(sp.getString("usuario1",""))){
                                 phone = sp.getString("telefono1", "");
                            }else{
                                 phone = sp.getString("telefono2", "");
                            }
                        String text = "Se detecto un uso no autorizado sobre el dispositivo de " + sp.getString("usuarioActual", "") + ". Las últimas coordenadas conocidas del dispositivo son: Latitud: " +
                                df.format(latitud) + " Longitud: " + df.format(longitud);
                        SmsManager sms = SmsManager.getDefault();
                        sms.sendTextMessage(phone, null, text, null, null);
                        try {
                            if(sp.getString("usuarioActual", "").equals(sp.getString("usuario1",""))){
                                servidorcorreo = new Correo(sp.getString("correo1", ""),sp.getString("usuarioActual", ""),df.format(latitud),df.format(longitud));
                            }else{
                                servidorcorreo = new Correo(sp.getString("correo2", ""),sp.getString("usuarioActual", ""),df.format(latitud),df.format(longitud));
                            }
                            servidorcorreo.enviarMensaje("Alerta de seguridad");
                        } catch (MessagingException e) {
                            e.printStackTrace();
                        }

                    }else{
                        String phone;
                        if(sp.getString("usuarioActual", "").equals(sp.getString("usuario1",""))){
                                 phone = sp.getString("telefono1", "");
						}else{
                                 phone = sp.getString("telefono2", "");
                            }
                        String text = "Se detecto un uso no autorizado sobre el dispositivo de " + sp.getString("usuarioActual", "");
                        SmsManager sms = SmsManager.getDefault();
                        sms.sendTextMessage(phone, null, text, null, null);

                        try {
                            if(sp.getString("usuarioActual", "").equals(sp.getString("usuario1",""))){
                                servidorcorreo = new Correo(sp.getString("correo1", ""),sp.getString("usuarioActual", ""),null,null)
                                ;
                            }else{
                                servidorcorreo = new Correo(sp.getString("correo2", ""),sp.getString("usuarioActual", ""),null,null);
                            }
                            servidorcorreo.enviarMensaje("Alerta de seguridad");
                        } catch (MessagingException e) {
                            e.printStackTrace();
                        }

                    }
                } else {
                    String phone;

                    if(sp.getString("usuarioActual", "").equals(sp.getString("usuario1",""))){
                                 phone = sp.getString("telefono1", "");
                     }else{
                                 phone = sp.getString("telefono2", "");
                            }
                    String text = "Se detecto un uso no autorizado sobre el dispositivo de " + sp.getString("usuarioActual", "");
                    SmsManager sms = SmsManager.getDefault();
                    sms.sendTextMessage(phone, null, text, null, null);

                    try {
                        if(sp.getString("usuarioActual", "").equals(sp.getString("usuario1",""))){
                            servidorcorreo = new Correo(sp.getString("correo1", ""),sp.getString("usuarioActual", ""),null,null);
                        }else{
                            servidorcorreo = new Correo(sp.getString("correo2", ""),sp.getString("usuarioActual", ""),null,null);
                        }
                        servidorcorreo.enviarMensaje("Alerta de seguridad");
                    } catch (MessagingException e) {
                        e.printStackTrace();
                    }
                }
                stopSelf();
                onDestroy();
            }

        }, 22000);

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void locationStart() {
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        Localizacion Local = new Localizacion();
        final boolean gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(mainActivity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,}, 1000);
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, (LocationListener) Local);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, (LocationListener) Local);

    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 1000) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                locationStart();
                return;
            }
        }
    }


    //Clases para la localizacion


    private boolean isLocationEnabled() {
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    public class Localizacion implements LocationListener {

        @Override
        public void onLocationChanged(Location loc) {
            // Este metodo se ejecuta cada vez que el GPS recibe nuevas coordenadas
            // debido a la deteccion de un cambio de ubicacion
            latitud = loc.getLatitude();
            longitud = loc.getLongitude();

        }

        @Override
        public void onProviderDisabled(String provider) {
            // Este metodo se ejecuta cuando el GPS es desactivado
        }

        @Override
        public void onProviderEnabled(String provider) {
            // Este metodo se ejecuta cuando el GPS es activado
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
           /* switch (status) {
                case LocationProvider.AVAILABLE:
                    Log.d("debug", "LocationProvider.AVAILABLE");
                    break;
                case LocationProvider.OUT_OF_SERVICE:
                    Log.d("debug", "LocationProvider.OUT_OF_SERVICE");
                    break;
                case LocationProvider.TEMPORARILY_UNAVAILABLE:
                    Log.d("debug", "LocationProvider.TEMPORARILY_UNAVAILABLE");
                   break;
          */
        }
    }

    private void espera() {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
        }
    }






}















