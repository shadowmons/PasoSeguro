package com.tesisuc.dv.pasoseguro.Procesos;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

/**
 *
 * @author Victor Gil
 */
public class ParametrosTiempo {

    private double[] vector;


    public ParametrosTiempo(double[] x) {
        vector = new double[x.length];
        for (int i = 0; i < x.length; i++) {
            vector[i] = x[i];

        }

    }

    public ParametrosTiempo(float[] x) {
        vector = new double[x.length];
        for (int i = 0; i < x.length; i++) {
            vector[i] = x[i];

        }
    }

    public static double[] parametrosTemporalesZ1(double[] vector) {
        DescriptiveStatistics stats = new DescriptiveStatistics();
        double[] ptz = new double[10];

        for (int i = 0; i < vector.length; i++) {
            stats.addValue(vector[i]);
        }
        //RMS
        ptz[0]= Math.sqrt(stats.getSumsq()/vector.length);

        //Energia
        ptz[1] = stats.getSumsq();

        //Longitud de Forma de Onda WL
        double wl =0;
        for (int i = 0; i < vector.length-1; i++) {
            wl = wl + Math.abs(vector[i+1]- vector[i]);
        }
        ptz[2] = wl;

        //Desviacion estandar
        ptz[3]= Math.sqrt(stats.getVariance());

        //Media
        ptz[4]=stats.getMean();

        //Momentos
        ptz[5] = stats.getSkewness();
        ptz[6] = stats.getKurtosis();
        //acceleracion max
        ptz[7] = stats.getMax();

        //acceleracion min
        ptz[8] = stats.getMin();

        //Pico a pico
        ptz[9] = Math.abs((ptz[7] - ptz[8]));
        return ptz;


    }



    public static double[] parametrosTemporalesM1(double[] vector, int longitud) {
        DescriptiveStatistics stats = new DescriptiveStatistics();
        double[] ptz = new double[9];

        for (int i = 0; i < vector.length; i++) {
            stats.addValue(vector[i]);

        }
        //RMS
        ptz[0]= Math.sqrt(stats.getSumsq()/vector.length);

        //Energia
        ptz[1] = stats.getSumsq();

        //Longitud de Forma de Onda WL
        double wl =0;
        for (int i = 0; i < vector.length-1; i++) {
            wl = wl + Math.abs(vector[i+1]- vector[i]);
        }
        ptz[2] = wl;

        //Desviacion estandar
        ptz[3]= Math.sqrt(stats.getVariance());

        //Media
        ptz[4]=stats.getMean();

        //Momentos
        ptz[5] = stats.getSkewness();
        ptz[6] = stats.getKurtosis();
        //acceleracion max
        ptz[7] = stats.getMax();
        //longitud
        ptz[8] = longitud;
        return ptz;

    }

    public static double[] parametrosTemporalesZ2(double[] vector) {
        DescriptiveStatistics stats = new DescriptiveStatistics();
        double[] ptz = new double[9];

        for (int i = 0; i < vector.length; i++) {
            stats.addValue(vector[i]);
        }
        //RMS
        ptz[0]= Math.sqrt(stats.getSumsq()/vector.length);

        //Desviacion estandar
        ptz[1]= Math.sqrt(stats.getVariance());

        //Media
        ptz[2]=stats.getMean();

        //acceleracion max
        ptz[3] = stats.getMax();

        //acceleracion min
        ptz[4] = stats.getMin();

        //Pico a pico
        ptz[5] = Math.abs((ptz[3] - ptz[4]));

        //Energia
        ptz[6] = stats.getSumsq();

        //Momentos
        ptz[7] = stats.getSkewness();
        ptz[8] = stats.getKurtosis();
        return ptz;
    }

}