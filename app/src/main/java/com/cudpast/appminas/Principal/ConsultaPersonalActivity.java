package com.cudpast.appminas.Principal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.cudpast.appminas.Adapter.AdapterDatosPersonales;
import com.cudpast.appminas.Common.Common;
import com.cudpast.appminas.Model.DatosPersonal;
import com.cudpast.appminas.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import static com.cudpast.appminas.R.layout.layout_raw_consulta_pesonal_item;

public class ConsultaPersonalActivity extends AppCompatActivity {

    private TextInputLayout consulta_dni_layout;
    private TextInputEditText consulta_dni;
    private RecyclerView myrecycleview_date;
    private Button btn_consulta_dni;

    private FirebaseDatabase database = FirebaseDatabase.getInstance();

    private DatabaseReference ref_datos_paciente;
    private List<DatosPersonal> listtemp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setTitle("Consulta Datos  ");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_consulta_minero);
        //
        consulta_dni_layout = findViewById(R.id.consulta_dni_layout);
        consulta_dni = findViewById(R.id.consulta_dni);
        myrecycleview_date = findViewById(R.id.myrecycleview_date);
        btn_consulta_dni = findViewById(R.id.btn_consulta_dni);
        //

        btn_consulta_dni.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                consultarDatosPaciente();
            }
        });


    }

    private void consultarDatosPaciente() {
        String dni = consulta_dni.getText().toString();

        myrecycleview_date.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        myrecycleview_date.setHasFixedSize(true);
        myrecycleview_date.setLayoutManager(new LinearLayoutManager(this));

        ref_datos_paciente = FirebaseDatabase.getInstance().getReference(Common.db_mina_personal_data).child(Common.unidadMineraSelected).child(dni);
        ref_datos_paciente.keepSynced(true);
        ref_datos_paciente.orderByKey();

        ref_datos_paciente.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listtemp = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    DatosPersonal datosPersonal = snapshot.getValue(DatosPersonal.class);
                    listtemp.add(datosPersonal);
                }
                //todo : adapterRV
                AdapterDatosPersonales adapter = new AdapterDatosPersonales(getApplicationContext(), listtemp);
                myrecycleview_date.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }


}
