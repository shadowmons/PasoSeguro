package com.tesisuc.dv.pasoseguro.Procesos;


import java.util.ArrayList;
import org.apache.commons.math3.fitting.PolynomialCurveFitter;
import org.apache.commons.math3.fitting.WeightedObservedPoints;

public class Ciclos {

    public ArrayList<Double> tramaZ;
    public ArrayList<Double> tramaMagnitud;
    private ArrayList<Integer> pospicos;

    public ArrayList<ArrayList<Double>> ciclosZ;
    public ArrayList<ArrayList<Double>> ciclosM;

    public Ciclos(ArrayList M, ArrayList Z) {
        tramaZ = new ArrayList<Double>(Z);
        tramaMagnitud = new ArrayList<Double>(M);

        pospicos = new ArrayList<Integer>();
    }

    public void calcularCiclos() {
        for (int i = 25; i < tramaZ.size() - 25; i++) {
            if ((tramaZ.get(i - 25) < tramaZ.get(i)) && (tramaZ.get(i - 24) < tramaZ.get(i))
                    && (tramaZ.get(i - 23) < tramaZ.get(i)) && (tramaZ.get(i - 22) < tramaZ.get(i))
                    && (tramaZ.get(i - 21) < tramaZ.get(i)) &&(tramaZ.get(i - 20) < tramaZ.get(i)) && (tramaZ.get(i - 19) < tramaZ.get(i))
                    && (tramaZ.get(i - 18) < tramaZ.get(i)) && (tramaZ.get(i - 17) < tramaZ.get(i))
                    && (tramaZ.get(i - 16) < tramaZ.get(i)) && (tramaZ.get(i - 15) < tramaZ.get(i))
                    && (tramaZ.get(i - 14) < tramaZ.get(i)) && (tramaZ.get(i - 13) < tramaZ.get(i))
                    && (tramaZ.get(i - 12) < tramaZ.get(i)) && (tramaZ.get(i - 11) < tramaZ.get(i))
                    && (tramaZ.get(i - 10) < tramaZ.get(i)) && (tramaZ.get(i - 9) < tramaZ.get(i))
                    && (tramaZ.get(i - 8) < tramaZ.get(i)) && (tramaZ.get(i - 7) < tramaZ.get(i))
                    && (tramaZ.get(i - 6) < tramaZ.get(i)) && (tramaZ.get(i - 5) < tramaZ.get(i))
                    && (tramaZ.get(i - 4) < tramaZ.get(i)) && (tramaZ.get(i - 3) < tramaZ.get(i)) && (tramaZ.get(i - 2) < tramaZ.get(i))
                    && (tramaZ.get(i - 1) < tramaZ.get(i)) && (tramaZ.get(i) >= tramaZ.get(i + 1)) && (tramaZ.get(i) > tramaZ.get(i + 2))
                    && (tramaZ.get(i) > tramaZ.get(i + 3)) && (tramaZ.get(i) > tramaZ.get(i + 4)) && (tramaZ.get(i) > tramaZ.get(i + 5))
                    && (tramaZ.get(i) > tramaZ.get(i + 6)) && (tramaZ.get(i) > tramaZ.get(i + 7)) && (tramaZ.get(i) > tramaZ.get(i + 8))
                    && (tramaZ.get(i) > tramaZ.get(i + 9)) && (tramaZ.get(i) > tramaZ.get(i + 10)) && (tramaZ.get(i) > tramaZ.get(i + 11))
                    && (tramaZ.get(i) > tramaZ.get(i + 12)) && (tramaZ.get(i) > tramaZ.get(i + 13)) && (tramaZ.get(i) > tramaZ.get(i + 14))
                    && (tramaZ.get(i) > tramaZ.get(i + 15)) && (tramaZ.get(i) > tramaZ.get(i + 16))
                    && (tramaZ.get(i) > tramaZ.get(i + 17)) && (tramaZ.get(i) > tramaZ.get(i + 18))
                    && (tramaZ.get(i) > tramaZ.get(i + 19)) && (tramaZ.get(i) > tramaZ.get(i + 20))&& (tramaZ.get(i) > tramaZ.get(i + 21))
                    && (tramaZ.get(i) > tramaZ.get(i + 22)) && (tramaZ.get(i) > tramaZ.get(i + 23))
                    && (tramaZ.get(i) > tramaZ.get(i + 24)) && (tramaZ.get(i) > tramaZ.get(i + 25))) {
                if (tramaZ.get(i+6)>tramaZ.get(i)-tramaZ.get(i)*0.5 ){
                    int k=i;
                    int j=i;
                    while(tramaZ.get(i)<tramaZ.get(j) + tramaZ.get(i)*0.6){
                        j=j+1;
                    }
                    while(tramaZ.get(i)<tramaZ.get(k) + tramaZ.get(i)*0.3){
                        k=k-1;
                    }
                    int p = Math.round((j+k)/2);
                    pospicos.add(p);
                }else {
                    pospicos.add(i);

                }


            }
        }

        ciclosZ = new ArrayList<ArrayList<Double>>();
        ciclosM = new ArrayList<ArrayList<Double>>();

        for (int i = 0; i < pospicos.size() - 1; i++) {
            ciclosZ.add(new ArrayList<Double>());
            ciclosM.add(new ArrayList<Double>());

            for (int j = pospicos.get(i); j < pospicos.get(i + 1); j++) {
                ciclosZ.get(i).add(tramaZ.get(j));
                ciclosM.get(i).add(tramaMagnitud.get(j));
            }
        }
    }


    public static int mediaCiclos(ArrayList<ArrayList<Double>> M){
        int med = 0;
        for (int i = 0; i < M.size(); i++) {
            med = med + M.get(i).size();
            }
        return Math.round((float)med/M.size());
    }

    public static double[] promediado(ArrayList<ArrayList<Double>> M){
        double[] prom = new double[M.get(0).size()];
        double acum =0;
        for (int i = 0; i < prom.length; i++) {
            acum =0;
            for (int j = 0; j < M.size(); j++) {
                acum = acum  + M.get(j).get(i);
            }
            prom[i] = acum / M.size();

        }

        return prom;
    }

    public static double[] promediado2(ArrayList<ArrayList<Double>> M, int n){
        double[] prom = new double[M.get(0).size()];
        double aux = 0;
        WeightedObservedPoints obs = new WeightedObservedPoints();;
        for (int i = 0; i < M.size(); i++) {
            for (int j = 0; j < M.get(0).size(); j++) {
                obs.add(j, M.get(i).get(j));
            }
        }
        PolynomialCurveFitter fitter = PolynomialCurveFitter.create(n);
        double[] coeff = fitter.fit(obs.toList());

        for (int i = 0; i < M.get(0).size(); i++) {

            aux = coeff[0];
            for (int j = 1; j < n+1; j++) {
                aux = aux + (Math.pow(i, j) * coeff[j]);

            }
            prom[i] = aux;


        }

        return prom;
    }
}
