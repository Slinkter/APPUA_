package com.cudpast.appminas.Principal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cudpast.appminas.Common.Common;
import com.cudpast.appminas.Model.DatosPersonal;
import com.cudpast.appminas.Model.Personal;
import com.cudpast.appminas.R;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.TimeZone;

public class ExportActivity extends AppCompatActivity {

    public static final String TAG = ExportActivity.class.getSimpleName();
    Button btn_export_date;
    TextView tv_fecha;

    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private Personal personal;
    private DatabaseReference ref_datos_paciente;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_export);

        btn_export_date = findViewById(R.id.btn_export_date);
        tv_fecha = findViewById(R.id.fecha);

        MaterialDatePicker.Builder builder = MaterialDatePicker.Builder.datePicker();
        builder.setTitleText("Seleccionar Fecha");
        final MaterialDatePicker materialDatePicker = builder.build();
        btn_export_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                materialDatePicker.show(getSupportFragmentManager(), "DATE_PICKER");
            }
        });

        materialDatePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Long>() {
            @Override
            public void onPositiveButtonClick(Long selection) {
                // Log.e("DATE", "selection  : " + selection.toString());
                // Log.e("DATE", "timestampToString  : " + timestampToString(selection));
                // Log.e("DATE", "materialDatePicker.getSelection().toString()   : " + materialDatePicker.getSelection().toString());
                // Log.e("DATE", "materialDatePicker.getHeaderText().toString()  : " + materialDatePicker.getHeaderText().toString());
                tv_fecha.setText("fecha  " + timestampToString(selection));
                final String seletedDate = timestampToString(selection);


                ref_datos_paciente = FirebaseDatabase.getInstance().getReference(Common.db_mina_personal_data).child(Common.unidadTrabajoSelected.getNameUT());
                ref_datos_paciente.keepSynced(true);
                ref_datos_paciente.orderByKey();
                ref_datos_paciente.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                            try {
                                //  Log.e(TAG, "==============================");
                                //  Log.e(TAG, "snapshot.getKey() " + snapshot.getKey());
                                //
                                String dni = snapshot.getKey();
                                //
                                for (DataSnapshot dataSnapshot1 : snapshot.getChildren()) {
                                    DatosPersonal datosPersonal = dataSnapshot1.getValue(DatosPersonal.class);
                                    String fecha = datosPersonal.getDateRegister().substring(0, 10).trim();
                                    if (fecha.equalsIgnoreCase(seletedDate)) {
                                        Log.e(TAG, "----------> Fecha : " + fecha + " ");
                                        Log.e(TAG, "getSymptoms = " + datosPersonal.getSymptoms());
                                        Log.e(TAG, "getTempurature = " + datosPersonal.getTempurature());
                                        Log.e(TAG, "getSo2 = " + datosPersonal.getSo2());
                                        Log.e(TAG, "getDateRegister = " + datosPersonal.getDateRegister());
                                        Log.e(TAG, "getPulse = " + datosPersonal.getPulse());
                                        Log.e(TAG, "getWho_user_register= " + datosPersonal.getWho_user_register());

                                        DatabaseReference ref_db_mina_personal = database.getReference(Common.db_mina_personal);
                                        DatabaseReference ref_mina = ref_db_mina_personal.child(Common.unidadTrabajoSelected.getNameUT());
                                        //
                                        ref_mina.child(dni).addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                personal = dataSnapshot.getValue(Personal.class);
                                                if (personal != null) {
                                                    Log.e(TAG, "nombre: " + personal.getName() + " ");
                                                } else {

                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                                Log.e(TAG, "error : " + databaseError.getMessage());
                                            }
                                        });


                                    }
                                }
                            } catch (Exception e) {
                                e.getMessage();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.e(TAG, "error : " + databaseError.getMessage());
                    }
                });


            }
        });


    }

    private String timestampToString(long time) {
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
        calendar.setTimeInMillis(time);
        String date = DateFormat.format("yyyy-MM-dd", calendar).toString();
        return date;
    }
}
