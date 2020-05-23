package com.cudpast.appminas.Principal;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.cudpast.appminas.R;
import com.github.barteksc.pdfviewer.PDFView;

import java.io.File;

public class ShowPdfActivity extends AppCompatActivity {

    private PDFView pdfView;
    private File file;

    public static final String TAG = ShowPdfActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().setTitle("Regresar");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        setContentView(R.layout.activity_show_pdf);
        pdfView = findViewById(R.id.pdfView);
        init();
        //initApp();
    }

    private void init() {
        try {
            file = new File(Environment.getExternalStorageDirectory(), "/arsi21.pdf");
            Log.e(TAG, "file archivo  " + file.toString());
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

    private void initApp() {

        file = new File(Environment.getExternalStorageDirectory(), "/arsi21.pdf");
        Log.e(TAG, "file archivo initApp() " + file.toString());
        if (file.exists()) {
            try {
                Log.e(TAG, "archivo existe ");
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.fromFile(file), "application/pdf");
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                Log.e(TAG, "1 .. 2");
                this.startActivity(intent);
            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.fromFile(file), "application/pdf");
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                this.startActivity(intent);

                //  this.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.adobe.reader&hl=en")));
                Toast.makeText(this, "no cuenta con una aplicacion de pdf", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "no cuenta con una aplicacion de pdf", Toast.LENGTH_SHORT).show();
        }

    }
}