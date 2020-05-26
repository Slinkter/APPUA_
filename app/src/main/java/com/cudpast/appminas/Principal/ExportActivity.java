package com.cudpast.appminas.Principal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.Environment;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.cudpast.appminas.Activities.RegisterActivity;
import com.cudpast.appminas.Common.Common;
import com.cudpast.appminas.Model.MetricasPersonal;
import com.cudpast.appminas.Model.Personal;
import com.cudpast.appminas.R;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class ExportActivity extends AppCompatActivity {

    public static final String TAG = ExportActivity.class.getSimpleName();
    private FirebaseDatabase database = FirebaseDatabase.getInstance();

    private DatabaseReference ref_datos_paciente;
    private List<MetricasPersonal> listaMetricasPersonales;
    private List<Personal> listaPersonal;
    private Button btn_selectDate, btn_viewPdf, btn_viewPdfDni;
    private TextView tv_show_date;
    private MaterialDatePicker.Builder dialogCalendar;
    private MaterialDatePicker materialDatePicker;
    private String seletedDate;
    private ProgressDialog mDialog;

    private TextInputLayout dni_metrica_layout;
    private TextInputEditText dni_metrica;
    private TextView show_dni_metricas;


    List<String> listDate;
    List<String> listTemperatura;
    List<Integer> listSaturacion;
    List<Integer> listPulso;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setTitle("Regresar");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_export);
        //Solicitar permisos
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PackageManager.PERMISSION_GRANTED);
        //
        btn_selectDate = findViewById(R.id.btn_selectDate);
        btn_viewPdf = findViewById(R.id.btn_viewPdf);
        btn_viewPdfDni = findViewById(R.id.btn_viewPdfDni);
        //
        dni_metrica_layout = findViewById(R.id.dni_metrica_layout);
        dni_metrica = findViewById(R.id.dni_metrica);
        //

        tv_show_date = findViewById(R.id.tv_show_date);
        show_dni_metricas = findViewById(R.id.show_dni_metricas);
        //
        dialogCalendar = MaterialDatePicker.Builder.datePicker();
        dialogCalendar.setTitleText("Seleccionar Fecha");
        materialDatePicker = dialogCalendar.build();

        // Button 1
        btn_selectDate.setOnClickListener(v -> materialDatePicker.show(getSupportFragmentManager(), "DATE_PICKER"));

        materialDatePicker.addOnPositiveButtonClickListener(
                (MaterialPickerOnPositiveButtonClickListener<Long>) dateSelected -> {

                    mDialog = new ProgressDialog(ExportActivity.this);
                    mDialog.setMessage("Obteniendo datos ...");
                    mDialog.show();

                    tv_show_date.setVisibility(View.VISIBLE);
                    tv_show_date.setText("Fecha Selecionada :  " + timestampToString(dateSelected));
                    btn_viewPdf.setVisibility(View.VISIBLE);

                    seletedDate = timestampToString(dateSelected);

                    listaMetricasPersonales = new ArrayList<>();
                    listaPersonal = new ArrayList<>();

                    ref_datos_paciente = FirebaseDatabase.getInstance().getReference(Common.db_mina_personal_data).child(Common.unidadTrabajoSelected.getNameUT());
                    ref_datos_paciente.keepSynced(true);
                    ref_datos_paciente.orderByKey();
                    ref_datos_paciente
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    for (DataSnapshot snapshotDatosPersonales : dataSnapshot.getChildren()) {
                                        // --> DNI del Personal
                                        String dniPersonal = snapshotDatosPersonales.getKey();
                                        Log.e(TAG, "dniPersonal : " + dniPersonal);
                                        if (dniPersonal.isEmpty()) {

                                        } else {
                                            // --> Lista de snapshotDatosPersonales
                                            for (DataSnapshot item : snapshotDatosPersonales.getChildren()) {
                                                MetricasPersonal metricasPersonal = item.getValue(MetricasPersonal.class);
                                                if (metricasPersonal != null) {
                                                    String dateRegister = metricasPersonal.getDateRegister().substring(0, 10).trim();
                                                    Log.e(TAG, "120 dateRegister = " + dateRegister);
                                                    //Buscar dateRegister q coincide
                                                    if (dateRegister.equalsIgnoreCase(seletedDate)) {
                                                        // enlistar datos
                                                        listaMetricasPersonales.add(metricasPersonal);
                                                        // Buscar datos
                                                        DatabaseReference ref_db_mina_personal = database.getReference(Common.db_mina_personal);
                                                        DatabaseReference ref_mina = ref_db_mina_personal.child(Common.unidadTrabajoSelected.getNameUT());
                                                        ref_mina.child(dniPersonal).addListenerForSingleValueEvent(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                                mDialog.dismiss();
                                                                Personal personal = dataSnapshot.getValue(Personal.class);
                                                                if (personal != null) {
                                                                    listaPersonal.add(personal);
                                                                    Log.e(TAG, listaPersonal.size() + " personal : " + personal.getName());
                                                                    //  Log.e(TAG, "tamaño personal : " + listaPersonal.size());
                                                                }
                                                            }

                                                            @Override
                                                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                                                Log.e(TAG, "error : " + databaseError.getMessage());
                                                                mDialog.dismiss();
                                                            }
                                                        });

                                                    }

                                                    if (listaMetricasPersonales.size() != 0) {
                                                        mDialog.dismiss();
                                                        Log.e(TAG, "Lista ");
                                                        btn_viewPdf.setVisibility(View.VISIBLE);
                                                        tv_show_date.setText("Fecha Selecionada " + timestampToString(dateSelected));
                                                        tv_show_date.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary));
                                                    } else {
                                                        // Al no tener fecha no se debe visulizar boton de generar pdf
                                                        // en el textView mostrar lista vacia
                                                        btn_viewPdf.setVisibility(View.INVISIBLE);
                                                        tv_show_date.setText("Lista vacia , seleciona otra fecha");
                                                        tv_show_date.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.rango_inadecuado));
                                                        Log.e(TAG, "no existe fecha : lista vacia");
                                                        mDialog.dismiss();
                                                    }

                                                } else {
                                                    Log.e(TAG, "metricasPersonal : lista vacia");
                                                    mDialog.dismiss();

                                                }
                                            }


                                        }


                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                    Log.e(TAG, "error : " + databaseError.getMessage());
                                    mDialog.dismiss();
                                }

                            });

                    btn_viewPdf.setOnClickListener(
                            v -> {
                                try {
                                    if (listaMetricasPersonales.size() != 0) {
                                        generarListaporFechaPdf(listaMetricasPersonales, listaPersonal, seletedDate);
                                        mDialog.dismiss();
                                        Intent intent = new Intent(ExportActivity.this, ShowPdfActivity.class);
                                        startActivity(intent);
                                        Toast.makeText(this, "Documento Generado", Toast.LENGTH_SHORT).show();
                                    } else {
                                        mDialog.dismiss();
                                        Toast.makeText(this, "Lista vacia", Toast.LENGTH_SHORT).show();
                                    }


                                } catch (Exception e) {
                                    Toast.makeText(this, "Lista Vacio", Toast.LENGTH_SHORT).show();
                                    Log.e(TAG, "Lista vacia  " + e.getMessage());
                                    mDialog.dismiss();
                                }
                            });

                });

        // Button 2
        btn_viewPdfDni.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                consultarDatosPaciente();
            }
        });
    }


    private void generarListaporFechaPdf(List<MetricasPersonal> listMetricasPersonal, List<Personal> listPersonal, String seletedDate) {
        //
        Log.e(TAG, "listMetricasPersonal.size() : " + listMetricasPersonal.size());
        Log.e(TAG, "listPersonal.size() : " + listPersonal.size());
        int pageWidth = 1200;
        Date currentDate = new Date();
        //
        java.text.DateFormat dateFormat;
        //
        PdfDocument pdfDocument = new PdfDocument();
        Paint myPaint = new Paint();
        //
        PdfDocument.PageInfo myPageInfo01 = new PdfDocument.PageInfo.Builder(1200, 2010, 1).create();
        PdfDocument.Page myPage01 = pdfDocument.startPage(myPageInfo01);
        Canvas cansas01 = myPage01.getCanvas();
        //
        Paint title = new Paint();
        title.setTextSize(70);
        title.setTextAlign(Paint.Align.CENTER);
        title.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        title.setColor(Color.BLACK);
        cansas01.drawText("UNIDADES ARSI ", pageWidth / 2, 80, title);

        Paint fecha = new Paint();
        fecha.setTextSize(25f);
        fecha.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        fecha.setTextAlign(Paint.Align.RIGHT);
        dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        cansas01.drawText("FECHA DE CONSULTA ", pageWidth - 20, 60, fecha);
        fecha.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
        cansas01.drawText("" + dateFormat.format(currentDate), pageWidth - 80, 90, fecha);
        String fechapdf = dateFormat.format(currentDate);

        dateFormat = new SimpleDateFormat("HH:mm:ss");
        fecha.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        cansas01.drawText("HORA ", pageWidth - 100, 120, fecha);
        fecha.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
        cansas01.drawText("" + dateFormat.format(currentDate), pageWidth - 90, 150, fecha);

        //
        Paint info = new Paint();
        info.setTextSize(35f);
        info.setTextAlign(Paint.Align.LEFT);
        info.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
        info.setColor(Color.BLACK);
        cansas01.drawText("Responsable : " + Common.currentUser.getReg_name(), 20, 200, info);
        cansas01.drawText("Unidad de Trabajo : " + Common.unidadTrabajoSelected.getNameUT(), 20, 250, info);
        cansas01.drawText("Fecha de medición : " + seletedDate, 20, 300, info);

        // Encabezados
        myPaint.setStyle(Paint.Style.STROKE);
        myPaint.setStrokeWidth(2);
        myPaint.setTextSize(25f);
        cansas01.drawRect(20, 360, pageWidth - 20, 440, myPaint);
        //
        myPaint.setTextAlign(Paint.Align.LEFT);
        myPaint.setStyle(Paint.Style.FILL);

        cansas01.drawText("Nro.", 40, 415, myPaint);
        cansas01.drawText("DNI", 180, 415, myPaint);
        cansas01.drawText("NOMBRE", 390, 415, myPaint);
        cansas01.drawText("TEMPERATURA", 680, 415, myPaint);
        cansas01.drawText("SO2.", 930, 415, myPaint);
        cansas01.drawText("PULSO", 1070, 415, myPaint);

        cansas01.drawLine(140, 380, 140, 430, myPaint);
        cansas01.drawLine(340, 380, 340, 430, myPaint);
        cansas01.drawLine(660, 380, 660, 430, myPaint);
        cansas01.drawLine(880, 380, 880, 430, myPaint);
        cansas01.drawLine(1030, 380, 1030, 430, myPaint);
        //
        int ytext = 400;
        int ysum = 100;
        int ytextname = 400;
        int ysumname = 100;
        //

        Paint temp = new Paint();
        Paint so = new Paint();
        so.setTextSize(25f);
        Paint pulse = new Paint();
        pulse.setTextSize(25f);
        //
        if (listMetricasPersonal.size() <= 28) {
            //-------------------------------------------------------------------------------
            //---> Pagina 01 Pagina 01 : [0-28]
            //metricas
            for (int i = 0; i < listMetricasPersonal.size(); i++) {
                cansas01.drawText(i + ".", 50, ytext + ysum, myPaint);

                cansas01.drawText(listMetricasPersonal.get(i).getTempurature().toString(), 760, ytext + ysum, myPaint);

                int valueSatura = Integer.parseInt(listMetricasPersonal.get(i).getSo2().toString());
                if (valueSatura >= 95 && valueSatura <= 99) {
                    so.setColor(Color.rgb(17, 230, 165));
                } else if (valueSatura >= 91 && valueSatura <= 94) {
                    so.setColor(Color.rgb(255, 235, 59));
                } else if (valueSatura >= 86 && valueSatura <= 90) {
                    so.setColor(Color.rgb(255, 38, 38));
                } else {
                    so.setColor(Color.rgb(255, 38, 38));
                }

                cansas01.drawText(listMetricasPersonal.get(i).getSo2().toString(), 940, ytext + ysum, so);

                int valuePulso = Integer.parseInt(listMetricasPersonal.get(i).getPulse().toString());
                if (valuePulso >= 86) {
                    pulse.setColor(Color.rgb(17, 230, 165));
                } else if (valuePulso >= 70 && valuePulso <= 84) {
                    pulse.setColor(Color.rgb(255, 235, 59));
                } else if (valuePulso >= 62 && valuePulso <= 68) {
                    pulse.setColor(Color.rgb(255, 38, 38));
                } else {
                    pulse.setColor(Color.rgb(255, 38, 38));
                }
                cansas01.drawText(listMetricasPersonal.get(i).getPulse().toString(), 1090, ytext + ysum, pulse);
                ysum = ysum + 50;
            }
            // info trabajador
            for (int i = 0; i < listPersonal.size(); i++) {
                cansas01.drawText(listPersonal.get(i).getDni().toString(), 170, ytextname + ysumname, myPaint);
                cansas01.drawText(listPersonal.get(i).getName().toString(), 340, ytextname + ysumname, myPaint);
                ysumname = ysumname + 50;
            }

            pdfDocument.finishPage(myPage01);
            //---> Cierre
            File file = new File(Environment.getExternalStorageDirectory(), "/arsi21.pdf");
            try {
                pdfDocument.writeTo(new FileOutputStream(file));
            } catch (IOException e) {
                e.printStackTrace();
            }
            pdfDocument.close();
            //-------------------------------------------------------------------------------
        } else if (listMetricasPersonal.size() >= 27) {
            Toast.makeText(this, "pagina 2", Toast.LENGTH_SHORT).show();
            //-------------------------------------------------------------------------------
            //---> Pagina 01 : [0-30]
            for (int i = 0; i <= 27; i++) {
                Log.e(TAG, "pagina 01 " + i);
                cansas01.drawText(i + ".", 50, ytext + ysum, myPaint);
                cansas01.drawText(listMetricasPersonal.get(i).getTempurature().toString(), 760, ytext + ysum, myPaint);


                int valueSatura = Integer.parseInt(listMetricasPersonal.get(i).getSo2().toString());
                if (valueSatura >= 95 && valueSatura <= 99) {
                    so.setColor(Color.rgb(17, 230, 165));
                } else if (valueSatura >= 91 && valueSatura <= 94) {
                    so.setColor(Color.rgb(255, 235, 59));
                } else if (valueSatura >= 86 && valueSatura <= 90) {
                    so.setColor(Color.rgb(255, 38, 38));
                } else {
                    so.setColor(Color.rgb(255, 38, 38));
                }

                cansas01.drawText(listMetricasPersonal.get(i).getSo2().toString(), 940, ytext + ysum, so);

                int valuePulso = Integer.parseInt(listMetricasPersonal.get(i).getPulse().toString());
                if (valuePulso >= 86) {
                    pulse.setColor(Color.rgb(17, 230, 165));
                } else if (valuePulso >= 70 && valuePulso <= 84) {
                    pulse.setColor(Color.rgb(255, 235, 59));
                } else if (valuePulso >= 62 && valuePulso <= 68) {
                    pulse.setColor(Color.rgb(255, 38, 38));
                } else {
                    pulse.setColor(Color.rgb(255, 38, 38));
                }

                cansas01.drawText(listMetricasPersonal.get(i).getPulse().toString(), 1090, ytext + ysum, pulse);
                ysum = ysum + 55;
            }
            //
            for (int i = 0; i <= 27; i++) {
                cansas01.drawText(listPersonal.get(i).getDni().toString(), 170, ytextname + ysumname, myPaint);
                cansas01.drawText(listPersonal.get(i).getName().toString(), 340, ytextname + ysumname, myPaint);
                ysumname = ysumname + 55;
            }
            //
            pdfDocument.finishPage(myPage01);
            //-------------------------------------------------------------------------------
            //---> Pagina 02 : [28-70]
            PdfDocument.PageInfo myPageInfo2 = new PdfDocument.PageInfo.Builder(1200, 2010, 2).create();
            PdfDocument.Page myPage2 = pdfDocument.startPage(myPageInfo2);
            Canvas canvas2 = myPage2.getCanvas();

            int y2sum = 100;
            int x2sum = 100;
            int list2a = listMetricasPersonal.size();
            int list2b = listPersonal.size();
            for (int i = 28; i < list2a; i++) {
                Log.e(TAG, "error lista  position " + i);
                canvas2.drawText(i + ".", 50, 30 + y2sum, myPaint);

                canvas2.drawText(listMetricasPersonal.get(i).getTempurature().toString(), 760, 30 + y2sum, myPaint);

                int valueSatura = Integer.parseInt(listMetricasPersonal.get(i).getSo2().toString());
                if (valueSatura >= 95 && valueSatura <= 99) {
                    so.setColor(Color.rgb(17, 230, 165));
                } else if (valueSatura >= 91 && valueSatura <= 94) {
                    so.setColor(Color.rgb(255, 235, 59));
                } else if (valueSatura >= 86 && valueSatura <= 90) {
                    so.setColor(Color.rgb(255, 38, 38));
                } else {
                    so.setColor(Color.rgb(255, 38, 38));
                }
                canvas2.drawText(listMetricasPersonal.get(i).getSo2().toString(), 940, 30 + y2sum, so);

                int valuePulso = Integer.parseInt(listMetricasPersonal.get(i).getPulse().toString());
                if (valuePulso >= 86) {
                    pulse.setColor(Color.rgb(17, 230, 165));
                } else if (valuePulso >= 70 && valuePulso <= 84) {
                    pulse.setColor(Color.rgb(255, 235, 59));
                } else if (valuePulso >= 62 && valuePulso <= 68) {
                    pulse.setColor(Color.rgb(255, 38, 38));
                } else {
                    pulse.setColor(Color.rgb(255, 38, 38));
                }
                canvas2.drawText(listMetricasPersonal.get(i).getPulse().toString(), 1090, 30 + y2sum, pulse);
                y2sum = y2sum + 50;
            }
            //
            for (int i = 28; i < list2b; i++) {
                canvas2.drawText(listPersonal.get(i).getDni().toString(), 170, 30 + x2sum, myPaint);
                canvas2.drawText(listPersonal.get(i).getName().toString(), 340, 30 + x2sum, myPaint);
                x2sum = x2sum + 50;
            }


            pdfDocument.finishPage(myPage2);
            //---> Cierre
            File file = new File(Environment.getExternalStorageDirectory(), "/arsi21.pdf");
            try {
                pdfDocument.writeTo(new FileOutputStream(file));

            } catch (IOException e) {
                e.printStackTrace();

            }

            pdfDocument.close();

        } else {
            Log.e(TAG, "ERROR problemas de index");
        }


    }

    private void generarListaporPersonalPdf(String nombre) {

        Log.e(TAG, "---> generarListaporPersonalPdf()  ");
        Log.e(TAG, " listDate : " + listDate.size());
        Log.e(TAG, " listTemperatura : " + listTemperatura.size());
        Log.e(TAG, " listSaturacion : " + listSaturacion.size());
        Log.e(TAG, " listPulso : " + listPulso.size());
        int pageWidth = 1200;
        Date currentDate = new Date();
        //
        java.text.DateFormat dateFormat;
        //
        PdfDocument pdfDocument = new PdfDocument();
        Paint myPaint = new Paint();
        //
        PdfDocument.PageInfo myPageInfo01 = new PdfDocument.PageInfo.Builder(1200, 2010, 1).create();
        PdfDocument.Page myPage01 = pdfDocument.startPage(myPageInfo01);
        Canvas cansas01 = myPage01.getCanvas();
        //
        Paint title = new Paint();
        title.setTextSize(70);
        title.setTextAlign(Paint.Align.CENTER);
        title.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        title.setColor(Color.BLACK);
        cansas01.drawText("UNIDADES ARSI ", pageWidth / 2, 80, title);

        Paint fecha = new Paint();
        fecha.setTextSize(25f);
        fecha.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        fecha.setTextAlign(Paint.Align.RIGHT);
        dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        cansas01.drawText("FECHA DE CONSULTA ", pageWidth - 20, 60, fecha);
        fecha.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
        cansas01.drawText("" + dateFormat.format(currentDate), pageWidth - 80, 90, fecha);
        String fechapdf = dateFormat.format(currentDate);

        dateFormat = new SimpleDateFormat("HH:mm:ss");
        fecha.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        cansas01.drawText("HORA ", pageWidth - 100, 120, fecha);
        fecha.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
        cansas01.drawText("" + dateFormat.format(currentDate), pageWidth - 90, 150, fecha);

        //
        Paint info = new Paint();
        info.setTextSize(35f);
        info.setTextAlign(Paint.Align.LEFT);
        info.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
        info.setColor(Color.BLACK);
        cansas01.drawText("Trabajador  : " + nombre, 20, 200, info);
        cansas01.drawText("Unidad de Trabajo : " + Common.unidadTrabajoSelected.getNameUT(), 20, 250, info);
        cansas01.drawText("Responsable : " + Common.currentUser.getReg_name().toString(), 20, 300, info);

        // Encabezados
        myPaint.setStyle(Paint.Style.STROKE);
        myPaint.setStrokeWidth(2);
        myPaint.setTextSize(25f);
        cansas01.drawRect(20, 360, pageWidth - 20, 440, myPaint);
        //
        myPaint.setTextAlign(Paint.Align.LEFT);
        myPaint.setStyle(Paint.Style.FILL);

        cansas01.drawText("Nro.", 35, 415, myPaint);
        cansas01.drawText("Fecha y hora", 200, 415, myPaint);
        cansas01.drawText("TEMPERATURA", 450, 415, myPaint);
        cansas01.drawText("SO2", 700, 415, myPaint);
        cansas01.drawText("PULSO.", 950, 415, myPaint);


        cansas01.drawLine(140, 380, 140, 430, myPaint);
        cansas01.drawLine(420, 380, 420, 430, myPaint);
        cansas01.drawLine(660, 380, 660, 430, myPaint);
        cansas01.drawLine(880, 380, 880, 430, myPaint);

        //
        int ytext = 400;
        int ysum = 100;
        int ytextname = 400;
        int ysumname = 100;
        //

        Paint temp = new Paint();
        Paint so = new Paint();
        so.setTextSize(25f);
        Paint pulse = new Paint();
        pulse.setTextSize(25f);
        //


        //-------------------------------------------------------------------------------
        //---> Pagina 01 Pagina 01 : [0-28]
        //metricas
        double sumaTemp = 0;
        double promedioTemp = 0.0f;

        double sumaSatura = 0;
        double promedioSatura = 0.0f;


        double sumaPulse = 0;
        double promedioPulse = 0.0f;


        for (int i = 0; i < listDate.size(); i++) {
            // Nro
            cansas01.drawText(i + 1 + " ", 80, ytext + ysum, myPaint);
            // Temperatura
            cansas01.drawText(listTemperatura.get(i).toString(), 490, ytext + ysum, myPaint);
            sumaTemp = sumaTemp + Double.parseDouble(listTemperatura.get(i).toString());
            // Saturacion
            int valueSatura = Integer.parseInt(listSaturacion.get(i).toString());
            sumaSatura = sumaSatura + valueSatura;
            if (valueSatura >= 95 && valueSatura <= 99) {
                so.setColor(Color.rgb(17, 230, 165));
            } else if (valueSatura >= 91 && valueSatura <= 94) {
                so.setColor(Color.rgb(255, 235, 59));
            } else if (valueSatura >= 86 && valueSatura <= 90) {
                so.setColor(Color.rgb(255, 38, 38));
            } else {
                so.setColor(Color.rgb(255, 38, 38));
            }
            cansas01.drawText(listSaturacion.get(i).toString(), 720, ytext + ysum, so);
            // Pulse
            int valuePulso = Integer.parseInt(listPulso.get(i).toString());
            sumaPulse = sumaPulse + valuePulso;

            if (valuePulso >= 86) {
                pulse.setColor(Color.rgb(17, 230, 165));
            } else if (valuePulso >= 70 && valuePulso <= 84) {
                pulse.setColor(Color.rgb(255, 235, 59));
            } else if (valuePulso >= 62 && valuePulso <= 68) {
                pulse.setColor(Color.rgb(255, 38, 38));
            } else {
                pulse.setColor(Color.rgb(255, 38, 38));
            }
            cansas01.drawText(listPulso.get(i).toString(), 970, ytext + ysum, pulse);
            //
            ysum = ysum + 50;
        }
        // info trabajador

        for (int i = 0; i < listDate.size(); i++) {
            cansas01.drawText(listDate.get(i), 170, ytextname + ysumname, myPaint);
            ysumname = ysumname + 50;
        }

        promedioTemp = sumaTemp / listTemperatura.size();
        promedioSatura = sumaSatura / listTemperatura.size();
        promedioPulse = sumaPulse / listTemperatura.size();


        String cadTemp = String.valueOf(promedioTemp);
        String cadSatura = String.valueOf(promedioSatura);
        String cadPulse = String.valueOf(promedioPulse);

        cansas01.drawText("Promedio de los ultimos 15 días", 35, 1600, myPaint);
        cansas01.drawText("Promedio temperatura : " + cadTemp.substring(0, 5), 35, 1650, myPaint);
        cansas01.drawText("Promedio saturación  : " + cadSatura.substring(0, 5), 35, 1700, myPaint);
        cansas01.drawText("Promedio pulso : " + cadPulse.substring(0, 5), 35, 1750, myPaint);
        //-------------------------------------------------------------------------------

        pdfDocument.finishPage(myPage01);
        //---> Cierre
        File file = new File(Environment.getExternalStorageDirectory(), "/arsi21.pdf");
        try {
            pdfDocument.writeTo(new FileOutputStream(file));
        } catch (IOException e) {
            e.printStackTrace();
        }
        pdfDocument.close();


        //
        /*
        File filelocation = new File(Environment.getExternalStorageDirectory(), "/arsi21.pdf");
        Uri path = Uri.fromFile(filelocation);
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        // set the type to 'email'
        emailIntent.setType("vnd.android.cursor.dir/email");
        String to[] = {"luis.j.cueva@gmail.com"};
        emailIntent.putExtra(Intent.EXTRA_EMAIL, to);
        // the attachment
        emailIntent.putExtra(Intent.EXTRA_STREAM, path);
        // the mail subject
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Subject");
        startActivity(Intent.createChooser(emailIntent, "Send email..."));
*/
        //

        mDialog.dismiss();
        Intent intent = new Intent(ExportActivity.this, ShowPdfActivity.class);
        startActivity(intent);
    }

    private void consultarDatosPaciente() {
        Log.e(TAG, "---> consultarDatosPaciente");
        final String dni = dni_metrica.getText().toString();
        if (!dni.toString().isEmpty()) {
            mDialog = new ProgressDialog(ExportActivity.this);
            mDialog.setMessage("Obteniendo datos ...");
            mDialog.show();

            Log.e(TAG, "---> dni : " + dni);
            DatabaseReference ref_db_mina_personal = database.getReference(Common.db_mina_personal);
            DatabaseReference ref_mina = ref_db_mina_personal.child(Common.unidadTrabajoSelected.getNameUT());
            ref_mina.child(dni).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Personal personal = dataSnapshot.getValue(Personal.class);
                    if (personal != null) {
                        Log.e(TAG, "---> personal.getName() : " + personal.getName());
                        dni_metrica_layout.setError(null);
                        if (personal.getLast() == null) {
                            personal.setLast(" ");
                        }
                        show_dni_metricas.setText("Trabajador : " + personal.getName() + " " + personal.getLast());
                        String fullname = personal.getName() + " " + personal.getLast();
                        getDataFromFirebase(dni, fullname);

                    } else {
                        Log.e(TAG, "---> personal.getName() : null ");
                        dni_metrica_layout.setError("El trabajador no exsite en la base de datos");
                        show_dni_metricas.setText("");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.e(TAG, "error : " + databaseError.getMessage());
                }
            });
        } else {
            Log.e(TAG, "---> dni :  vacio");
        }
    }

    private void getDataFromFirebase(String dni, String nombre) {
        Log.e(TAG, "-----> funcion  : getDataFromFirebase");
        listDate = new ArrayList<String>();
        listTemperatura = new ArrayList<>();
        listSaturacion = new ArrayList<>();
        listPulso = new ArrayList<>();

        Query query = FirebaseDatabase
                .getInstance()
                .getReference(Common.db_mina_personal_data).child(Common.unidadTrabajoSelected.getNameUT())
                .child(dni)
                .orderByKey()
                .limitToLast(15);

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    MetricasPersonal metricasPersonal = snapshot.getValue(MetricasPersonal.class);
                    if (metricasPersonal != null) {
                        listDate.add(metricasPersonal.getDateRegister().toString());
                        listTemperatura.add((metricasPersonal.getTempurature()));
                        listSaturacion.add(Integer.parseInt(metricasPersonal.getSo2()));
                        listPulso.add(Integer.parseInt(metricasPersonal.getPulse()));

                    } else {
                        mDialog.dismiss();
                        Log.e(TAG, "getDataFromFirebase --> MetricasPersonal = NULL");
                    }
                }

                Log.e(TAG, "---> listDate : " + listDate.size());
                Log.e(TAG, "---> listTemperatura : " + listTemperatura.size());
                Log.e(TAG, "---> listSaturacion : " + listSaturacion.size());
                Log.e(TAG, "---> listPulso : " + listPulso.size());

                generarListaporPersonalPdf(nombre);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "error" + " : " + databaseError.getMessage());
            }
        });


    }

    private String timestampToString(long time) {
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
        calendar.setTimeInMillis(time);
        String date = DateFormat.format("yyyy-MM-dd", calendar).toString();
        return date;
    }

}
