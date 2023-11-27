package com.tesisuc.dv.pasoseguro.Procesos;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;


public class SQLite extends SQLiteOpenHelper {

    //Sentencia para crear la tabla
    private String ID = "MUESTRA_ID";
    private String TIEMPO = "TIEMPO";
    private String X = "x", Y = "y", Z = "z";
    private SQLiteDatabase db;
    private Context context;
    private String databasePath;
    private String finalpath;
    private final String NOMBREBD;

    public SQLite(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        //Configuración para exportar base de datos
        NOMBREBD = name;
        finalpath = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS).getAbsolutePath();
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //Elimila la tabla si existe y la sustituye por una nueva
        db.execSQL("DROP TABLE IF EXISTS ACELEROMETRO");
        db.close();
    }

    public void crearTabla(String nombreTabla){
        //Sentencia que crea las tablas
        eliminarTabla(nombreTabla);
        if (!existeTabla(nombreTabla)) {
            db = this.getReadableDatabase();
            db.execSQL("CREATE TABLE " + nombreTabla +
                    " (" + ID + " INTEGER PRIMARY KEY, " +
                    TIEMPO + " REAL, " +
                    X + " REAL, " +
                    Y + " REAL, " +
                    Z + " REAL ) ");
        }

        db.close();
    }

    public void eliminarTabla(String nombre){
        db = this.getReadableDatabase();
        db.execSQL("DROP TABLE IF EXISTS " + nombre);
        db.close();
    }

    public void renombrarTabla(String oldname, String newname){
        db = this.getReadableDatabase();
        db.execSQL("ALTER TABLE " + oldname + " RENAME TO " + newname);
        db.close();

    }

    public void insertar(float[] valores, float t, String nombre) {
        db = this.getReadableDatabase();
        ContentValues contenedor = new ContentValues();
        contenedor.put(TIEMPO, t);
        contenedor.put(X, valores[0]);
        contenedor.put(Y, valores[1]);
        contenedor.put(Z, valores[2]);
        
        //Insertar los valores en la tabla especifica
        db.insert(nombre, null, contenedor);
        db.close();
    }

    public void actualizarPorId(float[] valores, String campo, int param, String nombre) {
        db = this.getReadableDatabase();
        ContentValues contenedor = new ContentValues();

        if (param == 0) {
            for (int i = 0; i < valores.length; i++) {
                contenedor.put(campo, valores[i]);
                db.update(nombre, contenedor, "MUESTRA_ID = " + (i + 1), null);
            }
        } else {
            contenedor.put(campo, valores[0]);
            db.update(nombre, contenedor, "MUESTRA_ID = " + param, null);
        }

        db.close();
    }


    public boolean existeTabla(String nombre) {
        boolean existe = false;
        db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT DISTINCT tbl_name FROM sqlite_master WHERE tbl_name = '" + nombre + "'", null);

        if (cursor != null) {
            if (cursor.getCount() > 0) {
                existe = true;
            }
            cursor.close();
        }

        db.close();
        return existe;
    }

    public Cursor Consultar(String nombre) {
        db = this.getReadableDatabase();

        Cursor c = db.rawQuery("SELECT * FROM " + nombre, null);
        db.close();

        return c;
    }

    public float[] consultarVector(String nombre, String columna) {
        float x[];

        db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT " + columna + " FROM " + nombre, null);
        int index = c.getColumnIndex(columna);
        x = new float[c.getCount()];

        if (c.moveToFirst()) {
            int i = 0;
            do {
                x[i] = c.getFloat(index);
                i++;
            } while (c.moveToNext());
        }
        db.close();
        return x;
    }

    public boolean exportar(int accion, String nombre){
        boolean resultado = false;
        //Ubicacion de la BD
        databasePath = context.getDatabasePath(nombre).getPath();
        File sourceLocation = new File (databasePath);
        //Ubicacion donde se ubicara el archivo de BD
        File targetLocation = new File(finalpath + "/" + nombre + "_BACKUP");

        //Para ver estas direcciones...
        Log.v("TAG", "sourceLocation: " + sourceLocation);
        Log.v("TAG", "targetLocation: " + targetLocation);

        try {
            // 1 =mover el archivo, 2 = copiar el archivo

            //Mover
            if(accion==1){

                if(sourceLocation.renameTo(targetLocation)){
                    Log.v("TAG", "Move file successful.");
                }else{
                    Log.v("TAG", "Move file failed.");
                }
            }

            //Copiar
            else{
                //Verficar que existe la ubicación
                if(sourceLocation.exists()){
                    InputStream in = new FileInputStream(sourceLocation);
                    OutputStream out = new FileOutputStream(targetLocation);

                    byte[] buf = new byte[1024];
                    int len;

                    while ((len = in.read(buf)) > 0) {
                        out.write(buf, 0, len);
                    }

                    in.close();
                    out.close();

                    Log.v("TAG", "Archivo copiado exitosamente");
                    resultado = !resultado;

                }else{
                    Log.v("TAG", "Error, fallo en la ubicación");
                }

            }

        } catch (NullPointerException e) {
            e.printStackTrace();
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    return resultado;
    }

    public String getFinalpath() {
        return finalpath;
    }

}







