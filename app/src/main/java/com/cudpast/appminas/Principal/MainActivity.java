package com.cudpast.appminas.Principal;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.cudpast.appminas.Common.Common;
import com.cudpast.appminas.R;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {


    private Button btnContinuar;
    private Spinner spinner_unidadMinera;
    private ArrayList<String> listaUnidadMinera;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();

        //Cargar Spinner Unidad Minera
        listaUnidadMinera = new ArrayList();
        listaUnidadMinera.add("Seleccione Unidad");
        listaUnidadMinera.add("COPE");
        listaUnidadMinera.add("Chavincha");
        listaUnidadMinera.add("Sonaje");
        listaUnidadMinera.add("of.Lima");
        listaUnidadMinera.add("of.Arequipa");
        listaUnidadMinera.add("of.Huaraz");

        spinner_unidadMinera = (Spinner) findViewById(R.id.spinner_unidadMinera);
        ArrayAdapter<CharSequence> adapter_spinner_um = new ArrayAdapter(this, R.layout.spinner_adapter_unidad_minera, listaUnidadMinera);
        spinner_unidadMinera.setAdapter(adapter_spinner_um);
        spinner_unidadMinera.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0) {
                    if (listaUnidadMinera.get(position).equalsIgnoreCase("Seleccione Minera")) {
                        btnContinuar.setVisibility(View.INVISIBLE);
                    } else {
                        Common.unidadMineraSelected = listaUnidadMinera.get(position);
                        btnContinuar.setVisibility(View.VISIBLE);
                    }

                } else {
                    btnContinuar.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        btnContinuar = findViewById(R.id.btnContinuar);
        btnContinuar.setVisibility(View.INVISIBLE);
        btnContinuar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initUnidad();
            }
        });


    }

    private void initUnidad() {
        Intent intent = new Intent(MainActivity.this, AllActivity.class);
        startActivity(intent);
    }
}
