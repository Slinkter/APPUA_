package com.cudpast.appminas.Principal;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Environment;
import android.util.Log;

import com.cudpast.appminas.R;
import com.github.barteksc.pdfviewer.PDFView;

import java.io.File;

public class ShowPdfActivity extends AppCompatActivity {

    private PDFView pdfView;
    private File file;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().setTitle("Regresar");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        setContentView(R.layout.activity_show_pdf);
        pdfView = findViewById(R.id.pdfView);
        init();
    }

    private void init() {
        try {
            file = new File(Environment.getExternalStorageDirectory(), "/arsi21.pdf");
            Log.e("file", file.toString());
            pdfView.fromFile(file)
                    .enableSwipe(true)
                    .swipeHorizontal(false)
                    .enableDoubletap(true)
                    .enableAntialiasing(true)
                    .load();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
