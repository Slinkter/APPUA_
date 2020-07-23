package com.cudpast.appminas.Principal.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.cudpast.appminas.Common.Common;
import com.cudpast.appminas.Model.MetricasPersonal;
import com.cudpast.appminas.Model.Personal;
import com.cudpast.appminas.Principal.Activities.Support.ShowPdfActivity;
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
import java.util.Objects;
import java.util.TimeZone;

public class ReportDataWorkerActivity extends AppCompatActivity {

    public static final String TAG = ReportDataWorkerActivity.class.getSimpleName();
    //
    private FirebaseDatabase database;
    private DatabaseReference ref_datos_paciente;
    private List<MetricasPersonal> listaMetricasPersonales;
    private List<Personal> listaPersonal;
    private MaterialDatePicker mdp;
    private MaterialDatePicker.Builder builder;
    //
    private String seletedDate;
    private ProgressDialog mDialog;
    private ImageView img_reportdatepdf, img_reportmailpdf, img_reportworkpdf, img_reportworkgmail, img_reportexampdf, img_reportexamemail;
    //
    List<String> listDate;
    List<String> listTemperatura;
    List<Integer> listSaturacion;
    List<Integer> listPulso;
    //

    private List<MetricasPersonal> listtemp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setTitle("Regresar");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_export);
        //Solicitar permisos
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PackageManager.PERMISSION_GRANTED);
        //xml
        img_reportdatepdf = findViewById(R.id.img_reportdatepdf);
        img_reportmailpdf = findViewById(R.id.img_reportmailpdf);
        img_reportworkpdf = findViewById(R.id.img_reportworkpdf);
        img_reportworkgmail = findViewById(R.id.img_reportworkgmail);
        img_reportexampdf = findViewById(R.id.img_reportexampdf);
        img_reportexamemail = findViewById(R.id.img_reportexamemail);
        // Report 1
        img_reportdatepdf.setOnClickListener(v -> showSelectDate("pdf"));
        img_reportmailpdf.setOnClickListener(v -> showSelectDate("email"));
        // Report 2
        img_reportworkpdf.setOnClickListener(v -> showPdfDialog("pdf"));
        img_reportworkgmail.setOnClickListener(v -> showEmailoDialog("email"));
        // Report 3
        img_reportexampdf.setOnClickListener(v -> showTestEmailDNI("pdf"));
        img_reportexamemail.setOnClickListener(v -> showTestEmailDNI("email"));
        //Firebaase

        database = FirebaseDatabase.getInstance();

    }

    // Bloque 1
    private void showSelectDate(String metodo) {

        builder = MaterialDatePicker.Builder.datePicker();
        builder.setTitleText("Seleccionar Fecha");
        mdp = builder.build();
        mdp.show(getSupportFragmentManager(), "DATE_PICKER");
        mdp.addOnPositiveButtonClickListener((MaterialPickerOnPositiveButtonClickListener<Long>) input_dateSelected -> {
            //
            mDialog = new ProgressDialog(ReportDataWorkerActivity.this);
            mDialog.setMessage("Obteniendo datos ...");
            mDialog.show();
            // Init arrays
            listaMetricasPersonales = new ArrayList<>();
            listaPersonal = new ArrayList<>();
            // Transform date selected
            seletedDate = timeStampToString(input_dateSelected);
            // Get Data From Firebase and init reference
            ref_datos_paciente = database.getReference(Common.db_mina_personal_data).child(Common.unidadTrabajoSelected.getNameUT());
            ref_datos_paciente.keepSynced(true);
            ref_datos_paciente.orderByKey();
            ref_datos_paciente.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    filtrarFecha(dataSnapshot, metodo);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.e(TAG, "[showSelectDate ] error : " + databaseError.getMessage());
                    mDialog.dismiss();
                }
            });

        });
    }

    private void filtrarFecha(DataSnapshot dataAll, String metodo) {

        ArrayList<String> listDNI = new ArrayList<>();
        //
        for (DataSnapshot snapshot : dataAll.getChildren()) { //<--- all data
            if (snapshot != null) {
                String dni = snapshot.getKey();// <-- Get key ( dni worker)
                for (DataSnapshot item_date : snapshot.getChildren()) {  // <-- Todas la metricas por fechas de 1 persona
                    if (item_date != null) {
                        // Check Date , si coincide la fecha guardar data metrica y dni
                        String registerDate = Objects.requireNonNull(item_date.getKey()).substring(0, 10).trim();
                        boolean checkdate = seletedDate.equalsIgnoreCase(registerDate);
                        if (checkdate) {
                            MetricasPersonal data = item_date.getValue(MetricasPersonal.class);
                            listaMetricasPersonales.add(data);
                            listDNI.add(dni);
                        }
                    }
                }
                Log.e(TAG, "[onDataChange] dni = " + dni);
            } else {
                Log.e(TAG, "no hay snaphot = " + snapshot);
            }
        }
        Log.e(TAG, "[onDataChange] listaMetricasPersonales.size() = " + listaMetricasPersonales.size());
        if (listDNI.size() == 0) {
            mDialog.dismiss();
            Log.e(TAG, " No hay datos para esta fecha");
            Toast.makeText(this, "No hay datos para esta fecha", Toast.LENGTH_SHORT).show();
        }

        for (int i = 0; i < listDNI.size(); i++) {

            String dni = listDNI.get(i);
            DatabaseReference ref_mina = database.getReference(Common.db_mina_personal).child(Common.unidadTrabajoSelected.getNameUT()).child(dni);
            ref_mina
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            Personal personal = dataSnapshot.getValue(Personal.class);
                            if (personal != null) {
                                listaPersonal.add(personal);
                            }
                            if (listaPersonal.size() == listDNI.size()) {
                                generarListaporFechaPdf(listaMetricasPersonales, listaPersonal, seletedDate, metodo);
                            }
                            //
                            Log.e(TAG, "[filtrarFecha]-onDataChange  listaPersonal.size()  : " + listaPersonal.size());
                            Log.e(TAG, "[filtrarFecha]-onDataChange  listDNI.size()  : " + listDNI.size());
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Log.e(TAG, "[filtrarFecha]-onCancelled databaseError : " + databaseError.getMessage());
                        }
                    });

        }
        Log.e(TAG, "[filtrarFecha] listDNI = " + listDNI.size());
        Log.e(TAG, "[filtrarFecha] listaMetricasPersonales =" + listaMetricasPersonales.size());
    }

    private void generarListaporFechaPdf(List<MetricasPersonal> listMetricasPersonal, List<Personal> listPersonal, String seletedDate, String metodo) {
        Log.e(TAG, "[generarListaporFechaPdf]");
        Log.e(TAG, "[generarListaporFechaPdf]-listMetricasPersonal.size() : " + listMetricasPersonal.size());
        Log.e(TAG, "[generarListaporFechaPdf]-listPersonal.size() : " + listPersonal.size());
        if (listPersonal.size() >= 1) {
            if (listMetricasPersonal.size() == listPersonal.size()) {
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
                title.setTextSize(60);

                title.setTextAlign(Paint.Align.CENTER);
                title.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
                title.setColor(Color.BLACK);
                cansas01.drawText("UNIDAD DE TRABAJO", pageWidth / 2, 80, title);
                cansas01.drawText(Common.unidadTrabajoSelected.getAliasUT(), pageWidth / 2, 150, title);
                Paint fecha = new Paint();
                fecha.setTextSize(25f);
                fecha.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
                fecha.setTextAlign(Paint.Align.RIGHT);
                dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                cansas01.drawText("FECHA DE REPORTE ", pageWidth - 10, 60, fecha);
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
                cansas01.drawText("Responsable : " + Common.currentUser.getReg_name(), 20, 240, info);
                cansas01.drawText("Fecha consulta : " + seletedDate, 20, 300, info);

                // Encabezados
                myPaint.setStyle(Paint.Style.STROKE);
                myPaint.setStrokeWidth(2);
                myPaint.setTextSize(25f);
                cansas01.drawRect(20, 360, pageWidth - 20, 440, myPaint);
                //
                myPaint.setTextAlign(Paint.Align.LEFT);
                myPaint.setStyle(Paint.Style.FILL);

                cansas01.drawText("Nro.", 50, 415, myPaint);
                cansas01.drawText("DNI", 200, 415, myPaint);
                cansas01.drawText("NOMBRES Y APELLIDOS", 330, 415, myPaint);
                cansas01.drawText("TEMPERATURA", 680, 415, myPaint);
                cansas01.drawText("SO2.", 930, 415, myPaint);
                cansas01.drawText("PULSO", 1070, 415, myPaint);

                cansas01.drawLine(140, 380, 140, 430, myPaint);
                cansas01.drawLine(300, 380, 300, 430, myPaint);
                cansas01.drawLine(650, 380, 650, 430, myPaint);
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
                        //Numeracion
                        cansas01.drawText(i + 1 + ".", 80, ytext + ysum, myPaint);
                        // temperatura
                        cansas01.drawText(listMetricasPersonal.get(i).getTempurature(), 760, ytext + ysum, myPaint);
                        //Saturacion
                        int valueSatura = Integer.parseInt(listMetricasPersonal.get(i).getSo2());
                        if (valueSatura >= 95 && valueSatura <= 99) {
                            so.setColor(Color.rgb(17, 230, 165));
                        } else if (valueSatura >= 91 && valueSatura <= 94) {
                            so.setColor(Color.rgb(255, 235, 59));
                        } else if (valueSatura >= 86 && valueSatura <= 90) {
                            so.setColor(Color.rgb(255, 38, 38));
                        } else {
                            so.setColor(Color.rgb(255, 38, 38));
                        }
                        cansas01.drawText(listMetricasPersonal.get(i).getSo2(), 940, ytext + ysum, so);
                        //Pulso
                        int valuePulso = Integer.parseInt(listMetricasPersonal.get(i).getPulse());
                        if (valuePulso >= 86) {
                            pulse.setColor(Color.rgb(17, 230, 165));
                        } else if (valuePulso >= 70 && valuePulso <= 84) {
                            pulse.setColor(Color.rgb(255, 235, 59));
                        } else if (valuePulso >= 62 && valuePulso <= 68) {
                            pulse.setColor(Color.rgb(255, 38, 38));
                        } else {
                            pulse.setColor(Color.rgb(255, 38, 38));
                        }
                        cansas01.drawText(listMetricasPersonal.get(i).getPulse(), 1090, ytext + ysum, pulse);
                        //Aumentar
                        ysum = ysum + 50;
                    }
                    // info trabajador
                    for (int i = 0; i < listPersonal.size(); i++) {
                        cansas01.drawText(listPersonal.get(i).getDni(), 170, ytextname + ysumname, myPaint);
                        if (listPersonal.get(i).getLast() == null) {
                            listPersonal.get(i).setLast("");
                        }
                        cansas01.drawText(listPersonal.get(i).getLast() + " " + listPersonal.get(i).getName(), 340, ytextname + ysumname, myPaint);
                        ysumname = ysumname + 50;
                    }
                    //
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
                } else if (listMetricasPersonal.size() >= 29 && listMetricasPersonal.size() <= 63) {
                    Toast.makeText(this, "pagina 2", Toast.LENGTH_SHORT).show();
                    //-------------------------------------------------------------------------------
                    //---> Pagina 01-02 : [0-28]
                    for (int i = 0; i <= 28; i++) {
                        Log.e(TAG, "pagina 01 " + i);
                        cansas01.drawText(i + 1 + ".", 60, ytext + ysum, myPaint);
                        cansas01.drawText(listMetricasPersonal.get(i).getTempurature(), 760, ytext + ysum, myPaint);


                        int valueSatura = Integer.parseInt(listMetricasPersonal.get(i).getSo2());
                        if (valueSatura >= 95 && valueSatura <= 99) {
                            so.setColor(Color.rgb(17, 230, 165));
                        } else if (valueSatura >= 91 && valueSatura <= 94) {
                            so.setColor(Color.rgb(255, 235, 59));
                        } else if (valueSatura >= 86 && valueSatura <= 90) {
                            so.setColor(Color.rgb(255, 38, 38));
                        } else {
                            so.setColor(Color.rgb(255, 38, 38));
                        }

                        cansas01.drawText(listMetricasPersonal.get(i).getSo2(), 940, ytext + ysum, so);

                        int valuePulso = Integer.parseInt(listMetricasPersonal.get(i).getPulse());
                        if (valuePulso >= 86) {
                            pulse.setColor(Color.rgb(17, 230, 165));
                        } else if (valuePulso >= 70 && valuePulso <= 84) {
                            pulse.setColor(Color.rgb(255, 235, 59));
                        } else if (valuePulso >= 62 && valuePulso <= 68) {
                            pulse.setColor(Color.rgb(255, 38, 38));
                        } else {
                            pulse.setColor(Color.rgb(255, 38, 38));
                        }

                        cansas01.drawText(listMetricasPersonal.get(i).getPulse(), 1090, ytext + ysum, pulse);
                        ysum = ysum + 55;
                    }
                    //
                    for (int i = 0; i <= 28; i++) {
                        cansas01.drawText(listPersonal.get(i).getDni(), 170, ytextname + ysumname, myPaint);
                        cansas01.drawText(listPersonal.get(i).getLast() + " " + listPersonal.get(i).getName(), 340, ytextname + ysumname, myPaint);
                        ysumname = ysumname + 55;
                    }
                    //
                    pdfDocument.finishPage(myPage01);
                    //-------------------------------------------------------------------------------
                    //---> Pagina 02 : [29-63]
                    PdfDocument.PageInfo myPageInfo2 = new PdfDocument.PageInfo.Builder(1200, 2010, 2).create();
                    PdfDocument.Page myPage2 = pdfDocument.startPage(myPageInfo2);
                    Canvas canvas2 = myPage2.getCanvas();

                    int y2sum = 100;
                    int x2sum = 100;
                    int list2a = listMetricasPersonal.size();
                    int list2b = listPersonal.size();
                    for (int i = 29; i < list2a; i++) {
                        Log.e(TAG, "pagina 02  position " + i);
                        canvas2.drawText(i + ".", 50, 30 + y2sum, myPaint);

                        canvas2.drawText(listMetricasPersonal.get(i).getTempurature(), 760, 30 + y2sum, myPaint);
                        int valueSatura = Integer.parseInt(listMetricasPersonal.get(i).getSo2());
                        if (valueSatura >= 95 && valueSatura <= 99) {
                            so.setColor(Color.rgb(17, 230, 165));
                        } else if (valueSatura >= 91 && valueSatura <= 94) {
                            so.setColor(Color.rgb(255, 235, 59));
                        } else if (valueSatura >= 86 && valueSatura <= 90) {
                            so.setColor(Color.rgb(255, 38, 38));
                        } else {
                            so.setColor(Color.rgb(255, 38, 38));
                        }
                        canvas2.drawText(listMetricasPersonal.get(i).getSo2(), 940, 30 + y2sum, so);

                        int valuePulso = Integer.parseInt(listMetricasPersonal.get(i).getPulse());
                        if (valuePulso >= 86) {
                            pulse.setColor(Color.rgb(17, 230, 165));
                        } else if (valuePulso >= 70 && valuePulso <= 84) {
                            pulse.setColor(Color.rgb(255, 235, 59));
                        } else if (valuePulso >= 62 && valuePulso <= 68) {
                            pulse.setColor(Color.rgb(255, 38, 38));
                        } else {
                            pulse.setColor(Color.rgb(255, 38, 38));
                        }
                        canvas2.drawText(listMetricasPersonal.get(i).getPulse(), 1090, 30 + y2sum, pulse);
                        y2sum = y2sum + 50;
                    }
                    //
                    for (int i = 29; i < list2b; i++) {
                        canvas2.drawText(listPersonal.get(i).getDni(), 170, 30 + x2sum, myPaint);
                        canvas2.drawText(listPersonal.get(i).getLast() + " " + listPersonal.get(i).getName() , 340, 30 + x2sum, myPaint);
                        x2sum = x2sum + 50;
                    }
                    //
                    pdfDocument.finishPage(myPage2);
                    //---> Cierre
                    File file = new File(Environment.getExternalStorageDirectory(), "/arsi21.pdf");
                    try {
                        pdfDocument.writeTo(new FileOutputStream(file));

                    } catch (IOException e) {
                        Log.e(TAG, "ERROR - PAGE 02-02 : " + e.getMessage());
                    }
                    pdfDocument.close();
                } else if (listMetricasPersonal.size() >= 67 && listMetricasPersonal.size() <= 90) {
                    //-------------------------------------------------------------------------------
                    //---> Pagina 01-03 : [0-27]
                    for (int i = 0; i <= 28; i++) {
                        Log.e(TAG, "pagina 01 " + i);
                        cansas01.drawText(i + 1 + ".", 50, ytext + ysum, myPaint);
                        cansas01.drawText(listMetricasPersonal.get(i).getTempurature(), 760, ytext + ysum, myPaint);
                        int valueSatura = Integer.parseInt(listMetricasPersonal.get(i).getSo2());
                        if (valueSatura >= 95 && valueSatura <= 99) {
                            so.setColor(Color.rgb(17, 230, 165));
                        } else if (valueSatura >= 91 && valueSatura <= 94) {
                            so.setColor(Color.rgb(255, 235, 59));
                        } else if (valueSatura >= 86 && valueSatura <= 90) {
                            so.setColor(Color.rgb(255, 38, 38));
                        } else {
                            so.setColor(Color.rgb(255, 38, 38));
                        }
                        cansas01.drawText(listMetricasPersonal.get(i).getSo2(), 940, ytext + ysum, so);
                        //
                        int valuePulso = Integer.parseInt(listMetricasPersonal.get(i).getPulse());
                        if (valuePulso >= 86) {
                            pulse.setColor(Color.rgb(17, 230, 165));
                        } else if (valuePulso >= 70 && valuePulso <= 84) {
                            pulse.setColor(Color.rgb(255, 235, 59));
                        } else if (valuePulso >= 62 && valuePulso <= 68) {
                            pulse.setColor(Color.rgb(255, 38, 38));
                        } else {
                            pulse.setColor(Color.rgb(255, 38, 38));
                        }

                        cansas01.drawText(listMetricasPersonal.get(i).getPulse(), 1090, ytext + ysum, pulse);
                        ysum = ysum + 55;
                    }
                    //
                    for (int i = 0; i <= 28; i++) {
                        cansas01.drawText(listPersonal.get(i).getDni(), 170, ytextname + ysumname, myPaint);
                        cansas01.drawText(listPersonal.get(i).getName(), 340, ytextname + ysumname, myPaint);
                        ysumname = ysumname + 55;
                    }
                    //
                    pdfDocument.finishPage(myPage01);
                    //-------------------------------------------------------------------------------
                    //---> Pagina 02-03 : [28-66]
                    PdfDocument.PageInfo myPageInfo2 = new PdfDocument.PageInfo.Builder(1200, 2010, 2).create();
                    PdfDocument.Page myPage2 = pdfDocument.startPage(myPageInfo2);
                    Canvas canvas2 = myPage2.getCanvas();

                    int y2sum = 100;
                    int x2sum = 100;
                    int list2a = 63;
                    int list2b = 63;

                    for (int i = 28; i < list2a; i++) {
                        //
                        canvas2.drawText(i + ".", 50, 30 + y2sum, myPaint);
                        //
                        canvas2.drawText(listMetricasPersonal.get(i).getTempurature(), 760, 30 + y2sum, myPaint);
                        //
                        int valueSatura = Integer.parseInt(listMetricasPersonal.get(i).getSo2());
                        if (valueSatura >= 95 && valueSatura <= 99) {
                            so.setColor(Color.rgb(17, 230, 165));
                        } else if (valueSatura >= 91 && valueSatura <= 94) {
                            so.setColor(Color.rgb(255, 235, 59));
                        } else if (valueSatura >= 86 && valueSatura <= 90) {
                            so.setColor(Color.rgb(255, 38, 38));
                        } else {
                            so.setColor(Color.rgb(255, 38, 38));
                        }
                        canvas2.drawText(listMetricasPersonal.get(i).getSo2(), 940, 30 + y2sum, so);
                        //
                        int valuePulso = Integer.parseInt(listMetricasPersonal.get(i).getPulse());
                        if (valuePulso >= 86) {
                            pulse.setColor(Color.rgb(17, 230, 165));
                        } else if (valuePulso >= 70 && valuePulso <= 84) {
                            pulse.setColor(Color.rgb(255, 235, 59));
                        } else if (valuePulso >= 62 && valuePulso <= 68) {
                            pulse.setColor(Color.rgb(255, 38, 38));
                        } else {
                            pulse.setColor(Color.rgb(255, 38, 38));
                        }
                        canvas2.drawText(listMetricasPersonal.get(i).getPulse(), 1090, 30 + y2sum, pulse);
                        y2sum = y2sum + 50;
                    }
                    //
                    for (int i = 28; i < list2b; i++) {
                        canvas2.drawText(listPersonal.get(i).getDni(), 170, 30 + x2sum, myPaint);
                        canvas2.drawText(listPersonal.get(i).getName(), 340, 30 + x2sum, myPaint);
                        x2sum = x2sum + 50;
                    }
                    //
                    pdfDocument.finishPage(myPage2);
                    //-------------------------------------------------------------------------------
                    //---> Pagina 03-03 [66-90]
                    PdfDocument.PageInfo myPageInfo3 = new PdfDocument.PageInfo.Builder(1200, 2010, 3).create();
                    PdfDocument.Page myPage3 = pdfDocument.startPage(myPageInfo3);
                    Canvas canvas3 = myPage3.getCanvas();
                    //
                    int y3sum = 100;
                    int x3sum = 100;
                    int list3a = listMetricasPersonal.size();
                    int list3b = listPersonal.size();

                    for (int i = 64; i < list3a; i++) {
                        //numeracion
                        canvas3.drawText(i + ".", 50, 30 + y3sum, myPaint);
                        //temperatura
                        canvas3.drawText(listMetricasPersonal.get(i).getTempurature(), 760, 30 + y3sum, myPaint);
                        //
                        int valueSatura = Integer.parseInt(listMetricasPersonal.get(i).getSo2());
                        if (valueSatura >= 95 && valueSatura <= 99) {
                            so.setColor(Color.rgb(17, 230, 165));
                        } else if (valueSatura >= 91 && valueSatura <= 94) {
                            so.setColor(Color.rgb(255, 235, 59));
                        } else if (valueSatura >= 86 && valueSatura <= 90) {
                            so.setColor(Color.rgb(255, 38, 38));
                        } else {
                            so.setColor(Color.rgb(255, 38, 38));
                        }
                        canvas3.drawText(listMetricasPersonal.get(i).getSo2(), 940, 30 + y3sum, so);
                        //
                        int valuePulso = Integer.parseInt(listMetricasPersonal.get(i).getPulse());
                        if (valuePulso >= 86) {
                            pulse.setColor(Color.rgb(17, 230, 165));
                        } else if (valuePulso >= 70 && valuePulso <= 84) {
                            pulse.setColor(Color.rgb(255, 235, 59));
                        } else if (valuePulso >= 62 && valuePulso <= 68) {
                            pulse.setColor(Color.rgb(255, 38, 38));
                        } else {
                            pulse.setColor(Color.rgb(255, 38, 38));
                        }
                        canvas3.drawText(listMetricasPersonal.get(i).getPulse(), 1090, 30 + y3sum, pulse);
                        y3sum = y3sum + 50;
                    }
                    //
                    for (int i = 64; i < list3b; i++) {
                        canvas3.drawText(listPersonal.get(i).getDni(), 170, 30 + x3sum, myPaint);
                        canvas3.drawText(listPersonal.get(i).getName(), 340, 30 + x3sum, myPaint);
                        x3sum = x3sum + 50;
                    }
                    //
                    pdfDocument.finishPage(myPage3);
                    //---> Cierre
                    File file = new File(Environment.getExternalStorageDirectory(), "/arsi21.pdf");
                    try {
                        pdfDocument.writeTo(new FileOutputStream(file));
                    } catch (IOException e) {
                        Log.e(TAG, "error page 03-03 " + e.getMessage());
                    }
                    pdfDocument.close();

                } else if (listMetricasPersonal.size() >= 99 && listMetricasPersonal.size() <= 133) {

                    //-------------------------------------------------------------------------------
                    //---> Pagina 01-04 : [0-28]
                    for (int i = 0; i <= 28; i++) {
                        Log.e(TAG, "pagina 01 " + i);
                        cansas01.drawText(i + 1 + ".", 50, ytext + ysum, myPaint);
                        cansas01.drawText(listMetricasPersonal.get(i).getTempurature(), 760, ytext + ysum, myPaint);
                        int valueSatura = Integer.parseInt(listMetricasPersonal.get(i).getSo2());
                        if (valueSatura >= 95 && valueSatura <= 99) {
                            so.setColor(Color.rgb(17, 230, 165));
                        } else if (valueSatura >= 91 && valueSatura <= 94) {
                            so.setColor(Color.rgb(255, 235, 59));
                        } else if (valueSatura >= 86 && valueSatura <= 90) {
                            so.setColor(Color.rgb(255, 38, 38));
                        } else {
                            so.setColor(Color.rgb(255, 38, 38));
                        }
                        cansas01.drawText(listMetricasPersonal.get(i).getSo2(), 940, ytext + ysum, so);
                        //
                        int valuePulso = Integer.parseInt(listMetricasPersonal.get(i).getPulse());
                        if (valuePulso >= 86) {
                            pulse.setColor(Color.rgb(17, 230, 165));
                        } else if (valuePulso >= 70 && valuePulso <= 84) {
                            pulse.setColor(Color.rgb(255, 235, 59));
                        } else if (valuePulso >= 62 && valuePulso <= 68) {
                            pulse.setColor(Color.rgb(255, 38, 38));
                        } else {
                            pulse.setColor(Color.rgb(255, 38, 38));
                        }

                        cansas01.drawText(listMetricasPersonal.get(i).getPulse(), 1090, ytext + ysum, pulse);
                        ysum = ysum + 55;
                    }
                    //
                    for (int i = 0; i <= 28; i++) {
                        cansas01.drawText(listPersonal.get(i).getDni(), 170, ytextname + ysumname, myPaint);
                        cansas01.drawText(listPersonal.get(i).getName(), 340, ytextname + ysumname, myPaint);
                        ysumname = ysumname + 55;
                    }
                    //
                    pdfDocument.finishPage(myPage01);
                    //-------------------------------------------------------------------------------
                    //---> Pagina 02-04 : [28-66]
                    PdfDocument.PageInfo myPageInfo2 = new PdfDocument.PageInfo.Builder(1200, 2010, 2).create();
                    PdfDocument.Page myPage2 = pdfDocument.startPage(myPageInfo2);
                    Canvas canvas2 = myPage2.getCanvas();

                    int y2sum = 100;
                    int x2sum = 100;
                    int list2a = 63;
                    int list2b = 63;
                    for (int i = 29; i < list2a; i++) {
                        //
                        canvas2.drawText(i + ".", 50, 30 + y2sum, myPaint);
                        //
                        canvas2.drawText(listMetricasPersonal.get(i).getTempurature(), 760, 30 + y2sum, myPaint);
                        //
                        int valueSatura = Integer.parseInt(listMetricasPersonal.get(i).getSo2());
                        if (valueSatura >= 95 && valueSatura <= 99) {
                            so.setColor(Color.rgb(17, 230, 165));
                        } else if (valueSatura >= 91 && valueSatura <= 94) {
                            so.setColor(Color.rgb(255, 235, 59));
                        } else if (valueSatura >= 86 && valueSatura <= 90) {
                            so.setColor(Color.rgb(255, 38, 38));
                        } else {
                            so.setColor(Color.rgb(255, 38, 38));
                        }
                        canvas2.drawText(listMetricasPersonal.get(i).getSo2(), 940, 30 + y2sum, so);
                        //
                        int valuePulso = Integer.parseInt(listMetricasPersonal.get(i).getPulse());
                        if (valuePulso >= 86) {
                            pulse.setColor(Color.rgb(17, 230, 165));
                        } else if (valuePulso >= 70 && valuePulso <= 84) {
                            pulse.setColor(Color.rgb(255, 235, 59));
                        } else if (valuePulso >= 62 && valuePulso <= 68) {
                            pulse.setColor(Color.rgb(255, 38, 38));
                        } else {
                            pulse.setColor(Color.rgb(255, 38, 38));
                        }
                        canvas2.drawText(listMetricasPersonal.get(i).getPulse(), 1090, 30 + y2sum, pulse);
                        y2sum = y2sum + 50;
                    }
                    //
                    for (int i = 29; i < list2b; i++) {
                        canvas2.drawText(listPersonal.get(i).getDni(), 170, 30 + x2sum, myPaint);
                        canvas2.drawText(listPersonal.get(i).getName(), 340, 30 + x2sum, myPaint);
                        x2sum = x2sum + 50;
                    }
                    //
                    pdfDocument.finishPage(myPage2);
                    //-------------------------------------------------------------------------------
                    //---> Pagina 03-04 [64-98]
                    PdfDocument.PageInfo myPageInfo3 = new PdfDocument.PageInfo.Builder(1200, 2010, 3).create();
                    PdfDocument.Page myPage3 = pdfDocument.startPage(myPageInfo3);
                    Canvas canvas3 = myPage3.getCanvas();
                    //
                    int y3sum = 100;
                    int x3sum = 100;
                    int list3a = 98;
                    int list3b = 98;
                    for (int i = 64; i < list3a; i++) {
                        //numeracion
                        canvas3.drawText(i + ".", 50, 30 + y3sum, myPaint);
                        //temperatura
                        canvas3.drawText(listMetricasPersonal.get(i).getTempurature(), 760, 30 + y3sum, myPaint);
                        //
                        int valueSatura = Integer.parseInt(listMetricasPersonal.get(i).getSo2());
                        if (valueSatura >= 95 && valueSatura <= 99) {
                            so.setColor(Color.rgb(17, 230, 165));
                        } else if (valueSatura >= 91 && valueSatura <= 94) {
                            so.setColor(Color.rgb(255, 235, 59));
                        } else if (valueSatura >= 86 && valueSatura <= 90) {
                            so.setColor(Color.rgb(255, 38, 38));
                        } else {
                            so.setColor(Color.rgb(255, 38, 38));
                        }
                        canvas3.drawText(listMetricasPersonal.get(i).getSo2(), 940, 30 + y3sum, so);
                        //
                        int valuePulso = Integer.parseInt(listMetricasPersonal.get(i).getPulse());
                        if (valuePulso >= 86) {
                            pulse.setColor(Color.rgb(17, 230, 165));
                        } else if (valuePulso >= 70 && valuePulso <= 84) {
                            pulse.setColor(Color.rgb(255, 235, 59));
                        } else if (valuePulso >= 62 && valuePulso <= 68) {
                            pulse.setColor(Color.rgb(255, 38, 38));
                        } else {
                            pulse.setColor(Color.rgb(255, 38, 38));
                        }
                        canvas3.drawText(listMetricasPersonal.get(i).getPulse(), 1090, 30 + y3sum, pulse);
                        y3sum = y3sum + 50;
                    }
                    //
                    for (int i = 64; i < list3b; i++) {
                        canvas3.drawText(listPersonal.get(i).getDni(), 170, 30 + x3sum, myPaint);
                        canvas3.drawText(listPersonal.get(i).getName(), 340, 30 + x3sum, myPaint);
                        x3sum = x3sum + 50;
                    }
                    //
                    pdfDocument.finishPage(myPage3);

                    //-------------------------------------------------------------------------------
                    //---> Pagina 04-04 [99-133]
                    PdfDocument.PageInfo myPageInfo4 = new PdfDocument.PageInfo.Builder(1200, 2010, 3).create();
                    PdfDocument.Page myPage4 = pdfDocument.startPage(myPageInfo4);
                    Canvas canvas4 = myPage4.getCanvas();
                    //
                    int y4sum = 100;
                    int x4sum = 100;
                    int list4a = listMetricasPersonal.size();
                    int list4b = listPersonal.size();
                    for (int i = 99; i < list4a; i++) {
                        //numeracion
                        canvas4.drawText(i + ".", 50, 30 + y4sum, myPaint);
                        //temperatura
                        canvas4.drawText(listMetricasPersonal.get(i).getTempurature(), 760, 30 + y4sum, myPaint);
                        //
                        int valueSatura = Integer.parseInt(listMetricasPersonal.get(i).getSo2());
                        if (valueSatura >= 95 && valueSatura <= 99) {
                            so.setColor(Color.rgb(17, 230, 165));
                        } else if (valueSatura >= 91 && valueSatura <= 94) {
                            so.setColor(Color.rgb(255, 235, 59));
                        } else if (valueSatura >= 86 && valueSatura <= 90) {
                            so.setColor(Color.rgb(255, 38, 38));
                        } else {
                            so.setColor(Color.rgb(255, 38, 38));
                        }
                        canvas4.drawText(listMetricasPersonal.get(i).getSo2(), 940, 30 + y4sum, so);
                        //
                        int valuePulso = Integer.parseInt(listMetricasPersonal.get(i).getPulse());
                        if (valuePulso >= 86) {
                            pulse.setColor(Color.rgb(17, 230, 165));
                        } else if (valuePulso >= 70 && valuePulso <= 84) {
                            pulse.setColor(Color.rgb(255, 235, 59));
                        } else if (valuePulso >= 62 && valuePulso <= 68) {
                            pulse.setColor(Color.rgb(255, 38, 38));
                        } else {
                            pulse.setColor(Color.rgb(255, 38, 38));
                        }
                        canvas4.drawText(listMetricasPersonal.get(i).getPulse(), 1090, 30 + y4sum, pulse);
                        y4sum = y4sum + 50;
                    }
                    //
                    for (int i = 99; i < list4b; i++) {
                        canvas4.drawText(listPersonal.get(i).getDni(), 170, 30 + x4sum, myPaint);
                        canvas4.drawText(listPersonal.get(i).getName(), 340, 30 + x4sum, myPaint);
                        x4sum = x4sum + 50;
                    }
                    //
                    pdfDocument.finishPage(myPage4);
                    //---> Cierre
                    File file = new File(Environment.getExternalStorageDirectory(), "/arsi21.pdf");
                    try {
                        pdfDocument.writeTo(new FileOutputStream(file));
                    } catch (IOException e) {
                        Log.e(TAG, "error page 03-03 " + e.getMessage());
                    }
                    pdfDocument.close();

                } else if (listMetricasPersonal.size() >= 134 && listMetricasPersonal.size() <= 150) {


                    //-------------------------------------------------------------------------------
                    //---> Pagina 01-05 : [0-28]
                    for (int i = 0; i <= 28; i++) {
                        Log.e(TAG, "pagina 01 " + i);
                        cansas01.drawText(i + 1 + ".", 50, ytext + ysum, myPaint);
                        cansas01.drawText(listMetricasPersonal.get(i).getTempurature(), 760, ytext + ysum, myPaint);
                        int valueSatura = Integer.parseInt(listMetricasPersonal.get(i).getSo2());
                        if (valueSatura >= 95 && valueSatura <= 99) {
                            so.setColor(Color.rgb(17, 230, 165));
                        } else if (valueSatura >= 91 && valueSatura <= 94) {
                            so.setColor(Color.rgb(255, 235, 59));
                        } else if (valueSatura >= 86 && valueSatura <= 90) {
                            so.setColor(Color.rgb(255, 38, 38));
                        } else {
                            so.setColor(Color.rgb(255, 38, 38));
                        }
                        cansas01.drawText(listMetricasPersonal.get(i).getSo2(), 940, ytext + ysum, so);
                        //
                        int valuePulso = Integer.parseInt(listMetricasPersonal.get(i).getPulse());
                        if (valuePulso >= 86) {
                            pulse.setColor(Color.rgb(17, 230, 165));
                        } else if (valuePulso >= 70 && valuePulso <= 84) {
                            pulse.setColor(Color.rgb(255, 235, 59));
                        } else if (valuePulso >= 62 && valuePulso <= 68) {
                            pulse.setColor(Color.rgb(255, 38, 38));
                        } else {
                            pulse.setColor(Color.rgb(255, 38, 38));
                        }

                        cansas01.drawText(listMetricasPersonal.get(i).getPulse(), 1090, ytext + ysum, pulse);
                        ysum = ysum + 55;
                    }
                    //
                    for (int i = 0; i <= 28; i++) {
                        cansas01.drawText(listPersonal.get(i).getDni(), 170, ytextname + ysumname, myPaint);
                        cansas01.drawText(listPersonal.get(i).getName(), 340, ytextname + ysumname, myPaint);
                        ysumname = ysumname + 55;
                    }
                    //
                    pdfDocument.finishPage(myPage01);
                    //-------------------------------------------------------------------------------
                    //---> Pagina 02-04 : [28-66]
                    PdfDocument.PageInfo myPageInfo2 = new PdfDocument.PageInfo.Builder(1200, 2010, 2).create();
                    PdfDocument.Page myPage2 = pdfDocument.startPage(myPageInfo2);
                    Canvas canvas2 = myPage2.getCanvas();

                    int y2sum = 100;
                    int x2sum = 100;
                    int list2a = 63;
                    int list2b = 63;
                    for (int i = 29; i < list2a; i++) {
                        //
                        canvas2.drawText(i + ".", 50, 30 + y2sum, myPaint);
                        //
                        canvas2.drawText(listMetricasPersonal.get(i).getTempurature(), 760, 30 + y2sum, myPaint);
                        //
                        int valueSatura = Integer.parseInt(listMetricasPersonal.get(i).getSo2());
                        if (valueSatura >= 95 && valueSatura <= 99) {
                            so.setColor(Color.rgb(17, 230, 165));
                        } else if (valueSatura >= 91 && valueSatura <= 94) {
                            so.setColor(Color.rgb(255, 235, 59));
                        } else if (valueSatura >= 86 && valueSatura <= 90) {
                            so.setColor(Color.rgb(255, 38, 38));
                        } else {
                            so.setColor(Color.rgb(255, 38, 38));
                        }
                        canvas2.drawText(listMetricasPersonal.get(i).getSo2(), 940, 30 + y2sum, so);
                        //
                        int valuePulso = Integer.parseInt(listMetricasPersonal.get(i).getPulse());
                        if (valuePulso >= 86) {
                            pulse.setColor(Color.rgb(17, 230, 165));
                        } else if (valuePulso >= 70 && valuePulso <= 84) {
                            pulse.setColor(Color.rgb(255, 235, 59));
                        } else if (valuePulso >= 62 && valuePulso <= 68) {
                            pulse.setColor(Color.rgb(255, 38, 38));
                        } else {
                            pulse.setColor(Color.rgb(255, 38, 38));
                        }
                        canvas2.drawText(listMetricasPersonal.get(i).getPulse(), 1090, 30 + y2sum, pulse);
                        y2sum = y2sum + 50;
                    }
                    //
                    for (int i = 29; i < list2b; i++) {
                        canvas2.drawText(listPersonal.get(i).getDni(), 170, 30 + x2sum, myPaint);
                        canvas2.drawText(listPersonal.get(i).getName(), 340, 30 + x2sum, myPaint);
                        x2sum = x2sum + 50;
                    }
                    //
                    pdfDocument.finishPage(myPage2);
                    //-------------------------------------------------------------------------------
                    //---> Pagina 03-05 [64-98]
                    PdfDocument.PageInfo myPageInfo3 = new PdfDocument.PageInfo.Builder(1200, 2010, 3).create();
                    PdfDocument.Page myPage3 = pdfDocument.startPage(myPageInfo3);
                    Canvas canvas3 = myPage3.getCanvas();
                    //
                    int y3sum = 100;
                    int x3sum = 100;
                    int list3a = 98;
                    int list3b = 98;
                    for (int i = 64; i < list3a; i++) {
                        //numeracion
                        canvas3.drawText(i + ".", 50, 30 + y3sum, myPaint);
                        //temperatura
                        canvas3.drawText(listMetricasPersonal.get(i).getTempurature(), 760, 30 + y3sum, myPaint);
                        //
                        int valueSatura = Integer.parseInt(listMetricasPersonal.get(i).getSo2());
                        if (valueSatura >= 95 && valueSatura <= 99) {
                            so.setColor(Color.rgb(17, 230, 165));
                        } else if (valueSatura >= 91 && valueSatura <= 94) {
                            so.setColor(Color.rgb(255, 235, 59));
                        } else if (valueSatura >= 86 && valueSatura <= 90) {
                            so.setColor(Color.rgb(255, 38, 38));
                        } else {
                            so.setColor(Color.rgb(255, 38, 38));
                        }
                        canvas3.drawText(listMetricasPersonal.get(i).getSo2(), 940, 30 + y3sum, so);
                        //
                        int valuePulso = Integer.parseInt(listMetricasPersonal.get(i).getPulse());
                        if (valuePulso >= 86) {
                            pulse.setColor(Color.rgb(17, 230, 165));
                        } else if (valuePulso >= 70 && valuePulso <= 84) {
                            pulse.setColor(Color.rgb(255, 235, 59));
                        } else if (valuePulso >= 62 && valuePulso <= 68) {
                            pulse.setColor(Color.rgb(255, 38, 38));
                        } else {
                            pulse.setColor(Color.rgb(255, 38, 38));
                        }
                        canvas3.drawText(listMetricasPersonal.get(i).getPulse(), 1090, 30 + y3sum, pulse);
                        y3sum = y3sum + 50;
                    }
                    //
                    for (int i = 64; i < list3b; i++) {
                        canvas3.drawText(listPersonal.get(i).getDni(), 170, 30 + x3sum, myPaint);
                        canvas3.drawText(listPersonal.get(i).getName(), 340, 30 + x3sum, myPaint);
                        x3sum = x3sum + 50;
                    }
                    //
                    pdfDocument.finishPage(myPage3);

                    //-------------------------------------------------------------------------------
                    //---> Pagina 04-05 [99-133]
                    PdfDocument.PageInfo myPageInfo4 = new PdfDocument.PageInfo.Builder(1200, 2010, 3).create();
                    PdfDocument.Page myPage4 = pdfDocument.startPage(myPageInfo4);
                    Canvas canvas4 = myPage4.getCanvas();
                    //
                    int y4sum = 100;
                    int x4sum = 100;
                    int list4a = listMetricasPersonal.size();
                    int list4b = listPersonal.size();
                    for (int i = 99; i < list4a; i++) {
                        //numeracion
                        canvas4.drawText(i + ".", 50, 30 + y4sum, myPaint);
                        //temperatura
                        canvas4.drawText(listMetricasPersonal.get(i).getTempurature(), 760, 30 + y4sum, myPaint);
                        //
                        int valueSatura = Integer.parseInt(listMetricasPersonal.get(i).getSo2());
                        if (valueSatura >= 95 && valueSatura <= 99) {
                            so.setColor(Color.rgb(17, 230, 165));
                        } else if (valueSatura >= 91 && valueSatura <= 94) {
                            so.setColor(Color.rgb(255, 235, 59));
                        } else if (valueSatura >= 86 && valueSatura <= 90) {
                            so.setColor(Color.rgb(255, 38, 38));
                        } else {
                            so.setColor(Color.rgb(255, 38, 38));
                        }
                        canvas4.drawText(listMetricasPersonal.get(i).getSo2(), 940, 30 + y4sum, so);
                        //
                        int valuePulso = Integer.parseInt(listMetricasPersonal.get(i).getPulse());
                        if (valuePulso >= 86) {
                            pulse.setColor(Color.rgb(17, 230, 165));
                        } else if (valuePulso >= 70 && valuePulso <= 84) {
                            pulse.setColor(Color.rgb(255, 235, 59));
                        } else if (valuePulso >= 62 && valuePulso <= 68) {
                            pulse.setColor(Color.rgb(255, 38, 38));
                        } else {
                            pulse.setColor(Color.rgb(255, 38, 38));
                        }
                        canvas4.drawText(listMetricasPersonal.get(i).getPulse(), 1090, 30 + y4sum, pulse);
                        y4sum = y4sum + 50;
                    }
                    //
                    for (int i = 99; i < list4b; i++) {
                        canvas4.drawText(listPersonal.get(i).getDni(), 170, 30 + x4sum, myPaint);
                        canvas4.drawText(listPersonal.get(i).getName(), 340, 30 + x4sum, myPaint);
                        x4sum = x4sum + 50;
                    }
                    //
                    pdfDocument.finishPage(myPage4);
                    //-------------------------------------------------------------------------------
                    //---> Pagina 05-05 [134-150]
                    PdfDocument.PageInfo myPageInfo5 = new PdfDocument.PageInfo.Builder(1200, 2010, 3).create();
                    PdfDocument.Page myPage5 = pdfDocument.startPage(myPageInfo5);
                    Canvas canvas5 = myPage5.getCanvas();
                    //
                    int y5sum = 100;
                    int x5sum = 100;
                    int list5a = listMetricasPersonal.size();
                    int list5b = listPersonal.size();
                    for (int i = 134; i < list5a; i++) {
                        //numeracion
                        canvas5.drawText(i + ".", 50, 30 + y5sum, myPaint);
                        //temperatura
                        canvas5.drawText(listMetricasPersonal.get(i).getTempurature(), 760, 30 + y5sum, myPaint);
                        //
                        int valueSatura = Integer.parseInt(listMetricasPersonal.get(i).getSo2());
                        if (valueSatura >= 95 && valueSatura <= 99) {
                            so.setColor(Color.rgb(17, 230, 165));
                        } else if (valueSatura >= 91 && valueSatura <= 94) {
                            so.setColor(Color.rgb(255, 235, 59));
                        } else if (valueSatura >= 86 && valueSatura <= 90) {
                            so.setColor(Color.rgb(255, 38, 38));
                        } else {
                            so.setColor(Color.rgb(255, 38, 38));
                        }
                        canvas5.drawText(listMetricasPersonal.get(i).getSo2(), 940, 30 + y5sum, so);
                        //
                        int valuePulso = Integer.parseInt(listMetricasPersonal.get(i).getPulse());
                        if (valuePulso >= 86) {
                            pulse.setColor(Color.rgb(17, 230, 165));
                        } else if (valuePulso >= 70 && valuePulso <= 84) {
                            pulse.setColor(Color.rgb(255, 235, 59));
                        } else if (valuePulso >= 62 && valuePulso <= 68) {
                            pulse.setColor(Color.rgb(255, 38, 38));
                        } else {
                            pulse.setColor(Color.rgb(255, 38, 38));
                        }
                        canvas5.drawText(listMetricasPersonal.get(i).getPulse(), 1090, 30 + y5sum, pulse);
                        y5sum = y5sum + 50;
                    }
                    //
                    for (int i = 134; i < list5b; i++) {
                        canvas5.drawText(listPersonal.get(i).getDni(), 170, 30 + x5sum, myPaint);
                        canvas5.drawText(listPersonal.get(i).getName(), 340, 30 + x5sum, myPaint);
                        x5sum = x5sum + 50;
                    }
                    //
                    pdfDocument.finishPage(myPage4);
                    //---> Cierre
                    File file = new File(Environment.getExternalStorageDirectory(), "/arsi21.pdf");
                    try {
                        pdfDocument.writeTo(new FileOutputStream(file));
                    } catch (IOException e) {
                        Log.e(TAG, "error page 03-03 " + e.getMessage());
                    }
                    pdfDocument.close();


                } else {
                    Log.e(TAG, "ERROR problemas de index");
                    Log.e(TAG, "maximo 150 personas");
                }


                if (metodo.equalsIgnoreCase("pdf")) {
                    Log.e(TAG, " metodo pdf ");
                    Intent intent = new Intent(ReportDataWorkerActivity.this, ShowPdfActivity.class);
                    startActivity(intent);
                    mDialog.dismiss();
                } else {
                    Log.e(TAG, " metodo sendEmail ");
                    sendEmail();
                    mDialog.dismiss();
                }

            }
        }
        mDialog.dismiss();

    }

    //===============================================================================

    private void generarListaporPersonalPdf(String nombre, String metodo) {

        Log.e(TAG, "---> generarListaporPersonalPdf()  ");
        Log.e(TAG, "tamaño  listDate : " + listDate.size());
        Log.e(TAG, "tamaño listTemperatura : " + listTemperatura.size());
        Log.e(TAG, "tamaño listSaturacion : " + listSaturacion.size());
        Log.e(TAG, "tamaño listPulso : " + listPulso.size());

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
        cansas01.drawText("Responsable : " + Common.currentUser.getReg_name(), 20, 300, info);

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
        int ysum = 50;
        int ytextname = 400;
        int ysumname = 50;
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
        //-------------------------------------------------------------------------------
        int size = listDate.size();
        // info trabajador

        for (int i = 0; i < size; i++) {
            ysumname = ysumname + 50;
            cansas01.drawText(listDate.get(i), 170, ytextname + ysumname, myPaint);
        }


        for (int i = 0; i < size; i++) {
            ysum = ysum + 50;
            // Nro
            cansas01.drawText(i + 1 + " ", 80, ytext + ysum, myPaint);
            // Temperatura
            cansas01.drawText(listTemperatura.get(i), 490, ytext + ysum, myPaint);
            sumaTemp = sumaTemp + Double.parseDouble(listTemperatura.get(i));

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
        }

        try {
            promedioTemp = sumaTemp / size;
            promedioSatura = sumaSatura / size;
            promedioPulse = sumaPulse / size;

            Log.e(TAG, "promedioTemp : " + promedioTemp);
            Log.e(TAG, "promedioSatura : " + promedioSatura);
            Log.e(TAG, "promedioPulse : " + promedioPulse);

            String cadTemp = String.valueOf(promedioTemp);
            String cadSa = String.valueOf(promedioSatura);
            String cadPulse = String.valueOf(promedioPulse);

            Log.e(TAG, "cadTemp : " + cadTemp);
            Log.e(TAG, "cadSa : " + cadSa);
            Log.e(TAG, "cadPulse : " + cadPulse);
            cansas01.drawText("Promedio de los ultimos " + size + " días", 35, 1600, myPaint);
            cansas01.drawText("Promedio temperatura : " + cadTemp.substring(0, 4), 35, 1650, myPaint);
            cansas01.drawText("Promedio SO2  : " + cadSa.substring(0, 4), 35, 1700, myPaint);
            cansas01.drawText("Promedio pulso : " + cadPulse.substring(0, 4), 35, 1750, myPaint);
            //-------------------------------------------------------------------------------
        } catch (Exception e) {
            Log.e("error promedio  :  ", " --> " + e.getMessage());
        }


        pdfDocument.finishPage(myPage01);
        //---> Cierre
        File file = new File(Environment.getExternalStorageDirectory(), "/arsi21.pdf");
        try {
            pdfDocument.writeTo(new FileOutputStream(file));
        } catch (Exception e) {
            Log.e(TAG, "TRY-CATH : " + e.getMessage());
        }
        pdfDocument.close();
        //


        if (metodo.equalsIgnoreCase("pdf")) {
            Log.e(TAG, " metodo pdf ");
            Intent intent = new Intent(ReportDataWorkerActivity.this, ShowPdfActivity.class);
            startActivity(intent);
            mDialog.dismiss();
        } else if (metodo.equalsIgnoreCase("email")) {
            Log.e(TAG, " metodo sendEmail ");
            sendEmail();
            mDialog.dismiss();
        }


    }

    private void getDataFromFirebase(String dni, String nombre, String metodo) {
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
                        listDate.add(metricasPersonal.getDateRegister());
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

                try {
                    generarListaporPersonalPdf(nombre, metodo);
                } catch (Exception e) {
                    Log.e(TAG, "ERROR --> getDataFromFirebase : " + e.getMessage());
                    Toast.makeText(ReportDataWorkerActivity.this, "Error al Generar PDF ", Toast.LENGTH_SHORT).show();
                    mDialog.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "error" + " : " + databaseError.getMessage());
                mDialog.dismiss();
            }
        });


    }

    private String timeStampToString(long time) {
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
        calendar.setTimeInMillis(time);
        String date = DateFormat.format("yyyy-MM-dd", calendar).toString();
        return date;
    }

    private void sendEmail() {
        Log.e(TAG, "sendEmail()  2 ");
        File root = Environment.getExternalStorageDirectory();
        String filelocation = root.getAbsolutePath() + "/arsi21.pdf";
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setType("text/plain");
        String message = "Documento Generado por " + Common.currentUser.getReg_name();
        intent.putExtra(Intent.EXTRA_SUBJECT, "Unidades ARSI : " + Common.unidadTrabajoSelected.getAliasUT() + "\n Saludos");
        intent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://" + filelocation));
        intent.putExtra(Intent.EXTRA_TEXT, message);
        String currentusermail = Common.currentUser.getReg_email();
        Log.e(TAG, "currentusermail  : " + currentusermail);
        intent.setData(Uri.parse("mailto:" + currentusermail));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Log.e(TAG, "sendEmail 2  -->  filelocation " + filelocation);
        startActivity(intent);
    }

    public void showPdfDialog(String metodo) {

        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(ReportDataWorkerActivity.this);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.pop_up_report_dni, null);
        builder.setView(view);
        builder.setCancelable(false);
        view.setKeepScreenOn(true);
        final AlertDialog dialog = builder.create();

        TextInputLayout report_dni_layout = view.findViewById(R.id.report_dni_layout);
        TextInputEditText report_dni = view.findViewById(R.id.report_dni);

        Button btn_report_dni_close = view.findViewById(R.id.btn_report_dni_close);
        Button btn_report_dni = view.findViewById(R.id.btn_report_dni);

        btn_report_dni.setOnClickListener(v -> {
            String dni = report_dni.getText().toString();
            if (report_dni.getText().toString().trim().isEmpty()) {
                report_dni_layout.setError("Ingrese su DNI");
                dialog.dismiss();
            } else {
                report_dni_layout.setError(null);
                mDialog = new ProgressDialog(view.getContext());
                mDialog.setMessage("Obteniendo datos ...");
                mDialog.show();
                //
                Log.e(TAG, "-----> funcion  : consultarDatosPaciente");
                Log.e(TAG, " dni : " + dni);
                //
                DatabaseReference ref_db_mina_personal = database.getReference(Common.db_mina_personal);
                DatabaseReference ref_mina = ref_db_mina_personal.child(Common.unidadTrabajoSelected.getNameUT());

                ref_mina.child(dni).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Personal personal = dataSnapshot.getValue(Personal.class);
                        if (personal != null) {
                            if (personal.getLast() == null) {
                                personal.setLast(" ");
                            }
                            Log.e(TAG, " personal.getName() : " + personal.getName());
                            report_dni_layout.setError(null);
                            String fullname = personal.getName() + " " + personal.getLast();
                            getDataFromFirebase(dni, fullname, metodo);
                        } else {
                            Log.e(TAG, " personal.getName() : null ");
                            mDialog.dismiss();
                            report_dni_layout.setError("El trabajador no exsite en la base de datos");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        mDialog.dismiss();
                        Log.e(TAG, "error : " + databaseError.getMessage());
                    }
                });

            }
        });

        btn_report_dni_close.setOnClickListener(v -> dialog.dismiss());
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

    public void showEmailoDialog(String cadena) {
        String metodo = cadena;
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(ReportDataWorkerActivity.this);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.pop_up_report_dni, null);
        builder.setView(view);
        builder.setCancelable(false);
        view.setKeepScreenOn(true);
        final AlertDialog dialog = builder.create();

        TextInputLayout report_dni_layout = view.findViewById(R.id.report_dni_layout);
        TextInputEditText report_dni = view.findViewById(R.id.report_dni);

        Button btn_report_dni_close = view.findViewById(R.id.btn_report_dni_close);
        Button btn_report_dni = view.findViewById(R.id.btn_report_dni);


        btn_report_dni.setOnClickListener(v -> {
            String dni = report_dni.getText().toString();

            if (report_dni.getText().toString().trim().isEmpty()) {
                report_dni_layout.setError("Ingrese su DNI");
                dialog.dismiss();
            } else {
                report_dni_layout.setError(null);
                mDialog = new ProgressDialog(view.getContext());
                mDialog.setMessage("Obteniendo datos ...");
                mDialog.show();
                //
                Log.e(TAG, "-----> funcion  : consultarDatosPaciente");
                Log.e(TAG, " dni : " + dni);
                //CreateUT
                DatabaseReference ref_db_mina_personal = database.getReference(Common.db_mina_personal);
                DatabaseReference ref_mina = ref_db_mina_personal.child(Common.unidadTrabajoSelected.getNameUT());

                ref_mina.child(dni).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Personal personal = dataSnapshot.getValue(Personal.class);
                        if (personal != null) {
                            if (personal.getLast() == null) {
                                personal.setLast(" ");
                            }
                            Log.e(TAG, " personal.getName() : " + personal.getName());
                            report_dni_layout.setError(null);
                            String fullname = personal.getName() + " " + personal.getLast();
                            getDataFromFirebase(dni, fullname, metodo);
                        } else {
                            Log.e(TAG, " personal.getName() : null ");
                            mDialog.dismiss();
                            report_dni_layout.setError("El trabajador no exsite en la base de datos");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        mDialog.dismiss();
                        Log.e(TAG, "error : " + databaseError.getMessage());
                    }
                });
                Toast.makeText(this, "Documento generado", Toast.LENGTH_SHORT).show();
            }
        });

        btn_report_dni_close.setOnClickListener(v -> dialog.dismiss());
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

    //===============================================================================

    public void showTestEmailDNI(final String metodo) {
        // todo : falta hacer para ver las pruebas rapida

        AlertDialog.Builder builder = new AlertDialog.Builder(ReportDataWorkerActivity.this);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.pop_up_test_fast, null);
        builder.setView(view);
        builder.setCancelable(false);
        view.setKeepScreenOn(true);
        final AlertDialog dialog = builder.create();
        //
        TextInputLayout test_dni_layout = view.findViewById(R.id.test_dni_layout);
        TextInputEditText test_dni = view.findViewById(R.id.test_dni);


        //
        Button btn_test_dni, btn_test_dni_close;
        btn_test_dni = view.findViewById(R.id.btn_test_dni);
        btn_test_dni_close = view.findViewById(R.id.btn_test_dni_close);

        btn_test_dni.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String dni = test_dni.getText().toString();
                Log.e(TAG, "[TextInputEditText] DNI " + dni);
                DatabaseReference ref_db_mina_personal = database.getReference(Common.db_mina_personal);
                DatabaseReference ref_mina = ref_db_mina_personal.child(Common.unidadTrabajoSelected.getNameUT());
                ref_mina.child(dni).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        final Personal personal = dataSnapshot.getValue(Personal.class);
                        if (personal != null) {
                            test_dni_layout.setError(null);
                            if (personal.getLast() == null) {
                                personal.setLast(" ");
                            }

                            Log.e(TAG, "NOMBRE DEL TRABJADOR  = " + personal.getName() + " " + personal.getLast());
                            ref_datos_paciente = FirebaseDatabase.getInstance().getReference(Common.db_mina_personal_data).child(Common.unidadTrabajoSelected.getNameUT()).child(dni);
                            ref_datos_paciente.keepSynced(true);
                            ref_datos_paciente.orderByKey();

                            ref_datos_paciente.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    listtemp = new ArrayList<>();
                                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                        MetricasPersonal metricasPersonal = snapshot.getValue(MetricasPersonal.class);
                                        if (metricasPersonal != null) {
                                            Log.e(TAG, " [ref_datos_paciente] test : " + metricasPersonal.getTestpruebarapida());
                                            if (metricasPersonal.getTestpruebarapida() != null) {
                                                boolean test = metricasPersonal.getTestpruebarapida();
                                                if (test) {
                                                    listtemp.add(metricasPersonal);
                                                }
                                            }
                                        }
                                    }

                                    try {
                                        String nombre = personal.getName() + " " + personal.getLast();

                                        generarPDFTestFast(listtemp, nombre, metodo);
                                    } catch (Exception e) {
                                        Log.e(TAG, "try-catch : " + e.getMessage());
                                    }

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                    Log.e(TAG, "  [ref_datos_paciente] error : " + databaseError.getMessage());
                                }
                            });


                        } else {
                            Log.e(TAG, "personal  = null");
                            test_dni_layout.setError("El trabajador no exsite en la base de datos");

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.e(TAG, "error : " + databaseError.getMessage());
                    }
                });
            }
        });

        btn_test_dni_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

    private void generarPDFTestFast(List<MetricasPersonal> listtemp, String nombre, String metodo) {
        Log.e(TAG, "---> generarListaporPersonalPdf()  ");
        Log.e(TAG, " Tamaño : " + listtemp.size());


        //
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

        cansas01.drawText("Unidad       :  " + Common.unidadTrabajoSelected.getAliasUT(), 20, 200, info);
        cansas01.drawText("Responsable  :  " + Common.currentUser.getReg_name(), 20, 250, info);
        cansas01.drawText("Trabajador   :  " + nombre, 20, 300, info);


        // Encabezados
        myPaint.setStyle(Paint.Style.STROKE);
        myPaint.setStrokeWidth(2);
        myPaint.setTextSize(25f);
        cansas01.drawRect(20, 360, pageWidth - 20, 440, myPaint);
        //
        myPaint.setTextAlign(Paint.Align.LEFT);
        myPaint.setStyle(Paint.Style.FILL);

        cansas01.drawText("Nro.", 65, 415, myPaint);
        cansas01.drawText("Fecha", 220, 415, myPaint);
        cansas01.drawText("Temperatura", 470, 415, myPaint);
        cansas01.drawText("Prueba Rápida.", 820, 415, myPaint);

        cansas01.drawLine(140, 380, 140, 430, myPaint);
        cansas01.drawLine(410, 380, 410, 430, myPaint);
        cansas01.drawLine(700, 380, 700, 430, myPaint);

        //
        int ytext = 400;
        int ysum = 50;
        int ytextname = 400;
        int ysumname = 50;
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

        //-------------------------------------------------------------------------------
        int size = listtemp.size();
        // info trabajador

        for (int i = 0; i < size; i++) {
            Log.e(TAG, " x : " + i);
            Log.e(TAG, " y : " + listtemp.get(i).getDateRegister());
            Log.e(TAG, " z : " + listtemp.get(i).getTempurature());
            Log.e(TAG, " r : " + listtemp.get(i).getTestpruebarapida());
            boolean test = listtemp.get(i).getTestpruebarapida();
            String testfast = "";
            if (test) {
                testfast = "Si";
                ysum = ysum + 50;
                // Nro
                cansas01.drawText(i + 1 + " ", 80, ytext + ysum, myPaint);
                // Fecha
                cansas01.drawText(listtemp.get(i).getDateRegister(), 160, ytext + ysum, myPaint);
                // Temperatura
                cansas01.drawText(listtemp.get(i).getTempurature(), 520, ytext + ysum, myPaint);
                // Prueba Rapida
                cansas01.drawText(testfast, 880, ytext + ysum, so);
                //
            }
        }
        pdfDocument.finishPage(myPage01);
        //---> Cierre
        File file = new File(Environment.getExternalStorageDirectory(), "/arsi21.pdf");
        try {
            pdfDocument.writeTo(new FileOutputStream(file));
        } catch (Exception e) {
            Log.e(TAG, "TRY-CATH : " + e.getMessage());
        }
        pdfDocument.close();
        //


        if (metodo.equalsIgnoreCase("pdf")) {
            Log.e(TAG, " metodo pdf ");
            Intent intent = new Intent(ReportDataWorkerActivity.this, ShowPdfActivity.class);
            startActivity(intent);
            mDialog.dismiss();
        } else if (metodo.equalsIgnoreCase("email")) {
            Log.e(TAG, " metodo sendEmail ");
            sendEmail();
            mDialog.dismiss();
        }


    }


}
