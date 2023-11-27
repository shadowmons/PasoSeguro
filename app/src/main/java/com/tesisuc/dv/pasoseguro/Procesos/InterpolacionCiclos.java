package com.tesisuc.dv.pasoseguro.Procesos;

import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.analysis.interpolation.SplineInterpolator;
import org.apache.commons.math3.analysis.interpolation.UnivariateInterpolator;
import org.apache.commons.math3.stat.StatUtils;

import java.util.ArrayList;

public class InterpolacionCiclos {

    public ArrayList<Double> cicloPred;
    public double[] ciclopre;
    private int n;
    private double[] x,y,xc,yc;
    private float aux=0;
    private int iaux = 0;
    private int pren = 0;
    UnivariateInterpolator interpolador = new SplineInterpolator();


    public InterpolacionCiclos(ArrayList<Double> H, int nt) {
        cicloPred = new ArrayList<Double>(H);
        this.n = nt;

    }
    public InterpolacionCiclos( int nt) {
        this.n = nt;

    }

    public void nuevoCicloInterpolado(ArrayList<Double> H){
        pren = cicloPred.size();
        x = new double[pren];
        y = new double[pren];

        for (int i = 0; i < pren; i++) {
            x[i]= i * 5;
            y[i]=cicloPred.get(i);
        }
        UnivariateFunction polinomio = interpolador.interpolate(x, y);
        int top = (int) Math.abs ((StatUtils.max(x)-StatUtils.min(x)));
        double[] xc = new double [top+1];
        double[] yc = new double [top+1];
        for (int i = 0; i < top+1; i++) {
            xc[i]= i;
            yc[i]=polinomio.value(xc[i]);
        }
        H.clear();
        aux = (float)(pren-1)/(n-1);
        // System.out.println(n + "   ,   " + pren + "   ,  " + yc.length + "   /" + aux);
        for (int i = 0; i < n; i++) {
            H.add((Double) yc[Math.round(i * 5 * aux)]);

        }

    }

    public double[] InterCicloAuten(double[] H, int n){
        this.n = n;
        ciclopre= H;
        pren = ciclopre.length;
        x = new double[pren];
        y = new double[pren];

        for (int i = 0; i < pren; i++) {
            x[i]= i * 10;
            y[i]=ciclopre[i];
        }
        UnivariateFunction polinomio = interpolador.interpolate(x, y);
        int top = (int) Math.abs ((StatUtils.max(x)-StatUtils.min(x)));
        double[] xc = new double [top+1];
        double[] yc = new double [top+1];
        for (int i = 0; i < top+1; i++) {
            xc[i]= i;
            yc[i]=polinomio.value(xc[i]);
        }
        double [] R = new double[n];
        aux = (float)(pren-1)/(n-1);
        // System.out.println(n + "   ,   " + pren + "   ,  " + yc.length + "   /" + aux);
        for (int i = 0; i < n; i++) {
            R[i]=(Double) yc[Math.round(i * 10 * aux)];
        }
        return R;
    }


}
