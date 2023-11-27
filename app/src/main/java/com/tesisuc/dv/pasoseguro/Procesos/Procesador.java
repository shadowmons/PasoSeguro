package com.tesisuc.dv.pasoseguro.Procesos;

import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.stat.StatUtils;

import java.util.ArrayList;

/**
 * Created by Daniel H on 24/02/2019.
 */

public class Procesador {
    private int numeroTablas;
    private String autentico;

    //Vectores de promedios
    private double[] zprom;
    private double[] mprom;
    ArrayList<double[]> mciclosList;
    ArrayList<double[]> zciclosList;

    //Vectores FFT
    private double[] zfft, mfft;
    private double[][] fftespectroM;

    //Vectores de correlaciĂ³n
    private double[] pearsonM;
    private double[] spearmanM;
    private double[] kendallM;
    private double[] pearsonZ;
    private double[] spearmanZ;
    private double[] kendallZ;

    //Matrices de valores de entrada auxiliar
    private ArrayList<ArrayList<Double>> ztotala;
    private ArrayList<ArrayList<Double>> mtotala;

    //Matriz de caracteristicas
    private ArrayList<double[]> matrizZ;
    private ArrayList<double[]> matrizZ2;
    private ArrayList<double[]> matrizM;

    public Procesador(String autentico, int numeroTablas){
        this.autentico = autentico;
        this.numeroTablas = numeroTablas;

    }

    public void procesarCiclos(ArrayList<ArrayList<Double>> ztotal,  ArrayList<ArrayList<Double>> mtotal, ArrayList<Integer> muestraslongitud, int n){
        mtotala = new ArrayList<>();
        ztotala = new ArrayList<>();
        for (int i = 0; i < ztotal.size(); i++) {
            ztotala.add(new ArrayList<>(ztotal.get(i)));
            mtotala.add(new ArrayList<>(mtotal.get(i)));
            InterpolacionCiclos magnitudInterpolada = new InterpolacionCiclos(mtotal.get(i), n);
            magnitudInterpolada.nuevoCicloInterpolado(mtotala.get(i));
            InterpolacionCiclos zInterpolada = new InterpolacionCiclos(ztotal.get(i), n);
            zInterpolada.nuevoCicloInterpolado(ztotala.get(i));
        }

        double[] mciclos = new double[mtotala.get(0).size()];
        double[] zciclos = new double[ztotala.get(0).size()];

        matrizZ = new ArrayList<double[]>();
        matrizZ2 = new ArrayList<double[]>();
        matrizM = new ArrayList<double[]>();

        //promediado
        zprom = Ciclos.promediado(ztotala);
        mprom = Ciclos.promediado(mtotala);
        descartarciclos(mprom, ztotala, mtotala);
        zprom = Ciclos.promediado(ztotala);
        mprom = Ciclos.promediado(mtotala);


        //lista de ciclos
        zciclosList = new ArrayList<double[]>();
        mciclosList = new ArrayList<double[]>();

        Correlacion correlacion = new Correlacion();
        kendallZ = new double[ztotala.size()];
        pearsonZ = new double[ztotala.size()];
        spearmanZ = new double[ztotala.size()];
        kendallM = new double[ztotala.size()];
        pearsonM = new double[ztotala.size()];
        spearmanM = new double[ztotala.size()];

        for (int i = 0; i < ztotala.size(); i++) {
            mciclos = new double[ztotala.get(i).size()];
            zciclos = new double[ztotala.get(i).size()];
            for (   int j = 0; j < ztotala.get(i).size(); j++) {
                zciclos[j] = ztotala.get(i).get(j);
                mciclos[j] = mtotala.get(i).get(j);
            }
            pearsonZ[i] = correlacion.CoPearson(zprom, zciclos);
            pearsonM[i] = correlacion.CoPearson(mprom, mciclos);
            spearmanZ[i] = correlacion.CoSpearman(zprom, zciclos);
            spearmanM[i] = correlacion.CoSpearman(mprom, mciclos);
            zciclosList.add(zciclos);
            mciclosList.add(mciclos);
            matrizZ.add(ParametrosTiempo.parametrosTemporalesZ1(zciclos));
            matrizZ2.add(ParametrosTiempo.parametrosTemporalesZ2(zciclos));
            if(muestraslongitud.size() != 0){
                matrizM.add(ParametrosTiempo.parametrosTemporalesM1(mciclos, muestraslongitud.get(i)));
            }
        }

        //Calculo de la FFT
        int nPuntos = 1024; //numero de puntos de la FFT
        //Vectores para el calculo de la FFT
        Complex zpromFrecuencia[] = new Complex[nPuntos];
        double zpromFrecuencia_mod[] = new double[nPuntos];
        Complex mpromFrecuencia[] = new Complex[nPuntos];
        double mpromFrecuencia_mod[] = new double[nPuntos];
        FFT fz = new FFT(zprom, nPuntos, 1);
        zpromFrecuencia = fz.Transformada(zprom.length);
        FFT fm = new FFT(mprom, nPuntos, 1);
        mpromFrecuencia = fm.Transformada(mprom.length);

        for (int i = 0; i < zpromFrecuencia.length; i++) {
            zpromFrecuencia_mod[i] = Math.sqrt(Math.pow(zpromFrecuencia[i].getReal(), 2) + Math.pow(zpromFrecuencia[i].getImaginary(), 2));
            mpromFrecuencia_mod[i] = Math.sqrt(Math.pow(mpromFrecuencia[i].getReal(), 2) + Math.pow(mpromFrecuencia[i].getImaginary(), 2));
        }
        zfft = new double[307];
        zfft = fz.FFTShift(zpromFrecuencia_mod);
        mfft = new double[307];
        mfft = fm.FFTShift(mpromFrecuencia_mod);

    }

    private void descartarciclos(double[] mpro, ArrayList<ArrayList<Double>> z, ArrayList<ArrayList<Double>> m) {
        Correlacion cor = new Correlacion();
        int j = 0;
        double[] aux;
        double[] corp = new double[m.size()];
        for (int i = 0; i < m.size(); i++) {
            aux = convertir(m.get(i));
            corp[i] = cor.CoPearson(aux, mpro);
        }
        double media = StatUtils.mean(corp);
        double desvi = Math.sqrt(StatUtils.variance(corp));
        for (int i = 0; i < corp.length; i++) {
            if (corp[i] < (media - 2 * desvi)) {
                m.remove(j);
                z.remove(j);
                j = j - 1;
            }
            j = j + 1;

        }

    }

    private double[] convertir(ArrayList<Double> a) {
        double x[] = new double[a.size()];

        for (int i = 0; i < a.size(); i++) {
            x[i] = a.get(i);

        }
        return x;
    }

    public int getNumeroTablas() {
        return numeroTablas;
    }

    public void setNumeroTablas(int numeroTablas) {
        this.numeroTablas = numeroTablas;
    }

    public String getAutentico() {
        return autentico;
    }

    public void setAutentico(String autentico) {
        this.autentico = autentico;
    }

    public double[] getZprom() {
        return zprom;
    }

    public void setZprom(double[] zprom) {
        this.zprom = zprom;
    }

    public double[] getMprom() {
        return mprom;
    }

    public void setMprom(double[] mprom) {
        this.mprom = mprom;
    }

    public double[] getZfft() {
        return zfft;
    }

    public void setZfft(double[] zfft) {
        this.zfft = zfft;
    }

    public double[] getMfft() {
        return mfft;
    }

    public void setMfft(double[] mfft) {
        this.mfft = mfft;
    }

    public double[][] getFftespectroM() {
        return fftespectroM;
    }

    public void setFftespectroM(double[][] fftespectroM) {
        this.fftespectroM = fftespectroM;
    }

    public double[] getPearsonM() {
        return pearsonM;
    }

    public void setPearsonM(double[] pearsonM) {
        this.pearsonM = pearsonM;
    }

    public double[] getSpearmanM() {
        return spearmanM;
    }

    public void setSpearmanM(double[] spearmanM) {
        this.spearmanM = spearmanM;
    }

    public double[] getKendallM() {
        return kendallM;
    }

    public void setKendallM(double[] kendallM) {
        this.kendallM = kendallM;
    }

    public double[] getPearsonZ() {
        return pearsonZ;
    }

    public void setPearsonZ(double[] pearsonZ) {
        this.pearsonZ = pearsonZ;
    }

    public double[] getSpearmanZ() {
        return spearmanZ;
    }

    public void setSpearmanZ(double[] spearmanZ) {
        this.spearmanZ = spearmanZ;
    }

    public double[] getKendallZ() {
        return kendallZ;
    }

    public void setKendallZ(double[] kendallZ) {
        this.kendallZ = kendallZ;
    }

    public ArrayList<double[]> getMatrizZ() {
        return matrizZ;
    }

    public ArrayList<double[]> getMatrizZ2() {
        return matrizZ2;
    }

    public void setMatrizZ(ArrayList<double[]> matrizZ) {
        this.matrizZ = matrizZ;
    }

    public ArrayList<double[]> getMatrizM() {
        return matrizM;
    }

    public void setMatrizM(ArrayList<double[]> matrizM) {
        this.matrizM = matrizM;
    }

    public ArrayList<double[]> getMciclosList() {
        return mciclosList;
    }

    public void setMciclosList(ArrayList<double[]> mciclosList) {
        this.mciclosList = mciclosList;
    }

    public ArrayList<double[]> getZciclosList() {
        return zciclosList;
    }

    public void setZciclosList(ArrayList<double[]> zciclosList) {
        this.zciclosList = zciclosList;
    }


}