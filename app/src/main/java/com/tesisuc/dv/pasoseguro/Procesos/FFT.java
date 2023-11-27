package com.tesisuc.dv.pasoseguro.Procesos;

import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.transform.DftNormalization;
import org.apache.commons.math3.transform.FastFourierTransformer;
import org.apache.commons.math3.transform.TransformType;

/**
 *
 * @author DEHernandez
 */
public class FFT {

    private double xs[];
    private TransformType t;
    int n;

    public FFT(double[] xs, int n, int i) {
        this.xs = xs;
        this.n = n;
        if (i == 1) {
            t = TransformType.FORWARD;
        } else {
            t = TransformType.INVERSE;
        }
    }

    public Complex[] Transformada(int length) {
        Complex zpromComplex[] = new Complex[n];
        for (int i = 0; i < length; i++) {
            zpromComplex[i] = new Complex(xs[i]);
        }
        //zero padding
        for (int i = length; i < n; i++) {
            zpromComplex[i] = new Complex(0);
        }

        return new FastFourierTransformer(DftNormalization.STANDARD).transform(zpromComplex, t);
    }

    public double[] FFTShift(double[] fft_original){
        //Desplazamiento de la FFT
        double fft_desplazada[] = new double[307];
//            for (int i = n/2; i < (n-1); i++) {
//                //Frecuencias negativas
//                fft_desplazada[i] = fft_original[i];
//            }
        for (int i = 0; i < 307; i++) {
            //Frecuencias positiva
            fft_desplazada[i] = fft_original[i];
        }

        return fft_desplazada;
    }

    public double[] FFTShiftA(double[] fft_original){
        //Desplazamiento de la FFT
        double fft_desplazada[] = new double[n/4];
//            for (int i = n/2; i < (n-1); i++) {
//                //Frecuencias negativas
//                fft_desplazada[i] = fft_original[i];
//            }
        for (int i = 0; i < n/4; i++) {
            //Frecuencias positiva
            fft_desplazada[i] = fft_original[i];
        }

        return fft_desplazada;
    }

    public double[] EjeNormalizado(){
        double ejeX[] = new double[n/2];
//            for (int i = 0; i < n/2; i++) {
//                ejeX[i] =  ((-1*n/2) + i)/1024f;
//            }
        ejeX[0] = 0;
        double k =1;
        for (int i = 1; i < n/2; i++) {
            ejeX[i] = k/n;
            k++;
        }
        return ejeX;
    }

}