package com.tesisuc.dv.pasoseguro.Activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import com.tesisuc.dv.pasoseguro.MainActivity;
import com.tesisuc.dv.pasoseguro.R;
import com.tesisuc.dv.pasoseguro.Procesos.PatronesAdapter;
import com.tesisuc.dv.pasoseguro.Procesos.SQLite;
import com.tesisuc.dv.pasoseguro.Procesos.Patron;

import java.util.ArrayList;

public class ListadoPatrones extends AppCompatActivity {

    private RecyclerView recyclerView;
    private PatronesAdapter adapter;
    private ArrayList<Patron> listaPatrones;
    private SharedPreferences sp;
    private SharedPreferences.Editor editor;
    private SQLite sqlHelper;
    private String usuarioActual;
    private String nombreBD;
    private int numeroTablas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.listado_patrones);
        Toolbar toolbar = (Toolbar) findViewById(R.id.patrones_appbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recyclerView = (RecyclerView) findViewById(R.id.patrones_rv);

        sp = getSharedPreferences("Usuarios", Context.MODE_PRIVATE);
        editor = sp.edit();

        usuarioActual = sp.getString("usuarioActual", "sin usuario registrado");
        nombreBD = usuarioActual.replaceAll("\\s+","");

        if(!usuarioActual.equals("sin usuario registrado")) {
            sqlHelper = new SQLite(this, nombreBD, null, 1);
            numeroTablas = sp.getInt(usuarioActual, 1);
        listaPatrones = new ArrayList<>();
        float x[];
        float y[];
        float z[];

        for (int i = 0; i < numeroTablas; i++) {
            String nombreTablaXYZ = nombreBD + "xyz" + i;
            x = sqlHelper.consultarVector(nombreTablaXYZ, "x");
            y = sqlHelper.consultarVector(nombreTablaXYZ, "y");
            z = sqlHelper.consultarVector(nombreTablaXYZ, "z");
            Patron p = new Patron("Patrón de caminar ", x, y, z);
            listaPatrones.add(p);
        }

        adapter = new PatronesAdapter(listaPatrones, this, usuarioActual, numeroTablas);

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 1);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
        }else{
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

    @Override
    public void onBackPressed() {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
