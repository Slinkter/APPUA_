package com.cudpast.appminas.Principal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
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
import android.widget.Toast;

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
    private FirebaseDatabase database = FirebaseDatabase.getInstance();

    private DatabaseReference ref_datos_paciente;
    private List<MetricasPersonal> listaMetricasPersonales;
    private List<Personal> listaPersonal;
    private Button btn_selectDate, btn_viewPdf;
    private TextView tv_show_date;
    private MaterialDatePicker.Builder dialogCalendar;
    private MaterialDatePicker materialDatePicker;
    private String seletedDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_export);
        //Solicitar permisos
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PackageManager.PERMISSION_GRANTED);
        //
        btn_selectDate = findViewById(R.id.btn_selectDate);
        btn_viewPdf = findViewById(R.id.btn_viewPdf);
        //
        tv_show_date = findViewById(R.id.tv_show_date);
        //
        dialogCalendar = MaterialDatePicker.Builder.datePicker();
        dialogCalendar.setTitleText("Seleccionar Fecha");
        materialDatePicker = dialogCalendar.build();

        btn_selectDate.setOnClickListener(v -> materialDatePicker.show(getSupportFragmentManager(), "DATE_PICKER"));

        materialDatePicker.addOnPositiveButtonClickListener((MaterialPickerOnPositiveButtonClickListener<Long>) dateSelected -> {

            tv_show_date.setVisibility(View.VISIBLE);
            tv_show_date.setText("Fecha Selecionada :  " + timestampToString(dateSelected));
            btn_viewPdf.setVisibility(View.VISIBLE);

            seletedDate = timestampToString(dateSelected);

            listaMetricasPersonales = new ArrayList<>();
            listaPersonal = new ArrayList<>();

            ref_datos_paciente = FirebaseDatabase.getInstance().getReference(Common.db_mina_personal_data).child(Common.unidadTrabajoSelected.getNameUT());
            ref_datos_paciente.keepSynced(true);
            ref_datos_paciente.orderByKey();
            ref_datos_paciente.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot snapshotDatosPersonales : dataSnapshot.getChildren()) {
                        // --> DNI del Personal
                        String dniPersonal = snapshotDatosPersonales.getKey();
                        // --> Lista de snapshotDatosPersonales
                        for (DataSnapshot item : snapshotDatosPersonales.getChildren()) {
                            MetricasPersonal metricasPersonal = item.getValue(MetricasPersonal.class);
                            if (metricasPersonal != null) {
                                String dateRegister = metricasPersonal.getDateRegister().substring(0, 10).trim();
                                //Buscar dateRegister q coincide
                                if (dateRegister.equalsIgnoreCase(seletedDate)) {
                                    // Si la dateRegister coincide enlistar datos
                                    listaMetricasPersonales.add(metricasPersonal);
                                    Log.e(TAG, "tamaño metricas  : " + listaMetricasPersonales.size());
                                    // Buscar datos
                                    DatabaseReference ref_db_mina_personal = database.getReference(Common.db_mina_personal);
                                    DatabaseReference ref_mina = ref_db_mina_personal.child(Common.unidadTrabajoSelected.getNameUT());
                                    //
                                    ref_mina.child(dniPersonal).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            Personal personal = dataSnapshot.getValue(Personal.class);
                                            if (personal != null) {
                                                listaPersonal.add(personal);
                                                Log.e(TAG, "tamaño personal : " + listaPersonal.size());
                                            }

                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {
                                            Log.e(TAG, "error : " + databaseError.getMessage());
                                        }
                                    });
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


            btn_viewPdf.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(ExportActivity.this, "Hola soy pdf", Toast.LENGTH_SHORT).show();
                    try {
                        generarPdf(listaMetricasPersonales, listaPersonal, seletedDate);
                        Intent intent = new Intent(ExportActivity.this, ShowPdfActivity.class);
                        startActivity(intent);
                    } catch (Exception e) {
                        Log.e(TAG, "btn_viewPdf - Error = " + e.getMessage());
                    }

                }
            });

        });
    }


    private String timestampToString(long time) {
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
        calendar.setTimeInMillis(time);
        String date = DateFormat.format("yyyy-MM-dd", calendar).toString();
        return date;
    }


    private void generarPdf(List<MetricasPersonal> listMetricasPersonal, List<Personal> listPersonal, String seletedDate) {
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
        //
        int ytext = 400;
        int ysum = 100;
        int ytextname = 400;
        int ysumname = 100;

        if (listMetricasPersonal.size() <= 28) {
            //-------------------------------------------------------------------------------
            //---> Pagina 01 Pagina 01 : [0-28]
            //metricas
            for (int i = 0; i < listMetricasPersonal.size(); i++) {
                cansas01.drawText(i + ".", 50, ytext + ysum, myPaint);
                cansas01.drawText(listMetricasPersonal.get(i).getTempurature().toString(), 760, ytext + ysum, myPaint);
                cansas01.drawText(listMetricasPersonal.get(i).getSo2().toString(), 940, ytext + ysum, myPaint);
                cansas01.drawText(listMetricasPersonal.get(i).getPulse().toString(), 1090, ytext + ysum, myPaint);
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
        } else if (listMetricasPersonal.size() >= 27 ) {
            Toast.makeText(this, "pagina 2", Toast.LENGTH_SHORT).show();
            //-------------------------------------------------------------------------------
            //---> Pagina 01 : [0-30]
            for (int i = 1; i <= 27; i++) {
                Log.e(TAG, "pagina 01 " + i );
                cansas01.drawText(i + ".", 50, ytext + ysum, myPaint);
                cansas01.drawText(listMetricasPersonal.get(i).getTempurature().toString(), 760, ytext + ysum, myPaint);
                cansas01.drawText(listMetricasPersonal.get(i).getSo2().toString(), 940, ytext + ysum, myPaint);
                cansas01.drawText(listMetricasPersonal.get(i).getPulse().toString(), 1090, ytext + ysum, myPaint);
                ysum = ysum + 55;
            }
            //
            for (int i = 1; i <= 27; i++) {
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
            for (int i = 28; i < listMetricasPersonal.size(); i++) {
                Log.e(TAG, "pagina 02 " + i );
                canvas2.drawText(i + ".", 50, 30 + y2sum, myPaint);
                canvas2.drawText(listMetricasPersonal.get(i).getTempurature().toString(), 760, 30 + y2sum, myPaint);
                canvas2.drawText(listMetricasPersonal.get(i).getSo2().toString(), 940, 30 + y2sum, myPaint);
                canvas2.drawText(listMetricasPersonal.get(i).getPulse().toString(), 1090, 30 + y2sum, myPaint);
                y2sum = y2sum + 50;
            }
            //
            for (int i = 28; i < listPersonal.size(); i++) {
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

        }else{
            Log.e(TAG, "ERROR problemas de index");
        }


    }


}
