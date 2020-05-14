package com.cudpast.appminas.Principal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cudpast.appminas.Common.Common;
import com.cudpast.appminas.Model.DatosPersonal;
import com.cudpast.appminas.Model.Personal;
import com.cudpast.appminas.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.view.LineChartView;

public class VisualActivity extends AppCompatActivity {

    public static final String TAG = VisualActivity.class.getSimpleName();

    TextInputLayout visual_dni_layout;
    TextInputEditText visual_dni;
    Button btn_visual_dni;

    FirebaseDatabase database = FirebaseDatabase.getInstance(); // conexion a firebase
    Personal personal;
    TextView show_name_visual_dni, meanTempe;
    LinearLayout visual_linerlayout;

    private DatabaseReference ref_datos_paciente;
    private List<DatosPersonal> listtemp;


    private List<String> listDate;
    private List<String> listTemperatura;
    private List<Integer> listSaturacion;
    private List<Integer> listPulso;

    LineChartView lineChartViewTemperatura;
    LineChartView lineChartViewSaturacion;
    LineChartView lineChartViewOxigeno;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visual);
        visual_dni_layout = findViewById(R.id.visual_dni_layout);
        visual_dni = findViewById(R.id.visual_dni);
        btn_visual_dni = findViewById(R.id.btn_visual_dni);
        show_name_visual_dni = findViewById(R.id.show_name_visual_dni);
        visual_linerlayout = findViewById(R.id.visual_linerlayout);
        lineChartViewTemperatura = findViewById(R.id.chart1);
        lineChartViewSaturacion = findViewById(R.id.chart2);
        lineChartViewOxigeno = findViewById(R.id.chart3);


        meanTempe = findViewById(R.id.meanTempe);

        btn_visual_dni.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                consultarDatosPaciente();
            }
        });
    }

    private void consultarDatosPaciente() {
        final String dni = visual_dni.getText().toString();

        if (dni.toString().isEmpty()) {

        } else {
            DatabaseReference ref_db_mina_personal = database.getReference(Common.db_mina_personal);
            DatabaseReference ref_mina = ref_db_mina_personal.child(Common.unidadTrabajoSelected.getNameUT());
            ref_mina.child(dni).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    personal = dataSnapshot.getValue(Personal.class);
                    if (personal != null) {
                        visual_dni_layout.setError(null);
                        if (personal.getLast() == null) {
                            personal.setLast(" ");
                        }
                        show_name_visual_dni.setText("Trabajador : " + personal.getName() + " " + personal.getLast());
                        visual_linerlayout.setVisibility(View.VISIBLE);

                        getDataFromFirebase(dni);
                        //  tempShowChart();


                    } else {
                        visual_dni_layout.setError("El trabajador no exsite en la base de datos");
                        show_name_visual_dni.setText("");
                        visual_linerlayout.setVisibility(View.INVISIBLE);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.e(TAG, "error : " + databaseError.getMessage());
                }
            });
        }
    }


    private void getDataFromFirebase(String dni) {

        listtemp = new ArrayList<>();
        listDate = new ArrayList<String>();
        listTemperatura = new ArrayList<>();
        listSaturacion = new ArrayList<>();
        listPulso = new ArrayList<>();

        Query query = FirebaseDatabase.getInstance().getReference(Common.db_mina_personal_data).child(Common.unidadTrabajoSelected.getNameUT()).child(dni)
                .orderByKey()
                .limitToFirst(12);

        query
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            DatosPersonal datosPersonal = snapshot.getValue(DatosPersonal.class);
                            if (datosPersonal != null) {

                                listtemp.add(datosPersonal);
                                //
                                listDate.add(datosPersonal.getDateRegister().substring(8, 10));
                                listTemperatura.add((datosPersonal.getTempurature()));
                                listSaturacion.add(Integer.parseInt(datosPersonal.getSo2()));
                                listPulso.add(Integer.parseInt(datosPersonal.getPulse()));

                            } else {

                            }
                        }

                        tempShowChart();

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.e(TAG, "error" + " : " + databaseError.getMessage());
                    }
                });
    }


    private void tempShowChart() {

        if (listDate != null && listTemperatura != null) {

            String[] axisData = listDate.stream().toArray(String[]::new);
            // String[] axisData = {"2020-05-12", "2020-05-12", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12"}; /// fecha
            //     int[] yAxisData = {50, 20, 15, 30, 20, 60, 15, 40, 45, 10, 90, 18};
            int[] yAxisData = new int[listTemperatura.size()];
            double suma = 0;
            double promedio = 0.0f;
            try {
                for (int i = 0; i < listTemperatura.size(); i++) {
                    Log.e(TAG, " " + (int) (Double.parseDouble(listTemperatura.get(i).toString())));
                    suma = suma + Double.parseDouble(listTemperatura.get(i).toString());
                    Log.e(TAG, "suma = " + suma);
                    yAxisData[i] = (int) (Double.parseDouble(listTemperatura.get(i).toString()));

                }


            } catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG, "[error]" + e.getMessage());
            }

            promedio = suma / listTemperatura.size();

            String cad = String.valueOf(promedio);

            meanTempe.setText("Promedio temperatura : " + cad.substring(0, 5));
            meanTempe.setTextColor(Color.parseColor("#03A9F4"));


            List yAxisValues = new ArrayList();
            List axisValues = new ArrayList();

            Line line = new Line(yAxisValues).setColor(Color.parseColor("#9C27B0"));

            for (int i = 0; i < axisData.length; i++) {
                Log.e(TAG, "axisData " + i + " = " + axisData[i]);
                axisValues.add(i, new AxisValue(i).setLabel(axisData[i]));
            }

            for (int i = 0; i < yAxisData.length; i++) {
                yAxisValues.add(new PointValue(i, (int) (yAxisData[i])));
            }

            List lines = new ArrayList();
            lines.add(line);

            LineChartData data = new LineChartData();
            data.setLines(lines);

            Axis axis = new Axis();
            axis.setValues(axisValues);
            axis.setTextSize(16);
            axis.setName("dias");
            axis.setTextColor(Color.parseColor("#03A9F4"));
            data.setAxisXBottom(axis);

            Axis yAxis = new Axis();
            //  yAxis.setName("Temperatura");
            yAxis.setTextColor(Color.parseColor("#03A9F4"));
            yAxis.setTextSize(16);
            data.setAxisYLeft(yAxis);

            lineChartViewTemperatura.setLineChartData(data);
            Viewport viewport = new Viewport(lineChartViewTemperatura.getMaximumViewport());
            viewport.bottom = 30;
            viewport.top = 45;
            lineChartViewTemperatura.setMaximumViewport(viewport);
            lineChartViewTemperatura.setCurrentViewport(viewport);
        } else {
            Log.e(TAG, "lista data null");
        }


    }


    private void graficosatu() {


        String[] axisData = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12"};
        int[] yAxisData = {50, 20, 15, 30, 20, 60, 15, 40, 45, 10, 90, 18};


        List yAxisValues = new ArrayList();
        List axisValues = new ArrayList();
        Line line = new Line(yAxisValues).setColor(Color.parseColor("#9C27B0"));

        for (int i = 0; i < axisData.length; i++) {
            axisValues.add(i, new AxisValue(i).setLabel(axisData[i]));
        }

        for (int i = 0; i < yAxisData.length; i++) {
            yAxisValues.add(new PointValue(i, yAxisData[i]));
        }

        List lines = new ArrayList();
        lines.add(line);

        LineChartData data = new LineChartData();
        data.setLines(lines);

        Axis axis = new Axis();
        axis.setValues(axisValues);
        axis.setTextSize(16);
        axis.setTextColor(Color.parseColor("#03A9F4"));
        data.setAxisXBottom(axis);

        Axis yAxis = new Axis();
        yAxis.setName("Saturacion");
        yAxis.setTextColor(Color.parseColor("#03A9F4"));
        yAxis.setTextSize(16);
        data.setAxisYLeft(yAxis);

        lineChartViewSaturacion.setLineChartData(data);
        Viewport viewport = new Viewport(lineChartViewSaturacion.getMaximumViewport());
        viewport.top = 110;
        lineChartViewSaturacion.setMaximumViewport(viewport);
        lineChartViewSaturacion.setCurrentViewport(viewport);
    }

    private void graficoOxige() {

        String[] axisData = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12"};
        int[] yAxisData = {50, 20, 15, 30, 20, 60, 15, 40, 45, 10, 90, 18};


        List yAxisValues = new ArrayList();
        List axisValues = new ArrayList();
        Line line = new Line(yAxisValues).setColor(Color.parseColor("#9C27B0"));

        for (int i = 0; i < axisData.length; i++) {
            axisValues.add(i, new AxisValue(i).setLabel(axisData[i]));
        }

        for (int i = 0; i < yAxisData.length; i++) {
            yAxisValues.add(new PointValue(i, yAxisData[i]));
        }

        List lines = new ArrayList();
        lines.add(line);

        LineChartData data = new LineChartData();
        data.setLines(lines);

        Axis axis = new Axis();
        axis.setValues(axisValues);
        axis.setTextSize(16);
        axis.setTextColor(Color.parseColor("#03A9F4"));
        data.setAxisXBottom(axis);

        Axis yAxis = new Axis();
        yAxis.setName("Oxigeno");
        yAxis.setTextColor(Color.parseColor("#03A9F4"));
        yAxis.setTextSize(16);
        data.setAxisYLeft(yAxis);

        lineChartViewOxigeno.setLineChartData(data);
        Viewport viewport = new Viewport(lineChartViewOxigeno.getMaximumViewport());
        viewport.top = 110;
        lineChartViewOxigeno.setMaximumViewport(viewport);
        lineChartViewOxigeno.setCurrentViewport(viewport);
    }


}
