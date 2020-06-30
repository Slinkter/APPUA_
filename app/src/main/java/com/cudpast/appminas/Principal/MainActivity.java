package com.cudpast.appminas.Principal;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.cudpast.appminas.Common.Common;
import com.cudpast.appminas.Model.UnidadTrabajo;
import com.cudpast.appminas.R;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {


    private Button btnContinuar;
    private Spinner spinner_unidadMinera;
    private ArrayList<String> listaUnidadMinera;
    private ArrayList<UnidadTrabajo> listaUT;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();

        listaUnidadMinera = new ArrayList<>();
        listaUT = new ArrayList<>();

        UnidadTrabajo e0 = new UnidadTrabajo();
        e0.setNameUT("SeleccioneUnidad");
        e0.setAliasUT("Buscar Unidad");

        UnidadTrabajo e1 = new UnidadTrabajo();
        e1.setNameUT("COPE");
        e1.setAliasUT("Cope");

        UnidadTrabajo e2 = new UnidadTrabajo();
        e2.setNameUT("Chavincha");
        e2.setAliasUT("Chavincha");

        UnidadTrabajo e3 = new UnidadTrabajo();
        e3.setNameUT("Sonaje");
        e3.setAliasUT("Sonaje");

        UnidadTrabajo e4 = new UnidadTrabajo();
        e4.setNameUT("oficinaLima");
        e4.setAliasUT("of.Lima");

        UnidadTrabajo e5 = new UnidadTrabajo();
        e5.setNameUT("oficinaArequipa");
        e5.setAliasUT("of.Arequipa");

        UnidadTrabajo e6 = new UnidadTrabajo();
        e6.setNameUT("oficinaHuaraz");
        e6.setAliasUT("of.Huaraz");

        listaUT.add(e0);
        listaUT.add(e1);
        listaUT.add(e2);
        listaUT.add(e3);
        listaUT.add(e4);
        listaUT.add(e5);
        listaUT.add(e6);

        for (int i = 0; i < listaUT.size(); i++) {
            listaUnidadMinera.add(listaUT.get(i).getAliasUT().toString());
        }


        spinner_unidadMinera = (Spinner) findViewById(R.id.spinner_unidadMinera);
        ArrayAdapter<CharSequence> adapter_spinner_um = new ArrayAdapter(this, R.layout.spinner_adapter_unidad_minera, listaUnidadMinera);
        spinner_unidadMinera.setAdapter(adapter_spinner_um);
        spinner_unidadMinera.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0) {
                    if (listaUnidadMinera.get(position).equalsIgnoreCase("Seleccione Unidad")) {
                        btnContinuar.setVisibility(View.INVISIBLE);
                    } else {
                        Common.unidadMineraSelected = listaUnidadMinera.get(position);
                        Common.unidadTrabajoSelected = listaUT.get(position);
                        Log.e("main", " Common.unidadMineraSelected = " + Common.unidadMineraSelected);
                        Log.e("main", " Common.unidadTrabajoSelected.getNameUT() = " + Common.unidadTrabajoSelected.getNameUT());
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
