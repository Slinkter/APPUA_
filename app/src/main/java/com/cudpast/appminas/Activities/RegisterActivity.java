package com.cudpast.appminas.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.cudpast.appminas.R;

public class RegisterActivity extends AppCompatActivity {

    Button btnCreateUser, btnBackMain;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        getSupportActionBar().hide();

        btnCreateUser = findViewById(R.id.btnCreateUser);
        btnBackMain = findViewById(R.id.btnBackMain);

        btnCreateUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createNewUser();
            }
        });

        btnBackMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backMain();
            }
        });


    }

    private void createNewUser() {
        Toast.makeText(this, "nada", Toast.LENGTH_SHORT).show();
    }

    private void backMain() {
        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}
