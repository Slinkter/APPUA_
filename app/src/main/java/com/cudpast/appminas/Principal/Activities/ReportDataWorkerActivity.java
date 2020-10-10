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
import android.widget.Toast;

import com.cudpast.appminas.Common.Common;
import com.cudpast.appminas.Model.AllPersonalMetricas;
import com.cudpast.appminas.Model.MetricasPersonal;
import com.cudpast.appminas.Model.Personal;
import com.cudpast.appminas.Principal.Activities.Support.ShowPdfActivity2;
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
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.TimeZone;

public class ReportDataWorkerActivity extends AppCompatActivity {

    public static final String TAG = ReportDataWorkerActivity.class.getSimpleName();
    public static final String folderpdf = "/arsi21.pdf";
    //
    private FirebaseDatabase database;
    private DatabaseReference ref_datos_paciente;
    private List<MetricasPersonal> listaMetricasPersonales;

    private List<MetricasPersonal> listaMetricasPersonalesEntrada;
    private List<MetricasPersonal> listaMetricasPersonalesSalida;

    private List<Personal> listaPersonal;
    private MaterialDatePicker mdp;
    private MaterialDatePicker.Builder builder;
    //
    private String seletedDate;
    private ProgressDialog mDialog;
    //
    List<String> listDate;
    List<String> listTemperatura;
    List<Integer> listSaturacion;
    List<Integer> listPulso;
    List<Boolean> listTest;
    //
    List<Boolean> s1;
    List<Boolean> s2;
    List<Boolean> s3;
    List<Boolean> s4;
    List<Boolean> s5;
    List<Boolean> s6;
    List<Boolean> s7;




    //
    private List<MetricasPersonal> listtemp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setTitle("Regresar");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_report_data_worker);
        //Solicitar permisos
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PackageManager.PERMISSION_GRANTED);
        //Firebaase
        database = FirebaseDatabase.getInstance();
    }


    //===============================================================================
    // CardView 1
    public void btn_entrada_pdf(View view) {

        String metodo = "pdf";
        Boolean horario = true;
        //
        builder = MaterialDatePicker.Builder.datePicker();
        builder.setTitleText("Seleccionar Fecha");
        mdp = builder.build();
        mdp.show(getSupportFragmentManager(), "DATE_PICKER");
        mdp.addOnPositiveButtonClickListener((MaterialPickerOnPositiveButtonClickListener<Long>) input_dateSelected -> {
            //
            mDialog = new ProgressDialog(ReportDataWorkerActivity.this);
            mDialog.setMessage("Descargando datos...");
            mDialog.show();
            // Init arrays
            //Horario
            listaMetricasPersonalesEntrada = new ArrayList<>();
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
                    dateTurnSelected(dataSnapshot, metodo, horario);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.e(TAG, "[showSelectDate ] error : " + databaseError.getMessage());
                    mDialog.dismiss();
                }
            });

        });
    }


    //===============================================================================
    // CardView 2
    public void btn_salida_pdf(View view) {
        String metodo = "pdf";
        Boolean horario = false;
        //
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
            //Horario
            listaMetricasPersonalesEntrada = new ArrayList<>();
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
                    dateTurnSelected(dataSnapshot, metodo, horario);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.e(TAG, "[showSelectDate ] error : " + databaseError.getMessage());
                    mDialog.dismiss();
                }
            });

        });

    }

    //===============================================================================
    // CardView 3
    public void btn_workerd(View view) {
        showPdfDialog("pdf");
    }

    // CardView 4
    public void btn_historialSintomas(View view) {
        showPopUpHistorialSintomas("pdf");
    }


    private void dateTurnSelected(DataSnapshot dataAll, String metodo, boolean horario) {

        ArrayList<String> listDNI = new ArrayList<>();
        for (DataSnapshot item_snapshot : dataAll.getChildren()) { //<--- all data
            if (item_snapshot != null) {
                String dni = item_snapshot.getKey();// <-- Get key ( dni worker)
                for (DataSnapshot item_date : item_snapshot.getChildren()) {  // <-- Todas la metricas por fechas de 1 persona
                    if (item_date != null) {
                        // Check Date , si coincide la fecha guardar data metrica y dni
                        String registerDate = Objects.requireNonNull(item_date.getKey()).substring(0, 10).trim();
                        boolean checkdate = seletedDate.equalsIgnoreCase(registerDate);
                        if (checkdate) {
                            try {
                                MetricasPersonal data = item_date.getValue(MetricasPersonal.class);

                                if (data.getHorario() == null) {
                                    data.setHorario(false);
                                }

                                data.showInfo();

                                if (data.getHorario() == horario) {
                                    listaMetricasPersonalesEntrada.add(data);
                                    listDNI.add(dni);
                                    //
                                    Log.e(TAG, "filtro horario = " + data.getHorario());
                                }

                            } catch (Exception e) {
                                Log.e(TAG, " error try - cath" + e.getMessage());
                            }
                        }
                    } else {
                        Log.e(TAG, "no hay item_date = null");
                    }
                }
                Log.e(TAG, "[onDataChange] dni = " + dni);
            } else {
                Log.e(TAG, "no hay snaphot = " + item_snapshot);
            }
        }

        if (listDNI.size() == 0) {
            mDialog.dismiss();
            Log.e(TAG, " No hay datos para esta fecha");
            Toast.makeText(this, "No hay datos para esta fecha", Toast.LENGTH_SHORT).show();
        }
        //===================================================
        //--> Generar lista por horario
        for (int i = 0; i < listDNI.size(); i++) {
            //
            String dni_personal = listDNI.get(i);
            //
            DatabaseReference ref_mina = database
                    .getReference(Common.db_mina_personal)
                    .child(Common.unidadTrabajoSelected.getNameUT())
                    .child(dni_personal);
            //
            ref_mina.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    //
                    Personal personal = dataSnapshot.getValue(Personal.class);
                    //
                    if (personal != null) {
                        listaPersonal.add(personal);
                    }
                    if (listaPersonal.size() == listDNI.size()) {
                        // -------------------------------->
                        reporte_por_dia(listaMetricasPersonalesEntrada, listaPersonal, seletedDate, metodo, horario);
                        //  <--------------------------------
                    }
                    Log.e(TAG, "[dateTurnSelected]-onDataChange  listaPersonal.size()  : " + listaPersonal.size());
                    Log.e(TAG, "[dateTurnSelected]-onDataChange  listDNI.size()  : " + listDNI.size());
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.e(TAG, "[filtrarFecha]-onCancelled databaseError : " + databaseError.getMessage());
                }
            });

        }

    }

    private void reporte_por_dia(List<MetricasPersonal> listMetricasPersonal, List<Personal> listPersonal, String seletedDate, String metodo, boolean horario) {

        List<AllPersonalMetricas> list_workers = new ArrayList<>();
        int nCountWorkers = listMetricasPersonal.size();
        //
        Log.e(TAG, ">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        Log.e(TAG, "[   reporte_por_dia   ]");
        Log.e(TAG, "listMetricasPersonal.size() = " + listMetricasPersonal.size());
        Log.e(TAG, "listPersonal.size() = " + listPersonal.size());
        Log.e(TAG, "nCountWorkers = " + nCountWorkers);
        //
        for (int i = 0; i < nCountWorkers; i++) {
            try {

                AllPersonalMetricas worker = new AllPersonalMetricas();
                //
                worker.setDni(listPersonal.get(i).getDni());
                worker.setName(listPersonal.get(i).getName());
                worker.setLast(listPersonal.get(i).getLast());
                worker.setAge(listPersonal.get(i).getAge());
                worker.setAddress(listPersonal.get(i).getAddress());
                worker.setBorn(listPersonal.get(i).getBorn());
                worker.setDate(listPersonal.get(i).getDate());
                worker.setPhone1(listPersonal.get(i).getPhone1());
                worker.setPhone2(listPersonal.get(i).getPhone2());
                //
                worker.setTempurature(listMetricasPersonal.get(i).getTempurature());
                worker.setSo2(listMetricasPersonal.get(i).getSo2());
                worker.setPulse(listMetricasPersonal.get(i).getPulse());
                worker.setSymptoms(listMetricasPersonal.get(i).getSymptoms());
                worker.setDateRegister(listMetricasPersonal.get(i).getDateRegister());
                worker.setWho_user_register(listMetricasPersonal.get(i).getWho_user_register());
                worker.setTestpruebarapida(listMetricasPersonal.get(i).getTestpruebarapida());
                worker.setHorario(listMetricasPersonal.get(i).getHorario());
                //
                worker.setS1(listMetricasPersonal.get(i).getS1());
                worker.setS2(listMetricasPersonal.get(i).getS2());
                worker.setS3(listMetricasPersonal.get(i).getS3());
                worker.setS4(listMetricasPersonal.get(i).getS4());
                worker.setS5(listMetricasPersonal.get(i).getS5());
                worker.setS6(listMetricasPersonal.get(i).getS6());
                worker.setS7(listMetricasPersonal.get(i).getS7());
                //
                Log.e(TAG, "  " + worker.getAllInfoWorker2());
                list_workers.add(worker);
            } catch (Exception e) {
                Log.e(TAG, " ERROR TRY -CATCH  " + e.getMessage());
            }

        }

        // Tratameindo : order por Apellido
        if (list_workers.size() >= 1) {
            Collections.sort(list_workers, (o1, o2) -> new String(o1.getLast()).compareToIgnoreCase(o2.getLast()));
        }
        //
        if (list_workers.size() >= 1) {
            //
            int pageWidth = 1200;
            int pageHeigt = 2010;
            Date currentDate = new Date();
            java.text.DateFormat dateFormat;
            // Crear el documento
            PdfDocument pdfDocument = new PdfDocument();
            PdfDocument.PageInfo myPageInfo01 = new PdfDocument.PageInfo.Builder(pageWidth, pageHeigt, 1).create();
            PdfDocument.Page myPage01 = pdfDocument.startPage(myPageInfo01);
            Canvas cansas01 = myPage01.getCanvas();
            //
            Paint myPaint = new Paint();
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
            String cad_horario = "";
            if (horario) {
                cad_horario = "Entrada";
            } else {
                cad_horario = "Salida";
            }

            cansas01.drawText("Horario  : " + cad_horario, 20, 220, info);
            cansas01.drawText("Responsable : " + Common.currentUser.getReg_name(), 20, 270, info);
            cansas01.drawText("Fecha consulta : " + seletedDate, 20, 320, info);
            // Encabezados
            myPaint.setStyle(Paint.Style.STROKE);
            myPaint.setStrokeWidth(2);
            myPaint.setTextSize(23f);
            cansas01.drawRect(20, 360, pageWidth - 20, 440, myPaint);
            //
            myPaint.setTextAlign(Paint.Align.LEFT);
            myPaint.setStyle(Paint.Style.FILL);
            // se imprime los campos de documentos
            Paint celda = new Paint();
            celda.setStyle(Paint.Style.STROKE);
            celda.setStrokeWidth(2);
            celda.setTextSize(23f);
            cansas01.drawText("Nro.", 50, 415, celda);
            cansas01.drawText("DNI", 170, 415, celda);
            cansas01.drawText("APELLIDOS Y NOMBRES ", 330, 415, celda);
            cansas01.drawText("TEMPERATURA", 760, 415, celda);
            cansas01.drawText("SO2.", 990, 415, celda);
            cansas01.drawText("PULSO", 1090, 415, celda);
            // se dibuja los recuerado o celdas de los campos
            cansas01.drawLine(120, 380, 120, 430, celda);
            cansas01.drawLine(280, 380, 280, 430, celda);
            cansas01.drawLine(730, 380, 730, 430, celda);
            cansas01.drawLine(960, 380, 960, 430, celda);
            cansas01.drawLine(1070, 380, 1070, 430, celda);
            // el aumento para cada fila para los empleados
            int yInit = 480;
            int ysum = 0;
            //
            Paint temp = new Paint();
            Paint so = new Paint();
            so.setTextSize(25f);
            Paint pulse = new Paint();
            pulse.setTextSize(25f);
            //
            if (nCountWorkers < 28) {
                //-------------------------------------------------------------------------------
                // Page 01-01 : [0-28]
                try {
                    for (int i = 0; i < nCountWorkers; i++) {
                        // Poner en mayuscula el Apellido y Nombre
                        String fullName = list_workers.get(i).getLast() + " , " + list_workers.get(i).getName();
                        //Saturacion  color
                        int valueSatura = Integer.parseInt(list_workers.get(i).getSo2());
                        setColorSaturacion(valueSatura, so);
                        //Pulso color
                        int valuePulso = Integer.parseInt(list_workers.get(i).getPulse());
                        setColorPulso(valuePulso, pulse);
                        //
                        cansas01.drawText(i + 1 + ".", 60, yInit + ysum, myPaint);
                        cansas01.drawText(list_workers.get(i).getDni(), 140, yInit + ysum, myPaint);
                        cansas01.drawText(fullName.toUpperCase(), 300, yInit + ysum, myPaint);
                        cansas01.drawText(list_workers.get(i).getTempurature(), 830, yInit + ysum, myPaint);
                        cansas01.drawText(list_workers.get(i).getSo2(), 1000, yInit + ysum, so);
                        cansas01.drawText(list_workers.get(i).getPulse(), 1105, yInit + ysum, pulse);
                        // el aumento en fila
                        ysum = ysum + 50;
                    }
                    //
                    pdfDocument.finishPage(myPage01);
                    File file = new File(Environment.getExternalStorageDirectory(), folderpdf);
                    pdfDocument.writeTo(new FileOutputStream(file));
                    pdfDocument.close();
                } catch (IOException e) {
                    Log.e(TAG, "try-catch :  Page 01 " + e.getMessage());
                }
                //-------------------------------------------------------------------------------
            } else if (nCountWorkers >= 29 && nCountWorkers <= 60) {
                //-------------------------------------------------------------------------------
                try {
                    //----------------------------------------------->
                    // Page 01-02
                    // [0-28]
                    //----------------------------------------------->
                    for (int i = 0; i < 28; i++) {
                        // Poner en mayuscula el Apellido y Nombre
                        String fullName = list_workers.get(i).getLast() + " , " + list_workers.get(i).getName();
                        //Saturacion  color
                        int valueSatura = Integer.parseInt(list_workers.get(i).getSo2());
                        setColorSaturacion(valueSatura, so);
                        //Pulso color
                        int valuePulso = Integer.parseInt(list_workers.get(i).getPulse());
                        setColorPulso(valuePulso, pulse);
                        //
                        cansas01.drawText(i + 1 + ".", 60, yInit + ysum, myPaint);
                        cansas01.drawText(list_workers.get(i).getDni(), 140, yInit + ysum, myPaint);
                        cansas01.drawText(fullName.toUpperCase(), 300, yInit + ysum, myPaint);
                        cansas01.drawText(list_workers.get(i).getTempurature(), 830, yInit + ysum, myPaint);
                        cansas01.drawText(list_workers.get(i).getSo2(), 1000, yInit + ysum, so);
                        cansas01.drawText(list_workers.get(i).getPulse(), 1105, yInit + ysum, pulse);
                        // el aumento en fila
                        ysum = ysum + 50;
                    }
                    //
                    pdfDocument.finishPage(myPage01);
                    //----------------------------------------------->
                    // Page 02-02 :
                    // [29-63]
                    //----------------------------------------------->
                    PdfDocument.PageInfo myPageInfo2 = new PdfDocument.PageInfo.Builder(pageWidth, pageHeigt, 2).create();
                    PdfDocument.Page myPage2 = pdfDocument.startPage(myPageInfo2);
                    Canvas canvas02 = myPage2.getCanvas();
                    //
                    yInit = 100;
                    ysum = 0;
                    for (int i = 29; i < nCountWorkers; i++) {
                        // Up FullName
                        String fullName = list_workers.get(i).getLast() + " , " + list_workers.get(i).getName();
                        //Saturacion  color
                        int valueSatura = Integer.parseInt(list_workers.get(i).getSo2());
                        setColorSaturacion(valueSatura, so);
                        //Pulso color
                        int valuePulso = Integer.parseInt(list_workers.get(i).getPulse());
                        setColorPulso(valuePulso, pulse);
                        //
                        canvas02.drawText(i + ".", 60, yInit + ysum, myPaint);
                        canvas02.drawText(list_workers.get(i).getDni(), 140, yInit + ysum, myPaint);
                        canvas02.drawText(fullName.toUpperCase(), 300, yInit + ysum, myPaint);
                        canvas02.drawText(list_workers.get(i).getTempurature(), 830, yInit + ysum, myPaint);
                        canvas02.drawText(list_workers.get(i).getSo2(), 1000, yInit + ysum, so);
                        canvas02.drawText(list_workers.get(i).getPulse(), 1105, yInit + ysum, pulse);
                        // el aumento en fila
                        ysum = ysum + 50;
                    }
                    pdfDocument.finishPage(myPage2);
                    //----------------------------------------------->
                    // creacion del pdf
                    //----------------------------------------------->
                    File file = new File(Environment.getExternalStorageDirectory(), folderpdf);
                    pdfDocument.writeTo(new FileOutputStream(file));
                    pdfDocument.close();
                } catch (IOException e) {
                    Log.e(TAG, "try-catch : Page 02 " + e.getMessage());
                }

            } else if (nCountWorkers >= 61 && nCountWorkers <= 92) {
                Toast.makeText(this, "nCountWorkers >= 67 && nCountWorkers <= 90", Toast.LENGTH_SHORT).show();
                try {
                    //----------------------------------------------->
                    // Page 01-03
                    // [0-28]
                    //----------------------------------------------->
                    for (int i = 0; i < 28; i++) {
                        // Poner en mayuscula el Apellido y Nombre
                        String fullName = list_workers.get(i).getLast() + " , " + list_workers.get(i).getName();
                        //Saturacion  color
                        int valueSatura = Integer.parseInt(list_workers.get(i).getSo2());
                        setColorSaturacion(valueSatura, so);
                        //Pulso color
                        int valuePulso = Integer.parseInt(list_workers.get(i).getPulse());
                        setColorPulso(valuePulso, pulse);
                        //
                        cansas01.drawText(i + 1 + ".", 60, yInit + ysum, myPaint);
                        cansas01.drawText(list_workers.get(i).getDni(), 140, yInit + ysum, myPaint);
                        cansas01.drawText(fullName.toUpperCase(), 300, yInit + ysum, myPaint);
                        cansas01.drawText(list_workers.get(i).getTempurature(), 830, yInit + ysum, myPaint);
                        cansas01.drawText(list_workers.get(i).getSo2(), 1000, yInit + ysum, so);
                        cansas01.drawText(list_workers.get(i).getPulse(), 1105, yInit + ysum, pulse);
                        // el aumento en fila
                        ysum = ysum + 50;
                    }
                    //
                    pdfDocument.finishPage(myPage01);
                    //----------------------------------------------->
                    // Page 02-03 :
                    // [29-63]
                    //----------------------------------------------->
                    PdfDocument.PageInfo myPageInfo2 = new PdfDocument.PageInfo.Builder(pageWidth, pageHeigt, 2).create();
                    PdfDocument.Page myPage2 = pdfDocument.startPage(myPageInfo2);
                    Canvas canvas02 = myPage2.getCanvas();
                    //
                    yInit = 100;
                    ysum = 0;

                    int listTemp = 63;

                    for (int i = 29; i < listTemp; i++) {
                        //
                        String fullName = list_workers.get(i).getLast() + " , " + list_workers.get(i).getName();
                        //Saturacion  color
                        int valueSatura = Integer.parseInt(list_workers.get(i).getSo2());
                        setColorSaturacion(valueSatura, so);
                        //Pulso color
                        int valuePulso = Integer.parseInt(list_workers.get(i).getPulse());
                        setColorPulso(valuePulso, pulse);
                        //
                        canvas02.drawText(i + ".", 60, yInit + ysum, myPaint);
                        canvas02.drawText(list_workers.get(i).getDni(), 140, yInit + ysum, myPaint);
                        canvas02.drawText(fullName.toUpperCase(), 300, yInit + ysum, myPaint);
                        canvas02.drawText(list_workers.get(i).getTempurature(), 830, yInit + ysum, myPaint);
                        canvas02.drawText(list_workers.get(i).getSo2(), 1000, yInit + ysum, so);
                        canvas02.drawText(list_workers.get(i).getPulse(), 1105, yInit + ysum, pulse);
                        // el aumento en fila
                        ysum = ysum + 50;
                    }
                    pdfDocument.finishPage(myPage2);
                    //----------------------------------------------->
                    // Pagina 03-03
                    // [66-90]
                    //----------------------------------------------->
                    PdfDocument.PageInfo myPageInfo3 = new PdfDocument.PageInfo.Builder(1200, 2010, 3).create();
                    PdfDocument.Page myPage3 = pdfDocument.startPage(myPageInfo3);
                    Canvas canvas03 = myPage3.getCanvas();

                    for (int i = 64; i < nCountWorkers; i++) {
                        //
                        String fullName = list_workers.get(i).getLast() + " , " + list_workers.get(i).getName();
                        //Saturacion  color
                        int valueSatura = Integer.parseInt(list_workers.get(i).getSo2());
                        setColorSaturacion(valueSatura, so);
                        //Pulso color
                        int valuePulso = Integer.parseInt(list_workers.get(i).getPulse());
                        setColorPulso(valuePulso, pulse);
                        //
                        canvas03.drawText(i + ".", 60, yInit + ysum, myPaint);
                        canvas03.drawText(list_workers.get(i).getDni(), 140, yInit + ysum, myPaint);
                        canvas03.drawText(fullName.toUpperCase(), 300, yInit + ysum, myPaint);
                        canvas03.drawText(list_workers.get(i).getTempurature(), 830, yInit + ysum, myPaint);
                        canvas03.drawText(list_workers.get(i).getSo2(), 1000, yInit + ysum, so);
                        canvas03.drawText(list_workers.get(i).getPulse(), 1105, yInit + ysum, pulse);
                        // el aumento en fila
                        ysum = ysum + 50;
                    }
                    pdfDocument.finishPage(myPage3);
                    //----------------------------------------------->
                    // creacion del pdf
                    //----------------------------------------------->
                    File file = new File(Environment.getExternalStorageDirectory(), folderpdf);
                    pdfDocument.writeTo(new FileOutputStream(file));
                    pdfDocument.close();
                } catch (IOException e) {
                    Log.e(TAG, "try-catch : Page 02 " + e.getMessage());
                }

            } else if (nCountWorkers >= 93 && nCountWorkers <= 124) {
                Toast.makeText(this, "nCountWorkers >= 99 && nCountWorkers <= 133", Toast.LENGTH_SHORT).show();
                try {
                    //----------------------------------------------->
                    // Page 01-04
                    // [0-28]
                    //----------------------------------------------->
                    for (int i = 0; i < 28; i++) {
                        // Poner en mayuscula el Apellido y Nombre
                        String fullName = list_workers.get(i).getLast() + " , " + list_workers.get(i).getName();
                        //Saturacion  color
                        int valueSatura = Integer.parseInt(list_workers.get(i).getSo2());
                        setColorSaturacion(valueSatura, so);
                        //Pulso color
                        int valuePulso = Integer.parseInt(list_workers.get(i).getPulse());
                        setColorPulso(valuePulso, pulse);
                        //
                        cansas01.drawText(i + 1 + ".", 60, yInit + ysum, myPaint);
                        cansas01.drawText(list_workers.get(i).getDni(), 140, yInit + ysum, myPaint);
                        cansas01.drawText(fullName.toUpperCase(), 300, yInit + ysum, myPaint);
                        cansas01.drawText(list_workers.get(i).getTempurature(), 830, yInit + ysum, myPaint);
                        cansas01.drawText(list_workers.get(i).getSo2(), 1000, yInit + ysum, so);
                        cansas01.drawText(list_workers.get(i).getPulse(), 1105, yInit + ysum, pulse);
                        // el aumento en fila
                        ysum = ysum + 50;
                    }
                    //
                    pdfDocument.finishPage(myPage01);
                    //----------------------------------------------->
                    // Page 02-04 :
                    // [29-63]
                    //----------------------------------------------->
                    PdfDocument.PageInfo myPageInfo2 = new PdfDocument.PageInfo.Builder(pageWidth, pageHeigt, 2).create();
                    PdfDocument.Page myPage2 = pdfDocument.startPage(myPageInfo2);
                    Canvas canvas02 = myPage2.getCanvas();
                    //
                    yInit = 100;
                    ysum = 0;

                    int listTemp = 63;

                    for (int i = 29; i < listTemp; i++) {
                        //
                        String fullName = list_workers.get(i).getLast() + " , " + list_workers.get(i).getName();
                        //Saturacion  color
                        int valueSatura = Integer.parseInt(list_workers.get(i).getSo2());
                        setColorSaturacion(valueSatura, so);
                        //Pulso color
                        int valuePulso = Integer.parseInt(list_workers.get(i).getPulse());
                        setColorPulso(valuePulso, pulse);
                        //
                        canvas02.drawText(i + ".", 60, yInit + ysum, myPaint);
                        canvas02.drawText(list_workers.get(i).getDni(), 140, yInit + ysum, myPaint);
                        canvas02.drawText(fullName.toUpperCase(), 300, yInit + ysum, myPaint);
                        canvas02.drawText(list_workers.get(i).getTempurature(), 830, yInit + ysum, myPaint);
                        canvas02.drawText(list_workers.get(i).getSo2(), 1000, yInit + ysum, so);
                        canvas02.drawText(list_workers.get(i).getPulse(), 1105, yInit + ysum, pulse);
                        // el aumento en fila
                        ysum = ysum + 50;
                    }
                    pdfDocument.finishPage(myPage2);
                    //----------------------------------------------->
                    // Pagina 03-04
                    // [66-90]
                    //----------------------------------------------->
                    PdfDocument.PageInfo myPageInfo3 = new PdfDocument.PageInfo.Builder(1200, 2010, 3).create();
                    PdfDocument.Page myPage3 = pdfDocument.startPage(myPageInfo3);
                    Canvas canvas03 = myPage3.getCanvas();

                    listTemp = 90;

                    for (int i = 64; i < listTemp; i++) {
                        //
                        String fullName = list_workers.get(i).getLast() + " , " + list_workers.get(i).getName();
                        //Saturacion  color
                        int valueSatura = Integer.parseInt(list_workers.get(i).getSo2());
                        setColorSaturacion(valueSatura, so);
                        //Pulso color
                        int valuePulso = Integer.parseInt(list_workers.get(i).getPulse());
                        setColorPulso(valuePulso, pulse);
                        //
                        canvas03.drawText(i + ".", 60, yInit + ysum, myPaint);
                        canvas03.drawText(list_workers.get(i).getDni(), 140, yInit + ysum, myPaint);
                        canvas03.drawText(fullName.toUpperCase(), 300, yInit + ysum, myPaint);
                        canvas03.drawText(list_workers.get(i).getTempurature(), 830, yInit + ysum, myPaint);
                        canvas03.drawText(list_workers.get(i).getSo2(), 1000, yInit + ysum, so);
                        canvas03.drawText(list_workers.get(i).getPulse(), 1105, yInit + ysum, pulse);
                        // el aumento en fila
                        ysum = ysum + 50;
                    }
                    pdfDocument.finishPage(myPage3);

                    //----------------------------------------------->
                    // Pagina 04-04
                    // [99-133]
                    //----------------------------------------------->
                    PdfDocument.PageInfo myPageInfo4 = new PdfDocument.PageInfo.Builder(1200, 2010, 3).create();
                    PdfDocument.Page myPage4 = pdfDocument.startPage(myPageInfo4);
                    Canvas canvas04 = myPage4.getCanvas();
                    //
                    for (int i = 99; i < nCountWorkers; i++) {
                        //
                        String fullName = list_workers.get(i).getLast() + " , " + list_workers.get(i).getName();
                        //Saturacion  color
                        int valueSatura = Integer.parseInt(list_workers.get(i).getSo2());
                        setColorSaturacion(valueSatura, so);
                        //Pulso color
                        int valuePulso = Integer.parseInt(list_workers.get(i).getPulse());
                        setColorPulso(valuePulso, pulse);
                        //
                        canvas04.drawText(i + ".", 60, yInit + ysum, myPaint);
                        canvas04.drawText(list_workers.get(i).getDni(), 140, yInit + ysum, myPaint);
                        canvas04.drawText(fullName.toUpperCase(), 300, yInit + ysum, myPaint);
                        canvas04.drawText(list_workers.get(i).getTempurature(), 830, yInit + ysum, myPaint);
                        canvas04.drawText(list_workers.get(i).getSo2(), 1000, yInit + ysum, so);
                        canvas04.drawText(list_workers.get(i).getPulse(), 1105, yInit + ysum, pulse);
                        // el aumento en fila
                        ysum = ysum + 50;
                    }
                    pdfDocument.finishPage(myPage4);
                    //----------------------------------------------->
                    // creacion del pdf
                    //----------------------------------------------->
                    File file = new File(Environment.getExternalStorageDirectory(), folderpdf);
                    pdfDocument.writeTo(new FileOutputStream(file));
                    pdfDocument.close();

                } catch (Exception e) {
                    Log.e(TAG, "try-catch : Page 02 " + e.getMessage());
                }

            } else if (nCountWorkers >= 125 && nCountWorkers <= 150) {
                Toast.makeText(this, "nCountWorkers >= 134 && nCountWorkers <= 150", Toast.LENGTH_SHORT).show();
                try {
                    //----------------------------------------------->
                    // Page 01-04
                    // [0-28]
                    //----------------------------------------------->
                    for (int i = 0; i < 28; i++) {
                        // Poner en mayuscula el Apellido y Nombre
                        String fullName = list_workers.get(i).getLast() + " , " + list_workers.get(i).getName();
                        //Saturacion  color
                        int valueSatura = Integer.parseInt(list_workers.get(i).getSo2());
                        setColorSaturacion(valueSatura, so);
                        //Pulso color
                        int valuePulso = Integer.parseInt(list_workers.get(i).getPulse());
                        setColorPulso(valuePulso, pulse);
                        //
                        cansas01.drawText(i + 1 + ".", 60, yInit + ysum, myPaint);
                        cansas01.drawText(list_workers.get(i).getDni(), 140, yInit + ysum, myPaint);
                        cansas01.drawText(fullName.toUpperCase(), 300, yInit + ysum, myPaint);
                        cansas01.drawText(list_workers.get(i).getTempurature(), 830, yInit + ysum, myPaint);
                        cansas01.drawText(list_workers.get(i).getSo2(), 1000, yInit + ysum, so);
                        cansas01.drawText(list_workers.get(i).getPulse(), 1105, yInit + ysum, pulse);
                        // el aumento en fila
                        ysum = ysum + 50;
                    }
                    //
                    pdfDocument.finishPage(myPage01);
                    //----------------------------------------------->
                    // Page 02-04 :
                    // [29-63]
                    //----------------------------------------------->
                    PdfDocument.PageInfo myPageInfo2 = new PdfDocument.PageInfo.Builder(pageWidth, pageHeigt, 2).create();
                    PdfDocument.Page myPage2 = pdfDocument.startPage(myPageInfo2);
                    Canvas canvas02 = myPage2.getCanvas();
                    //
                    yInit = 100;
                    ysum = 0;

                    int listTemp = 63;

                    for (int i = 29; i < listTemp; i++) {
                        //
                        String fullName = list_workers.get(i).getLast() + " , " + list_workers.get(i).getName();
                        //Saturacion  color
                        int valueSatura = Integer.parseInt(list_workers.get(i).getSo2());
                        setColorSaturacion(valueSatura, so);
                        //Pulso color
                        int valuePulso = Integer.parseInt(list_workers.get(i).getPulse());
                        setColorPulso(valuePulso, pulse);

                        //
                        canvas02.drawText(i + ".", 60, yInit + ysum, myPaint);
                        canvas02.drawText(list_workers.get(i).getDni(), 140, yInit + ysum, myPaint);
                        canvas02.drawText(fullName.toUpperCase(), 300, yInit + ysum, myPaint);
                        canvas02.drawText(list_workers.get(i).getTempurature(), 830, yInit + ysum, myPaint);
                        canvas02.drawText(list_workers.get(i).getSo2(), 1000, yInit + ysum, so);
                        canvas02.drawText(list_workers.get(i).getPulse(), 1105, yInit + ysum, pulse);
                        // el aumento en fila
                        ysum = ysum + 50;
                    }
                    pdfDocument.finishPage(myPage2);
                    //----------------------------------------------->
                    // Pagina 03-04
                    // [64-90]
                    //----------------------------------------------->
                    PdfDocument.PageInfo myPageInfo3 = new PdfDocument.PageInfo.Builder(1200, 2010, 3).create();
                    PdfDocument.Page myPage3 = pdfDocument.startPage(myPageInfo3);
                    Canvas canvas03 = myPage3.getCanvas();

                    listTemp = 90;

                    for (int i = 91; i < listTemp; i++) {
                        //
                        String fullName = list_workers.get(i).getLast() + " , " + list_workers.get(i).getName();
                        //Saturacion  color
                        int valueSatura = Integer.parseInt(list_workers.get(i).getSo2());
                        setColorSaturacion(valueSatura, so);
                        //Pulso color
                        int valuePulso = Integer.parseInt(list_workers.get(i).getPulse());
                        setColorPulso(valuePulso, pulse);

                        //
                        canvas03.drawText(i + ".", 60, yInit + ysum, myPaint);
                        canvas03.drawText(list_workers.get(i).getDni(), 140, yInit + ysum, myPaint);
                        canvas03.drawText(fullName.toUpperCase(), 300, yInit + ysum, myPaint);
                        canvas03.drawText(list_workers.get(i).getTempurature(), 830, yInit + ysum, myPaint);
                        canvas03.drawText(list_workers.get(i).getSo2(), 1000, yInit + ysum, so);
                        canvas03.drawText(list_workers.get(i).getPulse(), 1105, yInit + ysum, pulse);
                        // el aumento en fila
                        ysum = ysum + 50;
                    }
                    pdfDocument.finishPage(myPage3);

                    //----------------------------------------------->
                    // Pagina 04-04
                    // [99-133]
                    //----------------------------------------------->
                    PdfDocument.PageInfo myPageInfo4 = new PdfDocument.PageInfo.Builder(1200, 2010, 3).create();
                    PdfDocument.Page myPage4 = pdfDocument.startPage(myPageInfo4);
                    Canvas canvas04 = myPage4.getCanvas();
                    //
                    listTemp = 133;
                    for (int i = 99; i < listTemp; i++) {
                        //
                        String fullName = list_workers.get(i).getLast() + " , " + list_workers.get(i).getName();
                        //Saturacion  color
                        int valueSatura = Integer.parseInt(list_workers.get(i).getSo2());
                        setColorSaturacion(valueSatura, so);
                        //Pulso color
                        int valuePulso = Integer.parseInt(list_workers.get(i).getPulse());
                        setColorPulso(valuePulso, pulse);
                        //
                        canvas04.drawText(i + ".", 60, yInit + ysum, myPaint);
                        canvas04.drawText(list_workers.get(i).getDni(), 140, yInit + ysum, myPaint);
                        canvas04.drawText(fullName.toUpperCase(), 300, yInit + ysum, myPaint);
                        canvas04.drawText(list_workers.get(i).getTempurature(), 830, yInit + ysum, myPaint);
                        canvas04.drawText(list_workers.get(i).getSo2(), 1000, yInit + ysum, so);
                        canvas04.drawText(list_workers.get(i).getPulse(), 1105, yInit + ysum, pulse);
                        // el aumento en fila
                        ysum = ysum + 50;
                    }
                    pdfDocument.finishPage(myPage4);

                    //----------------------------------------------->
                    // Pagina 05-05
                    // [134-150]
                    //----------------------------------------------->
                    PdfDocument.PageInfo myPageInfo5 = new PdfDocument.PageInfo.Builder(1200, 2010, 3).create();
                    PdfDocument.Page myPage5 = pdfDocument.startPage(myPageInfo5);
                    Canvas canvas05 = myPage5.getCanvas();
                    //
                    for (int i = 134; i < nCountWorkers; i++) {
                        //
                        String fullName = list_workers.get(i).getLast() + " , " + list_workers.get(i).getName();
                        //Saturacion  color
                        int valueSatura = Integer.parseInt(list_workers.get(i).getSo2());
                        setColorSaturacion(valueSatura, so);
                        //Pulso color
                        int valuePulso = Integer.parseInt(list_workers.get(i).getPulse());
                        setColorPulso(valuePulso, pulse);
                        //
                        canvas05.drawText(i + ".", 60, yInit + ysum, myPaint);
                        canvas05.drawText(list_workers.get(i).getDni(), 140, yInit + ysum, myPaint);
                        canvas05.drawText(fullName.toUpperCase(), 300, yInit + ysum, myPaint);
                        canvas05.drawText(list_workers.get(i).getTempurature(), 830, yInit + ysum, myPaint);
                        canvas05.drawText(list_workers.get(i).getSo2(), 1000, yInit + ysum, so);
                        canvas05.drawText(list_workers.get(i).getPulse(), 1105, yInit + ysum, pulse);
                        // el aumento en fila
                        ysum = ysum + 50;
                    }
                    pdfDocument.finishPage(myPage5);
                    //----------------------------------------------->
                    // creacion del pdf
                    //----------------------------------------------->
                    File file = new File(Environment.getExternalStorageDirectory(), folderpdf);
                    pdfDocument.writeTo(new FileOutputStream(file));
                    pdfDocument.close();

                } catch (Exception e) {
                    Log.e(TAG, "try-catch : Page 02 " + e.getMessage());
                }

            }

            if (metodo.equalsIgnoreCase("pdf")) {
                Log.e(TAG, " metodo pdf ");
                Intent intent = new Intent(ReportDataWorkerActivity.this, ShowPdfActivity2.class);
                startActivity(intent);
            } else {
                Log.e(TAG, " metodo sendEmail ");
                sendEmail();
            }
            mDialog.dismiss();
        }
        mDialog.dismiss();

    }


    private void setColorSaturacion(int valueSatura, Paint so) {
        if (valueSatura >= 95 && valueSatura <= 99) {
            so.setColor(Color.rgb(17, 230, 165));
        } else if (valueSatura >= 91 && valueSatura <= 94) {
            so.setColor(Color.rgb(255, 235, 59));
        } else if (valueSatura >= 86 && valueSatura <= 90) {
            so.setColor(Color.rgb(255, 38, 38));
        } else {
            so.setColor(Color.rgb(255, 38, 38));
        }
    }

    private void setColorPulso(int valuePulso, Paint pulse) {
        if (valuePulso >= 86) {
            pulse.setColor(Color.rgb(17, 230, 165));
        } else if (valuePulso >= 70 && valuePulso <= 84) {
            pulse.setColor(Color.rgb(255, 235, 59));
        } else if (valuePulso >= 62 && valuePulso <= 68) {
            pulse.setColor(Color.rgb(255, 38, 38));
        } else {
            pulse.setColor(Color.rgb(255, 38, 38));
        }
    }


    private void reporterPorTrabajador(String nombre, String metodo, String dni) {
        //
        Log.e(TAG, "---> reporterPorTrabajador()");
        Log.e(TAG, "===> Nombre : " + nombre);
        Log.e(TAG, "===> DNI :  " + dni);
        Log.e(TAG, "tamaño  listDate : " + listDate.size());
        Log.e(TAG, "tamaño listTemperatura : " + listTemperatura.size());
        Log.e(TAG, "tamaño listSaturacion : " + listSaturacion.size());
        Log.e(TAG, "tamaño listPulso : " + listPulso.size());
        Log.e(TAG, "tamaño listTest : " + listTest.size());
        //
        int pageWidth = 1200;
        Date currentDate = new Date();
        java.text.DateFormat dateFormat;
        //
        PdfDocument pdfDocument = new PdfDocument();
        PdfDocument.PageInfo myPageInfo01 = new PdfDocument.PageInfo.Builder(1200, 2010, 1).create();
        PdfDocument.Page myPage01 = pdfDocument.startPage(myPageInfo01);
        Canvas cansas01 = myPage01.getCanvas();
        //
        Paint title = new Paint();
        title.setTextSize(60);
        title.setTextAlign(Paint.Align.CENTER);
        title.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        title.setColor(Color.BLACK);
        cansas01.drawText("UNIDAD DE TRABAJO ", pageWidth / 2, 80, title);
        cansas01.drawText(Common.unidadTrabajoSelected.getAliasUT(), pageWidth / 2, 160, title);

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
        info.setTextSize(30f);
        info.setTextAlign(Paint.Align.LEFT);
        info.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
        info.setColor(Color.BLACK);
        cansas01.drawText("DNI  : " + dni, 20, 220, info);
        cansas01.drawText("Trabajador  : " + nombre, 20, 270, info);
        cansas01.drawText("Responsable : " + Common.currentUser.getReg_name(), 20, 320, info);

        // Encabezados
        Paint myPaint = new Paint();
        myPaint.setStyle(Paint.Style.STROKE);
        myPaint.setStrokeWidth(2);
        myPaint.setTextSize(25f);
        cansas01.drawRect(20, 360, pageWidth - 20, 440, myPaint);
        //
        myPaint.setTextAlign(Paint.Align.LEFT);
        myPaint.setStyle(Paint.Style.FILL);

        cansas01.drawText("Nro.", 55, 415, myPaint);
        cansas01.drawText("Fecha y hora", 200, 415, myPaint);
        cansas01.drawText("TEMPERATURA", 460, 415, myPaint);
        cansas01.drawText("SO2", 710, 415, myPaint);
        cansas01.drawText("Pulso", 810, 415, myPaint);
        cansas01.drawText("Prueba Rápida.", 950, 415, myPaint);

        cansas01.drawLine(140, 380, 140, 430, myPaint);
        cansas01.drawLine(430, 380, 430, 430, myPaint);
        cansas01.drawLine(680, 380, 680, 430, myPaint);
        cansas01.drawLine(780, 380, 780, 430, myPaint);
        cansas01.drawLine(910, 380, 910, 430, myPaint);
        //
        int ytext = 400;
        int ysum = 50;
        int ytextname = 400;
        int ysumname = 50;
        //
        Paint temp = new Paint();
        Paint so = new Paint();
        Paint pulse = new Paint();

        Paint paint_pruebarapida = new Paint();


        so.setTextSize(25f);
        pulse.setTextSize(25f);
        paint_pruebarapida.setTextSize(25f);
        //-------------------------------------------------------------------------------
        //---> Pagina 01 Pagina 01 : [0-28]
        //Promedio
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

            // Temperatura
            sumaTemp = sumaTemp + Double.parseDouble(listTemperatura.get(i));

            // Color SO2
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

            // Color Pulse
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
            // Prueba rapida
            String cad_pruebaRapida = "";
            if (listTest.get(i)) {
                cad_pruebaRapida = "SI";
                paint_pruebarapida.setColor(Color.rgb(17, 230, 165));
            } else {
                cad_pruebaRapida = "NO";
                paint_pruebarapida.setColor(Color.rgb(255, 38, 38));
            }


            try {


                //
                cansas01.drawText(i + 1 + " ", 80, ytext + ysum, myPaint);
                cansas01.drawText(listTemperatura.get(i), 520, ytext + ysum, myPaint);
                cansas01.drawText(listSaturacion.get(i).toString(), 710, ytext + ysum, so);
                cansas01.drawText(listPulso.get(i).toString(), 810, ytext + ysum, pulse);
                cansas01.drawText(cad_pruebaRapida, 1010, ytext + ysum, paint_pruebarapida);
                //
            } catch (Exception e) {
                Log.e(TAG, "ERROR " + e.getMessage());
            }
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

            Paint promedio = new Paint();
            promedio.setTextSize(35f);
            promedio.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
            promedio.setTextAlign(Paint.Align.LEFT);
            promedio.setColor(Color.BLACK);

            cansas01.drawText("Promedio de los últimos " + size + " días", 35, 1600, promedio);
            cansas01.drawText("1)Promedio temperatura : " + cadTemp.substring(0, 4), 45, 1650, myPaint);
            cansas01.drawText("2)Promedio Saturación de oxigeno  : " + cadSa.substring(0, 4), 45, 1700, myPaint);
            cansas01.drawText("3)Promedio pulso : " + cadPulse.substring(0, 4), 45, 1750, myPaint);
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
            Intent intent = new Intent(ReportDataWorkerActivity.this, ShowPdfActivity2.class);
            startActivity(intent);
            mDialog.dismiss();
        } else if (metodo.equalsIgnoreCase("email")) {
            Log.e(TAG, " metodo sendEmail ");
            sendEmail();
            mDialog.dismiss();
        }


    }

    private void reporterPorTrabajadorSintomas(String nombre, String metodo, String dni) {
        //
        Log.e(TAG, "---> reporterPorTrabajador()");
        Log.e(TAG, "===> Nombre : " + nombre);
        Log.e(TAG, "===> DNI :  " + dni);
        Log.e(TAG, "tamaño  listDate : " + listDate.size());
        Log.e(TAG, "tamaño  s1 : " + s1.size());
        Log.e(TAG, "tamaño  s7 : " + s7.size());
        //
        int pageWidth = 1200;
        Date currentDate = new Date();
        java.text.DateFormat dateFormat;
        //
        PdfDocument pdfDocument = new PdfDocument();
        PdfDocument.PageInfo myPageInfo01 = new PdfDocument.PageInfo.Builder(1200, 2010, 1).create();
        PdfDocument.Page myPage01 = pdfDocument.startPage(myPageInfo01);
        Canvas cansas01 = myPage01.getCanvas();
        //
        Paint title = new Paint();
        title.setTextSize(60);
        title.setTextAlign(Paint.Align.CENTER);
        title.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        title.setColor(Color.BLACK);
        cansas01.drawText("UNIDAD DE TRABAJO ", pageWidth / 2, 80, title);
        cansas01.drawText(Common.unidadTrabajoSelected.getAliasUT(), pageWidth / 2, 160, title);

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
        info.setTextSize(30f);
        info.setTextAlign(Paint.Align.LEFT);
        info.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
        info.setColor(Color.BLACK);
        cansas01.drawText("DNI  : " + dni, 20, 220, info);
        cansas01.drawText("Trabajador  : " + nombre, 20, 270, info);
        cansas01.drawText("Responsable : " + Common.currentUser.getReg_name(), 20, 320, info);

        // Encabezados
        Paint myPaint = new Paint();
        myPaint.setStyle(Paint.Style.STROKE);
        myPaint.setStrokeWidth(2);
        myPaint.setTextSize(25f);
        cansas01.drawRect(20, 360, pageWidth - 20, 440, myPaint);
        //
        myPaint.setTextAlign(Paint.Align.LEFT);
        myPaint.setStyle(Paint.Style.FILL);

        cansas01.drawText("Nro.", 55, 415, myPaint);
        cansas01.drawText("Fecha y hora", 200, 415, myPaint);
        cansas01.drawText("s1.", 600, 415, myPaint);
        cansas01.drawText("s2.", 680, 415, myPaint);
        cansas01.drawText("s3.", 760, 415, myPaint);
        cansas01.drawText("s4.", 840, 415, myPaint);
        cansas01.drawText("s5.", 920, 415, myPaint);
        cansas01.drawText("s6.", 1000, 415, myPaint);
        cansas01.drawText("s7.", 1080, 415, myPaint);


        //
        int ytext = 400;
        int ysum = 50;
        int ytextname = 400;
        int ysumname = 50;
        //
        Paint temp = new Paint();
        Paint so = new Paint();
        Paint pulse = new Paint();

        Paint paint_pruebarapida = new Paint();


        so.setTextSize(25f);
        pulse.setTextSize(25f);
        paint_pruebarapida.setTextSize(25f);
        //-------------------------------------------------------------------------------
        //---> Pagina 01 Pagina 01 : [0-28]
        //Promedio
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

            cansas01.drawText(i + 1 + " ", 42, ytextname + ysumname, myPaint);
            cansas01.drawText(listDate.get(i), 170, ytextname + ysumname, myPaint);

            if (s1.get(i)) {
                cansas01.drawText("si", 600, ytextname + ysumname, myPaint);
            } else {
                cansas01.drawText("no", 600, ytextname + ysumname, myPaint);
            }
            if (s2.get(i)) {
                cansas01.drawText("si", 680, ytextname + ysumname, myPaint);
            } else {
                cansas01.drawText("no", 680, ytextname + ysumname, myPaint);
            }
            if (s3.get(i)) {
                cansas01.drawText("si", 760, ytextname + ysumname, myPaint);
            } else {
                cansas01.drawText("no", 760, ytextname + ysumname, myPaint);
            }
            if (s4.get(i)) {
                cansas01.drawText("si", 840, ytextname + ysumname, myPaint);
            } else {
                cansas01.drawText("no", 840, ytextname + ysumname, myPaint);
            }

            if (s5.get(i)) {
                cansas01.drawText("si", 920, ytextname + ysumname, myPaint);
            } else {
                cansas01.drawText("no", 920, ytextname + ysumname, myPaint);
            }

            if (s6.get(i)) {
                cansas01.drawText("si", 1000, ytextname + ysumname, myPaint);
            } else {
                cansas01.drawText("no", 1000, ytextname + ysumname, myPaint);
            }
            if (s7.get(i)) {
                cansas01.drawText("si", 1080, ytextname + ysumname, myPaint);
            } else {
                cansas01.drawText("no", 1080, ytextname + ysumname, myPaint);
            }

        }



        try {

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
            Intent intent = new Intent(ReportDataWorkerActivity.this, ShowPdfActivity2.class);
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
        listTest = new ArrayList<>();

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

                        try {
                            if (metricasPersonal.getHorario()) {
                                listDate.add(metricasPersonal.getDateRegister());
                                listTemperatura.add((metricasPersonal.getTempurature()));
                                listSaturacion.add(Integer.parseInt(metricasPersonal.getSo2()));
                                listPulso.add(Integer.parseInt(metricasPersonal.getPulse()));
                                listTest.add(metricasPersonal.getTestpruebarapida());
                            }
                        } catch (Exception e) {
                            Log.e(TAG, " no tiene horario ");
                        }


                    } else {
                        mDialog.dismiss();
                        Log.e(TAG, "getDataFromFirebase --> MetricasPersonal = NULL");
                    }
                }

                Log.e(TAG, "---> listDate : " + listDate.size());
                Log.e(TAG, "---> listTemperatura : " + listTemperatura.size());
                Log.e(TAG, "---> listSaturacion : " + listSaturacion.size());
                Log.e(TAG, "---> listPulso : " + listPulso.size());
                Log.e(TAG, "---> listTest : " + listTest.size());

                try {
                    reporterPorTrabajador(nombre, metodo, dni);
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

    private void getDataFromFirebaseSintomas(String dni, String nombre, String metodo) {
        Log.e(TAG, "-----> funcion  : getDataFromFirebase");
        listDate = new ArrayList<String>();
        //
        s1 = new ArrayList<>();
        s2 = new ArrayList<>();
        s3 = new ArrayList<>();
        s4 = new ArrayList<>();
        s5 = new ArrayList<>();
        s6 = new ArrayList<>();
        s7 = new ArrayList<>();

        Query query = FirebaseDatabase
                .getInstance()
                .getReference(Common.db_mina_personal_data).child(Common.unidadTrabajoSelected.getNameUT())
                .child(dni)
                .orderByKey()
                .limitToLast(15);

        query
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        //
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            MetricasPersonal metricasPersonal = snapshot.getValue(MetricasPersonal.class);

                            if (metricasPersonal != null) {

                                try {
                                    if (metricasPersonal.getHorario()) {
                                        listDate.add(metricasPersonal.getDateRegister());
                                        s1.add(metricasPersonal.getS1());
                                        s2.add(metricasPersonal.getS2());
                                        s3.add(metricasPersonal.getS3());
                                        s4.add(metricasPersonal.getS4());
                                        s5.add(metricasPersonal.getS5());
                                        s6.add(metricasPersonal.getS6());
                                        s7.add(metricasPersonal.getS7());



                                    }
                                } catch (Exception e) {
                                    Log.e(TAG, " no tiene horario ");
                                }


                            } else {
                                mDialog.dismiss();
                                Log.e(TAG, "getDataFromFirebase --> MetricasPersonal = NULL");
                            }
                        }

                        Log.e(TAG, "---> listDate : " + listDate.size());


                        try {
                            reporterPorTrabajadorSintomas(nombre, metodo, dni);
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

    public void showPopUpHistorialSintomas(String metodo) {


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

        btn_report_dni
                .setOnClickListener(v -> {

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

                        DatabaseReference ref_mina = database
                                .getReference(Common.db_mina_personal)
                                .child(Common.unidadTrabajoSelected.getNameUT());

                        ref_mina
                                .child(dni)
                                .addValueEventListener(new ValueEventListener() {
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
                                            getDataFromFirebaseSintomas(dni, fullname, metodo);
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


}
//
// Prueba unitarias
    /*
  List<AllPersonalMetricas> list_temp = list_workers;
            Log.e(TAG,"old  list ");
                    for(AllPersonalMetricas tempOld:list_workers){
                    Log.e("tempOld","-----------------------");
                    Log.e("tempOld"," "+tempOld.getAllInfoWorker2());

                    }
                    //   Collections.sort(list_workers, (o1, o2) -> o1.getLast().compareToIgnoreCase(o2.getLast()));

                    //
                    Log.e(TAG," ");
                    Log.e(TAG,"new list sorted");
                    Log.e(TAG," ");
                    for(AllPersonalMetricas tempNew:list_workers){
                    Log.e("tempNew","-----------------------");
                    Log.e("tempNew"," "+tempNew.getAllInfoWorker2());

                    }
                    // para los dos
                    Log.e(TAG," ");
                    Log.e(TAG,"Comparando las dos lista ");
                    Log.e(TAG," ");
                    int valorLista=list_workers.size();
                    for(int j=0;j<valorLista; j++){
        Log.e("list_workers"," "+list_workers.get(j).getAllInfoWorker2());
        Log.e("list_temp"," "+list_temp.get(j).getAllInfoWorker2());
        }

     */
// ser ordena por apellido

