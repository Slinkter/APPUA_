package com.cudpast.appminas.Principal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.cudpast.appminas.Common.Common;
import com.cudpast.appminas.Model.Personal;
import com.cudpast.appminas.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class DeletePersonalActivity extends AppCompatActivity {

    public static final String TAG = DeletePersonalActivity.class.getSimpleName();
    private TextInputLayout delete_personal_dni_layout;
    private TextInputEditText delete_personal_dni;
    private TextView show_delete_personal;

    private Button btndeletepersonal, btndeleteback, btndeletepersonalcosultardni;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private Personal personal;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setTitle("Eliminar Pacientes");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_delete_minero);


        delete_personal_dni_layout = findViewById(R.id.delete_personal_dni_layout);
        delete_personal_dni = findViewById(R.id.delete_personal_dni);

        show_delete_personal = findViewById(R.id.show_delete_personal);
        btndeletepersonalcosultardni = findViewById(R.id.btndeletepersonalcosultardni);

        btndeletepersonal = findViewById(R.id.btndeletepersonal);
        btndeleteback = findViewById(R.id.btndeleteback);


        btndeletepersonalcosultardni.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (submitForm()) {
                    String dni = delete_personal_dni.getText().toString();
                    consultarDniPersonal(dni);
                }
            }
        });

        btndeletepersonal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(DeletePersonalActivity.this, "Eliminar ", Toast.LENGTH_SHORT).show();
            }
        });
        

        btndeleteback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DeletePersonalActivity.this, AllActivity.class);
                startActivity(intent);
                finish();
            }
        });


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
                    show_delete_personal.setText(personal.getName());
                    show_delete_personal.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.color_error_null));
                    delete_personal_dni_layout.setError(null);
                } else {
                    Log.e(TAG, "el trabjador no existe en  ");
                    show_delete_personal.setText("");
                    show_delete_personal.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.color_error));
                    delete_personal_dni_layout.setError("El trabajador no exsite en la base de datos");
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "error : " + databaseError.getMessage());
            }
        });

    }


    private boolean checkDNI() {
        if (delete_personal_dni.getText().toString().trim().isEmpty()) {
            delete_personal_dni_layout.setError("Ingrese su DNI");
            return false;
        } else {
            delete_personal_dni_layout.setError(null);
        }
        return true;
    }


    private boolean submitForm() {

        if (!checkDNI()) {
            return false;
        }

        return true;
    }


}
