package com.tesisuc.dv.pasoseguro.Services;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v4.content.LocalBroadcastManager;

import com.kircherelectronics.fsensor.filter.BaseFilter;
import com.kircherelectronics.fsensor.filter.averaging.AveragingFilter;
import com.kircherelectronics.fsensor.filter.averaging.LowPassFilter;
import com.kircherelectronics.fsensor.filter.averaging.MedianFilter;
import com.kircherelectronics.fsensor.linearacceleration.LinearAcceleration;
import com.kircherelectronics.fsensor.linearacceleration.LinearAccelerationAveraging;
import com.tesisuc.dv.pasoseguro.Procesos.Autenticador;
import com.tesisuc.dv.pasoseguro.Procesos.Ciclos;
import com.tesisuc.dv.pasoseguro.Procesos.Knn;
import com.tesisuc.dv.pasoseguro.Procesos.Procesador;
import com.tesisuc.dv.pasoseguro.Procesos.SQLite;

import java.util.ArrayList;

public class AutenticacionExperimental extends IntentService implements SensorEventListener {

    private int numeroTablas;
    private String usuarioActual;
    private SharedPreferences sp;
    private SharedPreferences.Editor editor;
    private SQLite sqlHelper;
    private Procesador procesadorAutentico;
    private Procesador procesadorDesconocido;
    private Boolean controlService;
    //Sensor
    private SensorManager sm;
    private Sensor s;
    private int contadorMuestras;
    //Filtros
    private BaseFilter lpfSmoothing1;
    private BaseFilter lpfSmoothing2;
    private BaseFilter medianFilter;
    private LinearAcceleration lpfLinear;
    private AveragingFilter lpfGravity;
    //Vectores de tiempo real
    ArrayList<Double> xDesconocido;
    ArrayList<Double> yDesconocido;
    ArrayList<Double> zDesconocido;
    ArrayList<Double> magnitudDesconocido;
    //Datos del desconocido
    ArrayList<double[]> mciclosT;
    ArrayList<double[]> zciclosT;
    //Configuracion
    private String nivelSeguridad;
    private String metodo;
    private Knn clasificadorKnn;

    public AutenticacionExperimental() {
        super("Autenticador");



    }

    @Override
    public void onCreate() {
        super.onCreate();
        controlService = true;
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        espera();

        sp = getSharedPreferences("Usuarios", Context.MODE_PRIVATE);
        editor = sp.edit();
        usuarioActual = sp.getString("usuarioActual","");
        numeroTablas = sp.getInt(usuarioActual, 1);
        procesadorAutentico = new Procesador(usuarioActual, numeroTablas);
        nivelSeguridad = sp.getString("seguridad_preference", "1");
        metodo= sp.getString("algoritmo_preference", "0");
        //Configuración de Filtros
        lpfSmoothing1 = new LowPassFilter();
        lpfSmoothing2 = new LowPassFilter();
        lpfGravity = new LowPassFilter();
        medianFilter = new MedianFilter();
        lpfLinear = new LinearAccelerationAveraging(lpfGravity);
        medianFilter.setTimeConstant(0.06f);
        lpfSmoothing1.setTimeConstant(0.06f);
        lpfSmoothing2.setTimeConstant(0.06f);
        lpfLinear.setTimeConstant(0.11f);

        try {
            sqlHelper = new SQLite(this, usuarioActual, null, 1);
        }catch (Exception e){

        }

        switch (metodo) {
            case "0":
                //Autenticacion metodo experimental
                generarModeloUsuarioActual();

                while(controlService){
                    //Obtener el sensor y configuración
                    sm = (SensorManager) getSystemService(SENSOR_SERVICE);
                    s = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

                    contadorMuestras = 0;
                    xDesconocido = new ArrayList<>();
                    yDesconocido = new ArrayList<>();
                    zDesconocido = new ArrayList<>();
                    magnitudDesconocido = new ArrayList<>();

                    sm.registerListener(this, s, SensorManager.SENSOR_DELAY_GAME);

                    espera();

                    postSensor();

                }
                break;
            case "1":
                //Atenticacion K-NN

                generarModeloUsuarioActual();

                while(controlService){
                    //Obtener el sensor y configuración
                    sm = (SensorManager) getSystemService(SENSOR_SERVICE);
                    s = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

                    contadorMuestras = 0;
                    xDesconocido = new ArrayList<>();
                    yDesconocido = new ArrayList<>();
                    zDesconocido = new ArrayList<>();
                    magnitudDesconocido = new ArrayList<>();

                    sm.registerListener(this, s, SensorManager.SENSOR_DELAY_GAME);

                    espera();

                    postSensor2();

                }
                break;
            default:
                //Autenticacion doble
                generarModeloUsuarioActual();
                System.out.println("TTTTTTTTTTTTTTTTTTTTtt");


                while(controlService){
                    //Obtener el sensor y configuración
                    sm = (SensorManager) getSystemService(SENSOR_SERVICE);
                    s = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

                    contadorMuestras = 0;
                    xDesconocido = new ArrayList<>();
                    yDesconocido = new ArrayList<>();
                    zDesconocido = new ArrayList<>();
                    magnitudDesconocido = new ArrayList<>();

                    sm.registerListener(this, s, SensorManager.SENSOR_DELAY_GAME);

                    espera();

                    postSensor3();

                }
                break;
        }




    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        contadorMuestras++;

        //Vector a filtrar con componentes x, y y z
        float[] acceleration = new float[3];
        System.arraycopy(event.values, 0, acceleration, 0, event.values.length);

        //Aplicacion de filtros
        lpfGravity.filter(acceleration);
        acceleration = lpfLinear.filter(acceleration);
        acceleration = lpfSmoothing1.filter(acceleration);
        acceleration = lpfSmoothing2.filter(acceleration);
        acceleration = medianFilter.filter(acceleration);

        xDesconocido.add(((double) acceleration[0]));
        yDesconocido.add(((double) acceleration[1]));
        zDesconocido.add(((double) acceleration[2]));

        if (contadorMuestras > 310) {
            sm.unregisterListener(AutenticacionExperimental.this, s);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        controlService = false;
        stopSelf();
    }

    public void postSensor() {
        // acciones que se ejecutan tras los milisegundos
        //sm.unregisterListener(AutenticacionExperimental.this,s);
        boolean resp = generarModeloDesconocido();
        if (resp) {
            ArrayList<Integer> resultado = autenticar();
            Intent intent = new Intent("1");
            intent.putExtra("resultado", resultado);
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
        }
    }




    public void postSensor2() {
        // acciones que se ejecutan tras los milisegundos
        boolean resp = generarModeloDesconocido();
        if (resp) {
            ArrayList<Integer> resultado = autenticarKNN();
            Intent intent = new Intent("1");
            intent.putExtra("resultado", resultado);
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
        }
    }

    public void postSensor3() {
        // acciones que se ejecutan tras los milisegundos
        //sm.unregisterListener(AutenticacionExperimental.this,s);
        System.out.println("GGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGgg");
        boolean resp = generarModeloDesconocido();
        if (resp) {
            ArrayList<Integer> resultado2 = autenticarKNN();
            ArrayList<Integer> resultado1 = autenticar();
            Intent intent = new Intent("1");
            intent.putExtra("resultado", resultado1);
            intent.putExtra("resultado2", resultado2);
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
        }
    }

    private ArrayList<Integer> autenticarKNN() {
        ArrayList<Integer> resultado = new ArrayList<>();
        ArrayList<double[]> matrizZActual = procesadorDesconocido.getMatrizZ2();


        for (int i = 0; i < matrizZActual.size(); i++) {

            ArrayList<double[]> aux = new ArrayList<>();
            aux.add(matrizZActual.get(i));
            clasificadorKnn = new Knn(7, procesadorAutentico.getMatrizZ2(), this);
            Boolean resp = clasificadorKnn.clasificar(aux);
            if (resp) {
                resultado.add(3);

            } else {
                resultado.add(0);
            }
        }

        return resultado;
    }

    private ArrayList<Integer> autenticar(){
        Autenticador autenticador = new Autenticador(
                procesadorAutentico.getZprom(),
                procesadorAutentico.getMprom(),
                procesadorAutentico.getPearsonM(),
                procesadorAutentico.getSpearmanM(),
                procesadorAutentico.getPearsonZ(),
                procesadorAutentico.getSpearmanZ(),
                procesadorAutentico.getMatrizM(),
                procesadorAutentico.getMatrizZ(),
                procesadorAutentico.getZfft(),
                procesadorAutentico.getMfft());

        ArrayList<Integer> resultado = new ArrayList<>();

        for (int i = 0; i < zciclosT.size(); i++) {
            resultado.add(autenticador.verificarPersona(zciclosT.get(i), mciclosT.get(i)));
        }

        return resultado;
    }



    private void generarModeloUsuarioActual(){
        ArrayList<ArrayList<Double>> mtotal;
        ArrayList<ArrayList<Double>> ztotal;
        ArrayList<Integer> muestraslongitud;
        mtotal = new ArrayList<>();
        ztotal = new ArrayList<>();
        muestraslongitud = new ArrayList<>();

        ArrayList<Double> x;
        ArrayList<Double> y;
        ArrayList<Double> z;
        ArrayList<Double> magnitud = new ArrayList<>();
        Ciclos ciclosZyMagnitud;

        for (int i = 0; i < numeroTablas; i++) {
            String nombreTablaXYZ = usuarioActual + "xyz" + i;
            x = new ArrayList<Double>(convertirVectorArray(sqlHelper.consultarVector(nombreTablaXYZ, "x")));
            y =  new ArrayList<Double>(convertirVectorArray(sqlHelper.consultarVector(nombreTablaXYZ, "y")));
            z =  new ArrayList<Double>(convertirVectorArray(sqlHelper.consultarVector(nombreTablaXYZ, "z")));
            magnitud = new ArrayList<>();
            for (int j = 0; j < z.size(); j++) {
                magnitud.add(Math.sqrt(Math.pow(x.get(j), 2) + Math.pow(y.get(j), 2) + Math.pow(z.get(j), 2)));

            }

            ciclosZyMagnitud = new Ciclos(magnitud, z);
            ciclosZyMagnitud.calcularCiclos();

            for (int k = 0; k < ciclosZyMagnitud.ciclosZ.size(); k++) {
                ztotal.add(new ArrayList<>(ciclosZyMagnitud.ciclosZ.get(k)));
                mtotal.add(new ArrayList<>(ciclosZyMagnitud.ciclosM.get(k)));
                muestraslongitud.add(ciclosZyMagnitud.ciclosZ.get(k).size());
            }

        }

        int n = Ciclos.mediaCiclos(ztotal);


        procesadorAutentico.procesarCiclos(ztotal, mtotal, muestraslongitud, n);



    }

    private boolean generarModeloDesconocido(){
        boolean resp = true;
        procesadorDesconocido = new Procesador("Desconocido", 0);
        xDesconocido.remove(0);
        yDesconocido.remove(0);
        zDesconocido.remove(0);
        xDesconocido.remove(0);
        yDesconocido.remove(0);
        zDesconocido.remove(0);
        ArrayList<ArrayList<Double>> mtotal;
        ArrayList<ArrayList<Double>> ztotal;
        mtotal = new ArrayList<ArrayList<Double>>();
        ztotal = new ArrayList<ArrayList<Double>>();

        for (int j = 0; j < zDesconocido.size(); j++) {
            magnitudDesconocido.add(Math.sqrt(Math.pow(xDesconocido.get(j), 2) + Math.pow(yDesconocido.get(j), 2) + Math.pow(zDesconocido.get(j), 2)));
        }

        Ciclos ciclosZyMagnitud = new Ciclos(magnitudDesconocido, zDesconocido);
        ciclosZyMagnitud.calcularCiclos();

        if(ciclosZyMagnitud.ciclosZ.size() != 0) {
            for (int k = 0; k < ciclosZyMagnitud.ciclosZ.size(); k++) {
                ztotal.add(new ArrayList<>(ciclosZyMagnitud.ciclosZ.get(k)));
                mtotal.add(new ArrayList<>(ciclosZyMagnitud.ciclosM.get(k)));
            }
            double[] mciclos;
            double[] zciclos;
            zciclosT = new ArrayList<double[]>();
            mciclosT = new ArrayList<double[]>();
            for (int i = 0; i < ztotal.size(); i++) {
                mciclos = new double[ztotal.get(i).size()];
                zciclos = new double[ztotal.get(i).size()];
                for (   int j = 0; j < ztotal.get(i).size(); j++) {
                    zciclos[j] = ztotal.get(i).get(j);
                    mciclos[j] = mtotal.get(i).get(j);
                }

                zciclosT.add(zciclos);
                mciclosT.add(mciclos);
            }

            int n = Ciclos.mediaCiclos(ztotal);
            procesadorDesconocido.procesarCiclos(ztotal, mtotal, new ArrayList<Integer>(), n);

        }else{
            resp = false;
        }
        return resp;
    }



    private void espera()
    {
        try {
            Thread.sleep(7000);
        } catch(InterruptedException e) {}
    }

    private void dormir()
    {
        try {
            Thread.sleep(7000);
        } catch(InterruptedException e) {}
    }

    public ArrayList<Double> convertirVectorArray(float[] vector){
        ArrayList<Double> array = new ArrayList<>();

        for (int i = 0; i < vector.length; i++) {
            array.add((double) vector[i]);
        }
        return  array;
    }


}