package com.cudpast.appminas.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.cudpast.appminas.Principal.MainActivity;
import com.cudpast.appminas.R;

public class LoginActivity extends AppCompatActivity {

    Button btnLogin, btnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().hide();

        //
        //
        btnLogin = findViewById(R.id.btnLogin);
        btnRegister = findViewById(R.id.btnRegister);
        //
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initLogin();
            }
        });
        //
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initRegistro();
            }
        });
        //


    }

    private void initLogin() {
        Intent internt_register = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(internt_register);
        finish();
    }

    private void initRegistro() {
        Intent internt_register = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(internt_register);
        finish();
    }
}
