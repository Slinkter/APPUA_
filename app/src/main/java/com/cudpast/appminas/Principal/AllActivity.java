package com.cudpast.appminas.Principal;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.cudpast.appminas.Common.Common;
import com.cudpast.appminas.R;

public class AllActivity extends AppCompatActivity {

    private TextView tv_selectedunidadminera;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_all);

        tv_selectedunidadminera = findViewById(R.id.tv_selectedunidadminera);

    }

    @Override
    protected void onStart() {
        super.onStart();
        tv_selectedunidadminera.setText(Common.unidadMineraSelected);

    }

    public void btnNewMinero(View view) {
        Toast.makeText(this, "Nuevo ", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(AllActivity.this, AddPersonalActivity.class);
        startActivity(intent);
    }

    public void btnDeleteMinero(View view) {
        Toast.makeText(this, "Eliminar ", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(AllActivity.this, DeletePersonalActivity.class);
        startActivity(intent);
    }

    public void btnInputData(View view) {
        Toast.makeText(this, "Dato ", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(AllActivity.this, InputDataPersonalActivity.class);
        startActivity(intent);
    }

    public void btnQueryMinero(View view) {
        Toast.makeText(this, "Consulta ", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(AllActivity.this, ConsultaPersonalActivity.class);
        startActivity(intent);
    }


}
