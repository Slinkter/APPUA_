package com.cudpast.appminas.Principal;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.cudpast.appminas.R;

public class MainActivity extends AppCompatActivity {


    private Button btnContinuar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();
        btnContinuar = findViewById(R.id.btnContinuar);

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
        finish();
    }
}
