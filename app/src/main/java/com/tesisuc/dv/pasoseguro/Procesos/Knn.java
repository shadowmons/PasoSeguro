package com.tesisuc.dv.pasoseguro.Procesos;

import android.content.Context;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;
import java.util.Scanner;

/**
 * Created by Daniel H on 01/04/2019.
 */

public class Knn {

    private int k;
    private double conta, conti;
    private ArrayList<double[]> templateAutenticoZ;
    private ArrayList<double[]> templateImpostoresZ;

    public Knn(int k, ArrayList<double[]> templateAutenticoZ, Context context) {
        this.k = k;
        this.conta = 0.0;
        this.conti = 0.0;
        this.templateAutenticoZ = new ArrayList<>();
        this.templateImpostoresZ = new ArrayList<>();
        for (int i = 0; i < templateAutenticoZ.size(); i++) {
            this.templateAutenticoZ.add(new double[templateAutenticoZ.get(i).length]);
            for (int j = 0; j < templateAutenticoZ.get(i).length; j++) {
                this.templateAutenticoZ.get(i)[j] = templateAutenticoZ.get(i)[j];
             }
        }
        this.templateImpostoresZ = extraerMatrizImpostores(context);
    }

    public Boolean clasificar(ArrayList<double[]> entrada) {
        ArrayList<double[]> templateAutenticoZcopia = new ArrayList<>();
        ArrayList<double[]> templateImpostoresZcopia = new ArrayList<>();
        ArrayList<Double> distanciasAutentico = new ArrayList<>();
        ArrayList<Double> distanciasImpostores = new ArrayList<>();
        conta = 0;
        conti = 0;
        for (int i = 0; i < templateAutenticoZ.size(); i++) {
            templateAutenticoZcopia.add(new double[templateAutenticoZ.get(i).length]);
            for (int j = 0; j < templateAutenticoZ.get(i).length; j++) {
                templateAutenticoZcopia.get(i)[j] = templateAutenticoZ.get(i)[j];
            }
        }
        for (int i = 0; i < templateImpostoresZ.size(); i++) {
            templateImpostoresZcopia.add(new double[templateImpostoresZ.get(i).length]);
            for (int j = 0; j < templateImpostoresZ.get(i).length; j++) {
                templateImpostoresZcopia.get(i)[j] = templateImpostoresZ.get(i)[j];
            }
        }
        estandarizacion(entrada, templateAutenticoZcopia, templateImpostoresZcopia);
        normalizacion(entrada, templateAutenticoZcopia, templateImpostoresZcopia);

        for (int j = 0; j < templateAutenticoZcopia.size(); j++) {
            double dist_a = 0d;
            double aux_a = 0d;
            double aux2 = 0d;
            for (int l = 0; l < templateAutenticoZcopia.get(j).length; l++) {
                aux2 = templateAutenticoZcopia.get(j)[l] - entrada.get(0)[l];
                aux_a = Math.abs(aux2) + aux_a;
            }
            dist_a = aux_a;
            distanciasAutentico.add(dist_a);
        }

        for (int j = 0; j < templateImpostoresZcopia.size(); j++) {
            double aux2 = 0d;
            double dist_i = 0d;
            double aux_i = 0d;
            for (int l = 0; l < templateImpostoresZcopia.get(j).length; l++) {
                aux2 = templateImpostoresZcopia.get(j)[l] - entrada.get(0)[l];
                aux_i = Math.abs(aux2) + aux_i;
            }
            dist_i = aux_i;
            distanciasImpostores.add(dist_i);
        }

        for (int q = 0; q < k; q++) {
            if (Collections.min(distanciasAutentico) <= Collections.min(distanciasImpostores)) {
                conta = (1 / Collections.min(distanciasAutentico)) + conta;
                System.out.println((1 / Collections.min(distanciasAutentico)));
                distanciasAutentico.remove(new Double(Collections.min(distanciasAutentico)));
            } else {
                conti = (1 / Collections.min(distanciasImpostores)) + conti;
                distanciasImpostores.remove(new Double(Collections.min(distanciasImpostores)));
            }
        }


        if ((conta > conti) && conta > 8)  {
            return true;
        } else {
            return false;
        }
    }

    public void normalizacion(ArrayList<double[]> inputData, ArrayList<double[]> templateAutentico,
                              ArrayList<double[]> templateImpostores) {

        double[] minAutentico = new double[templateAutentico.get(0).length];
        double[] maxAutentico = new double[templateAutentico.get(0).length];
        double[] minImpostor = new double[templateImpostores.get(0).length];
        double[] maxImpostor = new double[templateImpostores.get(0).length];
        double[] minInputData = new double[inputData.get(0).length];
        double[] maxInputData = new double[inputData.get(0).length];

        for (int i = 0; i < templateAutentico.get(0).length; i++) {
            minAutentico[i] = templateAutentico.get(0)[i];
            maxAutentico[i] = templateAutentico.get(0)[i];
            for (int j = 0; j < templateAutentico.size(); j++) {
                if (templateAutentico.get(j)[i] < minAutentico[i]) {
                    minAutentico[i] = templateAutentico.get(j)[i];
                }
                if (templateAutentico.get(j)[i] > maxAutentico[i]) {
                    maxAutentico[i] = templateAutentico.get(j)[i];
                }
            }

        }

        for (int i = 0; i < templateImpostores.get(0).length; i++) {
            minImpostor[i] = templateAutentico.get(0)[i];
            maxImpostor[i] = templateAutentico.get(0)[i];
            for (int j = 0; j < templateImpostores.size(); j++) {
                if (templateImpostores.get(j)[i] < minImpostor[i]) {
                    minImpostor[i] = templateImpostores.get(j)[i];
                }
                if (templateImpostores.get(j)[i] > maxImpostor[i]) {
                    maxImpostor[i] = templateImpostores.get(j)[i];
                }
            }
        }
        for (int i = 0; i < inputData.get(0).length; i++) {
            minInputData[i] = inputData.get(0)[i];
            maxInputData[i] = inputData.get(0)[i];
            for (int j = 0; j < inputData.size(); j++) {
                if (inputData.get(j)[i] < minInputData[i]) {
                    minInputData[i] = inputData.get(j)[i];
                }
                if (inputData.get(j)[i] > maxInputData[i]) {
                    maxInputData[i] = inputData.get(j)[i];
                }
            }

        }

        double min, max;

        for (int i = 0; i < inputData.get(0).length; i++) {
            ArrayList<Double> minArray = new ArrayList<>();
            ArrayList<Double> maxArray = new ArrayList<>();

            minArray.add(minAutentico[i]);
            minArray.add(minImpostor[i]);
            minArray.add(minInputData[i]);

            maxArray.add(maxAutentico[i]);
            maxArray.add(maxImpostor[i]);
            maxArray.add(maxInputData[i]);

            min = Collections.min(minArray);
            max = Collections.max(maxArray);

            for (int j = 0; j < inputData.size(); j++) {
                inputData.get(j)[i] = (inputData.get(j)[i] - min) / (max - min);

            }

            for (int j = 0; j < templateAutentico.size(); j++) {
                templateAutentico.get(j)[i] = (templateAutentico.get(j)[i] - min) / (max - min);

            }

            for (int j = 0; j < templateImpostores.size(); j++) {
                templateImpostores.get(j)[i] = (templateImpostores.get(j)[i] - min) / (max - min);

            }

        }
    }

    public void estandarizacion(ArrayList<double[]> inputData, ArrayList<double[]> templateAutentico,
                                ArrayList<double[]> templateImpostores) {

        ArrayList<Double> v = new ArrayList<>();

        for (int i = 0; i < inputData.get(0).length; i++) {

            for (int j = 0; j < templateImpostores.size(); j++) {
                v.add(templateImpostores.get(j)[i]);
            }
            for (int j = 0; j < templateAutentico.size(); j++) {
                v.add(templateAutentico.get(j)[i]);
            }
            for (int j = 0; j < inputData.size(); j++) {
                v.add(inputData.get(j)[i]);
            }

            double promedio = promedio(v);
            double desviacion = desviacion(v);

            for (int j = 0; j < templateImpostores.size(); j++) {
                templateImpostores.get(j)[i] = (templateImpostores.get(j)[i] - promedio) / desviacion;
            }
            for (int j = 0; j < templateAutentico.size(); j++) {
                templateAutentico.get(j)[i] = (templateAutentico.get(j)[i] - promedio) / desviacion;
            }
            for (int j = 0; j < inputData.size(); j++) {
                inputData.get(j)[i] = (inputData.get(j)[i] - promedio) / desviacion;
            }

        }


    }

    public double promedio(ArrayList<Double> v) {
        double prom = 0.0;
        for (int i = 0; i < v.size(); i++)
            prom += v.get(i);

        return prom / (double) v.size();
    }

    public double desviacion(ArrayList<Double> v) {
        double prom, sum = 0;
        int i, n = v.size();
        prom = promedio(v);

        for (i = 0; i < n; i++)
            sum += Math.pow(v.get(i) - prom, 2);

        return Math.sqrt(sum / (double) n);
    }

    public static ArrayList<double[]> extraerMatrizImpostores(Context context) {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(context.getAssets().open("Matriz_Impostores_App.txt")));
            String linea;
            int lineas = 0;

            while ((linea = reader.readLine()) != null) {
                lineas++;
            }
            reader.close();

            ArrayList<double[]> matriz = new ArrayList<>();
            reader = new BufferedReader(new InputStreamReader(context.getAssets().open("Matriz_Impostores_App.txt")));
            for (int i = 0; i < lineas / 9; i++) {
                matriz.add(new double[9]);
                for (int j = 0; j < 9; j++) {
                    matriz.get(i)[j] =  Double.parseDouble(reader.readLine());
                }
            }

            return matriz;
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }
}

