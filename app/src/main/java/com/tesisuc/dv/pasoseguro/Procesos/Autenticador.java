package com.tesisuc.dv.pasoseguro.Procesos;

import java.util.ArrayList;
import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.stat.StatUtils;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

/**
 *
 * @author Victor Gil
 */
public class Autenticador {

    private String identificador;

    //Correlaciones
    private double mediaPM, desviPM;
    private double mediaSM, desviSM;
    private double mediaPZ, desviPZ;
    private double mediaSZ, desviSZ;
    private double criterioPM, criterioSM, criterioPZ, criterioSZ;

    //Vectores de verificacion de parametros
    private double[] mediaparametrosZ, desviacionParametrosZ;
    private double[] mediaparametrosM, desviacionParametrosM;
    //Vectores promedio de la persona
    private double[] zprom, mprom;
    private double[] zespectro, mespectro;

        public Autenticador(double[] zprome, double[] mprome,
                            double[] correPM, double[] correSM,
                            double[] correPZ, double[] correSZ,
                            ArrayList<double[]> matrizM, ArrayList<double[]> matrizZ,
                            double[] zfft, double[] mfft) {
            zprom = zprome;
            mprom = mprome;
            zespectro = zfft;
            mespectro = mfft;
            //CALCULAR CORRELACIONES
            mediaPM = StatUtils.mean(correPM);
            desviPM = Math.sqrt(StatUtils.variance(correPM));
            mediaSM = StatUtils.mean(correSM);
            desviSM = Math.sqrt(StatUtils.variance(correSM));
            mediaPZ = StatUtils.mean(correPZ);
            desviPZ = Math.sqrt(StatUtils.variance(correPZ));
            mediaSZ = StatUtils.mean(correSZ);
            desviSZ = Math.sqrt(StatUtils.variance(correSZ));

            ConstantesPearson();
            ConstantesSpearman();

            //CALCULAR VECTORES Z Y M
            mediaparametrosZ = new double[10];
            desviacionParametrosZ = new double[10];
            mediaparametrosM = new double[9];
            desviacionParametrosM = new double[9];
            DescriptiveStatistics[] statsZ = new DescriptiveStatistics[10];
            DescriptiveStatistics[] statsM = new DescriptiveStatistics[9];
            for (int i = 0; i < 9; i++) {
                statsZ[i] = new DescriptiveStatistics();
                statsM[i] = new DescriptiveStatistics();
                for (int j = 0; j < matrizM.size(); j++) {

                    statsZ[i].addValue(matrizZ.get(j)[i]);
                    statsM[i].addValue(matrizM.get(j)[i]);
                }

            }
            statsZ[9] = new DescriptiveStatistics();
            for (int i = 0; i < matrizM.size(); i++) {
                statsZ[9].addValue(matrizZ.get(i)[9]);

            }

            for (int i = 0; i < 9; i++) {
                mediaparametrosZ[i] = statsZ[i].getMean();
                desviacionParametrosZ[i] = Math.sqrt(statsZ[i].getVariance());
                mediaparametrosM[i] = statsM[i].getMean();
                desviacionParametrosM[i] = Math.sqrt(statsM[i].getVariance());
            }
            mediaparametrosZ[9] = statsZ[9].getMean();
            desviacionParametrosZ[9] = Math.sqrt(statsZ[9].getVariance());

        }

        public int verificarPersona(double[] cicloz, double[] ciclom) {
            //PONER UN TRY CATCH AQUI PARA VERIFICAR QUE CICLO Z Y CICLO M TIENEN EL MISMO TAMANO
            double[] sujetoz = cicloz;
            double[] sujetom = ciclom;
            float c = 0;
            int verificacion = 0;
            int tamano = sujetom.length;
            double acumulado = 0;
            double correPM, correSM;
            double correPZ, correSZ;

            //Hacer la interpolacion si hace falta
            if (sujetoz.length != zprom.length) {
                InterpolacionCiclos inter = new InterpolacionCiclos(zprom.length);
                sujetoz = inter.InterCicloAuten(sujetoz, zprom.length);
                sujetom = inter.InterCicloAuten(sujetom, zprom.length);
            }

            double[] parametrosZ = ParametrosTiempo.parametrosTemporalesZ1(sujetoz);
            double[] parametrosM = ParametrosTiempo.parametrosTemporalesM1(sujetom, tamano);

            Correlacion cor = new Correlacion();
            correPM = cor.CoPearson(getMprom(), sujetom);
            correSM = cor.CoSpearman(getMprom(), sujetom);
            correPZ = cor.CoPearson(getZprom(), sujetoz);
            correSZ = cor.CoSpearman(getZprom(), sujetoz);

            if (tamano > getMediaparametrosM()[8] - 3 * getDesviacionParametrosM()[8]
                    && tamano < getMediaparametrosM()[8] + 3 * getDesviacionParametrosM()[8]) {
            //Comprobar que cumple con las correlaciones Temporales
            if (correPM > criterioPM && correSM > criterioSM
                    && correPZ > criterioPZ && correSZ > criterioSZ) {
                int nPuntos = 1024; //numero de puntos de la FFT
                //Vectores para el calculo de la FFT del vector m
                Complex mpromFrecuencia[] = new Complex[nPuntos];
                double mpromFrecuencia_mod[] = new double[nPuntos];
                 FFT fm = new FFT(sujetom, nPuntos, 1);
                mpromFrecuencia = fm.Transformada(sujetom.length);
                for (int i = 0; i < mpromFrecuencia.length; i++) {
                    mpromFrecuencia_mod[i] = Math.sqrt(Math.pow(mpromFrecuencia[i].getReal(), 2) + Math.pow(mpromFrecuencia[i].getImaginary(), 2));

                }
                double mpromFrecuencia_mod2[];
                mpromFrecuencia_mod2 = fm.FFTShift(mpromFrecuencia_mod);
                double correPMFFT = cor.CoPearson(mespectro, mpromFrecuencia_mod2);

                if (correPMFFT > 0.988) {

                    //Comparacion de parametros temporales
                    verificacion = 2;
                    for (int i = 0; i < 9; i++) {
                        if (i < 4) {
                            c = 1.5f;
                        } else {
                            c = 1f;
                        }
                        //PARAMETROS Z
                        if (parametrosZ[i] > getMediaparametrosZ()[i] - getDesviacionParametrosZ()[i]
                                && parametrosZ[i] < getMediaparametrosZ()[i] + getDesviacionParametrosZ()[i]) {
                            //    System.out.println( 1 + " parametroZ en posi " + i + " : " + parametrosZ[i] );
                            acumulado = acumulado + 1 * c;

                        } else if (parametrosZ[i] > getMediaparametrosZ()[i] - 2 * getDesviacionParametrosZ()[i]
                                && parametrosZ[i] < getMediaparametrosZ()[i] + 2 * getDesviacionParametrosZ()[i]) {
                            //    System.out.println( 0.8 + " parametroZ en posi " + i + " : " + parametrosZ[i] );
                            acumulado = acumulado + 0.8 * c;
                        } else if (parametrosZ[i] > getMediaparametrosZ()[i] - 3 * getDesviacionParametrosZ()[i]
                                && parametrosZ[i] < getMediaparametrosZ()[i] + 3 * getDesviacionParametrosZ()[i]) {
                            //     System.out.println( 0.4 + " parametroZ en posi " + i + " : " + parametrosZ[i] );
                            acumulado = acumulado + 0.2 * c;
                        }

                        //Parametros M
                        if (parametrosM[i] > getMediaparametrosM()[i] - getDesviacionParametrosM()[i]
                                && parametrosM[i] < getMediaparametrosM()[i] + getDesviacionParametrosM()[i]) {
                            //      System.out.println( 1 + " parametroM en posi " + i + " : " + parametrosM[i] );
                            acumulado = acumulado + 1 * c;

                        } else if (parametrosM[i] > getMediaparametrosM()[i] - 2 * getDesviacionParametrosM()[i]
                                && parametrosM[i] < getMediaparametrosM()[i] + 2 * getDesviacionParametrosM()[i]) {
                            //    System.out.println( 0.8 + " parametroM en posi " + i + " : " + parametrosM[i] );
                            acumulado = acumulado + 0.8 * c;
                        } else if (parametrosM[i] > getMediaparametrosM()[i] - 3 * getDesviacionParametrosM()[i]
                                && parametrosM[i] < getMediaparametrosM()[i] + 3 * getDesviacionParametrosM()[i]) {
                            //     System.out.println( 0.4 + " parametroM en posi " + i + " : " + parametrosM[i] );
                            acumulado = acumulado + 0.2 * c;
                        }

                    }
                    if (parametrosZ[9] > getMediaparametrosZ()[9] - getDesviacionParametrosZ()[9]
                            && parametrosZ[9] < getMediaparametrosZ()[9] + getDesviacionParametrosZ()[9]) {
                        //      System.out.println( 1 + " parametroZ en posi " + 9 + " : " + parametrosZ[9] );
                        acumulado = acumulado + 1;

                    } else if (parametrosZ[9] > getMediaparametrosZ()[9] - 2 * getDesviacionParametrosZ()[9]
                            && parametrosZ[9] < getMediaparametrosZ()[9] + 2 * getDesviacionParametrosZ()[9]) {
                        //    System.out.println( 0.8 + " parametroZ en posi " + 9 + " : " + parametrosZ[9] );
                        acumulado = acumulado + 0.8;
                    } else if (parametrosZ[9] > getMediaparametrosZ()[9] - 3 * getDesviacionParametrosZ()[9]
                            && parametrosZ[9] < getMediaparametrosZ()[9] + 3 * getDesviacionParametrosZ()[9]) {
                        //   System.out.println( 0.4 + " parametroZ en posi " + 9 + " : " + parametrosZ[9] );
                        acumulado = acumulado + 0.2;
                    }

                    if (acumulado >= 15.4) {

                        verificacion = 3;
                    }

                    System.out.println(verificacion + ", acumulado despues de la correlacion: " + acumulado);
                }

            }
        }
            return verificacion;
        }



    public void ConstantesPearson() {
        criterioPM = 0;
        criterioPZ = 0;
        double mm = 0;
        double dm = 0;
        double mz = 0;
        double dz = 0;

        if (mediaPM > 0.9) {
            mm = (8 * mediaPM - 7) * 0.065;
        } else {
            mm = 0.2 * 0.065;
        }

        if (desviPM < 0.1) {
            dm = (1.20 / ((desviPM * 25) + 0.3)) * desviPM;
        } else {
            dm = 0.04286;
        }

        if (mediaPZ > 0.9) {
            mz = (8 * mediaPZ - 7) * 0.075;
        } else {
            mz = 0.2 * 0.075;
        }

        if (desviPZ < 0.1) {
            dz = (1.3 / ((desviPZ * 25) + 0.3)) * desviPZ;
        } else {
            dz = 0.04393;
        }
        criterioPM = mediaPM - mm - dm;
        if (criterioPM < 0.8) {
            criterioPM = 0.8;
        }
        criterioPZ = mediaPZ - mz - dz;
        if (criterioPZ < 0.8) {
            criterioPZ = 0.8;
        }
    }

    public void ConstantesSpearman() {
        criterioSM = 0;
        criterioSZ = 0;
        double mm = 0;
        double dm = 0;
        double mz = 0;
        double dz = 0;

        if (mediaSM > 0.89) {
            mm = (7.5 * mediaSM - 6.5) * 0.07;
        } else {
            mm = 0.175 * 0.07;
        }

        if (desviSM < 0.1) {
            dm = (1.5 / ((desviSM * 30) + 0.2)) * desviSM;
        } else {
            dm = 0.046875;
        }

        if (mediaSZ > 0.9) {
            mz = (8 * mediaSZ - 7) * 0.1;
        } else {
            mz = 0.2 * 0.1;
        }

        if (desviSZ < 0.1) {
            dz = (1.6 / ((desviSZ * 30) + 0.2)) * desviSZ;
        } else {
            dz = 0.05;
        }
        criterioSM = mediaSM - mm - dm;
        criterioSZ = mediaSZ - mz - dz;

        if (criterioSM < 0.78) {
            criterioSM = 0.8;
        }
        if (criterioSZ < 0.78) {
            criterioSZ = 0.8;
        }
    }

    public String getIdentificador() {
        return identificador;
    }

    public void setIdentificador(String identificador) {
        this.identificador = identificador;
    }

    public double getMediaPM() {
        return mediaPM;
    }

    public void setMediaPM(double mediaPM) {
        this.mediaPM = mediaPM;
    }

    public double getDesviPM() {
        return desviPM;
    }

    public void setDesviPM(double desviPM) {
        this.desviPM = desviPM;
    }

    public double getMediaSM() {
        return mediaSM;
    }

    public void setMediaSM(double mediaSM) {
        this.mediaSM = mediaSM;
    }

    public double getDesviSM() {
        return desviSM;
    }

    public void setDesviSM(double desviSM) {
        this.desviSM = desviSM;
    }

    public double getMediaPZ() {
        return mediaPZ;
    }

    public void setMediaPZ(double mediaPZ) {
        this.mediaPZ = mediaPZ;
    }

    public double getDesviPZ() {
        return desviPZ;
    }

    public void setDesviPZ(double desviPZ) {
        this.desviPZ = desviPZ;
    }

    public double getMediaSZ() {
        return mediaSZ;
    }

    public void setMediaSZ(double mediaSZ) {
        this.mediaSZ = mediaSZ;
    }

    public double getDesviSZ() {
        return desviSZ;
    }

    public void setDesviSZ(double desviSZ) {
        this.desviSZ = desviSZ;
    }

    public double getCriterioPM() {
        return criterioPM;
    }

    public void setCriterioPM(double criterioPM) {
        this.criterioPM = criterioPM;
    }

    public double getCriterioSM() {
        return criterioSM;
    }

    public void setCriterioSM(double criterioSM) {
        this.criterioSM = criterioSM;
    }

    public double getCriterioPZ() {
        return criterioPZ;
    }

    public void setCriterioPZ(double criterioPZ) {
        this.criterioPZ = criterioPZ;
    }

    public double getCriterioSZ() {
        return criterioSZ;
    }

    public void setCriterioSZ(double criterioSZ) {
        this.criterioSZ = criterioSZ;
    }

    public double[] getMediaparametrosZ() {
        return mediaparametrosZ;
    }

    public void setMediaparametrosZ(double[] mediaparametrosZ) {
        this.mediaparametrosZ = mediaparametrosZ;
    }

    public double[] getDesviacionParametrosZ() {
        return desviacionParametrosZ;
    }

    public void setDesviacionParametrosZ(double[] desviacionParametrosZ) {
        this.desviacionParametrosZ = desviacionParametrosZ;
    }

    public double[] getMediaparametrosM() {
        return mediaparametrosM;
    }

    public void setMediaparametrosM(double[] mediaparametrosM) {
        this.mediaparametrosM = mediaparametrosM;
    }

    public double[] getDesviacionParametrosM() {
        return desviacionParametrosM;
    }

    public void setDesviacionParametrosM(double[] desviacionParametrosM) {
        this.desviacionParametrosM = desviacionParametrosM;
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

    public double[] getZespectro() {
        return zespectro;
    }

    public void setZespectro(double[] zespectro) {
        this.zespectro = zespectro;
    }

    public double[] getMespectro() {
        return mespectro;
    }

    public void setMespectro(double[] mespectro) {
        this.mespectro = mespectro;
    }
}