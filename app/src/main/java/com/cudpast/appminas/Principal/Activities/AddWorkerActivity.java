package com.cudpast.appminas.Principal.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.cudpast.appminas.Common.Common;
import com.cudpast.appminas.Model.Personal;
import com.cudpast.appminas.Principal.AllActivity;
import com.cudpast.appminas.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AddWorkerActivity extends AppCompatActivity {

    public static final String TAG = AddWorkerActivity.class.getSimpleName();

    private TextInputLayout
            personal_dni_layout,
            personal_name_layout,
            personal_last_layout,
            personal_age_layout,
            personal_address_layout,
            personal_born_layout,
            personal_date_layout,
            personal_phone1_layout,
            personal_phone2_layout;

    private TextInputEditText
            personal_dni,
            personal_name,
            personal_last,
            personal_age,
            personal_address,
            personal_born,
            personal_date,
            personal_phone1,
            personal_phone2;

    private Button btn_personal_create_user, btn_personal_back_main;


    FirebaseAuth mAuth;
    FirebaseDatabase mDatabase;
    ProgressDialog mDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_minero);
        getSupportActionBar().setTitle("Registrar  trabajador");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //Firebase
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
        //
        btn_personal_create_user = findViewById(R.id.btn_personal_create_user);
        btn_personal_back_main = findViewById(R.id.btn_personal_back_main);
        //
        personal_dni = findViewById(R.id.personal_dni);
        personal_name = findViewById(R.id.personal_name);
        personal_last = findViewById(R.id.personal_last);
        personal_age = findViewById(R.id.personal_age);
        personal_address = findViewById(R.id.personal_address);
        personal_born = findViewById(R.id.personal_born);
        personal_date = findViewById(R.id.personal_date);
        personal_phone1 = findViewById(R.id.personal_phone1);
        personal_phone2 = findViewById(R.id.personal_phone2);
        //
        personal_dni_layout = findViewById(R.id.personal_dni_layout);
        personal_name_layout = findViewById(R.id.personal_name_layout);
        personal_last_layout = findViewById(R.id.personal_last_layout);
        personal_age_layout = findViewById(R.id.personal_age_layout);
        personal_address_layout = findViewById(R.id.personal_address_layout);
        personal_born_layout = findViewById(R.id.personal_born_layout);
        personal_date_layout = findViewById(R.id.personal_date_layout);
        personal_phone1_layout = findViewById(R.id.personal_phone1_layout);
        personal_phone2_layout = findViewById(R.id.personal_phone2_layout);
        //Ejecutar
        btn_personal_create_user.setOnClickListener(v -> createNewPersonal(v));
        btn_personal_back_main.setOnClickListener(v -> goToMainActivity());

    }


    private void createNewPersonal(final View v) {

        if (submitForm()) {
            //
            mDialog = new ProgressDialog(AddWorkerActivity.this);
            mDialog.setMessage(" Registrando trabajador ");
            mDialog.show();
            //Declerarar Variables
            final String dni, name, last, age, address, born, date, phone1, phone2;
            Personal user;
            DatabaseReference ref_db_personal;
            //Obtener valores
            dni = personal_dni.getText().toString();
            name = personal_name.getText().toString();
            last = personal_last.getText().toString();
            age = personal_age.getText().toString();
            address = personal_address.getText().toString();
            born = personal_born.getText().toString();
            date = personal_date.getText().toString();
            phone1 = personal_phone1.getText().toString();
            phone2 = personal_phone2.getText().toString();
            // Crear usuario validado
            user = new Personal(dni, name, last, age, address, born, date, phone1, phone2);
            // Instanciar base de datos firebase
            ref_db_personal = mDatabase.getReference(Common.db_mina_personal);
            ref_db_personal
                    .child(Common.unidadTrabajoSelected.getNameUT())
                    .child(user.getDni())
                    .setValue(user)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(AddWorkerActivity.this, "El trabajador ha sido registrado ", Toast.LENGTH_SHORT).show();
                        goToMainActivity();
                        mDialog.dismiss();

                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(AddWorkerActivity.this, "Trabajador no ha sido Registrado", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "error : personal no registrado");
                        mDialog.dismiss();

                    });
        }
    }

    private void goToMainActivity() {
        Intent intent = new Intent(AddWorkerActivity.this, AllActivity.class);
        startActivity(intent);
    }

    //Validacion
    private boolean checkDNI() {
        if (personal_dni.getText().toString().trim().isEmpty()) {
            personal_dni_layout.setError("Ingrese su DNI");
            personal_dni_layout.requestFocus();
            return false;
        } else {
            personal_dni_layout.setError(null);
        }
        return true;
    }

    private boolean checkName() {
        if (personal_name.getText().toString().trim().isEmpty()) {
            personal_name_layout.setError("Ingrese su nombre");
            personal_name_layout.requestFocus();
            return false;
        } else {
            personal_name_layout.setError(null);
        }
        return true;
    }

    private boolean checkLast() {
        if (personal_last.getText().toString().trim().isEmpty()) {
            personal_last_layout.setError("Ingrese su apellidos");
            personal_last_layout.requestFocus();
            return false;
        } else {
            personal_last_layout.setError(null);
        }
        return true;
    }

    private boolean checkAge() {
        if (personal_age.getText().toString().trim().isEmpty()) {
            personal_age_layout.setError("Ingrese su edad");
            personal_age_layout.requestFocus();
            return false;
        } else {
            personal_age_layout.setError(null);
        }
        return true;
    }

    private boolean checkAddress() {
        if (personal_address.getText().toString().trim().isEmpty()) {
            personal_address_layout.setError("Ingrese su dirección");
            personal_address_layout.requestFocus();
            return false;
        } else {
            personal_address_layout.setError(null);
        }
        return true;
    }

    private boolean checkBorn() {
        if (personal_born.getText().toString().trim().isEmpty()) {
            personal_born_layout.setError("Ingrese su lugar de nacimiento");
            personal_born_layout.requestFocus();
            return false;
        } else {
            personal_born_layout.setError(null);
        }
        return true;
    }

    private boolean checkDate() {
        if (personal_date.getText().toString().trim().isEmpty()) {
            personal_date_layout.setError("Ingrese su fecha de nacimiento");
            personal_date_layout.requestFocus();
            return false;
        } else {
            personal_date_layout.setError(null);
        }
        return true;
    }

    private boolean checkPhone1() {
        if (personal_phone1.getText().toString().trim().isEmpty()) {
            personal_phone1_layout.setError("Ingrese su telefono principal");
            personal_phone1_layout.requestFocus();
            return false;
        } else {
            personal_phone1_layout.setError(null);
        }
        return true;
    }

    private boolean checkPhone2() {
        if (personal_phone2.getText().toString().trim().isEmpty()) {
            personal_phone2_layout.setError("Ingrese su telefono secundario");
            personal_phone2_layout.requestFocus();
            return false;
        } else {
            personal_phone2_layout.setError(null);
        }
        return true;
    }


    private boolean submitForm() {

        if (!checkDNI()) {
            return false;
        }

        if (!checkName()) {
            return false;
        }

        if (!checkLast()) {
            return false;
        }

        if (!checkAge()) {
            return false;
        }

        if (!checkAddress()) {
            return false;
        }

        if (!checkBorn()) {
            return false;
        }

        if (!checkDate()) {
            return false;
        }

        if (!checkPhone1()) {
            return false;
        }

        /*
        if (!checkPhone2()) {
            return false;
        }

         */

        return true;
    }


}
