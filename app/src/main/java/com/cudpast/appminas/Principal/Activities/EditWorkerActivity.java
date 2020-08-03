package com.cudpast.appminas.Principal.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cudpast.appminas.Common.Common;
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

public class EditWorkerActivity extends AppCompatActivity {

    public static final String TAG = EditWorkerActivity.class.getSimpleName();
    private TextInputLayout query_personal_dni_layout;
    private TextInputEditText query_personal_dni;
    private TextView show_delete_personal;


    TextInputLayout update_personal_name_layout, update_personal_last_layout, update_personal_address_layout, update_personal_phone1_layout;
    TextInputEditText update_personal_name, update_personal_last, update_personal_address, update_personal_phone1;


    private Button btn_update_personal, btn_exit_back, btn_query_dni_personal;
    private FirebaseDatabase database;
    private Personal personal;
    private LinearLayout layout_btn_update;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setTitle(" ");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_delete_worker);

        database = FirebaseDatabase.getInstance();

        query_personal_dni_layout = findViewById(R.id.query_personal_dni_layout);
        query_personal_dni = findViewById(R.id.query_personal_dni);
        btn_query_dni_personal = findViewById(R.id.btn_query_dni_personal);

        update_personal_name_layout = findViewById(R.id.update_personal_name_layout);
        update_personal_last_layout = findViewById(R.id.update_personal_last_layout);
        update_personal_address_layout = findViewById(R.id.update_personal_address_layout);
        update_personal_phone1_layout = findViewById(R.id.update_personal_phone1_layout);


        update_personal_name = findViewById(R.id.update_personal_name);
        update_personal_last = findViewById(R.id.update_personal_last);
        update_personal_address = findViewById(R.id.update_personal_address);
        update_personal_phone1 = findViewById(R.id.update_personal_phone1);


        layout_btn_update = findViewById(R.id.layout_btn_update);
        btn_update_personal = findViewById(R.id.btn_update_personal);
        btn_exit_back = findViewById(R.id.btn_exit_back);

        show_delete_personal = findViewById(R.id.show_delete_personal);


        // Realiza la consulta del dni si exsite el trabajador
        btn_query_dni_personal.setOnClickListener(v -> {
            if (submitForm()) {
                String dni = query_personal_dni.getText().toString();
                consultarDniPersonal(dni);
            }
        });

        // Realiza la actualizacion del trabajador
        btn_update_personal.setOnClickListener(v -> {
            String dni = query_personal_dni.getText().toString();
            showDiaglo(dni);

        });

        // Salir
        btn_exit_back.setOnClickListener(v -> {
            Intent intent = new Intent(EditWorkerActivity.this, AllActivity.class);
            startActivity(intent);
            finish();
        });


    }


    private void consultarDniPersonal(String dni_personal) {

        DatabaseReference ref_db_mina_personal = database
                .getReference(Common.db_mina_personal)
                .child(Common.unidadTrabajoSelected.getNameUT())
                .child(dni_personal);


        ref_db_mina_personal
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        personal = dataSnapshot.getValue(Personal.class);
                        if (personal != null) {
                            Log.e(TAG, "nombre : " + personal.getName());
                            Log.e(TAG, "dni : " + personal.getDni());
                            Log.e(TAG, "dirección : " + personal.getAddress());
                            Log.e(TAG, "phone 1 : " + personal.getPhone1());
                            show_delete_personal.setText(personal.getName() + " " + personal.getLast());
                            show_delete_personal.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.color_error_null));
                            query_personal_dni_layout.setError(null);
                            layout_btn_update.setVisibility(View.VISIBLE);

                            //
                            update_personal_name.setText(personal.getName());
                            update_personal_last.setText(personal.getLast());
                            update_personal_address.setText(personal.getAddress());
                            update_personal_phone1.setText(personal.getPhone1());
                            //



                        } else {
                            Log.e(TAG, "el trabjador no existe en  ");
                            show_delete_personal.setText("");
                            show_delete_personal.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.color_error));
                            query_personal_dni_layout.setError("El trabajador no exsite en la base de datos");
                            query_personal_dni_layout.requestFocus();
                            layout_btn_update.setVisibility(View.INVISIBLE);
                            //
                            update_personal_name.setText(" ");
                            update_personal_last.setText(" ");
                            update_personal_address.setText(" ");
                            update_personal_phone1.setText(" ");
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.e(TAG, "error : " + databaseError.getMessage());
                    }
                });

    }


    private boolean checkDNI() {
        if (query_personal_dni.getText().toString().trim().isEmpty()) {
            query_personal_dni_layout.setError("Ingrese su DNI");
            query_personal_dni_layout.requestFocus();
            return false;
        } else {
            query_personal_dni_layout.setError(null);
        }
        return true;
    }


    private boolean submitForm() {

        if (!checkDNI()) {
            return false;
        }

        return true;
    }


    public void showDiaglo(final String dni) {

        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(EditWorkerActivity.this);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.pop_up_delete_personal, null);
        builder.setView(view);
        builder.setCancelable(false);
        view.setKeepScreenOn(true);
        final AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        //
        Button btn_delete_yes = view.findViewById(R.id.btn_delete_yes);
        Button btn_delete_no = view.findViewById(R.id.btn_delete_no);

        btn_delete_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DatabaseReference ref_db_mina_personal = database.getReference(Common.db_mina_personal);
                DatabaseReference ref_mina = ref_db_mina_personal.child(Common.unidadTrabajoSelected.getNameUT());

                DatabaseReference ref_db_mina_personal_data = database.getReference(Common.db_mina_personal_data);
                final DatabaseReference ref_mina_data = ref_db_mina_personal_data.child(Common.unidadTrabajoSelected.getNameUT());

                ref_mina.child(dni).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {


                        ref_mina_data.child(dni).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(EditWorkerActivity.this, "Trabajador Eliminado ", Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(EditWorkerActivity.this, "Trabajador no  Eliminado", Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                            }
                        });

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(EditWorkerActivity.this, "Trabjador no  Eliminado", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                });


                Log.e(TAG, "ref_db_mina_personal" + ref_db_mina_personal);
                Log.e(TAG, "ref_mina" + ref_mina);
                Log.e(TAG, "ref_mina.child(dni)." + ref_mina.child(dni));

            }
        });


        btn_delete_no
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
        dialog.show();
    }


}
