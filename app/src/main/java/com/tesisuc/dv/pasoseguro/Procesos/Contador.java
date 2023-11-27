package com.tesisuc.dv.pasoseguro.Procesos;

import android.content.Context;
import android.content.Intent;
import android.os.CountDownTimer;
import android.widget.Toast;

/**
 * Created by Daniel H on 09/09/2018.
 */

public class Contador extends CountDownTimer {
    private Context context;
    private int tipoContador;
    Intent intent;
    private Toast m1;
    private Toast m2;
    private boolean finalizo;

    public Contador(long milisfuturo, long intervalo) {
        super(milisfuturo, intervalo);
    }

    @Override
    public void onTick(long millisUntilFinished) {
        if (tipoContador == 1) {
            if (millisUntilFinished < 4800 && millisUntilFinished >= 3000) {
                m1.show();
            }
        }
    }

    @Override
    public void onFinish() {
        finalizo = true;
    }

    public void configuraciones(Context context) {
        this.context = context;
        m1 = Toast.makeText(context.getApplicationContext(), "Recuerde guardar el dispositivo en el bolsillo. Ya estamos por iniciar...", Toast.LENGTH_LONG);
     }

    public void Setcontext(Context context) {
        this.context = context;
    }

    public void setTipoContador(int tipoContador) {
        this.tipoContador = tipoContador;
    }

    public Toast getM1() {
        return m1;
    }

    public void setM1(Toast m1) {
        this.m1 = m1;
    }

    public Toast getM2() {
        return m2;
    }

    public void setM2(Toast m2) {
        this.m2 = m2;
    }

    public boolean isFinalizo() {
        return finalizo;
    }
}



