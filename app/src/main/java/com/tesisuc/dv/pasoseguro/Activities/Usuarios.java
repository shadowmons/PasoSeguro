package com.tesisuc.dv.pasoseguro.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.design.button.MaterialButton;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import com.tesisuc.dv.pasoseguro.MainActivity;
import com.tesisuc.dv.pasoseguro.R;

public class Usuarios extends AppCompatActivity {

    private SharedPreferences sp;
    private SharedPreferences.Editor editor;
    private TextView txt_usu1;
    private TextView txt_usu2;
    private TextView txt_avisoUsu1;
    private TextView txt_avisoUsu2;
    private MaterialButton btn_usu1;
    private MaterialButton btn_usu2;
    private Menu menu;
    private MenuItem deleteItem;
    private boolean onLongClick;
    private int btn_flag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.usuarios);
        Toolbar toolbar = (Toolbar) findViewById(R.id.usuarios_appbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        onLongClick = false;
        sp = getSharedPreferences("Usuarios", Context.MODE_PRIVATE);
        editor = sp.edit();
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        txt_usu1 = (TextView) findViewById(R.id.txt_usu1);
        txt_usu2 = (TextView) findViewById(R.id.txt_usu2);
        btn_usu1 = (MaterialButton) findViewById(R.id.btn_usu1);
        btn_usu2 = (MaterialButton) findViewById(R.id.btn_usu2);
        TextInputLayout layout1 = (TextInputLayout)  findViewById(R.id.til_usu1);
        TextInputLayout layout2 = (TextInputLayout)  findViewById(R.id.til_usu2);

        switch (sp.getInt("ContadorUsuarios", 0)) {
            case 1:
                txt_usu1.setText(sp.getString("usuario1", ""));
                //layout1.setLayoutParams();
                btn_usu1.setIconResource(R.drawable.account_circle_outline);
                break;
            case 2:
                txt_usu1.setText(sp.getString("usuario1", ""));
                txt_usu1.setWidth(Toolbar.LayoutParams.WRAP_CONTENT);
                btn_usu1.setIconResource(R.drawable.account_circle_outline);
                txt_usu2.setText(sp.getString("usuario2", ""));
                txt_usu2.setWidth(Toolbar.LayoutParams.WRAP_CONTENT);
                btn_usu2.setIconResource(R.drawable.account_circle_outline);
                break;
        }

        txt_avisoUsu1 = (TextView) findViewById(R.id.txt_avisoUsuario1);
        txt_avisoUsu2 = (TextView) findViewById(R.id.txt_avisoUsuario2);
        if(sp.getInt("ContadorUsuarios", 0) == 0){
            txt_avisoUsu1.setText("No hay usuarios registrados.");
        }else if(sp.getInt("ContadorUsuarios", 0) == 1){
            txt_avisoUsu1.setText(sp.getString("usuario1", "") + " tiene " + sp.getInt(sp.getString("usuario1",""),0) + " patrones de caminar registrados.");
        }else{
            txt_avisoUsu1.setText(sp.getString("usuario1", "") + " tiene " + sp.getInt(sp.getString("usuario1",""),0) + " patrones de caminar registrados.");
            txt_avisoUsu2.setText(sp.getString("usuario2", "") + " tiene " + sp.getInt(sp.getString("usuario2",""),0) + " patrones de caminar registrados.");
        }

        RegistrarPatron();
        if (sp.getInt("ContadorUsuarios", 0) >= 1) {
            LongClick();
        }

    }

    private void LongClick() {
        btn_usu1.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                //Agrego Item de papelera para eliminar usuario 1
                deleteItem = menu.add(0, 0, 0, "Borrar");
                deleteItem.setIcon(R.drawable.delete);
                deleteItem.getIcon().setTint(getResources().getColor(R.color.colorPrimaryDark));
                deleteItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);

                btn_usu1.setFocusableInTouchMode(true);
                btn_flag = 1;

                onLongClick = true;
                return true;
            }
        });

        btn_usu2.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                //Agrego Item de papelera para eliminar usuario 2
                deleteItem = menu.add(0, 0, 0, "Borrar");
                deleteItem.setIcon(R.drawable.delete);
                deleteItem.getIcon().setTint(getResources().getColor(R.color.colorPrimaryDark));
                deleteItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);

                btn_usu2.setFocusableInTouchMode(true);
                btn_flag = 2;

                onLongClick = true;
                return true;
            }
        });
    }

    private void RegistrarPatron() {
        //Oyente de boton de usuario 1
        btn_usu1.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Intent intent = new Intent(getApplicationContext(), Aviso.class);
                                            if (sp.getString("usuario1", "").equals("")) {
                                                startActivity(intent);
                                            } else {
                                                intent.putExtra("usuario", sp.getString("usuario1", ""));
                                                intent.putExtra("correo", sp.getString("correo1",""));
                                                Log.v("correo",sp.getString("correo1",""));
                                                intent.putExtra("telefono", sp.getString("telefono1",""));
                                                startActivity(intent);
                                            }

                                        }
                                    }
        );

        //Oyente de boton de usuario 2
        btn_usu2.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Intent intent = new Intent(getApplicationContext(), Aviso.class);
                                            if (sp.getString("usuario2", "").equals("")) {
                                                startActivity(intent);
                                            } else {
                                                intent.putExtra("usuario", sp.getString("usuario2", ""));
                                                intent.putExtra("correo", sp.getString("correo2",""));
                                                intent.putExtra("telefono", sp.getString("telefono2",""));
                                                startActivity(intent);
                                            }

                                        }
                                    }
        );
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.dinamic_menu, menu);
        this.menu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //Borrar usuario que genero el click
        if (id == 0) {
            String nombreBD ="";
            if (sp.getInt("ContadorUsuarios", 0) == 1) {
                //Remueve unico usuario existente (usuario 1)
                nombreBD = sp.getString("usuario1","").replaceAll("\\s+","");
                editor.remove(sp.getString("usuario1",""));
                editor.remove("usuario1");
                editor.remove("correo1");
                editor.remove("telefono1");
                editor.putInt("ContadorUsuarios", (sp.getInt("ContadorUsuarios", 0) - 1));
                editor.remove("usuarioActual");
                btn_usu1.setFocusableInTouchMode(false);
                btn_usu1.setFocusable(false);
            } else if (sp.getInt("ContadorUsuarios", 0) == 2) {
                if (btn_flag == 1) {
                    //Remueve usuario 1, el usuario 2 para a ser usuario 1
                    nombreBD = sp.getString("usuario1","").replaceAll("\\s+","");
                    editor.putString("usuario1", sp.getString("usuario2", ""));
                    editor.putString("usuarioActual", sp.getString("usuario2", ""));
                    editor.putString("correo1",sp.getString("correo2",""));
                    editor.putString("telefono1",sp.getString("telefono2",""));
                    editor.putInt("ContadorUsuarios", (sp.getInt("ContadorUsuarios", 0) - 1));
                    editor.remove(sp.getString("usuario1",""));
                    btn_usu1.setFocusableInTouchMode(false);
                    btn_usu1.setFocusable(false);
                } else if (btn_flag == 2) {
                    //Remueve usuario 2
                    nombreBD = sp.getString("usuario2","").replaceAll("\\s+","");
                    editor.putString("usuarioActual", sp.getString("usuario1", ""));
                    editor.putInt("ContadorUsuarios", (sp.getInt("ContadorUsuarios", 0) - 1));
                    editor.remove(sp.getString("usuario2",""));
                    btn_usu2.setFocusableInTouchMode(false);
                    btn_usu2.setFocusable(false);
                }
                editor.remove("usuario2");
                editor.remove("correo2");
                editor.remove("telefono2");
            }
            this.deleteDatabase(nombreBD);
            editor.commit();
            menu.removeItem(0);
            Intent intent = new Intent(getApplicationContext(), Usuarios.class);
            startActivity(intent);
            return true;
        } else if (id == android.R.id.home) {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (onLongClick) {
            menu.removeItem(0);
            btn_usu1.setFocusableInTouchMode(false);
            btn_usu1.setFocusable(false);
            btn_usu2.setFocusableInTouchMode(false);
            btn_usu2.setFocusable(false);
            onLongClick = !onLongClick;
        } else {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}
