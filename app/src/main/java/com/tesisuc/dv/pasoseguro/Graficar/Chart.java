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

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Daniel H on 22/09/2018.
 */

public class Chart {

    private LineChart mChart;
    private LineData data;

    public Chart(RelativeLayout layout, Context context, float limite, String text) {
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
        x.setDrawGridLines(false);
        //Eje y
        YAxis yleft = getmChart().getAxisLeft();
        yleft.setDrawGridLines(false);
        yleft.setTextColor(Color.BLACK);
        yleft.setAxisMinimum(-1 * limite);
        yleft.setAxisMaximum(limite);
        YAxis yRight = getmChart().getAxisRight();
        yRight.setEnabled(false);
    }


    public LineDataSet createSet(List<Entry> a, String label, int color) {
        LineDataSet set = new LineDataSet(a, label);
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        set.setColor(color);
        set.setLineWidth(1f);
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
        //Graficar
        mChart.invalidate();

    }

    public void mostrarChart(float[] x, float[] y, float[] z) {
        List<Entry> xChart = new ArrayList<Entry>();
        List<Entry> yChart = new ArrayList<Entry>();
        List<Entry> zChart = new ArrayList<Entry>();

        for (int i = 0; i < x.length; i++) {
            xChart.add(new Entry(i, x[i]));
            yChart.add(new Entry(i, y[i]));
            zChart.add(new Entry(i, z[i]));
        }

        LineDataSet dataSet1 = createSet(xChart, "x", Color.parseColor("#00B0FF"));
        LineDataSet dataSet2 = createSet(yChart, "y", Color.parseColor("#D50000"));
        LineDataSet dataSet3 = createSet(zChart, "z", Color.parseColor("#6A1B9A"));
        List<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
        dataSets.add(dataSet1);
        dataSets.add(dataSet2);
        dataSets.add(dataSet3);
        Graficar(dataSets);
    }


    public LineChart getmChart() {
        return mChart;
    }

    public void setmChart(LineChart mChart) {
        this.mChart = mChart;
    }
}
