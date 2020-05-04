package com.cudpast.appminas.Adapter;

import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cudpast.appminas.Model.DatosPersonal;
import com.cudpast.appminas.R;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class AdapterDatosPersonales extends RecyclerView.Adapter<AdapterDatosPersonales.MyViewHolder> {

    private Context mContext;
    private List<DatosPersonal> mData;

    public AdapterDatosPersonales(Context mContext, List<DatosPersonal> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }

    // Extends RecyclerView
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View row = LayoutInflater.from(mContext).inflate(R.layout.layout_raw_consulta_pesonal_item, parent, false);
        return new MyViewHolder(row);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        holder.tv_temperatura.setText(mData.get(position).getTempurature());
        holder.tv_satura.setText(mData.get(position).getSo2());
        holder.tv_pulso.setText(mData.get(position).getPulse());
        holder.tv_date.setText(mData.get(position).getDateRegister());

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    //Class Aux
    public class MyViewHolder extends RecyclerView.ViewHolder {

        LinearLayout rv_click_simtomas;
        TextView tv_temperatura, tv_satura, tv_pulso, tv_date;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            rv_click_simtomas = itemView.findViewById(R.id.rv_click_simtomas);
            tv_temperatura = itemView.findViewById(R.id.rv_temperatura);
            tv_satura = itemView.findViewById(R.id.rv_saturacion);
            tv_pulso = itemView.findViewById(R.id.rv_pulso);
            tv_date = itemView.findViewById(R.id.rv_date);
        }
    }

    private String timestampToString(long time) {
        Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
        calendar.setTimeInMillis(time);
        String date = DateFormat.format("dd-MM-yyyy", calendar).toString();
        return date;
    }
}
