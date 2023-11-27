package com.tesisuc.dv.pasoseguro;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.tesisuc.dv.pasoseguro.Activities.Configuracion;
import com.tesisuc.dv.pasoseguro.Activities.ListadoPatrones;
import com.tesisuc.dv.pasoseguro.Activities.Usuarios;
import com.tesisuc.dv.pasoseguro.Activities.Gestionar;
import com.tesisuc.dv.pasoseguro.Activities.Aviso;
import com.tesisuc.dv.pasoseguro.Activities.VerCiclos;
import com.tesisuc.dv.pasoseguro.Graficar.CharPorcentaje;
import com.tesisuc.dv.pasoseguro.Procesos.SQLite;

import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.tesisuc.dv.pasoseguro.Services.AutenticacionExperimental;
import com.tesisuc.dv.pasoseguro.Services.Respuesta;

import java.util.ArrayList;

import android.widget.Toast;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private SharedPreferences sp;
    private SharedPreferences.Editor editor;
    private TextView txt_navigationView;
    private SwitchCompat sw;
    private SQLite sqlHelper;
    private Intent autenticar;
    private TextView txt;
    private String print = "";
    //Contadores de autenticacion
    private int cont1, cont2, cont3, cont4;
    private ArrayList<Integer> vectorResultados;
    //para las graficas de porcentajes
    private float porcentaje;
    private boolean seguro;
    private CharPorcentaje charPorcentaje;
    //Variables globales para los settings
    private String nivelSeguridad;
    private String tiempoEspera;
    private String metodo;
    //Resutaldocongifguracion
    private Handler handler;
    private Runnable runable;
    protected static final int REQUEST_CODE = 10;

    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        txt = (TextView) findViewById(R.id.main_txt);

        setSupportActionBar(toolbar);
        sw = (SwitchCompat) findViewById(R.id.main_switch);

        sp = getSharedPreferences("Usuarios", Context.MODE_PRIVATE);
        editor = sp.edit();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Aviso.class);
                startActivity(intent);
            }
        });

        context = this;

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        txt_navigationView = (TextView) navigationView.getHeaderView(0).findViewById(R.id.txt2_header);
        String usuarioActual = sp.getString("usuarioActual", "sin usuario registrado");
        String usuario1 = sp.getString("usuario1", " ");
        String usuario2 = sp.getString("usuario2", " ");
        cont1 = 0;
        cont2 = 0;
        cont3 = 0;
        cont4 = 0;
        seguro = false;
        nivelSeguridad = "1";
        tiempoEspera = "3";
        metodo = "0";
        checkSMSStatePermission();


        String[] datos = new String[2];

        switch (sp.getInt("ContadorUsuarios", 0)) {
            case 0:
            case 1:
                datos = new String[1];
                break;
            case 2:
                datos = new String[2];
                if (usuarioActual.equals(usuario1)) {
                    datos[1] = usuario2;
                } else if (usuarioActual.equals(usuario2)) {
                    datos[1] = usuario1;
                }
                break;
        }
        datos[0] = usuarioActual;

        final ArrayAdapter<String> adaptador =
                new ArrayAdapter<String>(this,
                        R.layout.spinner, datos);

        adaptador.setDropDownViewResource(
                R.layout.spinner_dropdown_textviews);
        Spinner spinnerUsuarios = (Spinner) navigationView.getHeaderView(0).findViewById(R.id.nav_header_spinner);
        spinnerUsuarios.setAdapter(adaptador);
        spinnerUsuarios.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapter, View vies,
                                       int position, long id) {
                editor.putString("usuarioAnterior", sp.getString("usuarioActual", ""));
                editor.putString("usuarioActual", adapter.getSelectedItem().toString());
                editor.commit();
                txt_navigationView.setText("Patrones de caminar registrados: " + sp.getInt(sp.getString("usuarioActual", ""), 0));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapter) {

            }
        });


        switchOnOff();
        broadcastReceiver();

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE) {
            sw.setChecked(false);
            Toast.makeText(this, "Se detuvo la autenticación. Puede reiniciarla", Toast.LENGTH_LONG).show();
        }
    }


    private void broadcastReceiver() {
        BroadcastReceiver br = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                Bundle bundle = intent.getExtras();
                if (bundle != null) {
                    //    nivelSeguridad = bundle.getString("Seguridad");
                    //   tiempoEspera = bundle.getString("Tiempo");
                    //   metodo = bundle.getString("Algoritmo");
                    //   System.out.println("MMMMMMMMMMMMMMMMMMMM////////////////////MMMMMMMMMMMMMMMMMMMMM");
                    //  System.out.println(nivelSeguridad + " "+ tiempoEspera + " " + metodo);
                    ArrayList<Integer> resultados = bundle.getIntegerArrayList("resultado");
                    for (int i = 0; i < resultados.size(); i++) {
                        switch (resultados.get(i)) {
                            case 1:
                                cont1 += 1;
                                vectorResultados.add(0);
                                break;
                            case 2:
                                cont2 += 1;
                                vectorResultados.add(0);
                                break;
                            case 3:
                                cont3 += 1;
                                vectorResultados.add(1);
                                break;
                            default:
                                cont4 += 1;
                                vectorResultados.add(0);
                                break;
                        }
                        if (vectorResultados.size() >= 100) {
                            vectorResultados.remove(0);
                        }

                    }
                    print = "Señales incorrectas: " + cont4 +
                            " \nSolo correlación temporal: " + cont1 +
                            " \nSolo correlacion temporal y espectral: " + cont2 +
                            " \nVerificaciones correctas: " + cont3;
                    if (seguro) {
                        txt.setText(print);
                        DibujarValores(charPorcentaje, vectorResultados);
                    }

                    if (nivelSeguridad == "0") {
                        if (porcentaje >= 25) {
                            Detener();
                            ComenzarConteo(tiempoEspera);
                        }
                    } else if (nivelSeguridad == "1") {
                        if (porcentaje >= 35) {
                            Detener();
                            ComenzarConteo(tiempoEspera);
                        }

                    } else {
                        if (porcentaje >= 45) {
                            Detener();
                            ComenzarConteo(tiempoEspera);
                        }

                    }


                }
            }
        };
        IntentFilter filter = new IntentFilter();

        LocalBroadcastManager.getInstance(this).registerReceiver(br, new IntentFilter("1"));
    }

    private void switchOnOff() {
        sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton cb, boolean isChecked) {

                if (isChecked) {
                    checkSMSStatePermission();
                    //Establecer referencia y Autenticacion
                    autenticar = new Intent(MainActivity.this, AutenticacionExperimental.class);
                    if (sp.getInt(sp.getString("usuarioActual", ""), 0) >= 5) {
                        startService(autenticar);
                        Log.v("Activity", "inició servicio");
                        seguro = true;
                        txt.setText("Estableciendo referencia... \n Espere un momento");
                        //Dibujo de porcentaje
                        RelativeLayout chartLayout = (RelativeLayout) findViewById(R.id.main_cardview);
                        charPorcentaje = new CharPorcentaje(chartLayout, getApplicationContext(), "Porcentaje de autenticación");
                        porcentaje = 0;
                        vectorResultados = new ArrayList<Integer>();
                        //Manejo del tiempo de reaccion
                        handler = new Handler();
                        runable = new Runnable() {
                            public void run() {
                                // acciones que se ejecutan tras los milisegundos
                                stopService(autenticar);
                                RespuestaSeguridad();
                                Detener();
                                sw.setChecked(false);

                            }
                        };
                        tiempoEspera = sp.getString("tiempo_preference", "3");
                        nivelSeguridad = sp.getString("seguridad_preference", "1");
                        ComenzarConteo(tiempoEspera);
                    } else {
                        sw.setChecked(false);
                        Toast.makeText(context, "Aun no tiene suficiente patrones de caminar almacenados...", Toast.LENGTH_LONG).show();
                    }
                } else {
                    stopService(autenticar);
                    txt.setText("Reconocimiento apagado");
                    cont1 = 0;
                    cont2 = 0;
                    cont3 = 0;
                    cont4 = 0;
                    seguro = false;
                    Log.v("Activity", "servicio detenido");
                    Detener();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(getApplicationContext(), Configuracion.class);
            startActivityForResult(intent, REQUEST_CODE);
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_usus) {
            Intent intent = new Intent(getApplicationContext(), Usuarios.class);
            startActivity(intent);

        } else if (id == R.id.nav_patrones) {
            Intent intent = new Intent(getApplicationContext(), ListadoPatrones.class);
            startActivity(intent);
        } else if (id == R.id.nav_gestionar) {
            Intent intent = new Intent(getApplicationContext(), Gestionar.class);
            startActivity(intent);
        } else if (id == R.id.nav_ciclos) {
            Intent intent = new Intent(getApplicationContext(), VerCiclos.class);
            startActivity(intent);
        } else if (id == R.id.nav_exportar) {
            sp = getSharedPreferences("Usuarios", Context.MODE_PRIVATE);
            editor = sp.edit();
            String nombreBD = sp.getString("usuarioActual", "").replaceAll("\\s+", "");
            ;
            sqlHelper = new SQLite(MainActivity.this, nombreBD, null, 1);


            if (sqlHelper.exportar(2, nombreBD)) {
                AlertDialog.Builder dialogo = new AlertDialog.Builder(this);
                dialogo.setTitle("Exportar base de datos");
                dialogo.setIcon(R.drawable.alarm_light);
                dialogo.setMessage("Exportación exitosa.");
                dialogo.setCancelable(false);
                dialogo.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogo, int id) {
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent);
                    }

                });
                dialogo.show();
            } else {
                AlertDialog.Builder dialogo = new AlertDialog.Builder(this);
                dialogo.setTitle("Exportar base de datos");
                dialogo.setIcon(R.drawable.emoticon_sad_outline);
                dialogo.setMessage("No se pudo exportar.");
                dialogo.setCancelable(false);
                dialogo.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogo, int id) {
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent);
                    }

                });
                dialogo.show();
            }
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    private void DibujarValores(CharPorcentaje chart, ArrayList<Integer> valores) {
        if (valores.size() > 0) {
            int c1 = 0;
            float porc2;
            for (int i = 0; i < valores.size(); i++) {
                c1 += valores.get(i);
            }
            porcentaje = ((int) (c1 * 1000 / valores.size())) / 10;
            porc2 = 100 - porcentaje;
            ArrayList<PieEntry> yvalues = new ArrayList<PieEntry>();
            yvalues.add(new PieEntry(porcentaje, "Correctos"));
            yvalues.add(new PieEntry(porc2, "Descartados"));
            PieDataSet dataSet = new PieDataSet(yvalues, "Porcentajes");
            dataSet.setSliceSpace(3f);
            dataSet.setColors(ColorTemplate.LIBERTY_COLORS);
            PieData data = new PieData(dataSet);
            data.setValueFormatter(new PercentFormatter());
            data.setValueTextSize(16f);
            data.setValueTextColor(Color.BLACK);
            data.setValueTypeface(Typeface.DEFAULT_BOLD);
            chart.getmChart().setData(data);
            // undo all highlights
            chart.getmChart().highlightValues(null);
            chart.getmChart().invalidate();
        }


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void ComenzarConteo(String tiempo) {
        int x = 4;
        switch (tiempo) {
            case "0":
                x = 1;
                break;
            case "1":
                x = 2;
                break;
            case "2":
                x = 3;
                break;
            case "4":
                x = 5;
                break;
            case "5":
                x = 6;
                break;
            case "6":
                x = 12;
                break;
            case "7":
                x = 18;
                break;
            case "8":
                x = 24;
                break;
            default:
                x = 4;
                break;
        }

        handler.postDelayed(runable, x * 3600);

    }

    public void Detener() {
        handler.removeCallbacks(runable);

    }

    public void RespuestaSeguridad() {
        Intent intent = new Intent(this, Respuesta.class);
        startService(intent);

    }

    private void checkSMSStatePermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,}, 1000);
        }

        int permissionCheck = ContextCompat.checkSelfPermission(
                this, Manifest.permission.SEND_SMS);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            Log.i("Mensaje", "No se tiene permiso para enviar SMS.");
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, 225);
        } else {
            Log.i("Mensaje", "Se tiene permiso para enviar SMS!");
        }
    }

}
