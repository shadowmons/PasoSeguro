package com.tesisuc.dv.pasoseguro.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.tesisuc.dv.pasoseguro.Graficar.ChartCiclos;
import com.tesisuc.dv.pasoseguro.MainActivity;
import com.tesisuc.dv.pasoseguro.Procesos.Ciclos;
import com.tesisuc.dv.pasoseguro.Procesos.SQLite;
import com.tesisuc.dv.pasoseguro.R;

import java.util.ArrayList;

public class VerCiclos extends AppCompatActivity {

    private ChartCiclos chartCiclos;
    private ArrayList<Double> magnitud;
    private int numeroTablas;
    private  ArrayList<ArrayList<Double>> mtotal;
    private  ArrayList<ArrayList<Double>> ztotal;
    private ArrayList<Integer> muestraslongitud;
    private String usuarioActual;
    private String nombreBD;
    private SharedPreferences sp;
    private SharedPreferences.Editor editor;
    private SQLite sqlHelper;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ver_ciclos);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        RelativeLayout chartLayout = (RelativeLayout) findViewById(R.id.ver_ciclos_ChartLayout);

        //Chart
        chartCiclos = new ChartCiclos(chartLayout, getApplicationContext(), 4f, "Su patrón de caminar");

        sp = getSharedPreferences("Usuarios", Context.MODE_PRIVATE);
        editor = sp.edit();
        usuarioActual = sp.getString("usuarioActual","");
        nombreBD = usuarioActual.replaceAll("\\s+","");
        numeroTablas = sp.getInt(usuarioActual, 1);
        magnitud = new ArrayList<>();
        mtotal = new ArrayList<>();
        ztotal = new ArrayList<>();
        muestraslongitud = new ArrayList<>();

        try {
            sqlHelper = new SQLite(this, nombreBD, null, 1);
        }catch (Exception e){

        }

        ArrayList<Double> x;
        ArrayList<Double> y;
        ArrayList<Double> z;

        for (int i = 0; i < numeroTablas; i++) {
            String nombreTablaXYZ = nombreBD + "xyz" + i;
            x = new ArrayList<Double>(convertirVectorArray(sqlHelper.consultarVector(nombreTablaXYZ, "x")));
            y =  new ArrayList<Double>(convertirVectorArray(sqlHelper.consultarVector(nombreTablaXYZ, "y")));
            z =  new ArrayList<Double>(convertirVectorArray(sqlHelper.consultarVector(nombreTablaXYZ, "z")));

            for (int j = 0; j < z.size(); j++) {
                magnitud.add(Math.sqrt(Math.pow(x.get(j), 2) + Math.pow(y.get(j), 2) + Math.pow(z.get(j), 2)));
            }

            Ciclos ciclos = new Ciclos(magnitud, z);
            ciclos.calcularCiclos();

            Log.v("cantidad de ciclos", ciclos.ciclosZ.size()+"");

            for (int k = 0; k < ciclos.ciclosZ.size(); k++) {
                ztotal.add(new ArrayList<>(ciclos.ciclosZ.get(k)));
                mtotal.add(new ArrayList<>(ciclos.ciclosM.get(k)));
                muestraslongitud.add(ciclos.ciclosZ.get(k).size());
            }

        }

    ver();
    }

    public void ver(){
        Button ver = (Button) findViewById(R.id.ver_ciclos_btn);
        ver.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                TextInputEditText input = (TextInputEditText) findViewById(R.id.ver_ciclos_input);
                int tabla = Integer.parseInt(input.getText().toString());
                String nombreTablaXYZ = nombreBD + "xyz" + (tabla-1);
                if(numeroTablas >= tabla) {
                    chartCiclos.mostrarChartInterpolado("", sqlHelper.consultarVector(nombreTablaXYZ, "x"), sqlHelper.consultarVector(nombreTablaXYZ, "y"), sqlHelper.consultarVector(nombreTablaXYZ, "z"));
                }else{
                    Toast.makeText(getApplicationContext(), "No existe la tabla.", Toast.LENGTH_LONG).show();
                }
            }
        });
    }


    public ArrayList<Double> convertirVectorArray(float[] vector){
        ArrayList<Double> array = new ArrayList<>();

        for (int i = 0; i < vector.length; i++) {
            array.add((double) vector[i]);
        }
        return  array;
    }

    public float[] convertirArrayVector (ArrayList<Double> array){
        float[] vector = new float[array.size()];

        for (int i = 0; i < array.size(); i++) {
            vector[i] = (float) array.get(i).doubleValue();
        }

        return vector;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
