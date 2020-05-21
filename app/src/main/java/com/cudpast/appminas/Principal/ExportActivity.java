package com.cudpast.appminas.Principal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.pdf.PdfDocument;
import android.os.Bundle;
import android.os.Environment;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.cudpast.appminas.Common.Common;
import com.cudpast.appminas.Model.MetricasPersonal;
import com.cudpast.appminas.Model.Personal;
import com.cudpast.appminas.R;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
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
    Button btn_export_date;
    TextView tv_fecha;

    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private Personal personal;
    private DatabaseReference ref_datos_paciente;
    private List<MetricasPersonal> listaMetricasPersonales;
    private List<Personal> listaPersonal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_export);

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PackageManager.PERMISSION_GRANTED);
        btn_export_date = findViewById(R.id.btn_export_date);
        tv_fecha = findViewById(R.id.fecha);

        MaterialDatePicker.Builder builder = MaterialDatePicker.Builder.datePicker();
        builder.setTitleText("Seleccionar Fecha");
        final MaterialDatePicker materialDatePicker = builder.build();
        btn_export_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                materialDatePicker.show(getSupportFragmentManager(), "DATE_PICKER");
            }
        });

        materialDatePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Long>() {
            @Override
            public void onPositiveButtonClick(Long selection) {
                // Log.e("DATE", "selection  : " + selection.toString());
                // Log.e("DATE", "timestampToString  : " + timestampToString(selection));
                // Log.e("DATE", "materialDatePicker.getSelection().toString()   : " + materialDatePicker.getSelection().toString());
                // Log.e("DATE", "materialDatePicker.getHeaderText().toString()  : " + materialDatePicker.getHeaderText().toString());
                tv_fecha.setText("fecha  " + timestampToString(selection));
                final String seletedDate = timestampToString(selection);


                ref_datos_paciente = FirebaseDatabase.getInstance().getReference(Common.db_mina_personal_data).child(Common.unidadTrabajoSelected.getNameUT());
                ref_datos_paciente.keepSynced(true);
                ref_datos_paciente.orderByKey();
                ref_datos_paciente.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        listaMetricasPersonales = new ArrayList<>();

                        for (DataSnapshot snapshotDatosPersonales : dataSnapshot.getChildren()) {
                            try {
                                // <-- DNI del Personal
                                String dniPersonal = snapshotDatosPersonales.getKey();
                                // --> Lista de snapshotDatosPersonales
                                listaPersonal = new ArrayList<>();
                                for (DataSnapshot item : snapshotDatosPersonales.getChildren()) {
                                    //
                                    MetricasPersonal metricasPersonal = item.getValue(MetricasPersonal.class);
                                    if (metricasPersonal != null) {
                                        String dateRegister = metricasPersonal.getDateRegister().substring(0, 10).trim();
                                        //Buscar dateRegister q coincide
                                        if (dateRegister.equalsIgnoreCase(seletedDate)) {
                                            // LOG
                                            Log.e(TAG, "----------> Fecha : " + dateRegister + " ");
                                            Log.e(TAG, "getSymptoms = " + metricasPersonal.getSymptoms());
                                            Log.e(TAG, "getTempurature = " + metricasPersonal.getTempurature());
                                            Log.e(TAG, "getSo2 = " + metricasPersonal.getSo2());
                                            Log.e(TAG, "getDateRegister = " + metricasPersonal.getDateRegister());
                                            Log.e(TAG, "getPulse = " + metricasPersonal.getPulse());
                                            Log.e(TAG, "getWho_user_register= " + metricasPersonal.getWho_user_register());
                                            //Si la dateRegister coincide enlistar datos
                                            listaMetricasPersonales.add(metricasPersonal);
                                            // Buscar datos
                                            DatabaseReference ref_db_mina_personal = database.getReference(Common.db_mina_personal);
                                            DatabaseReference ref_mina = ref_db_mina_personal.child(Common.unidadTrabajoSelected.getNameUT());
                                            //
                                            ref_mina.child(dniPersonal).addValueEventListener(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                    personal = dataSnapshot.getValue(Personal.class);
                                                    if (personal != null) {
                                                        listaPersonal.add(personal);
                                                        Log.e(TAG, "nombre : " + personal.getName() + " ");
                                                    }
                                                    generarPdf(listaMetricasPersonales, listaPersonal, seletedDate);
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                                    Log.e(TAG, "error : " + databaseError.getMessage());
                                                }
                                            });
                                            //  generarPdf(listaMetricasPersonales, listaPersonal, seletedDate);
                                        }
                                    }
                                    //    generarPdf(listaMetricasPersonales, listaPersonal, seletedDate);
                                }
                                //  generarPdf(listaMetricasPersonales, listaPersonal, seletedDate);
                            } catch (Exception e) {
                                e.getMessage();
                            }

                        }


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.e(TAG, "error : " + databaseError.getMessage());
                    }


                });


            }
        });


    }

    private String timestampToString(long time) {
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
        calendar.setTimeInMillis(time);
        String date = DateFormat.format("yyyy-MM-dd", calendar).toString();
        return date;
    }


    private void generarPdf(List<MetricasPersonal> metricasPersonal, List<Personal> personal, String seletedDate) {
        //
        Log.e(TAG, "metricasPersonal.size() : " + metricasPersonal.size());
        Log.e(TAG, "personal.size() : " + personal.size());
        int pageWidth = 1200;
        Date currentDate = new Date();

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
        myPaint.setColor(Color.rgb(0, 113, 188));
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


        int ytext = 400;
        int ysum = 100;

        Log.e(TAG, "personal.get(0).getName()) : " + personal.get(0).getName());

        for (int i = 0; i < metricasPersonal.size(); i++) {
            Log.e(TAG, "metricasPersonal = " + metricasPersonal.get(i).getNamepaciente());
            cansas01.drawText(i + ".", 50, ytext + ysum, myPaint);
            cansas01.drawText("", 170, ytext + ysum, myPaint);
            //       cansas01.drawText(personal.get(i).getName().toString(), 340, ytext + ysum, myPaint);
            cansas01.drawText(metricasPersonal.get(i).getTempurature().toString(), 760, ytext + ysum, myPaint);
            cansas01.drawText(metricasPersonal.get(i).getSo2().toString(), 940, ytext + ysum, myPaint);
            cansas01.drawText(metricasPersonal.get(i).getPulse().toString(), 1090, ytext + ysum, myPaint);
            ysum = ysum + 80;
        }

        int ytextname = 400;
        int ysumname = 100;
        for (int i = 0; i < personal.size(); i++) {
            cansas01.drawText(personal.get(i).getName().toString(), 340, ytextname + ysumname, myPaint);
            ysumname = ysumname + 80;
        }


        pdfDocument.finishPage(myPage01);
        File file = new File(Environment.getExternalStorageDirectory(), "/arsi.pdf");
        try {
            pdfDocument.writeTo(new FileOutputStream(file));
        } catch (IOException e) {
            e.printStackTrace();
        }
        pdfDocument.close();


    }


}
