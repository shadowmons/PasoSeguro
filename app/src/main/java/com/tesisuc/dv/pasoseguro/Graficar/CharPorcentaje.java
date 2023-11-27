package com.tesisuc.dv.pasoseguro.Graficar;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.widget.RelativeLayout;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.PieData;

public class CharPorcentaje {
    private PieChart mChart;


public CharPorcentaje(RelativeLayout layout, Context context, String text){
        setmChart(new PieChart(context));
        layout.addView(getmChart());
    PieData data = new PieData();
        getmChart().setUsePercentValues(true);
        getmChart().getDescription().setEnabled(false);
    ChartConfig(text, true, true, true, false, true, Color.WHITE, data);


    // entry label styling

    }

    private void ChartConfig(String text, boolean touch,
                             boolean drag, boolean scale, boolean grid, boolean pinch, int background, PieData data) {
        Description desc = new Description();
        desc.setText(text);
        desc.setTextSize(14f);
        getmChart().getDescription().setEnabled(true);
        getmChart().setDescription(desc);
        getmChart().setTouchEnabled(touch);
        getmChart().setBackgroundColor(background);
        mChart.setHorizontalFadingEdgeEnabled(true);
        getmChart().getLayoutParams().height = android.view.ViewGroup.LayoutParams.MATCH_PARENT;
        getmChart().getLayoutParams().width = android.view.ViewGroup.LayoutParams.MATCH_PARENT;
        getmChart().setEntryLabelColor(Color.BLACK);
        getmChart().setEntryLabelTypeface(Typeface.DEFAULT_BOLD);
        getmChart().setEntryLabelTextSize(16f);
        getmChart().setExtraOffsets(5, 5, 55, 5);
        getmChart().setDragDecelerationFrictionCoef(0.95f);
        getmChart().setDrawHoleEnabled(true);
        getmChart().setHoleColor(Color.WHITE);

        getmChart().setTransparentCircleColor(Color.WHITE);
        getmChart().setTransparentCircleAlpha(110);

        getmChart().setHoleRadius(30f);
        getmChart().setTransparentCircleRadius(41f);

        getmChart().setDrawCenterText(true);

        getmChart().setRotationAngle(0);
        // enable rotation of the chart by touch
        getmChart().setRotationEnabled(true);
        getmChart().setHighlightPerTapEnabled(true);
        Legend l = getmChart().getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l.setOrientation(Legend.LegendOrientation.VERTICAL);
        l.setDrawInside(false);
        l.setXEntrySpace(7f);
        l.setTextSize(16f);
        l.setYEntrySpace(0f);
        l.setYOffset(0f);
    }

    public PieChart getmChart() {
        return mChart;
    }

    public void setmChart(PieChart mChart) {
        this.mChart = mChart;
    }
}
