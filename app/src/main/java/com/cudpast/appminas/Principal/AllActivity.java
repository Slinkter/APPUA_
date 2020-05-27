package com.cudpast.appminas.Principal;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.cudpast.appminas.Common.Common;
import com.cudpast.appminas.R;

public class AllActivity extends AppCompatActivity {

    private TextView tv_selectedunidadminera, currentuser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_all);
        tv_selectedunidadminera = findViewById(R.id.tv_selectedunidadminera);
        tv_selectedunidadminera.setText(Common.unidadTrabajoSelected.getAliasUT());

        currentuser = findViewById(R.id.currentuser);
        currentuser.setText(Common.currentUser.getReg_name());


    }

    @Override
    protected void onStart() {
        super.onStart();
        tv_selectedunidadminera.setText(Common.unidadTrabajoSelected.getAliasUT());
        currentuser.setText(Common.currentUser.getReg_name());
    }

    public void btnAddWorker(View view) {
        Intent intent = new Intent(AllActivity.this, AddWorkerActivity.class);
        startActivity(intent);
    }

    public void btnDeleteWorker(View view) {
        Intent intent = new Intent(AllActivity.this, DeleteWorkerActivity.class);
        startActivity(intent);
    }

    public void btnInputData(View view) {
        Intent intent = new Intent(AllActivity.this, InputDataPersonalActivity.class);
        startActivity(intent);
    }

    public void btnReportData(View view) {
        Intent intent = new Intent(AllActivity.this, ReportDataActivity.class);
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
