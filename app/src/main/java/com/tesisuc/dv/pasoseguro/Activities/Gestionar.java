package com.tesisuc.dv.pasoseguro.Activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.tesisuc.dv.pasoseguro.MainActivity;
import com.tesisuc.dv.pasoseguro.R;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class  Gestionar extends AppCompatActivity {


    private SharedPreferences sp;
    private SharedPreferences.Editor editor;
    private TextInputLayout textInputLayoutCorreo;
    private TextInputLayout textInputLayoutTelefono;
    private TextInputEditText inputCorreo;
    private TextInputEditText inputTelefono;
    String nombre;
    String correo;
    String telefono;
    String correoNuevo;
    String telefonoNuevo;
    Boolean correoFlag;
    Boolean telefonoFlag;
    int tipoUsuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sp = getSharedPreferences("Usuarios", Context.MODE_PRIVATE);
        editor = sp.edit();

        correoFlag = false;
        telefonoFlag = false;

        nombre = sp.getString("usuarioActual", "sin usuario registrado");
        if (!nombre.equals("sin usuario registrado")) {
            setContentView(R.layout.gestionar);
            Toolbar toolbar = (Toolbar) findViewById(R.id.gestionar_appbar);
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

            tipoUsuario = 1;

            if (sp.getInt("ContadorUsuarios", 0) == 1) {
                correo = sp.getString("correo1", "");
                telefono = sp.getString("telefono1", "");
            } else {
                if (nombre.equals(sp.getString("usuario1", ""))) {
                    correo = sp.getString("correo1", "");
                    telefono = sp.getString("telefono1", "");
                } else if (nombre.equals(sp.getString("usuario2", ""))) {
                    correo = sp.getString("correo2", "");
                    telefono = sp.getString("telefono2", "");
                    tipoUsuario = 2;
                }
            }

            textInputLayoutCorreo = (TextInputLayout) findViewById(R.id.til_gestionar_correo);
            textInputLayoutTelefono = (TextInputLayout) findViewById(R.id.til_gestionar_telefono);

            inputCorreo = (TextInputEditText) findViewById(R.id.gestionar_correo);
            inputTelefono = (TextInputEditText) findViewById(R.id.gestionar_telefono);

            textInputLayoutCorreo.setHint(correo);
            textInputLayoutTelefono.setHint(telefono);

            //Intents
            guardar();
            regresar();
            setInput();
        } else {
            AlertDialog.Builder dialogo = new AlertDialog.Builder(this);
            dialogo.setTitle("Disculpe pero...");
            dialogo.setIcon(R.drawable.alarm_light);
            dialogo.setMessage(R.string.alert_sinusuario);
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

    private void regresar() {
        Button regresar = (Button) findViewById(R.id.btn_gestionar_regresar);
        regresar.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);


            }
        });
    }

    private void guardar() {
        Button guardar = (Button) findViewById(R.id.btn_gestionar_continuar);
        guardar.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                correoNuevo = inputCorreo.getText().toString();
                telefonoNuevo = inputTelefono.getText().toString();

                if (correoFlag || telefonoFlag) {

                    if (correoFlag) {
                        if (validarCorreo()) {
                            if (tipoUsuario == 1) {
                                editor.putString("correo1", correoNuevo);
                            } else {
                                editor.putString("correo2", correoNuevo);
                            }
                            editor.commit();
                        }
                    }

                    if (telefonoFlag) {
                        if (validarTelefono()) {
                            if (tipoUsuario == 1) {
                                editor.putString("telefono1", telefonoNuevo);
                            } else {
                                editor.putString("telefono2", telefonoNuevo);
                            }
                            editor.commit();
                        }
                    }
                    Toast.makeText(getApplicationContext(), "¡Cambios realizados con exito!", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(getApplicationContext(), "No hay cambios por hacer.", Toast.LENGTH_LONG).show();
                }


            }
        });

    }

    private void setInput() {
        TextInputEditText inputCorreo = (TextInputEditText) findViewById(R.id.gestionar_correo);
        TextInputEditText inputTelefono = (TextInputEditText) findViewById(R.id.gestionar_telefono);

        inputCorreo.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    textInputLayoutCorreo.setHint("Correo");
                    correoFlag = true;
                }
            }
        });

        inputTelefono.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    textInputLayoutTelefono.setHint("Teléfono");
                    telefonoFlag = true;
                }
            }
        });
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

    public boolean validarCorreo() {
        inputCorreo.setError(null);

        if (correoNuevo.isEmpty()) {
            inputCorreo.setError("Se requiere un correo");
            return false;
        } else if (!correoValido()) {
            inputCorreo.setError("Introduzca un correo valido");
            return false;
        }

        return true;
    }

    public boolean validarTelefono() {
        inputTelefono.setError(null);

        if (telefonoNuevo.isEmpty()) {
            inputTelefono.setError("Se requiere un correo");
            return false;
        } else if (!telefonoValido()) {
            inputTelefono.setError("Introduzca un telefono valido");
            return false;
        }

        return true;
    }

    public boolean correoValido() {
        Pattern pattern = Patterns.EMAIL_ADDRESS;
        Matcher matcher = pattern.matcher(correoNuevo);
        return matcher.matches();
    }

    public boolean telefonoValido() {
        return telefonoNuevo.length() == 11;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}
