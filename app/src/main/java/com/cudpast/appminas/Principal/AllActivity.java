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

    private FirebaseAuth firebaseAuth;


    // todo : notificacion para los usuario para actualicen versiones

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_all);


        tv_selectedunidadminera = findViewById(R.id.tv_selectedunidadminera);
        tv_name_current_user = findViewById(R.id.tv_name_current_user);


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

        if (Common.currentUser == null) {
            Intent intent = new Intent(AllActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }

        tv_selectedunidadminera.setText(Common.unidadTrabajoSelected.getAliasUT());
        tv_name_current_user.setText(Common.currentUser.getReg_name());
    }


    public void btnAddWorker(View view) {
        Intent intent = new Intent(AllActivity.this, AddWorkerActivity.class);
        startActivity(intent);
    }


    public void btnRegisterSymptoms(View view) {
        Intent intent = new Intent(AllActivity.this, InputDataWorkerActivity.class);
        startActivity(intent);
    }

    public void btnEditWorker(View view) {
        // Toast.makeText(this, "Solo admin", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(AllActivity.this, EditWorkerActivity.class);
        startActivity(intent);
    }


    public void btnReportData(View view) {
        Intent intent = new Intent(AllActivity.this, ReportPdfctivity.class);
        startActivity(intent);
    }


    // metodos de baja
    public void btnQueryMinero(View view) {
        Toast.makeText(this, "Solo admin", Toast.LENGTH_SHORT).show();
        //   Intent intent = new Intent(AllActivity.this, ConsultaPersonalActivity.class);
        //   startActivity(intent);
    }

    public void btnVisualData(View view) {
        Toast.makeText(this, "Solo admin", Toast.LENGTH_SHORT).show();
        //  Intent intent = new Intent(AllActivity.this, VisualActivity.class);
        //  startActivity(intent);
    }


}
