package com.cudpast.appminas.Principal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.cudpast.appminas.Common.Common;
import com.cudpast.appminas.Model.DatosPersonal;
import com.cudpast.appminas.Model.Personal;
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

public class InputDataPersonalActivity extends AppCompatActivity {

    private TextView show_consulta_nombre, show_consulta_edad;

    private TextInputLayout input_dni_layout, input_temperatura_layout, input_saturacion_layout, input_pulso_layout, input_sintomas_layout;
    private TextInputEditText input_dni, input_temperatura, input_saturacion, input_pulso;
    private EditText input_sintomas;
    private Button btn_input_consulta, btn_input_data, btn_input_back;

    public static final String TAG = InputDataPersonalActivity.class.getSimpleName();
    private FirebaseDatabase database = FirebaseDatabase.getInstance();

    private Personal personal;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_input_data_minero);

        show_consulta_nombre = findViewById(R.id.show_consulta_nombre);
        show_consulta_edad = findViewById(R.id.show_consulta_edad);

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

        btn_input_consulta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String dni_personal = input_dni.getText().toString();
                consultarDniPersonal(dni_personal);
            }
        });

        btn_input_data.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(InputDataPersonalActivity.this, "Ingresar", Toast.LENGTH_SHORT).show();
                if (submitForm()) {
                    savePersonalData();
                }
            }
        });

        btn_input_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(InputDataPersonalActivity.this, AllActivity.class);
                startActivity(intent);
                finish();
            }
        });


    }

    private void savePersonalData() {
        DatabaseReference ref_db_mina_personal_data = database.getReference(Common.db_mina_personal_data);
        // DatosPersonal(String tempurature, String so2, String pulse, String symptoms, String dateRegister)
        String tempurature = input_temperatura.getText().toString();
        String so2 = input_saturacion.getText().toString();
        String pulse = input_pulso.getText().toString();
        String symptoms = input_sintomas.getText().toString();
        String dateRegister = "hola";

        DatosPersonal datosPersonal = new DatosPersonal();


        ref_db_mina_personal_data
                .child(Common.unidadMineraSelected)
                .child(personal.getDni())
                .setValue("user")
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.e(TAG, Common.unidadMineraSelected);
                        Log.e(TAG, "datos registrado");
                        Toast.makeText(InputDataPersonalActivity.this, "Personal Registrador", Toast.LENGTH_SHORT).show();
                        gotoMAin();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(InputDataPersonalActivity.this, "Personal NO Registrador", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "datos no registrado");

                    }
                });

    }

    private void gotoMAin() {
        Intent intent = new Intent(InputDataPersonalActivity.this, AllActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    private void consultarDniPersonal(String dni_personal) {
        DatabaseReference ref_db_mina_personal = database.getReference(Common.db_mina_personal);
        DatabaseReference ref_mina = ref_db_mina_personal.child(Common.unidadMineraSelected);
        ref_mina.child(dni_personal).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                personal = dataSnapshot.getValue(Personal.class);
                if (personal != null) {
                    Log.e(TAG, "nombre : " + personal.getName());
                    Log.e(TAG, "dni : " + personal.getDni());
                    Log.e(TAG, "direccion : " + personal.getAddress());
                    Log.e(TAG, "phone 1 : " + personal.getPhone1());
                    show_consulta_nombre.setText("Nombre : " + personal.getName());
                    show_consulta_edad.setText("Edad : " + personal.getAge() + " años");
                    show_consulta_nombre.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.color_error_null));
                    show_consulta_edad.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.color_error_null));
                } else {
                    Log.e(TAG, "el usuario no existe en  ");
                    show_consulta_nombre.setText("el usuario no existe en " + Common.unidadMineraSelected);
                    show_consulta_nombre.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.color_error));
                    show_consulta_edad.setText(" ");
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
        if (input_temperatura.getText().toString().trim().isEmpty()) {
            input_temperatura_layout.setError("Ingrese su nombre");
            return false;
        } else {
            input_temperatura_layout.setError(null);
        }
        return true;
    }

    private boolean checkSaturacion() {
        if (input_saturacion.getText().toString().trim().isEmpty()) {
            input_saturacion_layout.setError("Ingrese su edad");
            return false;
        } else {
            input_saturacion_layout.setError(null);
        }
        return true;
    }

    private boolean checkPulso() {
        if (input_pulso.getText().toString().trim().isEmpty()) {
            input_pulso_layout.setError("Ingrese su dirección");
            return false;
        } else {
            input_pulso_layout.setError(null);
        }
        return true;
    }

    private boolean checkSintomas() {
        if (input_sintomas.getText().toString().trim().isEmpty()) {
            input_sintomas_layout.setError("Ingrese su lugar de nacimiento");
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


        return true;
    }


}