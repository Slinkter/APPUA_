package com.cudpast.appminas.Principal.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.cudpast.appminas.Adapter.AdapterDatosPersonales;
import com.cudpast.appminas.Common.Common;
import com.cudpast.appminas.Model.MetricasPersonal;
import com.cudpast.appminas.Model.Personal;
import com.cudpast.appminas.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class QueryActivity extends AppCompatActivity {

    private TextInputLayout consulta_dni_layout;
    private TextInputEditText consulta_dni;
    private RecyclerView myrecycleview_date;
    private Button btn_consulta_dni;

    private TextView show_name_consulta_dni;

    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference ref_datos_paciente;
    private List<MetricasPersonal> listtemp;
    private Personal personal;

    public static final String TAG = QueryActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setTitle("Consulta Datos");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_query_worker);
        //
        consulta_dni_layout = findViewById(R.id.consulta_dni_layout);
        consulta_dni = findViewById(R.id.consulta_dni);
        myrecycleview_date = findViewById(R.id.myrecycleview_date);
        btn_consulta_dni = findViewById(R.id.btn_consulta_dni);
        //
        show_name_consulta_dni = findViewById(R.id.show_name_consulta_dni);
        btn_consulta_dni.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (submitForm()) {
                    consultarDatosPaciente();
                }
            }
        });
    }

    private void consultarDatosPaciente() {
        final String dni = consulta_dni.getText().toString();

        if (dni.toString().isEmpty()) {

        } else {
            DatabaseReference ref_db_mina_personal = database.getReference(Common.db_mina_personal);
            DatabaseReference ref_mina = ref_db_mina_personal.child(Common.unidadTrabajoSelected.getNameUT());
            ref_mina.child(dni).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    personal = dataSnapshot.getValue(Personal.class);
                    if (personal != null) {
                        consulta_dni_layout.setError(null);
                        if (personal.getLast() == null) {
                            personal.setLast(" ");
                        }
                        show_name_consulta_dni.setText("Trabajador : " + personal.getName() + " " + personal.getLast());
                        ejecutar(dni);
                    } else {
                        consulta_dni_layout.setError("El trabajador no exsite en la base de datos");
                        myrecycleview_date.setAdapter(null);
                        myrecycleview_date.clearOnScrollListeners();
                        show_name_consulta_dni.setText("");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.e(TAG, "error : " + databaseError.getMessage());
                    myrecycleview_date.setAdapter(null);
                }
            });
        }
    }


    private void ejecutar(String dni) {

        myrecycleview_date.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        myrecycleview_date.setHasFixedSize(true);
        myrecycleview_date.setLayoutManager(new LinearLayoutManager(this));

        ref_datos_paciente = FirebaseDatabase.getInstance().getReference(Common.db_mina_personal_data).child(Common.unidadTrabajoSelected.getNameUT()).child(dni);
        ref_datos_paciente.keepSynced(true);
        ref_datos_paciente.orderByKey();

        ref_datos_paciente.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listtemp = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    MetricasPersonal metricasPersonal = snapshot.getValue(MetricasPersonal.class);
                    listtemp.add(metricasPersonal);
                }

                AdapterDatosPersonales adapter = new AdapterDatosPersonales(getApplicationContext(), listtemp);
                adapter.setOnItemClickListener(new AdapterDatosPersonales.OnItemClickListener() {
                    @Override
                    public void onItemClick(int position) {
                        String data_sintomas = listtemp.get(position).getSymptoms();
                        Boolean data_testfast = listtemp.get(position).getTestpruebarapida();
                        Log.e(TAG, "prubadno el dialogo  :  " + data_sintomas);
                        showDiaglo1(data_sintomas, data_testfast);
                    }
                });
                myrecycleview_date.setAdapter(adapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                myrecycleview_date.setAdapter(null);
            }
        });
    }


    //Validacion
    private boolean checkDNI() {
        if (consulta_dni.getText().toString().trim().isEmpty()) {
            consulta_dni_layout.setError("Ingrese su DNI");
            return false;
        } else {
            consulta_dni_layout.setError(null);
        }
        return true;
    }


    private boolean submitForm() {
        if (!checkDNI()) {
            return false;
        }
        return true;
    }


    public void showDiaglo1(String msn, Boolean data_testfast) {


        Log.e(TAG, "esto es el menjaje " + msn);

        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(QueryActivity.this);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.pop_up_sintomas, null);
        builder.setView(view);
        builder.setCancelable(false);
        view.setKeepScreenOn(true);
        final AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        //
        Button btn_back = view.findViewById(R.id.btn_sintomas);

        TextView tv_prueba = view.findViewById(R.id.msn_testfast);
        TextView tv_msn = view.findViewById(R.id.msn_sintomas);

        if (data_testfast == null) {
            tv_prueba.setText("NO");
            Log.e(TAG, "datafast es null");
        } else {
            if (data_testfast) {
                tv_prueba.setText("SI");
            } else {
                tv_prueba.setText("NO");
            }
        }


        if (msn == null) {
            tv_msn.setText("sin comentarios ");
        } else {
            tv_msn.setText(msn);
        }


        btn_back
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
        dialog.show();
    }

}
