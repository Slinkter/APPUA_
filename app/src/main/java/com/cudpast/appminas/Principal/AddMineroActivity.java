package com.cudpast.appminas.Principal;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.cudpast.appminas.R;

public class AddMineroActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_minero);
        getSupportActionBar().hide();
    }
}
