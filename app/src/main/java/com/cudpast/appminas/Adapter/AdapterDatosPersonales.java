package com.cudpast.appminas.Adapter;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.cudpast.appminas.Model.DatosPersonal;
import com.cudpast.appminas.R;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class AdapterDatosPersonales extends RecyclerView.Adapter<AdapterDatosPersonales.MyViewHolder> {

    private Context mContext;
    private List<DatosPersonal> mData;
    private OnItemClickListener mListener;


    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }


    Dialog myDialog;

    public AdapterDatosPersonales(Context mContext, List<DatosPersonal> mData) {
        this.mContext = mContext;
        this.mData = mData;
        myDialog = new Dialog(mContext);
    }

    // Extends RecyclerView
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View row = LayoutInflater.from(mContext).inflate(R.layout.layout_raw_consulta_pesonal_item_v1, parent, false);
        MyViewHolder myViewHolder = new MyViewHolder(row, mListener);


        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {

        //temperatura
        holder.tv_temperatura.setText(mData.get(position).getTempurature());
        int valueTemp = Integer.parseInt(mData.get(position).getTempurature());
        if (valueTemp > 1) {
            holder.tv_temperatura.setTextColor(ContextCompat.getColor(mContext, R.color.rango_normal));
        }
        //Saturacion de Oxigenno
        holder.tv_satura.setText(mData.get(position).getSo2());
        int valueSatura = Integer.parseInt(mData.get(position).getSo2());
        if (valueSatura >= 95 && valueSatura <= 99) {
            holder.tv_satura.setTextColor(ContextCompat.getColor(mContext, R.color.rango_normal));
        } else if (valueSatura >= 91 && valueSatura <= 94) {
            holder.tv_satura.setTextColor(ContextCompat.getColor(mContext, R.color.rango_leve));
        } else if (valueSatura >= 86 && valueSatura <= 90) {
            holder.tv_satura.setTextColor(ContextCompat.getColor(mContext, R.color.rango_moderada));
        } else {
            holder.tv_satura.setTextColor(ContextCompat.getColor(mContext, R.color.rango_severa));
        }

        //Pulso
        holder.tv_pulso.setText(mData.get(position).getPulse());
        int valuePulso = Integer.parseInt(mData.get(position).getPulse());
        if (valuePulso >= 86) {
            holder.tv_pulso.setTextColor(ContextCompat.getColor(mContext, R.color.rango_excelente));
        } else if (valuePulso >= 70 && valuePulso <= 84) {
            holder.tv_pulso.setTextColor(ContextCompat.getColor(mContext, R.color.rango_bueno));
        } else if (valuePulso >= 62 && valuePulso <= 68) {
            holder.tv_pulso.setTextColor(ContextCompat.getColor(mContext, R.color.rango_adecuado));
        } else {
            holder.tv_pulso.setTextColor(ContextCompat.getColor(mContext, R.color.rango_inadecuado));
        }


        holder.tv_date.setText(mData.get(position).getDateRegister());


    }


    @Override
    public int getItemCount() {
        return mData.size();
    }

    //Class Aux
    public static class MyViewHolder extends RecyclerView.ViewHolder {

        LinearLayout rv_click_simtomas;
        TextView tv_temperatura, tv_satura, tv_pulso, tv_date;
        Button img_sintomas;

        public MyViewHolder(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);
            rv_click_simtomas = itemView.findViewById(R.id.rv_click_simtomas);
            tv_temperatura = itemView.findViewById(R.id.rv_temperatura);
            tv_satura = itemView.findViewById(R.id.rv_saturacion);
            tv_pulso = itemView.findViewById(R.id.rv_pulso);
            tv_date = itemView.findViewById(R.id.rv_date);
            img_sintomas = itemView.findViewById(R.id.img_sintomas);

            img_sintomas.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onItemClick(position);
                        }
                    }
                }
            });
        }
    }

    private String timestampToString(long time) {
        Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
        calendar.setTimeInMillis(time);
        String date = DateFormat.format("dd-MM-yyyy", calendar).toString();
        return date;
    }

}
