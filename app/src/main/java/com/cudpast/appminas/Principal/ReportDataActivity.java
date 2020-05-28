package com.cudpast.appminas.Principal;

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
import android.widget.TextView;
import android.widget.Toast;

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

public class ReportDataActivity extends AppCompatActivity {

    public static final String TAG = ReportDataActivity.class.getSimpleName();
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
    //
    private ImageView img_reportdatepdf, img_reportmailpdf, img_reportworkpdf, img_reportworkgmail, img_reportexampdf, img_reportexamemail;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setTitle("Regresar");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_export);
        //Solicitar permisos
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PackageManager.PERMISSION_GRANTED);
        //
        img_reportdatepdf = findViewById(R.id.img_reportdatepdf);
        img_reportmailpdf = findViewById(R.id.img_reportmailpdf);
        img_reportworkpdf = findViewById(R.id.img_reportworkpdf);
        img_reportworkgmail = findViewById(R.id.img_reportworkgmail);
        img_reportexampdf = findViewById(R.id.img_reportexampdf);
        img_reportexamemail = findViewById(R.id.img_reportexamemail);
        //
        img_reportdatepdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ReportDataActivity.this, "1", Toast.LENGTH_SHORT).show();
                selectDate("pdf");
            }
        });
        img_reportmailpdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ReportDataActivity.this, "2", Toast.LENGTH_SHORT).show();
                selectDate("email");

            }
        });
        //
        img_reportworkpdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ReportDataActivity.this, "3", Toast.LENGTH_SHORT).show();
                showPdfDialog();

            }
        });
        img_reportworkgmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEmailoDialog();
                Toast.makeText(ReportDataActivity.this, "4", Toast.LENGTH_SHORT).show();
            }
        });
        img_reportexampdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ReportDataActivity.this, "5", Toast.LENGTH_SHORT).show();
            }
        });
        img_reportexamemail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ReportDataActivity.this, "6 ", Toast.LENGTH_SHORT).show();
            }
        });


    }

    private void selectDate(String metodo) {
        dialogCalendar = MaterialDatePicker.Builder.datePicker();
        dialogCalendar.setTitleText("Seleccionar Fecha");
        materialDatePicker = dialogCalendar.build();
        materialDatePicker.show(getSupportFragmentManager(), "DATE_PICKER");
        materialDatePicker.addOnPositiveButtonClickListener((MaterialPickerOnPositiveButtonClickListener<Long>) dateSelected -> {
            //
            mDialog = new ProgressDialog(ReportDataActivity.this);
            mDialog.setMessage("Obteniendo datos ...");
            mDialog.show();

            // Fecha escogida
            seletedDate = timestampToString(dateSelected);
            // inicializar lista vacias para guardar datos
            listaMetricasPersonales = new ArrayList<>();
            listaPersonal = new ArrayList<>();
            // Conexion con la base de datos
            ref_datos_paciente = FirebaseDatabase.getInstance().getReference(Common.db_mina_personal_data).child(Common.unidadTrabajoSelected.getNameUT());
            ref_datos_paciente.keepSynced(true);
            ref_datos_paciente.orderByKey();
            ref_datos_paciente.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot snapshotDatosPersonales : dataSnapshot.getChildren()) {
                        // --> DNI del Personal
                        String dniPersonal = snapshotDatosPersonales.getKey();
                        Log.e(TAG, "dniPersonal : " + dniPersonal);
                        if (dniPersonal.isEmpty()) {
                            Log.e(TAG, "campo vacio  : " + null);
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
                                                Personal personal = dataSnapshot.getValue(Personal.class);
                                                if (personal != null) {
                                                    listaPersonal.add(personal);
                                                    Log.e(TAG, listaPersonal.size() + " personal : " + personal.getName());
                                                    Log.e(TAG, "tamaño de  metricas  : " + listaMetricasPersonales.size());
                                                    Log.e(TAG, "tamaño de  personal  : " + listaPersonal.size());
                                                    Log.e(TAG, "metodo : " + metodo);
                                                    if (listaMetricasPersonales.size() == listaPersonal.size()) {


                                                        generarListaporFechaPdf(listaMetricasPersonales, listaPersonal, seletedDate, metodo);
                                                        Log.e(TAG, "se genero la lista pdf");
                                                    } else {
                                                        Log.e(TAG, "todavia no se  genero la lista pdf");
                                                    }
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                                Log.e(TAG, "error : " + databaseError.getMessage());

                                            }
                                        });

                                    }


                                } else {
                                    Log.e(TAG, "metricasPersonal : lista vacia");
                                }
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.e(TAG, "error : " + databaseError.getMessage());
                }
            });
        });


    }


    private void generarListaporFechaPdf(List<MetricasPersonal> listMetricasPersonal, List<Personal> listPersonal, String seletedDate, String metodo) {
        //
        Log.e(TAG, "-----> generarListaporFechaPdf");
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


        if (metodo.equalsIgnoreCase("pdf")) {
            Log.e(TAG, " metodo pdf ");
            Intent intent = new Intent(ReportDataActivity.this, ShowPdfActivity.class);
            startActivity(intent);
            mDialog.dismiss();
        } else {
            Log.e(TAG, " metodo sendEmail ");
            sendEmail();
            mDialog.dismiss();
        }


    }

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

        // info trabajador

        for (int i = 0; i < listDate.size(); i++) {
            ysumname = ysumname + 50;
            cansas01.drawText(listDate.get(i), 170, ytextname + ysumname, myPaint);
        }


        for (int i = 0; i < listTemperatura.size(); i++) {
            ysum = ysum + 50;
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


        }

        /*
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
        */

        //-------------------------------------------------------------------------------

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
            Intent intent = new Intent(ReportDataActivity.this, ShowPdfActivity.class);
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

                try {
                    generarListaporPersonalPdf(nombre, metodo);
                } catch (Exception e) {
                    Log.e(TAG, "ERROR --> getDataFromFirebase : " + e.getMessage());
                    Toast.makeText(ReportDataActivity.this, "Error al Generar PDF ", Toast.LENGTH_SHORT).show();
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

    private String timestampToString(long time) {
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


    /////////

    public void showPdfDialog() {
        String metodo = "pdf";
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(ReportDataActivity.this);
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
                Toast.makeText(this, "Documento generado", Toast.LENGTH_SHORT).show();
            }
        });

        btn_report_dni_close.setOnClickListener(v -> dialog.dismiss());
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

    public void showEmailoDialog() {
        String metodo = "email";
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(ReportDataActivity.this);
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
                Toast.makeText(this, "Documento generado", Toast.LENGTH_SHORT).show();
            }
        });

        btn_report_dni_close.setOnClickListener(v -> dialog.dismiss());
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }


}
