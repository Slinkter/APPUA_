package com.cudpast.appminas.Principal;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.cudpast.appminas.R;

public class DeletePersonalActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setTitle("Eliminar Pacientes");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_delete_minero);
    }
}
