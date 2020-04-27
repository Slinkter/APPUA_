package com.cudpast.appminas.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.cudpast.appminas.R;

public class SplashActivity extends AppCompatActivity {


    Button splash ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_splash);

        splash = findViewById(R.id.splash);
        splash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SplashActivity.this,LoginActivity.class);
                startActivity(intent);
            }
        });



    }
}
