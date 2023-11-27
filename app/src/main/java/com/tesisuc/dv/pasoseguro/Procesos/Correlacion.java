package com.tesisuc.dv.pasoseguro.Procesos;

import org.apache.commons.math3.stat.correlation.*;

import java.util.ArrayList;

public class Correlacion {

    private PearsonsCorrelation cor;
    private SpearmansCorrelation spear;
    private KendallsCorrelation kend;

    public Correlacion(){
        cor = new PearsonsCorrelation();
        spear = new SpearmansCorrelation();
        kend = new KendallsCorrelation();

    }


    public double CoPearson(double[] x, double[] y){
        return cor.correlation(x, y);
    }

    public double CoSpearman(double[] x, double[] y){
        return spear.correlation(x, y);
    }

    public double CoKendall(double[] x, double[] y){
        return kend.correlation(x, y);
    }

}