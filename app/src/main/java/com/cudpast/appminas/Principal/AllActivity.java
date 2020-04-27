package com.cudpast.appminas.Principal;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.cudpast.appminas.R;

public class AllActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_all);
    }

    public void btnNewMinero(View view) {
        Toast.makeText(this, "Nuevo Minero", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(AllActivity.this, AddMineroActivity.class);
        startActivity(intent);
    }

    public void btnDeleteMinero(View view) {
        Toast.makeText(this, "Eliminar Minero", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(AllActivity.this, DeleteMineroActivity.class);
        startActivity(intent);
    }

    public void btnInputData(View view) {
        Toast.makeText(this, "Dato Minero", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(AllActivity.this, InputDataMineroActivity.class);
        startActivity(intent);
    }

    public void btnQueryMinero(View view) {
        Toast.makeText(this, "Consulta Minero", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(AllActivity.this, ConsultaMineroActivity.class);
        startActivity(intent);
    }


}
