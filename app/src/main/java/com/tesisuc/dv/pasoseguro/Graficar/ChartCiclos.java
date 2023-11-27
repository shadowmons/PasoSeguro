package com.tesisuc.dv.pasoseguro.Graficar;

import android.content.Context;
import android.graphics.Color;
import android.widget.RelativeLayout;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.tesisuc.dv.pasoseguro.Procesos.*;

import java.nio.DoubleBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Daniel H on 22/09/2018.
 */

public class ChartCiclos {

    private LineChart mChart;
    private LineData data;

    public ChartCiclos(RelativeLayout layout, Context context, float limite, String text) {
        mChart = new LineChart(context);
        layout.addView(mChart);
        data = new LineData();
        data.setValueTextColor(Color.BLACK);
        ChartConfig(text, true, true, true, false, true, Color.WHITE, limite, data);

    }

    private void ChartConfig(String text, boolean touch,
                             boolean drag, boolean scale, boolean grid, boolean pinch, int background, float limite, LineData data) {
        Description desc = new Description();
        desc.setText(text);
        getmChart().getDescription().setEnabled(true);
        getmChart().setDescription(desc);
        getmChart().setTouchEnabled(touch);
        getmChart().setDragEnabled(drag);
        getmChart().setScaleEnabled(scale);
        getmChart().setDrawGridBackground(grid);
        getmChart().setPinchZoom(pinch);
        getmChart().setBackgroundColor(background);
        getmChart().getLayoutParams().height = android.view.ViewGroup.LayoutParams.MATCH_PARENT;
        getmChart().getLayoutParams().width = android.view.ViewGroup.LayoutParams.MATCH_PARENT;
        getmChart().setData(data);

        //Legenda y ejes
        Legend legenda = getmChart().getLegend();
        legenda.setForm(Legend.LegendForm.LINE);
        legenda.setTextColor(Color.BLACK);
        //Eje x
        XAxis x = getmChart().getXAxis();
        x.setTextColor(Color.BLACK);
        x.setAvoidFirstLastClipping(true);
      //  x.setDrawGridLines(false);
        x.setTextSize(15);
        //Eje y
        YAxis yleft = getmChart().getAxisLeft();
       // yleft.setDrawGridLines(false);
        yleft.setTextColor(Color.BLACK);
        yleft.setAxisMinimum(-1 * limite);
        yleft.setAxisMaximum(limite);
        yleft.setTextSize(15);
        YAxis yRight = getmChart().getAxisRight();
        yRight.setEnabled(false);
    }


    public LineDataSet createSet(List<Entry> a, String label, int color) {
        LineDataSet set = new LineDataSet(a, label);
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        set.setColor(color);
        set.setLineWidth(1.5f);
        set.setFillAlpha(35);
        set.setFillColor(color);
        set.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        set.setHighLightColor(Color.rgb(244, 177, 177));
        set.setValueTextColor(Color.BLACK);
        set.setValueTextSize(3f);
        set.setCircleRadius(4f);
        set.setDrawValues(false);
        set.setDrawCircles(false);
        return set;
    }

    public void Graficar(List<ILineDataSet> dataSets) {
        LineData lineData = new LineData(dataSets);
        mChart.setData(lineData);

        //limitar numero de muestras visibles
        getmChart().setVisibleXRangeMaximum(200);
        getmChart().moveViewToX(data.getEntryCount() - 201);
        mChart.getLegend().setEnabled(false);
        //Graficar
        mChart.invalidate();

    }



    public void mostrarChartInterpolado(String nombre, float[] x, float[] y,float[] z) {
        ArrayList<Double> zciclo = new ArrayList<Double>();
        ArrayList<Double> magnitud = new ArrayList<Double>();

        for (int i = 0; i < z.length; i++) {
            zciclo.add((double)z[i]);
            magnitud.add(Math.sqrt(Math.pow((double) x[i], 2) + Math.pow((double) y[i], 2) + Math.pow((double) z[i], 2)));
        }

        Ciclos ciclo1 = new Ciclos(magnitud,zciclo);
        ciclo1.calcularCiclos();
        int n = ciclo1.mediaCiclos(ciclo1.ciclosZ);

        for (int i = 0; i < ciclo1.ciclosZ.size(); i++) {
            InterpolacionCiclos ciclointerZ = new InterpolacionCiclos(ciclo1.ciclosZ.get(i), n);
            ciclointerZ.nuevoCicloInterpolado(ciclo1.ciclosZ.get(i));
            InterpolacionCiclos ciclointerM = new InterpolacionCiclos(ciclo1.ciclosM.get(i), n);
            ciclointerM.nuevoCicloInterpolado(ciclo1.ciclosM.get(i));
            }
        double[] zprom = Ciclos.promediado(ciclo1.ciclosZ);
        double[] mprom = Ciclos.promediado(ciclo1.ciclosM);


            graficarCiclos(ciclo1.ciclosZ,ciclo1.ciclosM, zprom, mprom );

    }

    public void graficarCiclos(ArrayList<ArrayList<Double>> z, ArrayList<ArrayList<Double>> m,double[] zp, double[] mp){

        List<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
        for (int j = 0; j <z.size(); j++) {
            List<Entry> czChart = new ArrayList<Entry>();
            List<Entry> cmChart = new ArrayList<Entry>();
            for (int i = 0; i <z.get(j).size() ; i++) {
            double c1,c2;
            c1=z.get(j).get(i);
            c2=m.get(j).get(i);
            czChart.add(new Entry(i,(float) c1));
            cmChart.add(new Entry(i,(float) c2));

            }
        LineDataSet dataSet1 = createSet(czChart, "z"+j, Color.parseColor("#FFEB3B"));
        LineDataSet dataSet2 = createSet(cmChart, "m"+j, Color.parseColor("#D4E157"));
        dataSets.add(dataSet1);
        dataSets.add(dataSet2);
        }
        List<Entry> czChart = new ArrayList<Entry>();
        List<Entry> cmChart = new ArrayList<Entry>();
        for (int i = 0; i < zp.length; i++) {
            czChart.add(new Entry(i,(float) zp[i]));
            cmChart.add(new Entry(i,(float) mp[i]));

        }
        LineDataSet dataSet3 = createSet(cmChart, "mprom", Color.parseColor("#D50000"));
        LineDataSet dataSet4 = createSet(czChart, "zprom", Color.parseColor("#6A1B9A"));
        dataSets.add(dataSet3);
        dataSets.add(dataSet4);
        Graficar(dataSets);
    }


    public LineChart getmChart() {
        return mChart;
    }

    public void setmChart(LineChart mChart) {
        this.mChart = mChart;
    }
}
