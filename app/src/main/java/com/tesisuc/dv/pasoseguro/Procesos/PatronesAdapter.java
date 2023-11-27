package com.tesisuc.dv.pasoseguro.Procesos;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tesisuc.dv.pasoseguro.Activities.ListadoPatrones;
import com.tesisuc.dv.pasoseguro.MainActivity;
import com.tesisuc.dv.pasoseguro.R;
import com.tesisuc.dv.pasoseguro.Graficar.Chart;
import java.util.ArrayList;

public class PatronesAdapter extends RecyclerView.Adapter<PatronesAdapter.MyViewHolder> {

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView titulo;
        public Chart chart;
        public Button borrar;
        public RelativeLayout chartLayout;

        public MyViewHolder(View view) {
            super(view);
            titulo = (TextView) view.findViewById(R.id.patrones_txt);
            borrar = (Button) view.findViewById(R.id.btn_patrones_borrar);
            chartLayout = (RelativeLayout) view.findViewById(R.id.ChartLayout);
            chart = new Chart(chartLayout, context, 4f, "");
        }
    }

    private ArrayList<Patron> list;
    private Context context;
    private SQLite sqlHelper;
    private String usuarioActual;
    private String nombreBD;
    private int numeroTablas;
    private SharedPreferences sp;
    private SharedPreferences.Editor editor;

    public PatronesAdapter(ArrayList<Patron> list, Context context, String usuarioActual, int numeroTablas){
        this.list = list;
        this.context = context;
        this.usuarioActual = usuarioActual;
        this.nombreBD = this.usuarioActual.replaceAll("\\s+","");
        this.numeroTablas = numeroTablas;
        sp = context.getSharedPreferences("Usuarios", Context.MODE_PRIVATE);
        editor = sp.edit();
        sqlHelper = new SQLite(context, this.nombreBD, null, 1);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.patrones_cardview, viewGroup, false);
        MyViewHolder viewHolder = new MyViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder myViewHolder, final int i) {
        myViewHolder.titulo.setText(list.get(i).getTitulo() + (i+1));
        myViewHolder.chart.mostrarChart(list.get(i).getX(),list.get(i).getY(),list.get(i).getZ());
        myViewHolder.borrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sqlHelper.eliminarTabla( nombreBD + "xyz" + i);
                for (int j = i+1; j < numeroTablas; j++) {
                    sqlHelper.renombrarTabla(nombreBD + "xyz" + j,nombreBD + "xyz" + (j-1));
                    myViewHolder.titulo.setText(list.get(j).getTitulo() + (j-1));
                    notifyItemChanged(j);
                }
                editor.putInt(usuarioActual, (sp.getInt(usuarioActual,1) -1));
                numeroTablas = numeroTablas - 1;
                editor.commit();
                list.remove(i);
               notifyItemRemoved(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

}
