package com.cudpast.appminas.Principal.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.cudpast.appminas.Activities.LoginActivity;
import com.cudpast.appminas.Activities.SplashActivity;
import com.cudpast.appminas.Common.Common;
import com.cudpast.appminas.Model.MetricasPersonal;
import com.cudpast.appminas.Model.Personal;
import com.cudpast.appminas.Principal.AllActivity;
import com.cudpast.appminas.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class InputDataWorkerActivity extends AppCompatActivity {

    private TextView show_consulta_lastname, show_consulta_firstname;
    private TextInputLayout show_consulta_nombre_layout, show_consulta_edad_layout;
    private TextInputLayout input_dni_layout, input_temperatura_layout, input_saturacion_layout, input_pulso_layout, input_sintomas_layout;
    private TextInputEditText input_dni, input_temperatura, input_saturacion, input_pulso;
    private EditText input_sintomas;
    private Button btn_input_consulta, btn_input_data, btn_input_back;

    private CheckBox input_entrada, input_salida;
    private CheckBox input_test_yes, input_test_no;

    private boolean horario;
    private boolean testfastcovid;

    private CheckBox s1, s2, s3, s4, s5, s6, s7;
    private Boolean sa1, sa2, sa3, sa4, sa5, sa6, sa7;

    public static final String TAG = InputDataWorkerActivity.class.getSimpleName();
    private FirebaseDatabase database;

    private Personal personal;

    private static final long SPLASH_SCREEN_DELAY = 1800;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setTitle("Registrar Síntomas");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_input_data_minero);

        database = FirebaseDatabase.getInstance();

        show_consulta_lastname = findViewById(R.id.show_consulta_nombre);
        show_consulta_firstname = findViewById(R.id.show_consulta_edad);

        show_consulta_nombre_layout = findViewById(R.id.show_consulta_nombre_layout);
        show_consulta_edad_layout = findViewById(R.id.show_consulta_edad_layout);

        input_dni_layout = findViewById(R.id.input_dni_layout);
        input_temperatura_layout = findViewById(R.id.input_temperatura_layout);
        input_saturacion_layout = findViewById(R.id.input_saturacion_layout);
        input_pulso_layout = findViewById(R.id.input_pulso_layout);
        input_sintomas_layout = findViewById(R.id.input_sintomas_layout);

        input_dni = findViewById(R.id.input_dni);
        input_temperatura = findViewById(R.id.input_temperatura);
        input_saturacion = findViewById(R.id.input_saturacion);
        input_pulso = findViewById(R.id.input_pulso);

        input_sintomas = findViewById(R.id.input_sintomas);

        btn_input_consulta = findViewById(R.id.btn_input_consulta);
        btn_input_data = findViewById(R.id.btn_input_data);
        btn_input_back = findViewById(R.id.btn_input_back);
        //
        input_entrada = findViewById(R.id.input_entrada);
        input_salida = findViewById(R.id.input_salida);
        //
        input_test_yes = findViewById(R.id.input_test_yes);
        input_test_no = findViewById(R.id.input_test_no);
        //
        s1 = findViewById(R.id.s1);
        s2 = findViewById(R.id.s2);
        s3 = findViewById(R.id.s3);
        s4 = findViewById(R.id.s4);
        s5 = findViewById(R.id.s5);
        s6 = findViewById(R.id.s6);
        s7 = findViewById(R.id.s7);
        //default
        s1.setText("Tos");
        s2.setText("Dolor de Garganta");
        s3.setText("Fiebre");
        s4.setText("Dificultad respitoria");
        s5.setText("Diarrea");
        s6.setText("Dolor abdominal");
        s7.setText("Dolor pecho");
        sa1 = false;
        sa2 = false;
        sa3 = false;
        sa4 = false;
        sa5 = false;
        sa6 = false;
        sa7 = false;
        //
        toggleCheckHorario();
        toggleCheckTest();
        //
        btn_input_consulta.setOnClickListener(v -> {
            String dni_personal = input_dni.getText().toString();
            consultarDniPersonal(dni_personal);
        });

        btn_input_data.setOnClickListener(v -> {
            if (submitForm()) {
                savePersonalData();
            }

        });

        btn_input_back.setOnClickListener(v -> {
            Intent intent = new Intent(InputDataWorkerActivity.this, AllActivity.class);
            startActivity(intent);

        });

        // Capturando los valores de checkbox ...
        s1.setOnCheckedChangeListener((buttonView, isChecked) -> {
            sa1 = isChecked;
            Log.e(TAG, " s1  = " + sa1);
        });

        s2.setOnCheckedChangeListener((buttonView, isChecked) -> {
            sa2 = isChecked;
            Log.e(TAG, " s2  = " + sa2);
        });

        s3.setOnCheckedChangeListener((buttonView, isChecked) -> {
            sa3 = isChecked;
            Log.e(TAG, " s3  = " + sa3);
        });

        s4.setOnCheckedChangeListener((buttonView, isChecked) -> {
            sa4 = isChecked;
            Log.e(TAG, " s4  = " + sa4);
        });

        s5.setOnCheckedChangeListener((buttonView, isChecked) -> {
            sa5 = isChecked;
            Log.e(TAG, " s5  = " + sa5);
        });

        s6.setOnCheckedChangeListener((buttonView, isChecked) -> {
            sa6 = isChecked;
            Log.e(TAG, " s6  = " + sa6);
        });

        s7.setOnCheckedChangeListener((buttonView, isChecked) -> {
            sa7 = isChecked;
            Log.e(TAG, " s7  = " + sa7);
        });


    }


    private void toggleCheckHorario() {
        input_entrada.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                input_salida.setEnabled(false);
                horario = true;
            } else {
                input_salida.setEnabled(true);
            }
        });
        input_salida.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                input_entrada.setEnabled(false);
                horario = false;
            } else {
                input_entrada.setEnabled(true);
            }
        });
    }


    private void toggleCheckTest() {

        input_test_yes.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                input_test_no.setEnabled(false);
                testfastcovid = true;
            } else {
                input_test_no.setEnabled(true);
            }
        });

        input_test_no.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                input_test_yes.setEnabled(false);
                testfastcovid = false;
            } else {
                input_test_yes.setEnabled(true);
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        notEnable();
    }

    private void notEnable() {
        input_temperatura_layout.setEnabled(false);


        input_saturacion_layout.setEnabled(false);
        input_pulso_layout.setEnabled(false);


        input_sintomas_layout.setEnabled(false);
        show_consulta_nombre_layout.setEnabled(false);
        show_consulta_edad_layout.setEnabled(false);

        input_entrada.setEnabled(false);
        input_salida.setEnabled(false);

        input_test_yes.setEnabled(false);
        input_test_no.setEnabled(false);

        s1.setEnabled(false);
        s2.setEnabled(false);
        s3.setEnabled(false);
        s4.setEnabled(false);
        s5.setEnabled(false);
        s6.setEnabled(false);
        s7.setEnabled(false);

    }

    private void checkEnable() {
        input_temperatura_layout.setEnabled(true);

        if (Common.unidadTrabajoSelected.getAliasUT().equals("of.Lima")) {


            input_saturacion_layout.setEnabled(false);
            input_pulso_layout.setEnabled(false);
        } else {
            input_saturacion_layout.setEnabled(true);
            input_pulso_layout.setEnabled(true);
        }


        input_sintomas_layout.setEnabled(true);

        input_entrada.setEnabled(true);
        input_salida.setEnabled(true);

        input_test_yes.setEnabled(true);
        input_test_no.setEnabled(true);

        s1.setEnabled(true);
        s2.setEnabled(true);
        s3.setEnabled(true);
        s4.setEnabled(true);
        s5.setEnabled(true);
        s6.setEnabled(true);
        s7.setEnabled(true);
    }


    private void savePersonalData() {


        DatabaseReference ref_db_mina_personal_data = database.getReference(Common.db_mina_personal_data);
        // fecha
        final String date_atention = getCurrentTimeStamp();
        //
        String tempurature = input_temperatura.getText().toString();
        String so2 = input_saturacion.getText().toString();
        String pulse = input_pulso.getText().toString();
        String symptoms = input_sintomas.getText().toString();
        String dateRegister = date_atention;
        //String tempurature, String so2, String pulse, String symptoms, String dateRegister
        MetricasPersonal metricasPersonal = new MetricasPersonal();
        metricasPersonal.setTempurature(tempurature);
        metricasPersonal.setSo2(so2);
        metricasPersonal.setPulse(pulse);
        metricasPersonal.setSymptoms(symptoms);
        metricasPersonal.setDateRegister(dateRegister);
        metricasPersonal.setWho_user_register(Common.currentUser.getUid()); // requerido
        metricasPersonal.setTestpruebarapida(testfastcovid);
        //
        metricasPersonal.setS1(sa1);
        metricasPersonal.setS2(sa2);
        metricasPersonal.setS3(sa3);
        metricasPersonal.setS4(sa4);
        metricasPersonal.setS5(sa5);
        metricasPersonal.setS6(sa6);
        metricasPersonal.setS7(sa7);

        // si es verdadero es turno entrada
        // si es falso es turno salida
        metricasPersonal.setHorario(horario);


        ProgressDialog mDialog;
        mDialog = new ProgressDialog(InputDataWorkerActivity.this);
        mDialog.setMessage(" Registrando datos del trabajador  " + personal.getName());
        mDialog.show();


        ref_db_mina_personal_data
                .child(Common.unidadTrabajoSelected.getNameUT())
                .child(personal.getDni())
                .child(date_atention)
                .setValue(metricasPersonal)
                .addOnSuccessListener(aVoid -> {
                    //  Toast.makeText(InputDataWorkerActivity.this, "Registro de síntomas completado ", Toast.LENGTH_LONG).show();
                    gotoMAin(personal, metricasPersonal);
                    Log.e(TAG, "datos registrado");
                    mDialog.dismiss();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(InputDataWorkerActivity.this, "Error al ingresar los datos", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "datos no registrado");
                    mDialog.dismiss();

                });

        FirebaseCrashlytics.getInstance().setUserId(Common.currentUser.getReg_email());
        FirebaseCrashlytics.getInstance().log("error en --> savePersonalData");

    }

    private void gotoMAin(Personal personal, MetricasPersonal metricasPersonal) {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(InputDataWorkerActivity.this);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.pop_up_msj_sintomas, null);
        builder.setView(view);
        builder.setCancelable(false);
        view.setKeepScreenOn(true);
        final AlertDialog dialog = builder.create();

        Button btn_pop_up_msj = view.findViewById(R.id.btn_pop_up_msj_sintomas);

        TextView name = view.findViewById(R.id.pop_up_msj_nombre);
        name.setText(personal.getName() + personal.getLast());

        TextView temp = view.findViewById(R.id.pop_up_msj_temperatura);
        temp.setText(metricasPersonal.getTempurature());

        TextView so = view.findViewById(R.id.pop_up_msj_so);
        so.setText(metricasPersonal.getSo2());

        TextView pulse = view.findViewById(R.id.pop_up_msj_pulso);
        pulse.setText(metricasPersonal.getPulse());


        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {

                Intent intent = new Intent(InputDataWorkerActivity.this, AllActivity.class);
                startActivity(intent);
                dialog.dismiss();




                btn_pop_up_msj.setOnClickListener(v -> {

                });

            }
        };

        Timer timer = new Timer();
        timer.schedule(timerTask, SPLASH_SCREEN_DELAY);

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();





    }

    private void consultarDniPersonal(String dni_personal) {
        DatabaseReference ref_db_mina_personal = database.getReference(Common.db_mina_personal);
        DatabaseReference ref_mina = ref_db_mina_personal.child(Common.unidadTrabajoSelected.getNameUT());
        ref_mina.child(dni_personal).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                personal = dataSnapshot.getValue(Personal.class);
                if (personal != null) {
                    try {
                        if (personal.getLast() == null) {
                            personal.setLast(" <-");
                        }
                        if (personal.getName() == null) {
                            personal.setName(" ->");
                        }

                        show_consulta_lastname.setText(personal.getLast());
                        show_consulta_firstname.setText(personal.getName());
                        show_consulta_lastname.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.color_error_null));
                        show_consulta_firstname.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.color_error_null));
                        checkEnable();
                        input_dni_layout.setError(null);
                    } catch (Exception e) {
                        Log.e(TAG, "ERROR " + e.getMessage());
                    }


                } else {
                    show_consulta_lastname.setText("");
                    show_consulta_lastname.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.color_error));
                    show_consulta_firstname.setText("");
                    notEnable();
                    input_dni_layout.setError("El trabajador no exsite en la base de datos");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, " error : " + databaseError.getMessage());
            }
        });

        FirebaseCrashlytics.getInstance().setUserId(Common.currentUser.getReg_email());
        FirebaseCrashlytics.getInstance().log("error en --> consultarDniPersonal()");
    }

    //Validacion
    private boolean checkDNI() {
        if (input_dni.getText().toString().trim().isEmpty()) {
            input_dni_layout.setError("Ingrese su DNI");
            input_dni_layout.requestFocus();
            return false;
        } else {
            input_dni_layout.setError(null);
        }
        return true;
    }

    private boolean checkTemperatura() {
        try {
            if (input_temperatura.getText().toString().trim().isEmpty() && input_temperatura.getText().toString().trim() != null) {
                input_temperatura_layout.setError("Debes ingresar Temperatura ");
                input_temperatura_layout.requestFocus();
                return false;
            } else {
                input_temperatura_layout.setError(null);
                if (input_temperatura.getText().toString() != null) {
                    int value = Integer.parseInt(input_temperatura.getText().toString());
                    if (value < 35 || value > 43) {
                        input_temperatura_layout.setError("Solo rango [35 - 43]");
                        input_temperatura_layout.requestFocus();
                        Log.e("number", " int   " + Integer.parseInt(input_temperatura.getText().toString()));
                        return false;
                    }
                }
            }


        } catch (Exception e) {
            Log.e(TAG, "error - checkTemperatura : " + e.getMessage());
        }

        return true;
    }

    private boolean checkSaturacion() {

        try {

            if (Common.unidadTrabajoSelected.getAliasUT().equals("of.Lima")) {
                input_saturacion.setText("0");
            } else {
                if (input_saturacion.getText().toString().trim().isEmpty() && input_saturacion.getText().toString().trim() != null) {
                    input_saturacion_layout.setError("Debes ingresar SO2 ");
                    input_saturacion_layout.requestFocus();
                    return false;
                } else {
                    input_saturacion_layout.setError(null);
                    if (input_saturacion.getText().toString().trim() != null) {
                        int value = Integer.parseInt(input_saturacion.getText().toString());
                        if (value < 85 || value > 100) {
                            input_saturacion_layout.setError("solo rango [85-100]");
                            input_saturacion_layout.requestFocus();
                            return false;
                        }
                    }
                }
            }

        } catch (Exception e) {
            Log.e(TAG, "error - checkTemperatura : " + e.getMessage());
        }


        return true;
    }

    private boolean checkPulso() {

        try {

            if (Common.unidadTrabajoSelected.getAliasUT().equals("of.Lima")) {
                input_pulso.setText("0");
            } else {
                if (input_pulso.getText().toString().trim().isEmpty() && input_pulso.getText().toString().trim() != null) {
                    input_pulso_layout.setError("Debes ingresar el pulso ");
                    input_pulso_layout.requestFocus();
                    return false;
                } else {
                    input_pulso_layout.setError(null);
                    if (input_pulso.getText().toString().trim() != null) {
                        int value = Integer.parseInt(input_pulso.getText().toString());
                        if (value < 50 || value > 115) {
                            input_pulso_layout.setError("Solo rango [50-115]");
                            input_pulso_layout.requestFocus();
                            return false;
                        }
                    }
                }
            }


        } catch (Exception e) {
            Log.e(TAG, "error - checkTemperatura : " + e.getMessage());
        }


        return true;
    }

    private boolean checkSintomas() {

        try {
            if (input_sintomas.getText().toString().trim().isEmpty()) {
                input_sintomas_layout.setError("falta ingresar los sistomas del paciente");
                return false;
            } else {
                input_sintomas_layout.setError(null);
            }
        } catch (Exception e) {
            Log.e(TAG, "error - checkTemperatura : " + e.getMessage());
        }


        return true;
    }

    //
    private boolean submitForm() {

        if (!checkDNI()) {
            return false;
        }


        if (!checkTemperatura()) {
            return false;
        }

        if (!checkSaturacion()) {
            return false;
        }

        if (!checkPulso()) {
            return false;
        }


        if (!checkSintomas()) {
            return false;
        }

        if ((input_test_no.isChecked() || !input_test_yes.isChecked()) && (!input_test_no.isChecked() || input_test_yes.isChecked())) {
            Toast.makeText(this, "falta  prueba rapida", Toast.LENGTH_SHORT).show();
            return false;
        }


        if ((input_entrada.isChecked() || !input_salida.isChecked()) && (!input_entrada.isChecked() || input_salida.isChecked())) {
            Toast.makeText(this, "Falta validar el horario de Entrada/Salida", Toast.LENGTH_SHORT).show();
            return false;
        }


        return true;
    }

    //
    public static String getCurrentTimeStamp() {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String currentTimeStamp = dateFormat.format(new Date());
            return currentTimeStamp;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    //

}
