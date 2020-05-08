package com.cudpast.appminas.Principal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.cudpast.appminas.Activities.RegisterActivity;
import com.cudpast.appminas.Common.Common;
import com.cudpast.appminas.Model.Personal;
import com.cudpast.appminas.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AddPersonalActivity extends AppCompatActivity {

    public static final String TAG = AddPersonalActivity.class.getSimpleName();

    private TextInputLayout
            personal_dni_layout,
            personal_name_layout,
            personal_age_layout,
            personal_address_layout,
            personal_born_layout,
            personal_date_layout,
            personal_phone1_layout,
            personal_phone2_layout;

    private TextInputEditText
            personal_dni,
            personal_name,
            personal_age,
            personal_address,
            personal_born,
            personal_date,
            personal_phone1,
            personal_phone2;

    private Button btn_personal_create_user, btn_personal_back_main;

    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private FirebaseAuth mAuth;
    private ProgressDialog mDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_minero);
        getSupportActionBar().setTitle("Registro de Paciente ");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //
        mAuth = FirebaseAuth.getInstance();
        //
        btn_personal_create_user = findViewById(R.id.btn_personal_create_user);
        btn_personal_back_main = findViewById(R.id.btn_personal_back_main);
        //
        personal_dni = findViewById(R.id.personal_dni);
        personal_name = findViewById(R.id.personal_name);
        personal_age = findViewById(R.id.personal_age);
        personal_address = findViewById(R.id.personal_address);
        personal_born = findViewById(R.id.personal_born);
        personal_date = findViewById(R.id.personal_date);
        personal_phone1 = findViewById(R.id.personal_phone1);
        personal_phone2 = findViewById(R.id.personal_phone2);
        //
        personal_dni_layout = findViewById(R.id.personal_dni_layout);
        personal_name_layout = findViewById(R.id.personal_name_layout);
        personal_age_layout = findViewById(R.id.personal_age_layout);
        personal_address_layout = findViewById(R.id.personal_address_layout);
        personal_born_layout = findViewById(R.id.personal_born_layout);
        personal_date_layout = findViewById(R.id.personal_date_layout);
        personal_phone1_layout = findViewById(R.id.personal_phone1_layout);
        personal_phone2_layout = findViewById(R.id.personal_phone2_layout);
        //
        btn_personal_create_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createNewPersonal();
            }
        });

        btn_personal_back_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddPersonalActivity.this, AllActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();

      //  personal_dni_layout.setEnabled(false);

    }

    private void createNewPersonal() {

        if (submitForm()) {
            final String dni, name, age, address, born, date, phone1, phone2;
            dni = personal_dni.getText().toString();
            name = personal_name.getText().toString();
            age = personal_age.getText().toString();
            address = personal_address.getText().toString();
            born = personal_born.getText().toString();
            date = personal_date.getText().toString();
            phone1 = personal_phone1.getText().toString();
            phone2 = personal_phone2.getText().toString();

            Personal user = new Personal(dni, name, age, address, born, date, phone1, phone2);
            DatabaseReference ref_db_personal = database.getReference(Common.db_mina_personal);


            mDialog = new ProgressDialog(AddPersonalActivity.this);
            mDialog.setMessage("Registrando Personal ...");
            mDialog.show();

            ref_db_personal
                    .child(Common.unidadTrabajoSelected.getNameUT())
                    .child(user.getDni())
                    .setValue(user)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.e(TAG, Common.unidadMineraSelected);
                            Log.e(TAG, "dni" + dni);
                            Log.e(TAG, "personal registrado");
                            Toast.makeText(AddPersonalActivity.this, "Personal Registrador", Toast.LENGTH_SHORT).show();
                            gotoMAin();
                            mDialog.dismiss();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(AddPersonalActivity.this, "Personal NO Registrador", Toast.LENGTH_SHORT).show();
                            Log.e(TAG, "personal no registrado");
                            mDialog.dismiss();
                        }
                    });

        }


    }

    private void gotoMAin() {
        Intent intent = new Intent(AddPersonalActivity.this, AllActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    //Validacion
    private boolean checkDNI() {
        if (personal_dni.getText().toString().trim().isEmpty()) {
            personal_dni_layout.setError("Ingrese su DNI");
            return false;
        } else {
            personal_dni_layout.setError(null);
        }
        return true;
    }

    private boolean checkName() {
        if (personal_name.getText().toString().trim().isEmpty()) {
            personal_name_layout.setError("Ingrese su nombre");
            return false;
        } else {
            personal_name_layout.setError(null);
        }
        return true;
    }

    private boolean checkAge() {
        if (personal_age.getText().toString().trim().isEmpty()) {
            personal_age_layout.setError("Ingrese su edad");
            return false;
        } else {
            personal_age_layout.setError(null);
        }
        return true;
    }

    private boolean checkAddress() {
        if (personal_address.getText().toString().trim().isEmpty()) {
            personal_address_layout.setError("Ingrese su dirección");
            return false;
        } else {
            personal_address_layout.setError(null);
        }
        return true;
    }

    private boolean checkBorn() {
        if (personal_born.getText().toString().trim().isEmpty()) {
            personal_born_layout.setError("Ingrese su lugar de nacimiento");
            return false;
        } else {
            personal_born_layout.setError(null);
        }
        return true;
    }

    private boolean checkDate() {
        if (personal_date.getText().toString().trim().isEmpty()) {
            personal_date_layout.setError("Ingrese su fecha de nacimiento");
            return false;
        } else {
            personal_date_layout.setError(null);
        }
        return true;
    }

    private boolean checkPhone1() {
        if (personal_phone1.getText().toString().trim().isEmpty()) {
            personal_phone1_layout.setError("Ingrese su telefono principal");
            return false;
        } else {
            personal_phone1_layout.setError(null);
        }
        return true;
    }

    private boolean checkPhone2() {
        if (personal_phone2.getText().toString().trim().isEmpty()) {
            personal_phone2_layout.setError("Ingrese su telefono secundario");
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
        if (!checkPhone2()) {
            return false;
        }

        return true;
    }


}
