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
import com.cudpast.appminas.Model.AllPersonalMetricas;
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
import java.util.Collections;
import java.util.Comparator;
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

    private List<MetricasPersonal> listaMetricasPersonalesEntrada;
    private List<MetricasPersonal> listaMetricasPersonalesSalida;


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
    List<Boolean> listTest;
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

        // Report 2
        // todo : falta diseño de formato pdf
        img_reportworkpdf.setOnClickListener(v -> showPdfDialog("pdf"));
        img_reportworkgmail.setOnClickListener(v -> showEmailoDialog("email"));
        // Report 3
        img_reportexampdf.setOnClickListener(v -> showTestEmailDNI("pdf"));
        img_reportexamemail.setOnClickListener(v -> showTestEmailDNI("email"));
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
                    filtrarFecha(dataSnapshot, metodo, horario);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.e(TAG, "[showSelectDate ] error : " + databaseError.getMessage());
                    mDialog.dismiss();
                }
            });

        });
    }

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
                    filtrarFecha(dataSnapshot, metodo, horario);
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


    //===============================================================================
    // CardView 3


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
            //Horario
            listaMetricasPersonalesEntrada = new ArrayList<>();
            listaMetricasPersonalesSalida = new ArrayList<>();

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
                    // filtrarFecha(dataSnapshot, metodo);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.e(TAG, "[showSelectDate ] error : " + databaseError.getMessage());
                    mDialog.dismiss();
                }
            });

        });
    }

    private void filtrarFecha(DataSnapshot dataAll, String metodo, boolean horario) {

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
                                Log.e(TAG, "======================== ");
                                Log.e(TAG, "getDateRegister = " + data.getDateRegister());
                                Log.e(TAG, "getHorario = " + data.getHorario());
                                Log.e(TAG, "getPulse = " + data.getPulse());
                                Log.e(TAG, "getSo2 = " + data.getSo2());
                                Log.e(TAG, "getSymptoms = " + data.getSymptoms());
                                Log.e(TAG, "getTempurature = " + data.getTempurature());
                                Log.e(TAG, "getTestpruebarapida = " + data.getTestpruebarapida());
                                Log.e(TAG, "getWho_user_register = " + data.getWho_user_register());

                                if (data.getHorario() == horario) {
                                    listaMetricasPersonalesEntrada.add(data);
                                    listDNI.add(dni);
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
            String dni = listDNI.get(i);
            DatabaseReference ref_mina = database.getReference(Common.db_mina_personal).child(Common.unidadTrabajoSelected.getNameUT()).child(dni);
            ref_mina.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Personal personal = dataSnapshot.getValue(Personal.class);
                    if (personal != null) {
                        listaPersonal.add(personal);
                    }
                    if (listaPersonal.size() == listDNI.size()) {
                        //-->
                        reporte_por_dia(listaMetricasPersonalesEntrada, listaPersonal, seletedDate, metodo, horario);
                    }
                    Log.e(TAG, "[filtrarFecha]-onDataChange  listaPersonal.size()  : " + listaPersonal.size());
                    Log.e(TAG, "[filtrarFecha]-onDataChange  listDNI.size()  : " + listDNI.size());
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.e(TAG, "[filtrarFecha]-onCancelled databaseError : " + databaseError.getMessage());
                }
            });

        }

    }

    private void reporte_por_dia(List<MetricasPersonal> listMetricasPersonal, List<Personal> listPersonal, String seletedDate, String metodo, boolean horario) {
        Log.e(TAG, ">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        Log.e(TAG, "[   reporte_por_dia   ]");
        Log.e(TAG, "listMetricasPersonal.size() = " + listMetricasPersonal.size());
        Log.e(TAG, "listPersonal.size() = " + listPersonal.size());

        // todo :  aqui se juntaria las dos lista para order por Apellido
        List<AllPersonalMetricas> list_workers = new ArrayList<>();
        int nCountWorkers = listMetricasPersonal.size();
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

                Log.e(TAG, "  " + worker.getAllInfoWorker());
                list_workers.add(worker);
            } catch (Exception e) {
                Log.e(TAG, " ERROR TRY -CATCH  " + e.getMessage());
            }

        }

        // Tratameindo : order por Apellido
        if (list_workers.size() >= 1) {
            // JAVA SORT
            Collections.sort(list_workers, new Comparator<AllPersonalMetricas>() {
                @Override
                public int compare(AllPersonalMetricas o1, AllPersonalMetricas o2) {
                    return o1.getLast().compareToIgnoreCase(o2.getLast());
                }
            });
            // JAVA Sort TEST
            Log.e(TAG, "nueva lista ordenada");
            for (AllPersonalMetricas temp : list_workers) {
                Log.e(TAG, " Dni : " + temp.getDni());
                Log.e(TAG, " Apellido : " + temp.getLast());
            }

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
            myPaint.setTextSize(25f);
            cansas01.drawRect(20, 360, pageWidth - 20, 440, myPaint);
            //
            myPaint.setTextAlign(Paint.Align.LEFT);
            myPaint.setStyle(Paint.Style.FILL);
            // se imprime los campos de documentos
            cansas01.drawText("Nro.", 50, 415, myPaint);
            cansas01.drawText("DNI", 170, 415, myPaint);
            cansas01.drawText("APELLIDOS Y NOMBRES ", 330, 415, myPaint);
            cansas01.drawText("TEMPERATURA", 760, 415, myPaint);
            cansas01.drawText("SO2.", 990, 415, myPaint);
            cansas01.drawText("PULSO", 1090, 415, myPaint);
            // se dibuja los recuerado o celdas de los campos
            cansas01.drawLine(120, 380, 120, 430, myPaint);
            cansas01.drawLine(280, 380, 280, 430, myPaint);
            cansas01.drawLine(730, 380, 730, 430, myPaint);
            cansas01.drawLine(960, 380, 960, 430, myPaint);
            cansas01.drawLine(1070, 380, 1070, 430, myPaint);
            // el aumento para cada fila para los empleados
            int ytext = 480;
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
                        //
                        if (list_workers.get(i).getLast() == null) {
                            list_workers.get(i).setLast("");
                        }
                        //Saturacion  color
                        int valueSatura = Integer.parseInt(list_workers.get(i).getSo2());
                        setColorSaturacion(valueSatura, so);

                        //Pulso color
                        int valuePulso = Integer.parseInt(list_workers.get(i).getPulse());
                        setColorPulso(valuePulso, pulse);
                        //
                        cansas01.drawText(i + 1 + ".", 60, ytext + ysum, myPaint);
                        cansas01.drawText(list_workers.get(i).getDni(), 140, ytext + ysum, myPaint);
                        cansas01.drawText(list_workers.get(i).getLast() + " , " + listPersonal.get(i).getName(), 300, ytext + ysum, myPaint);
                        cansas01.drawText(list_workers.get(i).getTempurature(), 830, ytext + ysum, myPaint);
                        cansas01.drawText(list_workers.get(i).getSo2(), 1000, ytext + ysum, so);
                        cansas01.drawText(list_workers.get(i).getPulse(), 1105, ytext + ysum, pulse);
                        // el aumento en fila
                        ysum = ysum + 50;
                    }
                    //
                    pdfDocument.finishPage(myPage01);
                    File file = new File(Environment.getExternalStorageDirectory(), "/arsi21.pdf");
                    pdfDocument.writeTo(new FileOutputStream(file));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                pdfDocument.close();
                //-------------------------------------------------------------------------------
            } else if (nCountWorkers >= 29 && nCountWorkers <= 63) {

            } else if (nCountWorkers >= 67 && nCountWorkers <= 90) {

            } else if (nCountWorkers >= 99 && nCountWorkers <= 133) {

            } else if (nCountWorkers >= 134 && nCountWorkers <= 150) {

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

    //===============================================================================

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
