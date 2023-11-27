package com.tesisuc.dv.pasoseguro.Activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.github.mikephil.charting.utils.Utils;
import com.tesisuc.dv.pasoseguro.MainActivity;
import com.tesisuc.dv.pasoseguro.Procesos.Contador;
import com.tesisuc.dv.pasoseguro.R;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Aviso extends AppCompatActivity {
    private TextInputEditText inputNombre;
    private TextInputEditText inputCorreo;
    private TextInputEditText inputTelefono;
    private SharedPreferences sp;
    private SharedPreferences.Editor editor;
    String nombre;
    String correo;
    String telefono;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aviso);
        Toolbar toolbar = (Toolbar) findViewById(R.id.aviso_appbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        inputNombre = (TextInputEditText) findViewById(R.id.primer_aviso_nombre);
        inputCorreo = (TextInputEditText) findViewById(R.id.primer_aviso_correo);
        inputTelefono = (TextInputEditText) findViewById(R.id.primer_aviso_telefono);

        try {
            if (getIntent().getExtras().getString("usuario") != null) {
                inputNombre.setText(getIntent().getExtras().getString("usuario"));
                inputNombre.setEnabled(false);
                inputCorreo.setText(getIntent().getExtras().getString("correo"));
                inputCorreo.setEnabled(false);
                inputTelefono.setText(getIntent().getExtras().getString("telefono"));
                inputTelefono.setEnabled(false);
            }
        } catch (NullPointerException e) {

        }

        sp = getSharedPreferences("Usuarios", Context.MODE_PRIVATE);
        editor = sp.edit();

        txtChanged();
        //Intents
        continuar();
        regresar();
    }

    public void txtChanged() {
        inputNombre.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
            }

            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                if(s.toString().equals(sp.getString("usuario1", ""))){
                    inputCorreo.setText(sp.getString("correo1",""));
                    inputCorreo.setEnabled(false);
                    inputTelefono.setText(sp.getString("telefono1",""));
                    inputTelefono.setEnabled(false);
                }else if (s.toString().equals(sp.getString("usuario2", ""))){
                    inputCorreo.setText(sp.getString("correo2",""));
                    inputCorreo.setEnabled(false);
                    inputTelefono.setText(sp.getString("telefono2",""));
                    inputTelefono.setEnabled(false);
                }else {
                    inputCorreo.setText("");
                    inputCorreo.setEnabled(true);
                    inputTelefono.setText("");
                    inputTelefono.setEnabled(true);
                }
            }
        });

    }



    public void continuar() {
        Button registrar = (Button) findViewById(R.id.btn_continuar1);
        registrar.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                nombre = inputNombre.getText().toString();
                correo = inputCorreo.getText().toString();
                telefono = inputTelefono.getText().toString();
                if (validar()) {
                    AlertDialog.Builder dialogo = new AlertDialog.Builder(Aviso.this);
                    dialogo.setTitle("Para continuar...");
                    dialogo.setIcon(R.drawable.alarm_light);
                    dialogo.setMessage(R.string.txt_segundoAviso);
                    dialogo.setCancelable(false);
                    dialogo.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialogo, int id) {
                            sp = getSharedPreferences("Usuarios", Context.MODE_PRIVATE);
                            editor = sp.edit();

                            //Nueva persona para guardar patron
                            if (sp.getInt(nombre, -1) == -1) {
                                switch (sp.getInt("ContadorUsuarios", 0)) {
                                    case 0:
                                        editor.putInt("ContadorUsuarios", 1);
                                        editor.putString("usuario1", nombre);
                                        editor.putString("correo1", correo);
                                        editor.putString("telefono1", telefono);
                                        editor.putString("usuarioActual", nombre);
                                        editor.putInt(nombre, 0);
                                        break;
                                    case 1:
                                        editor.putInt("ContadorUsuarios", 2);
                                        editor.putString("usuario2", nombre);
                                        editor.putString("correo2", correo);
                                        editor.putString("telefono2", telefono);
                                        editor.putString("usuarioActual", nombre);
                                        editor.putInt(nombre, 0);
                                        break;
                                    default:
                                        break;
                                }
                                editor.commit();
                            }

                            //Verificación de que el usuario introducido ya existe
                            if ((nombre.equals(sp.getString("usuario1", ""))) || nombre.equals(sp.getString("usuario2", ""))) {
                                //Verificacion de la cantidad de tablas de ese usuario
                                if (sp.getInt(sp.getString("usuarioActual", ""), 0) < 10) {
                                    //Contador para dar inicio
                                    Contador cont = new Contador(5000, 1000);
                                    cont.setTipoContador(1);
                                    cont.configuraciones(getApplicationContext());
                                    editor.putString("usuarioAnterior", sp.getString("usuarioActual", "no hay"));
                                    editor.putString("usuarioActual", nombre);
                                    editor.commit();
                                    Handler handler = new Handler();
                                    cont.start();
                                    handler.postDelayed(new Runnable() {
                                        public void run() {
                                            // acciones que se ejecutan tras los milisegundos
                                            Intent intent = new Intent(getApplicationContext(), Registro.class);
                                            startActivity(intent);
                                        }
                                    }, 5000);
                                } else {
                                    //El usuario ya tiene suficientes listado_patrones de caminar
                                    //El usuario actual vuelve a ser el que estaba anteriormente
                                    editor.putString("usuarioActual", sp.getString("usuarioAnterior", ""));
                                    editor.commit();
                                    dialogo.cancel();
                                    AlertDialog.Builder dialogo_error = new AlertDialog.Builder(Aviso.this);
                                    dialogo_error.setTitle("¡Lo sentimos!");
                                    dialogo_error.setIcon(R.drawable.emoticon_sad_outline);
                                    dialogo_error.setMessage("Ya tiene suficientes patrones de caminar registrados");
                                    dialogo_error.setCancelable(false);
                                    dialogo_error.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialogo1, int id) {
                                            Intent intent = new Intent(getApplicationContext(), Aviso.class);
                                            startActivity(intent);
                                        }
                                    });
                                    dialogo_error.show();
                                }

                            } else {
                                dialogo.cancel();
                                AlertDialog.Builder dialogo_error = new AlertDialog.Builder(Aviso.this);
                                dialogo_error.setTitle("¡Lo sentimos!");
                                dialogo_error.setIcon(R.drawable.emoticon_sad_outline);
                                dialogo_error.setMessage("Ya tiene 2 usuarios registrados, elimine uno para poder continuar.");
                                dialogo_error.setCancelable(false);
                                dialogo_error.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialogo1, int id) {
                                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                        startActivity(intent);
                                    }
                                });
                                dialogo_error.show();
                            }
                        }
                    });
                    dialogo.setNegativeButton("Regresar", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialogo1, int id) {
                        }
                    });
                    dialogo.show();


                }
            }
        });
    }

    public void regresar() {
        Button registrar = (Button) findViewById(R.id.btn_regresar1);
        registrar.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        continuar();
        regresar();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.dinamic_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == android.R.id.home) {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    private boolean validar() {

        // Reset errors.
        inputNombre.setError(null);
        inputCorreo.setError(null);
        inputTelefono.setError(null);

        if (nombre.isEmpty()) {
            inputNombre.setError("Introduzca su nombre.");
            return false;
        }

        if (correo.isEmpty()) {
            inputCorreo.setError("Se requiere un correo.");
            return false;
        } else if (!correoValido()) {
            inputCorreo.setError("Introduzca un correo válido.");
            return false;
        }

        if (telefono.isEmpty()) {
            inputTelefono.setError("Se requiere un correo");
            return false;
        } else if (!telefonoValido()) {
            inputTelefono.setError("Introduzca un número celular válido, sin simbolos especiales");
            return false;
        }

        return true;
    }

    public boolean correoValido() {
        Pattern pattern = Patterns.EMAIL_ADDRESS;
        Matcher matcher = pattern.matcher(correo);
        return matcher.matches();
    }

    public boolean telefonoValido() {
        return telefono.length() == 11;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
