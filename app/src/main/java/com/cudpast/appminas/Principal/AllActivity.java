package com.cudpast.appminas.Principal;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.cudpast.appminas.Activities.LoginActivity;
import com.cudpast.appminas.Common.Common;
import com.cudpast.appminas.Principal.Activities.AddWorkerActivity;
import com.cudpast.appminas.Principal.Activities.EditWorkerActivity;
import com.cudpast.appminas.Principal.Activities.InputDataWorkerActivity;
import com.cudpast.appminas.Principal.Activities.ReportPdfctivity;
import com.cudpast.appminas.R;
import com.google.firebase.auth.FirebaseAuth;

public class AllActivity extends AppCompatActivity {

    private TextView tv_selectedunidadminera, tv_name_current_user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_all);

        tv_selectedunidadminera = findViewById(R.id.tv_selectedunidadminera);
        tv_name_current_user = findViewById(R.id.tv_name_current_user);


        checkoutSessionUser();

    }

    private void checkoutSessionUser() {

        if (Common.currentUser == null) {
            Intent intent = new Intent(AllActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        } else {
            tv_selectedunidadminera.setText(Common.unidadTrabajoSelected.getAliasUT());
            tv_name_current_user.setText(Common.currentUser.getReg_name());
        }
    }


    @Override
    protected void onStart() {
        super.onStart();
        checkoutSessionUser();
    }

    // metodo 1
    public void btnAddWorker(View view) {
        Intent intent = new Intent(AllActivity.this, AddWorkerActivity.class);
        startActivity(intent);
    }

    // metodo 2
    public void btnRegisterSymptoms(View view) {
        Intent intent = new Intent(AllActivity.this, InputDataWorkerActivity.class);
        startActivity(intent);
    }

    // metodo 3
    public void btnEditWorker(View view) {
        Intent intent = new Intent(AllActivity.this, EditWorkerActivity.class);
        startActivity(intent);
    }

    // metodo 4
    public void btnReportData(View view) {
        Intent intent = new Intent(AllActivity.this, ReportPdfctivity.class);
        startActivity(intent);
    }


}
