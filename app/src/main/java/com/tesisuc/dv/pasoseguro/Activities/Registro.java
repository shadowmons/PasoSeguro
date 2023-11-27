package com.tesisuc.dv.pasoseguro.Activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;
import com.kircherelectronics.fsensor.filter.BaseFilter;
import com.kircherelectronics.fsensor.filter.averaging.AveragingFilter;
import com.kircherelectronics.fsensor.filter.averaging.LowPassFilter;
import com.kircherelectronics.fsensor.filter.averaging.MedianFilter;
import com.kircherelectronics.fsensor.linearacceleration.LinearAcceleration;
import com.kircherelectronics.fsensor.linearacceleration.LinearAccelerationAveraging;
import com.tesisuc.dv.pasoseguro.MainActivity;
import com.tesisuc.dv.pasoseguro.R;
import com.tesisuc.dv.pasoseguro.Graficar.Chart;
import com.tesisuc.dv.pasoseguro.Procesos.SQLite;

import static java.lang.System.arraycopy;

/**
 * Created by Daniel H on 22/09/2018.
 */

public class Registro extends Activity implements SensorEventListener {
    private int contadorMuestras;
    private ProgressDialog procesando1;
    private ProgressDialog procesando2;
    //Sensor
    private SensorManager sm;
    private Sensor s;
    //Graficador
    private Chart chart;
    //Filtros
    private BaseFilter lpfSmoothing1;
    private BaseFilter lpfSmoothing2;
    private BaseFilter lpfSmoothing3;
    private BaseFilter medianFilter;
    private LinearAcceleration lpfLinear;
    private AveragingFilter lpfGravity;
    //Shared Preferences
    private SharedPreferences sp;
    private SharedPreferences.Editor editor;
    //Variables de la base de datos
    private SQLite sqlHelper;
    private String nombre;
    private String nombreBD;
    private String nombreTablaXYZ;
    private int numeroTabla;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        contadorMuestras = 0;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registro);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        RelativeLayout chartLayout = (RelativeLayout) findViewById(R.id.ChartLayout);

        //Intent
        home();
        borrar();

        //Chart
        chart = new Chart(chartLayout, getApplicationContext(), 5f, "Su patrón de caminar");

        //Usuario Actual
        sp = getSharedPreferences("Usuarios", Context.MODE_PRIVATE);
        editor = sp.edit();
        nombre = sp.getString("usuarioActual", "");
        nombreBD = nombre.replaceAll("\\s+","");
        numeroTabla = sp.getInt(nombre, -1);

        if(numeroTabla<10){
            editor.putInt(nombre,  numeroTabla + 1);
            nombreTablaXYZ = nombreBD + "xyz" + numeroTabla ;
        }
        editor.commit();

        //Apertura de la base de datos
        sqlHelper = new SQLite(this, nombreBD, null, 1);
        sqlHelper.crearTabla(nombreTablaXYZ);

        //Configuracion del progressBar 1
        procesando1 = new ProgressDialog(Registro.this);
        procesando1.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        procesando1.setTitle("Guardando patrón de caminar");
        procesando1.setMessage("Registrando...");
        procesando1.setCancelable(true);
        procesando1.setMax(100);
        //Configuracion del progressBar 2
        procesando2 = new ProgressDialog(Registro.this);
        procesando2.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        procesando2.setTitle("Ya estamos por terminar");
        procesando2.setMessage("Procesando...");
        procesando2.setIndeterminate(true);
        procesando2.setCancelable(true);

        //Configuración de Filtros
        lpfSmoothing1 = new LowPassFilter();
        lpfSmoothing2 = new LowPassFilter();
        lpfSmoothing3 = new LowPassFilter();
        lpfGravity = new LowPassFilter();
        medianFilter = new MedianFilter();
        lpfLinear = new LinearAccelerationAveraging(lpfGravity);
        medianFilter.setTimeConstant(0.06f);
        lpfSmoothing1.setTimeConstant(0.06f);
        lpfSmoothing2.setTimeConstant(0.06f);
        lpfSmoothing3.setTimeConstant(0.05f);
        lpfLinear.setTimeConstant(0.11f);

        //Obtener el sensor y configuración
        sm = (SensorManager) getSystemService(SENSOR_SERVICE);
        s = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        //Manejador de acelerometro para detenerlo luego de 10 segundos
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                // acciones que se ejecutan tras los milisegundos
                sm.unregisterListener(Registro.this, s);

                TareaAsync2 tarea2 = new TareaAsync2();
                tarea2.execute();
            }
        }, 10000);

        TareaAsync1 tarea;
        tarea = new TareaAsync1();
        tarea.execute();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        contadorMuestras += 1;

        //Vector a filtrar con componentes x, y y z
        float[] acceleration;
        acceleration = event.values.clone();

        //Aplicacion de filtros
        lpfGravity.filter(acceleration);
        acceleration = lpfLinear.filter(acceleration);
        acceleration = lpfSmoothing1.filter(acceleration);
        acceleration = lpfSmoothing2.filter(acceleration);
        acceleration = medianFilter.filter(acceleration);

        sqlHelper.insertar(acceleration, event.timestamp, nombreTablaXYZ);
        if (contadorMuestras > 520) {
            sm.unregisterListener(Registro.this, s);
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public void CorreccionTiempo() {
        float vectorTiempo[];
        float t[] = sqlHelper.consultarVector(nombreTablaXYZ, "TIEMPO");
        vectorTiempo = new float[t.length];
        arraycopy(t, 0, vectorTiempo, 0, t.length);

        //Correción
        if (vectorTiempo[0] != 0f) {
            for (int i = 0; i < vectorTiempo.length; i++) {
                vectorTiempo[i] -= t[0];
            }
        }

        sqlHelper.actualizarPorId(vectorTiempo, "TIEMPO", 0, nombreTablaXYZ);
    }

    private void sleep() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
        }
    }

    public class TareaAsync1 extends AsyncTask<Void, Integer, Boolean> {
        @Override
        protected Boolean doInBackground(Void... params) {
            for (int i = 1; i <= 10; i++) {
                sleep();
                publishProgress(i * 10);
                if (isCancelled())
                    break;
            }
            return true;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            int progreso = values[0].intValue();
            procesando1.setProgress(progreso);
        }

        @Override
        protected void onPreExecute() {

            procesando1.setOnCancelListener(new OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    TareaAsync1.this.cancel(true);
                }
            });
            sm.registerListener(Registro.this, s, SensorManager.SENSOR_DELAY_GAME);
            procesando1.setProgress(0);
            procesando1.show();
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (result) {
                procesando1.dismiss();
            }
        }

        @Override
        protected void onCancelled() {
            Toast.makeText(Registro.this, "¡Cancelaste el proceso!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getApplicationContext(), Aviso.class);
            startActivity(intent);
        }


    }

    //Clase para Interpolación y filtrado
    public class TareaAsync2 extends AsyncTask<Void, Integer, Boolean> {
        //Valores sin interpolar
        float x[];
        float y[];
        float z[];
        float t[];
        float ti[];
        float xi[];
        float yi[];
        float zi[];

        @Override
        protected Boolean doInBackground(Void... params) {
            CorreccionTiempo();

            //Variables sin procesar
            t = sqlHelper.consultarVector(nombreTablaXYZ, "TIEMPO");
            x = sqlHelper.consultarVector(nombreTablaXYZ, "x");
            y = sqlHelper.consultarVector(nombreTablaXYZ, "y");
            z = sqlHelper.consultarVector(nombreTablaXYZ, "z");
            //Variables interpoladas
            ti = new float[t.length];
            xi = new float[x.length];
            yi = new float[y.length];
            zi = new float[z.length];
            //Se mantienen la primera y la ultima muestra
            ti[0] = t[0];
            xi[0] = x[0];
            yi[0] = y[0];
            zi[0] = z[0];
            ti[t.length - 1] = t[t.length - 1];
            xi[t.length - 1] = x[x.length - 1];
            yi[t.length - 1] = y[y.length - 1];
            zi[t.length - 1] = z[z.length - 1];

            //Duracion del evento
            double tEvent = t[t.length - 1];
            //Indice donde se interpolara
            int index = 1;

            float m;

            for (int i = 1; i < t.length - 1; i++) {
                ti[i] = (float) ((i * tEvent) / t.length);

                while (ti[i] > t[index]) {
                    index++;
                }

                //Interpolacion
                m = (ti[i] - t[index - 1]) / (t[index] - t[index - 1]);
                xi[i] = m * (x[index] - x[index - 1]) + x[index - 1];
                yi[i] = m * (y[index] - y[index - 1]) + y[index - 1];
                zi[i] = m * (z[index] - z[index - 1]) + z[index - 1];

            }

            sqlHelper.actualizarPorId(xi, "x", 0, nombreTablaXYZ);
            sqlHelper.actualizarPorId(yi, "y", 0, nombreTablaXYZ);
            sqlHelper.actualizarPorId(zi, "z", 0, nombreTablaXYZ);

            return true;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPreExecute() {

            procesando2.setOnCancelListener(new OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    TareaAsync2.this.cancel(true);
                }
            });
            procesando2.show();
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (result) {
                procesando2.dismiss();
                chart.mostrarChart(sqlHelper.consultarVector(nombreTablaXYZ,"x"),sqlHelper.consultarVector(nombreTablaXYZ,"y") , sqlHelper.consultarVector(nombreTablaXYZ,"z"));
                Toast.makeText(Registro.this, "¡Hemos terminado!", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected void onCancelled() {
            Toast.makeText(Registro.this, "¡Cancelaste el proceso!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getApplicationContext(), Aviso.class);
            startActivity(intent);
        }


    }

    public void home() {
        Button home = (Button) findViewById(R.id.btn_home);
        home.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });
    }

    public void borrar() {
        Button borrar = (Button) findViewById(R.id.btn_borrar);
        borrar.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                sqlHelper.eliminarTabla(nombreTablaXYZ);
                editor.putInt(nombre,  sp.getInt(sp.getString("usuarioActual", ""), -1) - 1);
                editor.commit();
                Toast.makeText(Registro.this, "¡Patrón borrado!", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}


