package com.cudpast.appminas.Principal.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.cudpast.appminas.Common.Common;
import com.cudpast.appminas.Model.MetricasPersonal;
import com.cudpast.appminas.Model.Personal;
import com.cudpast.appminas.Principal.AllActivity;
import com.cudpast.appminas.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;

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


    public static final String TAG = InputDataWorkerActivity.class.getSimpleName();
    private FirebaseDatabase database = FirebaseDatabase.getInstance();

    private Personal personal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setTitle("Ingresar datos");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_input_data_minero);

        // todo : control de trabjao
        // todo : prueba rapida
        // todo : sintomas check ... ok


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


        toggleCheckHorario();
        toggleCheck();
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
            finish();
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


    private void toggleCheck() {

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
        input_saturacion_layout.setEnabled(true);
        input_pulso_layout.setEnabled(true);
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
        // si es verdadero es turno entrada
        // si es falso es turno salida
        metricasPersonal.setHorario(horario);


        ref_db_mina_personal_data
                .child(Common.unidadTrabajoSelected.getNameUT())
                .child(personal.getDni())
                .child(date_atention)
                .setValue(metricasPersonal)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(InputDataWorkerActivity.this, "Excelente , datos registrados !!!!", Toast.LENGTH_SHORT).show();
                        gotoMAin();

                        Log.e(TAG, "datos registrado");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(InputDataWorkerActivity.this, "Error al ingresar los datos", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "datos no registrado");

                    }
                });

    }

    private void gotoMAin() {
        Intent intent = new Intent(InputDataWorkerActivity.this, AllActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    private void consultarDniPersonal(String dni_personal) {
        DatabaseReference ref_db_mina_personal = database.getReference(Common.db_mina_personal);
        DatabaseReference ref_mina = ref_db_mina_personal.child(Common.unidadTrabajoSelected.getNameUT());
        ref_mina.child(dni_personal).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                personal = dataSnapshot.getValue(Personal.class);
                if (personal != null) {
                    Log.e(TAG, "nombre : " + personal.getName());
                    Log.e(TAG, "dni : " + personal.getDni());
                    Log.e(TAG, "dirección : " + personal.getAddress());
                    Log.e(TAG, "phone 1 : " + personal.getPhone1());
                    show_consulta_lastname.setText(personal.getLast());
                    show_consulta_firstname.setText(personal.getName());
                    show_consulta_lastname.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.color_error_null));
                    show_consulta_firstname.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.color_error_null));
                    checkEnable();
                    input_dni_layout.setError(null);
                } else {
                    Log.e(TAG, "el trabjador no existe en  ");
                    show_consulta_lastname.setText("");
                    show_consulta_lastname.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.color_error));
                    show_consulta_firstname.setText("");
                    notEnable();
                    input_dni_layout.setError("El trabajador no exsite en la base de datos");

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "error : " + databaseError.getMessage());
            }
        });

    }

    //Validacion
    private boolean checkDNI() {
        if (input_dni.getText().toString().trim().isEmpty()) {
            input_dni_layout.setError("Ingrese su DNI");
            return false;
        } else {
            input_dni_layout.setError(null);
        }
        return true;
    }

    private boolean checkTemperatura() {

        try {
            if (input_temperatura.getText().toString().trim().isEmpty() && input_temperatura.getText().toString() != null) {
                input_temperatura_layout.setError("falta ingresar temperatura del trabajador");
                Log.e("number", " int   " + Integer.parseInt(input_temperatura.getText().toString()));
                return false;
            } else {
                input_temperatura_layout.setError(null);
                Log.e("number", " int   " + Integer.parseInt(input_temperatura.getText().toString()));
                if (input_temperatura.getText().toString() != null) {
                    int value = Integer.parseInt(input_temperatura.getText().toString());
                    if (value < 35 || value > 43) {
                        input_temperatura_layout.setError("Solo rango [35 - 43]");
                        Log.e("number", " int   " + Integer.parseInt(input_temperatura.getText().toString()));
                        return false;
                    }
                }
            }


        } catch (Exception e) {
            e.getMessage();
        }

        return true;
    }

    private boolean checkSaturacion() {
        if (input_saturacion.getText().toString().trim().isEmpty() && input_saturacion.getText().toString().trim() != null) {
            input_saturacion_layout.setError("falta ingresar SO2 del trabajador");
            return false;
        } else {
            input_saturacion_layout.setError(null);
            //
            if (input_saturacion.getText().toString().trim() != null) {
                int value = Integer.parseInt(input_saturacion.getText().toString());
                if (value < 85 || value > 100) {
                    input_saturacion_layout.setError("solo rango [85-100]");
                    return false;
                }
            }
        }
        return true;
    }

    private boolean checkPulso() {
        if (input_pulso.getText().toString().trim().isEmpty() && input_pulso.getText().toString() != null) {
            input_pulso_layout.setError("falta ingresare el pulso del trabajador ");
            return false;
        } else {
            input_pulso_layout.setError(null);
            if (input_pulso.getText().toString().trim() != null) {
                int value = Integer.parseInt(input_pulso.getText().toString());
                if (value < 50 || value > 115) {
                    input_pulso_layout.setError("Solo rango [50-115]");
                    return false;
                }
            }
        }
        return true;
    }

    private boolean checkSintomas() {
        if (input_sintomas.getText().toString().trim().isEmpty()) {
            input_sintomas_layout.setError("falta ingresar los sistomas del paciente");
            return false;
        } else {
            input_sintomas_layout.setError(null);
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
